package mcq.Data;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import mcq.Main;

import java.util.ArrayList;
import java.util.List;

public class PreMadeQuizOptions {

    private static VBox vBox;
    private static Stage newStage=new Stage();
    private static Scene newScene;

    public static Stage getNewStage() {
        List<String> quizList = QuestionDataSource.getInstance().getQuizzesInDatabase();
        List<Button> quizListButtons = new ArrayList<>();
        for (String quizTitle : quizList) {
            Button newButton = new Button(quizTitle);
            newButton.setMaxWidth(Double.MAX_VALUE);
            newButton.setTextAlignment(TextAlignment.CENTER);
            newButton.setAlignment(Pos.CENTER);
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

        newStage.setOnCloseRequest(e ->{
            newStage.close();
            Main.homeScreen("");
        });

        newScene = new Scene(gridPane);
        newStage.setTitle("Select quiz");
        newStage.setScene(newScene);

        return newStage;
    }
}
