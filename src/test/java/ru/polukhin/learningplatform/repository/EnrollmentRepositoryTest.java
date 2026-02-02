package ru.polukhin.learningplatform.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.polukhin.learningplatform.entity.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EnrollmentRepositoryTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User student;
    private Course course;

    @BeforeEach
    void setUp() {
        student = User.builder()
                .username("student")
                .email("student@example.com")
                .password("password")
                .firstName("Student")
                .lastName("Test")
                .role(RoleType.STUDENT)
                .active(true)
                .build();
        student = userRepository.save(student);

        User instructor = User.builder()
                .username("instructor")
                .email("instructor@example.com")
                .password("password")
                .firstName("Instructor")
                .lastName("Test")
                .role(RoleType.INSTRUCTOR)
                .active(true)
                .build();
        instructor = userRepository.save(instructor);

        Category category = Category.builder()
                .name("Test Category")
                .build();
        category = categoryRepository.save(category);

        course = Course.builder()
                .title("Test Course")
                .description("Description")
                .instructor(instructor)
                .category(category)
                .published(true)
                .durationHours(40)
                .difficultyLevel(DifficultyLevel.BEGINNER)
                .build();
        course = courseRepository.save(course);
    }

    @Test
    void shouldSaveEnrollment() {
        // Given
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .status(EnrollmentStatus.ACTIVE)
                .progressPercentage(0.0)
                .build();

        // When
        Enrollment saved = enrollmentRepository.save(enrollment);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStudent()).isEqualTo(student);
        assertThat(saved.getCourse()).isEqualTo(course);
        assertThat(saved.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
    }

    @Test
    void shouldFindEnrollmentByStudentAndCourse() {
        // Given
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .status(EnrollmentStatus.ACTIVE)
                .progressPercentage(0.0)
                .build();
        enrollmentRepository.save(enrollment);

        // When
        Optional<Enrollment> found = enrollmentRepository.findByStudentAndCourse(student, course);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getStudent()).isEqualTo(student);
        assertThat(found.get().getCourse()).isEqualTo(course);
    }

    @Test
    void shouldCheckIfEnrollmentExists() {
        // Given
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .status(EnrollmentStatus.ACTIVE)
                .progressPercentage(0.0)
                .build();
        enrollmentRepository.save(enrollment);

        // When
        boolean exists = enrollmentRepository.existsByStudentAndCourse(student, course);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldCountActiveEnrollments() {
        // Given
        Enrollment active1 = createEnrollment(student, course, EnrollmentStatus.ACTIVE);
        
        User student2 = userRepository.save(User.builder()
                .username("student2")
                .email("student2@example.com")
                .password("password")
                .firstName("Student2")
                .lastName("Test")
                .role(RoleType.STUDENT)
                .active(true)
                .build());
        
        Enrollment active2 = createEnrollment(student2, course, EnrollmentStatus.ACTIVE);
        Enrollment completed = createEnrollment(student2, course, EnrollmentStatus.COMPLETED);
        
        enrollmentRepository.save(active1);

        // When
        long count = enrollmentRepository.countActiveEnrollmentsByCourseId(course.getId());

        // Then
        assertThat(count).isEqualTo(1);
    }

    private Enrollment createEnrollment(User student, Course course, EnrollmentStatus status) {
        return Enrollment.builder()
                .student(student)
                .course(course)
                .status(status)
                .progressPercentage(0.0)
                .build();
    }
}
