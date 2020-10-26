package bg.startit.spring.quiz.controller;

import bg.startit.spring.quiz.dto.ChangePasswordRequest;
import bg.startit.spring.quiz.dto.UserResponse;
import bg.startit.spring.quiz.model.User;
import bg.startit.spring.quiz.repository.UserRepository;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Validated
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  // /api/v1/users -> create (register)
  // /api/v1/users/me -> read, update(change password), delete (unregister)
  @PostMapping
  public ResponseEntity<Void> register(@RequestBody User user) {
    user = userRepository.save(user);

    // POST (data) -> return Redirect to GET link
    URI redirect = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/me") // link to /api/v1/users/me
        .build()
        .toUri();
    return ResponseEntity.created(redirect).build();
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> readUser() {
    User current = getCurrentUser();
    return ResponseEntity.ok(new UserResponse()
        .username(current.getName()));
  }

  @PutMapping("/me")
  public ResponseEntity<User> updatePassword(@RequestBody ChangePasswordRequest passwordRequest) {
    User toUpdate = getCurrentUser();
    // 1. check current password
    if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), toUpdate.getPassword())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // 2. check password validated
    if (!passwordRequest.getNewPassword().equals(passwordRequest.getNewPasswordAgain())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    // 3. update password
    String encodedPassword = passwordEncoder.encode(passwordRequest.getNewPassword());
    toUpdate.setPasswordHash(encodedPassword.toCharArray());
    userRepository.save(toUpdate);

    return ResponseEntity.ok(toUpdate);
  }

  @DeleteMapping("/me")
  public void deleteUser() {
    User user = getCurrentUser();
    userRepository.delete(user);
  }

  private User getCurrentUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

}
