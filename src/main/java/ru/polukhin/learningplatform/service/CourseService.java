package ru.polukhin.learningplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.polukhin.learningplatform.entity.Category;
import ru.polukhin.learningplatform.entity.Course;
import ru.polukhin.learningplatform.entity.Module;
import ru.polukhin.learningplatform.entity.RoleType;
import ru.polukhin.learningplatform.entity.User;
import ru.polukhin.learningplatform.exception.BusinessException;
import ru.polukhin.learningplatform.exception.ResourceNotFoundException;
import ru.polukhin.learningplatform.repository.CategoryRepository;
import ru.polukhin.learningplatform.repository.CourseRepository;
import ru.polukhin.learningplatform.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public List<Course> getAllCourses() {
        log.debug("Fetching all courses");
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        log.debug("Fetching course by id: {}", id);
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }

    @Transactional
    public Course getCourseWithModules(Long id) {
        log.debug("Fetching course with modules by id: {}", id);
        Course course = courseRepository.findByIdWithModules(id);
        if (course == null) {
            throw new ResourceNotFoundException("Course", id);
        }
        // Initialize lazy collections
        course.getModules().size();
        return course;
    }

    @Transactional
    public Course getCourseWithFullStructure(Long id) {
        log.debug("Fetching course with full structure by id: {}", id);
        Course course = courseRepository.findByIdWithFullStructure(id);
        if (course == null) {
            throw new ResourceNotFoundException("Course", id);
        }
        return course;
    }

    public List<Course> getPublishedCourses() {
        log.debug("Fetching all published courses");
        return courseRepository.findByPublishedTrue();
    }

    public List<Course> getCoursesByCategory(Long categoryId) {
        log.debug("Fetching courses by category id: {}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
        return courseRepository.findByCategory(category);
    }

    public List<Course> getCoursesByInstructor(Long instructorId) {
        log.debug("Fetching courses by instructor id: {}", instructorId);
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", instructorId));
        
        if (instructor.getRole() != RoleType.INSTRUCTOR) {
            throw new BusinessException("User is not an instructor");
        }
        
        return courseRepository.findByInstructor(instructor);
    }

    @Transactional
    public Course createCourse(Course course, Long instructorId, Long categoryId) {
        log.info("Creating new course: {}", course.getTitle());
        
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", instructorId));
        
        if (instructor.getRole() != RoleType.INSTRUCTOR) {
            throw new BusinessException("User is not an instructor");
        }
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
        
        course.setInstructor(instructor);
        course.setCategory(category);
        course.setPublished(false);
        
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long id, Course courseDetails) {
        log.info("Updating course with id: {}", id);
        
        Course course = getCourseById(id);
        
        course.setTitle(courseDetails.getTitle());
        course.setDescription(courseDetails.getDescription());
        course.setImageUrl(courseDetails.getImageUrl());
        course.setDurationHours(courseDetails.getDurationHours());
        course.setDifficultyLevel(courseDetails.getDifficultyLevel());
        
        return courseRepository.save(course);
    }

    @Transactional
    public Course publishCourse(Long id) {
        log.info("Publishing course with id: {}", id);
        
        Course course = getCourseById(id);
        course.setPublished(true);
        
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        log.info("Deleting course with id: {}", id);
        Course course = getCourseById(id);
        courseRepository.delete(course);
    }

    @Transactional
    public Module addModuleToCourse(Long courseId, Module module) {
        log.info("Adding module to course id: {}", courseId);
        
        Course course = getCourseById(courseId);
        module.setCourse(course);
        
        if (module.getOrderIndex() == null) {
            module.setOrderIndex(course.getModules().size());
        }
        
        course.getModules().add(module);
        courseRepository.save(course);
        
        return module;
    }
}
