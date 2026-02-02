package ru.polukhin.learningplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.polukhin.learningplatform.entity.Module;
import ru.polukhin.learningplatform.entity.Quiz;

import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    Optional<Quiz> findByModule(Module module);
    
    @Query("SELECT q FROM Quiz q WHERE q.module.id = :moduleId")
    Optional<Quiz> findByModuleId(@Param("moduleId") Long moduleId);
    
    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :id")
    Quiz findByIdWithQuestions(@Param("id") Long id);
    
    @Query("SELECT DISTINCT q FROM Quiz q " +
           "LEFT JOIN FETCH q.questions qu " +
           "LEFT JOIN FETCH qu.answerOptions " +
           "WHERE q.id = :id")
    Quiz findByIdWithFullStructure(@Param("id") Long id);
}
