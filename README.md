* Quiz
    * title
    * description
    * List<Question>
    * isPublish
* Question
    * title
    * description
    * List<Answers>
    * type -> Enum
        * SingleChoice
        * MultiChoices
* Answer
    * description
    * isCorrect
    * score

* User
    * name
    * password
    * email
    * List<Answer> - @ManyToMany
 
