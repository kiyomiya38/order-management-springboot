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

Write-Host "[1/5] Root application tests and executable Jar"
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

Write-Host "[2/5] Maven sandbox"
$sandbox = Join-Path $curriculumRoot "lesson01/maven-sandbox"
Invoke-Checked -WorkingDirectory $sandbox -Command "mvn" -Arguments @("clean", "verify")

Write-Host "[3/5] Docker Compose syntax"
Invoke-Checked -WorkingDirectory $repoRoot -Command "docker" -Arguments @(
    "compose", "--env-file", ".env.example", "config", "--quiet"
)

Write-Host "[4/5] Local Markdown links"
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

Write-Host "[5/5] Markdown code fences"
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

Write-Host "Spring Boot curriculum verification passed."
