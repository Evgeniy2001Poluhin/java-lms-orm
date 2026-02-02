package ru.polukhin.learningplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.polukhin.learningplatform.entity.Question;
import ru.polukhin.learningplatform.entity.Quiz;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    List<Question> findByQuizOrderByOrderIndexAsc(Quiz quiz);
    
    @Query("SELECT q FROM Question q WHERE q.quiz.id = :quizId ORDER BY q.orderIndex ASC")
    List<Question> findByQuizId(@Param("quizId") Long quizId);
    
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.answerOptions WHERE q.id = :id")
    Question findByIdWithAnswers(@Param("id") Long id);
}
