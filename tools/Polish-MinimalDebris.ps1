$ErrorActionPreference='Stop'
$root=Split-Path -Parent $PSScriptRoot
$utf8=[Text.UTF8Encoding]::new($false)
$android='http://schemas.android.com/apk/res/android'
$cards=[Collections.Generic.List[string]]::new()
$common=@(1,2,7,8,15,16,17,18,29,30,31,32,33,34)
$uncommon=@(3,9,10,19,20,21,35,36,37,38,39)
$rare=@(4,11,12,22,23,24,40,41,42,43)
$epic=@(5,13,25,26,44,45,46)
function Stroke([string]$d,[string]$color,[double]$width) {
 '<path android:fillColor="#00000000" android:strokeColor="'+$color+'" android:strokeWidth="'+$width+'" android:strokeLineCap="round" android:strokeLineJoin="round" android:pathData="'+$d+'"/>'
}
foreach($id in 1..48) {
 $suffix='{0:D2}' -f $id
 [xml]$source=[IO.File]::ReadAllText((Join-Path $root ('app/src/main/res/drawable/minimal_debris_'+$suffix+'_v7.xml')))
 $body=$source.vector.path[0].GetAttribute('pathData',$android)
 $detail=$source.vector.path[1].GetAttribute('pathData',$android)
 $accent=if($id -in $common){'#AEC8D8'}elseif($id -in $uncommon){'#8ED9B1'}elseif($id -in $rare){'#7DC9EE'}elseif($id -in $epic){'#BDA2E8'}else{'#E9C77F'}
 $fill=if($id -in $common){'#71889C'}elseif($id -in $uncommon){'#547F7D'}elseif($id -in $rare){'#526F94'}elseif($id -in $epic){'#71658C'}else{'#8B795C'}
 switch($id) {
  1 {$body='M31,21Q46,16 62,21L66,31L60,45L65,57L60,75Q46,81 32,75L27,61L32,46L27,33Z';$detail='M34,25Q47,21 59,25M35,33L54,36L43,48L56,57M34,70Q47,75 58,70'}
  2 {$body='M24,22H60L75,37L69,51L73,61L62,74H25L19,56L23,44L18,33Z';$detail='M30,32H54L64,42M29,61H55M53,49L60,54'}
  3 {$body='M25,29H66L75,38V65L66,74H25L18,65V38Z';$detail='M33,38H61V59H33ZM41,68H55'}
  9 {$body='M29,26Q48,16 67,26L75,43V61L63,75H33L21,61V43Z';$detail='M32,39H63V54H32ZM35,65H44M55,65H61'}
  10 {$body='M24,24H72V72H24Z';$detail='M37,36H58V57H37ZM29,43H37M58,49H67M44,57V66M53,29V36'}
  11 {$body='M24,20H72V76H24Z';$detail='M32,29H64V67H32ZM48,29V67M32,42H64M32,54H64'}
  16 {$body='M26,23H70Q74,23 74,27V69Q74,73 70,73H26Q22,73 22,69V27Q22,23 26,23Z';$detail='M32,34H64M32,44H64M32,54H64M32,64H64'}
  18 {$body='M27,25H69Q73,25 73,29V66Q73,71 68,71H28Q23,71 23,66V29Q23,25 27,25Z';$detail='M36,36H60V59H36ZM42,47H54'}
  25 {$body='M48,17L70,42L48,79L26,42Z';$detail='M48,28L59,43L48,66L37,43ZM37,43H59'}
  34 {$body='M48,22a25,25 0 1,0 0,50a25,25 0 1,0 0,-50 M48,35a12,12 0 1,1 0,24a12,12 0 1,1 0,-24';$detail='M35,29L32,34M61,59L58,64'}
  48 {$body='M48,19a29,29 0 1,0 0,58a29,29 0 1,0 0,-58 M48,34a14,14 0 1,1 0,28a14,14 0 1,1 0,-28';$detail='M35,28L29,35M67,58L60,67M28,59L35,67M61,28L68,36'}
 }
 $paths='<path android:fillColor="'+$fill+'" android:fillType="evenOdd" android:strokeColor="#182A3D" android:strokeWidth="3" android:strokeLineJoin="round" android:pathData="'+$body+'"/>'
 $paths+=(Stroke $detail '#25394E' 7)
 $paths+=(Stroke $detail $accent 3.5)
 $vector='<vector xmlns:android="'+$android+'" android:width="96dp" android:height="96dp" android:viewportWidth="96" android:viewportHeight="96">'+$paths+'</vector>'
 [IO.File]::WriteAllText((Join-Path $root ('app/src/main/res/drawable/debris_'+$suffix+'_v8.xml')),$vector,$utf8)
 $svg=$paths.Replace('android:','').Replace('pathData=','d=').Replace('fillColor=','fill=').Replace('strokeColor=','stroke=').Replace('strokeWidth=','stroke-width=').Replace('strokeLineCap=','stroke-linecap=').Replace('strokeLineJoin=','stroke-linejoin=').Replace('fillType="evenOdd"','fill-rule="evenodd"').Replace('#00000000','none')
 $cards.Add('<figure><svg viewBox="0 0 96 96">'+$svg+'</svg><figcaption>'+ $suffix +'</figcaption></figure>')
}
$html='<!doctype html><html lang="en"><meta charset="utf-8"><title>Minimal debris</title><style>body{background:#101b2f;color:#abc2d4;font:14px system-ui;margin:24px}main{display:grid;grid-template-columns:repeat(8,110px);gap:12px}figure{margin:0;background:#17283d;border-radius:14px;padding:8px;text-align:center}svg{width:88px;height:88px}</style><h1>48 minimal salvage icons</h1><main>'+($cards -join '')+'</main></html>'
[IO.File]::WriteAllText((Join-Path $root 'docs/minimal-debris-preview.html'),$html,$utf8)
