package mcq.QuestionScenes;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mcq.Questions.Question;

import java.util.List;

public class WriteInQuestionScene extends QuestionScene {
    private TextField answerField;
    private Stage newStage;
    private Scene newScene;

    public WriteInQuestionScene(Question question) {
        super(question);

        this.answerField  = new TextField();
    }

    @Override
    public void setQuestionWindow(List buttonActions, HBox progressHBox) {
        newStage = new Stage();

        answerField.prefWidth(sceneWidth);

        VBox optionsVBox = questionHeading(question,questionNumber);
        optionsVBox.getChildren().add(answerField);
        optionsVBox.setAlignment(Pos.CENTER);

        optionsVBox.setSpacing(10);

        questionWindow = new GridPane();
        questionWindow.add(optionsVBox, 0,0);
        questionWindow.add(progressHBox, 0, 1);


        questionWindow.setVgap(10);
    }

    public TextField getAnswerField() {
        return answerField;
    }
}
