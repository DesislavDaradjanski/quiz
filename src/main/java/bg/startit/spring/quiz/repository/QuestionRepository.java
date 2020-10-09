package bg.startit.spring.quiz.repository;

import bg.startit.spring.quiz.model.Question;
import bg.startit.spring.quiz.model.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuiz(Quiz quiz);
    Page<Question> findByQuiz(Quiz quiz, Pageable pageable);
}
