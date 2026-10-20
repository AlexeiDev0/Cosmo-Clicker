param([switch]$Apply)
$ErrorActionPreference = 'Stop'
$root = [IO.Path]::GetFullPath((Split-Path -Parent $PSScriptRoot))
$resRoot = Join-Path $root 'app/src/main/res'
$utf8 = [Text.UTF8Encoding]::new($false)
$references = [Collections.Generic.HashSet[string]]::new([StringComparer]::Ordinal)
$sourceFiles = Get-ChildItem (Join-Path $root 'app/src') -File -Recurse |
    Where-Object { $_.Extension -in '.kt', '.java', '.xml' }
foreach ($file in $sourceFiles) {
    $text = [IO.File]::ReadAllText($file.FullName)
    foreach ($match in [regex]::Matches($text, '\bR\.(drawable|mipmap|raw|font)\.([a-zA-Z0-9_]+)')) {
        [void]$references.Add($match.Groups[1].Value + '/' + $match.Groups[2].Value)
    }
    # References inside values, manifest, vector, selector and adaptive-icon XML.
    foreach ($match in [regex]::Matches($text, '@(?:\+)?(drawable|mipmap|raw|font)/([a-zA-Z0-9_]+)')) {
        [void]$references.Add($match.Groups[1].Value + '/' + $match.Groups[2].Value)
    }
}

$candidates = [Collections.Generic.List[object]]::new()
foreach ($file in Get-ChildItem $resRoot -Recurse -File) {
    $type = $file.Directory.Name.Split('-')[0]
    if ($type -notin 'drawable', 'mipmap', 'raw', 'font') { continue }
    $name = $file.BaseName -replace '\.9$', ''
    if (!$references.Contains($type + '/' + $name)) {
        $candidates.Add([PSCustomObject]@{ Path=$file.FullName.Substring($root.Length+1).Replace('\','/'); Bytes=$file.Length; Reason='No source, test or Android XML reference'; SHA256=(Get-FileHash -LiteralPath $file.FullName -Algorithm SHA256).Hash })
    }
}

# Empty files outside generated data and developer-owned configuration.
$protected = '\\(?:\.git|\.gradle|\.idea|\.kotlin|build|keystore)(?:\\|$)'
foreach ($file in Get-ChildItem $root -Recurse -File -Force -ErrorAction SilentlyContinue) {
    if ($file.FullName -match $protected -or $file.Length -ne 0) { continue }
    if ($file.Name -in '.gitkeep', '.keep', '__init__.py') { continue }
    $relative = $file.FullName.Substring($root.Length+1).Replace('\','/')
    if ($candidates.Path -contains $relative) { continue }
    $candidates.Add([PSCustomObject]@{Path=$relative;Bytes=0;Reason='Empty file';SHA256=(Get-FileHash -LiteralPath $file.FullName -Algorithm SHA256).Hash})
}

$report = Join-Path $root 'docs/cleanup-unused-files.csv'
$candidates | Sort-Object Path | Export-Csv -LiteralPath $report -NoTypeInformation -Encoding UTF8
if ($Apply) {
    # Validate every absolute path and every file hash before deleting anything.
    foreach ($candidate in $candidates) {
        $absolute = [IO.Path]::GetFullPath((Join-Path $root $candidate.Path))
        if (!$absolute.StartsWith($root + [IO.Path]::DirectorySeparatorChar, [StringComparison]::OrdinalIgnoreCase)) { throw "Outside workspace: $absolute" }
        if ($absolute -match $protected) { throw "Protected directory: $absolute" }
        $file = Get-Item -LiteralPath $absolute
        if ($file.Attributes -band [IO.FileAttributes]::ReparsePoint) { throw "Refusing link: $absolute" }
        if ((Get-FileHash -LiteralPath $absolute -Algorithm SHA256).Hash -ne $candidate.SHA256) { throw "File changed: $absolute" }
    }
    foreach ($candidate in $candidates) {
        Remove-Item -LiteralPath (Join-Path $root $candidate.Path) -Force
    }
}
[PSCustomObject]@{Files=$candidates.Count;Megabytes=[Math]::Round(($candidates | Measure-Object Bytes -Sum).Sum/1MB,2);Applied=[bool]$Apply;Report=$report} | Format-List
