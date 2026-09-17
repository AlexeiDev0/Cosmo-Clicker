# Шаг 7 — event UI и event artwork

Дата: 2026-09-14.

Создан отдельный обзорный лист [events.html](visual/events.html) для десяти event compositions: Black Hole, Meteor, Trading Ship, Unknown Object, Solar, Cyber, Pirate, Station, Distress и Storm. Все изображения используют те же flat/vector pathData, что и Android resources.

Проверка показала, что события имеют разные силуэты и композиционные акценты:

- Black Hole — тёмный центр и орбитальные кольца.
- Meteor — несколько крупных объектов с trails.
- Trading Ship — широкий грузовой корпус.
- Unknown Object — симметричный загадочный кристаллический объект.
- Solar — радиальная вспышка.
- Cyber — терминал с центральным сигналом.
- Pirate — атакующий корабль.
- Station — орбитальный реактор.
- Distress — rescue capsule.
- Storm — облако и электрический разряд.

Существующий EventBanner и event dialogs используют общий artwork resolver. Для Meteor Intercept и обычной event challenge image добавлены доступные content descriptions. Сценарии событий, выборы, награды и persistence в этом шаге не изменялись. Unknown Object пока является только подготовленной иллюстрацией: отдельный игровой тип события не добавлялся.

Проверено:

- обзорный лист event artwork в обычном размере;
- отсутствие реалистичных материалов и фототекстур;
- distinguishable silhouettes на плоском фоне;
- localization keys EN/RU/ES без изменений.

После добавления описаний `testDebugUnitTest` нужно запустить повторно. Проверка фактического Android layout, анимаций reduced motion и clipping на устройстве остаётся невозможной без подключённого устройства или hypervisor driver.

Следующий шаг — проверить event animation lifecycle и reduced motion на уровне Compose, затем переходить к sound/music audit.
