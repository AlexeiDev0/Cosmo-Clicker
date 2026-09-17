param([switch]$Check)
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$utf8 = New-Object System.Text.UTF8Encoding($false)
. (Join-Path $PSScriptRoot 'Planet-SurfaceDetails.ps1')

function Blend([string]$a, [string]$b, [double]$amount) {
    $result = '#'
    foreach ($offset in @(1,3,5)) {
        $first = [Convert]::ToInt32($a.Substring($offset,2),16)
        $second = [Convert]::ToInt32($b.Substring($offset,2),16)
        $result += ([int][Math]::Round($first + ($second - $first) * $amount)).ToString('X2')
    }
    return $result
}
function P([string]$color, [string]$path) { return [ordered]@{ c=$color; d=$path } }
function Lit($path, [double]$strength = .22, [double]$extent = 256) {
    if ($path.c -match '^#[A-Fa-f0-9]{6}$') {
        $path | Add-Member -NotePropertyName gradient -NotePropertyValue ([ordered]@{
            x1=$extent*.18; y1=$extent*.12; x2=$extent*.82; y2=$extent*.88
            # Warm starlight, local material colour and cool reflected shadow.
            # Twelve authored stops per material preserve coherent hue families.
            stops=@(0..11 | ForEach-Object {
                $t = $_ / 11.0
                $color = if ($t -lt .45) {
                    Blend $path.c '#FFF1C9' ($strength * [Math]::Pow(1 - $t / .45, 1.4))
                } elseif ($t -lt .65) {
                    Blend $path.c '#9DE9F5' ($strength * .16 * [Math]::Sin(($t - .45) / .2 * [Math]::PI))
                } else {
                    Blend $path.c '#20204E' ($strength * 1.65 * [Math]::Pow(($t - .65) / .35, .85))
                }
                @{at=$t; c=$color}
            })
        }) -Force
    }
    return $path
}

$assets = [Collections.Generic.List[object]]::new()
$heroes = Get-Content (Join-Path $root 'docs/visual/hero-art-redesign.json') -Raw -Encoding UTF8 | ConvertFrom-Json
foreach ($planet in @($heroes | Where-Object name -Match '^flat_(planet|drone|case)_')) {
    # Preserve the authored continents, currents, crystals and anomalous silhouettes.
    # Light is global, so adjacent forms belong to the same illustration.
    $index = 0
    foreach ($path in $planet.paths) {
        $strength = if ($index -lt 2) { .38 } else { .29 }
        $null = Lit $path $strength
        $index++
    }
    if ($planet.name -like 'flat_planet_*') { Add-PlanetSurfaceDetails $planet }
    $assets.Add($planet)
}
if (@($assets | Where-Object name -Like 'flat_planet_*').Count -ne 39) { throw 'Expected exactly 39 planets' }
if (@($assets | Where-Object name -Match '^flat_drone_\d+$').Count -ne 29) { throw 'Expected exactly 29 drones' }
if (@($assets | Where-Object name -Match '^flat_case_(common|rare|legendary)_\d+$').Count -ne 24) { throw 'Expected exactly 24 case frames' }
foreach ($planet in @($assets | Where-Object name -Like 'flat_planet_*')) {
    $colors = @($planet.paths | ForEach-Object { $_.gradient.stops.c } | Sort-Object -Unique)
    if ($colors.Count -lt 50) { throw "Planet palette is too small: $($planet.name)" }
}

