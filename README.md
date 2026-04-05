# qa-internship-test-task

API-автотесты для тестового задания на стажировку QA.

## Стек

- Java 17
- Maven
- JUnit 5
- Rest Assured
- Allure
- Spotless
- Checkstyle

## Окружение

- Java 17 (OpenJDK или Oracle JDK)
- Maven 3.9+
- Интернет-доступ к `https://qa-internship.avito.com`

Проверить версии:

```bash
java -version
mvn -version
```

## Быстрый старт

```bash
git clone <repo>
cd qa-internship-test-tasks
mvn clean test                 
```

Все тесты независимы, данные генерируются на лету (уникальные `sellerId`, имена объявлений).

## Allure

```bash
mvn clean test                  
mvn allure:report               
mvn allure:serve                
```

Сырые результаты: `target/allure-results`.

## Линтер и форматтер

```bash
mvn spotless:check   
mvn spotless:apply   
mvn checkstyle:check 
```

## Покрываемые сценарии

- `POST /api/1/item`: позитивы, граничные значения `sellerId`, обязательные поля, типы, идемпотентность.
- `GET /api/1/item/{id}`: happy-path, неизвестный id.
- `GET /api/1/{sellerId}/item`: списки объявлений, проверки sellerId.
- `GET /api/1/statistic/{id}`: валидный и несуществующий id.
- E2E-потоки: create - get, create x2 -get by seller, create - get statistics.
- Нефункциональные проверки: Content-Type и время отклика `POST`.


