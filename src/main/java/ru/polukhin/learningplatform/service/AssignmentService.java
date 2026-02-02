package ru.polukhin.learningplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.polukhin.learningplatform.entity.*;
import ru.polukhin.learningplatform.exception.BusinessException;
import ru.polukhin.learningplatform.exception.DuplicateResourceException;
import ru.polukhin.learningplatform.exception.ResourceNotFoundException;
import ru.polukhin.learningplatform.repository.AssignmentRepository;
import ru.polukhin.learningplatform.repository.SubmissionRepository;
import ru.polukhin.learningplatform.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public List<Assignment> getAllAssignments() {
        log.debug("Fetching all assignments");
        return assignmentRepository.findAll();
    }

    public Assignment getAssignmentById(Long id) {
        log.debug("Fetching assignment by id: {}", id);
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", id));
    }

    public List<Assignment> getAssignmentsByLesson(Long lessonId) {
        log.debug("Fetching assignments for lesson id: {}", lessonId);
        return assignmentRepository.findByLessonId(lessonId);
    }

    public List<Assignment> getAssignmentsByCourse(Long courseId) {
        log.debug("Fetching assignments for course id: {}", courseId);
        return assignmentRepository.findByCourseId(courseId);
    }

    @Transactional
    public Assignment createAssignment(Assignment assignment) {
        log.info("Creating new assignment: {}", assignment.getTitle());
        return assignmentRepository.save(assignment);
    }

    @Transactional
    public Assignment updateAssignment(Long id, Assignment assignmentDetails) {
        log.info("Updating assignment with id: {}", id);
        
        Assignment assignment = getAssignmentById(id);
        assignment.setTitle(assignmentDetails.getTitle());
        assignment.setDescription(assignmentDetails.getDescription());
        assignment.setMaxScore(assignmentDetails.getMaxScore());
        assignment.setDeadline(assignmentDetails.getDeadline());
        
        return assignmentRepository.save(assignment);
    }

    @Transactional
    public void deleteAssignment(Long id) {
        log.info("Deleting assignment with id: {}", id);
        Assignment assignment = getAssignmentById(id);
        assignmentRepository.delete(assignment);
    }

    @Transactional
    public Submission submitAssignment(Long assignmentId, Long studentId, String content, String fileUrl) {
        log.info("Submitting assignment {} by student {}", assignmentId, studentId);
        
        Assignment assignment = getAssignmentById(assignmentId);
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", studentId));
        
        if (student.getRole() != RoleType.STUDENT) {
            throw new BusinessException("User is not a student");
        }
        
        if (submissionRepository.existsByStudentAndAssignment(student, assignment)) {
            throw new DuplicateResourceException("Assignment already submitted by this student");
        }
        
        if (assignment.getDeadline() != null && LocalDateTime.now().isAfter(assignment.getDeadline())) {
            throw new BusinessException("Assignment deadline has passed");
        }
        
        Submission submission = Submission.builder()
                .student(student)
                .assignment(assignment)
                .content(content)
                .fileUrl(fileUrl)
                .submittedAt(LocalDateTime.now())
                .status(SubmissionStatus.SUBMITTED)
                .build();
        
        return submissionRepository.save(submission);
    }

    @Transactional
    public Submission gradeSubmission(Long submissionId, Integer score, String feedback) {
        log.info("Grading submission id: {}", submissionId);
        
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", submissionId));
        
        if (score > submission.getAssignment().getMaxScore()) {
            throw new BusinessException("Score cannot exceed maximum score");
        }
        
        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setStatus(SubmissionStatus.GRADED);
        submission.setGradedAt(LocalDateTime.now());
        
        return submissionRepository.save(submission);
    }

    public List<Submission> getSubmissionsByStudent(Long studentId) {
        log.debug("Fetching submissions for student id: {}", studentId);
        return submissionRepository.findByStudentId(studentId);
    }

    public List<Submission> getSubmissionsByAssignment(Long assignmentId) {
        log.debug("Fetching submissions for assignment id: {}", assignmentId);
        return submissionRepository.findByAssignmentId(assignmentId);
    }
}
