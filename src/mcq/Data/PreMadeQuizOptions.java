package mcq.Data;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class PreMadeQuizOptions {

    private static VBox vBox;
    private static Stage newStage=new Stage();
    private static Scene newScene;

    public static Stage getNewStage() {
        List<String> quizList = QuestionDataSource.getInstance().getQuizzes();
        List<Button> quizListButtons = new ArrayList<>();
        for (String quizTitle : quizList) {
            Button newButton = new Button(quizTitle);
            newButton.setMaxWidth(Double.MAX_VALUE);
            newButton.setOnAction(e -> {
                QuestionDataSource.getInstance().queryQuizQuestion(quizTitle);
                newStage.close();
            });
            quizListButtons.add(newButton);
        }

        vBox = new VBox();
        vBox.setSpacing(20);
        HBox hBox= new HBox();
        for (int i = 0; i < quizListButtons.size(); i += 3) {
            hBox.setSpacing(20);
            hBox.getChildren().add(quizListButtons.get(i));
            try {
                hBox.getChildren().add(quizListButtons.get(i + 1));
            } catch (IndexOutOfBoundsException e1) {
            }
            try {
                hBox.getChildren().add(quizListButtons.get(i + 2));
            } catch (IndexOutOfBoundsException e2) {
            }
            if (hBox != null) {
                vBox.getChildren().add(hBox);
                hBox = new HBox();
            }
        }
        GridPane gridPane = new GridPane();
        gridPane.add(vBox,0,0);
        gridPane.setVgap(20);
        gridPane.setHgap(20);
        gridPane.setStyle("-fx-padding: 50");
        newScene = new Scene(gridPane);
        newStage.setTitle("Select quiz");
        newStage.setScene(newScene);

        return newStage;
    }
}
