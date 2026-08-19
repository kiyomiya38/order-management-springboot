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
        [Parameter(Mandatory)] [string] $Language,
        [int] $BlockIndex = 0
    )

    $markdown = [IO.File]::ReadAllText($FilePath, [Text.Encoding]::UTF8)
    $headingIndex = $markdown.IndexOf($Heading, [StringComparison]::Ordinal)
    if ($headingIndex -lt 0) {
        throw "Heading not found in $FilePath : $Heading"
    }

    $afterHeading = $markdown.Substring($headingIndex + $Heading.Length)
    $marker = '```' + $Language
    $searchIndex = 0
    for ($index = 0; $index -le $BlockIndex; $index++) {
        $markerIndex = $afterHeading.IndexOf($marker, $searchIndex, [StringComparison]::Ordinal)
        if ($markerIndex -lt 0) {
            throw "$Language code block $BlockIndex not found after heading in $FilePath : $Heading"
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
        $searchIndex = $contentEnd + 3
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

Write-Host "[1/8] Java and Maven commands"
Invoke-Checked -WorkingDirectory $repoRoot -Command "java" -Arguments @("-version")
Invoke-Checked -WorkingDirectory $repoRoot -Command "javac" -Arguments @("-version")
Invoke-Checked -WorkingDirectory $repoRoot -Command "mvn" -Arguments @("-version")

Write-Host "[2/8] Java-01 through Java-20A final single-file samples"
$singleFileSamples = @(
    @{ Lesson = "java-01-intro.md"; Heading = "### Step 3:"; File = "IntroHello.java" },
    @{ Lesson = "java-02-program-flow.md"; Heading = "### Step 5:"; File = "HelloFlow.java" },
    @{ Lesson = "java-03-variables-and-types.md"; Heading = "### Step 4:"; File = "VariableTypeDemo.java" },
    @{ Lesson = "java-04-expressions-and-operators.md"; Heading = "### Step 4:"; File = "OperatorDemo.java" },
    @{ Lesson = "java-04a-type-conversion-and-cast.md"; Heading = "### Step 4:"; File = "TypeConversionDemo.java" },
    @{ Lesson = "java-05-class-libraries.md"; Heading = "### Step 3:"; File = "LibraryDemo.java" },
    @{ Lesson = "java-06-conditions-and-loops.md"; Heading = "### Step 5:"; File = "ControlFlowDemo.java" },
    @{ Lesson = "java-06a-advanced-control-flow.md"; Heading = "### Step 4:"; File = "AdvancedControlFlowDemo.java" },
    @{ Lesson = "java-07-arrays.md"; Heading = "### Step 3:"; File = "ArrayDemo.java" },
    @{ Lesson = "java-07a-reference-types-and-multidimensional-arrays.md"; Heading = "### Step 3:"; File = "ReferenceArrayDemo.java" },
    @{ Lesson = "java-08-methods.md"; Heading = "### Step 5:"; File = "MethodDemo.java" },
    @{ Lesson = "java-09-instances-and-classes.md"; Heading = "### Step 3:"; File = "InstanceDemo.java" },
    @{ Lesson = "java-09a-string-reference-and-value-comparison.md"; Heading = "### Step 4:"; File = "StringComparisonDemo.java" },
    @{ Lesson = "java-11-class-mechanisms.md"; Heading = "### Step 4:"; File = "ClassMechanismDemo.java" },
    @{ Lesson = "java-11a-constructor-chaining.md"; Heading = "### Step 3:"; File = "ConstructorChainingDemo.java" },
    @{ Lesson = "java-13-inheritance.md"; Heading = "### Step 3:"; File = "InheritanceDemo.java" },
    @{ Lesson = "java-13a-inheritance-rules.md"; Heading = "### Step 3:"; File = "InheritanceRulesDemo.java" },
    @{ Lesson = "java-14-advanced-inheritance.md"; Heading = "### Step 2:"; File = "AdvancedInheritanceDemo.java" },
    @{ Lesson = "java-15-polymorphism.md"; Heading = "### Step 5:"; File = "PolymorphismDemo.java" },
    @{ Lesson = "java-16-standard-classes.md"; Heading = "### Step 8:"; File = "StandardClassDemo.java" },
    @{ Lesson = "java-16a-regex-basics.md"; Heading = "### Step 4:"; File = "RegexBasicsDemo.java" },
    @{ Lesson = "java-17-exceptions.md"; Heading = "### Step 3:"; File = "ExceptionDemo.java" },
    @{ Lesson = "java-17a-exception-types-and-throws.md"; Heading = "### Step 3:"; File = "ExceptionTypesDemo.java" },
    @{ Lesson = "java-18-collections.md"; Heading = "### Step 3:"; File = "CollectionDemo.java" },
    @{ Lesson = "java-19-stream-api.md"; Heading = "### Step 3:"; File = "StreamApiDemo.java" },
    @{ Lesson = "java-20-javadoc-reading.md"; Heading = "### Step 3:"; File = "HttpServerDocDemo.java" },
    @{ Lesson = "java-20a-record-enum.md"; Heading = "### Step 5:"; File = "RecordEnumDemo.java" }
)
foreach ($sample in $singleFileSamples) {
    $source = Join-Path $handsonRoot $sample.Lesson
    $sampleRoot = Join-Path $verificationRoot ([IO.Path]::GetFileNameWithoutExtension($sample.Lesson))
    $code = Get-CodeBlockAfterHeading -FilePath $source -Heading $sample.Heading -Language "java"
    Write-Utf8File -Path (Join-Path $sampleRoot $sample.File) -Content $code
    Invoke-Checked -WorkingDirectory $sampleRoot -Command "javac" -Arguments @("-encoding", "UTF-8", $sample.File)
}

Write-Host "[3/8] Multi-file final samples"
$java10Source = Join-Path $handsonRoot "java-10-multi-class-development.md"
$java10Root = Join-Path $verificationRoot "java10"
Write-Utf8File -Path (Join-Path $java10Root "src/model/OrderItem.java") -Content (Get-CodeBlockAfterHeading -FilePath $java10Source -Heading "### Step 4:" -Language "java" -BlockIndex 0)
Write-Utf8File -Path (Join-Path $java10Root "src/service/OrderCalculator.java") -Content (Get-CodeBlockAfterHeading -FilePath $java10Source -Heading "### Step 4:" -Language "java" -BlockIndex 1)
Write-Utf8File -Path (Join-Path $java10Root "src/app/OrderApp.java") -Content (Get-CodeBlockAfterHeading -FilePath $java10Source -Heading "### Step 4:" -Language "java" -BlockIndex 2)
[IO.Directory]::CreateDirectory((Join-Path $java10Root "out")) | Out-Null
Invoke-Checked -WorkingDirectory $java10Root -Command "javac" -Arguments @("-encoding", "UTF-8", "-d", "out", "src/model/OrderItem.java", "src/service/OrderCalculator.java", "src/app/OrderApp.java")

$java12Source = Join-Path $handsonRoot "java-12-encapsulation.md"
$java12Root = Join-Path $verificationRoot "java12"
Write-Utf8File -Path (Join-Path $java12Root "UserAccount.java") -Content (Get-CodeBlockAfterHeading -FilePath $java12Source -Heading "### Step 3:" -Language "java")
Write-Utf8File -Path (Join-Path $java12Root "EncapsulationDemo.java") -Content (Get-CodeBlockAfterHeading -FilePath $java12Source -Heading "### Step 2:" -Language "java")
Invoke-Checked -WorkingDirectory $java12Root -Command "javac" -Arguments @("-encoding", "UTF-8", "UserAccount.java", "EncapsulationDemo.java")

$java12aSource = Join-Path $handsonRoot "java-12a-access-modifiers.md"
$java12aRoot = Join-Path $verificationRoot "java12a"
Write-Utf8File -Path (Join-Path $java12aRoot "src/model/Account.java") -Content (Get-CodeBlockAfterHeading -FilePath $java12aSource -Heading "### Step 1:" -Language "java" -BlockIndex 0)
Write-Utf8File -Path (Join-Path $java12aRoot "src/model/InternalRule.java") -Content (Get-CodeBlockAfterHeading -FilePath $java12aSource -Heading "### Step 2:" -Language "java")
Write-Utf8File -Path (Join-Path $java12aRoot "src/model/AccountInspector.java") -Content (Get-CodeBlockAfterHeading -FilePath $java12aSource -Heading "### Step 2:" -Language "java" -BlockIndex 1)
[IO.Directory]::CreateDirectory((Join-Path $java12aRoot "out")) | Out-Null
Invoke-Checked -WorkingDirectory $java12aRoot -Command "javac" -Arguments @("-encoding", "UTF-8", "-d", "out", "src/model/Account.java", "src/model/AccountInspector.java", "src/model/InternalRule.java")

Write-Host "[4/8] Level 3 completed-code answers"
$answerRootForCompile = Join-Path $handsonRoot ([string][char]0x30DF + [char]0x30CB + [char]0x6F14 + [char]0x7FD2 + [char]0x89E3 + [char]0x7B54)
$completedCodeHeading = "### $([char]0x30EC)$([char]0x30D9)$([char]0x30EB)3$([char]0x5B8C)$([char]0x4E86)$([char]0x6642)$([char]0x306E)$([char]0x5168)$([char]0x30B3)$([char]0x30FC)$([char]0x30C9)"

$answer05 = Join-Path $answerRootForCompile "java-05-class-libraries-$([char]0x30DF)$([char]0x30CB)$([char]0x6F14)$([char]0x7FD2)$([char]0x89E3)$([char]0x7B54).md"
$answer05Root = Join-Path $verificationRoot "answer05"
Write-Utf8File -Path (Join-Path $answer05Root "LibraryDemo.java") -Content (Get-CodeBlockAfterHeading -FilePath $answer05 -Heading $completedCodeHeading -Language "java")
Invoke-Checked -WorkingDirectory $answer05Root -Command "javac" -Arguments @("-encoding", "UTF-8", "LibraryDemo.java")

$answer16 = Join-Path $answerRootForCompile "java-16-standard-classes-$([char]0x30DF)$([char]0x30CB)$([char]0x6F14)$([char]0x7FD2)$([char]0x89E3)$([char]0x7B54).md"
$answer16Root = Join-Path $verificationRoot "answer16"
Write-Utf8File -Path (Join-Path $answer16Root "StandardClassDemo.java") -Content (Get-CodeBlockAfterHeading -FilePath $answer16 -Heading "### $([char]0x30EC)$([char]0x30D9)$([char]0x30EB)3$([char]0x5B8C)$([char]0x4E86)$([char]0x6642)$([char]0x306E)$([char]0x5168)$([char]0x30B3)$([char]0x30FC)$([char]0x30C9)" -Language "java")
Invoke-Checked -WorkingDirectory $answer16Root -Command "javac" -Arguments @("-encoding", "UTF-8", "StandardClassDemo.java")

$answer19 = Join-Path $answerRootForCompile "java-19-stream-api-$([char]0x30DF)$([char]0x30CB)$([char]0x6F14)$([char]0x7FD2)$([char]0x89E3)$([char]0x7B54).md"
$answer19Root = Join-Path $verificationRoot "answer19"
Write-Utf8File -Path (Join-Path $answer19Root "StreamApiDemo.java") -Content (Get-CodeBlockAfterHeading -FilePath $answer19 -Heading "### $([char]0x30EC)$([char]0x30D9)$([char]0x30EB)3$([char]0x5B8C)$([char]0x4E86)$([char]0x6642)$([char]0x306E)$([char]0x5168)$([char]0x30B3)$([char]0x30FC)$([char]0x30C9)" -Language "java")
Invoke-Checked -WorkingDirectory $answer19Root -Command "javac" -Arguments @("-encoding", "UTF-8", "StreamApiDemo.java")

$answer12a = Join-Path $answerRootForCompile "java-12a-access-modifiers-$([char]0x30DF)$([char]0x30CB)$([char]0x6F14)$([char]0x7FD2)$([char]0x89E3)$([char]0x7B54).md"
$answer12aRoot = Join-Path $verificationRoot "answer12a"
Write-Utf8File -Path (Join-Path $answer12aRoot "src/model/Account.java") -Content (Get-CodeBlockAfterHeading -FilePath $answer12a -Heading $completedCodeHeading -Language "java" -BlockIndex 0)
Write-Utf8File -Path (Join-Path $answer12aRoot "src/model/InternalRule.java") -Content (Get-CodeBlockAfterHeading -FilePath $answer12a -Heading $completedCodeHeading -Language "java" -BlockIndex 1)
Write-Utf8File -Path (Join-Path $answer12aRoot "src/model/AccountInspector.java") -Content (Get-CodeBlockAfterHeading -FilePath $answer12a -Heading $completedCodeHeading -Language "java" -BlockIndex 2)
[IO.Directory]::CreateDirectory((Join-Path $answer12aRoot "out")) | Out-Null
Invoke-Checked -WorkingDirectory $answer12aRoot -Command "javac" -Arguments @("-encoding", "UTF-8", "-d", "out", "src/model/Account.java", "src/model/InternalRule.java", "src/model/AccountInspector.java")

$additionalCompletedAnswers = @(
    @{ Lesson = "java-04a-type-conversion-and-cast"; File = "TypeConversionDemo.java" },
    @{ Lesson = "java-08-methods"; File = "MethodDemo.java" },
    @{ Lesson = "java-13a-inheritance-rules"; File = "InheritanceRulesDemo.java" },
    @{ Lesson = "java-14-advanced-inheritance"; File = "AdvancedInheritanceDemo.java" },
    @{ Lesson = "java-15-polymorphism"; File = "PolymorphismDemo.java" },
    @{ Lesson = "java-17a-exception-types-and-throws"; File = "ExceptionTypesDemo.java" }
)
foreach ($answerSample in $additionalCompletedAnswers) {
    $answerPath = Join-Path $answerRootForCompile ($answerSample.Lesson + "-$([char]0x30DF)$([char]0x30CB)$([char]0x6F14)$([char]0x7FD2)$([char]0x89E3)$([char]0x7B54).md")
    $answerSampleRoot = Join-Path $verificationRoot ("answer-" + $answerSample.Lesson)
    $answerCode = Get-CodeBlockAfterHeading -FilePath $answerPath -Heading $completedCodeHeading -Language "java"
    Write-Utf8File -Path (Join-Path $answerSampleRoot $answerSample.File) -Content $answerCode
    Invoke-Checked -WorkingDirectory $answerSampleRoot -Command "javac" -Arguments @("-encoding", "UTF-8", $answerSample.File)
}

$answer12 = Join-Path $answerRootForCompile "java-12-encapsulation-$([char]0x30DF)$([char]0x30CB)$([char]0x6F14)$([char]0x7FD2)$([char]0x89E3)$([char]0x7B54).md"
$answer12Root = Join-Path $verificationRoot "answer12"
Write-Utf8File -Path (Join-Path $answer12Root "UserAccount.java") -Content (Get-CodeBlockAfterHeading -FilePath $answer12 -Heading $completedCodeHeading -Language "java" -BlockIndex 0)
Write-Utf8File -Path (Join-Path $answer12Root "EncapsulationDemo.java") -Content (Get-CodeBlockAfterHeading -FilePath $answer12 -Heading $completedCodeHeading -Language "java" -BlockIndex 1)
Invoke-Checked -WorkingDirectory $answer12Root -Command "javac" -Arguments @("-encoding", "UTF-8", "UserAccount.java", "EncapsulationDemo.java")

Write-Host "[5/8] Java-20B Web API sample"
$java20bSource = Join-Path $handsonRoot "java-20b-web-api-prep.md"
$java20bRoot = Join-Path $verificationRoot "java20b"
$webApiHtml = Get-CodeBlockAfterHeading -FilePath $java20bSource -Heading "### Step 1:" -Language "html"
$webApiCode = Get-CodeBlockAfterHeading -FilePath $java20bSource -Heading "### Step 5:" -Language "java"
Write-Utf8File -Path (Join-Path $java20bRoot "static/index.html") -Content $webApiHtml
Write-Utf8File -Path (Join-Path $java20bRoot "WebApiPrepDemo.java") -Content $webApiCode
Invoke-Checked -WorkingDirectory $java20bRoot -Command "javac" -Arguments @("-encoding", "UTF-8", "WebApiPrepDemo.java")

Write-Host "[6/8] Java-21 Maven/JUnit sample"
$java21Source = Join-Path $handsonRoot "java-21-junit-basics.md"
$java21Root = Join-Path $verificationRoot "java21"
$pom = Get-CodeBlockAfterHeading -FilePath $java21Source -Heading "### Step 1:" -Language "xml"
$taxCalculator = Get-CodeBlockAfterHeading -FilePath $java21Source -Heading "### Step 2:" -Language "java"
# Step 5 now contains the complete TaxCalculatorTest.java source.
$taxCalculatorTest = Get-CodeBlockAfterHeading -FilePath $java21Source -Heading "### Step 5:" -Language "java"
Write-Utf8File -Path (Join-Path $java21Root "pom.xml") -Content $pom
Write-Utf8File -Path (Join-Path $java21Root "src/main/java/com/example/tax/TaxCalculator.java") -Content $taxCalculator
Write-Utf8File -Path (Join-Path $java21Root "src/test/java/com/example/tax/TaxCalculatorTest.java") -Content $taxCalculatorTest
Invoke-Checked -WorkingDirectory $java21Root -Command "mvn" -Arguments @("test")

Write-Host "[7/8] Local Markdown links and code fences"
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
    throw ("Broken local links:" + [Environment]::NewLine +
        ($brokenLinks -join [Environment]::NewLine))
}
if ($oddFences.Count -gt 0) {
    throw ("Unbalanced Markdown fences:" + [Environment]::NewLine +
        ($oddFences -join [Environment]::NewLine))
}

Write-Host "[8/8] Lesson/answer coverage, progression, and current navigation"
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
    throw ("Missing mini-exercise answers:" + [Environment]::NewLine +
        ($missingAnswers -join [Environment]::NewLine))
}

