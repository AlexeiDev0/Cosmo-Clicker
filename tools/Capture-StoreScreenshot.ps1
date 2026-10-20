param(
    [Parameter(Mandatory = $true)][string]$Serial,
    [Parameter(Mandatory = $true)]
    [ValidateSet('01-gameplay','02-hangar','03-collection','04-upgrades','05-cases','06-route','07-quests','08-start')]
    [string]$Name
)

$ErrorActionPreference = 'Stop'
$project = Split-Path $PSScriptRoot -Parent
$adb = Join-Path $env:LOCALAPPDATA 'Android/Sdk/platform-tools/adb.exe'
$destination = Join-Path $project 'play-store-listing/screenshots/ru'
$scratch = Join-Path $project 'tmp/store-capture'
New-Item -ItemType Directory -Force -Path $destination, $scratch | Out-Null

# Capture the currently visible game screen. No save changes, taps or resizing.
$state = & $adb -s $Serial get-state 2>&1
if ($LASTEXITCODE -ne 0 -or "$state".Trim() -ne 'device') {
    throw "Device $Serial is unavailable: $state"
}
$activity = & $adb -s $Serial shell dumpsys activity activities
if ($LASTEXITCODE -ne 0 -or -not ($activity -match '(?:mResumedActivity|topResumedActivity).*com.orbitsalvagers.droneclicker')) {
    throw 'Open Space Salvage before capturing. The game is not in the foreground.'
}
$remote = '/sdcard/space-salvage-store-capture.png'
$raw = Join-Path $scratch "$Name.png"
& $adb -s $Serial shell screencap -p $remote
if ($LASTEXITCODE -ne 0) { throw 'Screenshot capture failed.' }
& $adb -s $Serial pull $remote $raw
if ($LASTEXITCODE -ne 0) { throw 'Screenshot transfer failed.' }

# Export a lossless RGB PNG, without changing the screenshot dimensions.
Add-Type -AssemblyName System.Drawing
$source = [System.Drawing.Image]::FromFile($raw)
try {
    $long = [Math]::Max($source.Width, $source.Height)
    $short = [Math]::Min($source.Width, $source.Height)
    if ($short -lt 320 -or $long -gt 3840 -or $long -gt 2 * $short) {
        throw "Capture is $($source.Width)x$($source.Height). Use a display with a maximum 2:1 aspect ratio; do not stretch the image."
    }
    $rgb = New-Object System.Drawing.Bitmap($source.Width, $source.Height, [System.Drawing.Imaging.PixelFormat]::Format24bppRgb)
    try {
        $graphics = [System.Drawing.Graphics]::FromImage($rgb)
        try { $graphics.DrawImageUnscaled($source, 0, 0) } finally { $graphics.Dispose() }
        $output = Join-Path $destination "$Name.png"
        $rgb.Save($output, [System.Drawing.Imaging.ImageFormat]::Png)
        Write-Output $output
    } finally { $rgb.Dispose() }
} finally { $source.Dispose() }
