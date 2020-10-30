package bg.startit.spring.quiz.controller;

import bg.startit.spring.quiz.api.AnswerApi;
import bg.startit.spring.quiz.dto.AnswerResponse;
import bg.startit.spring.quiz.dto.CreateAnswerRequest;
import bg.startit.spring.quiz.exception.TooManyAnswersException;
import bg.startit.spring.quiz.model.Answer;
import bg.startit.spring.quiz.model.Question;
import bg.startit.spring.quiz.repository.AnswerRepository;
import bg.startit.spring.quiz.repository.QuestionRepository;
import bg.startit.spring.quiz.repository.QuizRepository;
import bg.startit.spring.quiz.service.AnswerService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  private AnswerService answerService;


  @Override
  public ResponseEntity<Void> createAnswer(Long quizId, Long questionId,
      @Valid CreateAnswerRequest createAnswerRequest) {
    if (!answerService.exists(quizId, questionId)) {
      return ResponseEntity.notFound().build();
    }

    try {
      answerService.create(questionId);
    } catch (TooManyAnswersException e) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok().build();

  }

  @Override
  public ResponseEntity<Void> deleteAnswer(Long quizId, Long questionId, Long answerId) {
    if (!answerService.delete(answerId)) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok().build();
    }
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