# Each backdrop has a different landmark. Keep the central reading area quiet.
$backgrounds = @(
    @('main','Tidal nebula','#10162F','#263C65','#3B7190','#A89BE7'),
    @('start','Dawn beyond the orbital horizon','#15152F','#463258','#A06174','#F4C78E'),
    @('shop','Orbital salvage market','#12192D','#29445A','#3C7F80','#E8BD7A'),
    @('hangar','Fleet docking bay','#111B31','#283C5F','#447897','#95DED3'),
    @('goals','Charted constellation','#17162F','#343463','#6457A3','#BDADD9'),
    @('achievements','Celestial trophy chamber','#1C1731','#493456','#8C5D77','#F1C08C'),
    @('statistics','Deep-space observatory','#111B30','#254564','#377D95','#9ACEDE'),
    @('cases','Cargo vault','#19162E','#3D355F','#6E5592','#C6B6EA'),
    @('events','Signal across the rift','#1E172F','#503350','#954F6E','#E6A19A'),
    @('prestige','Ascension aperture','#171331','#453466','#8169AC','#8CDED9'),
    @('offline','Returning salvage convoy','#141D30','#314956','#548884','#EDCC8E'),
    @('settings','Quiet control deck','#141D2D','#2E4055','#536D83','#A5C4D0')
)
foreach ($entry in $backgrounds) {
    $name,$label,$dark,$mid,$bright,$accent = $entry
    $paths = [Collections.Generic.List[object]]::new()
    $base = [pscustomobject](P $dark 'M0,0H360V720H0Z')
    $base | Add-Member gradient ([ordered]@{ x1=0;y1=0;x2=360;y2=720;stops=@(@{at=0;c=$mid},@{at=.32;c=$dark},@{at=.74;c=$dark},@{at=1;c=(Blend $mid $dark .4)}) })
    $paths.Add($base)
    $paths.Add((P (Blend $mid $dark .25) 'M0,0H360V105C287,149 241,40 174,73C111,105 94,178 0,147Z'))
    $paths.Add((P $mid 'M0,0H360V56C273,105 231,18 166,44C105,71 55,108 0,87Z'))
    $paths.Add((P $bright 'M0,0H187C138,16 116,41 70,40C41,39 18,52 0,61Z'))
    $paths.Add((P (Blend $bright $accent .45) 'M0,0H104C63,18 48,30 0,26Z'))
    $paths.Add((P (Blend $mid $dark .35) 'M0,645C68,595 134,643 189,626C251,607 301,622 360,666V720H0Z'))
    $paths.Add((P $mid 'M0,699C79,650 124,689 183,666C249,641 305,664 360,699V720H0Z'))
    # A small, deliberate star field in the margins, with warm and cool depth.
    $paths.Add((P (Blend $bright '#D2E6FF' .38) 'M32,211a1,1 0 1,0 2,0a1,1 0 1,0 -2,0 M317,293a1.3,1.3 0 1,0 2.6,0a1.3,1.3 0 1,0 -2.6,0 M47,484a1,1 0 1,0 2,0a1,1 0 1,0 -2,0 M302,547a1,1 0 1,0 2,0a1,1 0 1,0 -2,0'))
    $paths.Add((P (Blend $accent $dark .3) 'M282,198a1.5,1.5 0 1,0 3,0a1.5,1.5 0 1,0 -3,0 M72,567a1.2,1.2 0 1,0 2.4,0a1.2,1.2 0 1,0 -2.4,0'))
    switch ($name) {
        'main' {
            $paths.Add((P (Blend $accent $dark .25) 'M283,158a19,19 0 1,0 38,0a19,19 0 1,0 -38,0'))
            $paths.Add((P $accent 'M285,151C290,138 309,134 318,146C306,141 296,146 293,157Z'))
        }
        'start' {
            $paths.Add((P $bright 'M0,720V664C100,581 248,591 360,650V720Z'))
            $paths.Add((P $accent 'M0,665C108,583 249,598 360,650V659C242,608 113,602 0,674Z'))
            $paths.Add((P '#FFF0C2' 'M287,157a17,17 0 1,0 34,0a17,17 0 1,0 -34,0'))
        }
        'shop' {
            $paths.Add((P $bright 'M252,170L292,149L333,170V194L292,214L252,194Z'))
            $paths.Add((P $accent 'M252,170L292,149L333,170L292,190Z'))
            $paths.Add((P $mid 'M292,190L333,170V194L292,214Z'))
            $paths.Add((P '#EAE1B6' 'M288,175H296V195H288Z'))
        }
        'hangar' {
            $paths.Add((P $bright 'M0,148V630H24V185Q24,165 44,165H74V148ZM360,148V630H336V185Q336,165 316,165H286V148Z'))
            $paths.Add((P $accent 'M24,185V268H29V187Q29,170 46,170H67V165H44Q24,165 24,185ZM331,187V268H336V185Q336,165 316,165H293V170H314Q331,170 331,187Z'))
            $paths.Add((P $bright 'M110,691L150,670H210L250,691L210,711H150Z'))
        }
        'goals' {
            $paths.Add([ordered]@{c='#00000000';d='M39,216L62,172L107,188L133,145';stroke=(Blend $bright $dark .4);width=1.5})
            $paths.Add((P $accent 'M34,216a5,5 0 1,0 10,0a5,5 0 1,0 -10,0 M58,172a4,4 0 1,0 8,0a4,4 0 1,0 -8,0 M103,188a4,4 0 1,0 8,0a4,4 0 1,0 -8,0 M128,145a5,5 0 1,0 10,0a5,5 0 1,0 -10,0'))
        }
        'achievements' {
            $paths.Add((P $bright 'M269,152H329V178Q329,203 299,215Q269,203 269,178Z'))
            $paths.Add((P $accent 'M299,161L305,176L321,177L309,188L312,204L299,196L286,204L289,188L277,177L293,176Z'))
            $paths.Add((P $mid 'M279,686Q279,669 296,669H307Q324,669 324,686V720H279Z'))
        }
        'statistics' {
            $paths.Add((P $bright 'M35,193a31,31 0 1,0 62,0a31,31 0 1,0 -62,0'))
            $paths.Add((P $dark 'M41,193a25,25 0 1,0 50,0a25,25 0 1,0 -50,0'))
            $paths.Add((P $accent 'M49,185Q66,163 83,185Q67,175 49,190Z'))
            $paths.Add((P $bright 'M300,702V671Q300,666 305,666H313Q318,666 318,671V702ZM272,702V686Q272,681 277,681H285Q290,681 290,686V702Z'))
        }
        'cases' {
            $paths.Add((P $bright 'M261,164Q261,153 272,153H323Q334,153 334,164V202Q334,213 323,213H272Q261,213 261,202Z'))
            $paths.Add((P $accent 'M266,166Q266,158 274,158H321Q329,158 329,166V174H266Z'))
            $paths.Add((P $dark 'M292,176H303V190Q303,195 298,195Q292,195 292,190Z'))
            $paths.Add((P '#9EE6DA' 'M295,180H301V187H295Z'))
        }
        'events' {
            $paths.Add((P $bright 'M298,169L316,146L328,158L310,181L316,201L298,214L282,201L289,181L271,158L283,146Z'))
            $paths.Add((P $accent 'M291,179Q298,170 305,179V191Q298,201 291,191Z'))
        }
        'prestige' {
            $paths.Add((P $bright 'M264,182a36,36 0 1,0 72,0a36,36 0 1,0 -72,0'))
            $paths.Add((P $accent 'M269,182a31,31 0 1,0 62,0a31,31 0 1,0 -62,0'))
            $paths.Add((P $dark 'M276,182a24,24 0 1,0 48,0a24,24 0 1,0 -48,0'))
            $paths.Add((P '#F8DDA5' 'M300,162L307,182L300,202L293,182Z'))
        }
        'offline' {
            $paths.Add((P $bright 'M259,186L285,163H313L334,186L313,198H279Z'))
            $paths.Add((P $accent 'M279,173H311L321,183H269Z'))
            $paths.Add((P '#EDE6B7' 'M257,186L232,180L242,188L232,195Z'))
        }
        'settings' {
            $paths.Add((P $bright 'M0,650H360V661H0ZM0,662H23V720H0ZM337,662H360V720H337Z'))
            $paths.Add((P $accent 'M272,680Q272,676 276,676H298Q302,676 302,680V687Q302,691 298,691H276Q272,691 272,687Z'))
        }
    }
    foreach ($path in $paths) {
        if (!$path.gradient) { $null = Lit $path .18 360 }
    }
    $assets.Add([ordered]@{name="flat_bg_$name";label=$label;w=360;h=720;paths=@($paths.ToArray())})
}

