package ru.polukhin.learningplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.polukhin.learningplatform.entity.Course;
import ru.polukhin.learningplatform.entity.Module;
import ru.polukhin.learningplatform.service.CourseService;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management APIs")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "Get all courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/{id}/with-modules")
    @Operation(summary = "Get course with modules")
    public ResponseEntity<Course> getCourseWithModules(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseWithModules(id));
    }

    @GetMapping("/{id}/full")
    @Operation(summary = "Get course with full structure")
    public ResponseEntity<Course> getCourseWithFullStructure(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseWithFullStructure(id));
    }

    @GetMapping("/published")
    @Operation(summary = "Get all published courses")
    public ResponseEntity<List<Course>> getPublishedCourses() {
        return ResponseEntity.ok(courseService.getPublishedCourses());
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get courses by category")
    public ResponseEntity<List<Course>> getCoursesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(courseService.getCoursesByCategory(categoryId));
    }

    @GetMapping("/instructor/{instructorId}")
    @Operation(summary = "Get courses by instructor")
    public ResponseEntity<List<Course>> getCoursesByInstructor(@PathVariable Long instructorId) {
        return ResponseEntity.ok(courseService.getCoursesByInstructor(instructorId));
    }

    @PostMapping
    @Operation(summary = "Create a new course")
    public ResponseEntity<Course> createCourse(
            @Valid @RequestBody Course course,
            @RequestParam Long instructorId,
            @RequestParam Long categoryId) {
        Course created = courseService.createCourse(course, instructorId, categoryId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update course")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody Course course) {
        return ResponseEntity.ok(courseService.updateCourse(id, course));
    }

    @PutMapping("/{id}/publish")
    @Operation(summary = "Publish course")
    public ResponseEntity<Course> publishCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.publishCourse(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete course")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{courseId}/modules")
    @Operation(summary = "Add module to course")
    public ResponseEntity<Module> addModule(
            @PathVariable Long courseId,
            @Valid @RequestBody Module module) {
        Module created = courseService.addModuleToCourse(courseId, module);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
