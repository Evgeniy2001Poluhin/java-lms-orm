package ru.polukhin.learningplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.polukhin.learningplatform.entity.Assignment;
import ru.polukhin.learningplatform.entity.Submission;
import ru.polukhin.learningplatform.service.AssignmentService;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Tag(name = "Assignments", description = "Assignment and submission management APIs")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @GetMapping
    @Operation(summary = "Get all assignments")
    public ResponseEntity<List<Assignment>> getAllAssignments() {
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get assignment by ID")
    public ResponseEntity<Assignment> getAssignmentById(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getAssignmentById(id));
    }

    @GetMapping("/lesson/{lessonId}")
    @Operation(summary = "Get assignments by lesson")
    public ResponseEntity<List<Assignment>> getAssignmentsByLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByLesson(lessonId));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get assignments by course")
    public ResponseEntity<List<Assignment>> getAssignmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByCourse(courseId));
    }

    @PostMapping
    @Operation(summary = "Create a new assignment")
    public ResponseEntity<Assignment> createAssignment(@Valid @RequestBody Assignment assignment) {
        Assignment created = assignmentService.createAssignment(assignment);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update assignment")
    public ResponseEntity<Assignment> updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody Assignment assignment) {
        return ResponseEntity.ok(assignmentService.updateAssignment(id, assignment));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete assignment")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "Submit assignment")
    public ResponseEntity<Submission> submitAssignment(
            @PathVariable Long id,
            @RequestParam Long studentId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String fileUrl) {
        Submission submission = assignmentService.submitAssignment(id, studentId, content, fileUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }

    @PutMapping("/submissions/{submissionId}/grade")
    @Operation(summary = "Grade submission")
    public ResponseEntity<Submission> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestParam Integer score,
            @RequestParam(required = false) String feedback) {
        return ResponseEntity.ok(assignmentService.gradeSubmission(submissionId, score, feedback));
    }

    @GetMapping("/submissions/student/{studentId}")
    @Operation(summary = "Get submissions by student")
    public ResponseEntity<List<Submission>> getSubmissionsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(assignmentService.getSubmissionsByStudent(studentId));
    }

    @GetMapping("/{id}/submissions")
    @Operation(summary = "Get submissions for assignment")
    public ResponseEntity<List<Submission>> getSubmissionsByAssignment(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getSubmissionsByAssignment(id));
    }
}
