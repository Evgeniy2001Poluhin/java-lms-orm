package ru.polukhin.learningplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz extends BaseEntity {

    @NotBlank(message = "Quiz title is required")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @NotNull
    @Column(name = "passing_score", nullable = false)
    @Builder.Default
    private Integer passingScore = 70;

    @NotNull
    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<QuizSubmission> submissions = new ArrayList<>();

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", passingScore=" + passingScore +
                '}';
    }
}
