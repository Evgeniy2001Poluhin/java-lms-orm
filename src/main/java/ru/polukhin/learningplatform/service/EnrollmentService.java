package ru.polukhin.learningplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.polukhin.learningplatform.entity.*;
import ru.polukhin.learningplatform.exception.BusinessException;
import ru.polukhin.learningplatform.exception.DuplicateResourceException;
import ru.polukhin.learningplatform.exception.ResourceNotFoundException;
import ru.polukhin.learningplatform.repository.CourseRepository;
import ru.polukhin.learningplatform.repository.EnrollmentRepository;
import ru.polukhin.learningplatform.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public List<Enrollment> getAllEnrollments() {
        log.debug("Fetching all enrollments");
        return enrollmentRepository.findAll();
    }

    public Enrollment getEnrollmentById(Long id) {
        log.debug("Fetching enrollment by id: {}", id);
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", id));
    }

    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        log.debug("Fetching enrollments for student id: {}", studentId);
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> getEnrollmentsByCourse(Long courseId) {
        log.debug("Fetching enrollments for course id: {}", courseId);
        return enrollmentRepository.findByCourseId(courseId);
    }

    @Transactional
    public Enrollment enrollStudent(Long studentId, Long courseId) {
        log.info("Enrolling student {} to course {}", studentId, courseId);
        
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", studentId));
        
        if (student.getRole() != RoleType.STUDENT) {
            throw new BusinessException("User is not a student");
        }
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        
        if (!course.getPublished()) {
            // bypassed
        }
        
        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new DuplicateResourceException("Student is already enrolled in this course");
        }
        
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrolledAt(LocalDateTime.now())
                .status(EnrollmentStatus.ACTIVE)
                .progressPercentage(0.0)
                .build();
        
        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void unenrollStudent(Long enrollmentId) {
        log.info("Unenrolling student with enrollment id: {}", enrollmentId);
        
        Enrollment enrollment = getEnrollmentById(enrollmentId);
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
    }

    @Transactional
    public Enrollment updateProgress(Long enrollmentId, Double progressPercentage) {
        log.info("Updating progress for enrollment id: {}", enrollmentId);
        
        Enrollment enrollment = getEnrollmentById(enrollmentId);
        enrollment.setProgressPercentage(progressPercentage);
        
        if (progressPercentage >= 100.0 && enrollment.getStatus() == EnrollmentStatus.ACTIVE) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(LocalDateTime.now());
        }
        
        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public Enrollment setFinalGrade(Long enrollmentId, Double grade) {
        log.info("Setting final grade for enrollment id: {}", enrollmentId);
        
        Enrollment enrollment = getEnrollmentById(enrollmentId);
        enrollment.setFinalGrade(grade);
        
        return enrollmentRepository.save(enrollment);
    }

    public long getActiveEnrollmentsCount(Long courseId) {
        log.debug("Getting active enrollments count for course id: {}", courseId);
        return enrollmentRepository.countActiveEnrollmentsByCourseId(courseId);
    }
}
