# Learning Platform - Система управления обучением (LMS)

Backend-приложение для образовательной платформы, разработанное на Spring Boot с использованием Hibernate ORM.

## Описание проекта
Проект демонстрирует работу с комплексными моделями данных и оптимизацией JPA-запросов.
В системе реализовано более 15 сущностей, включая курсы, уроки, тесты и систему оценивания.

## Архитектура
- **Базовый пакет:** ru.polukhin.learningplatform
- **Технологии:** Java 17, Spring Boot 3.2.0, PostgreSQL, Docker, Swagger.
- **Слои:** Entity, Repository, Service, Controller, DTO.

## Как запустить (Docker)
1. Убедитесь, что Docker запущен.
2. Выполните в терминале:
   docker-compose up --build -d

Приложение будет доступно по адресу: http://localhost:8081
Swagger UI: http://localhost:8081/swagger-ui/index.html

## Документация
Подробная информация находится в файлах:
- [ARCHITECTURE.md](./ARCHITECTURE.md) — описание базы данных и связей.
- [DEMO_DATA.md](./DEMO_DATA.md) — тестовые аккаунты.
- [API_EXAMPLES.md](./API_EXAMPLES.md) — примеры запросов.

## Тестирование
Для запуска всех тестов выполните:
mvn clean test

