Захарова Сандаара 
Б9124-09.03.03 (пикд 3)

https://jsonplaceholder.typicode.com
Ресурс: Посты (Posts)

Что даёт API:
- Получение списка постов
- Получение деталей поста по ID
- Фильтрация постов по ID пользователя

ДЗ 3: Базовое приложение

Реализованный функционал
- 2 экрана: список постов + детали поста
- Navigation Compose
- Retrofit + конвертер Gson
- ViewModel + viewModelScope
- Repository между ViewModel и Retrofit
- Compose + Material3

UI-состояния
- Loading — индикатор загрузки
- Error + Retry — сообщение об ошибке и кнопка повтора
- Empty — сообщение, если ничего не найдено
- Success — отображение списка постов

ДЗ 4: Hilt + Room

Добавлено
- Hilt для Dependency Injection (Retrofit, Repository, Room, ViewModel)
- Room — таблица favorites для хранения избранного

Выбранный сценарий
Избранное (Favourites)

- Пользователь может добавить пост в избранное (иконка)
- Состояние избранного сохраняется в Room
- После перезапуска приложения избранное восстанавливается
- Добавлен отдельный экран «Избранное» с кнопкой перехода в шапке

ДЗ 5: Тестирование

Юнит-тесты (6+)

Файл: PostListViewModelTest.kt

тест/статус
1. Начальное состояние экрана — Loading
2. Успешная загрузка данных — Success
3. Ошибка загрузки — Error 
4. Retry после ошибки
5. Пустой результат — Empty 
6. Фильтр по userId 

Нетривиальные тесты (2+)

Файл: PostListViewModelAdvancedTest.kt

Тест (Что проверяет)

1. retry() действительно инициирует новый запрос (Контракт поведения)
2. Пустой результат → Empty (не Success) (Правильная типизация состояния)

Flow-тесты (2+)

Файл: PostListViewModelFlowTest.kt

Тест  (Что проверяет)

1. Полная последовательность эмиссий (Loading → Error → Loading → Success) (Последовательность состояний)
2.  Обновление состояния при изменении избранного (Реактивность Flow)

Интеграционные тесты (3+)

Файл (Что тестирует)
1. RepositoryRoomIntegrationTest.kt (Repository + Room (вставка и чтение))
2. RepositoryWithFakeApiTest.kt (Repository + Fake API + Room)
3. SimpleIntegrationTest.kt  (Базовый интеграционный тест)


Запуск проекта

Требования
- Android Studio (последняя версия)
- Устройство или эмулятор с Android API 24+

Шаги для запуска
1. Клонировать репозиторий
2. Открыть проект в Android Studio
3. Дождаться синхронизации Gradle
4. Запустить приложение на устройстве/эмуляторе

Запуск тестов
bash
Unit тесты
./gradlew test

Интеграционные тесты (требуется устройство)
./gradlew connectedAndroidTest
<img src="screenshots/ph1.jpg" width="200">  <img src="screenshots/ph2.jpg" width="200">
<img src="screenshots/ph3.jpg" width="200">
<img src="screenshots/ph4.jpg" width="200">
<img src="screenshots/ph5.jpg" width="200">
<img src="screenshots/ph6.jpg" width="200">
<img src="screenshots/ph7.jpg" width="200">
<img src="screenshots/ph8.jpg" width="200">
<img src="screenshots/ph9.jpg" width="200">
