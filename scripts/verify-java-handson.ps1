$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$repoRoot = Split-Path -Parent $PSScriptRoot
$handsonRoot = Join-Path $repoRoot "docs/curriculum/java/java-handson"
$verificationRoot = Join-Path $repoRoot "target/java-handson-verification"
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)

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

function Get-CodeBlockAfterHeading {
    param(
        [Parameter(Mandatory)] [string] $FilePath,
        [Parameter(Mandatory)] [string] $Heading,
        [Parameter(Mandatory)] [string] $Language
    )

    $markdown = [IO.File]::ReadAllText($FilePath, [Text.Encoding]::UTF8)
    $headingIndex = $markdown.IndexOf($Heading, [StringComparison]::Ordinal)
    if ($headingIndex -lt 0) {
        throw "Heading not found in $FilePath : $Heading"
    }

    $afterHeading = $markdown.Substring($headingIndex + $Heading.Length)
    $marker = '```' + $Language
    $markerIndex = $afterHeading.IndexOf($marker, [StringComparison]::Ordinal)
    if ($markerIndex -lt 0) {
        throw "$Language code block not found after heading in $FilePath : $Heading"
    }

    $contentStart = $afterHeading.IndexOf("`n", $markerIndex)
    if ($contentStart -lt 0) {
        throw "Code block start is malformed in $FilePath : $Heading"
    }
    $contentStart++

    $contentEnd = $afterHeading.IndexOf('```', $contentStart, [StringComparison]::Ordinal)
    if ($contentEnd -lt 0) {
        throw "Code block end is missing in $FilePath : $Heading"
    }

    return $afterHeading.Substring($contentStart, $contentEnd - $contentStart).TrimEnd() + [Environment]::NewLine
}

function Write-Utf8File {
    param(
        [Parameter(Mandatory)] [string] $Path,
        [Parameter(Mandatory)] [string] $Content
    )

    $directory = Split-Path -Parent $Path
    [IO.Directory]::CreateDirectory($directory) | Out-Null
    [IO.File]::WriteAllText($Path, $Content, $utf8NoBom)
}

if (Test-Path -LiteralPath $verificationRoot) {
    Remove-Item -LiteralPath $verificationRoot -Recurse -Force
}
[IO.Directory]::CreateDirectory($verificationRoot) | Out-Null

Write-Host "[1/6] Java and Maven commands"
Invoke-Checked -WorkingDirectory $repoRoot -Command "java" -Arguments @("-version")
Invoke-Checked -WorkingDirectory $repoRoot -Command "javac" -Arguments @("-version")
Invoke-Checked -WorkingDirectory $repoRoot -Command "mvn" -Arguments @("-version")

Write-Host "[2/6] Java-20A final record/enum sample"
$java20aSource = Join-Path $handsonRoot "java-20a-record-enum.md"
$java20aRoot = Join-Path $verificationRoot "java20a"
$recordEnumCode = Get-CodeBlockAfterHeading -FilePath $java20aSource -Heading "### Step 3:" -Language "java"
Write-Utf8File -Path (Join-Path $java20aRoot "RecordEnumDemo.java") -Content $recordEnumCode
Invoke-Checked -WorkingDirectory $java20aRoot -Command "javac" -Arguments @("-encoding", "UTF-8", "RecordEnumDemo.java")

Write-Host "[3/6] Java-20B Web API sample"
$java20bSource = Join-Path $handsonRoot "java-20b-web-api-prep.md"
$java20bRoot = Join-Path $verificationRoot "java20b"
$webApiHtml = Get-CodeBlockAfterHeading -FilePath $java20bSource -Heading "### Step 1:" -Language "html"
$webApiCode = Get-CodeBlockAfterHeading -FilePath $java20bSource -Heading "### Step 2:" -Language "java"
Write-Utf8File -Path (Join-Path $java20bRoot "static/index.html") -Content $webApiHtml
Write-Utf8File -Path (Join-Path $java20bRoot "WebApiPrepDemo.java") -Content $webApiCode
Invoke-Checked -WorkingDirectory $java20bRoot -Command "javac" -Arguments @("-encoding", "UTF-8", "WebApiPrepDemo.java")

