$ErrorActionPreference='Stop'
$root=Split-Path -Parent $PSScriptRoot
$utf8=[Text.UTF8Encoding]::new($false)
Get-ChildItem (Join-Path $root 'app/src/main/res/drawable/debris_*_v8.xml') | ForEach-Object {
 $text=[IO.File]::ReadAllText($_.FullName)
 $text=[regex]::Replace($text,'<aapt:attr name="android:fillColor">.*?</aapt:attr>','')
 $text=$text.Replace('<path android:strokeColor="#15283E"','<path android:fillColor="#A4B9C9" android:strokeColor="#15283E"')
 $text=[regex]::Replace($text,'<path android:fillColor="#FFFFFF" android:fillAlpha="0.48".*?</vector>','</vector>')
 $text=$text.Replace('xmlns:aapt="http://schemas.android.com/aapt" ','')
 [IO.File]::WriteAllText($_.FullName,$text,$utf8)
}
