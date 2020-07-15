package mcq;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class CreateNewQuizStage {

    private Stage newStage;
    private Scene newScene;
    private String quizTitle;
    private TextField quizNameTextField;
    private Button backButton;
    private Button okButton;

    public CreateNewQuizStage() {
    }

    public Scene getNewScene() {
        newStage = new Stage();

        Label quizNameLabel = new Label("Quiz Name");
        backButton = new Button("Back");
        okButton = new Button("OK");
        HBox hBox = new HBox(backButton,okButton);
        quizNameTextField = new TextField();
        GridPane gridPane = new GridPane();

        gridPane.add(quizNameLabel, 0,0);
        gridPane.add(quizNameTextField,0,1);
        gridPane.add(hBox,0,2);
        gridPane.setStyle("-fx-padding: 30");
        gridPane.setVgap(20);

        return newScene = new Scene(gridPane);
    }


    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public TextField getQuizNameTextField() {
        return quizNameTextField;
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getOkButton() {
        return okButton;
    }
}
