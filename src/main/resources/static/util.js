'use strict'
const baseURL = window.location;

// fetch utility function
function f(path, body=null, json=true, queryParams={}) {
  var url = new URL(baseURL);
  url.pathname = path;
  Object.entries(queryParams).forEach((e)=>{
    url.searchParams.append(e[0], e[1]);
  })
  return (body == null ? fetch(url) : fetch(url, { method: 'post', body: body }))
    .then((response) => {
      console.log(response);
      if (response.status === 401) { // not authorized
        $('#registerModal').modal('show');
        throw 'unauthorized';
      }
      if (response.status >= 400) {
        var err = `Error #${response.status}`;
        response.json().then((m) => {
          notify(err, JSON.stringify(m, null, '  '));
        });
        throw err;
      }
      return json ? response.json() : response;
    });
}

// safe escape html code
function htmlDecode(t){
   return t ? $('<div />').html(t).text() : '';
}
// notify utility function
function notify(title, message) {
  $('.toast')
    .find('.toast-header > strong').text(title).end()
    .find('.toast-body').text(htmlDecode(message)).end()
    .toast('show');
}
