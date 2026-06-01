package uz.testplatform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String resultCode;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;


    @Column(nullable = false)
    private Integer totalQuestions;


    // Boshida 0, submit qilganda yangilanadi
    @Column(nullable = false)
    private Integer correctAnswers;


    // Boshida 0, submit qilganda yangilanadi
    @Column(nullable = false)
    private Integer score;


    @Column(nullable = false)
    private LocalDateTime startedAt;


    // Boshida NULL, submit qilganda yangilanadi
    @Column(nullable = true)
    private LocalDateTime finishedAt;


    // User test boshlaganda generatsiya qilingan savollar
    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    @Builder.Default
    private List<UserAnswer> answers = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        if (this.resultCode == null) {
            this.resultCode = UUID.randomUUID().toString();
        }
    }
}