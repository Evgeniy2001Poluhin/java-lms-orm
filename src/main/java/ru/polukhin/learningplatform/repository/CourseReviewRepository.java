package ru.polukhin.learningplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.polukhin.learningplatform.entity.Course;
import ru.polukhin.learningplatform.entity.CourseReview;
import ru.polukhin.learningplatform.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    
    Optional<CourseReview> findByStudentAndCourse(User student, Course course);
    
    List<CourseReview> findByCourse(Course course);
    
    List<CourseReview> findByStudent(User student);
    
    @Query("SELECT cr FROM CourseReview cr WHERE cr.course.id = :courseId")
    List<CourseReview> findByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT AVG(cr.rating) FROM CourseReview cr WHERE cr.course.id = :courseId")
    Double getAverageRatingByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(cr) FROM CourseReview cr WHERE cr.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);
    
    boolean existsByStudentAndCourse(User student, Course course);
}
