$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$utf8 = [Text.UTF8Encoding]::new($false)
$updates = @(
    @{ Dir='values'; Version='Version 7 · 7 October 2026'; Retention='Retention and deletion\nProgress remains until you clear app storage in Android settings. Clearing storage deletes all local game data, settings, consent and input-protection state. Uninstalling removes local app data, but existing system backups may remain; manage them separately with your backup provider.'; Support='Security and support\nProgress and settings are kept in private Android app storage. The game does not add encryption to these files; device and backup protection depend on Android and your settings. Bundled music and sounds play locally. When you email support, the recipient receives your email address, message and any attachments you choose to send. Messages are used to answer your request; request deletion through the same email. Your email provider handles its own copies. Do not send passwords, payment details or sensitive personal information.'; Withdraw='Withdrawing consent stops the game and returns to the consent screen. Progress remains on the device. To delete all local data, including settings and consent, clear app storage in Android settings.'; Contact='Contact the developer by email' },
    @{ Dir='values-ru'; Version='Версия 7 · 7 октября 2026 года'; Retention='Хранение и удаление\nПрогресс хранится до очистки хранилища приложения в настройках Android. Очистка удаляет все локальные игровые данные, настройки, согласие и сведения защиты нажатий. Удаление приложения удаляет локальные данные, но ранее созданные системные резервные копии могут остаться; управляйте ими отдельно через поставщика резервных копий.'; Support='Защита данных и поддержка\nПрогресс и настройки хранятся в закрытом хранилище приложения Android. Игра не выполняет дополнительное шифрование этих файлов; защита устройства и резервных копий зависит от Android и ваших настроек. Музыка и звуковые эффекты включены в приложение и воспроизводятся локально. При обращении по электронной почте получатель видит ваш адрес, сообщение и вложения, которые вы решите отправить. Сообщения используются для ответа на обращение; запросить удаление можно через тот же email. Почтовый сервис обрабатывает собственные копии. Не отправляйте пароли, платёжные данные или чувствительные персональные сведения.'; Withdraw='Отзыв останавливает игру и возвращает экран согласия. Прогресс остаётся на устройстве. Чтобы удалить все локальные данные, включая настройки и согласие, очистите хранилище приложения в настройках Android.'; Contact='Связаться с разработчиком по email' },
    @{ Dir='values-es'; Version='Versión 7 · 7 de octubre de 2026'; Retention='Conservación y eliminación\nEl progreso permanece hasta que borres el almacenamiento de la aplicación en los ajustes de Android. Esto elimina todos los datos locales, los ajustes, el consentimiento y la protección de pulsaciones. Desinstalar elimina los datos locales, pero pueden permanecer copias del sistema anteriores; adminístralas con tu proveedor de copias.'; Support='Seguridad y soporte\nEl progreso y los ajustes se guardan en el almacenamiento privado de Android. El juego no añade cifrado; la protección del dispositivo y las copias depende de Android y tus ajustes. La música y los efectos incluidos se reproducen localmente. Al contactar por email, el destinatario recibe tu dirección, tu mensaje y los archivos que decidas enviar. Los mensajes se usan para responder; puedes solicitar su eliminación por el mismo email. Tu proveedor de correo administra sus propias copias. No envíes contraseñas, datos de pago ni información personal sensible.'; Withdraw='Retirar el consentimiento detiene el juego y vuelve a la pantalla de consentimiento. El progreso permanece en el dispositivo. Para eliminar todos los datos locales, incluidos ajustes y consentimiento, borra el almacenamiento en los ajustes de Android.'; Contact='Contactar al desarrollador por email' }
)
foreach($update in $updates) {
    $path=Join-Path $projectRoot ('app/src/main/res/' + $update.Dir + '/privacy.xml')
    $source=[IO.File]::ReadAllText($path)
    $match=[regex]::Match($source,'<string name="privacy_policy_body">(.*?)</string>','Singleline')
    $body=$match.Groups[1].Value
    $body=[regex]::Replace($body,'(Version|Версия|Versión) [2-7] · [^\\]+',$update.Version)
    $retentionHeader=$update.Retention.Split(@('\n'),[StringSplitOptions]::None)[0]
    $supportHeader=$update.Support.Split(@('\n'),[StringSplitOptions]::None)[0]
    $parts=$body.Split(@('\n\n'),[StringSplitOptions]::None)
    for($index=0;$index -lt $parts.Length;$index++) {
        if($parts[$index].StartsWith($retentionHeader+'\n')) {$parts[$index]=$update.Retention}
        elseif($parts[$index].StartsWith($supportHeader+'\n')) {$parts[$index]=$update.Support}
        else {
            $parts[$index]=$parts[$index].Replace('; Telegram @AlexFitlin','')
            $parts[$index]=[regex]::Replace($parts[$index],'[^.\\]*(?:Telegram|сброс|reset|restablec)[^.\\]*\. ?', '', 'IgnoreCase')
        }
    }
    $body=$parts -join '\n\n'
    if($update.Dir -eq 'values' -and !$body.Contains('A cooldown lasts at most two minutes')) {
        $body=$body.Replace('Clearing app storage removes this metadata.', 'A cooldown lasts at most two minutes; repeated-detection escalation expires after five quiet minutes. Clearing app storage removes this metadata.')
    }
    $body=[regex]::Replace($body,'(?<=[.!?])(?=[A-ZА-ЯÁÉÍÓÚÑ])',' ')
    $source=$source.Substring(0,$match.Groups[1].Index)+$body+$source.Substring($match.Groups[1].Index+$match.Groups[1].Length)
    foreach($field in @(@{Name='privacy_withdraw_description';Text=$update.Withdraw},@{Name='privacy_contact';Text=$update.Contact})) {
        $source=[regex]::Replace($source,'(<string name="'+$field.Name+'">).*?(</string>)',('$1'+$field.Text+'$2'),'Singleline')
    }
    [IO.File]::WriteAllText($path,$source,$utf8)
}
