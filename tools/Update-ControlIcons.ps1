$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$utf8 = [Text.UTF8Encoding]::new($false)
$namespace = 'http://schemas.android.com/apk/res/android'
foreach ($file in Get-ChildItem (Join-Path $root 'app/src/main/res/drawable/ui_refined_*.xml')) {
    $source = [IO.File]::ReadAllText($file.FullName)
    if ($source.Contains('<!-- control-material-v2 -->')) { continue }
    $xml = [xml]$source
    $accent = '#9ADCF1'
    foreach ($path in $xml.vector.path) {
        $color = $path.GetAttribute('strokeColor', $namespace)
        if ($color -match '^#[0-9A-Fa-f]{6}$') { $accent = $color; break }
    }
    $source = $source.Replace('<vector xmlns:android=', '<vector xmlns:aapt="http://schemas.android.com/aapt" xmlns:android=')
    $opening = $source.IndexOf('>') + 1
    $plate = @"

    <!-- control-material-v2 -->
    <path android:pathData="M4,0.7H20L23.3,4V20L20,23.3H4L0.7,20V4Z" android:strokeColor="$accent" android:strokeAlpha="0.38" android:strokeWidth="0.7">
        <aapt:attr name="android:fillColor"><gradient android:startX="2" android:startY="1" android:endX="22" android:endY="24" android:type="linear">
            <item android:color="#E02B405D" android:offset="0"/><item android:color="#E016243C" android:offset="0.45"/><item android:color="#F0091225" android:offset="1"/>
        </gradient></aapt:attr>
    </path>
    <path android:fillColor="#00000000" android:strokeColor="#D8EEFF" android:strokeAlpha="0.3" android:strokeWidth="0.6" android:pathData="M4,1.6H19.6L22.4,4.4M1.6,4.4V10"/>
    <path android:fillColor="$accent" android:fillAlpha="0.8" android:pathData="M4,21.6H7V22.2H4ZM17,21.6H20V22.2H17Z"/>
"@
    # Keep the actual symbol intact and inset it slightly into the chamfered plate.
    $symbols = $source.Substring($opening).Replace('</vector>', '</group></vector>')
    $source = $source.Substring(0,$opening) + $plate + '<group android:pivotX="12" android:pivotY="12" android:scaleX="0.82" android:scaleY="0.82">' + $symbols
    [IO.File]::WriteAllText($file.FullName, $source, $utf8)
}
Write-Output 'Updated 14 vector control icons with metallic panels and accent lights.'
