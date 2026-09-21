$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$root = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
$entries = Get-Content -Raw (Join-Path $root 'tmp/visual-audit/active-images.json') | ConvertFrom-Json
$output = Join-Path $PSScriptRoot 'review'
New-Item -ItemType Directory -Force $output | Out-Null
$font = [Drawing.Font]::new('Arial', 8)
try {
    for ($page = 0; $page -lt [Math]::Ceiling($entries.Count / 48.0); $page++) {
        $sheet = [Drawing.Bitmap]::new(1200, 1080)
        $g = [Drawing.Graphics]::FromImage($sheet)
        try {
            $g.Clear([Drawing.Color]::FromArgb(12, 21, 35))
            for ($cell = 0; $cell -lt 48; $cell++) {
                $index = $page * 48 + $cell
                if ($index -ge $entries.Count) { break }
                $entry = $entries[$index]
                $path = Join-Path $root ('app/src/main/res/' + $entry.Folder + '/' + $entry.Name)
                $source = [Drawing.Image]::FromFile($path)
                try {
                    $x = ($cell % 8) * 150
                    $y = [Math]::Floor($cell / 8) * 180
                    $ratio = [Math]::Min(144.0 / $source.Width, 150.0 / $source.Height)
                    $width = [int]($source.Width * $ratio)
                    $height = [int]($source.Height * $ratio)
                    $g.DrawImage($source, [int]($x + (150 - $width) / 2), [int]$y, $width, $height)
                    $g.DrawString($entry.Name, $font, [Drawing.Brushes]::White, [Drawing.RectangleF]::new($x, $y + 151, 150, 29))
                } finally { $source.Dispose() }
            }
            $sheet.Save((Join-Path $output "current-$page.png"), [Drawing.Imaging.ImageFormat]::Png)
        } finally { $g.Dispose(); $sheet.Dispose() }
    }
} finally { $font.Dispose() }
Write-Output "Reviewed $($entries.Count) inventory images in $output"
