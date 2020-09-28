package bg.startit.spring.quiz.repository;

import bg.startit.spring.quiz.model.Answer;
import bg.startit.spring.quiz.model.Question;
import bg.startit.spring.quiz.model.Quiz;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionException;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class QuestionRepositoryTest {
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private QuestionRepository questionRepository;
    private Quiz quiz;
    @Autowired
    private QuizRepository quizRepository;

    //positive test scenarios
    // - adding valid question
    @Test
    void with_validData_then_Save_must_succeed() {
        Question question = new Question(null, "with_validData_then_Save_must_succeed",
                null, Question.Type.SingleChoice,quiz);
        questionRepository.save(question);
    }

    // - with valid answers
    @Test
    void with_validAnswers_then_save_must_succeed() {
        Question question = new Question(null, "with_validData_with_validAnswers",
                null, Question.Type.SingleChoice,quiz);
        questionRepository.save(question);
        Answer answer = new Answer(null, "Answer1", true, 2, question);
        Answer answer2 = new Answer(null, "Answer2", false, 2, question);
        Answer answer3 = new Answer(null, "Answer3", false, 2, question);
        Answer answer4 = new Answer(null, "Answer4", false, 2, question);
        answerRepository.saveAll(List.of(answer, answer2, answer3, answer4));
        List<Answer> answers = answerRepository.findByQuestion(question);
        assertThat(answers, hasItems(answer));
        assertThat(answers, hasItems(answer2));
        assertThat(answers, hasItems(answer3));
        assertThat(answers, hasItems(answer4));
        assertEquals(4, answers.size());
        Question question2 = new Question(null, "with_validData_with_no_answers",
                null, Question.Type.SingleChoice,quiz);
        questionRepository.save(question2);
        answers = answerRepository.findByQuestion(question2);
        assertTrue(answers.isEmpty());

    }
    //negative test scenarios
    // - adding invalid question
    @Test
    void with_invalidData_then_save_must_fail(){
        //Title is null
        assertThrows(TransactionException.class, () -> {
            Question question = new Question(null, null, null, Question.Type.SingleChoice,quiz);
            questionRepository.save(question);
        });
        //Title is short
        assertThrows(TransactionException.class, () -> {
            Question question = new Question(null, "ABCD", null, Question.Type.SingleChoice,quiz);
            questionRepository.save(question);
        });
        //Title is long
        assertThrows(TransactionException.class, () -> {
            char[] title = new char[1025];
            Arrays.fill(title, 'A');
            Question question = new Question(null, new String(title), null, Question.Type.SingleChoice,quiz);
            questionRepository.save(question);
        });
        //Description is long
        assertThrows(TransactionException.class, () -> {
            char[] desc = new char[1025];
            Arrays.fill(desc, 'A');
            Question question = new Question(null, "Alabala", new String(desc), Question.Type.MultipleChoices,quiz);
            questionRepository.save(question);
        });
    }
    // - valid question + answer with no reference
    @Test
    void when_answer_has_no_question_then_save_must_fail(){
        Question question = new Question(null, "with_validData_with_validAnswers",
                null, Question.Type.SingleChoice,quiz);
        questionRepository.save(question);
        Answer answer = new Answer(null, "Alabala", true, 5, null);
        assertThrows(TransactionException.class, () -> {
            answerRepository.save(answer);

        });
    }

    @BeforeEach
    void setUp() {
        quiz = new Quiz(null, "Alabalala", null, false);
        quizRepository.save(quiz);
    }

    @AfterEach
    void tearDown() {
        answerRepository.deleteAll();
        questionRepository.deleteAll();
        quizRepository.delete(quiz);
    }
}
