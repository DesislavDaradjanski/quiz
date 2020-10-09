package bg.startit.spring.quiz.controller;

import bg.startit.spring.quiz.model.Quiz;
import bg.startit.spring.quiz.repository.QuizRepository;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.Id;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/quizzes")
public class QuizController {

  @Autowired
  private QuizRepository quizRepository;


  // 1. BAD option
  // POST -> returns Quiz
  // Why bad: object is not cacheable (POST), to long data
  // 2. GOOD (redirect after post)
  // POST -> return id
  // GET <newly-create-object>
  // Why good: objects returned by GET can be cached (by browser, by proxy)
  // Why good: the client may decide that doesn't need the response (e.g. quiz object)
  @PostMapping
  public ResponseEntity<Void> createQuiz(@RequestBody Quiz quiz) {
    quiz = quizRepository.save(quiz);

    // POST (data) -> return Redirect to GET link
    URI redirect = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}") // link to /api/v1/quizzes/{id}
        .buildAndExpand(quiz.getId()) // replace {id} with quiz.getId()
        .toUri();
    return ResponseEntity.created(redirect).build();
  }

  // /api/v1/quizzes/{id}
  @GetMapping("/{id}")
  @Transactional
  public ResponseEntity<Quiz> readQuiz(@PathVariable Long id) {
    if (quizRepository.existsById(id)) {
      // hibernate returns class, that extends our Quiz and has some additional properties
      // but we don't know how to serialize them.
      Quiz quiz = quizRepository.getOne(id);
      // copy quiz, so we can remove the properties, added by hibernate
      Quiz ret = new Quiz(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.isVisible());
      return ResponseEntity.ok(ret);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping
  public Page<Quiz> listQuizzes(
      @PositiveOrZero @Valid @RequestParam(required = false, defaultValue = "0") Integer page,
      @PositiveOrZero @Max(100) @Valid @RequestParam(required = false, defaultValue = "20") Integer size) {
    Page<Quiz> list = quizRepository.findAll(PageRequest.of(page, size));
    return list;
  }

  @DeleteMapping("/{id}")
  public void deleteQuiz(@PathVariable Long id) {
    quizRepository.deleteById(id);
  }

  @PutMapping("/{id}")
  @Transactional
  public ResponseEntity<Void> updateQuiz(@RequestBody Quiz quiz, @PathVariable Long id) {
    if (!quizRepository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    Quiz toUpdate = quizRepository.getOne(id);
    // 2. modify the object
    toUpdate.setDescription(quiz.getDescription());
    toUpdate.setTitle(quiz.getTitle());
    toUpdate.setVisible(quiz.isVisible());
    // 3. save the object
    quizRepository.save(toUpdate);
    URI redirect = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .build()
        .toUri();
    return ResponseEntity
        .status(HttpStatus.FOUND)
        .location(redirect)
        .build();
    // 1. read the object
  }

  // GET /api/v1/quizzes
  // GET /api/v1/quizzes/{id}
  // POST /api/v1/quizzes
  // DELETE /api/v1/quizzes/{id}
  // PUT /api/v1/quizzes/{id}


}
