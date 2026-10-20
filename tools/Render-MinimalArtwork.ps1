param(
    [string]$PageName = 'minimal-artwork-preview.html',
    [string]$ImageName = 'minimal-artwork-preview.png',
    [int]$ViewHeight = 2100
)
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
$browser = 'C:/Program Files/Google/Chrome/Application/chrome.exe'
$profile = Join-Path $root 'tmp/minimal-art-browser'
$output = Join-Path $root ('docs/' + $ImageName)
$page = 'file:///' + $root.Replace('\','/') + '/docs/' + $PageName
$arguments = @('--headless','--disable-gpu','--disable-extensions','--disable-background-networking','--no-first-run','--no-default-browser-check','--hide-scrollbars',('--window-size=1280,' + $ViewHeight),('"--user-data-dir=' + $profile + '"'),('"--screenshot=' + $output + '"'),('"' + $page + '"'))
$process = Start-Process -FilePath $browser -ArgumentList $arguments -WindowStyle Hidden -PassThru -Wait
if ($process.ExitCode -ne 0 -or !(Test-Path -LiteralPath $output)) { throw 'Artwork preview rendering failed' }
Write-Output $output

