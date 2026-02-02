package ru.polukhin.learningplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question extends BaseEntity {

    @NotBlank(message = "Question text is required")
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private QuestionType type = QuestionType.SINGLE_CHOICE;

    @NotNull
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @NotNull
    @Column(nullable = false)
    @Builder.Default
    private Integer points = 1;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AnswerOption> answerOptions = new ArrayList<>();

    @Override
    public String toString() {
        return "Question{" +
                "id=" + getId() +
                ", type=" + type +
                ", points=" + points +
                '}';
    }
}
