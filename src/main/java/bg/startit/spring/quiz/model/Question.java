package bg.startit.spring.quiz.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

//save to DB
@Entity
//Make the Class as Java Bean
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor

public class Question {

    public enum Type {
        SingleChoice,
        MultipleChoices
    }
    @Id
    //Auto-generate ID
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questionGenerator")
    @SequenceGenerator(name = "questionGenerator", initialValue = 100)
    private Long id;
    @Size (min = 5,max = 1024)
    @NotNull
    private String title;
    @Size (max = 256)
    private String description;
    @NotNull
    private Type type = Type.SingleChoice;
    @ManyToOne
    @NotNull
    private Quiz quiz;


}
