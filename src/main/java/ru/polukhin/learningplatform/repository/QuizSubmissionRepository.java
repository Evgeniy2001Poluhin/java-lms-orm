package ru.polukhin.learningplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.polukhin.learningplatform.entity.Quiz;
import ru.polukhin.learningplatform.entity.QuizSubmission;
import ru.polukhin.learningplatform.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    
    Optional<QuizSubmission> findByStudentAndQuiz(User student, Quiz quiz);
    
    List<QuizSubmission> findByStudent(User student);
    
    List<QuizSubmission> findByQuiz(Quiz quiz);
    
    List<QuizSubmission> findByPassedTrue();
    
    @Query("SELECT qs FROM QuizSubmission qs WHERE qs.student.id = :studentId")
    List<QuizSubmission> findByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT qs FROM QuizSubmission qs WHERE qs.quiz.id = :quizId")
    List<QuizSubmission> findByQuizId(@Param("quizId") Long quizId);
    
    @Query("SELECT qs FROM QuizSubmission qs WHERE qs.quiz.module.course.id = :courseId AND qs.student.id = :studentId")
    List<QuizSubmission> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    
    boolean existsByStudentAndQuiz(User student, Quiz quiz);
}
