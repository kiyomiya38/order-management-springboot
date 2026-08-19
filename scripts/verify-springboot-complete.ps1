$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$repoRoot = Split-Path -Parent $PSScriptRoot
$appRoot = Join-Path $repoRoot "complete"
$docsRoot = Join-Path $repoRoot "docs/curriculum/springboot-complete"

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

function Get-RequiredText {
    param([Parameter(Mandatory)] [string] $Path)

    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) {
        throw "Required file is missing: $Path"
    }
    return Get-Content -LiteralPath $Path -Raw -Encoding UTF8
}

function Assert-Contains {
    param(
        [Parameter(Mandatory)] [string] $Text,
        [Parameter(Mandatory)] [string] $Expected,
        [Parameter(Mandatory)] [string] $Description
    )

    if (-not $Text.Contains($Expected)) {
        throw "$Description`nMissing text: $Expected"
    }
}

Write-Host "[1/5] Required application and curriculum files"

$requiredFiles = @(
    "complete/pom.xml",
    "complete/Dockerfile",
    "complete/docker-compose.yml",
    "complete/.env.example",
    "complete/src/main/resources/application.yml",
    "complete/src/main/resources/application-dev.yml",
    "complete/src/main/resources/application-prod.yml",
    "complete/src/main/resources/db/migration/V1__create_tables.sql",
    "complete/src/main/resources/db/migration/V2__add_index_to_attendance_work_date.sql",
    "complete/src/test/resources/application-test.yml",
    "complete/src/test/java/com/shinesoft/attendance/MigrationSmokeTest.java",
    "docs/curriculum/springboot-complete/README.md",
    "docs/curriculum/springboot-complete/00-java-web-database-primer.md",
    "docs/curriculum/springboot-complete/01-spring-boot-overview.md",
    "docs/curriculum/springboot-complete/02-architecture-and-request-flow.md",
    "docs/curriculum/springboot-complete/03-instructor-demo.md",
    "docs/curriculum/springboot-complete/04-handson-guide.md",
    "docs/curriculum/springboot-complete/05-deployment.md",
    "docs/curriculum/springboot-complete/troubleshooting.md",
    "docs/curriculum/springboot-complete/instructor-guide.md",
    "docs/curriculum/springboot-complete/glossary.md",
    "docs/curriculum/springboot-complete/checkpoints-and-answers.md"
)

foreach ($relativePath in $requiredFiles) {
    $fullPath = Join-Path $repoRoot $relativePath
    if (-not (Test-Path -LiteralPath $fullPath -PathType Leaf)) {
        throw "Required file is missing: $relativePath"
    }
}

$pom = Get-RequiredText (Join-Path $appRoot "pom.xml")
foreach ($dependency in @(
    "spring-boot-starter-web",
    "spring-boot-starter-thymeleaf",
    "spring-boot-starter-security",
    "spring-boot-starter-validation",
    "spring-boot-starter-data-jpa",
    "flyway-core",
    "flyway-mysql",
    "mariadb-java-client",
    "spring-boot-starter-test",
    "spring-security-test"
)) {
    Assert-Contains $pom $dependency "Required dependency is missing from complete/pom.xml."
}

$application = Get-RequiredText (Join-Path $appRoot "src/main/resources/application.yml")
foreach ($setting in @(
    "default: dev",
    "ddl-auto: validate",
    "open-in-view: false",
    "locations: classpath:db/migration",
    'address: ${SERVER_ADDRESS:127.0.0.1}'
)) {
    Assert-Contains $application $setting "Required common application setting is missing."
}

$prod = Get-RequiredText (Join-Path $appRoot "src/main/resources/application-prod.yml")
foreach ($setting in @('${DB_URL}', '${DB_USER}', '${DB_PASSWORD}')) {
    Assert-Contains $prod $setting "Production datasource must require external configuration."
}

$dockerfile = Get-RequiredText (Join-Path $appRoot "Dockerfile")
Assert-Contains $dockerfile '-Duser.timezone=Asia/Tokyo' `
    "The container JVM timezone must match the attendance business timezone."

$apiAdvice = Get-RequiredText (
    Join-Path $appRoot `
        "src/main/java/com/shinesoft/attendance/web/api/advice/ApiExceptionHandler.java"
)
Assert-Contains $apiAdvice "HttpMessageNotReadableException" `
    "Malformed API JSON must be mapped to a 400 response."

$securityConfig = Get-RequiredText (
    Join-Path $appRoot "src/main/java/com/shinesoft/attendance/config/SecurityConfig.java"
)
Assert-Contains $securityConfig "WWW_AUTHENTICATE" `
    "HTTP Basic 401 responses must include an authentication challenge."

