package bg.startit.spring.quiz.repository;

import bg.startit.spring.quiz.model.Answer;
import bg.startit.spring.quiz.model.Question;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AnswerRepositoryTest {
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private QuestionRepository questionRepository;
    private Question question;

    @Test
    void with_NullId_then_Save_must_Succeed() {
        Answer answer = new Answer(null, "Alabala", true, 10, question);
        // save - (answer is unmanaged object)
        answer = answerRepository.save(answer);
        assertNotNull(answer);
        assertEquals(1, answerRepository.count());
        assertNotNull(answer.getId());
    }

    @Test
    void with_SetId_then_Save_must_Succeed() {
        Answer answer = new Answer(10L, "Alabala", true, 10, question);
        assertEquals(10l, answer.getId());
        // save - (answer is unmanaged object)
        answer = answerRepository.save(answer);
        assertNotNull(answer);
        assertEquals(1, answerRepository.count());
        assertTrue(answer.getId() >= 100L);
        assertThat("Must be > 100", answer.getId(), greaterThanOrEqualTo(100L));
    }

    // when(precondition) - then(action) - what(result)
    // with(precondition) - then(action) - what(result)
    // what = must/may/should
    @Test
    void with_NullDescription_then_Save_must_Fail() {
        assertThrows(RuntimeException.class, () -> {
            Answer answer = new Answer(null, null, true, 5, question);
            answer = answerRepository.save(answer);
        });
    }

    @Test
    void with_InvalidScore_then_Save_must_Fail() {
        assertThrows(RuntimeException.class, () -> {
            Answer answer = new Answer(null, "Alabala", true, 20, question);
            answer = answerRepository.save(answer);
        });
        assertThrows(RuntimeException.class, () -> {
            Answer answer = new Answer(null, "Alabala", true, 0, question);
            answer = answerRepository.save(answer);
        });
    }

    @BeforeEach
    void setUp() {
        question = new Question(null, "Alabala", null, Question.Type.MultipleChoices);
        questionRepository.save(question);
    }

    @AfterEach
    void tearDown() {
        answerRepository.deleteAll();
    }
}