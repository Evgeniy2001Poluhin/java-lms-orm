package ru.polukhin.learningplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.polukhin.learningplatform.entity.AnswerOption;
import ru.polukhin.learningplatform.entity.Question;

import java.util.List;

@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
    
    List<AnswerOption> findByQuestionOrderByOrderIndexAsc(Question question);
    
    @Query("SELECT a FROM AnswerOption a WHERE a.question.id = :questionId ORDER BY a.orderIndex ASC")
    List<AnswerOption> findByQuestionId(@Param("questionId") Long questionId);
    
    @Query("SELECT a FROM AnswerOption a WHERE a.question.id = :questionId AND a.isCorrect = true")
    List<AnswerOption> findCorrectAnswersByQuestionId(@Param("questionId") Long questionId);
}
