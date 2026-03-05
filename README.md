# ArtSchool Client-Server App

Курсовая работа: информационно-справочная система художественной школы (клиент–сервер).

## Стек
- Server: Spring Boot (REST API), Spring Data JPA (Hibernate), H2, Spring Security (Basic Auth)
- Client: JavaFX
- Формат обмена: JSON по HTTP

## Запуск
### Сервер
```bash
cd server
mvnw spring-boot:run

cd client
mvn javafx:run
