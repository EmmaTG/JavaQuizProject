package mcq.QuestionScenes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mcq.Questions.MultipleChoiceQuestion;
import mcq.Questions.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javafx.scene.text.TextAlignment.CENTER;

public class QuestionScene<T extends Question> {

    protected T question;
    protected static int questionNumber=0;
    public  final int sceneWidth = 400;
    public  final int sceneHeight = 500;
    private Stage newStage;
    private Scene newScene;



    protected GridPane questionWindow;
    private List<Button> possibleAnswers; //List of buttons with the first one being the correct answer;
    private double time; //length of question

    public QuestionScene(T question) {
        this.question = question;
        this.possibleAnswers = new ArrayList<>();
        this.time = 30.00;
    }

    public static void resetQuestionNumbers(){
        questionNumber = 0;
    }
    public T getQuestion() {
        return question;
    }

    public GridPane getQuestionWindow() {
        return questionWindow;
    }

    public void setQuestionWindow(List<Button> buttonActions, HBox progressHBox){
        List<HBox> hboxes = new ArrayList<>();
        for (int i=0;i<buttonActions.size();i+=2){
            HBox answerOptions = new HBox();
            buttonActions.get(i).getStyleClass().add("answerButton"+i);
            buttonActions.get(i+1).getStyleClass().add("answerButton"+(i+1));
            try {
                answerOptions = new HBox(buttonActions.get(i),buttonActions.get(i+1));
            } catch (IndexOutOfBoundsException e){
                System.out.println(e.getMessage());
                answerOptions = new HBox(buttonActions.get(i));
            } finally {
                answerOptions.setSpacing(10);
                hboxes.add(answerOptions);
            }
        }
        VBox optionsVBox = questionHeading(question,questionNumber);


        for (HBox hBox: hboxes) {
            optionsVBox.getChildren().add(hBox);
        }

        optionsVBox.setAlignment(Pos.CENTER);
        optionsVBox.setSpacing(10);

        questionWindow = new GridPane();
        questionWindow.add(optionsVBox, 0,0);
        questionWindow.add(progressHBox, 0, 1);
    }
    public Stage getQuestionStage() {
        newStage = new Stage();
        newScene = new Scene(questionWindow);
        newStage.setScene(newScene);
        newScene.getStylesheets().add(QuestionScene.class.getResource("QuestionScene.css").toExternalForm());
        return newStage;
    }

    public ObservableList<Button> getPossibleAnswers() {
        return FXCollections.observableArrayList(possibleAnswers);
    }

    public double getTime() {
        return time;
    }

    public void setPossibleAnswers() {
        questionNumber++;
        if (this.question instanceof MultipleChoiceQuestion) {
            for (String buttonText : question.getOptions()) {
                Button newButton = new Button(buttonText);
                newButton.setPrefWidth(((float) sceneWidth) / 2.0);
                newButton.setPrefHeight(100);
                newButton.setWrapText(true);
                newButton.setAlignment(Pos.CENTER);
                newButton.setTextAlignment(CENTER);
                possibleAnswers.add(newButton);
            }
            Collections.shuffle(possibleAnswers);
        }
    }

    public <T extends Question> VBox questionHeading(T question, int questionNumber){
        Label heading = new Label("Question " + questionNumber);
        heading.getStyleClass().add("questionNumber");
        heading.setPrefHeight(50);
        heading.setPrefWidth(sceneWidth);
        heading.setWrapText(true);
        Label questionToAnswer = new Label(question.getQuestion());
        questionToAnswer.getStyleClass().add("questionLabel");
        questionToAnswer.setAlignment(Pos.CENTER);
        questionToAnswer.setTextAlignment( CENTER );
        questionToAnswer.setPrefHeight(150);
        questionToAnswer.setPrefWidth(sceneWidth);
        questionToAnswer.setWrapText(true);

        VBox optionsVBox = new VBox(heading,questionToAnswer);
        return optionsVBox;
    }
}