$userService = Get-RequiredText (
    Join-Path $appRoot "src/main/java/com/shinesoft/attendance/service/UserService.java"
)
Assert-Contains $userService "countByRole" `
    "The last administrator must be protected from deletion or demotion."

Write-Host "[2/5] Maven tests and executable Jar"
Invoke-Checked -WorkingDirectory $appRoot -Command "mvn" -Arguments @(
    "-Ddebug=false", "clean", "verify"
)

$jarPath = Join-Path $appRoot "target/attendance-management-complete-0.0.1-SNAPSHOT.jar"
if (-not (Test-Path -LiteralPath $jarPath -PathType Leaf)) {
    throw "Executable Jar was not created: $jarPath"
}

Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead($jarPath)
try {
    $manifestEntry = $zip.GetEntry("META-INF/MANIFEST.MF")
    if ($null -eq $manifestEntry) {
        throw "Jar manifest is missing."
    }
    $reader = [System.IO.StreamReader]::new($manifestEntry.Open())
    try {
        $manifest = $reader.ReadToEnd()
    } finally {
        $reader.Dispose()
    }
    if ($manifest -notmatch "Main-Class: org\.springframework\.boot\.loader") {
        throw "The generated file is not a Spring Boot executable Jar."
    }
} finally {
    $zip.Dispose()
}

Write-Host "[3/5] Docker Compose syntax"
Invoke-Checked -WorkingDirectory $appRoot -Command "docker" -Arguments @(
    "compose", "--env-file", ".env.example", "config", "--quiet"
)

Write-Host "[4/5] Local Markdown links"
$brokenLinks = [System.Collections.Generic.List[string]]::new()
Get-ChildItem -LiteralPath $docsRoot -Recurse -Filter "*.md" | ForEach-Object {
    $markdownFile = $_
    $markdown = Get-Content -LiteralPath $markdownFile.FullName -Raw -Encoding UTF8
    [regex]::Matches($markdown, '\[[^\]]*\]\(([^)#]+)(?:#[^)]*)?\)') | ForEach-Object {
        $target = [System.Uri]::UnescapeDataString($_.Groups[1].Value)
        if ($target -notmatch '^(https?|mailto):') {
            $resolved = [IO.Path]::GetFullPath(
                (Join-Path $markdownFile.DirectoryName $target)
            )
            if (-not (Test-Path -LiteralPath $resolved)) {
                $brokenLinks.Add("$($markdownFile.FullName) -> $target")
            }
        }
    }
}
if ($brokenLinks.Count -gt 0) {
    throw "Broken local links:`n$($brokenLinks -join "`n")"
}

Write-Host "[5/5] Markdown fences and curriculum coverage"
$oddFences = [System.Collections.Generic.List[string]]::new()
Get-ChildItem -LiteralPath $docsRoot -Recurse -Filter "*.md" | ForEach-Object {
    $count = @(Select-String -LiteralPath $_.FullName -Pattern '^```' -Encoding UTF8).Count
    if ($count % 2 -ne 0) {
        $oddFences.Add("$count`t$($_.FullName)")
    }
}
if ($oddFences.Count -gt 0) {
    throw "Unbalanced Markdown fences:`n$($oddFences -join "`n")"
}

$curriculum = (
    Get-ChildItem -LiteralPath $docsRoot -Filter "*.md" |
        ForEach-Object {
            Get-Content -LiteralPath $_.FullName -Raw -Encoding UTF8
        }
) -join "`n"

foreach ($topic in @(
    "Spring Framework",
    "IoC",
    "DI",
    "Controller",
    "Service",
    "Repository",
    "Spring MVC",
    "Starter",
    "@SpringBootApplication",
    "JPA",
    "Validation",
    "BusinessException",
    "Spring Security",
    "REST API",
    "mvn spring-boot:run",
    "Flyway",
    "H2",
    "MariaDB",
    "Docker Compose"
)) {
    Assert-Contains $curriculum $topic "The curriculum does not cover every required topic."
}

Write-Host "Spring Boot complete curriculum verification passed."
