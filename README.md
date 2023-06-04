Back часть 

Endpoints:

1. AuthController
- POST: /auth/signin - войти в систему
- POST: /auth/signin - зарегистрироваться в системе

2. UserController
- GET: /user/all - получить список всех пользователей
- GET: /user/{username} - найти пользователя по имени
- GET: /user/my_friends - отобразить список моих друзей
- GET: /user/incoming_friendship - отобразить список исходящий заявок в друзья
- GET: /user/outgoing_friendship - отобразить список входящих заявок в друзья
- POST: /user/send_friendship/{id} - отправить заявку в друзья пользователю с id
- PATCH: /user/cancel_friendship/{id} - отменить заявку в друзья пользователю с id (возможно, если пользователь ещё не принял заявку или не отклонил его)
- PATCH: /user/accept_friendship/{id} - принять заявку в друзья от пользователя с id
- PATCH: /user/decline_friendship/{id} - отклонить заявку в друзья от пользователя с id
- PATCH: /user/delete_friend/{id} - удалить из друзей пользователя с id
- GET: /user/my_subscriptions - отобразить список моих подписок
- POST: /user/subscribe/{id} - подписаться на пользователя с id
- PATCH: /user/unsubscribe/{id} - отписаться на пользователя с id
- POST: /user/chat/{username} - получить chatId с пользователем username, если пользователь является другом

3. PublicationController
- GET: /post/{id} - получить публикацию по id
- GET: /post/my - получить список моих публикаций
- POST: /post - создать публикацию
- PATCH: /post - редактировать публикацию
- DELETE: /post/{id} - удалить публикацию
- GET: /post/news - получить постранично список публикаций подписок. Параметры запроса offset - номер страницы, limit - количество записей