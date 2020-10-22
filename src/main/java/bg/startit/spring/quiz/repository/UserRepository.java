package bg.startit.spring.quiz.repository;

import bg.startit.spring.quiz.model.Answer;
import bg.startit.spring.quiz.model.Question;
import bg.startit.spring.quiz.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

}


