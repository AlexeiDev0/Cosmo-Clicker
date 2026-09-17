# Authored vector layers: deterministic materials, relief and spherical lighting.
function Add-PlanetSurfaceDetails($planet) {
    $number = [int]($planet.name -replace 'flat_planet_', '')
    $rng = [Random]::new(7409 + $number * 131)
    $base = $planet.paths[1].c
    $accent = $planet.paths[4].c
    $mask = $planet.paths[1].d
    $layers = [Collections.Generic.List[object]]::new()
    function N([double]$value) { $value.ToString('0.###', [Globalization.CultureInfo]::InvariantCulture) }
    function Detail([string]$color, [string]$shape, [double]$alpha) {
        $layers.Add([ordered]@{c=$color;d=$shape;opacity=$alpha;clip=$mask})
    }
    # Small irregular facets follow the sphere, rather than a uniform dot grid.
    for ($i = 0; $i -lt 110; $i++) {
        $x = 28 + $rng.NextDouble() * 200
        $y = 28 + $rng.NextDouble() * 200
        $s = 1.2 + $rng.NextDouble() * 4.6
        $r = 1.2 + $rng.NextDouble() * 3.2
        $shape = 'M' + (N $x) + ',' + (N $y) + 'q' + (N $s) + ',' + (N (-$r)) + ' ' + (N ($s*2)) + ',0l' + (N (-$s*.4)) + ',' + (N $r) + 'q' + (N (-$s)) + ',' + (N ($r*.7)) + ' ' + (N (-$s*1.6)) + ',0Z'
        $color = if ($i % 3 -eq 0) { '#F7E6C7' } elseif ($i % 3 -eq 1) { '#122344' } else { $accent }
        Detail $color $shape (.07 + $rng.NextDouble() * .16)
    }
    $rocky = $number -in @(2,5,9,10,12,19,25,28,30,37)
    $cloudy = $number -in @(1,3,6,7,8,13,14,15,16,17,20,22,24,26,27,29,31,32,33,36)
    if ($rocky) {
        # Crater bowls have a recessed floor, an uneven rim and a sunlit lip.
        for ($i = 0; $i -lt 13; $i++) {
            $x = 53 + $rng.NextDouble() * 153; $y = 52 + $rng.NextDouble() * 150
            $r = 3 + $rng.NextDouble() * 9; $d = $r*2
            $circle = 'M' + (N ($x-$r)) + ',' + (N $y) + 'a' + (N $r) + ',' + (N ($r*.73)) + ' 0 1,0 ' + (N $d) + ',0a' + (N $r) + ',' + (N ($r*.73)) + ' 0 1,0 ' + (N (-$d)) + ',0'
            Detail '#13203C' $circle .24
            $lip = 'M' + (N ($x-$r)) + ',' + (N $y) + 'q' + (N $r) + ',' + (N (-$r*1.3)) + ' ' + (N $d) + ',0q' + (N (-$r)) + ',' + (N (-$r*.8)) + ' ' + (N (-$d)) + ',0Z'
            Detail '#FFE6BB' $lip .42
        }
        for ($i = 0; $i -lt 12; $i++) {
            $x=40+$rng.NextDouble()*165; $y=75+$rng.NextDouble()*125; $s=4+$rng.NextDouble()*10
            Detail (Blend $base '#F6D09D' .48) ('M'+(N $x)+','+(N $y)+'l'+(N $s)+','+(N (-$s*.8))+' '+(N ($s*.7))+','+(N ($s*.9))+'Z') .33
            Detail '#1C2748' ('M'+(N ($x+$s))+','+(N ($y-$s*.8))+'l'+(N ($s*.7))+','+(N ($s*.9))+' '+(N (-$s*.6))+','+(N ($s*.2))+'Z') .27
        }
    } elseif (!$cloudy) {
        # Engineered / crystalline worlds get etched plates and small luminous seams.
        for ($i = 0; $i -lt 18; $i++) {
            $x=43+$rng.NextDouble()*155; $y=48+$rng.NextDouble()*153; $s=5+$rng.NextDouble()*10
            Detail '#172642' ('M'+(N $x)+','+(N $y)+'h'+(N $s)+'v1.2h'+(N (-$s))+'Z') .38
            Detail (Blend $accent '#BAFFF0' .55) ('M'+(N $x)+','+(N ($y+2))+'h'+(N ($s*.65))+'v.8h'+(N (-$s*.65))+'Z') .47
        }
    }
    if ($cloudy) {
        # Curved translucent layers let continents / vortex motifs remain visible.
        for ($i = 0; $i -lt 16; $i++) {
            $x = 24 + $rng.NextDouble()*145; $y = 40 + $rng.NextDouble()*173
            $s = 13 + $rng.NextDouble()*39; $rise = 3 + $rng.NextDouble()*7
            $shape='M'+(N $x)+','+(N $y)+'q'+(N ($s*.3))+','+(N (-$rise))+' '+(N ($s*.6))+',0t'+(N ($s*.6))+',0q'+(N (-$s*.3))+','+(N ($rise*.7))+' '+(N (-$s*.6))+',2t'+(N (-$s*.6))+',-2Z'
            Detail (Blend $base '#ECFFF9' .85) $shape (.13+$rng.NextDouble()*.18)
        }
    }
    # A continuous terminator models the whole sphere, including all added surface detail.
    $layers.Add([ordered]@{
        c=$base;d='M0,0H256V256H0Z';clip=$mask
        gradient=@{type='radial';cx=83;cy=73;radius=193;stops=@(
            @{at=0;c='#42FFF4DB'},@{at=.3;c='#12F4FFE8'},@{at=.48;c='#00112540'},
            @{at=.65;c='#18101C39'},@{at=.82;c='#65101A36'},@{at=1;c='#CD080F25'}
        )}
    })
    # Thin atmospheric rim and a soft reflected edge, not an opaque outline.
    $layers.Add([ordered]@{
        c=$base;d='M0,0H256V256H0Z';clip=$mask
        gradient=@{type='radial';cx=128;cy=128;radius=102;stops=@(
            @{at=0;c='#00B1F4FF'},@{at=.86;c='#00B1F4FF'},@{at=.93;c='#24B1F4FF'},
            @{at=.975;c='#8AC7F6FF'},@{at=1;c='#13A5DBFF'}
        )}
    })
    $planet.paths = @($planet.paths) + @($layers.ToArray())
}
