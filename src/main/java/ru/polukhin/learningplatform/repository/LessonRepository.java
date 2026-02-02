package ru.polukhin.learningplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.polukhin.learningplatform.entity.Lesson;
import ru.polukhin.learningplatform.entity.LessonType;
import ru.polukhin.learningplatform.entity.Module;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    
    List<Lesson> findByModuleOrderByOrderIndexAsc(Module module);
    
    @Query("SELECT l FROM Lesson l WHERE l.module.id = :moduleId ORDER BY l.orderIndex ASC")
    List<Lesson> findByModuleId(@Param("moduleId") Long moduleId);
    
    List<Lesson> findByType(LessonType type);
    
    @Query("SELECT l FROM Lesson l LEFT JOIN FETCH l.assignments WHERE l.id = :id")
    Lesson findByIdWithAssignments(@Param("id") Long id);
}
