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

-------
* Result
    * User
    * quizId
    * List<QuestionAnswers>
      
  
* QuestionAnswers
   * questionId
   * List<Boolean>    
