package bg.startit.spring.quiz.controller;

import bg.startit.spring.quiz.api.QuestionApi;
import bg.startit.spring.quiz.dto.CreateQuestionRequest;
import bg.startit.spring.quiz.dto.QuestionList;
import bg.startit.spring.quiz.dto.QuestionResponse;
import bg.startit.spring.quiz.model.Question;
import bg.startit.spring.quiz.model.Quiz;
import bg.startit.spring.quiz.repository.QuestionRepository;
import bg.startit.spring.quiz.repository.QuizRepository;
import java.net.URI;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class QuestionController implements QuestionApi {

  // /api/v1/quizzes/{quizId}/questions - list, create
  // /api/v1/quizzes/{quizId}/questions/{questionId} -  read, delete, update

  @Autowired
  private QuestionRepository questionRepository;

  @Autowired
  private QuizRepository quizRepository;


  // "/api/v1/quizzes/{quizId}/questions/{questionId}"
  @Override
  public ResponseEntity<QuestionResponse> readQuestion(@PathVariable Long quizId,
      @PathVariable Long questionId) {
    if (questionRepository.existsById(questionId)) {
      // hibernate returns class, that extends our Quiz and has some additional properties
      // but we don't know how to serialize them.
      Question question = questionRepository.getOne(questionId);
      // copy quiz, so we can remove the properties, added by hibernate
      QuestionResponse ret = toResponse(question);

      return ResponseEntity.ok(ret);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  private static QuestionResponse toResponse(Question question) {
    QuestionResponse ret = new QuestionResponse()
        .id(question.getId())
        .title(question.getTitle())
        .description(question.getDescription())
        .type(question.getType().name());
    return ret;
  }

  @Override
  public ResponseEntity<QuestionResponse> updateQuestion(Long quizId, Long questionId,
      @Valid CreateQuestionRequest createQuestionRequest) {
    if (!questionRepository.existsById(questionId)) {
      return ResponseEntity.notFound().build();
    }
    Question question = new Question();
    Question toUpdate = questionRepository.getOne(questionId);
    // 2. modify the object
    toUpdate.setDescription(question.getDescription());
    toUpdate.setTitle(question.getTitle());
    toUpdate.setType(question.getType());
    // 3. save the object
    questionRepository.save(toUpdate);

    return ResponseEntity.ok(toResponse(toUpdate));
  }

  @Override
  public ResponseEntity<Void> createQuestion(Long quizId,
      @Valid CreateQuestionRequest createQuestionRequest) {
    if (!quizRepository.existsById(quizId)) {
      return ResponseEntity.notFound().build();
    }
    Question question = new Question();
    Quiz quiz = quizRepository.getOne(quizId);
    question.setQuiz(quiz);
    question = questionRepository.save(question);

    // POST (data) -> return Redirect to GET link
    URI redirect = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}") // link to "/api/v1/quizzes/{quizId}/questions/{questionId}"
        .buildAndExpand(question.getId()) // replace {id} with question.getId()
        .toUri();
    return ResponseEntity.created(redirect).build();
  }

  @Override
  public ResponseEntity<Void> deleteQuestion(Long quizId, Long questionId) {
    questionRepository.deleteById(questionId);
    return ResponseEntity.ok().build();
  }

  //api/v1/quizzes/{quizId}/questions?page=0&size=30
  @Override
  public ResponseEntity<QuestionList> listQuestion(
      @PathVariable Long quizId,
      @PositiveOrZero @Valid @RequestParam(required = false, defaultValue = "0") Integer page,
      @PositiveOrZero @Max(100) @Valid @RequestParam(required = false, defaultValue = "20") Integer size
  ) {
    if (!quizRepository.existsById(quizId)) {
      return ResponseEntity.notFound().build();
    }

//    Page<Question> findByQuiz(Quiz quiz, Pageable pageable);
    Page<Question> list = questionRepository.findByQuiz(
        quizRepository.getOne(quizId),
        PageRequest.of(page, size));
    QuestionList response = new QuestionList()
        .totalPages(list.getTotalPages())
        .totalElements(list.getTotalElements())
        .number(list.getNumber())
        .size(list.getSize())
        .numberOfElements(list.getNumberOfElements());

    return ResponseEntity.ok(response);
  }
}
