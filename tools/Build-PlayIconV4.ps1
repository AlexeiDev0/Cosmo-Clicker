param([string]$SourceImage = 'docs/launcher-icon-source-v4.png')
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$root = Split-Path $PSScriptRoot -Parent
$sourcePath = Join-Path $root $SourceImage
$source = [Drawing.Image]::FromFile($sourcePath)
try {
    $outputs = @(
        @{ Path='play-store-listing/icon-512-v4.png'; Size=512; Round=$false },
        @{ Path='Google-Play-Upload/store-icon-512-v4.png'; Size=512; Round=$false },
        @{ Path='app/src/main/res/drawable-nodpi/launcher_legacy_v4.png'; Size=1024; Round=$false },
        @{ Path='app/src/main/res/drawable-nodpi/launcher_foreground_art_v4.png'; Size=1024; Round=$false },
        @{ Path='app/src/main/res/drawable-nodpi/launcher_round_v4.png'; Size=512; Round=$true },
        @{ Path='app/src/main/res/mipmap-mdpi/ic_launcher.png'; Size=48; Round=$false },
        @{ Path='app/src/main/res/mipmap-hdpi/ic_launcher.png'; Size=72; Round=$false },
        @{ Path='app/src/main/res/mipmap-xhdpi/ic_launcher.png'; Size=96; Round=$false },
        @{ Path='app/src/main/res/mipmap-xxhdpi/ic_launcher.png'; Size=144; Round=$false },
        @{ Path='app/src/main/res/mipmap-xxxhdpi/ic_launcher.png'; Size=192; Round=$false }
    )
    foreach ($entry in $outputs) {
        $bitmap = [Drawing.Bitmap]::new($entry.Size, $entry.Size, [Drawing.Imaging.PixelFormat]::Format32bppArgb)
        $graphics = [Drawing.Graphics]::FromImage($bitmap)
        try {
            $graphics.InterpolationMode = [Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
            $graphics.SmoothingMode = [Drawing.Drawing2D.SmoothingMode]::AntiAlias
            if ($entry.Round) {
                $clip = [Drawing.Drawing2D.GraphicsPath]::new()
                try { $clip.AddEllipse(0, 0, $entry.Size, $entry.Size); $graphics.SetClip($clip) } finally { $clip.Dispose() }
            }
            $graphics.DrawImage($source, 0, 0, $entry.Size, $entry.Size)
            $bitmap.Save((Join-Path $root $entry.Path), [Drawing.Imaging.ImageFormat]::Png)
        } finally { $graphics.Dispose(); $bitmap.Dispose() }
        if ($entry.Path -like 'app/src/main/res/mipmap-*/ic_launcher.png') {
            $roundPath = (Join-Path $root $entry.Path).Replace('ic_launcher.png','ic_launcher_round.png')
            $round = [Drawing.Bitmap]::new($entry.Size, $entry.Size, [Drawing.Imaging.PixelFormat]::Format32bppArgb)
            $roundGraphics = [Drawing.Graphics]::FromImage($round)
            $clip = [Drawing.Drawing2D.GraphicsPath]::new()
            try {
                $roundGraphics.InterpolationMode = [Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
                $roundGraphics.SmoothingMode = [Drawing.Drawing2D.SmoothingMode]::AntiAlias
                $clip.AddEllipse(0, 0, $entry.Size, $entry.Size)
                $roundGraphics.SetClip($clip)
                $roundGraphics.DrawImage($source, 0, 0, $entry.Size, $entry.Size)
                $round.Save($roundPath, [Drawing.Imaging.ImageFormat]::Png)
            } finally { $clip.Dispose(); $roundGraphics.Dispose(); $round.Dispose() }
        }
        Write-Output $entry.Path
    }
} finally { $source.Dispose() }