$incompleteAnswerLevels = [Collections.Generic.List[string]]::new()
Get-ChildItem -LiteralPath $answerRoot -File -Filter '*.md' | ForEach-Object {
    $answerText = [IO.File]::ReadAllText($_.FullName, [Text.Encoding]::UTF8)
    foreach ($level in 1..3) {
        $levelHeadingPattern = '## \u30EC\u30D9\u30EB' + $level
        if ($answerText -notmatch $levelHeadingPattern) {
            $incompleteAnswerLevels.Add("$($_.Name): level $level")
        }
    }
}
if ($incompleteAnswerLevels.Count -gt 0) {
    throw ('Mini-exercise answer level headings are missing:' + [Environment]::NewLine +
        ($incompleteAnswerLevels -join [Environment]::NewLine))
}

$nonCumulativeLessons = [Collections.Generic.List[string]]::new()
$resetInstructions = [Collections.Generic.List[string]]::new()
Get-ChildItem -LiteralPath $handsonRoot -File -Filter 'java-*.md' |
    Where-Object { $_.BaseName -notin @('java-20-javadoc-reading', 'java-21-junit-basics') } |
    ForEach-Object {
        $markdown = [IO.File]::ReadAllText($_.FullName, [Text.Encoding]::UTF8)
        $exerciseHeading = '## 5.'
        $exerciseIndex = $markdown.IndexOf($exerciseHeading, [StringComparison]::Ordinal)
        if ($exerciseIndex -ge 0) {
            $guidanceLength = [Math]::Min(500, $markdown.Length - $exerciseIndex)
            $guidance = $markdown.Substring($exerciseIndex, $guidanceLength)
            if ($guidance -notmatch '\u5F15\u304D\u7D99|\u9806\u756A\u306B|\u76F4\u524D\u306E\u5909\u66F4|\u30EC\u30D9\u30EB1\u304B\u3089') {
                $nonCumulativeLessons.Add($_.Name)
            }

            $exerciseEnd = $markdown.IndexOf('## 6.', $exerciseIndex, [StringComparison]::Ordinal)
            if ($exerciseEnd -lt 0) {
                $exerciseEnd = $markdown.Length
            }
            $exerciseText = $markdown.Substring($exerciseIndex, $exerciseEnd - $exerciseIndex)
            if ($exerciseText -match '\u5B8C\u6210\u30B3\u30FC\u30C9\u3078\u623B|\u5B8C\u6210\u72B6\u614B\u3078\u623B') {
                $resetInstructions.Add($_.Name)
            }
        }
    }
if ($nonCumulativeLessons.Count -gt 0) {
    throw ('Mini-exercise progression guidance is missing:' + [Environment]::NewLine +
        ($nonCumulativeLessons -join [Environment]::NewLine))
}

if ($resetInstructions.Count -gt 0) {
    throw ('Reset-based mini-exercise instructions remain:' + [Environment]::NewLine +
        ($resetInstructions -join [Environment]::NewLine))
}

$staleLesson0Paths = @(
    (Join-Path $handsonRoot "java-20a-record-enum.md"),
    (Join-Path $handsonRoot "java-20b-web-api-prep.md")
)
$staleLesson0 = @(Select-String -LiteralPath $staleLesson0Paths -Pattern 'Lesson0' -Encoding UTF8)
if ($staleLesson0.Count -gt 0) {
    throw ("Stale Lesson0 references remain in Java-20A/20B:" + [Environment]::NewLine +
        ($staleLesson0 -join [Environment]::NewLine))
}

Write-Host "Java handson verification passed."
