package mcq;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class CreateNewQuizStage {

    private Stage newStage;
    private Scene newScene;
    private String quizTitle;

    public CreateNewQuizStage() {
    }

    public Stage getNewStage() {
        newStage = new Stage();

        Label quizNameLabel = new Label("Quiz Name");
        TextField quizNameTextField = new TextField();
        GridPane gridPane = new GridPane();

        gridPane.add(quizNameLabel, 0,0);
        gridPane.add(quizNameTextField,0,1);
        gridPane.setStyle("-fx-padding: 30");
        gridPane.setVgap(20);

        quizNameTextField.setOnAction(e -> {
            setQuizTitle(quizNameTextField.getText());
            newStage.close();
        });

        newScene = new Scene(gridPane);
        newStage.setTitle("Creating new quiz");
        newStage.setScene(newScene);
        return newStage;
    }


    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }
}
