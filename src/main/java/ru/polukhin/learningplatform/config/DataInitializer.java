package ru.polukhin.learningplatform.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.polukhin.learningplatform.entity.AnswerOption;
import ru.polukhin.learningplatform.entity.Assignment;
import ru.polukhin.learningplatform.entity.Category;
import ru.polukhin.learningplatform.entity.Course;
import ru.polukhin.learningplatform.entity.DifficultyLevel;
import ru.polukhin.learningplatform.entity.Lesson;
import ru.polukhin.learningplatform.entity.LessonType;
import ru.polukhin.learningplatform.entity.Module;
import ru.polukhin.learningplatform.entity.Question;
import ru.polukhin.learningplatform.entity.QuestionType;
import ru.polukhin.learningplatform.entity.Quiz;
import ru.polukhin.learningplatform.entity.RoleType;
import ru.polukhin.learningplatform.entity.User;
import ru.polukhin.learningplatform.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            CourseRepository courseRepository,
            ModuleRepository moduleRepository,
            LessonRepository lessonRepository,
            AssignmentRepository assignmentRepository,
            QuizRepository quizRepository,
            QuestionRepository questionRepository,
            AnswerOptionRepository answerOptionRepository) {

        return args -> {
            log.info("Initializing demo data...");

            // Create users
            User instructor = User.builder()
                    .username("prof_ivanov")
                    .email("ivanov@polukhin.ru")
                    .password("password123")
                    .firstName("Иван")
                    .lastName("Иванов")
                    .role(RoleType.INSTRUCTOR)
                    .bio("Профессор кафедры программирования")
                    .active(true)
                    .build();
            instructor = userRepository.save(instructor);
            log.info("Created instructor: {}", instructor.getUsername());

            User student1 = User.builder()
                    .username("student_petrov")
                    .email("petrov@student.polukhin.ru")
                    .password("password123")
                    .firstName("Петр")
                    .lastName("Петров")
                    .role(RoleType.STUDENT)
                    .bio("Студент 3 курса")
                    .active(true)
                    .build();
            student1 = userRepository.save(student1);
            log.info("Created student: {}", student1.getUsername());

            User student2 = User.builder()
                    .username("student_sidorova")
                    .email("sidorova@student.polukhin.ru")
                    .password("password123")
                    .firstName("Мария")
                    .lastName("Сидорова")
                    .role(RoleType.STUDENT)
                    .bio("Студентка 2 курса")
                    .active(true)
                    .build();
            student2 = userRepository.save(student2);
            log.info("Created student: {}", student2.getUsername());

            // Create categories
            Category categoryProgramming = Category.builder()
                    .name("Программирование")
                    .description("Курсы по программированию")
                    .build();
            categoryProgramming = categoryRepository.save(categoryProgramming);
            log.info("Created category: {}", categoryProgramming.getName());

            Category categoryDatabases = Category.builder()
                    .name("Базы данных")
                    .description("Курсы по работе с базами данных")
                    .build();
            categoryDatabases = categoryRepository.save(categoryDatabases);
            log.info("Created category: {}", categoryDatabases.getName());

            // Create course
            Course course = Course.builder()
                    .title("Основы Hibernate и ORM")
                    .description("Полный курс по изучению Hibernate ORM Framework")
                    .instructor(instructor)
                    .category(categoryDatabases)
                    .published(true)
                    .durationHours(40)
                    .difficultyLevel(DifficultyLevel.INTERMEDIATE)
                    .build();
            course = courseRepository.save(course);
            log.info("Created course: {}", course.getTitle());

            // Create Module 1
            Module module1 = Module.builder()
                    .title("Введение в ORM")
                    .description("Основные концепции объектно-реляционного отображения")
                    .orderIndex(0)
                    .course(course)
                    .build();
            module1 = moduleRepository.save(module1);
            log.info("Created module: {}", module1.getTitle());

            // Create Lessons for Module 1
            Lesson lesson1 = Lesson.builder()
                    .title("Что такое ORM?")
                    .content("ORM (Object-Relational Mapping) - это технология программирования...")
                    .orderIndex(0)
                    .type(LessonType.TEXT)
                    .durationMinutes(30)
                    .module(module1)
                    .build();
            lesson1 = lessonRepository.save(lesson1);

            Lesson lesson2 = Lesson.builder()
                    .title("Введение в Hibernate")
                    .content("Hibernate - это популярный ORM фреймворк для Java...")
                    .orderIndex(1)
                    .type(LessonType.VIDEO)
                    .videoUrl("https://example.com/video1")
                    .durationMinutes(45)
                    .module(module1)
                    .build();
            lesson2 = lessonRepository.save(lesson2);
            log.info("Created {} lessons", 2);

            // Create Assignment for Lesson 1
            Assignment assignment1 = Assignment.builder()
                    .title("Домашнее задание: Настройка Hibernate")
                    .description("Настройте Hibernate в своем проекте и создайте первую entity")
                    .maxScore(100)
                    .deadline(LocalDateTime.now().plusDays(7))
                    .lesson(lesson1)
                    .build();
            assignmentRepository.save(assignment1);
            log.info("Created assignment: {}", assignment1.getTitle());

            // Create Module 2
            Module module2 = Module.builder()
                    .title("Продвинутые возможности Hibernate")
                    .description("Углубленное изучение возможностей Hibernate")
                    .orderIndex(1)
                    .course(course)
                    .build();
            module2 = moduleRepository.save(module2);

            Lesson lesson3 = Lesson.builder()
                    .title("Lazy Loading и Eager Loading")
                    .content("Стратегии загрузки данных в Hibernate...")
                    .orderIndex(0)
                    .type(LessonType.TEXT)
                    .durationMinutes(60)
                    .module(module2)
                    .build();
            lessonRepository.save(lesson3);

            // Create Quiz for Module 1
            Quiz quiz = Quiz.builder()
                    .title("Тест по основам ORM")
                    .description("Проверьте свои знания основ ORM")
                    .passingScore(70)
                    .timeLimitMinutes(30)
                    .module(module1)
                    .build();
            quiz = quizRepository.save(quiz);
            log.info("Created quiz: {}", quiz.getTitle());

            // Create Questions
            Question question1 = Question.builder()
                    .questionText("Что означает аббревиатура ORM?")
                    .type(QuestionType.SINGLE_CHOICE)
                    .orderIndex(0)
                    .points(10)
                    .quiz(quiz)
                    .build();
            question1 = questionRepository.save(question1);

            AnswerOption answer1_1 = AnswerOption.builder()
                    .optionText("Object-Relational Mapping")
                    .isCorrect(true)
                    .orderIndex(0)
                    .question(question1)
                    .build();
            answerOptionRepository.save(answer1_1);

            AnswerOption answer1_2 = AnswerOption.builder()
                    .optionText("Object-Resource Manager")
                    .isCorrect(false)
                    .orderIndex(1)
                    .question(question1)
                    .build();
            answerOptionRepository.save(answer1_2);

            AnswerOption answer1_3 = AnswerOption.builder()
                    .optionText("Online Resource Mapping")
                    .isCorrect(false)
                    .orderIndex(2)
                    .question(question1)
                    .build();
            answerOptionRepository.save(answer1_3);

            Question question2 = Question.builder()
                    .questionText("Hibernate является фреймворком для Java")
                    .type(QuestionType.TRUE_FALSE)
                    .orderIndex(1)
                    .points(10)
                    .quiz(quiz)
                    .build();
            question2 = questionRepository.save(question2);

            AnswerOption answer2_1 = AnswerOption.builder()
                    .optionText("Правда")
                    .isCorrect(true)
                    .orderIndex(0)
                    .question(question2)
                    .build();
            answerOptionRepository.save(answer2_1);

            AnswerOption answer2_2 = AnswerOption.builder()
                    .optionText("Ложь")
                    .isCorrect(false)
                    .orderIndex(1)
                    .question(question2)
                    .build();
            answerOptionRepository.save(answer2_2);

            log.info("Created {} questions with answers", 2);

            log.info("Demo data initialization completed!");
            log.info("Instructor: username={}, password=password123", instructor.getUsername());
            log.info("Student 1: username={}, password=password123", student1.getUsername());
            log.info("Student 2: username={}, password=password123", student2.getUsername());
        };
    }
}
