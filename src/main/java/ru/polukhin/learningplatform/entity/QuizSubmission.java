package ru.polukhin.learningplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_submissions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "quiz_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmission extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @NotNull
    @Column(nullable = false)
    private Integer score;

    @NotNull
    @Column(name = "max_score", nullable = false)
    private Integer maxScore;

    @NotNull
    @Column(name = "percentage_score", nullable = false)
    private Double percentageScore;

    @NotNull
    @Column(nullable = false)
    @Builder.Default
    private Boolean passed = false;

    @Column(name = "submitted_at", nullable = false)
    @Builder.Default
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Column(name = "time_taken_minutes")
    private Integer timeTakenMinutes;

    @Override
    public String toString() {
        return "QuizSubmission{" +
                "id=" + getId() +
                ", score=" + score +
                ", passed=" + passed +
                '}';
    }
}
