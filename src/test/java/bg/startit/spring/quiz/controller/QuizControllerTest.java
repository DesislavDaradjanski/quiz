package bg.startit.spring.quiz.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class QuizControllerTest {

  private MockMvc http;

  @Autowired
  private WebApplicationContext context;

  @BeforeEach
  void setUp() {
    http = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  void createQuiz() throws Exception {
      String content = "{\n"
          + "    \"title\":\"Alabala\",\n"
          + "    \"visible\": true \n"
          + "}";
    http.perform(post("/api/v1/quizzes")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(content))
        .andExpect(status().isCreated())
        .andExpect(header().exists(HttpHeaders.LOCATION));
  }

  @Test
  void readQuiz() {
  }

  @Test
  void listQuizzes() {
  }

  @Test
  void deleteQuiz() {
  }

  @Test
  void updateQuiz() {
  }
}