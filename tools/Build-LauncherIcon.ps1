param([Parameter(Mandatory=$true)][string]$SourceImage)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$root = Split-Path $PSScriptRoot -Parent
$source = [Drawing.Image]::FromFile((Resolve-Path -LiteralPath $SourceImage).Path)
try {
    foreach ($entry in @(
        @{Size=1024;Path='drawable-nodpi/launcher_art_v2.png';Round=$false},
        @{Size=512;Path='drawable-nodpi/launcher_round_v2.png';Round=$true},
        @{Size=48;Path='mipmap-mdpi/ic_launcher.png';Round=$false},
        @{Size=72;Path='mipmap-hdpi/ic_launcher.png';Round=$false},
        @{Size=96;Path='mipmap-xhdpi/ic_launcher.png';Round=$false},
        @{Size=144;Path='mipmap-xxhdpi/ic_launcher.png';Round=$false},
        @{Size=192;Path='mipmap-xxxhdpi/ic_launcher.png';Round=$false}
    )) {
        $bitmap = [Drawing.Bitmap]::new($entry.Size, $entry.Size)
        $graphics = [Drawing.Graphics]::FromImage($bitmap)
        try {
            $graphics.InterpolationMode = [Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
            $graphics.SmoothingMode = [Drawing.Drawing2D.SmoothingMode]::AntiAlias
            if ($entry.Round) {
                $clip = [Drawing.Drawing2D.GraphicsPath]::new()
                try { $clip.AddEllipse(0, 0, $entry.Size, $entry.Size); $graphics.SetClip($clip) } finally { $clip.Dispose() }
            }
            $graphics.DrawImage($source, 0, 0, $entry.Size, $entry.Size)
            $bitmap.Save((Join-Path $root "app/src/main/res/$($entry.Path)"), [Drawing.Imaging.ImageFormat]::Png)
        } finally { $graphics.Dispose(); $bitmap.Dispose() }
        if ($entry.Path.StartsWith('mipmap-')) {
            $round = [Drawing.Bitmap]::new($entry.Size, $entry.Size)
            $roundGraphics = [Drawing.Graphics]::FromImage($round)
            $roundClip = [Drawing.Drawing2D.GraphicsPath]::new()
            try {
                $roundGraphics.InterpolationMode = [Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
                $roundGraphics.SmoothingMode = [Drawing.Drawing2D.SmoothingMode]::AntiAlias
                $roundClip.AddEllipse(0, 0, $entry.Size, $entry.Size)
                $roundGraphics.SetClip($roundClip)
                $roundGraphics.DrawImage($source, 0, 0, $entry.Size, $entry.Size)
                $round.Save((Join-Path $root ('app/src/main/res/' + $entry.Path.Replace('ic_launcher.png', 'ic_launcher_round.png'))), [Drawing.Imaging.ImageFormat]::Png)
            } finally { $roundClip.Dispose(); $roundGraphics.Dispose(); $round.Dispose() }
        }
    }
} finally { $source.Dispose() }
