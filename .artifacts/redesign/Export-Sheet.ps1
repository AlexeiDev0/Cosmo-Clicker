param(
    [Parameter(Mandatory=$true)][string]$Source,
    [Parameter(Mandatory=$true)][string]$Prefix,
    [int]$Columns = 4,
    [int]$Rows = 2
)
$ErrorActionPreference = 'Stop'
if ($Prefix -notmatch '^[a-z0-9_]+$') { throw 'Invalid prefix' }
Add-Type -AssemblyName System.Drawing
$sheet = [Drawing.Bitmap]::FromFile($Source)
try {
    $cellWidth = [int][Math]::Floor($sheet.Width / $Columns)
    $cellHeight = [int][Math]::Floor($sheet.Height / $Rows)
    $exports = Join-Path $PSScriptRoot 'exports'
    New-Item -ItemType Directory -Force $exports | Out-Null
    for ($index = 0; $index -lt $Columns * $Rows; $index++) {
        $rect = [Drawing.Rectangle]::new(($index % $Columns) * $cellWidth, [int][Math]::Floor($index / $Columns) * $cellHeight, $cellWidth, $cellHeight)
        $cell = $sheet.Clone($rect, [Drawing.Imaging.PixelFormat]::Format32bppArgb)
        $name = $Prefix + '_' + ($index + 1) + '.png'
        $path = Join-Path $exports $name
        try { $cell.Save($path, [Drawing.Imaging.ImageFormat]::Png) } finally { $cell.Dispose() }
        & (Join-Path $PSScriptRoot 'Import-Art.ps1') -Source $path -Name $name -MaxEdge 384
    }
} finally { $sheet.Dispose() }
