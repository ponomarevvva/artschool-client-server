# ArtSchool Client-Server App

Курсовая работа: информационно-справочная система художественной школы, реализованная как клиент–серверное приложение.

## Возможности
- Авторизация и регистрация пользователей
- Роли доступа: **ADMIN** (полный доступ) и **USER** (только просмотр)
- CRUD для сущностей **Course** (курсы) и **Student** (ученики)
- Привязка ученика к курсу по `courseId`
- Фильтрация учеников по имени
- Статистика: количество учеников по каждому курсу
- Раздел «Об авторе» в виде отдельного окна

## Технологии
- **Server:** Java, Spring Boot (REST API), Spring Data JPA (Hibernate), H2, Spring Security (Basic Auth)
- **Client:** JavaFX
- Обмен данными: HTTP + JSON

## Структура проекта
- `server/` — серверная часть (Spring Boot)
- `client/` — клиентская часть (JavaFX)

## Запуск проекта

### 1) Запуск сервера
1. Откройте PowerShell/Terminal.
2. Перейдите в папку `server` и запустите сервер:

```powershell
cd C:\GitProjects\ArtSchool\server
.\mvnw spring-boot:run

После запуска сервер доступен по адресу:
http://localhost:8080

2) Запуск клиента

Откройте второе окно PowerShell/Terminal.

Перейдите в папку client и запустите клиент:

cd C:\GitProjects\ArtSchool\client
.\mvnw javafx:run
Учётные записи

ADMIN: admin / admin

USER: создаётся через регистрацию в клиентском приложении

Примечание

Если путь к проекту на вашем компьютере другой, замените C:\GitProjects\ArtSchool\... на фактический путь к папке проекта.
