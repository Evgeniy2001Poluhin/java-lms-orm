# API Examples for Learning Platform

This file contains example API requests using curl. Replace `localhost:8081` with your server URL if different.

## Users API

### Create a new student
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "new_student",
    "email": "newstudent@example.com",
    "password": "password123",
    "firstName": "New",
    "lastName": "Student",
    "role": "STUDENT"
  }'
```

### Get all users
```bash
curl http://localhost:8081/api/users
```

### Get user by ID
```bash
curl http://localhost:8081/api/users/1
```

### Get users by role
```bash
curl http://localhost:8081/api/users/role/STUDENT
```

## Courses API

### Get all courses
```bash
curl http://localhost:8081/api/courses
```

### Get published courses
```bash
curl http://localhost:8081/api/courses/published
```

### Get course by ID with modules
```bash
curl http://localhost:8081/api/courses/1/with-modules
```

### Create a new course
```bash
curl -X POST "http://localhost:8081/api/courses?instructorId=1&categoryId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Advanced Java Programming",
    "description": "Deep dive into Java",
    "durationHours": 60,
    "difficultyLevel": "ADVANCED"
  }'
```

### Publish a course
```bash
curl -X PUT http://localhost:8081/api/courses/1/publish
```

## Enrollments API

### Enroll student to course
```bash
curl -X POST "http://localhost:8081/api/enrollments?studentId=2&courseId=1"
```

### Get student enrollments
```bash
curl http://localhost:8081/api/enrollments/student/2
```

### Update enrollment progress
```bash
curl -X PUT "http://localhost:8081/api/enrollments/1/progress?progressPercentage=50.0"
```

### Get enrollments for course
```bash
curl http://localhost:8081/api/enrollments/course/1
```

## Assignments API

### Create assignment
```bash
curl -X POST http://localhost:8081/api/assignments \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Homework 1",
    "description": "Complete the exercises",
    "maxScore": 100,
    "deadline": "2024-12-31T23:59:59",
    "lesson": {
      "id": 1
    }
  }'
```

### Submit assignment
```bash
curl -X POST "http://localhost:8081/api/assignments/1/submit?studentId=2&content=My solution here"
```

### Grade submission
```bash
curl -X PUT "http://localhost:8081/api/assignments/submissions/1/grade?score=85&feedback=Good job!"
```

### Get assignments by course
```bash
curl http://localhost:8081/api/assignments/course/1
```

### Get student submissions
```bash
curl http://localhost:8081/api/assignments/submissions/student/2
```

## Quizzes API

### Get quiz with questions
```bash
curl http://localhost:8081/api/quizzes/1/with-questions
```

### Create quiz
```bash
curl -X POST http://localhost:8080/api/quizzes \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Final Test",
    "description": "Test your knowledge",
    "passingScore": 70,
    "timeLimitMinutes": 45,
    "module": {
      "id": 1
    }
  }'
```

### Add question to quiz
```bash
curl -X POST http://localhost:8080/api/quizzes/1/questions \
  -H "Content-Type: application/json" \
  -d '{
    "questionText": "What is ORM?",
    "type": "SINGLE_CHOICE",
    "orderIndex": 0,
    "points": 10
  }'
```

### Add answer option to question
```bash
curl -X POST http://localhost:8080/api/quizzes/questions/1/answers \
  -H "Content-Type: application/json" \
  -d '{
    "optionText": "Object-Relational Mapping",
    "isCorrect": true,
    "orderIndex": 0
  }'
```

### Take quiz (submit answers)
```bash
curl -X POST "http://localhost:8080/api/quizzes/1/take?studentId=2&timeTaken=25" \
  -H "Content-Type: application/json" \
  -d '{
    "1": [1],
    "2": [3]
  }'
```

### Get quiz submissions
```bash
curl http://localhost:8080/api/quizzes/1/submissions
```

### Get student quiz results
```bash
curl http://localhost:8080/api/quizzes/submissions/student/2
```

## Testing Lazy Loading

### This will work - data is loaded with JOIN FETCH
```bash
curl http://localhost:8080/api/courses/1/with-modules
```

### Get course with full structure (modules + lessons)
```bash
curl http://localhost:8080/api/courses/1/full
```

## Useful curl options

- `-v` - Verbose mode (shows request/response headers)
- `-i` - Include response headers
- `-s` - Silent mode (no progress bar)
- `-o filename` - Save output to file

Example with verbose mode:
```bash
curl -v http://localhost:8080/api/courses
```

## Pretty print JSON responses with jq

If you have `jq` installed:
```bash
curl http://localhost:8080/api/courses | jq '.'
```

## Error handling examples

### Try to enroll non-existent student (404)
```bash
curl -X POST "http://localhost:8080/api/enrollments?studentId=9999&courseId=1"
```

### Try to create user with duplicate username (409)
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "prof_ivanov",
    "email": "another@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User",
    "role": "STUDENT"
  }'
```

### Try to submit invalid data (400)
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ab",
    "email": "invalid-email",
    "password": "123"
  }'
```
