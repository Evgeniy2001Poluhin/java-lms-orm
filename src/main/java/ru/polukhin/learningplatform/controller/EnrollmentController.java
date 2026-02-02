package ru.polukhin.learningplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.polukhin.learningplatform.entity.Enrollment;
import ru.polukhin.learningplatform.service.EnrollmentService;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Course enrollment management APIs")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    @Operation(summary = "Get all enrollments")
    public ResponseEntity<List<Enrollment>> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get enrollment by ID")
    public ResponseEntity<Enrollment> getEnrollmentById(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get enrollments by student")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get enrollments by course")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }

    @GetMapping("/course/{courseId}/count")
    @Operation(summary = "Get active enrollments count for course")
    public ResponseEntity<Long> getActiveEnrollmentsCount(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getActiveEnrollmentsCount(courseId));
    }

    @PostMapping
    @Operation(summary = "Enroll student to course")
    public ResponseEntity<Enrollment> enrollStudent(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        Enrollment enrollment = enrollmentService.enrollStudent(studentId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
    }

    @PutMapping("/{id}/unenroll")
    @Operation(summary = "Unenroll student from course")
    public ResponseEntity<Void> unenrollStudent(@PathVariable Long id) {
        enrollmentService.unenrollStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/progress")
    @Operation(summary = "Update enrollment progress")
    public ResponseEntity<Enrollment> updateProgress(
            @PathVariable Long id,
            @RequestParam Double progressPercentage) {
        return ResponseEntity.ok(enrollmentService.updateProgress(id, progressPercentage));
    }

    @PutMapping("/{id}/grade")
    @Operation(summary = "Set final grade for enrollment")
    public ResponseEntity<Enrollment> setFinalGrade(
            @PathVariable Long id,
            @RequestParam Double grade) {
        return ResponseEntity.ok(enrollmentService.setFinalGrade(id, grade));
    }
}
