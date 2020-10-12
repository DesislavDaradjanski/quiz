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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    createQuiz("VP Bran International", true, "Verano Azur");
  }

  private String createQuiz(String title, boolean visible, String description) throws Exception {
    String content = String.format("{\n"
        + "    \"title\":\"%s\",\n"
        + "    \"description\":\"%s\",\n"
        + "    \"visible\": %b \n"
        + "}", title, description, visible);
    return http.perform(post("/api/v1/quizzes")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(content)) // content is used *only* with @RequestBody
        .andExpect(status().isCreated())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andReturn().getResponse().getHeader(HttpHeaders.LOCATION);
  }

  @Test
  void readQuiz() throws Exception {
    String link = createQuiz("Gotmar", true, "Derby");

    http.perform(get(link))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Gotmar"))
        .andExpect(jsonPath("$.visible").value(Boolean.TRUE))
        .andExpect(jsonPath("$.description").value("Derby"));
  }

  @Test
  void listQuizzes() {
  }

  @Test
  void deleteQuiz() throws Exception {
    String link = createQuiz("**ITD**", false, "PVC bottles");
    http.perform(get(link))
        .andDo(print())
        .andExpect(status().isOk());
    http.perform(delete(link))
        .andDo(print())
        .andExpect(status().isOk());
    http.perform(get(link))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  void updateQuiz() throws Exception {
    String link = createQuiz("Honda", true,"F20");
    String content = String.format("{\n"
        + "    \"title\":\"%s\",\n"
        + "    \"description\":\"%s\",\n"
        + "    \"visible\": %b \n"
        + "}", "Jigula", "", false);
    String updatedLink = http.perform(put(link)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(content)) // content is used *only* with @RequestBody
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andReturn().getResponse().getHeader(HttpHeaders.LOCATION);
    assertEquals(link, updatedLink);

    http.perform(get(link))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Jigula"))
        .andExpect(jsonPath("$.visible").value(Boolean.FALSE))
        .andExpect(jsonPath("$.description").value(""));

  }
}