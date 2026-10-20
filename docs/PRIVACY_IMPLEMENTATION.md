# Политика и согласие в игре

## Актуальная редакция — версия 10, 13 октября 2026 года

В `values`, `values-ru` и `values-es` добавлено описание слияния дронов.
Итоговый состав флота, открытия и прогресс заданий записываются в существующее
локальное сохранение. Отдельная история попыток не записывается; выбранная пара
и состояние анимации существуют только в текущем окне мастерской. Подтверждённый
результат применяется до анимации. Слияние само по себе не вызывает рекламных
запросов и не передаёт выбранную пару или результат разработчику.

`PrivacyConsent.POLICY_VERSION` повышен до 10. Согласие с прежней редакцией
потребует повторного принятия при запуске обновлённого приложения; сохранение
игры при этом не удаляется. Экспорт читает тексты приложения и проверяет версию
согласия, не изменяя исходные политики. Контакт поддержки в HTML — email.

Описание рекламных данных сверено с [документацией Google Mobile Ads SDK](https://developers.google.com/admob/android/privacy/play-data-disclosure).
Проверки выполняются локально на компьютере; проверки на телефоне не запускаются.
Сведения о старых редакциях и публикациях ниже оставлены как история.

> Обновление 05.10.2026: для тестовой интеграции AdMob политика повышена до версии 5. SDK Google Mobile Ads и UMP добавлены, поэтому описание отсутствия сетевых SDK ниже относится к прежней офлайн-версии. Актуальные сведения о рекламе, возрастном режиме 13+, тестовых и реальных ID, публичной политике и Data safety находятся в [ADMOB_SETUP.md](ADMOB_SETUP.md). Тексты EN/RU/ES и HTML обновлены вместе. Реальные показы пока отключены без production ID.


Политика версии 4 доступна на английском, русском и испанском. Текст описывает фактические данные game_prefs/settings, временный локальный анализ кликов, локальные сроки паузы в input_guard и счётчик загрузок Android, отсутствие сетевых SDK, системные копии Android и внешнюю связь через существующий контакт Telegram.

До согласия MainActivity показывает PrivacyScreen и не вызывает GameScreen, поэтому GameViewModel не создаётся и не запускает загрузку, сохранение, симуляцию или звук игры. Кнопка принятия доступна только после самостоятельной отметки. Закрытие и «Назад» завершают Activity и не записывают согласие. Выбор хранится в privacy_consent с версией политики и датой; переход в игру происходит только после успешного commit на IO-потоке. При повышении POLICY_VERSION требуется новое согласие.

В настройках можно открыть полный текст или отозвать согласие. Удаление согласия возвращает начальный экран; удаление GameScreen из композиции останавливает симуляцию и звук через существующий lifecycle-код, который также сохраняет последний снимок прогресса. Сохранение остаётся доступным после нового согласия. Отзыв не обещает удаление всех данных: удаление прогресса и очистка всех данных Android описаны отдельно.

Согласие и локальные сроки защиты нажатий исключены из облачных копий и переноса устройства в обоих наборах правил Android. Прогресс и пользовательские настройки продолжают участвовать в системном резервном копировании, что явно отражено в политике.

Политика версии 6 опубликована 06.10.2026 через Sites: https://drona-salvage-privacy.slavfit.chatgpt.site . Доступ public, deployment succeeded. docs/privacy-policy.html и sites/privacy-policy/dist/index.html содержат одинаковый текст EN/RU/ES. Email владельца alex.fitlin@gmail.com добавлен по его сообщению; имеющийся Telegram @AlexFitlin сохранён. Политика описывает отдельный выбор рекламных запросов, остановку новых запросов и исключение AD_ID/AdServices permissions. Актуальные проверки и ограничения: play-store-listing/release-fixes.md.

Основание: Google Play User Data, https://support.google.com/googleplay/android-developer/answer/10144311?hl=en. Реализация описывает текущую сборку и не является утверждением о прохождении всей проверки Google Play либо универсальном соответствии законодательству.

UI-регрессии добавлены в PrivacyConsentUiTest: отказ не изменяет сохранение, кнопка принятия изначально выключена, согласие сохраняется при пересоздании Activity, после отзыва снова показывается экран. Существующие smoke-тесты проходят согласие явным нажатием, а не обходят его записью фальшивого принятия.

Проверка: compileDebugKotlin завершилась успешно; testDebugUnitTest выполнен, 160 тестов без ошибок и падений. compileDebugAndroidTestKotlin также прошёл при первом запуске проверки. UI-тесты на устройстве не выполнялись: adb devices не обнаружил подключённых устройств. XML политики и обоих правил резервного копирования проверены на корректность.
# Update — 11 October 2026, policy version 8

The policy now describes bundled artwork and manual/system language selection.
Lifetime active play time is stored locally as `lifetimePlayTimeMillis` for time
achievements. Offline time and advertising playback do not increase this counter.
Player titles are computed from claimed achievement IDs. Daily reward day,
merchant exchanges and new drone inventory use existing local progress storage.
New music and sound effects are bundled; no image, music or translation service
is contacted during gameplay. Public copies and in-app text are synchronized in
English, Russian and Spanish. New language options currently translate main
controls; longer text falls back to English.