# Semantic icons: a small number of meaningful forms, a consistent 24-unit viewport.
$ink='#18233E'; $mint='#79E1C3'; $blue='#76C9F5'; $purple='#AE91ED'; $gold='#FFD38A'; $coral='#F3939E'; $white='#E3EDF7'
$icons = @{}
$icons.shop=@((P '#65579C' 'M4,9H20V19Q20,21 18,21H6Q4,21 4,19Z'),(P $mint 'M3,9L6,3H18L21,9Q19,12 17,10Q14,12 12,10Q9,12 7,10Q5,12 3,9Z'),(P $gold 'M9,14Q9,13 10,13H14Q15,13 15,14V21H9Z'))
$icons.hangar=@((P '#537099' 'M3,10L12,3L21,10V20Q21,21 20,21H4Q3,21 3,20Z'),(P $blue 'M5,10L12,5L19,10Z'),(P $ink 'M7,13Q7,11 9,11H15Q17,11 17,13V21H7Z'),(P $mint 'M10,15H14L15,18H9Z'))
$icons.quests=@((P '#7371A8' 'M5,4H8Q8,2 10,2H14Q16,2 16,4H19V20Q19,22 17,22H7Q5,22 5,20Z'),(P $purple 'M8,4H16V7H8Z'),(P $white 'M8,10H16V12H8ZM8,15H12V17H8Z'),(P $mint 'M14,17L16,19L21,13L22,15L16,22L13,19Z'))
$icons.stats=@((P '#52789F' 'M3,15Q3,14 4,14H7Q8,14 8,15V21H3Z'),(P $blue 'M10,10Q10,9 11,9H14Q15,9 15,10V21H10Z'),(P $mint 'M17,4Q17,3 18,3H21Q22,3 22,4V21H17Z'))
$icons.settings=@((P '#7685A7' 'M9,3H15L16,6L19,7L21,11V14L18,16L17,19L13,21H10L8,18L5,17L3,13V10L6,8L7,5Z'),(P $blue 'M7,12a5,5 0 1,0 10,0a5,5 0 1,0 -10,0'),(P $ink 'M9,12a3,3 0 1,0 6,0a3,3 0 1,0 -6,0'))
$icons.prestige=@((P '#7260A8' 'M2,12a10,10 0 1,0 20,0a10,10 0 1,0 -20,0'),(P $mint 'M5,12a7,7 0 1,0 14,0a7,7 0 1,0 -14,0'),(P $ink 'M7,12a5,5 0 1,0 10,0a5,5 0 1,0 -10,0'),(P $gold 'M12,5L15,12L12,19L9,12Z'))
$icons.achievements=@((P $purple 'M6,13L4,22L9,20L12,22L14,13ZM12,13L15,22L18,20L21,22L18,13Z'),(P '#CE9468' 'M4,9a8,8 0 1,0 16,0a8,8 0 1,0 -16,0'),(P $gold 'M6,8a6,6 0 1,0 12,0a6,6 0 1,0 -12,0'),(P '#A96D55' 'M12,4L13.5,7L17,7.5L14.5,10L15,13L12,11.5L9,13L9.5,10L7,7.5L10.5,7Z'))
$icons.route=@((P '#697AA6' 'M4,5H6V16Q6,18 8,18H18V20H8Q4,20 4,16Z'),(P $mint 'M1,5a4,4 0 1,0 8,0a4,4 0 1,0 -8,0'),(P $gold 'M15,19a4,4 0 1,0 8,0a4,4 0 1,0 -8,0'),(P $blue 'M16,4H21V10L16,8Z'))
$icons.debris=@((P '#9374B7' 'M4,9L10,3L18,5L21,13L15,21L6,19Z'),(P $purple 'M4,9L10,3L18,5L13,12Z'),(P $mint 'M13,12L18,5L21,13L15,21Z'),(P '#D8B1EE' 'M6,10L11,5L15,6L12,10Z'))
$icons.energy=@((P '#5D80A1' 'M8,2H16V5H18Q20,5 20,7V20Q20,22 18,22H6Q4,22 4,20V7Q4,5 6,5H8Z'),(P $mint 'M6,8H18V19Q18,20 17,20H7Q6,20 6,19Z'),(P $ink 'M13,8L8,14H11L10,18L16,12H13Z'))
$icons.lock=@((P '#6E82A3' 'M6,10V8Q6,2 12,2Q18,2 18,8V10H15V8Q15,5 12,5Q9,5 9,8V10Z'),(P $blue 'M5,10H19Q21,10 21,12V20Q21,22 19,22H5Q3,22 3,20V12Q3,10 5,10Z'),(P $ink 'M10,15a2,2 0 1,0 4,0a2,2 0 1,0 -4,0 M11,16H13V19H11Z'))
$icons.close=@((P $white 'M5,3L12,10L19,3L21,5L14,12L21,19L19,21L12,14L5,21L3,19L10,12L3,5Z'))
$icons.chevron=@((P $blue 'M8,3L17,12L8,21L5,18L11,12L5,6Z'))
$icons.language=@((P '#5D81AB' 'M2,12a10,10 0 1,0 20,0a10,10 0 1,0 -20,0'),(P $blue 'M4,12a8,8 0 1,0 16,0a8,8 0 1,0 -16,0'),(P $ink 'M4,11H20V13H4ZM11,4H13V20H11Z'),(P $mint 'M6,5L8,4Q4,12 8,20L6,19Q2,12 6,5ZM18,5L16,4Q20,12 16,20L18,19Q22,12 18,5Z'))
$icons.sound=@((P '#6982A5' 'M3,9H7L13,4V20L7,15H3Z'),(P $blue 'M7,9L13,4V20L7,15Z'),[ordered]@{c='#00000000';d='M16,8Q20,12 16,16M19,5Q25,12 19,19';stroke=$mint;width=2})
$icons.motion=@((P '#6E7BA8' 'M7,9Q7,5 12,2Q17,5 17,9V15H7Z'),(P $blue 'M8,9Q8,6 12,3Q16,6 16,9Z'),(P $ink 'M10,8a2,2 0 1,0 4,0a2,2 0 1,0 -4,0'),(P $mint 'M7,10L3,16H7ZM17,10L21,16H17Z'),(P $gold 'M9,16H15L12,23Z'))
$icons.reset=@([ordered]@{c='#00000000';d='M6,6Q13,0 19,7Q25,16 16,21Q7,25 3,16';stroke=$coral;width=3},(P $coral 'M2,2V10H10Z'))
$icons.launch=@((P '#7286A9' 'M9,12V21H15V12Z'),(P $mint 'M12,2L22,13H16V16H8V13H2Z'),(P '#D9F4DD' 'M12,2L22,13H17L12,7L7,13H2Z'))
$icons.recall=@((P '#7684AC' 'M9,3V12H15V3Z'),(P $purple 'M12,22L22,11H16V8H8V11H2Z'),(P '#DED0F3' 'M12,22L22,11H17L12,17L7,11H2Z'))
$icons.sell=@((P '#C79563' 'M2,12a10,8 0 1,0 20,0a10,8 0 1,0 -20,0'),(P $gold 'M2,10a10,8 0 1,0 20,0a10,8 0 1,0 -20,0'),(P '#AB765C' 'M12,4L17,10L12,16L7,10Z'))
$icons.core=@((P '#705999' 'M4,7L12,2L20,7V17L12,22L4,17Z'),(P $purple 'M4,7L12,2L20,7L12,12Z'),(P $mint 'M8,12a4,4 0 1,0 8,0a4,4 0 1,0 -8,0'),(P $white 'M10,11a2,2 0 1,0 4,0a2,2 0 1,0 -4,0'))
$icons.luck=@((P '#659579' 'M12,12C1,11 3,2 9,4Q12,5 12,8Q12,3 16,3C23,3 24,12 15,12C24,13 23,22 17,21Q12,20 12,16Q12,21 8,21C1,21 1,13 12,12Z'),(P $mint 'M12,12C3,11 4,4 9,5Q12,6 12,9Q12,5 16,5Q22,6 20,10Q19,12 12,12Z'),(P $gold 'M10,12a2,2 0 1,0 4,0a2,2 0 1,0 -4,0'))
$icons.ai=@((P '#667CA8' 'M5,5H19Q21,5 21,7V19Q21,21 19,21H5Q3,21 3,19V7Q3,5 5,5Z'),(P $purple 'M5,7H19V15Q19,18 16,18H8Q5,18 5,15Z'),(P $mint 'M7,10H10V13H7ZM14,10H17V13H14Z'),(P $white 'M9,15H15V17H9Z'),(P $blue 'M11,2H13V5H11Z'))
$icons.magnet=@((P '#7785AB' 'M3,4H9V13Q9,16 12,16Q15,16 15,13V4H21V13Q21,22 12,22Q3,22 3,13Z'),(P $coral 'M3,4H9V10H3Z'),(P $blue 'M15,4H21V10H15Z'),(P $white 'M3,4H9V7H3ZM15,4H21V7H15Z'))
$icons.repair=@((P '#7E9CB5' 'M15,2Q21,1 22,7L18,6L15,9L16,12Q18,12 20,10Q21,16 16,17L7,22Q4,23 2,20Q1,17 4,15L12,10Q10,5 15,2Z'),(P $mint 'M5,17L14,11L16,13L6,20Z'))
$icons.case=@((P '#6A5D9A' 'M3,7Q3,4 6,4H18Q21,4 21,7V19Q21,21 19,21H5Q3,21 3,19Z'),(P $purple 'M3,7Q3,4 6,4H18Q21,4 21,7V10H3Z'),(P $gold 'M10,9H14V15Q14,17 12,17Q10,17 10,15Z'),(P $mint 'M5,18H9V20H5ZM15,18H19V20H15Z'))
$icons.planet=@((P '#4C75A6' 'M2,12a10,10 0 1,0 20,0a10,10 0 1,0 -20,0'),(P $blue 'M3,10a8,8 0 1,0 16,0a8,8 0 1,0 -16,0'),(P $mint 'M4,7Q7,3 11,3L14,7L10,10L11,14L7,16L5,12Z'),(P $gold 'M15,12Q20,12 19,17L16,19L14,16Z'))
$icons.speed=@((P '#687AA3' 'M3,12a9,9 0 1,0 18,0a9,9 0 1,0 -18,0'),(P $blue 'M5,12a7,7 0 1,0 14,0a7,7 0 1,0 -14,0'),(P $ink 'M11,5H13V12L18,15L17,17L11,13Z'),(P $mint 'M1,6H5V8H1ZM0,11H4V13H0Z'))
$icons.fleet=@((P '#7480AB' 'M3,8H8V17H3ZM16,8H21V17H16Z'),(P $blue 'M7,5Q12,1 17,5V17Q12,22 7,17Z'),(P $mint 'M9,9H15V13H9Z'),(P $gold 'M10,19H14L12,23Z'))
$icons.warning=@((P '#C29262' 'M10,3Q12,0 14,3L23,19Q24,22 21,22H3Q0,22 1,19Z'),(P $gold 'M11,4Q12,2 13,4L21,19H3Z'),(P $ink 'M11,8H13V14H11ZM11,16H13V18H11Z'))
$icons.new=@((P $mint 'M3,5Q3,2 6,2H18Q21,2 21,5V19Q21,22 18,22H6Q3,22 3,19Z'),(P $ink 'M12,5L14,10L19,12L14,14L12,19L10,14L5,12L10,10Z'))
$icons.telegram=@((P $blue 'M2,10L21,3Q23,2 22,5L18,21Q18,23 16,21L11,17L8,20L7,14Z'),(P $white 'M7,14L18,6L10,16L8,19Z'))
$icons.torch=@((P '#7184AA' 'M4,13L9,8L16,15L11,20Q9,22 7,20Z'),(P $blue 'M9,8L12,5L19,12L16,15Z'),(P $gold 'M13,5L13,2Q22,3 22,11L19,11Z'),(P $coral 'M17,5Q21,6 21,9L19,9Z'))
$icons.harvester=@((P '#6B7DA3' 'M4,6H20L17,15H7Z'),(P $mint 'M5,6H19L16,11H8Z'),(P $blue 'M8,15H16V20H8Z'),(P $gold 'M10,2H14V5H10Z'))
$icons.beacon=@((P '#687BA4' 'M10,9H14L17,22H7Z'),(P $mint 'M9,9a3,3 0 1,0 6,0a3,3 0 1,0 -6,0'),[ordered]@{c='#00000000';d='M6,5Q2,9 6,13M18,5Q22,9 18,13';stroke=$blue;width=2},(P $gold 'M10,18H14V20H10Z'))
$icons.amplifier=@((P '#6D629F' 'M4,9L12,4L20,9V19L12,23L4,19Z'),(P $purple 'M5,9L12,5L19,9L12,13Z'),(P $mint 'M8,13H10V18H8ZM11,11H13V20H11ZM14,13H16V18H14Z'))
$icons.compressor=@((P '#6D7CA2' 'M2,3H22V7H2ZM2,17H22V21H2Z'),(P $purple 'M4,7H8L10,10H4ZM16,7H20V10H14Z'),(P $blue 'M4,14H10L8,17H4ZM14,14H20V17H16Z'),(P $mint 'M9,12a3,3 0 1,0 6,0a3,3 0 1,0 -6,0'))
$icons.singularity=@((P '#7158A7' 'M2,12a10,10 0 1,0 20,0a10,10 0 1,0 -20,0'),(P $purple 'M4,12a8,8 0 1,0 16,0a8,8 0 1,0 -16,0'),(P $ink 'M6,12a6,6 0 1,0 12,0a6,6 0 1,0 -12,0'),(P $mint 'M5,15Q12,19 20,9Q17,20 8,18Z'))
$icons.lens=@((P '#6889A9' 'M2,12Q12,0 22,12Q12,24 2,12Z'),(P $blue 'M4,12Q12,3 20,12Q12,21 4,12Z'),(P $purple 'M8,12a4,4 0 1,0 8,0a4,4 0 1,0 -8,0'),(P $mint 'M10,11a2,2 0 1,0 4,0a2,2 0 1,0 -4,0'))
$icons.pulsar=@((P '#8D73AA' 'M6,4H18Q20,4 20,6V20Q20,22 18,22H6Q4,22 4,20V6Q4,4 6,4Z'),(P $purple 'M6,6H18V20H6Z'),(P $gold 'M7,13H9L11,8L14,17L16,12H18V14H17L14,21L11,13L10,15H7Z'),(P $blue 'M9,1H15V4H9Z'))
$icons.press=@((P '#777DA1' 'M2,3H22V7H14V12H10V7H2Z'),(P $blue 'M6,12H18V16H6Z'),(P $mint 'M8,18H16V21H8Z'),(P $purple 'M2,20H6V23H2ZM18,20H22V23H18Z'))
$icons.nanites=@((P $blue 'M1,8L5,4L9,8L5,12Z'),(P $mint 'M9,16L13,12L17,16L13,20Z'),(P $purple 'M14,7L18,3L22,7L18,11Z'),(P $gold 'M3,19L5,17L7,19L5,21Z'))
$icons.forge=@((P '#7464A0' 'M3,9L8,4H16L21,9V22H3Z'),(P $purple 'M4,9L8,5H16L20,9Z'),(P $ink 'M7,13Q7,11 9,11H15Q17,11 17,13V22H7Z'),(P $coral 'M9,22Q6,16 12,13Q11,16 15,17Q19,21 15,22Z'),(P $gold 'M11,22Q9,19 12,17Q16,20 14,22Z'))
$icons.relay=@((P '#637AA1' 'M2,4H9V10H2ZM15,14H22V20H15Z'),(P $mint 'M4,5H7V8H4ZM17,15H20V18H17Z'),(P $purple 'M8,7H16Q20,7 20,11V14H17V11Q17,10 16,10H8ZM7,10V14Q7,15 8,15H16V18H8Q4,18 4,14V10Z'))
$icons.resonator=@((P '#6F7CA9' 'M9,3H15V21H9Z'),(P $mint 'M10,9a2,2 0 1,0 4,0a2,2 0 1,0 -4,0'),[ordered]@{c='#00000000';d='M6,6Q2,12 6,18M18,6Q22,12 18,18';stroke=$purple;width=2},(P $gold 'M10,16H14V18H10Z'))
$icons.entropy=@((P '#7C64A4' 'M3,5H21V8L15,12L21,16V19H3V16L9,12L3,8Z'),(P $purple 'M5,5H19V7L12,11L5,7Z'),(P $mint 'M5,19V17L12,13L19,17V19Z'),(P $gold 'M10,12a2,2 0 1,0 4,0a2,2 0 1,0 -4,0'))
$icons.anchor=@((P '#7186AD' 'M10,2H14V15H18V11H22V16Q20,23 12,23Q4,23 2,16V11H6V15H10Z'),(P $blue 'M6,16Q8,20 12,20Q16,20 18,16H14V18H10V16Z'),(P $mint 'M9,5a3,3 0 1,0 6,0a3,3 0 1,0 -6,0'),(P $ink 'M11,5a1,1 0 1,0 2,0a1,1 0 1,0 -2,0'))
$icons.omega=@((P '#8367A9' 'M2,12a10,10 0 1,0 20,0a10,10 0 1,0 -20,0'),(P $purple 'M4,12a8,8 0 1,0 16,0a8,8 0 1,0 -16,0'),(P $ink 'M6,18V15H9Q5,8 12,5Q19,8 15,15H18V18H12V15Q17,9 12,8Q7,9 12,15V18Z'),(P $mint 'M10,10a2,2 0 1,0 4,0a2,2 0 1,0 -4,0'))

