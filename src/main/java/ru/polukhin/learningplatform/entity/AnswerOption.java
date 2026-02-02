package ru.polukhin.learningplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "answer_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerOption extends BaseEntity {

    @NotBlank(message = "Option text is required")
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String optionText;

    @NotNull
    @Column(name = "is_correct", nullable = false)
    @Builder.Default
    private Boolean isCorrect = false;

    @NotNull
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Override
    public String toString() {
        return "AnswerOption{" +
                "id=" + getId() +
                ", isCorrect=" + isCorrect +
                '}';
    }
}
