param(
    [Parameter(Mandatory=$true)][ValidateRange(1,39)][int]$Number,
    [Parameter(Mandatory=$true)][string]$Source
)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$root = Split-Path -Parent $PSScriptRoot
$name = "planet_${Number}_painted_v3.png"
$masterDir = Join-Path $root 'docs/visual/painted-planets/masters'
New-Item -ItemType Directory -Force -Path $masterDir | Out-Null
$masterPath = Join-Path $masterDir $name
if ([IO.Path]::GetFullPath($Source) -ne [IO.Path]::GetFullPath($masterPath)) {
    Copy-Item -LiteralPath $Source -Destination $masterPath
}
$image = [Drawing.Bitmap]::FromFile($Source)
try {
    if ($image.GetPixel(0,0).A -ne 0) { throw 'The planet sprite must have a transparent background' }
    $edge = 768
    $bitmap = New-Object Drawing.Bitmap($edge,$edge,([Drawing.Imaging.PixelFormat]::Format32bppArgb))
    $graphics = [Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.CompositingMode = [Drawing.Drawing2D.CompositingMode]::SourceCopy
        $graphics.InterpolationMode = [Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $graphics.PixelOffsetMode = [Drawing.Drawing2D.PixelOffsetMode]::HighQuality
        $graphics.DrawImage($image,0,0,$edge,$edge)
        $destination = Join-Path $root "app/src/main/res/drawable-nodpi/$name"
        $bitmap.Save($destination,[Drawing.Imaging.ImageFormat]::Png)
    } finally { $graphics.Dispose(); $bitmap.Dispose() }
} finally { $image.Dispose() }
Write-Output $name
