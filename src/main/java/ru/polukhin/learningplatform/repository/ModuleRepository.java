package ru.polukhin.learningplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.polukhin.learningplatform.entity.Course;
import ru.polukhin.learningplatform.entity.Module;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    
    List<Module> findByCourseOrderByOrderIndexAsc(Course course);
    
    @Query("SELECT m FROM Module m WHERE m.course.id = :courseId ORDER BY m.orderIndex ASC")
    List<Module> findByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT m FROM Module m LEFT JOIN FETCH m.lessons WHERE m.id = :id")
    Module findByIdWithLessons(@Param("id") Long id);
}
