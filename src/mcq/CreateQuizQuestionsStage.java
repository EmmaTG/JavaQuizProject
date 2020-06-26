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

    private VBox vBox;
    private Stage newStage;
    private Quiz createdQuiz;
    private static ObservableList<Question> observableQuestionList;

    public CreateQuizQuestionsStage() {
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
        newQuestion.setOnAction(e -> {
            Main.typeOfQuestionToCreate(createdQuiz);
        });

        Button editButton = new Button("Edit");
        editButton.setMaxWidth(Double.MAX_VALUE);
        editButton.setOnAction(e -> {
            Question selectQuestion = listView.getSelectionModel().getSelectedItem();
            int selectedQuestionInt = listView.getSelectionModel().getSelectedIndex();
            if (selectQuestion instanceof TrueFalse) {
                TrueFalseDialog dialogObject = new TrueFalseDialog();
                dialogObject.getQuestionTextField().setText(selectQuestion.getQuestion());
                dialogObject.getTrueToggle().setSelected(selectQuestion.getCorrectAnswer().equalsIgnoreCase("true"));
                dialogObject.getFalseToggle().setSelected(selectQuestion.getCorrectAnswer().equalsIgnoreCase("false"));
                Optional<TrueFalse> result = dialogObject.getDialog().showAndWait();
                if (result.isPresent()) {
                    TrueFalse tf = result.get();
                    if (tf!=null) {
                        observableQuestionList.set(selectedQuestionInt, tf);
                        createdQuiz.updateQuestion(selectedQuestionInt, tf);
                    }

                }
            } else if (selectQuestion instanceof WriteInQuestion){
                WriteInDialog dialogObject = new WriteInDialog();
                dialogObject.getQuestionTextField().setText(selectQuestion.getQuestion());
                dialogObject.getAnswerTextField().setText(selectQuestion.getCorrectAnswer());
                Optional<WriteInQuestion> result = dialogObject.getDialog().showAndWait();
                if (result.isPresent()) {
                    WriteInQuestion wi = result.get();
                    if (wi != null) {
                        observableQuestionList.set(selectedQuestionInt, wi);
                        createdQuiz.updateQuestion(selectedQuestionInt, wi);
                    }
                }
            } else if (selectQuestion instanceof MultipleChoiceQuestion){
                MCQDialog dialogObject = new MCQDialog();
                dialogObject.getQuestionTextField().setText(selectQuestion.getQuestion());
                dialogObject.getAnswerTextField().setText(selectQuestion.getCorrectAnswer());
                dialogObject.getFalseAnswerTextField1().setText(selectQuestion.getOptions().get(0));
                dialogObject.getFalseAnswerTextField2().setText(selectQuestion.getOptions().get(1));
                dialogObject.getFalseAnswerTextField3().setText(selectQuestion.getOptions().get(2));
                Optional<MultipleChoiceQuestion> result = dialogObject.getDialog().showAndWait();
                if (result.isPresent()) {
                    MultipleChoiceQuestion mcq = result.get();
                    if (mcq != null) {
                        observableQuestionList.set(selectedQuestionInt, mcq);
                        createdQuiz.updateQuestion(selectedQuestionInt, mcq);
                    }
                }
            } else {
                System.out.println("No question selected");
            }

        });

        Button deleteButton = new Button("Delete");
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setOnAction(e -> {
            Question selectQuestion = listView.getSelectionModel().getSelectedItem();
            observableQuestionList.remove(selectQuestion);
            createdQuiz.removeQuestion(selectQuestion);
        });

        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                QuestionDataSource.getInstance().saveNewQuiz(createdQuiz);
            }
        };

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

        vBox = new VBox();
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
}
