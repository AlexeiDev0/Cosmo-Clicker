param(
    [Parameter(Mandatory=$true)][string]$Source,
    [Parameter(Mandatory=$true)][string]$Name,
    [string]$Folder = 'drawable-nodpi',
    [int]$MaxEdge = 512
)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$root = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
if ($Name -notmatch '^[a-z0-9_]+\.png$' -or $Folder -notmatch '^(drawable|mipmap)(-[a-z0-9]+)?$') { throw 'Invalid resource name' }
$masters = Join-Path $PSScriptRoot 'masters'
New-Item -ItemType Directory -Force $masters | Out-Null
Copy-Item -LiteralPath $Source -Destination (Join-Path $masters "$Folder-$Name")
$destination = Join-Path $root "app/src/main/res/$Folder/$Name"
$sourceImage = [Drawing.Image]::FromFile($Source)
try {
    $ratio = [Math]::Min(1.0, $MaxEdge / [Math]::Max($sourceImage.Width, $sourceImage.Height))
    $width = [Math]::Max(1, [int][Math]::Round($sourceImage.Width * $ratio))
    $height = [Math]::Max(1, [int][Math]::Round($sourceImage.Height * $ratio))
    $bitmap = New-Object Drawing.Bitmap($width, $height, ([Drawing.Imaging.PixelFormat]::Format32bppArgb))
    $graphics = [Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.CompositingMode = [Drawing.Drawing2D.CompositingMode]::SourceCopy
        $graphics.InterpolationMode = [Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $graphics.PixelOffsetMode = [Drawing.Drawing2D.PixelOffsetMode]::HighQuality
        $graphics.DrawImage($sourceImage, 0, 0, $width, $height)
        $bitmap.Save($destination, [Drawing.Imaging.ImageFormat]::Png)
    } finally { $graphics.Dispose(); $bitmap.Dispose() }
} finally { $sourceImage.Dispose() }
[pscustomobject]@{name=$Name;folder=$Folder;source=$Source;width=$width;height=$height;sha256=(Get-FileHash -LiteralPath $destination -Algorithm SHA256).Hash} | ConvertTo-Json -Compress | Add-Content -Encoding utf8 (Join-Path $PSScriptRoot 'completed.jsonl')
Write-Output "$Folder/$Name ${width}x${height}"
