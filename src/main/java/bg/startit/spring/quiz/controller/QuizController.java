package bg.startit.spring.quiz.controller;

import bg.startit.spring.quiz.api.QuizApi;
import bg.startit.spring.quiz.dto.CreateQuizRequest;
import bg.startit.spring.quiz.dto.QuizList;
import bg.startit.spring.quiz.dto.QuizResponse;
import bg.startit.spring.quiz.model.Quiz;
import bg.startit.spring.quiz.repository.QuizRepository;
import java.net.URI;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class QuizController implements QuizApi {

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
  @Override
  public ResponseEntity<Void> createQuiz(@Valid CreateQuizRequest createQuizRequest) {
    Quiz quiz = new Quiz();
    quiz.setVisible(createQuizRequest.getVisible());
    quiz.setDescription(createQuizRequest.getDescription());
    quiz.setTitle(createQuizRequest.getTitle());
    quiz = quizRepository.save(quiz);

    // POST (data) -> return Redirect to GET link
    URI redirect = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}") // link to /api/v1/quizzes/{id}
        .buildAndExpand(quiz.getId()) // replace {id} with quiz.getId()
        .toUri();
    return ResponseEntity.created(redirect).build();
  }

  @Override
  public ResponseEntity<Void> deleteQuiz(Long quizId) {
    if (!quizRepository.existsById(quizId)) {
      return ResponseEntity.notFound().build();
    }
    quizRepository.deleteById(quizId);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<QuizList> listQuizzes(@Min(0) @Valid Integer page,
      @Min(0) @Max(100) @Valid Integer size) {
    Page<Quiz> list = quizRepository.findAll(PageRequest.of(page, size));
    QuizList quizList = new QuizList()
        .totalPages(list.getTotalPages())
        .totalElements(list.getTotalElements())
        .number(list.getNumber())
        .size(list.getSize())
        .numberOfElements(list.getNumberOfElements())
        .content(list.getContent().stream()
            .map(QuizController::toResponse)
            .collect(Collectors.toList()));
    return ResponseEntity.ok(quizList);
  }

  @Override
  public ResponseEntity<QuizResponse> readQuiz(Long quizId) {
    if (!quizRepository.existsById(quizId)) {
      return ResponseEntity.notFound().build();
    }
    // hibernate returns class, that extends our Quiz and has some additional properties
    // but we don't know how to serialize them.
    Quiz quiz = quizRepository.getOne(quizId);
    // copy quiz, so we can remove the properties, added by hibernate
    return ResponseEntity.ok(toResponse(quiz));
  }

  @Override
  public ResponseEntity<QuizResponse> updateQuiz(Long quizId,
      @Valid CreateQuizRequest req) {
    if (!quizRepository.existsById(quizId)) {
      return ResponseEntity.notFound().build();
    }
    Quiz toUpdate = quizRepository.getOne(quizId);

    // 2. modify the object
    toUpdate.setDescription(req.getDescription());
    toUpdate.setTitle(req.getTitle());
    toUpdate.setVisible(req.getVisible());
    // 3. save the object
    quizRepository.save(toUpdate);

    return ResponseEntity.ok(toResponse(toUpdate));
  }

  private static QuizResponse toResponse(Quiz quiz) {
    return new QuizResponse()
        .id(quiz.getId())
        .title(quiz.getTitle())
        .description(quiz.getDescription())
        .visible(quiz.isVisible());
  }


}
