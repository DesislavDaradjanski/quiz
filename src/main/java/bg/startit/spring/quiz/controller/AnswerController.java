package bg.startit.spring.quiz.controller;

import bg.startit.spring.quiz.model.Answer;
import bg.startit.spring.quiz.model.Question;
import bg.startit.spring.quiz.model.Quiz;
import bg.startit.spring.quiz.repository.AnswerRepository;
import bg.startit.spring.quiz.repository.QuestionRepository;
import bg.startit.spring.quiz.repository.QuizRepository;
import java.net.URI;
import java.util.List;
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
@RequestMapping("/api/v1/quizzes/{quizId}/questions/{questionId}/answers")
public class AnswerController {
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

  // /api/v1/quizzes/{quizId}/questions/{questionId}/answers
  @GetMapping
  public ResponseEntity<List<Answer>> listAnswer(
      @PathVariable Long quizId,
      @PathVariable Long questionId) {
    if (!quizRepository.existsById(quizId)) {
      return ResponseEntity.notFound().build();
    }
    if (!questionRepository.existsById(questionId)) {
      return ResponseEntity.notFound().build();
    }
    List<Answer> list = answerRepository.findByQuestionId(questionId);

    return ResponseEntity.ok(list);
  }

  // /api/v1/quizzes/{quizId}/questions/{questionId}/answers
  @PostMapping
  public ResponseEntity<Void> createAnswer(
      @PathVariable Long quizId,
      @PathVariable Long questionId,
      @RequestBody Answer answer) {
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
    answer.setQuestion(question);
    answerRepository.save(answer);

    return ResponseEntity.ok().build();
  }

  // /api/v1/quizzes/{quizId}/questions/{questionId}/answers/{answerId}
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAnswer(
      @PathVariable Long quizId,
      @PathVariable Long questionId,
      @PathVariable Long answerId) {
    if (!answerRepository.existsById(answerId)) {
      return ResponseEntity.notFound().build();
    }
    answerRepository.deleteById(answerId);
    return ResponseEntity.ok().build();
  }


  // /api/v1/quizzes/{quizId}/questions/{questionId}/answers/{answerId}
  @PutMapping("/{id}")
  @Transactional
  public ResponseEntity<Void> updateAnswer(
      @PathVariable Long quizId,
      @PathVariable Long questionId,
      @PathVariable Long answerId,
      @RequestBody Answer answer) {
    if (!answerRepository.existsById(answerId)) {
      return ResponseEntity.notFound().build();
    }
    Answer toUpdate = answerRepository.getOne(answerId);
    toUpdate.setDescription(answer.getDescription());
    toUpdate.setCorrect(answer.isCorrect());
    toUpdate.setScore(answer.getScore());

    answerRepository.save(toUpdate);
    return ResponseEntity.noContent().build();
  }
}
