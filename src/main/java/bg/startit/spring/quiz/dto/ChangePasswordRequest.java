package bg.startit.spring.quiz.dto;


import bg.startit.spring.quiz.validation.ValidPassword;

public class ChangePasswordRequest {

  String currentPassword;
  @ValidPassword
  String newPassword;
  @ValidPassword
  String newPasswordAgain;

  public String getCurrentPassword() {
    return currentPassword;
  }

  public ChangePasswordRequest setCurrentPassword(String currentPassword) {
    this.currentPassword = currentPassword;
    return this;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public ChangePasswordRequest setNewPassword(String newPassword) {
    this.newPassword = newPassword;
    return this;
  }

  public String getNewPasswordAgain() {
    return newPasswordAgain;
  }

  public ChangePasswordRequest setNewPasswordAgain(String newPasswordAgain) {
    this.newPasswordAgain = newPasswordAgain;
    return this;
  }
}
