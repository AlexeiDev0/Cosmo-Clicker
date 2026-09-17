param([switch]$Check)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$root = Split-Path -Parent $PSScriptRoot
$utf8 = New-Object System.Text.UTF8Encoding($false)
function Save([string]$relative,[string]$content) {
    $target = Join-Path $root $relative
    if ($Check) {
        if (!(Test-Path -LiteralPath $target) -or [IO.File]::ReadAllText($target).Replace("`r`n","`n") -ne $content.Replace("`r`n","`n")) { throw "Painted planet output is out of date: $relative" }
    } else { [IO.File]::WriteAllText($target,$content,$utf8) }
}
$heroes = Get-Content (Join-Path $root 'docs/visual/hero-art-redesign.json') -Raw -Encoding UTF8 | ConvertFrom-Json
$manifest = [Collections.Generic.List[object]]::new()
$cards = [Collections.Generic.List[string]]::new()
$palette = [Collections.Generic.List[string]]::new()
for ($n=1; $n -le 39; $n++) {
    $resource = "planet_${n}_painted_v3"
    $path = Join-Path $root "app/src/main/res/drawable-nodpi/$resource.png"
    if (!(Test-Path -LiteralPath $path)) { throw "Missing painted planet: $resource" }
    $image = [Drawing.Bitmap]::FromFile($path)
    try {
        # Dominant saturated material, excluding transparent padding and dark night pixels.
        $bins = @{}
        for ($x=0; $x -lt $image.Width; $x+=8) {
            for ($y=0; $y -lt $image.Height; $y+=8) {
                $c=$image.GetPixel($x,$y)
                if ($c.A -lt 200 -or $c.GetSaturation() -lt .24 -or $c.GetBrightness() -lt .24 -or $c.GetBrightness() -gt .85) { continue }
                $key=[int][Math]::Floor($c.GetHue()/30)
                if (!$bins.ContainsKey($key)) { $bins[$key]=@{count=0;r=0;g=0;b=0;hue=$key} }
                $bin=$bins[$key]; $bin.count++; $bin.r+=$c.R; $bin.g+=$c.G; $bin.b+=$c.B
            }
        }
        $ranked=@($bins.Values | Sort-Object @{Expression='count';Descending=$true}, @{Expression='hue';Descending=$false})
        if (!$ranked.Count) { throw "No visible planet material: $resource" }
        $colors=@(0..1 | ForEach-Object {
            $bin=$ranked[[Math]::Min($_,$ranked.Count-1)]
            $rgb=@($bin.r,$bin.g,$bin.b) | ForEach-Object { [int][Math]::Round(($_/$bin.count)*.7 + 255*.3) }
            '#'+(($rgb | ForEach-Object { $_.ToString('X2') }) -join '')
        })
        $label=($heroes | Where-Object name -EQ "flat_planet_$n").label
        $manifest.Add([ordered]@{id="p$n";label=$label;resource=$resource;width=$image.Width;height=$image.Height;primary=$colors[0];secondary=$colors[1]})
        $palette.Add('        PlanetColors(Color(0xFF'+$colors[0].TrimStart('#')+'), Color(0xFF'+$colors[1].TrimStart('#')+')),')
        $cards.Add('<figure><img loading="lazy" src="../../app/src/main/res/drawable-nodpi/'+$resource+'.png" alt="'+[Security.SecurityElement]::Escape($label)+'"><figcaption>'+[Security.SecurityElement]::Escape($label)+'</figcaption></figure>')
    } finally { $image.Dispose() }
}
Save 'docs/visual/painted-planets.json' (($manifest.ToArray() | ConvertTo-Json -Depth 5)+"`n")
$html=@'
<!doctype html><html lang="ru"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Планеты — живописный стиль</title>
<style>body{margin:24px;background:#10182c;color:#eaf0ff;font:16px system-ui}h1{font-size:26px}p{color:#b9c8dd}button{padding:12px 20px;border:0;border-radius:12px;background:#7de1d4;color:#10223a;cursor:pointer}main{display:grid;grid-template-columns:repeat(auto-fit,minmax(180px,1fr));gap:16px;margin-top:20px}figure{background:#1a2740;border-radius:20px;margin:0;padding:16px;text-align:center}img{width:100%;height:170px;object-fit:contain}figcaption{padding-top:12px;font-size:13px}.small img{width:64px;height:64px;margin:20px}</style>
<h1>39 планет — живописное оформление</h1><p>Рисунки, подключённые в приложение. Это обзор ресурсов, а не снимок экрана телефона.</p><button onclick="document.body.classList.toggle('small')">Размер иконок</button><main>
'@
Save 'docs/visual/painted-planets.html' ($html+($cards -join "`n")+'</main><script>if(location.search.includes("small"))document.body.classList.add("small")</script></html>'+"`n")
$kotlin=@(
    'package com.example.myapplication.ui.theme','',
    'import androidx.compose.ui.graphics.Color','',
    '/** Generated from the actual painted sprites by Build-PaintedPlanetReview.ps1. */',
    'data class PlanetColors(val primary: Color, val secondary: Color)','',
    'object PlanetPalette {','    private val colors = listOf(',($palette -join "`n"),'    )','',
    '    fun forPlanet(id: String): PlanetColors {',
    '        val index = id.removePrefix("p").toIntOrNull()?.minus(1) ?: 0',
    '        return colors[index.coerceIn(0, colors.lastIndex)]','    }','}',''
) -join "`n"
Save 'app/src/main/java/com/example/myapplication/ui/theme/PlanetPalette.kt' $kotlin
Write-Output 'Validated 39 painted planet sprites, sampled their palettes and updated the review gallery.'
