package bg.startit.spring.quiz.dto;

public class ChangePasswordRequest {

  String currentPassword ;
  String newPassword;
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
