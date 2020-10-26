package bg.startit.spring.quiz.validation;

import static org.junit.jupiter.api.Assertions.*;

import bg.startit.spring.quiz.dto.ChangePasswordRequest;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import lombok.Data;
import org.junit.jupiter.api.Test;

class PasswordConstraintValidatorTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  // test valid password
  // test invalid password
  // - no lower
  // - no upper
  // - no digit
  // - no special
  // - has space
  // - too small

  @Data
  class PasswordBean {
    @ValidPassword
    private String password;
  }


  @Test
  public void with_valid_password_validation_should_pass() {
    Set<ConstraintViolation<PasswordBean>> errors = getConstraintViolations("Qwerty1234!");
    assertTrue(errors.isEmpty());
  }

  @Test
  public void without_upperCase_password_validation_should_fail() {
    Set<ConstraintViolation<PasswordBean>> errors = getConstraintViolations("qwerty1234!");
    assertFalse(errors.isEmpty());
  }

  @Test
  public void without_lowerCase_password_validation_should_fail() {
    Set<ConstraintViolation<PasswordBean>> errors = getConstraintViolations("QWERTY1234!");
    assertFalse(errors.isEmpty());
  }

  @Test
  public void without_digits_password_validation_should_fail() {
    Set<ConstraintViolation<PasswordBean>> errors = getConstraintViolations("qwertyasdQ!");
    assertFalse(errors.isEmpty());
  }

  @Test
  public void with_space_password_validation_should_fail() {
    Set<ConstraintViolation<PasswordBean>> errors = getConstraintViolations(
        "Dwerty1234! ");
    assertFalse(errors.isEmpty());
  }

  @Test
  public void without_specialSymbol_password_validation_should_fail() {
    Set<ConstraintViolation<PasswordBean>> errors = getConstraintViolations("qwertyssS1");
    assertFalse(errors.isEmpty());
  }

  @Test
  public void short_password_validation_should_fail() {
    Set<ConstraintViolation<PasswordBean>> errors = getConstraintViolations("Qw1!");
    assertFalse(errors.isEmpty());
  }

  @Test
  public void long_password_validation_should_fail() {
    Set<ConstraintViolation<PasswordBean>> errors = getConstraintViolations(
        "Qw1!qwewqewqewqewqewqewqewqeqweqwewqeqweqweqweqwew");
    assertFalse(errors.isEmpty());
  }

  private Set<ConstraintViolation<PasswordBean>> getConstraintViolations(String password) {
    PasswordBean bean = new PasswordBean();
    bean.setPassword(password);
    return validator.validate(bean);
  }

}
