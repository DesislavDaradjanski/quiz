package bg.startit.spring.quiz.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

//save to DB
@Entity
//Make the Class as Java Bean
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Answer {
    //DB table unique ID
    @Id
    //Auto-generate ID
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answerGenerator")
    @SequenceGenerator(name = "answerGenerator", initialValue = 100)
    private Long id;
    @NotNull //javax.validation === nullable = false
    @Size(min = 5, max = 512)  //javax.validation === length = 512
    @Column(nullable = false,length = 512) //JPA / DB
    private String description;
    private boolean correct;
    @Min(0)
    @Max(10)
    private int score;
    @ManyToOne
    @NotNull
    private Question question;
}
