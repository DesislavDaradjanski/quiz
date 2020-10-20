function getQuizzes() {
  f('/api/v1/quizzes')
    .then((data) => {
      var quizList = $('#quizList').empty();
      data.content.forEach(quiz => {
        $(`<li class="list-group-item">${quiz.title}</li>`)
          .click(function() {
            loadQuiz(quiz.id);
          }).appendTo(quizList);

      });
    });
  return false;
}

function loadQuiz(quizId) {
  f(`/api/v1/quizzes/${quizId}`)
    .then((data) => {
      $('#quizTitle').text(data.title);
      $('#quizDescription').text(data.description);
      loadQuestions(quizId);
    });
  return false;
}

function loadQuestions(quizId) {
  f(`/api/v1/quizzes/${quizId}/questions`)
    .then((data) => {
      var form = $('#quizForm').empty();
      data.content.forEach(question => {
        renderQuestion(quizId, question).appendTo(form);
      });
    });
}

function renderQuestion(quizId, question) {
  // checkbox/radio
  // name = questionId
  // value = list of answers
  return $( `<div class="card mt-2">
        <div class="card-body">
          <h5 class="card-title">${question.title}</h5>
          <h6 class="card-subtitle mb-2 text-muted">${question.description}</h6>
          <div class="form-check">
            <input class="form-check-input" type="checkbox" value="2001">
            <label class="form-check-label">
              Default checkbox
            </label>
          </div>
          <div class="form-check">
            <input class="form-check-input" type="checkbox" value="2002">
            <label class="form-check-label">
              Default checkbox
            </label>
          </div>
        </div>
      </div>`);
}

getQuizzes();
//loadQuiz(1);
