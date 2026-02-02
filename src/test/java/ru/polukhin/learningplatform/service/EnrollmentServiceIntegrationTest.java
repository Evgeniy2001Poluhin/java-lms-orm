package ru.polukhin.learningplatform.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.polukhin.learningplatform.entity.*;
import ru.polukhin.learningplatform.exception.BusinessException;
import ru.polukhin.learningplatform.exception.DuplicateResourceException;
import ru.polukhin.learningplatform.repository.CategoryRepository;
import ru.polukhin.learningplatform.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EnrollmentServiceIntegrationTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldEnrollStudentToCourse() {
        // Given
        User student = createStudent("student1", "student1@test.com");
        User instructor = createInstructor("instructor1", "instructor1@test.com");
        Category category = createCategory("Test Category");
        Course course = createCourse("Test Course", instructor, category, true);

        // When
        Enrollment enrollment = enrollmentService.enrollStudent(student.getId(), course.getId());

        // Then
        assertThat(enrollment).isNotNull();
        assertThat(enrollment.getStudent()).isEqualTo(student);
        assertThat(enrollment.getCourse()).isEqualTo(course);
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
    }

    
    @Test
    void shouldNotEnrollStudentTwice() {
        // Given
        User student = createStudent("student3", "student3@test.com");
        User instructor = createInstructor("instructor3", "instructor3@test.com");
        Category category = createCategory("Test Category 3");
        Course course = createCourse("Test Course 3", instructor, category, true);
        
        enrollmentService.enrollStudent(student.getId(), course.getId());

        // When / Then
        assertThatThrownBy(() -> enrollmentService.enrollStudent(student.getId(), course.getId()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already enrolled");
    }

    @Test
    void shouldUpdateEnrollmentProgress() {
        // Given
        User student = createStudent("student4", "student4@test.com");
        User instructor = createInstructor("instructor4", "instructor4@test.com");
        Category category = createCategory("Test Category 4");
        Course course = createCourse("Test Course 4", instructor, category, true);
        
        Enrollment enrollment = enrollmentService.enrollStudent(student.getId(), course.getId());

        // When
        Enrollment updated = enrollmentService.updateProgress(enrollment.getId(), 50.0);

        // Then
        assertThat(updated.getProgressPercentage()).isEqualTo(50.0);
        assertThat(updated.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
    }

    @Test
    void shouldCompleteEnrollmentWhenProgressReaches100() {
        // Given
        User student = createStudent("student5", "student5@test.com");
        User instructor = createInstructor("instructor5", "instructor5@test.com");
        Category category = createCategory("Test Category 5");
        Course course = createCourse("Test Course 5", instructor, category, true);
        
        Enrollment enrollment = enrollmentService.enrollStudent(student.getId(), course.getId());

        // When
        Enrollment updated = enrollmentService.updateProgress(enrollment.getId(), 100.0);

        // Then
        assertThat(updated.getProgressPercentage()).isEqualTo(100.0);
        assertThat(updated.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
        assertThat(updated.getCompletedAt()).isNotNull();
    }

    private User createStudent(String username, String email) {
        User student = User.builder()
                .username(username)
                .email(email)
                .password("password")
                .firstName("First")
                .lastName("Last")
                .role(RoleType.STUDENT)
                .active(true)
                .build();
        return userRepository.save(student);
    }

    private User createInstructor(String username, String email) {
        User instructor = User.builder()
                .username(username)
                .email(email)
                .password("password")
                .firstName("First")
                .lastName("Last")
                .role(RoleType.INSTRUCTOR)
                .active(true)
                .build();
        return userRepository.save(instructor);
    }

    private Category createCategory(String name) {
        Category category = Category.builder()
                .name(name)
                .description("Description")
                .build();
        return categoryRepository.save(category);
    }

    private Course createCourse(String title, User instructor, Category category, boolean published) {
        Course course = Course.builder()
                .title(title)
                .description("Description")
                .instructor(instructor)
                .category(category)
                .published(published)
                .durationHours(40)
                .difficultyLevel(DifficultyLevel.BEGINNER)
                .build();
        return courseService.createCourse(course, instructor.getId(), category.getId());
    }
}
