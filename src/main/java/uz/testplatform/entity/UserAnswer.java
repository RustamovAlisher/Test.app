package uz.testplatform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;


@Entity
@Table(name = "user_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate

public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Bu javob qaysi Result ga tegishli (qaysi test sessiyasi)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private Result result;


    // Qaysi savol berilgan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;


    // User qaysi variantni tanlagan (null = javob bermagan)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_variant_id", nullable = true)
    private Variant selectedVariant;


    // To'g'rimi? (boshida false, submit'da hisoblanadi)
    @Column(nullable = false)
    private boolean correct;
}