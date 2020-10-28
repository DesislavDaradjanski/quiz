package bg.startit.spring.quiz.controller;

import bg.startit.spring.quiz.api.AnswerApi;
import bg.startit.spring.quiz.dto.AnswerList;
import bg.startit.spring.quiz.dto.AnswerResponse;
import bg.startit.spring.quiz.dto.CreateAnswerRequest;
import bg.startit.spring.quiz.model.Answer;
import bg.startit.spring.quiz.model.Question;
import bg.startit.spring.quiz.model.Quiz;
import bg.startit.spring.quiz.repository.AnswerRepository;
import bg.startit.spring.quiz.repository.QuestionRepository;
import bg.startit.spring.quiz.repository.QuizRepository;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Validated
@RestController
@RequestMapping
public class AnswerController implements AnswerApi {
  // /api/v1/quizzes/{quizId}/questions/{questionId}/answers -  list, create
  // /api/v1/quizzes/{quizId}/questions/{questionId}/answers/{answerId} -  delete, update

  /*
   * Quiz: Java Basics
   * Q1: Which of the following is true
   * - A1. byte is 8 bits
   * - A2. int is 32 bits
   */

  // if we have limit of answers, then do we need paging?
  // when creating answer, we shouldn't exceed a given limit

  final int MAX_NUMBER_OF_ANSWERS = 10;
  @Autowired
  private QuestionRepository questionRepository;

  @Autowired
  private QuizRepository quizRepository;

  @Autowired
  private AnswerRepository answerRepository;


  @Override
  public ResponseEntity<Void> createAnswer(Long quizId, Long questionId,
      @Valid CreateAnswerRequest createAnswerRequest) {
    if (!quizRepository.existsById(quizId)) {
      return ResponseEntity.notFound().build();
    }
    if (!questionRepository.existsById(questionId)) {
      return ResponseEntity.notFound().build();
    }
    // check limit
    // 1. list all answers to the given question
    // 2. if list size exceeds maximum size - return error
    List<Answer> list = answerRepository.findByQuestionId(questionId);
    if (list.size() >= MAX_NUMBER_OF_ANSWERS) {
      return ResponseEntity.badRequest().build();
    }

    // add answer
    Question question = questionRepository.getOne(questionId);
    Answer answer = new Answer();
    answer.setQuestion(question);
    answerRepository.save(answer);

    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> deleteAnswer(Long quizId, Long questionId, Long answerId) {
    if (!answerRepository.existsById(answerId)) {
      return ResponseEntity.notFound().build();
    }
    answerRepository.deleteById(answerId);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<List<AnswerResponse>> listAnswers(Long quizId, Long questionId) {
    if (!quizRepository.existsById(quizId)) {
      return ResponseEntity.notFound().build();
    }
    if (!questionRepository.existsById(questionId)) {
      return ResponseEntity.notFound().build();
    }
    List<Answer> list = answerRepository.findByQuestionId(questionId);
    List<AnswerResponse> response = list.stream()
        .map(AnswerController::toResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<AnswerResponse> updateAnswer(Long quizId, Long questionId, Long answerId,
      @Valid CreateAnswerRequest createAnswerRequest) {
    if (!answerRepository.existsById(answerId)) {
      return ResponseEntity.notFound().build();
    }
    Answer toUpdate = answerRepository.getOne(answerId);
    toUpdate.setDescription(createAnswerRequest.getDescription());
    toUpdate.setScore(createAnswerRequest.getScore());
    toUpdate.setCorrect(createAnswerRequest.getCorrect());
    answerRepository.save(toUpdate);
    return ResponseEntity.ok(toResponse(toUpdate));
  }

  private static AnswerResponse toResponse(Answer toUpdate) {
    return new AnswerResponse()
        .id(toUpdate.getId())
        .description(toUpdate.getDescription())
        .correct(toUpdate.isCorrect())
        .score(toUpdate.getScore());
  }
}
