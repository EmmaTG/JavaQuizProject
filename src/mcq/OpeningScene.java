package mcq;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class OpeningScene {

    private Scene scene;
    private final Button createQuiz = new Button("Create a quiz");
    private final Button existingQuiz = new Button("Use existing Quiz");
    private final Button quitQuiz = new Button("Quit");

    private Label welcomeLabel = new Label();
    private final Label userPrompt = new Label("What would you like to do?");
    private final Label orLabel = new Label("");
    private final Label orLabel2 = new Label("");

    private GridPane gridPane = new GridPane();

    private void setGridPane(){
        userPrompt.setStyle("-fx-padding: 50");
        gridPane.add(welcomeLabel,0,0);
        gridPane.add(userPrompt,0,1);
        gridPane.add(createQuiz,0,2);
        gridPane.add(orLabel,0,3);
        gridPane.add(existingQuiz,0,4);
        gridPane.add(orLabel2,0,5);
        gridPane.add(quitQuiz,0,6);
        gridPane.setAlignment(Pos.CENTER);
    }

    public Scene getScene() {
        setGridPane();
        scene = new Scene(this.gridPane, 300,400);
        scene.getStylesheets().add(Main.class.getResource("OpeningSceneCSS.css").toExternalForm());
        gridPane.getStyleClass().add("myGrid");
        createQuiz.getStyleClass().add("createQuizButton");
        createQuiz.setMaxWidth(Double.MAX_VALUE);
        existingQuiz.getStyleClass().add("existingQuizButton");
        existingQuiz.setMaxWidth(Double.MAX_VALUE);
        quitQuiz.getStyleClass().add("quitQuizButton");
        quitQuiz.setMaxWidth(Double.MAX_VALUE);
        welcomeLabel.getStyleClass().add("welcomeLabel");
        userPrompt.getStyleClass().add("userPrompt");
        return scene;
    }

    public Button getCreateQuiz() {
        return createQuiz;
    }

    public Button getExistingQuiz() {
        return existingQuiz;
    }

    public void setWelcomeLabelText(String message) {
        this.welcomeLabel.setText(message);
    }

    public Button getQuitQuiz() {
        return quitQuiz;
    }
}
