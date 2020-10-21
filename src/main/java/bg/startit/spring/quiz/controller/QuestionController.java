package bg.startit.spring.quiz.controller;

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
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/api/v1/quizzes/{quizId}/questions")
public class QuestionController {

  // /api/v1/quizzes/{quizId}/questions - list, create
  // /api/v1/quizzes/{quizId}/questions/{questionId} -  read, delete, update

  @Autowired
  private QuestionRepository questionRepository;

  @Autowired
  private QuizRepository quizRepository;

  @PostMapping
  public ResponseEntity<Void> createQuestion(@PathVariable Long quizId,
      @RequestBody Question question) {
    if (!quizRepository.existsById(quizId)) {
      return ResponseEntity.notFound().build();
    }
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

  // "/api/v1/quizzes/{quizId}/questions/{questionId}"
  @GetMapping("/{questionId}")
  @Transactional
  public ResponseEntity<Question> readQuestion(@PathVariable Long quizId,
      @PathVariable Long questionId) {
    if (questionRepository.existsById(questionId)) {
      // hibernate returns class, that extends our Quiz and has some additional properties
      // but we don't know how to serialize them.
      Question quiz = questionRepository.getOne(questionId);
      // copy quiz, so we can remove the properties, added by hibernate
      Question ret = new Question(questionId, quiz.getTitle(), quiz.getDescription(),
          quiz.getType(), null);
      return ResponseEntity.ok(ret);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  //api/v1/quizzes/{quizId}/questions?page=0&size=30
  @GetMapping
  public ResponseEntity<Page<Question>> listQuestion(
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
    return ResponseEntity.ok(list);
  }

  @DeleteMapping("/{questionId}")
  public void deleteQuestion(@PathVariable Long questionId) {
    questionRepository.deleteById(questionId);
  }

  @PutMapping("/{questionId}")
  @Transactional
  public ResponseEntity<Void> updateQuestion(@RequestBody Question question,
      @PathVariable Long questionId) {
    if (!questionRepository.existsById(questionId)) {
      return ResponseEntity.notFound().build();
    }
    Question toUpdate = questionRepository.getOne(questionId);
    // 2. modify the object
    toUpdate.setDescription(question.getDescription());
    toUpdate.setTitle(question.getTitle());
    toUpdate.setType(question.getType());
    // 3. save the object
    questionRepository.save(toUpdate);
    URI redirect = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .build()
        .toUri();
    return ResponseEntity
        .status(HttpStatus.FOUND)
        .location(redirect)
        .build();

  }

}
