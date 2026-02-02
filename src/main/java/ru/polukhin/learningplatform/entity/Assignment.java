package ru.polukhin.learningplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment extends BaseEntity {

    @NotBlank(message = "Assignment title is required")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 3000)
    private String description;

    @NotNull
    @Column(name = "max_score", nullable = false)
    @Builder.Default
    private Integer maxScore = 100;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Submission> submissions = new ArrayList<>();

    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", maxScore=" + maxScore +
                '}';
    }
}
