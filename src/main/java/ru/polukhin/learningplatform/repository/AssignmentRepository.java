package ru.polukhin.learningplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.polukhin.learningplatform.entity.Assignment;
import ru.polukhin.learningplatform.entity.Lesson;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    
    List<Assignment> findByLesson(Lesson lesson);
    
    @Query("SELECT a FROM Assignment a WHERE a.lesson.id = :lessonId")
    List<Assignment> findByLessonId(@Param("lessonId") Long lessonId);
    
    @Query("SELECT a FROM Assignment a WHERE a.lesson.module.course.id = :courseId")
    List<Assignment> findByCourseId(@Param("courseId") Long courseId);
    
    List<Assignment> findByDeadlineBefore(LocalDateTime deadline);
    
    List<Assignment> findByDeadlineAfter(LocalDateTime deadline);
    
    @Query("SELECT a FROM Assignment a LEFT JOIN FETCH a.submissions WHERE a.id = :id")
    Assignment findByIdWithSubmissions(@Param("id") Long id);
}
