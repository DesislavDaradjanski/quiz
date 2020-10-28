package bg.startit.spring.quiz.controller;

import bg.startit.spring.quiz.model.Question;
import bg.startit.spring.quiz.repository.QuizRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class QuizControllerTest {

  @Autowired
  private QuizRepository quizRepository;

  private MockMvc http;

  @Autowired
  private WebApplicationContext context;

  @BeforeEach
  void setUp() {
    http = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  void createQuiz_with_validData_should_succeed() throws Exception {
    createQuiz("VP Brand International", true, "Verano Azur");

  }

  @Test
  void createQuiz_with_invalidTitle_should_fail() throws Exception {
    assertThrows(NestedServletException.class, () -> {
      createQuiz("VP", true, "Verano Azur");
    });
  }

  private String createQuiz(String title, boolean visible, String description) throws Exception {
    String content = String.format("{\n"
        + "    \"title\":\"%s\",\n"
        + "    \"description\":\"%s\",\n"
        + "    \"visible\": %b \n"
        + "}", title, description, visible);
    String location = http.perform(post("/api/v1/quizzes")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(content)) // content is used *only* with @RequestBody
        .andExpect(status().isCreated())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andReturn().getResponse().getHeader(HttpHeaders.LOCATION);
    return location;
  }

  @Test
  void readQuiz_with_existing_link_must_succeed() throws Exception {
    String link = createQuiz("Gotmar", true, "Derby");

    http.perform(get(link))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Gotmar"))
        .andExpect(jsonPath("$.visible").value(Boolean.TRUE))
        .andExpect(jsonPath("$.description").value("Derby"));
  }

  @Test
  void readQuiz_with_non_existing_link_must_return_notfound() throws Exception {

    http.perform(get("/api/v1/quizzes/99"))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  void listQuizzes_must_succeed() throws Exception {
    String link1 = createQuiz("Gotmar", true, "Derby");
    String link2 = createQuiz("Godfather", true, "Unknown");
    http.perform(get("/api/v1/quizzes"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.numberOfElements").value(2))
        .andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.size").value(20))
//        .andExpect(jsonPath("$.first").value(Boolean.TRUE))
//        .andExpect(jsonPath("$.last").value(Boolean.TRUE))
        .andExpect(jsonPath("$.content[0].title").value("Gotmar"))
        .andExpect(jsonPath("$.content[1].title").value("Godfather"));

    http.perform(get("/api/v1/quizzes")
          .queryParam("page", "1")
          .queryParam("size", "1"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalPages").value(2))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.numberOfElements").value(1))
        .andExpect(jsonPath("$.number").value(1))
        .andExpect(jsonPath("$.size").value(1))
//        .andExpect(jsonPath("$.first").value(Boolean.FALSE))
//        .andExpect(jsonPath("$.last").value(Boolean.TRUE))
        .andExpect(jsonPath("$.content[0].title").value("Godfather"));
  }

  @Test
  void list_with_no_quizzes_must_succeed() throws Exception {
    http.perform(get("/api/v1/quizzes").queryParam("size", "12"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalPages").value(0))
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.numberOfElements").value(0))
        .andExpect(jsonPath("$.size").value(12))
        .andExpect(jsonPath("$.number").value(0))
//        .andExpect(jsonPath("$.first").value(Boolean.TRUE))
//        .andExpect(jsonPath("$.last").value(Boolean.TRUE))
        .andExpect(jsonPath("$.content").isEmpty());

  }

  @Test
  void  list_with_negative_page_must_fail(){
    assertThrows(NestedServletException.class, () -> {
      http.perform(get("/api/v1/quizzes").queryParam("page", "-1"))
          .andDo(print());
    });
  }

  @Test
  void  list_with_negative_size_must_fail(){
    assertThrows(NestedServletException.class, () -> {
      http.perform(get("/api/v1/quizzes").queryParam("size", "-1"))
          .andDo(print());
    });
  }
  @Test
  void  list_with_bigger_size_must_fail(){
    assertThrows(NestedServletException.class, () -> {
      http.perform(get("/api/v1/quizzes?size=101"))
          .andDo(print());
    });
  }

  @Test
  void deleteQuiz_with_existing_link_must_succeed() throws Exception {
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
  void deleteQuiz_with_non_existing_link_must_return_notfound() throws Exception {
    http.perform(delete("/api/v1/quizzes/99"))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  void updateQuiz_with_valid_data_must_succeed() throws Exception {
    String link = createQuiz("Honda", true, "F20");
    String content = String.format("{\n"
        + "    \"title\":\"%s\",\n"
        + "    \"description\":\"%s\",\n"
        + "    \"visible\": %b \n"
        + "}", "Jigula", "", false);
    http.perform(put(link)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(content)) // content is used *only* with @RequestBody
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Jigula"))
        .andExpect(jsonPath("$.visible").value(Boolean.FALSE))
        .andExpect(jsonPath("$.description").value(""));
  }

  @Test
  void updateQuiz_with_invalid_data_must_fail() throws Exception {
    String link = createQuiz("Honda", true, "F20");
    String content = String.format("{\n"
        + "    \"title\":\"%s\",\n"
        + "    \"description\":\"%s\",\n"
        + "    \"visible\": %b \n"
        + "}", "JJ", "", false);

    assertThrows(NestedServletException.class, () -> {
      http.perform(put(link)
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .content(content)) // content is used *only* with @RequestBody
          .andDo(print())
          .andExpect(status().isOk());
    });

    http.perform(get(link))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Honda"))
        .andExpect(jsonPath("$.visible").value(Boolean.TRUE))
        .andExpect(jsonPath("$.description").value("F20"));
  }

  @Test
  void updateQuiz_with_no_data_must_fail() throws Exception {
    String link = createQuiz("Honda", true, "F20");

    http.perform(put(link)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        //   .content(content)) // content is used *only* with @RequestBody
        .andDo(print())
        .andExpect(status().is(400));

    http.perform(get(link))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Honda"))
        .andExpect(jsonPath("$.visible").value(Boolean.TRUE))
        .andExpect(jsonPath("$.description").value("F20"));
  }

  @Test
  void update_non_existing_quiz_with_valid_data_must_fail() throws Exception {
    String content = String.format("{\n"
        + "    \"title\":\"%s\",\n"
        + "    \"description\":\"%s\",\n"
        + "    \"visible\": %b \n"
        + "}", "Jigula", "", false);

    http.perform(put("/api/v1/quizzess/100")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(content)) // content is used *only* with @RequestBody
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @AfterEach
  void tearDown() {
    quizRepository.deleteAll();
  }
}