param([string]$Browser = 'C:/Program Files/Google/Chrome/Application/chrome.exe')
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
if (!(Test-Path -LiteralPath $Browser)) { throw 'Headless Chromium browser is not available' }
$reviews=@(
    @{page='painted-planets.html';file='painted-planets.png';height=2400},
    @{page='painted-planets.html?small';file='painted-planets-small.png';height=1600},
    @{page='planets.html';file='color-planets.png';height=2700},
    @{page='planets.html?small';file='color-planets-small.png';height=1600},
    @{page='drones.html';file='color-drones.png';height=2200},
    @{page='cases.html';file='color-cases.png';height=1800},
    @{page='backgrounds.html';file='color-backgrounds.png';height=1800},
    @{page='icons.html';file='color-icons.png';height=3800},
    @{page='icons.html?small';file='color-icons-small.png';height=3000}
)
foreach ($review in $reviews) {
    $screenshot=Join-Path $root ('.artifacts/' + $review.file)
    $profile=Join-Path $root '.artifacts/color-design-browser'
    $page='file:///' + $root.Replace('\','/') + '/docs/visual/' + $review.page
    $arguments=@('--headless','--disable-gpu','--disable-extensions','--disable-background-networking','--no-first-run','--no-default-browser-check','--hide-scrollbars',
        ('--window-size=1280,'+$review.height),('"--user-data-dir='+$profile+'"'),('"--screenshot='+$screenshot+'"'),('"'+$page+'"'))
    $process=Start-Process -FilePath $Browser -ArgumentList $arguments -WindowStyle Hidden -PassThru -Wait
    if ($process.ExitCode -ne 0 -or !(Test-Path -LiteralPath $screenshot)) { throw ('Rendering failed: '+$review.file) }
    Write-Output $review.file
}
