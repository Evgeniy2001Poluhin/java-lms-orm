# Архитектура проекта Learning Platform

## Обзор

Проект построен на основе многослойной (layered) архитектуры с четким разделением ответственности между компонентами.

## Структура слоев

```
┌─────────────────────────────────────┐
│     Presentation Layer (REST)       │
│        Controllers                  │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│       Business Logic Layer          │
│          Services                   │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Data Access Layer              │
│       Repositories (JPA)            │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Domain Layer                │
│      Entities (JPA/Hibernate)       │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│          Database                   │
│        PostgreSQL                   │
└─────────────────────────────────────┘
```

## Слои приложения

### 1. Domain Layer (Entity)

**Пакет:** `ru.polukhin.learningplatform.entity`

**Ответственность:**
- Определение бизнес-сущностей
- Отображение на таблицы БД (JPA/Hibernate)
- Связи между сущностями

**Основные классы:**
- `BaseEntity` - базовый класс с общими полями (id, timestamps)
- `User` - пользователи системы
- `Course`, `Module`, `Lesson` - структура курсов
- `Assignment`, `Submission` - задания и решения
- `Quiz`, `Question`, `AnswerOption` - тестирование
- `Enrollment` - запись на курсы
- `CourseReview` - отзывы

**Типы связей:**
- 1-1: Module ↔ Quiz
- 1-M: Course → Module, Module → Lesson, Quiz → Question
- M-M: User ↔ Course (через Enrollment)

### 2. Data Access Layer (Repository)

**Пакет:** `ru.polukhin.learningplatform.repository`

**Ответственность:**
- CRUD операции с БД
- Кастомные запросы
- Оптимизированные выборки с JOIN FETCH

**Технологии:**
- Spring Data JPA
- JPQL для сложных запросов
- Query Methods

**Примеры:**
```java
// Именованный метод
List<User> findByRole(RoleType role);

// JPQL запрос
@Query("SELECT c FROM Course c LEFT JOIN FETCH c.modules WHERE c.id = :id")
Course findByIdWithModules(@Param("id") Long id);
```

### 3. Business Logic Layer (Service)

**Пакет:** `ru.polukhin.learningplatform.service`

**Ответственность:**
- Бизнес-логика приложения
- Валидация данных
- Управление транзакциями
- Обработка исключений

**Основные сервисы:**
- `UserService` - управление пользователями
- `CourseService` - управление курсами
- `EnrollmentService` - записи на курсы
- `AssignmentService` - задания и решения
- `QuizService` - тестирование

**Паттерны:**
- Service Pattern
- Transaction Management (@Transactional)
- Dependency Injection

### 4. Presentation Layer (Controller)

**Пакет:** `ru.polukhin.learningplatform.controller`

**Ответственность:**
- REST API endpoints
- Обработка HTTP запросов/ответов
- Валидация входных данных
- Документация API (Swagger)

**Контроллеры:**
- `UserController` - API пользователей
- `CourseController` - API курсов
- `EnrollmentController` - API записей
- `AssignmentController` - API заданий
- `QuizController` - API тестов

**Используемые аннотации:**
- `@RestController`
- `@RequestMapping`
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- `@Valid` для валидации

### 5. Cross-cutting Concerns

#### Exception Handling

**Пакет:** `ru.polukhin.learningplatform.exception`

**Компоненты:**
- `GlobalExceptionHandler` - глобальный обработчик исключений
- `ResourceNotFoundException` - ресурс не найден (404)
- `DuplicateResourceException` - дубликат (409)
- `BusinessException` - бизнес-ошибки (400)
- `ErrorResponse` - стандартизированный ответ об ошибке

#### Configuration

**Пакет:** `ru.polukhin.learningplatform.config`

**Компоненты:**
- `DataInitializer` - предзаполнение демо-данными
- Application properties (профили dev/test/prod)

## Принципы проектирования

### SOLID Principles

1. **Single Responsibility** - каждый класс имеет одну ответственность
2. **Open/Closed** - расширяемость через наследование и интерфейсы
3. **Liskov Substitution** - использование полиморфизма
4. **Interface Segregation** - Spring Data JPA repositories
5. **Dependency Inversion** - DI через Spring

### DRY (Don't Repeat Yourself)

- `BaseEntity` для общих полей
- `@MappedSuperclass` для переиспользования
- Utility методы в сервисах

### Separation of Concerns

- Четкое разделение по слоям
- Отсутствие бизнес-логики в контроллерах
- Отсутствие SQL в бизнес-логике

