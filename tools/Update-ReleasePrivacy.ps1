$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$utf8 = [Text.UTF8Encoding]::new($false)
$sections = @{
    'values' = 'Optional advertising requests\nThe game does not automatically preload rewarded ads. Before the first advertising request in each app session, a separate notice explains the data processed by Google and offers Allow and load ad or Continue without an ad. Declining or dismissing the notice sends no advertising request from the game and does not affect ordinary rewards. UMP is accessed only after allowing requests. Permission is not saved across app restarts or Activity recreation. Settings lets you stop new advertising requests; this cannot undo an in-flight request, stop all processing inside an already initialized Google SDK, or delete data already sent to Google. Accepting the game privacy policy does not allow advertising requests. The advertising ID and Privacy Sandbox advertising permissions are explicitly removed from the app manifest; other available SDK identifiers may still be processed. Rewarded bonuses are not cash, and cases use only earned game debris. There are no real-money purchases.'
    'values-ru' = 'Добровольные рекламные запросы\nИгра не предзагружает рекламу автоматически. Перед первым рекламным запросом в каждой сессии отдельное уведомление объясняет обработку данных Google и предлагает разрешить загрузку или продолжить без рекламы. Отказ или закрытие уведомления не отправляет рекламный запрос из игры и не влияет на обычную награду. Обращение к UMP начинается только после разрешения запросов. Разрешение не сохраняется после перезапуска приложения или пересоздания Activity. В настройках можно остановить новые рекламные запросы; это не отменяет запрос в процессе выполнения, не останавливает всю внутреннюю обработку уже инициализированного SDK Google и не удаляет уже отправленные Google данные. Принятие политики игры не разрешает рекламные запросы. Доступ к рекламному идентификатору и рекламные разрешения Privacy Sandbox явно исключены из манифеста; другие доступные идентификаторы SDK могут обрабатываться. Рекламные бонусы не являются деньгами; кейсы используют только заработанный игровой мусор. Покупок за реальные деньги нет.'
    'values-es' = 'Solicitudes publicitarias opcionales\nEl juego no precarga anuncios automáticamente. Antes de la primera solicitud publicitaria de cada sesión, un aviso separado explica los datos tratados por Google y permite autorizar la carga o continuar sin anuncios. Rechazar o cerrar el aviso no envía ninguna solicitud publicitaria desde el juego ni afecta a la recompensa normal. Solo se accede a UMP después de permitir las solicitudes. El permiso no se conserva al reiniciar la aplicación o recrear la Activity. En Ajustes puedes detener nuevas solicitudes; esto no cancela una solicitud en curso, no detiene todo el tratamiento interno de un SDK de Google ya inicializado ni elimina datos enviados a Google. Aceptar la política del juego no permite solicitudes publicitarias. Los permisos del identificador publicitario y de publicidad de Privacy Sandbox se excluyen explícitamente del manifiesto; otros identificadores disponibles del SDK pueden seguir tratándose. Los bonos publicitarios no son dinero y las cajas solo usan residuos ganados en el juego. No hay compras con dinero real.'
}
foreach ($dir in $sections.Keys) {
    $file = Join-Path $root "app/src/main/res/$dir/privacy.xml"
    $source = [IO.File]::ReadAllText($file)
    $match = [regex]::Match($source, '<string name="privacy_policy_body">(.*?)</string>', 'Singleline')
    if (!$match.Success) { throw "Missing policy: $dir" }
    $body = $match.Groups[1].Value
    $heading = $sections[$dir].Split('\')[0]
    if (!$body.Contains($heading)) {
        $newBody = $body + '\n\n' + $sections[$dir]
        $source = $source.Substring(0, $match.Groups[1].Index) + $newBody + $source.Substring($match.Groups[1].Index + $match.Groups[1].Length)
        [IO.File]::WriteAllText($file, $source, $utf8)
    }
}
& (Join-Path $PSScriptRoot 'Export-PrivacyPolicy.ps1')
