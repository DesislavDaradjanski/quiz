package bg.startit.spring.quiz.repository;

import bg.startit.spring.quiz.model.Answer;
import bg.startit.spring.quiz.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestion(Question question);
    List<Answer> findByQuestionId(Long questionId);
}
