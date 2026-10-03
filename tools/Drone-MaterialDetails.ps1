function Add-DroneMaterialDetails($asset) {
    $layers = [Collections.Generic.List[object]]::new()
    foreach ($part in $asset.paths) {
        $layers.Add($part)
        # Reflections are clipped to each authored part, including claws and wings.
        # Insert immediately after the part so foreground armour still occludes it.
        if ($part.d.Length -gt 65) {
            $layers.Add([ordered]@{c='#00000000';d=$part.d;stroke='#081528';width=7;opacity=.45;clip=$part.d})
            $layers.Add([ordered]@{c='#00000000';d=$part.d;stroke='#D3ECFF';width=2.6;opacity=.55;clip=$part.d})
            $layers.Add([ordered]@{c='#E8F8FF';d='M0,33L256,107V116L0,42Z';opacity=.22;clip=$part.d})
            $layers.Add([ordered]@{c='#E8F8FF';d='M0,49L256,123V126L0,52Z';opacity=.12;clip=$part.d})
            $layers.Add([ordered]@{c='#07152D';d='M0,180L256,151V256H0Z';opacity=.16;clip=$part.d})
            $layers.Add([ordered]@{c='#07152D';d='M82,144H107V147H82ZM149,144H174V147H149ZM86,151H104V154H86ZM152,151H170V154H152Z';opacity=.65;clip=$part.d})
            $layers.Add([ordered]@{c='#EAF8FF';d='M86,87h4v4h-4ZM166,87h4v4h-4ZM87,139h3v3h-3ZM166,139h3v3h-3Z';opacity=.85;clip=$part.d})
            $layers.Add([ordered]@{c='#081528';d='M92,157L108,163H148L164,157V160L148,166H108L92,160Z';opacity=.65;clip=$part.d})
        }
        $layers.Add([ordered]@{c='#00000000';d=$part.d;stroke='#609CCBDD';width=1.4})
    }
    $asset.paths = @($layers.ToArray())
}