Write-Host "[4/6] Java-21 Maven/JUnit sample"
$java21Source = Join-Path $handsonRoot "java-21-junit-basics.md"
$java21Root = Join-Path $verificationRoot "java21"
$pom = Get-CodeBlockAfterHeading -FilePath $java21Source -Heading "### Step 1:" -Language "xml"
$taxCalculator = Get-CodeBlockAfterHeading -FilePath $java21Source -Heading "### Step 2:" -Language "java"
$taxCalculatorTest = Get-CodeBlockAfterHeading -FilePath $java21Source -Heading "### Step 3:" -Language "java"
$additionalTaxTest = Get-CodeBlockAfterHeading -FilePath $java21Source -Heading "### Step 5:" -Language "java"
$testClassEnd = $taxCalculatorTest.LastIndexOf('}')
if ($testClassEnd -lt 0) {
    throw "TaxCalculatorTest class end was not found"
}
$taxCalculatorTest = $taxCalculatorTest.Substring(0, $testClassEnd) + $additionalTaxTest + "}`r`n"
Write-Utf8File -Path (Join-Path $java21Root "pom.xml") -Content $pom
Write-Utf8File -Path (Join-Path $java21Root "src/main/java/com/example/tax/TaxCalculator.java") -Content $taxCalculator
Write-Utf8File -Path (Join-Path $java21Root "src/test/java/com/example/tax/TaxCalculatorTest.java") -Content $taxCalculatorTest
Invoke-Checked -WorkingDirectory $java21Root -Command "mvn" -Arguments @("test")

Write-Host "[5/6] Local Markdown links and code fences"
$brokenLinks = [Collections.Generic.List[string]]::new()
$oddFences = [Collections.Generic.List[string]]::new()
$markdownFiles = Get-ChildItem -LiteralPath $handsonRoot -Recurse -File -Filter "*.md"
foreach ($markdownFile in $markdownFiles) {
    $markdown = [IO.File]::ReadAllText($markdownFile.FullName, [Text.Encoding]::UTF8)
    [regex]::Matches($markdown, '\[[^\]]*\]\(([^)#]+)(?:#[^)]*)?\)') | ForEach-Object {
        $target = $_.Groups[1].Value
        if ($target -notmatch '^(https?|mailto):') {
            $decodedTarget = [Uri]::UnescapeDataString($target)
            $resolved = [IO.Path]::GetFullPath((Join-Path $markdownFile.DirectoryName $decodedTarget))
            if (-not (Test-Path -LiteralPath $resolved)) {
                $brokenLinks.Add("$($markdownFile.FullName) -> $target")
            }
        }
    }

    $count = @(Select-String -LiteralPath $markdownFile.FullName -Pattern '^```' -Encoding UTF8).Count
    if ($count % 2 -ne 0) {
        $oddFences.Add("$count`t$($markdownFile.FullName)")
    }
}
if ($brokenLinks.Count -gt 0) {
    throw "Broken local links:`n$($brokenLinks -join "`n")"
}
if ($oddFences.Count -gt 0) {
    throw "Unbalanced Markdown fences:`n$($oddFences -join "`n")"
}

Write-Host "[6/6] Lesson/answer coverage and current navigation"
$answerDirectories = @(Get-ChildItem -LiteralPath $handsonRoot -Directory)
if ($answerDirectories.Count -ne 1) {
    throw "Expected exactly one answer directory under $handsonRoot"
}
$answerRoot = $answerDirectories[0].FullName
$answerNames = @(Get-ChildItem -LiteralPath $answerRoot -File -Filter "*.md" | ForEach-Object { $_.Name })
$missingAnswers = [Collections.Generic.List[string]]::new()
Get-ChildItem -LiteralPath $handsonRoot -File -Filter "java-*.md" |
    Where-Object { $_.BaseName -notin @("java-20-javadoc-reading", "java-21-junit-basics") } |
    ForEach-Object {
        $lessonBaseName = $_.BaseName
        $matchingAnswers = @($answerNames | Where-Object {
            $_.StartsWith($lessonBaseName + "-", [StringComparison]::Ordinal)
        })
        if ($matchingAnswers.Count -eq 0) {
            $missingAnswers.Add("$lessonBaseName-*.md")
        }
    }
if ($missingAnswers.Count -gt 0) {
    throw "Missing mini-exercise answers:`n$($missingAnswers -join "`n")"
}

$staleLesson0Paths = @(
    (Join-Path $handsonRoot "java-20a-record-enum.md"),
    (Join-Path $handsonRoot "java-20b-web-api-prep.md")
)
$staleLesson0 = @(Select-String -LiteralPath $staleLesson0Paths -Pattern 'Lesson0' -Encoding UTF8)
if ($staleLesson0.Count -gt 0) {
    throw "Stale Lesson0 references remain in Java-20A/20B:`n$($staleLesson0 -join "`n")"
}

Write-Host "Java handson verification passed."
