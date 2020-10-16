package bg.startit.spring.quiz.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userGenerator")
  @SequenceGenerator(name = "userGenerator", initialValue = 100)
  private Long id;

  @Size(min = 5, max = 256)
  @NotNull
  private String name;

  @NotNull
  @Size(max = 1024)
  private char[] passwordHash;

  @Email
  @NotNull
  @Column(unique = true)
  private String email;

  @ManyToMany
  private List<Answer> answers;

}
