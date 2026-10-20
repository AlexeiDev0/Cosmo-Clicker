$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
$utf8 = [Text.UTF8Encoding]::new($false)
$names = @'
{"ru":["\u041e\u0431\u043b\u043e\u043c\u043e\u043a \u0442\u0435\u043f\u043b\u043e\u0437\u0430\u0449\u0438\u0442\u044b","\u0421\u043b\u043e\u043c\u0430\u043d\u043d\u044b\u0439 \u043d\u0430\u0432\u0438\u0433\u0430\u0442\u043e\u0440","\u041a\u0440\u0435\u043f\u0451\u0436\u043d\u0430\u044f \u0441\u043a\u043e\u0431\u0430","\u0421\u0435\u0440\u0432\u0438\u0441\u043d\u044b\u0439 \u043b\u044e\u043a","\u0421\u0442\u044b\u043a\u043e\u0432\u043e\u0447\u043d\u044b\u0439 \u0437\u0430\u0436\u0438\u043c","\u0423\u043f\u043b\u043e\u0442\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0435 \u043a\u043e\u043b\u044c\u0446\u043e","\u041c\u043e\u0434\u0443\u043b\u044c \u0441\u0432\u044f\u0437\u0438","\u0411\u043e\u0440\u0442\u043e\u0432\u043e\u0439 \u0434\u0438\u0441\u043f\u043b\u0435\u0439","\u041e\u043f\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u043e\u0442\u0440\u0430\u0436\u0430\u0442\u0435\u043b\u044c","\u041f\u043b\u0430\u0442\u0430 \u0443\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u044f","\u0420\u0435\u043b\u0435 \u043f\u0438\u0442\u0430\u043d\u0438\u044f","\u041b\u0438\u043d\u0437\u0430 \u0441\u043a\u0430\u043d\u0435\u0440\u0430","\u041f\u043b\u0430\u0437\u043c\u0435\u043d\u043d\u044b\u0439 \u0438\u043d\u0436\u0435\u043a\u0442\u043e\u0440","\u042f\u0447\u0435\u0439\u043a\u0430 \u0449\u0438\u0442\u0430","\u041d\u0430\u0432\u0438\u0433\u0430\u0446\u0438\u043e\u043d\u043d\u044b\u0439 \u0447\u0438\u043f","\u041e\u0441\u043a\u043e\u043b\u043e\u043a \u0433\u0438\u043f\u0435\u0440\u043a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u0430","\u041a\u0443\u0431 \u043f\u0430\u043c\u044f\u0442\u0438","\u0424\u0430\u0437\u043e\u0432\u044b\u0439 \u0440\u0435\u0437\u043e\u043d\u0430\u0442\u043e\u0440","\u042f\u0434\u0440\u043e \u0441\u0432\u0435\u0440\u0445\u043d\u043e\u0432\u043e\u0439","\u0413\u0440\u0430\u0432\u0438\u0442\u0430\u0446\u0438\u043e\u043d\u043d\u043e\u0435 \u043a\u043e\u043b\u044c\u0446\u043e"],"en":["Heat shield fragment","Broken navigator","Mounting bracket","Service hatch","Docking clip","Sealing ring","Communications module","Ship display","Optical reflector","Control board","Power relay","Scanner lens","Plasma injector","Shield cell","Navigation chip","Hypercrystal shard","Memory cube","Phase resonator","Supernova core","Gravity ring"],"es":["Fragmento de escudo t\u00e9rmico","Navegador roto","Soporte de montaje","Escotilla de servicio","Abrazadera de acoplamiento","Anillo de sellado","M\u00f3dulo de comunicaciones","Pantalla de nave","Reflector \u00f3ptico","Placa de control","Rel\u00e9 de alimentaci\u00f3n","Lente de esc\u00e1ner","Inyector de plasma","Celda de escudo","Chip de navegaci\u00f3n","Fragmento de hipercristal","Cubo de memoria","Resonador de fase","N\u00facleo de supernova","Anillo gravitatorio"]}
'@ | ConvertFrom-Json
foreach ($locale in @(@{Dir='values';Key='en'},@{Dir='values-ru';Key='ru'},@{Dir='values-es';Key='es'})) {
 $path = Join-Path $root ('app/src/main/res/' + $locale.Dir + '/debris_collection.xml')
 $content = [IO.File]::ReadAllText($path)
 $entries = [Collections.Generic.List[string]]::new()
 for ($i=0; $i -lt 20; $i++) {
  $id = ($i+29).ToString('D2')
  $content = [regex]::Replace($content, '\s*<string name="debris_name_' + $id + '">.*?</string>', '')
  if (-not $content.Contains('name="debris_name_' + $id + '"')) {
   $entries.Add('    <string name="debris_name_' + $id + '">' + [Security.SecurityElement]::Escape($names.($locale.Key)[$i]) + '</string>')
  }
 }
 $content = $content.Replace('</resources>', ($entries -join "`n") + "`n</resources>")
 [IO.File]::WriteAllText($path,$content,$utf8)
}

