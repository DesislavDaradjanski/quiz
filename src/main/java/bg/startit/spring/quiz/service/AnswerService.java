package bg.startit.spring.quiz.service;

import bg.startit.spring.quiz.controller.AnswerController;
import bg.startit.spring.quiz.exception.TooManyAnswersException;
import bg.startit.spring.quiz.model.Answer;
import bg.startit.spring.quiz.model.Question;
import bg.startit.spring.quiz.repository.AnswerRepository;
import bg.startit.spring.quiz.repository.QuestionRepository;
import bg.startit.spring.quiz.repository.QuizRepository;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class AnswerService {

  // if we have limit of answers, then do we need paging?
  // when creating answer, we shouldn't exceed a given limit

  final int MAX_NUMBER_OF_ANSWERS = 10;
  @Autowired
  private AnswerRepository answerRepository;

  @Autowired
  private QuizRepository quizRepository;

  @Autowired
  private QuestionRepository questionRepository;

  public boolean exists(Long quizId, Long questionId) {

    return quizRepository.existsById(quizId) && questionRepository.existsById(questionId);
  }

  public Answer create(Long questionId) throws TooManyAnswersException {
    // check limit
    // 1. list all answers to the given question
    // 2. if list size exceeds maximum size - return error
    List<Answer> list = answerRepository.findByQuestionId(questionId);
    if (list.size() >= MAX_NUMBER_OF_ANSWERS) {
      throw new TooManyAnswersException(
          String.format("Can't create more answers to %d, because maximum allowed is %d",
              questionId, MAX_NUMBER_OF_ANSWERS));
    }

    // add answer
    Question question = questionRepository.getOne(questionId);
    Answer answer = new Answer();
    answer.setQuestion(question);
    return answerRepository.save(answer);

  }

  public boolean delete(Long answerId) {
    if (!answerRepository.existsById(answerId)) {
      return false;
    }
    answerRepository.deleteById(answerId);
    return true;
  }

}