## Управление транзакциями

### Стратегия

- `@Transactional(readOnly = true)` на уровне сервисов
- `@Transactional` для операций изменения данных
- Ленивая загрузка выполняется внутри транзакций

### Проблема N+1 и решения

**Проблема:**
```java
// Приведет к N+1 запросам
Course course = courseRepository.findById(id);
course.getModules().forEach(module -> 
    System.out.println(module.getTitle())
);
```

**Решения:**

1. **JOIN FETCH:**
```java
@Query("SELECT c FROM Course c LEFT JOIN FETCH c.modules WHERE c.id = :id")
Course findByIdWithModules(@Param("id") Long id);
```

2. **Entity Graph:**
```java
@EntityGraph(attributePaths = {"modules", "modules.lessons"})
Course findById(Long id);
```

3. **Batch Size:**
```java
@BatchSize(size = 10)
private List<Module> modules;
```

## Lazy Loading

### Стратегия

По умолчанию используется LAZY загрузка для коллекций:
- `@OneToMany(fetch = FetchType.LAZY)` - по умолчанию
- `@ManyToMany(fetch = FetchType.LAZY)` - по умолчанию
- `@ManyToOne(fetch = FetchType.EAGER)` - по умолчанию

### Решение проблем

1. **В рамках транзакции:**
```java
@Transactional
public Course getCourseWithModules(Long id) {
    Course course = courseRepository.findById(id);
    course.getModules().size(); // инициализация
    return course;
}
```

2. **JOIN FETCH запросы:**
```java
findByIdWithModules(Long id)
findByIdWithFullStructure(Long id)
```

## Валидация данных

### Bean Validation

Используются аннотации на уровне Entity:
- `@NotBlank` - не пустое значение
- `@NotNull` - не null
- `@Email` - email формат
- `@Size(min, max)` - размер строки
- `@Min`, `@Max` - числовые ограничения

### Валидация в контроллерах

```java
@PostMapping
public ResponseEntity<User> create(@Valid @RequestBody User user) {
    // автоматическая валидация
}
```

### Кастомная валидация

В сервисах:
- Проверка уникальности
- Бизнес-правила
- Проверка доступа

## Обработка ошибок

### HTTP Status Codes

- **200 OK** - успешная операция
- **201 Created** - ресурс создан
- **204 No Content** - успешное удаление
- **400 Bad Request** - ошибка валидации
- **404 Not Found** - ресурс не найден
- **409 Conflict** - конфликт (дубликат)
- **500 Internal Server Error** - серверная ошибка

### Структура ответа об ошибке

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 123",
  "path": "/api/users/123"
}
```

## Тестирование

### Уровни тестирования

1. **Repository Tests** (`@DataJpaTest`)
   - Тестирование слоя данных
   - H2 in-memory database
   - Проверка CRUD и запросов

2. **Service Integration Tests** (`@SpringBootTest`)
   - Тестирование бизнес-логики
   - Работа с реальными компонентами
   - Транзакционные тесты

3. **Lazy Loading Tests**
   - Демонстрация LazyInitializationException
   - Проверка JOIN FETCH решений

4. **Controller Tests** (опционально)
   - `@WebMvcTest`
   - MockMvc для тестирования REST API

### Testcontainers (готовность)

Проект готов к использованию Testcontainers для интеграционных тестов с реальной PostgreSQL.

## Deployment

### Docker

**Многоступенчатая сборка:**
1. Build stage - Maven компиляция
2. Runtime stage - JRE Alpine

**Docker Compose:**
- PostgreSQL контейнер
- Application контейнер
- Сетевое взаимодействие
- Volume для данных БД

### CI/CD

**GitHub Actions:**
- Автоматическая сборка
- Запуск тестов
- Docker image build
- Code quality checks

## Масштабируемость

### Горизонтальное масштабирование

- Stateless сервисы
- Shared database
- Load balancer ready

### Кэширование (будущее)

Готовность к добавлению:
- Spring Cache
- Redis integration
- Query result caching

### Производительность

- Connection pooling (HikariCP)
- Batch operations
- Pagination для больших выборок
- Index optimization в БД

## Безопасность (future scope)

Архитектура готова к добавлению:
- Spring Security
- JWT authentication
- Role-based access control
- Password encryption

## Мониторинг (future scope)

Возможности добавления:
- Spring Actuator
- Prometheus metrics
- Logging (Logback/SLF4J)
- Application health checks
