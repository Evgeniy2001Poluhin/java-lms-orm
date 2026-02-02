package ru.polukhin.learningplatform.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.polukhin.learningplatform.entity.Category;
import ru.polukhin.learningplatform.entity.Course;
import ru.polukhin.learningplatform.entity.DifficultyLevel;
import ru.polukhin.learningplatform.entity.Module;
import ru.polukhin.learningplatform.entity.RoleType;
import ru.polukhin.learningplatform.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    private User instructor;
    private Category category;

    @BeforeEach
    void setUp() {
        instructor = User.builder()
                .username("instructor")
                .email("instructor@example.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .role(RoleType.INSTRUCTOR)
                .active(true)
                .build();
        instructor = userRepository.save(instructor);

        category = Category.builder()
                .name("Programming")
                .description("Programming courses")
                .build();
        category = categoryRepository.save(category);
    }

    @Test
    void shouldSaveCourse() {
        // Given
        Course course = Course.builder()
                .title("Test Course")
                .description("Test Description")
                .instructor(instructor)
                .category(category)
                .published(true)
                .durationHours(40)
                .difficultyLevel(DifficultyLevel.BEGINNER)
                .build();

        // When
        Course saved = courseRepository.save(course);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test Course");
        assertThat(saved.getInstructor()).isEqualTo(instructor);
        assertThat(saved.getCategory()).isEqualTo(category);
    }

    @Test
    void shouldFindPublishedCourses() {
        // Given
        Course published = createCourse("Published Course", true);
        Course draft = createCourse("Draft Course", false);
        courseRepository.saveAll(List.of(published, draft));

        // When
        List<Course> publishedCourses = courseRepository.findByPublishedTrue();

        // Then
        assertThat(publishedCourses).hasSize(1);
        assertThat(publishedCourses.get(0).getTitle()).isEqualTo("Published Course");
    }

    @Test
    void shouldFindCoursesByCategory() {
        // Given
        Course course1 = createCourse("Course 1", true);
        Course course2 = createCourse("Course 2", true);
        courseRepository.saveAll(List.of(course1, course2));

        // When
        List<Course> courses = courseRepository.findByCategory(category);

        // Then
        assertThat(courses).hasSize(2);
    }

    @Test
    void shouldFindCoursesByInstructor() {
        // Given
        Course course1 = createCourse("Course 1", true);
        Course course2 = createCourse("Course 2", false);
        courseRepository.saveAll(List.of(course1, course2));

        // When
        List<Course> courses = courseRepository.findByInstructor(instructor);

        // Then
        assertThat(courses).hasSize(2);
    }

    @Test
    void shouldCascadeModulesOnCourseSave() {
        // Given
        Course course = createCourse("Course with Modules", true);
        Module module1 = Module.builder()
                .title("Module 1")
                .description("First module")
                .orderIndex(0)
                .course(course)
                .build();
        Module module2 = Module.builder()
                .title("Module 2")
                .description("Second module")
                .orderIndex(1)
                .course(course)
                .build();
        course.getModules().add(module1);
        course.getModules().add(module2);

        // When
        Course saved = courseRepository.save(course);

        // Then
        assertThat(saved.getModules()).hasSize(2);
        List<Module> modules = moduleRepository.findByCourseOrderByOrderIndexAsc(saved);
        assertThat(modules).hasSize(2);
    }

    @Test
    void shouldDeleteCourseAndCascadeModules() {
        // Given
        Course course = createCourse("Course to Delete", true);
        Module module = Module.builder()
                .title("Module to be deleted")
                .orderIndex(0)
                .course(course)
                .build();
        course.getModules().add(module);
        Course saved = courseRepository.save(course);
        Long courseId = saved.getId();

        // When
        courseRepository.deleteById(courseId);

        // Then
        assertThat(courseRepository.findById(courseId)).isEmpty();
        List<Module> modules = moduleRepository.findByCourseId(courseId);
        assertThat(modules).isEmpty();
    }

    private Course createCourse(String title, boolean published) {
        return Course.builder()
                .title(title)
                .description("Description")
                .instructor(instructor)
                .category(category)
                .published(published)
                .durationHours(40)
                .difficultyLevel(DifficultyLevel.BEGINNER)
                .build();
    }
}
