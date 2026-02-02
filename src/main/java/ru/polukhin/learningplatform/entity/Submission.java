package ru.polukhin.learningplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "assignment_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Column(length = 5000)
    private String content;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "submitted_at", nullable = false)
    @Builder.Default
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.SUBMITTED;

    @Column(name = "score")
    private Integer score;

    @Column(name = "feedback", length = 2000)
    private String feedback;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    @Override
    public String toString() {
        return "Submission{" +
                "id=" + getId() +
                ", status=" + status +
                ", score=" + score +
                '}';
    }
}
