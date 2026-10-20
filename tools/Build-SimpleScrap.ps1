$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$utf8 = [Text.UTF8Encoding]::new($false)
$shapes = @{
 1=@('M30,22L62,22L67,34L59,45L65,61L61,74L33,74L27,61L34,45L28,33Z','M35,29L57,29M35,67L57,67M39,40L51,47L43,55')
 2=@('M24,24L61,21L76,38L67,54L71,69L31,76L20,59L26,44Z','M32,34L55,31L63,39M33,62L56,59')
 7=@('M36,17L60,17L69,30L60,43L54,43L54,77L42,77L42,43L36,43L27,30Z','M38,30L58,30M42,53L54,49M42,63L54,59M42,73L54,69')
 8=@('M30,20L65,20L80,48L65,76L30,76L15,48Z M48,34A14,14 0,1 0,48,62A14,14 0,1 0,48,34Z','M34,27L60,27M24,46L33,65')
 15=@('M24,25Q54,12 72,35Q79,55 58,64L39,71M25,39Q45,29 58,39Q67,50 50,53L30,58M21,52L29,63')
 16=@('M48,17A31,31 0,1 0,48,79A31,31 0,1 0,48,17Z M48,34A14,14 0,1 0,48,62A14,14 0,1 0,48,34Z','M28,33Q34,25 45,25')
 17=@('M22,69L22,36Q22,21 38,21L74,21L74,37L42,37Q38,37 38,42L38,69Z','M24,62L36,62M65,23L65,35')
 18=@('M27,19L69,19L69,36L55,36L55,66L48,80L41,66L41,36L27,36Z','M36,27L60,27M41,46L55,42M41,56L55,52M42,65L54,61')
 29=@('M19,37L37,22L54,31L71,26L76,56L58,73L39,66L24,74Z','M30,39L38,31L51,41M43,59L64,50')
 30=@('M39,17L57,17L60,27L69,31L79,28L85,44L77,50L76,60L82,68L68,80L60,74L50,77L44,86L28,79L29,68L23,60L13,57L16,40L27,38L34,30Z M49,36A14,14 0,1 0,49,64A14,14 0,1 0,49,36Z')
 31=@('M22,22L42,22L42,55L76,55L76,75L22,75Z M31,30A4,4 0,1 0,31,38A4,4 0,1 0,31,30Z M65,61A4,4 0,1 0,65,69A4,4 0,1 0,65,61Z')
 32=@('M23,26L69,20L78,64L61,75L20,69Z M31,33A4,4 0,1 0,31,41A4,4 0,1 0,31,33Z M63,55A4,4 0,1 0,63,63A4,4 0,1 0,63,55Z','M39,47L60,43')
 33=@('M21,23L75,23L75,41L59,41L59,70L37,70L37,41L21,41Z','M30,32L66,32M44,59L52,59')
 34=@('M48,18A30,30 0,1 0,48,78A30,30 0,1 0,48,18Z M48,29A19,19 0,1 0,48,67A19,19 0,1 0,48,29Z')
}
$names = @{
 7=@('Loose bolt','Крепёжный болт','Perno suelto')
 8=@('Hex nut','Шестигранная гайка','Tuerca hexagonal')
 16=@('Metal washer','Металлическая шайба','Arandela metálica')
 17=@('Bent pipe','Гнутая труба','Tubo doblado')
 18=@('Old screw','Старый винт','Tornillo viejo')
 29=@('Metal offcut','Обрезок металла','Recorte de metal')
 30=@('Broken gear','Сломанная шестерёнка','Engranaje roto')
}
$previews = [Collections.Generic.List[string]]::new()
foreach ($number in 1..48) {
 $path = Join-Path $root ('app/src/main/res/drawable/debris_{0:00}_v8.xml' -f $number)
 if ($shapes.ContainsKey($number)) { $parts = $shapes[$number] }
 else {
  [xml]$xml = [IO.File]::ReadAllText($path)
  $nodes = @($xml.vector.path)
  $parts = @($nodes[0].GetAttribute('android:pathData'))
  if ($nodes.Count -gt 1) { $parts += $nodes[-1].GetAttribute('android:pathData') }
 }
 if ($number -in @(1,2,7,8,15,16,17,18,29,30,31,32,33,34)) { $body='#A7BAC6'; $detail='#D8E4E9' }
 elseif ($number -in @(3,9,10,19,20,21,35,36,37,38,39)) { $body='#76BCAF'; $detail='#BDEBE0' }
 elseif ($number -in @(4,11,12,22,23,24,40,41,42,43)) { $body='#72B9DE'; $detail='#C8EDFF' }
 elseif ($number -in @(5,13,25,26,44,45,46)) { $body='#B79ADA'; $detail='#E6D6FF' }
 else { $body='#DBB86D'; $detail='#FFF0B6' }
 $vectors = [Collections.Generic.List[string]]::new()
 $svg = [Collections.Generic.List[string]]::new()
 for ($i=0; $i -lt $parts.Count; $i++) {
  $fill='#00000000'; $svgFill='none'; $stroke=$detail; $width=3
  if ($i -eq 0 -and $number -ne 15) { $fill=$body; $svgFill=$body; $stroke='#142A3C' }
  if ($number -eq 15) { $stroke=$body; $width=9 }
  $vectors.Add('<path android:fillColor="'+$fill+'" android:fillType="evenOdd" android:strokeColor="'+$stroke+'" android:strokeWidth="'+$width+'" android:strokeLineCap="round" android:strokeLineJoin="round" android:pathData="'+$parts[$i]+'"/>')
  $svg.Add('<path d="'+$parts[$i]+'" fill="'+$svgFill+'" fill-rule="evenodd" stroke="'+$stroke+'" stroke-width="'+$width+'" stroke-linecap="round" stroke-linejoin="round"/>')
 }
 $vector='<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="96dp" android:height="96dp" android:viewportWidth="96" android:viewportHeight="96">'+($vectors -join '')+'</vector>'
 [IO.File]::WriteAllText($path,$vector+"`n",$utf8)
 $x=(($number-1)%8)*116+10; $y=[math]::Floor(($number-1)/8)*120+4
 $previews.Add('<g transform="translate('+$x+','+$y+')">'+($svg -join '')+'<text x="48" y="108" text-anchor="middle" fill="#BDCDDB" font-family="sans-serif" font-size="12">'+$number+'</text></g>')
}
$preview='<svg xmlns="http://www.w3.org/2000/svg" width="928" height="720" viewBox="0 0 928 720"><rect width="928" height="720" fill="#091525"/>'+($previews -join '')+'</svg>'
[IO.File]::WriteAllText((Join-Path $root 'docs/simple-scrap-preview.svg'),$preview,$utf8)
[IO.File]::WriteAllText((Join-Path $root 'docs/simple-scrap-preview.html'),'<html><head><meta charset="utf-8"></head><body style="margin:0;background:#091525">'+$preview+'</body></html>',$utf8)
$folders=@('values','values-ru','values-es')
for ($language=0; $language -lt 3; $language++) {
 $path=Join-Path $root ('app/src/main/res/'+$folders[$language]+'/debris_collection.xml')
 $text=[IO.File]::ReadAllText($path)
 foreach ($number in $names.Keys) {
  $pattern='(<string name="debris_name_{0:00}">).*?(</string>)' -f $number
  $text=[regex]::Replace($text,$pattern,('${1}'+[Net.WebUtility]::HtmlEncode($names[$number][$language])+'${2}'))
 }
 [IO.File]::WriteAllText($path,$text,$utf8)
}
Write-Output 'Updated 48 flat scrap vectors; preview: docs/simple-scrap-preview.svg'
