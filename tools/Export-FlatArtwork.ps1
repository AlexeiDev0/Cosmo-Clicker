param([switch]$Check)
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$painted = @{}
$paintedPath = Join-Path $root 'docs/visual/painted-planets.json'
if (Test-Path -LiteralPath $paintedPath) {
    foreach ($planet in @(Get-Content $paintedPath -Raw -Encoding UTF8 | ConvertFrom-Json)) { $painted[$planet.id] = $planet.resource }
}
$assets = Get-Content (Join-Path $root 'docs/visual/flat-art-catalog.json') -Raw -Encoding UTF8 | ConvertFrom-Json
$refinements = Get-Content (Join-Path $root 'docs/visual/flat-art-refinements.json') -Raw -Encoding UTF8 | ConvertFrom-Json
$heroes = Get-Content (Join-Path $root 'docs/visual/hero-art-redesign.json') -Raw -Encoding UTF8 | ConvertFrom-Json
$heroNames = @{}
foreach ($asset in $heroes) { $heroNames[$asset.name] = $true }
$refinements = @($refinements | Where-Object { !$heroNames.ContainsKey($_.name) }) + @($heroes)
$support = Get-Content (Join-Path $root 'docs/visual/step04-art.json') -Raw -Encoding UTF8 | ConvertFrom-Json
$refinements = @($refinements) + @($support)
$colorPassPath = Join-Path $root 'docs/visual/colorful-design-pass.json'
$colorPass = if (Test-Path -LiteralPath $colorPassPath) { @(Get-Content $colorPassPath -Raw -Encoding UTF8 | ConvertFrom-Json) } else { @() }
$refinements = @($refinements) + @($colorPass)
$replacements = @{}
foreach ($asset in $refinements) { $replacements[$asset.name] = $asset }
$catalogNames = @{}
foreach ($asset in $assets) { $catalogNames[$asset.name] = $true }
$assets = @($assets) + @($support | Where-Object { !$catalogNames.ContainsKey($_.name) })
foreach ($asset in $assets) { $catalogNames[$asset.name] = $true }
$assets = @($assets) + @($colorPass | Where-Object { !$catalogNames.ContainsKey($_.name) })
$assets = @($assets | ForEach-Object { if ($replacements.ContainsKey($_.name)) { $replacements[$_.name] } else { $_ } })
$utf8 = New-Object System.Text.UTF8Encoding($false)
function Save-Artifact([string]$relative, [string]$content) {
    $target = Join-Path $root $relative
    if ($Check) {
        if (!(Test-Path -LiteralPath $target) -or ([IO.File]::ReadAllText($target).Replace("$([char]13)$([char]10)", "$([char]10)").TrimEnd() -ne $content.TrimEnd())) { throw "Generated artifact is out of date: $relative" }
    } else { [IO.File]::WriteAllText($target, $content, $utf8) }
}
function Escape([string]$value) { [System.Security.SecurityElement]::Escape($value) }
function As-Number($value) { ([double]$value).ToString('0.########', [Globalization.CultureInfo]::InvariantCulture) }
$figures = @()
foreach ($asset in $assets) {
    if ($asset.name -notmatch '^(flat_|ic_|ui_)[a-z0-9_]+$') { throw 'Invalid resource name' }
    $w = if ($asset.w) { $asset.w } else { 256 }
    $h = if ($asset.h) { $asset.h } else { 256 }
    $xml = @('<vector xmlns:android="http://schemas.android.com/apk/res/android" xmlns:aapt="http://schemas.android.com/aapt" android:width="' + $w + 'dp" android:height="' + $h + 'dp" android:viewportWidth="' + $w + '" android:viewportHeight="' + $h + '">')
    $svg = @('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ' + $w + ' ' + $h + '" aria-hidden="true">')
    $pathIndex = 0
    $activeClip = ''
    foreach ($p in $asset.paths) {
        if ([string]$p.clip -ne $activeClip) {
            if ($activeClip) { $xml += '</group>'; $svg += '</g>' }
            $activeClip = [string]$p.clip
            if ($activeClip) {
                $clipId = $asset.name + '_clip' + $pathIndex
                $xml += '<group><clip-path android:pathData="' + (Escape $activeClip) + '"/>'
                $svg += '<defs><clipPath id="' + $clipId + '"><path d="' + (Escape $activeClip) + '"/></clipPath></defs><g clip-path="url(#' + $clipId + ')">'
            }
        }
        $opacity = if ($null -ne $p.opacity) { As-Number $p.opacity } else { '1' }
        $strokeXml = ''
        $strokeSvg = ''
        if ($p.stroke) {
            $width = As-Number $p.width
            $strokeXml = ' android:strokeColor="' + $p.stroke + '" android:strokeWidth="' + $width + '" android:strokeLineCap="round" android:strokeLineJoin="round"'
            $strokeSvg = ' stroke="' + $p.stroke + '" stroke-width="' + $width + '" stroke-linecap="round" stroke-linejoin="round"'
        }
        if ($p.gradient) {
            $g = $p.gradient
            $gradientId = $asset.name + '_g' + $pathIndex
            $xml += '    <path android:fillAlpha="' + $opacity + '" android:pathData="' + (Escape $p.d) + '"' + $strokeXml + '><aapt:attr name="android:fillColor">'
            if ($g.type -eq 'radial') {
                $xml += '<gradient android:type="radial" android:centerX="' + (As-Number $g.cx) + '" android:centerY="' + (As-Number $g.cy) + '" android:gradientRadius="' + (As-Number $g.radius) + '">'
                $svg += '<defs><radialGradient id="' + $gradientId + '" gradientUnits="userSpaceOnUse" cx="' + (As-Number $g.cx) + '" cy="' + (As-Number $g.cy) + '" r="' + (As-Number $g.radius) + '">'
            } else {
                $xml += '<gradient android:type="linear" android:startX="' + (As-Number $g.x1) + '" android:startY="' + (As-Number $g.y1) + '" android:endX="' + (As-Number $g.x2) + '" android:endY="' + (As-Number $g.y2) + '">'
                $svg += '<defs><linearGradient id="' + $gradientId + '" gradientUnits="userSpaceOnUse" x1="' + (As-Number $g.x1) + '" y1="' + (As-Number $g.y1) + '" x2="' + (As-Number $g.x2) + '" y2="' + (As-Number $g.y2) + '">'
            }
            foreach ($stop in $g.stops) {
                $xml += '<item android:color="' + $stop.c + '" android:offset="' + (As-Number $stop.at) + '"/>'
                $stopColor = $stop.c
                $stopOpacity = '1'
                if ($stopColor.Length -eq 9) {
                    $stopOpacity = As-Number ([Convert]::ToInt32($stopColor.Substring(1,2),16) / 255.0)
                    $stopColor = '#' + $stopColor.Substring(3)
                }
                $svg += '<stop offset="' + (As-Number $stop.at) + '" stop-color="' + $stopColor + '" stop-opacity="' + $stopOpacity + '"/>'
            }
            $xml += '</gradient></aapt:attr></path>'
            $svg += $(if ($g.type -eq 'radial') { '</radialGradient></defs>' } else { '</linearGradient></defs>' })
        } else {
            $xml += '    <path android:fillAlpha="' + $opacity + '" android:fillColor="' + $p.c + '" android:pathData="' + (Escape $p.d) + '"' + $strokeXml + '/>'
        }
        $fill = if ($p.c -eq '#00000000') { 'none' } else { $p.c }
        if ($p.gradient) { $fill = 'url(#' + $gradientId + ')' }
        $svg += '<path fill-opacity="' + $opacity + '" fill="' + $fill + '" d="' + (Escape $p.d) + '"' + $strokeSvg + '/>'
        $pathIndex++
    }
    if ($activeClip) { $xml += '</group>'; $svg += '</g>' }
    $xml += '</vector>'
    $svg += '</svg>'
    if ($replacements.ContainsKey($asset.name)) { Save-Artifact ('app/src/main/res/drawable/' + $asset.name + '.xml') (($xml -join "$([char]10)") + "$([char]10)") }
    $group = if ($asset.name -like 'flat_planet_*') { 'planets' } elseif ($asset.name -like 'flat_drone_*') { 'drones' } elseif ($asset.name -like 'flat_case_*') { 'cases' } elseif ($asset.name -like 'flat_event_*') { 'events' } elseif ($asset.name -like 'flat_bg_*') { 'backgrounds' } elseif ($asset.name -match '^flat_(upgrade|debris)_') { 'support' } elseif ($asset.name -match '^(flat_nav_|flat_icon_|ic_|ui_)') { 'icons' } else { 'other' }
    $preview = $svg -join ''
    if ($asset.name -match '^flat_planet_(\d+)$' -and $painted.ContainsKey('p' + $Matches[1])) {
        $preview = '<img loading="lazy" src="../../app/src/main/res/drawable-nodpi/' + $painted['p' + $Matches[1]] + '.png" alt="' + (Escape $asset.label) + '" style="width:100%;height:170px;object-fit:contain">'
    }
    $figures += [pscustomobject]@{ Group = $group; Html = '<figure data-name="' + $asset.name + '">' + $preview + '<figcaption>' + (Escape $asset.label) + '</figcaption></figure>' }
}
$prefix = @'
<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Drona Salvage - Art review</title>
<style>body{background:#0e1230;color:#edf1ff;font:16px system-ui;margin:24px}nav{display:flex;gap:16px;flex-wrap:wrap}a{color:#96ecd6}button{padding:10px 18px;border:0;border-radius:12px;background:#7ce8de;color:#10162f;font:inherit;cursor:pointer}main{display:grid;grid-template-columns:repeat(auto-fit,minmax(190px,1fr));gap:16px;margin-top:20px}figure{margin:0;background:#191f41;border-radius:24px;padding:12px;display:flex;flex-direction:column;align-items:center}svg{width:100%;height:170px}figcaption{padding:8px;font-size:14px;min-height:34px;text-align:center}.small svg{width:48px;height:48px;margin:24px}.silhouette path:not([fill="none"]){fill:#e1eafa}.silhouette path[stroke]{stroke:#e1eafa}.silhouette path[fill="none"]{fill:none}h1{font-size:26px}</style>
</head><body><h1>Drona Salvage - Art review</h1><p>Same paths as Android vectors. This is an asset review, not an Android screenshot.</p>
<nav><a href="flat-art-gallery.html">All</a><a href="planets.html">39 planets</a><a href="drones.html">29 drones</a><a href="cases.html">24 case frames</a><a href="events.html">Event art</a><button onclick="document.body.classList.toggle('small')">48 px</button><button onclick="document.body.classList.toggle('silhouette')">Silhouettes</button></nav>
'@
$prefix = $prefix.Replace('<a href="events.html">Event art</a>', '<a href="events.html">Event art</a><a href="backgrounds.html">Backgrounds</a><a href="support.html">Salvage and upgrades</a>')
$prefix = $prefix.Replace('</head>', '<style>body.backgrounds main{grid-template-columns:repeat(auto-fit,minmax(220px,1fr))}body.backgrounds svg{height:440px}body.backgrounds.small svg{height:96px}body.backgrounds figure{padding:0;overflow:hidden}body.backgrounds figcaption{padding:12px}</style></head>')
$prefix = $prefix.Replace('</nav>', '<a href="icons.html">Icons</a></nav>')
$prefix = $prefix.Replace('</head>', '<style>body.icons.small svg{width:24px;height:24px}body.icons.small svg[viewBox="0 0 108 108"]{width:48px;height:48px}</style></head>')
$prefix = $prefix.Replace('</head>', '<style>.small img{width:48px!important;height:48px!important;margin:24px}.silhouette img{filter:brightness(0) invert(1)}</style></head>')
if ($painted.Count) { $prefix = $prefix.Replace('Same paths as Android vectors.', 'Painted planets and vector artwork used by the Android app.') }
foreach ($group in @('all','planets','drones','cases','events','backgrounds','support','icons')) {
    $items = if ($group -eq 'all') { $figures } else { @($figures | Where-Object Group -eq $group) }
    $name = if ($group -eq 'all') { 'flat-art-gallery' } else { $group }
    $controls = '<script>history.scrollRestoration="manual";const q=new URLSearchParams(location.search);if(q.has("small"))document.body.classList.add("small");if(q.has("silhouette"))document.body.classList.add("silhouette");window.addEventListener("load",()=>window.scrollTo(0,0));</script>'
    $pagePrefix = $prefix.Replace('<body>', '<body class="' + $group + '">')
    if ($group -eq 'icons') { $pagePrefix = $pagePrefix.Replace('>48 px</button>', '>24 px</button>') }
    Save-Artifact ('docs/visual/' + $name + '.html') ($pagePrefix + '<main>' + (($items | ForEach-Object Html) -join '') + '</main>' + $controls + '</body></html>')
}
Write-Output "Validated/exported $($replacements.Count) refined vectors and eight review pages."
