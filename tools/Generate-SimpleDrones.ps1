$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
$utf8 = [Text.UTF8Encoding]::new($false)
$cards = [Collections.Generic.List[string]]::new()
for ($number = 1; $number -le 29; $number++) {
    $accent = if ($number -le 10) { '#AFBBCB' } elseif ($number -le 18) { '#7AE5AE' } elseif ($number -le 24) { '#73C6FF' } elseif ($number -le 27) { '#C39BFF' } else { '#FFD479' }
    $parts = [Collections.Generic.List[object]]::new()
    function Add-Part([string]$color, [string]$shape, [bool]$outline = $false) {
        $parts.Add(@{ color = $color; shape = $shape; outline = $outline })
    }
    $family = ($number - 1) % 6
    $variant = [int][Math]::Floor(($number - 1) / 6)
    # Broad silhouettes and a single sensor remain readable at 38dp.
    Add-Part '#26354C' 'M44,108H212V142H44Z'
    switch ($family) {
        0 {
            Add-Part '#657D98' 'M24,98Q24,86 36,86H58Q70,86 70,98V150Q70,162 58,162H36Q24,162 24,150Z M186,98Q186,86 198,86H220Q232,86 232,98V150Q232,162 220,162H198Q186,162 186,150Z' $true
            Add-Part '#B6C8D8' 'M86,64H170Q186,64 186,80V160Q186,182 164,182H92Q70,182 70,160V80Q70,64 86,64Z' $true
        }
        1 {
            Add-Part '#657D98' 'M18,104L64,78V160L18,146Z M238,104L192,78V160L238,146Z' $true
            Add-Part '#B6C8D8' 'M128,48L184,90V154L128,198L72,154V90Z' $true
        }
        2 {
            Add-Part '#657D98' 'M26,76H64L76,94V160L58,176H26Z M192,76H230V176H198L180,160V94Z' $true
            Add-Part '#B6C8D8' 'M84,60H172L192,82V166L168,190H88L64,166V82Z' $true
        }
        3 {
            Add-Part '#657D98' 'M18,128L64,88L84,136L60,162Z M238,128L192,88L172,136L196,162Z' $true
            Add-Part '#B6C8D8' 'M128,56C172,56 188,90 188,126C188,166 166,188 128,188C90,188 68,166 68,126C68,90 84,56 128,56Z' $true
        }
        4 {
            Add-Part '#657D98' 'M24,84H66V166H24Z M190,84H232V166H190Z' $true
            Add-Part '#B6C8D8' 'M88,58H168L192,106L176,184H80L64,106Z' $true
        }
        5 {
            Add-Part '#657D98' 'M20,102L76,62V164L20,150Z M236,102L180,62V164L236,150Z' $true
            Add-Part '#B6C8D8' 'M102,48H154L184,80V162L154,192H102L72,162V80Z' $true
        }
    }
    # A large colored armor panel communicates rarity without fine decoration.
    Add-Part $accent 'M94,72H162L174,88H82Z'
    Add-Part '#142338' 'M94,96H162Q170,96 170,104V140Q170,148 162,148H94Q86,148 86,140V104Q86,96 94,96Z'
    switch ($variant) {
        0 { Add-Part $accent 'M108,122a20,20 0 1,0 40,0a20,20 0 1,0 -40,0'; Add-Part '#F3FCFF' 'M116,116a6,6 0 1,0 12,0a6,6 0 1,0 -12,0' }
        1 { Add-Part $accent 'M98,110H116V134H98Z M140,110H158V134H140Z' }
        2 { Add-Part $accent 'M98,114H158V130H98Z'; Add-Part '#F3FCFF' 'M108,114H118V130H108Z' }
        3 { Add-Part $accent 'M128,102L150,122L128,142L106,122Z'; Add-Part '#F3FCFF' 'M128,112L138,122L128,132L118,122Z' }
        4 { Add-Part $accent 'M100,112H112V132H100Z M122,108H134V136H122Z M144,112H156V132H144Z' }
    }
    Add-Part '#26354C' 'M96,180H116V198H96Z M140,180H160V198H140Z'
    Add-Part '#8DE8F6' 'M98,198H114L110,212H102Z M142,198H158L154,212H146Z'
    if ($number -gt 10) { Add-Part $accent 'M122,38H134V58H122Z' }
    if ($number -gt 24) { Add-Part $accent 'M94,46L106,30L118,46Z M138,46L150,30L162,46Z' }
    $xml = [Collections.Generic.List[string]]::new()
    $svg = [Collections.Generic.List[string]]::new()
    foreach ($part in $parts) {
        $stroke = if ($part.outline) { ' android:strokeColor="#26354C" android:strokeWidth="5" android:strokeLineJoin="round"' } else { '' }
        $xml.Add('    <path android:fillColor="' + $part.color + '" android:pathData="' + $part.shape + '"' + $stroke + '/>')
        $svgStroke = if ($part.outline) { ' stroke="#26354C" stroke-width="5" stroke-linejoin="round"' } else { '' }
        $svg.Add('<path fill="' + $part.color + '" d="' + $part.shape + '"' + $svgStroke + '/>')
    }
    $vector = '<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="128dp" android:height="128dp" android:viewportWidth="256" android:viewportHeight="256">' + "`n" + ($xml -join "`n") + "`n</vector>`n"
    [IO.File]::WriteAllText((Join-Path $root "app/src/main/res/drawable/simple_drone_$number.xml"), $vector, $utf8)
    $cards.Add('<figure><svg viewBox="0 0 256 256" aria-label="Drone ' + $number + '">' + ($svg -join '') + '</svg><figcaption>' + $number + '</figcaption></figure>')
}
$html = '<!doctype html><html lang="ru"><meta charset="utf-8"><title>Простые космические дроны</title><style>body{background:#101b2f;color:#dfeefa;font:16px system-ui;margin:24px}main{display:grid;grid-template-columns:repeat(auto-fit,minmax(150px,1fr));gap:12px}figure{margin:0;background:#1b2b43;border-radius:16px;text-align:center;padding:12px}svg{width:140px;height:140px}figcaption{color:#aabbcc}</style><h1>Простые космические дроны</h1><p>29 моделей · цвет редкости · крупные сенсоры</p><main>' + ($cards -join "`n") + '</main></html>'
[IO.File]::WriteAllText((Join-Path $root 'docs/simple-drones-preview.html'), $html, $utf8)
