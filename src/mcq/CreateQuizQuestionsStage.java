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
import mcq.Questions.Question;

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
    private ListView<Question> listView;
    private Button newQuestion = new Button("New Question");
    private Button editButton = new Button("Edit");
    private Button deleteButton = new Button("Delete");
    private Button saveQuiz = new Button("Save and play later");
    private Button playQuiz = new Button("Save and play now");
    private Button cancel = new Button("Cancel");
    private VBox vBox = new VBox();

    public CreateQuizQuestionsStage(String quizTitle) {
        this.createdQuiz = new Quiz(quizTitle);
        this.observableQuestionList = FXCollections.observableArrayList();
    }

    public CreateQuizQuestionsStage(String quizTitle,List<Question> questionList) {
        this.createdQuiz = new Quiz(quizTitle);
        this.observableQuestionList = FXCollections.observableArrayList(questionList);
    }

    public CreateQuizQuestionsStage(Quiz quiz) {
        this.createdQuiz = quiz;
        this.observableQuestionList = FXCollections.observableArrayList(quiz.getQuestions());
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

        listView = new ListView<>();
        listView.setOrientation(Orientation.VERTICAL);
        listView.setItems(observableQuestionList);

        newQuestion.setMaxWidth(Double.MAX_VALUE);
        newQuestion.setOnAction(e -> typeOfQuestionToCreate(createdQuiz,newStage));

        editButton.setMaxWidth(Double.MAX_VALUE);
        editButton.setOnAction(e -> {
            Question selectQuestion = listView.getSelectionModel().getSelectedItem();
            int selectedQuestionInt = listView.getSelectionModel().getSelectedIndex();
            EditQuizScene editQuestions = new EditQuizScene(createdQuiz);
            editQuestions.EditAction(selectQuestion, selectedQuestionInt);
            observableQuestionList.clear();
            observableQuestionList.setAll(FXCollections.observableArrayList(editQuestions.getQuestionList()));
            createdQuiz = editQuestions.getCreatedQuiz();
            stage.setScene(getNewScene(stage));
        });

        deleteButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setOnAction(e -> {
            Question selectQuestion = listView.getSelectionModel().getSelectedItem();
            observableQuestionList.remove(selectQuestion);
            createdQuiz.removeQuestion(selectQuestion);
        });

        Runnable saveRunnable = () -> QuestionDataSource.getInstance().saveNewQuiz(createdQuiz);

        saveQuiz.setMaxWidth(Double.MAX_VALUE);
        saveQuiz.setOnAction(e -> {
            new Thread(saveRunnable).start();
            Main.homeScreen("",newStage);
        });

        playQuiz.setMaxWidth(Double.MAX_VALUE);
        playQuiz.setOnAction(e -> {
            new Thread(saveRunnable).start();
            ObservableList<QuestionScene> questionScenes = Main.createQuestionScenes(createdQuiz.getQuestions());
            newStage.close();
            Main.runQuiz(questionScenes);

        });

        cancel.setMaxWidth(Double.MAX_VALUE);

        borderPane.setCenter(listView);
        borderPane.setRight(vBox);
        newScene = new Scene(borderPane);
        return newScene;
    }

    public void setvBox(ObservableList<Button> buttons) {
        VBox buttonBox  = new VBox();
        buttonBox.getChildren().setAll(buttons);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);
        this.vBox = buttonBox;
    }

    public Button getNewQuestion() {
        return newQuestion;
    }

    public Button getEditButton() {
        return editButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getSaveQuiz() {
        return saveQuiz;
    }

    public Button getPlayQuiz() {
        return playQuiz;
    }

    public Button getCancel() {
        return cancel;
    }

    public VBox getvBox() {
        return vBox;
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
        VBox vBoxTypeQuestions = new VBox(typeofQ, mcqButton, tfButton, wiButton, new HBox(quitButton, doneButton, numberOfQuestions, numberOfQuestions2));
        gridPane.add(vBoxTypeQuestions, 0, 0);
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
            stage.setScene(getNewScene(stage));
            stage.setTitle(newQuiz.getName());
        });

        quitButton.setOnAction(e -> {
            stage.setScene(getNewScene(stage));
            stage.setTitle(newQuiz.getName());
//            Main.homeScreen("",typeOfQStage);
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
