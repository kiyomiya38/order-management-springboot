$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$repoRoot = Split-Path -Parent $PSScriptRoot
$curriculumRoot = Join-Path $repoRoot "docs/curriculum/springboot"

function Invoke-Checked {
    param(
        [Parameter(Mandatory)] [string] $WorkingDirectory,
        [Parameter(Mandatory)] [string] $Command,
        [Parameter(Mandatory)] [string[]] $Arguments
    )

    Push-Location $WorkingDirectory
    try {
        & $Command @Arguments
        if ($LASTEXITCODE -ne 0) {
            throw "$Command $($Arguments -join ' ') failed with exit code $LASTEXITCODE"
        }
    } finally {
        Pop-Location
    }
}

Write-Host "[1/6] Root application build and executable Jar"
Invoke-Checked -WorkingDirectory $repoRoot -Command "mvn" -Arguments @("clean", "verify")

$jarPath = Join-Path $repoRoot "target/attendance-management-0.0.1-SNAPSHOT.jar"
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead($jarPath)
try {
    $manifestEntry = $zip.GetEntry("META-INF/MANIFEST.MF")
    if ($null -eq $manifestEntry) {
        throw "Jar manifest is missing"
    }
    $reader = [System.IO.StreamReader]::new($manifestEntry.Open())
    try {
        $manifest = $reader.ReadToEnd()
    } finally {
        $reader.Dispose()
    }
    if ($manifest -notmatch "Main-Class: org\.springframework\.boot\.loader") {
        throw "Jar is not a Spring Boot executable Jar"
    }
} finally {
    $zip.Dispose()
}

Write-Host "[2/6] Maven sandbox"
$sandbox = Join-Path $curriculumRoot "lesson01/maven-sandbox"
Invoke-Checked -WorkingDirectory $sandbox -Command "mvn" -Arguments @("clean", "verify")

Write-Host "[3/6] Docker Compose syntax"
Invoke-Checked -WorkingDirectory $repoRoot -Command "docker" -Arguments @(
    "compose", "--env-file", ".env.example", "config", "--quiet"
)

Write-Host "[4/6] Local Markdown links"
$brokenLinks = [System.Collections.Generic.List[string]]::new()
Get-ChildItem -LiteralPath $curriculumRoot -Recurse -Filter "*.md" | ForEach-Object {
    $markdownFile = $_
    $markdown = Get-Content -LiteralPath $markdownFile.FullName -Raw -Encoding UTF8
    [regex]::Matches($markdown, '\[[^\]]*\]\(([^)#]+)(?:#[^)]*)?\)') | ForEach-Object {
        $target = $_.Groups[1].Value
        if ($target -notmatch '^(https?|mailto):') {
            $resolved = [IO.Path]::GetFullPath((Join-Path $markdownFile.DirectoryName $target))
            if (-not (Test-Path -LiteralPath $resolved)) {
                $brokenLinks.Add("$($markdownFile.FullName) -> $target")
            }
        }
    }
}
if ($brokenLinks.Count -gt 0) {
    throw "Broken local links:`n$($brokenLinks -join "`n")"
}

Write-Host "[5/6] Markdown code fences"
$oddFences = [System.Collections.Generic.List[string]]::new()
Get-ChildItem -LiteralPath $curriculumRoot -Recurse -Filter "*.md" | ForEach-Object {
    $count = @(Select-String -LiteralPath $_.FullName -Pattern '^```' -Encoding UTF8).Count
    if ($count % 2 -ne 0) {
        $oddFences.Add("$count`t$($_.FullName)")
    }
}
if ($oddFences.Count -gt 0) {
    throw "Unbalanced Markdown fences:`n$($oddFences -join "`n")"
}

Write-Host "[6/6] Short-course transitions and prerequisite alignment"

function Get-CurriculumText {
    param([Parameter(Mandatory)] [string] $RelativePath)

    $path = Join-Path $repoRoot $RelativePath
    if (-not (Test-Path -LiteralPath $path)) {
        throw "Required curriculum file is missing: $RelativePath"
    }
    return Get-Content -LiteralPath $path -Raw -Encoding UTF8
}

function Assert-ContainsText {
    param(
        [Parameter(Mandatory)] [string] $Text,
        [Parameter(Mandatory)] [string] $Expected,
        [Parameter(Mandatory)] [string] $Description
    )

    if (-not $Text.Contains($Expected)) {
        throw "Curriculum invariant failed: $Description`nMissing text: $Expected"
    }
}

