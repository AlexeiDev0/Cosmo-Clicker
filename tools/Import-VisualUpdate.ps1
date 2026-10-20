param(
    [Parameter(Mandatory=$true)][string]$Icon,
    [Parameter(Mandatory=$true)][string]$Background,
    [Parameter(Mandatory=$true)][string]$Drones,
    [Parameter(Mandatory=$true)][string]$Debris,
    [string]$DebrisVersion = 'v3'
)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
Add-Type -ReferencedAssemblies System.Drawing -TypeDefinition @'
using System;
using System.Drawing;
public static class SpriteGutters {
    public static int Find(Bitmap image, int expected, int radius, int from, int to, bool vertical) {
        int best = expected;
        long minimum = long.MaxValue;
        int limit = vertical ? image.Width : image.Height;
        for (int position = Math.Max(1, expected-radius); position < Math.Min(limit-1, expected+radius); position++) {
            long sum = 0;
            for (int other = from; other < to; other++) {
                int alpha = vertical ? image.GetPixel(position, other).A : image.GetPixel(other, position).A;
                if (alpha > 24) sum += alpha;
            }
            if (sum < minimum || (sum == minimum && Math.Abs(position-expected) < Math.Abs(best-expected))) {
                best = position; minimum = sum;
            }
        }
        return best;
    }
}
'@
$projectRoot = Split-Path $PSScriptRoot -Parent
$resRoot = Join-Path $projectRoot 'app/src/main/res'
$artRoot = Join-Path $projectRoot 'docs/visual/update-v3'
New-Item -ItemType Directory -Force -Path $artRoot | Out-Null
foreach ($asset in @(@{Path=$Icon;Name='launcher-source.png'}, @{Path=$Background;Name='background-source.png'}, @{Path=$Drones;Name='drones-source.png'}, @{Path=$Debris;Name=$(if($DebrisVersion -eq 'v3'){'debris-source.png'}else{"debris-$DebrisVersion-source.png"})})) {
    $originalPath=(Resolve-Path -LiteralPath $asset.Path).Path
    $savedPath=Join-Path $artRoot $asset.Name
    if ($originalPath -ne $savedPath) { Copy-Item -LiteralPath $originalPath -Destination $savedPath }
}
Copy-Item -LiteralPath $Icon -Destination (Join-Path $resRoot 'drawable-nodpi/launcher_foreground_art_v3.png')
Copy-Item -LiteralPath $Background -Destination (Join-Path $resRoot 'drawable-nodpi/background_cosmic_v5.png')

function Export-SpriteSheet([string]$Path, [int]$Columns, [int]$Rows, [int]$Count, [string]$Prefix, [string]$Version = 'v3') {
    $sheet = [Drawing.Image]::FromFile((Resolve-Path -LiteralPath $Path).Path)
    try {
        for ($index=0; $index -lt $Count; $index++) {
            $column=$index % $Columns
            $row=[int][Math]::Floor($index / $Columns)
            $left=[int][Math]::Round($column * $sheet.Width / $Columns)
            $top=[int][Math]::Round($row * $sheet.Height / $Rows)
            $right=[int][Math]::Round(($column+1) * $sheet.Width / $Columns)
            $bottom=[int][Math]::Round(($row+1) * $sheet.Height / $Rows)
            $horizontalRadius=[int]($sheet.Width / $Columns * 0.2)
            $verticalRadius=[int]($sheet.Height / $Rows * 0.2)
            if ($row -gt 0) { $top=[SpriteGutters]::Find($sheet,$top,$verticalRadius,0,$sheet.Width,$false) }
            if ($row -lt $Rows-1) { $bottom=[SpriteGutters]::Find($sheet,$bottom,$verticalRadius,0,$sheet.Width,$false) }
            if ($column -gt 0) { $left=[SpriteGutters]::Find($sheet,$left,$horizontalRadius,$top,$bottom,$true) }
            if ($column -lt $Columns-1) { $right=[SpriteGutters]::Find($sheet,$right,$horizontalRadius,$top,$bottom,$true) }
            $bitmap=[Drawing.Bitmap]::new(256,256)
            $graphics=[Drawing.Graphics]::FromImage($bitmap)
            try {
                $graphics.InterpolationMode=[Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
                $graphics.DrawImage($sheet,[Drawing.Rectangle]::new(0,0,256,256),$left,$top,$right-$left,$bottom-$top,[Drawing.GraphicsUnit]::Pixel)
                $bitmap.Save((Join-Path $resRoot ('drawable-nodpi/{0}_{1:D2}_{2}.png' -f $Prefix,($index+1),$Version)),[Drawing.Imaging.ImageFormat]::Png)
            } finally { $graphics.Dispose(); $bitmap.Dispose() }
        }
    } finally { $sheet.Dispose() }
}
Export-SpriteSheet $Drones 6 5 29 'toon_drone'
Export-SpriteSheet $Debris 7 4 28 'toon_debris' $DebrisVersion

$iconImage=[Drawing.Image]::FromFile((Resolve-Path -LiteralPath $Icon).Path)
try {
    foreach ($entry in @(@{Size=48;Folder='mipmap-mdpi'},@{Size=72;Folder='mipmap-hdpi'},@{Size=96;Folder='mipmap-xhdpi'},@{Size=144;Folder='mipmap-xxhdpi'},@{Size=192;Folder='mipmap-xxxhdpi'},@{Size=512;Folder='store'})) {
        foreach ($round in @($false,$true)) {
            if ($entry.Folder -eq 'store' -and $round) { continue }
            $bitmap=[Drawing.Bitmap]::new($entry.Size,$entry.Size)
            $graphics=[Drawing.Graphics]::FromImage($bitmap)
            try {
                if ($round) {
                    $clip=[Drawing.Drawing2D.GraphicsPath]::new()
                    try { $clip.AddEllipse(0,0,$entry.Size,$entry.Size); $graphics.SetClip($clip) } finally { $clip.Dispose() }
                }
                $backgroundBrush=[Drawing.SolidBrush]::new([Drawing.Color]::FromArgb(255,9,21,43))
                try { $graphics.FillRectangle($backgroundBrush,0,0,$entry.Size,$entry.Size) } finally { $backgroundBrush.Dispose() }
                $graphics.InterpolationMode=[Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
                $graphics.DrawImage($iconImage,0,0,$entry.Size,$entry.Size)
                if ($entry.Folder -eq 'store') { $destination=Join-Path $projectRoot 'play-store-listing/icon-512.png' }
                else { $destination=Join-Path $resRoot ($entry.Folder+'/'+$(if($round){'ic_launcher_round.png'}else{'ic_launcher.png'})) }
                $bitmap.Save($destination,[Drawing.Imaging.ImageFormat]::Png)
                if ($entry.Size -eq 192) { $bitmap.Save((Join-Path $resRoot $(if($round){'drawable-nodpi/launcher_round_v3.png'}else{'drawable-nodpi/launcher_legacy_v3.png'})),[Drawing.Imaging.ImageFormat]::Png) }
            } finally { $graphics.Dispose(); $bitmap.Dispose() }
        }
    }
} finally { $iconImage.Dispose() }
