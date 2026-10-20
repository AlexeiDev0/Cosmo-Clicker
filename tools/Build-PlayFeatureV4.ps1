param([string]$SourceImage = 'docs/play-feature-source-v4.png')
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$root = Split-Path $PSScriptRoot -Parent
$source = [Drawing.Image]::FromFile((Join-Path $root $SourceImage))
$bitmap = [Drawing.Bitmap]::new(1024, 500, [Drawing.Imaging.PixelFormat]::Format24bppRgb)
$graphics = [Drawing.Graphics]::FromImage($bitmap)
try {
    $graphics.InterpolationMode = [Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $graphics.SmoothingMode = [Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $graphics.TextRenderingHint = [Drawing.Text.TextRenderingHint]::AntiAliasGridFit
    $graphics.DrawImage($source, [Drawing.Rectangle]::new(0, 0, 1024, 500), [Drawing.Rectangle]::new(0, 11, $source.Width, $source.Height - 22), [Drawing.GraphicsUnit]::Pixel)
    $font = [Drawing.Font]::new('Segoe UI', 76, [Drawing.FontStyle]::Bold, [Drawing.GraphicsUnit]::Pixel)
    $shadow = [Drawing.SolidBrush]::new([Drawing.Color]::FromArgb(190, 0, 4, 18))
    $white = [Drawing.SolidBrush]::new([Drawing.Color]::FromArgb(245, 250, 255))
    $cyan = [Drawing.SolidBrush]::new([Drawing.Color]::FromArgb(115, 231, 249))
    $pen = [Drawing.Pen]::new([Drawing.Color]::FromArgb(115, 231, 249), 4)
    try {
        $graphics.DrawString('SPACE', $font, $shadow, 61, 165)
        $graphics.DrawString('SALVAGE', $font, $shadow, 61, 250)
        $graphics.DrawString('SPACE', $font, $white, 56, 160)
        $graphics.DrawString('SALVAGE', $font, $white, 56, 245)
        $graphics.FillEllipse($cyan, 65, 365, 10, 10)
        $graphics.DrawLine($pen, 86, 370, 342, 370)
    } finally { $font.Dispose(); $shadow.Dispose(); $white.Dispose(); $cyan.Dispose(); $pen.Dispose() }
    foreach ($path in @('play-store-listing/feature-graphic-1024x500-v4.png', 'Google-Play-Upload/feature-graphic-1024x500-v4.png')) {
        $bitmap.Save((Join-Path $root $path), [Drawing.Imaging.ImageFormat]::Png)
        Write-Output $path
    }
} finally { $graphics.Dispose(); $bitmap.Dispose(); $source.Dispose() }
