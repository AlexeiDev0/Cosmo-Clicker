# Шрифты интерфейса

Exo 2 используется для заголовков, кнопок, вкладок и счётчиков; Manrope — для описаний и текста. Все 15 базовых стилей Material 3 настроены явно. Встроенные файлы работают офлайн и поддерживают кириллицу и латиницу.

| Файл | Источник | SHA-256 |
| --- | --- | --- |
| `app/src/main/res/font/exo2_variable.ttf` | [Google Fonts / Exo 2](https://github.com/google/fonts/tree/main/ofl/exo2) | `205a448676a2586f9c57c25f3d5c58ca8db7e6cf5edf7506783a010c6fe2bfb5` |
| `app/src/main/res/font/manrope_variable.ttf` | [Google Fonts / Manrope](https://github.com/google/fonts/tree/main/ofl/manrope) | `3ae11c49db0455a3cc33e37d380f20fdb8c7f8b41dc07625c177e3d87a9d6ae6` |

Оригинальные файлы загружены 06.10.2026; не модифицированы. SIL Open Font License 1.1 и copyright обоих проектов включены в APK: `app/src/main/assets/licenses/exo2-ofl.txt` и `manrope-ofl.txt`.

Variable fonts используются на Android 8+ с явными осями веса. Manrope поддерживает веса до 800, поэтому запрос 900 использует ось 800. На Android 7 используется безопасный системный sans-serif: variable fonts доступны с API 26. Числа используют табличную ширину (`tnum`), чтобы обновление баланса и таймеров не раскачивало строку. Итоговое размещение текста на устройстве ещё нужно проверить при увеличенном системном размере шрифта.
