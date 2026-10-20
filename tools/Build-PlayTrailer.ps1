param(
    [string]$Ffmpeg = 'C:/Users/User/AppData/Local/CapCut/Apps/9.5.0.4050/ffmpeg.exe'
)
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
$destination = Join-Path $root 'Google-Play-Upload/trailer'
New-Item -ItemType Directory -Force -Path $destination | Out-Null
$shots = Get-ChildItem (Join-Path $root 'Google-Play-Upload/phone-screenshots') -Filter '*.png' | Sort-Object Name
if ($shots.Count -ne 6) { throw 'Expected exactly six phone screenshots.' }
$lines = [Collections.Generic.List[string]]::new()
foreach ($shot in $shots) {
    $path = $shot.FullName.Replace('\', '/')
    $lines.Add("file '$path'")
    $lines.Add('duration 3')
}
$lines.Add("file '$($shots[-1].FullName.Replace('\', '/'))'")
$listPath = Join-Path $destination 'frames.txt'
[IO.File]::WriteAllLines($listPath, $lines, [Text.UTF8Encoding]::new($false))
$music = Join-Path $root 'docs/audio/observing_the_star/ObservingTheStar.ogg'
$output = Join-Path $destination 'Space-Salvage-preview-vertical.mp4'
& $Ffmpeg -hide_banner -loglevel error -y -f concat -safe 0 -i $listPath -stream_loop -1 -i $music -vf 'scale=720:1280,format=yuv420p,fade=t=in:st=0:d=0.4,fade=t=out:st=17.5:d=0.5' -r 25 -t 18 -c:v h264_mf -b:v 2800k -c:a aac -b:a 128k -movflags '+faststart' $output
if ($LASTEXITCODE -ne 0) { throw "ffmpeg failed with exit code $LASTEXITCODE" }
Write-Output $output
