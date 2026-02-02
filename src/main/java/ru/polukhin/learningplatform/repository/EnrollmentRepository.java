package ru.polukhin.learningplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.polukhin.learningplatform.entity.Course;
import ru.polukhin.learningplatform.entity.Enrollment;
import ru.polukhin.learningplatform.entity.EnrollmentStatus;
import ru.polukhin.learningplatform.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    
    List<Enrollment> findByStudent(User student);
    
    List<Enrollment> findByCourse(Course course);
    
    List<Enrollment> findByStatus(EnrollmentStatus status);
    
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId")
    List<Enrollment> findByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId")
    List<Enrollment> findByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.status = :status")
    List<Enrollment> findByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") EnrollmentStatus status);
    
    boolean existsByStudentAndCourse(User student, Course course);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = 'ACTIVE'")
    long countActiveEnrollmentsByCourseId(@Param("courseId") Long courseId);
}