foreach ($key in @($icons.Keys | Sort-Object)) {
    $iconPaths = @($icons[$key] | ForEach-Object { Lit ([pscustomobject]$_) .13 24 })
    $assets.Add([ordered]@{name="flat_icon_$key";label="Icon: $key";w=24;h=24;paths=$iconPaths})
}
# Preserve resource names used by the current UI and painter aliases.
$aliases=@{
    flat_nav_shop='shop';flat_nav_hangar='hangar';flat_nav_quests='quests';flat_nav_stats='stats';flat_nav_settings='settings';flat_nav_prestige='prestige';flat_nav_achievements='achievements';flat_nav_route='route'
    ic_nav_shop_minimal='shop';ic_nav_hangar_minimal='hangar';ic_nav_quests_minimal='quests';ic_nav_stats_minimal='stats';ic_nav_settings_minimal='settings';ic_nav_prestige_minimal='prestige';ic_achievement_medal='achievements';ic_goal_route_minimal='route'
    ic_debris_minimal='debris';ic_drone_income_minimal='energy';ic_prestige_core='prestige';ic_action_launch='launch';ic_action_recall='recall';ic_action_sell='sell';ic_product_power_core='core';ic_product_luck_matrix='luck';ic_product_offline_ai='ai';ic_reset_progress='reset';ic_space_lock='lock';ic_telegram='telegram'
    ui_close_simple='close';ui_chevron_right_v2='chevron';ui_language_simple='language';ui_sound_simple='sound';ui_motion_simple='motion';ui_reset_simple='reset';ui_lock_simple='lock'
}
foreach ($alias in @($aliases.Keys | Sort-Object)) {
    $assets.Add([ordered]@{name=$alias;label="Icon: $alias";w=24;h=24;paths=@($icons[$aliases[$alias]] | ForEach-Object { Lit ([pscustomobject]$_) .13 24 })})
}
$brandPaths=@(
    (P '#425A98' 'M25,54a29,29 0 1,0 58,0a29,29 0 1,0 -58,0'),
    (P '#75BFE5' 'M27,51a26,26 0 1,0 52,0a26,26 0 1,0 -52,0'),
    (P '#82DFC4' 'M31,42C37,32 48,29 55,30L62,39Q60,46 50,45Q44,48 47,55Q45,65 37,60L35,51Z'),
    (P '#F7D397' 'M63,54Q78,49 78,62L71,73Q59,74 59,65Z'),
    (P '#A68AD5' 'M19,66C27,80 69,89 89,50L84,48C68,78 31,80 23,64Z'),
    (P '#E8E7F5' 'M75,32L86,30L92,38L82,44L73,40Z'),
    (P '#7DE4CD' 'M78,33H85L87,37L81,39L77,37Z'),
    (P '#FFCF8C' 'M74,34L65,35L72,40Z')
)
$assets.Add([ordered]@{name='ic_launcher_foreground';label='Drona Salvage: orbital salvage emblem';w=108;h=108;paths=@($brandPaths | ForEach-Object { Lit ([pscustomobject]$_) .23 108 })})
$assets.Add([ordered]@{name='ic_launcher_background';label='Launcher: deep space';w=108;h=108;paths=@((P '#18213C' 'M0,0H108V108H0Z'))})
$assets.Add([ordered]@{name='flat_icon_launcher_mono';label='Launcher: monochrome';w=108;h=108;paths=@(
    (P '#FFFFFF' 'M25,54a29,29 0 1,0 58,0a29,29 0 1,0 -58,0 M19,66C27,80 69,89 89,50L84,48C68,78 31,80 23,64Z M75,32L86,30L92,38L82,44L73,40Z')
)})
$json = ($assets.ToArray() | ConvertTo-Json -Depth 16) + "`n"
$planetColors = @($assets | Where-Object name -Like 'flat_planet_*' | Sort-Object { [int]($_.name -replace 'flat_planet_', '') } | ForEach-Object {
    $primary = (Blend $_.paths[1].c '#E5F4FF' .32).TrimStart('#')
    $secondary = (Blend $_.paths[4].c '#FFEBC9' .35).TrimStart('#')
    "        PlanetColors(Color(0xFF$primary), Color(0xFF$secondary)),"
})
$palette = @(
    'package com.example.myapplication.ui.theme', '',
    'import androidx.compose.ui.graphics.Color', '',
    '/** Generated from the authored planet materials by Build-ColorfulDesign.ps1. */',
    'data class PlanetColors(val primary: Color, val secondary: Color)', '',
    'object PlanetPalette {', '    private val colors = listOf(',
    ($planetColors -join "`n"), '    )', '',
    '    fun forPlanet(id: String): PlanetColors {',
    '        val index = id.removePrefix("p").toIntOrNull()?.minus(1) ?: 0',
    '        return colors[index.coerceIn(0, colors.lastIndex)]',
    '    }', '}', ''
) -join "`n"
$paletteTarget = Join-Path $root 'app/src/main/java/com/example/myapplication/ui/theme/PlanetPalette.kt'
if (Test-Path -LiteralPath (Join-Path $root 'docs/visual/painted-planets.json')) {
    & (Join-Path $PSScriptRoot 'Build-PaintedPlanetReview.ps1') -Check:$Check
} elseif ($Check) {
    if (!(Test-Path -LiteralPath $paletteTarget) -or [IO.File]::ReadAllText($paletteTarget).Replace("`r`n","`n") -ne $palette) { throw 'Planet palette is out of date' }
} else { [IO.File]::WriteAllText($paletteTarget, $palette, $utf8) }
$target=Join-Path $root 'docs/visual/colorful-design-pass.json'
if ($Check) {
    if (!(Test-Path -LiteralPath $target) -or [IO.File]::ReadAllText($target).Replace("`r`n","`n") -ne $json.Replace("`r`n","`n")) { throw 'Colorful design source is out of date' }
} else { [IO.File]::WriteAllText($target,$json,$utf8) }
Write-Output "Colorful design: 39 planets (50+ shades each), 29 drones, 24 case frames, $($backgrounds.Count) backgrounds, $($icons.Count) semantic icons and $($aliases.Count) aliases."
