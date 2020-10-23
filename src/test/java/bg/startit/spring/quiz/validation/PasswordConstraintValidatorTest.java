package bg.startit.spring.quiz.validation;

import static org.junit.jupiter.api.Assertions.*;

import bg.startit.spring.quiz.dto.ChangePasswordRequest;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
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


  @Test
  public void with_valid_password_validation_should_pass() {
    Set<ConstraintViolation<ChangePasswordRequest>> errors = getConstraintViolations("Qwerty1234!");
    assertTrue(errors.isEmpty());
  }

  @Test
  public void without_upperCase_password_validation_should_fail() {
    Set<ConstraintViolation<ChangePasswordRequest>> errors = getConstraintViolations("qwerty1234!");
    assertFalse(errors.isEmpty());
  }

  @Test
  public void without_lowerCase_password_validation_should_fail() {
    Set<ConstraintViolation<ChangePasswordRequest>> errors = getConstraintViolations("QWERTY1234!");
    assertFalse(errors.isEmpty());
  }

  @Test
  public void without_digits_password_validation_should_fail() {
    Set<ConstraintViolation<ChangePasswordRequest>> errors = getConstraintViolations("qwertyasdQ!");
    assertFalse(errors.isEmpty());
  }

  @Test
  public void with_space_password_validation_should_fail() {
    Set<ConstraintViolation<ChangePasswordRequest>> errors = getConstraintViolations(
        "Dwerty1234! ");
    assertFalse(errors.isEmpty());
  }

  @Test
  public void without_specialSymbol_password_validation_should_fail() {
    Set<ConstraintViolation<ChangePasswordRequest>> errors = getConstraintViolations("qwertyssS1");
    assertFalse(errors.isEmpty());
  }

  @Test
  public void short_password_validation_should_fail() {
    Set<ConstraintViolation<ChangePasswordRequest>> errors = getConstraintViolations("Qw1!");
    assertFalse(errors.isEmpty());
  }

  @Test
  public void long_password_validation_should_fail() {
    Set<ConstraintViolation<ChangePasswordRequest>> errors = getConstraintViolations(
        "Qw1!qwewqewqewqewqewqewqewqeqweqwewqeqweqweqweqwew");
    assertFalse(errors.isEmpty());
  }

  private Set<ConstraintViolation<ChangePasswordRequest>> getConstraintViolations(String password) {
    ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest()
        .setCurrentPassword(password)
        .setNewPassword(password)
        .setNewPasswordAgain(password);
    return validator
        .validate(changePasswordRequest);
  }

}
