package bg.startit.spring.quiz.controller;

import bg.startit.spring.quiz.model.User;
import bg.startit.spring.quiz.repository.UserRepository;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  // /api/v1/users -> create (register)
  // /api/v1/users/me -> read, update(change password), delete (unregister)
  @PostMapping
  public ResponseEntity<Void> register(@RequestBody User user){
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
  public ResponseEntity<User> readUser(){
    User current = getCurrentUser();
    return ResponseEntity.ok(current);
  }
  @PutMapping("/me")
  public ResponseEntity<Void> updatePassword(@RequestBody User user){
    User toUpdate = getCurrentUser();
    toUpdate.setPasswordHash(user.getPasswordHash());
    userRepository.save(toUpdate);
    URI redirect = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .build()
        .toUri();
    return ResponseEntity
        .status(HttpStatus.FOUND)
        .location(redirect)
        .build();
  }
  @DeleteMapping("/me")
  public void deleteUser(){
    User user = getCurrentUser();
    userRepository.delete(user);
  }

  private User getCurrentUser() {
    // TODO: return non-null user, or throw exception
    return null;
  }

}
