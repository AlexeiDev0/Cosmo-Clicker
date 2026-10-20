param(
    [string]$Source = 'play-store-listing/screenshots/ru',
    [string]$Destination = 'Google-Play-Upload/phone-screenshots'
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$project = Split-Path $PSScriptRoot -Parent
$sourcePath = Join-Path $project $Source
$destinationPath = Join-Path $project $Destination
New-Item -ItemType Directory -Force -Path $destinationPath | Out-Null

$shots = @(
    @{ Input = '01-gameplay.png'; Output = '01-MAIN-Salvage-the-Galaxy.png'; Title = 'SALVAGE THE GALAXY'; Angle = -2; Color = '#C7EFFF' },
    @{ Input = '02-hangar.png'; Output = '02-Build-Your-Drone-Fleet.png'; Title = 'BUILD YOUR DRONE FLEET'; Angle = 1; Color = '#B8F0DA' },
    @{ Input = '05-cases.png'; Output = '03-Open-Reward-Cases.png'; Title = 'OPEN REWARD CASES'; Angle = -1; Color = '#FFE1A4' },
    @{ Input = '04-upgrades.png'; Output = '04-Upgrade-Your-Ship.png'; Title = 'UPGRADE YOUR SHIP'; Angle = 2; Color = '#C7EFFF' },
    @{ Input = '06-route.png'; Output = '05-Explore-New-Worlds.png'; Title = 'EXPLORE NEW WORLDS'; Angle = -2; Color = '#B8F0DA' },
    @{ Input = '07-quests.png'; Output = '06-Complete-Daily-Missions.png'; Title = 'COMPLETE DAILY MISSIONS'; Angle = 1; Color = '#FFE1A4' }
)

foreach ($shot in $shots) {
    $inputPath = Join-Path $sourcePath $shot.Input
    if (-not (Test-Path $inputPath)) { throw "Missing screenshot: $inputPath" }
    $sourceImage = [System.Drawing.Image]::FromFile($inputPath)
    try {
        if ($sourceImage.Width -ne 1080 -or $sourceImage.Height -ne 1920) {
            throw "Unexpected screenshot size: $inputPath ($($sourceImage.Width)x$($sourceImage.Height))"
        }
        # The removed source rows contain only the Android status and navigation bars.
        $topCrop = 95
        $bottomCrop = 55
        $gameHeight = $sourceImage.Height - $topCrop - $bottomCrop
        $gameTop = 0
        $footerHeight = $topCrop + $bottomCrop
        $bitmap = New-Object System.Drawing.Bitmap(1080, 1920, [System.Drawing.Imaging.PixelFormat]::Format24bppRgb)
        try {
            $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
            try {
                $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
                $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
                $graphics.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAliasGridFit
                $graphics.Clear([System.Drawing.ColorTranslator]::FromHtml('#0A1428'))
                $sourceRect = New-Object System.Drawing.Rectangle(0, $topCrop, 1080, $gameHeight)
                $targetRect = New-Object System.Drawing.Rectangle(0, 0, 1080, $gameHeight)
                $graphics.DrawImage($sourceImage, $targetRect, $sourceRect, [System.Drawing.GraphicsUnit]::Pixel)

                $footerRect = New-Object System.Drawing.Rectangle(0, $gameHeight, 1080, $footerHeight)
                $topColor = [System.Drawing.ColorTranslator]::FromHtml('#11233D')
                $bottomColor = [System.Drawing.ColorTranslator]::FromHtml('#080F20')
                $gradient = New-Object System.Drawing.Drawing2D.LinearGradientBrush($footerRect, $topColor, $bottomColor, 90)
                try { $graphics.FillRectangle($gradient, $footerRect) } finally { $gradient.Dispose() }
                $linePen = New-Object System.Drawing.Pen([System.Drawing.ColorTranslator]::FromHtml('#75BDE2'), 3)
                try { $graphics.DrawLine($linePen, 0, $gameHeight + 1, 1080, $gameHeight + 1) } finally { $linePen.Dispose() }

                $font = New-Object System.Drawing.Font('Segoe UI', 64, [System.Drawing.FontStyle]::Bold, [System.Drawing.GraphicsUnit]::Pixel)
                try {
                    $measured = $graphics.MeasureString($shot.Title, $font)
                    if ($measured.Width -gt 970) {
                        $font.Dispose()
                        $font = New-Object System.Drawing.Font('Segoe UI', 58, [System.Drawing.FontStyle]::Bold, [System.Drawing.GraphicsUnit]::Pixel)
                    }
                    $format = New-Object System.Drawing.StringFormat
                    try {
                        $format.Alignment = [System.Drawing.StringAlignment]::Center
                        $format.LineAlignment = [System.Drawing.StringAlignment]::Center
                        $graphics.TranslateTransform(540, $gameHeight + 75)
                        $graphics.RotateTransform([single]$shot.Angle)
                        $textRect = New-Object System.Drawing.RectangleF(-515, -60, 1030, 120)
                        $shadow = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(130, 0, 0, 0))
                        $color = New-Object System.Drawing.SolidBrush([System.Drawing.ColorTranslator]::FromHtml($shot.Color))
                        try {
                            $shadowRect = New-Object System.Drawing.RectangleF(-512, -57, 1030, 120)
                            $graphics.DrawString($shot.Title, $font, $shadow, $shadowRect, $format)
                            $graphics.DrawString($shot.Title, $font, $color, $textRect, $format)
                        } finally { $shadow.Dispose(); $color.Dispose() }
                        $graphics.ResetTransform()
                    } finally { $format.Dispose() }
                } finally { $font.Dispose() }
            } finally { $graphics.Dispose() }
            $outputPath = Join-Path $destinationPath $shot.Output
            $bitmap.Save($outputPath, [System.Drawing.Imaging.ImageFormat]::Png)
            Write-Output $outputPath
        } finally { $bitmap.Dispose() }
    } finally { $sourceImage.Dispose() }
}
