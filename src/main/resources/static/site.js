var quizForm = $('#quizForm').submit(function(e) {
   e.preventDefault();
});

var quizSubmit = $('#quizSubmit');
$('#passwordChangeButton').click(function() {
  var data = {
    currentPassword: $('#currentPassword').val(),
    newPassword: $('#newPassword').val(),
    newPasswordAgain: $('#newPasswordAgain').val()
  }
  f('/api/v1/users/me', data, true, {}, 'put')
    .then((data) => {
      $('#changePasswordForm')[0].reset();
      $('#passwordDialog').modal('hide');
    });
})

function getQuizzes() {
  f('/api/v1/quizzes')
    .then((data) => {
      var quizList = $('#quizList').empty();
      data.content.forEach(quiz => {
        $(`<li class="list-group-item">${quiz.title}</li>`)
          .click(function() {
            getQuiz(quiz.id);
          }).appendTo(quizList);

      });
    });
  return false;
}

function getQuiz(quizId) {
  f(`/api/v1/quizzes/${quizId}`)
    .then((data) => {
      $('#quizTitle').text(data.title);
      $('#quizDescription').text(data.description);
      // load questions
      f(`/api/v1/quizzes/${quizId}/questions`)
        .then((data) => {
          // clear the form
          quizForm.empty();
          // setup submit button
          quizSubmit.off('click').on('click', function() {
            var formData = new FormData(quizForm[0]);
            console.log('form-data', JSON.stringify( quizForm.serializeArray() ))
            f(`/api/v1/quizzes/${quizId}/submissions`, formData, true)
              .then((data) => {
                 console.log(data);
              });
          })
          // load the questions
          data.content.forEach(question => {
            renderQuestion(quizId, question).appendTo(quizForm);
          });
        });
    });
  return false;
}

function renderAnswers(quizId, question, answer) {
  var type = question.type === 'SingleChoice' ? 'radio' : 'checkbox';
  return $(`<div class="form-check">
              <input class="form-check-input" type="${type}" name="${question.id}" value="${answer.id}">
              <label class="form-check-label" >${answer.description}</label>
            </div>`);
}

function renderQuestion(quizId, question) {
  var card = $(`<div class="card mt-2">
                  <div class="card-body">
                    <h5 class="card-title">${question.title}</h5>
                    <h6 class="card-subtitle mb-2 text-muted">${question.description}</h6>
                  </div>
                </div>`);
  var cardBody = card.find('.card-body');
  f(`/api/v1/quizzes/${quizId}/questions/${question.id}/answers`)
    .then((data) => { // no pagination here
      data.forEach(answer => {
        renderAnswers(quizId, question, answer).appendTo(cardBody);
      });
    });
  return card;
}
function getUser() {
  f(`/api/v1/users/me`)
    .then((data) => {
      $('#userName').text(data.name);
    });
}

getQuizzes();
getUser();
//getQuiz(1);
