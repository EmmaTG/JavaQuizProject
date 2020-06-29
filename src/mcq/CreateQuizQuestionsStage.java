package mcq;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mcq.Data.QuestionDataSource;
import mcq.QuestionScenes.QuestionScene;
import mcq.Questions.MultipleChoiceQuestion;
import mcq.Questions.Question;
import mcq.Questions.TrueFalse;
import mcq.Questions.WriteInQuestion;

import java.util.List;
import java.util.Optional;

public class CreateQuizQuestionsStage {

    private Stage newStage;
    private Quiz createdQuiz;
    private static ObservableList<Question> observableQuestionList;

    public CreateQuizQuestionsStage(String quizTitle) {
        this.createdQuiz = new Quiz(quizTitle);
    }

    public void setCreatedQuiz(String quizTitle) {
        this.createdQuiz = new Quiz(quizTitle);
    }

    public ObservableList<Question> getObservableQuestionList() {
        return observableQuestionList;
    }

    public static void setObservableQuestionList(List<Question> questionList) {
        observableQuestionList.clear();
        observableQuestionList.addAll(questionList);
    }

    public Stage getNewStage() {
        newStage = new Stage();

        BorderPane borderPane = new BorderPane();

        observableQuestionList = FXCollections.observableArrayList();
        ListView<Question> listView = new ListView<>();
        listView.setOrientation(Orientation.VERTICAL);
        listView.setItems(observableQuestionList);

        Button newQuestion = new Button("New Question");
        newQuestion.setMaxWidth(Double.MAX_VALUE);
        newQuestion.setOnAction(e -> Main.typeOfQuestionToCreate(createdQuiz));

        Button editButton = new Button("Edit");
        editButton.setMaxWidth(Double.MAX_VALUE);
        editButton.setOnAction(e -> {
            Question selectQuestion = listView.getSelectionModel().getSelectedItem();
            int selectedQuestionInt = listView.getSelectionModel().getSelectedIndex();
            EditAction(selectQuestion, selectedQuestionInt);
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setOnAction(e -> {
            Question selectQuestion = listView.getSelectionModel().getSelectedItem();
            observableQuestionList.remove(selectQuestion);
            createdQuiz.removeQuestion(selectQuestion);
        });

        Runnable saveRunnable = () -> QuestionDataSource.getInstance().saveNewQuiz(createdQuiz);

        Button saveQuiz = new Button("Save and play later");
        saveQuiz.setMaxWidth(Double.MAX_VALUE);
        saveQuiz.setOnAction(e -> {
            new Thread(saveRunnable).start();
            newStage.close();
            Main.homeScreen("");
        });

        Button playQuiz = new Button("Save and play now");
        playQuiz.setMaxWidth(Double.MAX_VALUE);
        playQuiz.setOnAction(e -> {
            new Thread(saveRunnable).start();
            ObservableList<QuestionScene> questionScenes = Main.createQuestionScenes(createdQuiz.getQuestions());
            Main.runQuiz(questionScenes);
            newStage.close();

        });

        Button cancel = new Button("Cancel");
        cancel.setMaxWidth(Double.MAX_VALUE);
        cancel.setOnAction(e -> {
            newStage.close();
            Main.homeScreen("");
        });

        VBox vBox = new VBox();
        vBox.getChildren().setAll(FXCollections.observableArrayList(newQuestion,editButton,deleteButton,playQuiz,saveQuiz,cancel));
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);

        borderPane.setCenter(listView);
        borderPane.setRight(vBox);
        Scene newScene = new Scene(borderPane);
        newStage.setTitle("Create Quiz");
        newStage.setScene(newScene);

        return newStage;
    }


    private void EditAction(Question question, int questionNumber){
        Optional<? extends Question> result;
        if (question instanceof TrueFalse) {
            result = editTrueFalse((TrueFalse) question);
            setEditedQuestion(result, questionNumber);
        } else if (question instanceof WriteInQuestion){
            result = editWriteIn((WriteInQuestion) question);
            setEditedQuestion(result, questionNumber);
        } else if (question instanceof MultipleChoiceQuestion){
            result = editMCQ((MultipleChoiceQuestion) question);
            setEditedQuestion(result, questionNumber);
        } else {
            System.out.println("No question selected");
        }
    }

    private Optional<? extends Question> editTrueFalse(TrueFalse selectQuestion){
        TrueFalseDialog dialogObject = new TrueFalseDialog();
        dialogObject.getQuestionTextField().setText(selectQuestion.getQuestion());
        dialogObject.getTrueToggle().setSelected(selectQuestion.getCorrectAnswer().equalsIgnoreCase("true"));
        dialogObject.getFalseToggle().setSelected(selectQuestion.getCorrectAnswer().equalsIgnoreCase("false"));
        return dialogObject.getDialog().showAndWait();
    }

    private Optional<? extends Question> editWriteIn(WriteInQuestion selectQuestion){
        WriteInDialog dialogObject = new WriteInDialog();
        dialogObject.getQuestionTextField().setText(selectQuestion.getQuestion());
        dialogObject.getAnswerTextField().setText(selectQuestion.getCorrectAnswer());
        return dialogObject.getDialog().showAndWait();
    }

    private Optional<? extends Question> editMCQ(MultipleChoiceQuestion selectQuestion){
        MCQDialog dialogObject = new MCQDialog();
        dialogObject.getQuestionTextField().setText(selectQuestion.getQuestion());
        dialogObject.getAnswerTextField().setText(selectQuestion.getCorrectAnswer());
        dialogObject.getFalseAnswerTextField1().setText(selectQuestion.getOptions().get(0));
        dialogObject.getFalseAnswerTextField2().setText(selectQuestion.getOptions().get(1));
        dialogObject.getFalseAnswerTextField3().setText(selectQuestion.getOptions().get(2));
        return dialogObject.getDialog().showAndWait();
    }

    private void setEditedQuestion(Optional<? extends Question> result, int questionNumber){
        if (result.isPresent()) {
            observableQuestionList.set(questionNumber, result.get());
            createdQuiz.updateQuestion(questionNumber, result.get());
        }
    }
}