$springGuide = Get-CurriculumText "docs/curriculum/springboot/README.md"
$mandatoryMatch = [regex]::Match(
    $springGuide,
    '(?s)#### 1\.[^\r\n]*\r?\n(.*?)#### 2\.'
)
$mandatoryStart = $springGuide.IndexOf("#### 1.")
$mandatoryEnd = $springGuide.IndexOf("#### 2.")
if ($mandatoryMatch.Success) {
    $mandatorySection = $mandatoryMatch.Groups[1].Value
} elseif ($mandatoryStart -ge 0 -and $mandatoryEnd -gt $mandatoryStart) {
    $mandatorySection = $springGuide.Substring(
        $mandatoryStart,
        $mandatoryEnd - $mandatoryStart
    )
} else {
    $guideType = $springGuide.GetType().FullName
    throw "Spring Boot short-course mandatory section could not be parsed " +
        "(type=$guideType, length=$($springGuide.Length), " +
        "start=$mandatoryStart, end=$mandatoryEnd)"
}
foreach ($deferredJavaLesson in @("Java-17A", "Java-20A", "Java-20B", "Java-21")) {
    if ($mandatorySection.Contains($deferredJavaLesson)) {
        throw "$deferredJavaLesson must not be a Spring Boot start prerequisite"
    }
}
if ($mandatoryStart -gt 0) {
    $beforeMandatorySection = $springGuide.Substring(0, $mandatoryStart)
    foreach ($deferredJavaLesson in @("Java-17A", "Java-20A", "Java-20B", "Java-21")) {
        if ($beforeMandatorySection.Contains($deferredJavaLesson)) {
            throw "$deferredJavaLesson must not appear before the short-course mandatory table"
        }
    }
}
if ($springGuide -notmatch '(?m)^\| Lesson 2[^\r\n]*java-19-stream-api\.md') {
    throw "The short course must identify the Lesson 2 just-in-time Java review"
}
if ($springGuide -notmatch '(?m)^\| Lesson 6[^\r\n]*Java-20A') {
    throw "The short course must identify the Lesson 6 just-in-time Java review"
}

$sandboxGuide = Get-CurriculumText "docs/curriculum/springboot/lesson01/maven-sandbox/README.md"
Assert-ContainsText -Text $sandboxGuide `
    -Expected "practice/springboot/maven-sandbox" `
    -Description "Students must work outside the completed Maven Sandbox reference"

$lesson5A = Get-CurriculumText "docs/curriculum/springboot/lesson05/lesson5a-authentication.md"
$seedConfigStep = $lesson5A.IndexOf("#### Phase 1-0:")
$dataSeederStep = $lesson5A.IndexOf("#### Phase 1-4:")
if ($seedConfigStep -lt 0 -or $dataSeederStep -lt 0 -or $seedConfigStep -ge $dataSeederStep) {
    throw "Lesson5A must configure initial users before creating the conditional DataSeeder"
}
foreach ($requiredSeedText in @(
    'enabled: true',
    'admin-password: ${APP_SEED_ADMIN_PASSWORD:admin123}',
    'user-password: ${APP_SEED_USER_PASSWORD:password}',
    '@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")'
)) {
    Assert-ContainsText -Text $lesson5A `
        -Expected $requiredSeedText `
        -Description "Lesson5A must be runnable with admin and user1 before Lesson5C"
}

$lesson6 = Get-CurriculumText "docs/curriculum/springboot/lesson06/lesson6.md"
foreach ($requiredLesson6Text in @(
    'java-19-stream-api.md',
    'java-20a-record-enum.md',
    'record UserResponse',
    '.stream()',
    'this::toResponse',
    'curl -i'
)) {
    Assert-ContainsText -Text $lesson6 `
        -Expected $requiredLesson6Text `
        -Description "Lesson6 must teach its deferred Java and HTTP prerequisites in context"
}

$lesson5And6Files = @(
    Get-ChildItem -LiteralPath (Join-Path $curriculumRoot "lesson05") -Filter "*.md"
    Get-ChildItem -LiteralPath (Join-Path $curriculumRoot "lesson06") -Filter "*.md"
)
$lesson5And6Text = ($lesson5And6Files | ForEach-Object {
    Get-Content -LiteralPath $_.FullName -Raw -Encoding UTF8
}) -join "`n"
if ($lesson5And6Text -match '\bvar\s+[A-Za-z_][A-Za-z0-9_]*\s*=') {
    throw "Lesson5/6 must use explicit Java types for short-course learners"
}
if ($lesson5And6Text -match 'return\s+switch\s*\(') {
    throw "Lesson5/6 must not require an unexplained switch expression"
}

$lesson7 = Get-CurriculumText "docs/curriculum/springboot/lesson07/lesson7.md"
foreach ($requiredLesson7Text in @(
    'active: ${SPRING_PROFILES_ACTIVE:dev}',
    'url: ${DB_URL:jdbc:h2:file:./data/attendance;MODE=MariaDB}',
    'ddl-auto: validate',
    'open-in-view: false',
    'locations: classpath:db/migration',
    'address: ${SERVER_ADDRESS:0.0.0.0}',
    'application-dev.yml',
    'application-prod.yml'
)) {
    Assert-ContainsText -Text $lesson7 `
        -Expected $requiredLesson7Text `
        -Description "Lesson7 must provide a complete application.yml without losing inherited settings"
}

Write-Host "Spring Boot curriculum verification passed."
