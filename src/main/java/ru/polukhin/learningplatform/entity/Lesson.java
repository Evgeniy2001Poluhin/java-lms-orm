package ru.polukhin.learningplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson extends BaseEntity {

    @NotBlank(message = "Lesson title is required")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 5000)
    private String content;

    @NotNull
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private LessonType type = LessonType.TEXT;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Assignment> assignments = new ArrayList<>();

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", orderIndex=" + orderIndex +
                '}';
    }
}
