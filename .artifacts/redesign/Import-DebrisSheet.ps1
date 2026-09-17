param([Parameter(Mandatory=$true)][string]$Source, [int]$StartIndex = 1)
$ErrorActionPreference = 'Stop'
if ($StartIndex -notin @(1,15)) { throw 'Expected first or second collection sheet' }
Add-Type -AssemblyName System.Drawing
$sheet = [Drawing.Bitmap]::FromFile($Source)
try {
    for ($i = 0; $i -lt 14; $i++) {
        $left = [int][Math]::Round(($i % 7) * $sheet.Width / 7.0)
        $right = [int][Math]::Round((($i % 7) + 1) * $sheet.Width / 7.0)
        $top = [int][Math]::Round([Math]::Floor($i / 7) * $sheet.Height / 2.0)
        $bottom = [int][Math]::Round(([Math]::Floor($i / 7) + 1) * $sheet.Height / 2.0)
        $cell = $sheet.Clone([Drawing.Rectangle]::new($left, $top, $right-$left, $bottom-$top), [Drawing.Imaging.PixelFormat]::Format32bppArgb)
        $name = 'debris_{0:d2}_v2.png' -f ($StartIndex+$i)
        $path = Join-Path $PSScriptRoot "exports/$name"
        try { $cell.Save($path, [Drawing.Imaging.ImageFormat]::Png) } finally { $cell.Dispose() }
        & (Join-Path $PSScriptRoot 'Import-Art.ps1') -Source $path -Name $name -MaxEdge 384
    }
} finally { $sheet.Dispose() }
