package mcq;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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

enum questionType {
    MCQ,
    TF,
    WI
}

public class CreateQuizQuestionsStage {

    private Scene newScene;
    private Stage newStage;
    private Quiz createdQuiz;
    private ObservableList<Question> observableQuestionList;

    public CreateQuizQuestionsStage(String quizTitle) {
        this.createdQuiz = new Quiz(quizTitle);
        this.observableQuestionList = FXCollections.observableArrayList();
    }

    public CreateQuizQuestionsStage(String quizTitle,List<Question> questionList) {
        this.createdQuiz = new Quiz(quizTitle);
        this.observableQuestionList = FXCollections.observableArrayList(questionList);
    }

    public void setCreatedQuiz(String quizTitle) {
        this.createdQuiz = new Quiz(quizTitle);
    }

    public ObservableList<Question> getObservableQuestionList() {
        return observableQuestionList;
    }

    public void setObservableQuestionList(List<Question> questionList) {
        observableQuestionList.addAll(questionList);
    }

    public Scene getNewScene(Stage stage) {
        newStage = stage;

        BorderPane borderPane = new BorderPane();

        ListView<Question> listView = new ListView<>();
        listView.setOrientation(Orientation.VERTICAL);
        listView.setItems(observableQuestionList);

        Button newQuestion = new Button("New Question");
        newQuestion.setMaxWidth(Double.MAX_VALUE);
        newQuestion.setOnAction(e -> typeOfQuestionToCreate(createdQuiz,newStage));

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
            Main.homeScreen("",newStage);
        });

        Button playQuiz = new Button("Save and play now");
        playQuiz.setMaxWidth(Double.MAX_VALUE);
        playQuiz.setOnAction(e -> {
            new Thread(saveRunnable).start();
            ObservableList<QuestionScene> questionScenes = Main.createQuestionScenes(createdQuiz.getQuestions());
            newStage.close();
            Main.runQuiz(questionScenes);
//            newStage.close();

        });

        Button cancel = new Button("Cancel");
        cancel.setMaxWidth(Double.MAX_VALUE);
        cancel.setOnAction(e -> {
//            newStage.close();
            Main.homeScreen("",newStage);
        });

        VBox vBox = new VBox();
        vBox.getChildren().setAll(FXCollections.observableArrayList(newQuestion,editButton,deleteButton,playQuiz,saveQuiz,cancel));
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);

        borderPane.setCenter(listView);
        borderPane.setRight(vBox);
        newScene = new Scene(borderPane);
        return newScene;
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

    public void typeOfQuestionToCreate(Quiz newQuiz, Stage stage) {
        IntegerProperty property = new SimpleIntegerProperty(newQuiz.getQuestions().size());
        Stage typeOfQStage = stage;
        Label typeofQ = new Label("What type of question would you like to create?");
        typeofQ.setWrapText(true);
        Button mcqButton = new Button("Multiple Choice");
        mcqButton.setMaxWidth(Double.MAX_VALUE);
        Button tfButton = new Button("True/False");
        tfButton.setMaxWidth(Double.MAX_VALUE);
        Button wiButton = new Button("Write in Question");
        wiButton.setMaxWidth(Double.MAX_VALUE);

        Button doneButton = new Button("Done");
        doneButton.setMaxWidth(Double.MAX_VALUE);
        doneButton.disableProperty().bind(property.lessThan(1));

        Button quitButton = new Button("Cancel");
        quitButton.setMaxWidth(Double.MAX_VALUE);

        Label numberOfQuestions = new Label();
        numberOfQuestions.textProperty().bind(property.asString());
        Label numberOfQuestions2 = new Label(" Questions created");

        GridPane gridPane = new GridPane();
        VBox vBox = new VBox(typeofQ, mcqButton, tfButton, wiButton, new HBox(quitButton, doneButton, numberOfQuestions, numberOfQuestions2));
        gridPane.add(vBox, 0, 0);
        gridPane.setVgap(20);
        gridPane.setHgap(20);
        gridPane.setStyle("-fx-padding: 50");

        mcqButton.setOnAction(e -> {
            if (createQuestion(newQuiz, questionType.MCQ)) {
                property.set(property.get() + 1);
            }
        });
        tfButton.setOnAction(e -> {
            if (createQuestion(newQuiz, questionType.TF)) {
                property.set(property.get() + 1);
            }
        });
        wiButton.setOnAction(e -> {
            if (createQuestion(newQuiz,questionType.WI)) {
                property.set(property.get() + 1);
            }
        });
        doneButton.setOnAction(e -> {
            observableQuestionList.clear();
            observableQuestionList.addAll(newQuiz.getQuestions());
            typeOfQStage.setScene(newScene);
        });

        quitButton.setOnAction(e -> {
            Main.homeScreen("",typeOfQStage);
        });


        typeOfQStage.setOnCloseRequest(e -> {
            Main.homeScreen("",typeOfQStage);
        });

        Scene typeOfQScene = new Scene(gridPane);
        typeOfQStage.setScene(typeOfQScene);
        typeOfQStage.setTitle("Type of question");
    }

    private boolean createQuestion(Quiz newQuiz, questionType type){
        Dialog<? extends Question> dialog = null;
        if (type == questionType.MCQ){
            dialog = new MCQDialog().getDialog();
        } else if ( type == questionType.TF){
            dialog = new TrueFalseDialog().getDialog();
        } else if (type == questionType.WI){
            dialog= new WriteInDialog().getDialog();
        }
        if (dialog != null) {
            Optional<? extends Question> result1 = dialog.showAndWait();
            if (result1.isPresent()) {
                Question question = result1.get();
                newQuiz.addQuestion(question);
                return true;
            }
        }
        return false;
    }

}
