$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$utf8 = [Text.UTF8Encoding]::new($false)
$languages = @(
    @{Dir='values';Id='en';Version='Version 3 · 3 October 2026';Extra='Security and support\nProgress and settings are stored in the private Android app storage. The game does not provide additional encryption of these files; device security and system backups depend on Android and your settings. All music and sound effects are bundled with the app and are played locally without contacting asset websites. If you contact the developer through Telegram, the recipient can see the profile and messages that you share. Support messages are used to answer your request; request their deletion through the same contact. Telegram handles its own copies under its privacy policy. Do not send passwords, payment details or sensitive personal information.'},
    @{Dir='values-ru';Id='ru';Version='Версия 3 · 3 октября 2026 года';Extra='Защита данных и поддержка\nПрогресс и настройки хранятся в закрытом хранилище приложения Android. Игра не выполняет дополнительное шифрование этих файлов; защита устройства и системных резервных копий зависит от Android и ваших настроек. Музыка и звуковые эффекты включены в приложение и воспроизводятся локально без обращения к сайтам авторов. При обращении через Telegram получатель видит профиль и сообщения, которыми вы делитесь. Сообщения поддержки используются для ответа на обращение; запросить их удаление можно через тот же контакт. Telegram обрабатывает собственные копии по своей политике. Не отправляйте пароли, платёжные данные или чувствительные персональные сведения.'},
    @{Dir='values-es';Id='es';Version='Versión 3 · 3 de octubre de 2026';Extra='Seguridad y soporte\nEl progreso y los ajustes se guardan en el almacenamiento privado de Android. El juego no añade cifrado a estos archivos; la protección del dispositivo y de las copias depende de Android y tus ajustes. La música y los efectos están incluidos en la aplicación y se reproducen localmente sin acceder a las páginas de sus autores. Si contactas por Telegram, el destinatario puede ver el perfil y los mensajes que compartas. Los mensajes de soporte se utilizan para responder; puedes solicitar su eliminación mediante el mismo contacto. Telegram trata sus propias copias conforme a su política. No envíes contraseñas, datos de pago ni información personal sensible.'}
)
$articles = [Collections.Generic.List[string]]::new()
foreach ($language in $languages) {
    $path = Join-Path $root "app/src/main/res/$($language.Dir)/privacy.xml"
    $source = [IO.File]::ReadAllText($path)
    $match = [regex]::Match($source, '<string name="privacy_policy_body">(.*?)</string>', 'Singleline')
    if (!$match.Success) { throw "Missing policy: $path" }
    $body = $match.Groups[1].Value
    $body = [regex]::Replace($body, '(Version|Версия|Versión) [23] · [^\\]+', $language.Version)
    $heading = $language.Extra.Substring(0, $language.Extra.IndexOf('\n'))
    if (!$body.Contains($heading)) { $body += '\n\n' + $language.Extra }
    $source = $source.Substring(0,$match.Groups[1].Index) + $body + $source.Substring($match.Groups[1].Index + $match.Groups[1].Length)
    [IO.File]::WriteAllText($path, $source, $utf8)
    $plain = [System.Net.WebUtility]::HtmlDecode($body).Replace('\n', "`n")
    [IO.File]::WriteAllText((Join-Path $root "docs/privacy-policy-$($language.Id).txt"), $plain + "`n", $utf8)
    $escaped = [System.Net.WebUtility]::HtmlEncode($plain)
    $articles.Add('<article id="' + $language.Id + '" lang="' + $language.Id + '">' + $escaped + '</article>')
}
$html = @'
<!doctype html>
<html lang="ru"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Drona Salvage — Политика конфиденциальности / Privacy policy</title><style>body{font:16px/1.65 system-ui;max-width:850px;margin:40px auto;padding:0 20px;color:#182b3e}article{white-space:pre-line;margin:40px 0}a{color:#145c85}@media print{nav{display:none}article{break-before:page}}</style></head><body><nav><a href="#ru">Русский</a> · <a href="#en">English</a> · <a href="#es">Español</a> · <a href="https://t.me/AlexFitlin">Telegram @AlexFitlin</a></nav>
'@
[IO.File]::WriteAllText((Join-Path $root 'docs/privacy-policy.html'), $html + "`n" + ($articles -join "`n") + "`n</body></html>`n", $utf8)
