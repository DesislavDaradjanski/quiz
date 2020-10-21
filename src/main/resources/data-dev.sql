BEGIN;

INSERT INTO "QUIZ" (id, title, description, visible) VALUES
  (1, 'Basic Java Quiz', 'This is an example quiz, containing some questions about Java.', TRUE),
  (2, 'Basic C Quiz', 'This is an example quiz, containing some questions about C.', TRUE),
  (3, 'Example C# Quiz', NULL, TRUE),
  (4, 'Hidden Quiz', NULL, FALSE);

INSERT INTO "QUESTION" (id, title, description, type, quiz_id) VALUES
 (1, 'Question no description', NULL, 0, 1),
 (2, 'Question with description', 'Some description here', 0, 1),
 (3, 'Multiple choices question', 'Here comes multi-choice!', 1, 2);

-- the score is >1 but should accept 0!
INSERT INTO "ANSWER" (id, description, correct, score, question_id) VALUES
 (1, 'Answer 1', TRUE, 9, 1),
 (2, 'Answer 2', FALSE, 1, 1),
 (3, 'Answer 3', FALSE, 1, 1),
 (4, 'M-1', TRUE, 5, 3),
 (5, 'M-2', TRUE, 5, 3),
 (6, 'M-3', FALSE, 1, 3);

-- password is 'admin'
INSERT INTO "USER" (id, name, pwd, email) VALUES
 (1, 'admin', '$2a$10$M/QuIW0TWmNwooXWtTcdqu2DFez9mcAK0ERgmRyTJdPwS7siom8ya', 'admin@abv.bg');

COMMIT;