package ru.polukhin.learningplatform.entity;
import java.util.Set;
import java.util.LinkedHashSet;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;

@Entity
@Table(name = "modules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Module extends BaseEntity {

    @NotBlank(message = "Module title is required")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @NotNull
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Lesson> lessons = new LinkedHashSet<>();

    // One-to-one relationship with Quiz (optional)
    @OneToOne(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Quiz quiz;

    @Override
    public String toString() {
        return "Module{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", orderIndex=" + orderIndex +
                '}';
    }
}
