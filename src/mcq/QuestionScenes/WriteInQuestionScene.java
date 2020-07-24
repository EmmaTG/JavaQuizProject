package mcq.QuestionScenes;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import mcq.Questions.Question;

public class WriteInQuestionScene extends QuestionScene {
    private TextField answerField;

    public WriteInQuestionScene(Question question) {
        super(question);
        this.answerField  = new TextField();
    }

    @Override
    public void setQuestionWindow(HBox progressHBox) {
        answerField.prefWidth(sceneWidth);

        VBox optionsVBox = questionHeading(question,questionNumber);
        optionsVBox.getChildren().add(answerField);
        optionsVBox.setAlignment(Pos.CENTER);

        optionsVBox.setSpacing(10);

        setFullQuestionWindow(question,optionsVBox,progressHBox);
    }

    public TextField getAnswerField() {
        return answerField;
    }
}
