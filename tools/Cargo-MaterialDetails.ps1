function Add-CargoMaterialDetails($asset) {
    $layers = [Collections.Generic.List[object]]::new()
    $accent = if ($asset.name -like '*legendary*') { '#FFD88C' } elseif ($asset.name -like '*rare*') { '#C0ADFF' } else { '#83F3E1' }
    foreach ($part in $asset.paths) {
        $layers.Add($part)
        if ($part.d.Length -gt 65) {
            $layers.Add([ordered]@{c='#EAF5FF';d='M20,80L230,140V145L20,85Z';opacity=.24;clip=$part.d})
            $layers.Add([ordered]@{c='#08142D';d='M20,175L230,155V230H20Z';opacity=.25;clip=$part.d})
            $layers.Add([ordered]@{c='#08142D';d='M30,164H65V167H30ZM72,164H107V167H72ZM149,164H184V167H149ZM191,164H226V167H191ZM34,172H61V175H34ZM76,172H103V175H76ZM153,172H180V175H153ZM195,172H222V175H195Z';opacity=.65;clip=$part.d})
            $layers.Add([ordered]@{c=$accent;d='M30,179H107V182H30ZM149,179H226V182H149Z';opacity=.85;clip=$part.d})
            $layers.Add([ordered]@{c='#EAF5FF';d='M37,151h3v3h-3ZM77,151h3v3h-3ZM176,151h3v3h-3ZM216,151h3v3h-3Z';opacity=.85;clip=$part.d})
        }
        $layers.Add([ordered]@{c='#00000000';d=$part.d;stroke='#90BDD8';width=1.2;opacity=.6})
    }
    $asset.paths = @($layers.ToArray())
}
