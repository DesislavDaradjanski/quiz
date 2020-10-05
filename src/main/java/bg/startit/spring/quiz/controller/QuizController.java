package bg.startit.spring.quiz.controller;

import bg.startit.spring.quiz.model.Quiz;
import bg.startit.spring.quiz.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/quizzes")
public class QuizController {
    @Autowired
    private QuizRepository quizRepository;

    @PostMapping
    public Quiz createQuiz(@RequestBody Quiz quiz) {
        quiz = quizRepository.save(quiz);
        return quiz;
    }

    // /api/v1/quizzes/{id}
    @GetMapping("/{id}")
    public Quiz readQuiz(@PathVariable Long id) {
        Quiz quiz = quizRepository.getOne(id);
        return quiz;
    }

    @GetMapping
    public List<Quiz> listQuizzes() {
        List<Quiz> list = quizRepository.findAll();
        return list;
    }

    @DeleteMapping("/{id}")
    public void deleteQuiz(@PathVariable Long id) {
        quizRepository.deleteById(id);
    }

}
