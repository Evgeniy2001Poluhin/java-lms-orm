package ru.polukhin.learningplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.polukhin.learningplatform.entity.Category;
import ru.polukhin.learningplatform.entity.Course;
import ru.polukhin.learningplatform.entity.DifficultyLevel;
import ru.polukhin.learningplatform.entity.User;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByCategory(Category category);
    
    List<Course> findByInstructor(User instructor);
    
    List<Course> findByPublishedTrue();
    
    List<Course> findByDifficultyLevel(DifficultyLevel level);
    
    @Query("SELECT c FROM Course c WHERE c.category.id = :categoryId AND c.published = true")
    List<Course> findPublishedCoursesByCategory(@Param("categoryId") Long categoryId);
    
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.modules WHERE c.id = :id")
    Course findByIdWithModules(@Param("id") Long id);
    
    @Query("SELECT DISTINCT c FROM Course c " +
           "LEFT JOIN FETCH c.modules m " +
           "LEFT JOIN FETCH m.lessons " +
           "WHERE c.id = :id")
    Course findByIdWithFullStructure(@Param("id") Long id);
}
