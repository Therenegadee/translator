## Установка и запуск

1. Клонируйте этот репозиторий на свой компьютер с помощью команды `git clone https://github.com/Therenegadee/translator`.
2. После установки проекта необходимо запустить его сборку с помощью Docker, написав в терминале команду `docker compose up -d`. в корневом каталоге проекта. Это запустит все контейнеры, определённые в файле docker-compose.yml.
3. Swagger UI будет доступен по адресу http://localhost:8080/swagger-ui.html. Здесь вы сможете увидеть endpoint'ы данного проекта и отправить на них запросы.

### Пример Request Body для запроса на перевод:

```json
{
    "textToTranslate": "hello world, i am human",
    "sourceLanguage": "en",
    "destinationLanguage": "ru"
}
```
### БД
БД доступна по адресу: _jdbc:postgresql://localhost:10000/translator_

БД Credentials:
- username: translator
- password: translator

К БД можно подключиться из IDEA, если версия Ultimate, либо можно использовать DBeaver или аналоги.