package mcq;

import mcq.Questions.MultipleChoiceQuestion;
import mcq.Questions.Question;
import mcq.Questions.TrueFalse;
import mcq.Questions.WriteInQuestion;

import java.util.List;
import java.util.Optional;

public class EditQuizScene {
    private Quiz createdQuiz;
    private List<Question> questionList;
    public EditQuizScene(Quiz createdQuiz) {
        this.createdQuiz = createdQuiz;
        this.questionList = createdQuiz.getQuestions();
    }

    public void EditAction(Question question, int questionNumber){
        Optional<? extends Question> result = Optional.empty();
        if (question instanceof TrueFalse) {
            result = editTrueFalse((TrueFalse) question);
        } else if (question instanceof WriteInQuestion){
            result = editWriteIn((WriteInQuestion) question);
        } else if (question instanceof MultipleChoiceQuestion){
            result = editMCQ((MultipleChoiceQuestion) question);
        } else {
            System.out.println("No question selected");
        }
        if (result.isPresent()) {
            Question editedQuestion = result.get();
            setEditedQuestion(editedQuestion,questionNumber);
        }
    }

    private Optional<? extends Question> editTrueFalse(TrueFalse selectQuestion){
        TrueFalseDialog dialogObject = new TrueFalseDialog();
        dialogObject.getQuestionTextField().setText(selectQuestion.getQuestion());
        dialogObject.getTrueToggle().setSelected(selectQuestion.getCorrectAnswer().equalsIgnoreCase("true"));
        dialogObject.getFalseToggle().setSelected(selectQuestion.getCorrectAnswer().equalsIgnoreCase("false"));
        if (selectQuestion.getQuestionImage() != null){
            dialogObject.getImageCheck().setSelected(true);
            dialogObject.getGridPane().add(dialogObject.getFilePath(), 0, 10);
            dialogObject.getFilePath().setText(selectQuestion.getQuestionImagePath());
        }
//        dialogObject.createDialogBox();
        return dialogObject.getDialog().showAndWait();
    }

    private Optional<? extends Question> editWriteIn(WriteInQuestion selectQuestion){
        WriteInDialog dialogObject = new WriteInDialog();
        dialogObject.getAnswerTextField().setText(selectQuestion.getCorrectAnswer());
        dialogObject.getQuestionTextField().setText(selectQuestion.getQuestion());
        if (selectQuestion.getQuestionImage() != null){
            dialogObject.getImageCheck().setSelected(true);
            dialogObject.getGridPane().add(dialogObject.getFilePath(), 0, 10);
            dialogObject.getFilePath().setText(selectQuestion.getQuestionImagePath());
        }
        return dialogObject.getDialog().showAndWait();
    }

    private Optional<? extends Question> editMCQ(MultipleChoiceQuestion selectQuestion){
        MCQDialog dialogObject = new MCQDialog();
        dialogObject.getQuestionTextField().setText(selectQuestion.getQuestion());
        dialogObject.getAnswerTextField().setText(selectQuestion.getCorrectAnswer());
        dialogObject.getFalseAnswerTextField1().setText(selectQuestion.getOptions().get(1));
        dialogObject.getFalseAnswerTextField2().setText(selectQuestion.getOptions().get(2));
        dialogObject.getFalseAnswerTextField3().setText(selectQuestion.getOptions().get(3));
        if (selectQuestion.getQuestionImage() != null){
            dialogObject.getImageCheck().setSelected(true);
            dialogObject.getGridPane().add(dialogObject.getFilePath(), 0, 10);
            dialogObject.getFilePath().setText(selectQuestion.getQuestionImagePath());
        }
        return dialogObject.getDialog().showAndWait();
    }

    private void setEditedQuestion(Question question, int questionNumber){
            createdQuiz.updateQuestion(questionNumber, question);
            questionList = createdQuiz.getQuestions();
    }

    public Quiz getCreatedQuiz() {
        return createdQuiz;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }
}
