package bg.startit.spring.quiz.controller;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import bg.startit.spring.quiz.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerTest {

  private MockMvc http;

  @Autowired
  UserRepository userRepository;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private UserDetailsService userDetailsService;
  private UserDetails testUserDetails;

  @BeforeEach
  void setUp() throws Exception {
    http = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
    createUser("testuser", "testuser@abv.bg", "Qwerty1234!");
    testUserDetails = userDetailsService.loadUserByUsername("testuser@abv.bg");
  }

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
  }

  @Test
  void readUser() {

  }

  //TODO :
  @Test
  // To use the annotation below, the user MUST be created in data.sql,
  // As the line is executed before setUp method.
//  @WithUserDetails(value = "testuser@abv.bg")
  void updatePassword() throws Exception {
    String content = String.format("{\n"
        + "    \"currentPassword\":\"%s\",\n"
        + "    \"newPassword\":\"%s\",\n"
        + "    \"newPasswordAgain\":\"%s\"\n"
        + "}", "Qwerty1234!", "Qwerty12345!", "Qwerty12345!");

    http.perform(put("/api/v1/users/me")
        .with(user(testUserDetails)) // supply current logged user
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(content)) // content is used *only* with @RequestBody
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("testuser"));
  }

  private String createUser(String userName, String email, String password) throws Exception {
    String content = String.format("{\n"
        + "    \"username\":\"%s\",\n"
        + "    \"email\":\"%s\",\n"
        + "    \"password\":\"%s\",\n"
        + "    \"passwordAgain\":\"%s\"\n"
        + "}", userName, email, password, password);
    String location = http.perform(post("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(content)) // content is used *only* with @RequestBody
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(
            header().string(HttpHeaders.LOCATION, matchesPattern("http://.*/api/v1/users/me")))
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andReturn().getResponse().getHeader(HttpHeaders.LOCATION);
    return location;
  }

  // TODO: create user with invalid data
  @Test
  void createUser() throws Exception {
    createUser("admin", "admin@abv.bg", "Qwerty1234!");
  }

  @Test
  void deleteUser() {
  }
}