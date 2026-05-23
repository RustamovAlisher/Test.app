package uz.testplatform.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.testplatform.enums.TestLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestLevel level;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private Integer questionCount;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}