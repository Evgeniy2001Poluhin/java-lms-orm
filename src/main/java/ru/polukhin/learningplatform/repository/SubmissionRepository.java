package ru.polukhin.learningplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.polukhin.learningplatform.entity.Assignment;
import ru.polukhin.learningplatform.entity.Submission;
import ru.polukhin.learningplatform.entity.SubmissionStatus;
import ru.polukhin.learningplatform.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    
    Optional<Submission> findByStudentAndAssignment(User student, Assignment assignment);
    
    List<Submission> findByStudent(User student);
    
    List<Submission> findByAssignment(Assignment assignment);
    
    List<Submission> findByStatus(SubmissionStatus status);
    
    @Query("SELECT s FROM Submission s WHERE s.student.id = :studentId")
    List<Submission> findByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT s FROM Submission s WHERE s.assignment.id = :assignmentId")
    List<Submission> findByAssignmentId(@Param("assignmentId") Long assignmentId);
    
    @Query("SELECT s FROM Submission s WHERE s.assignment.lesson.module.course.id = :courseId AND s.student.id = :studentId")
    List<Submission> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    
    boolean existsByStudentAndAssignment(User student, Assignment assignment);
}
