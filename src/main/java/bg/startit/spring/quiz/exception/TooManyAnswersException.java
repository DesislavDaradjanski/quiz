package bg.startit.spring.quiz.exception;

public class TooManyAnswersException extends Exception {

  public TooManyAnswersException(String message) {
    super(message);
  }
}
