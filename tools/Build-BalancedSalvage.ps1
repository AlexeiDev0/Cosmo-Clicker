$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$assets = [Collections.Generic.List[object]]::new()
function Part($color, $d) { [ordered]@{c=$color;d=$d} }
function Metal($d, $top='#8197AB', $bottom='#23374F') {
    [ordered]@{c=$top;d=$d;stroke='#B5CDDC';width=1.2;gradient=[ordered]@{
        x1=60;y1=45;x2=180;y2=205;stops=@(
            @{at=0;c=$top},@{at=.42;c='#486079'},@{at=1;c=$bottom}
        )
    }}
}
$accents=@('#83E6EA','#A2E49E','#8CBFFF','#C8A2FF','#FFD18B')
for ($i=1;$i -le 29;$i++) {
    $tier=if($i -le 10){0}elseif($i -le 18){1}elseif($i -le 24){2}elseif($i -le 27){3}else{4}
    $accent=$accents[$tier]
    $variant=($i-1)%6
    $width=18+([int][math]::Floor(($i-1)/6)%3)*4
    $left=58-$width
    $right=198+$width
    $paths=[Collections.Generic.List[object]]::new()
    # Compact working machines: clear silhouettes, bevels, one optic, no ornament.
    $paths.Add((Part '#0C1B30' "M$left,107H$right V139H$left Z"))
    $paths.Add((Metal "M$left,88H62L76,101V145L62,158H$left L24,145V101Z"))
    $paths.Add((Metal "M194,88H$right L232,101V145L$right,158H194L180,145V101Z"))
    $paths.Add((Part '#13263C' 'M32,111H59V133H32ZM197,111H224V133H197Z'))
    $paths.Add((Part $accent 'M36,116H55V121H36ZM201,116H220V121H201Z'))
    $hull = switch($variant) {
        0 {'M82,59H174L190,80V156L168,185H88L66,156V80Z'}
        1 {'M128,46L188,82V153L158,188H98L68,153V82Z'}
        2 {'M92,60H164L195,108L176,170L128,194L80,170L61,108Z'}
        3 {'M76,65H180L189,151L164,184H92L67,151Z'}
        4 {'M128,43L177,76L193,142L160,181H96L63,142L79,76Z'}
        5 {'M88,57H168L185,88V162L155,189H101L71,162V88Z'}
    }
    $paths.Add((Metal $hull))
    $paths.Add((Part '#102339' 'M92,93H164L174,106V140L161,153H95L82,140V106Z'))
    $paths.Add((Part '#071424' 'M103,119a25,25 0 1,0 50,0a25,25 0 1,0 -50,0'))
    $paths.Add((Part $accent 'M109,119a19,19 0 1,0 38,0a19,19 0 1,0 -38,0'))
    $paths.Add((Part '#23445D' 'M116,119a12,12 0 1,0 24,0a12,12 0 1,0 -24,0'))
    $paths.Add((Part '#E8FAFF' 'M120,115a5,5 0 1,0 10,0a5,5 0 1,0 -10,0'))
    $paths.Add((Part '#172B43' 'M94,162H113V165H94ZM143,162H162V165H143ZM99,169H114V172H99ZM142,169H157V172H142Z'))
    $paths.Add((Part $accent 'M105,75H151V79H105Z'))
    $paths.Add((Part '#D1E2ED' 'M88,89h4v4h-4ZM164,89h4v4h-4ZM88,145h4v4h-4ZM164,145h4v4h-4Z'))
    if($variant%2 -eq 0) {
        $paths.Add((Metal 'M96,183H108V201L119,215L112,224L96,206ZM148,183H160V206L144,224L137,215L148,201Z' '#D8B980' '#6E5134'))
    } else {
        $paths.Add((Part '#172B43' 'M99,183H119V202H99ZM137,183H157V202H137Z'))
        $paths.Add((Part $accent 'M102,202H116L109,222ZM140,202H154L147,222Z'))
    }
    if($tier -gt 1) {
        $paths.Add((Metal 'M116,58V40H140V58Z'))
        $paths.Add((Part $accent 'M121,44H135V49H121Z'))
    }
    $assets.Add([ordered]@{name="flat_mid_drone_$i";label="Drone $i - balanced industrial style";paths=@($paths.ToArray())})
}
foreach($tier in @('common','rare','legendary')) {
    $accent=switch($tier){common{'#83E6EA'}rare{'#C8A2FF'}legendary{'#FFD18B'}}
    $trim=switch($tier){common{'#ABC4D0'}rare{'#AB9ACD'}legendary{'#D8B980'}}
    foreach($frame in 1..8) {
        $open=if($frame -le 2){0}else{($frame-2)*7}
        $top=78-$open;$front=101-$open;$edge=123-$open
        $paths=[Collections.Generic.List[object]]::new()
        if($open -gt 0){
            $paths.Add([ordered]@{c=$accent;d="M67,112L84,$edge H177L198,112V160H67Z";opacity=.18})
            $paths.Add((Part $accent 'M69,114H193V120H69Z'))
        }
        $paths.Add((Metal 'M42,124L65,105H196L220,124V196L197,216H65L42,196Z' '#4D6A88' '#0B1C33'))
        $paths.Add((Part '#102339' 'M196,130L211,123V191L196,202Z'))
        $paths.Add((Metal 'M51,129H191V198L182,205H64L51,194Z' '#708BA4' '#20364E'))
        $paths.Add((Metal 'M55,128H74V199H63L55,190ZM169,128H190V193L180,201H169Z' $trim '#33465B'))
        $paths.Add((Part '#14283F' 'M80,160H101V163H80ZM80,169H98V172H80ZM146,160H164V163H146ZM149,169H164V172H149Z'))
        $paths.Add((Part $accent 'M80,188H101V192H80ZM146,188H167V192H146Z'))
        $paths.Add((Metal 'M107,143L116,136H139L148,143V171L139,180H116L107,171Z'))
        $paths.Add((Part '#0B1C33' 'M116,156a12,12 0 1,0 24,0a12,12 0 1,0 -24,0'))
        $paths.Add((Part $accent 'M121,156a7,7 0 1,0 14,0a7,7 0 1,0 -14,0'))
        $paths.Add((Part '#EEFAFF' 'M123,153a2,2 0 1,0 4,0a2,2 0 1,0 -4,0'))
        $paths.Add((Metal "M42,$front L65,$top H196L220,$front L196,$edge H65Z" '#7C99B2' '#20364E'))
        $paths.Add((Metal "M61,$front L77,$($top+8)H181L201,$front L184,$($edge-8)H77Z" $trim '#3A4C63'))
        $paths.Add((Part '#14283F' "M102,$($top+11)H154V$($top+21)H102Z"))
        $paths.Add((Part $accent "M108,$($top+14)H148V$($top+17)H108Z"))
        $paths.Add((Part '#E1EDF5' 'M60,143h4v4h-4ZM180,143h4v4h-4ZM60,183h4v4h-4ZM180,183h4v4h-4Z'))
        $assets.Add([ordered]@{name="flat_mid_case_${tier}_$frame";label="$tier case - $frame/8";paths=@($paths.ToArray())})
    }
}
[IO.File]::WriteAllText((Join-Path $root 'docs/visual/balanced-salvage.json'),($assets.ToArray() | ConvertTo-Json -Depth 12),[Text.UTF8Encoding]::new($false))
Write-Output 'Generated 29 balanced drones and 24 case frames.'
