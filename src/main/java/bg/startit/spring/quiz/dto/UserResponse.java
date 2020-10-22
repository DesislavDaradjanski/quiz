package bg.startit.spring.quiz.dto;

public class UserResponse {

  private String name;

  public String getName() {
    return name;
  }

  public UserResponse setName(String name) {
    this.name = name;
    return this;
  }
}
