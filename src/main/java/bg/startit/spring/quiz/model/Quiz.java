package bg.startit.spring.quiz.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
    @Id
    //Auto-generate ID
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quizGenerator")
    @SequenceGenerator(name = "quizGenerator", initialValue = 100)
    private Long id;
    @Size(min = 5,max = 1024)
    @NotNull
    private String title;
    @Size (max = 1024)
    private String description;
    private boolean visible;


}
