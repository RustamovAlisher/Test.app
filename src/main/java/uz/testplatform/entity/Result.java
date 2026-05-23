package uz.testplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(nullable = false)
    private Integer correctAnswers;


    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime finishedAt;


    @PrePersist
    protected void onCreate() {
        if (this.resultCode == null) {
            this.resultCode = UUID.randomUUID().toString();
        }
    }
}