$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$utf8 = [Text.UTF8Encoding]::new($false)
$languages = @(
    @{Dir='values';Version='Version 10 · 13 October 2026';Heading='Drone fusion';Section='Drone fusion\nFusion spends two healthy reserve drones of the same rarity. The success chance is shown before confirmation. Both drones are consumed; success adds one random drone of the target rarity and failure gives no reward. Fusion does not involve real-money purchases, cash prizes or a required advertising view. The resulting fleet inventory, drone discoveries and quest progress are stored in the existing local save. No separate history of fusion attempts is saved. The selected pair and animation state exist only in the current workshop window; closing it does not undo an already confirmed outcome. Selection and fusion do not themselves initiate advertising requests or send the pair or outcome to the developer. The animation is drawn locally, with reduced-motion preferences applied. The backup and deletion rules described above also apply to the updated inventory.'},
    @{Dir='values-ru';Version='Версия 10 · 13 октября 2026 года';Heading='Слияние дронов';Section='Слияние дронов\nДля слияния расходуются два исправных дрона одной редкости из резерва. Шанс успеха показан до подтверждения. Оба дрона исчезают: при успехе добавляется один случайный дрон указанной редкости, при неудаче награды нет. Слияние не предусматривает покупки за реальные деньги, денежные призы или обязательный просмотр рекламы. Итоговый состав флота, открытия дронов и прогресс заданий записываются в существующее локальное сохранение. Отдельная история попыток слияния не сохраняется. Выбранная пара и состояние анимации существуют только в текущем окне мастерской; его закрытие не отменяет уже подтверждённый результат. Выбор пары и слияние сами по себе не инициируют рекламные запросы и не отправляют выбранных дронов или результат разработчику. Анимация рисуется локально с учётом настройки уменьшения движения. Описанные выше правила резервного копирования и удаления также относятся к изменённому составу флота.'},
    @{Dir='values-es';Version='Versión 10 · 13 de octubre de 2026';Heading='Fusión de drones';Section='Fusión de drones\nLa fusión consume dos drones sanos de reserva de la misma rareza. La probabilidad de éxito se muestra antes de confirmar. Ambos drones se consumen: el éxito añade un dron aleatorio de la rareza indicada y el fallo no da recompensa. No hay compras con dinero real, premios en efectivo ni anuncios obligatorios para fusionar. La flota resultante, los descubrimientos de drones y el progreso de misiones se guardan en la partida local existente. No se guarda un historial separado de intentos de fusión. La pareja elegida y el estado de la animación solo existen en la ventana actual del taller; cerrarla no deshace un resultado ya confirmado. Elegir o fusionar drones no inicia por sí mismo solicitudes publicitarias ni envía la pareja o el resultado al desarrollador. La animación se dibuja localmente y respeta el ajuste de movimiento reducido. Las reglas de copia de seguridad y eliminación anteriores también se aplican a la flota actualizada.'}
)
foreach ($language in $languages) {
    $path = Join-Path $root "app/src/main/res/$($language.Dir)/privacy.xml"
    $source = [IO.File]::ReadAllText($path)
    $match = [regex]::Match($source, '<string name="privacy_policy_body">(.*?)</string>', 'Singleline')
    if (!$match.Success) { throw "Missing policy: $path" }
    $body = [regex]::Replace($match.Groups[1].Value, '(Version|Версия|Versión) \d+ · [^\\]+', $language.Version)
    $body = switch ($language.Dir) {
        'values' { $body.Replace('ad interactions and diagnostic data', 'interactions with the app and ads (such as app launches, taps and video views), and diagnostic data') }
        'values-ru' { $body.Replace('взаимодействия с объявлениями и диагностические сведения', 'взаимодействия с приложением и объявлениями (например, запуски приложения, нажатия и просмотры видео), а также диагностические сведения') }
        'values-es' { $body.Replace('interacciones con anuncios y datos de diagnóstico', 'interacciones con la aplicación y los anuncios (como inicios de la aplicación, pulsaciones y vistas de vídeos), y datos de diagnóstico') }
    }
    $sectionPattern = '\\n\\n' + [regex]::Escape($language.Heading) + '\\n.*?(?=\\n\\n|$)'
    $body = [regex]::Replace($body, $sectionPattern, '') + '\n\n' + $language.Section
    $source = $source.Substring(0, $match.Groups[1].Index) + $body + $source.Substring($match.Groups[1].Index + $match.Groups[1].Length)
    [IO.File]::WriteAllText($path, $source, $utf8)
}
& (Join-Path $PSScriptRoot 'Export-PrivacyPolicy.ps1')
