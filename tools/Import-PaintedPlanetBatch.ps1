param([string]$Manifest = 'docs/visual/painted-planets/generation-manifest.json')
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$records = Get-Content (Join-Path $root $Manifest) -Raw -Encoding UTF8 | ConvertFrom-Json
foreach ($record in $records) {
    $source = $record.source
    if (![IO.Path]::IsPathRooted($source)) { $source = Join-Path $root $source }
    $master = Join-Path $root "docs/visual/painted-planets/masters/planet_$($record.number)_painted_v3.png"
    $target = Join-Path $root "app/src/main/res/drawable-nodpi/planet_$($record.number)_painted_v3.png"
    if (!(Test-Path -LiteralPath $master) -or !(Test-Path -LiteralPath $target) -or (Get-FileHash -LiteralPath $master).Hash -ne (Get-FileHash -LiteralPath $source).Hash) {
        & (Join-Path $PSScriptRoot 'Import-PaintedPlanet.ps1') -Number $record.number -Source $source
    }
}
