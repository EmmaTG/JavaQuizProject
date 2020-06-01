package mcq;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import mcq.Questions.MultipleChoiceQuestion;

import java.util.Arrays;
import java.util.List;

public class NewMCQDialog {

    private  Dialog<MultipleChoiceQuestion> dialog;
    private  Label label1 = new Label("Question: ");
    private  Label label2 = new Label("Answer: ");
    private  Label label3 = new Label("Three false answers");
    private  final TextField questionTextField = new TextField();
    private  final TextField answerTextField = new TextField();
    private  TextField falseAnswerTextField1 = new TextField();
    private  TextField falseAnswerTextField2 = new TextField();
    private  TextField falseAnswerTextField3 = new TextField();
    private  ButtonType okButton = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
    private  ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    public NewMCQDialog() {
        this.dialog = new Dialog<>();
    }

    public TextField getQuestionTextField() {
        return questionTextField;
    }

    public TextField getAnswerTextField() {
        return answerTextField;
    }

    public TextField getFalseAnswerTextField1() {
        return falseAnswerTextField1;
    }

    public TextField getFalseAnswerTextField2() {
        return falseAnswerTextField2;
    }

    public TextField getFalseAnswerTextField3() {
        return falseAnswerTextField3;
    }

    public ButtonType getOkButton() {
        return okButton;
    }

    private  void createDialogBox() {
        GridPane gridPane = new GridPane();

        dialog.setTitle("New Question");
        dialog.setResizable(true);

        gridPane.add(label1, 0, 0);
        gridPane.add(questionTextField, 0, 1);
        gridPane.add(label2, 0, 2);
        gridPane.add(answerTextField, 0, 3);
        gridPane.add(label3, 0, 4);
        gridPane.add(falseAnswerTextField1, 0, 5);
        gridPane.add(falseAnswerTextField2, 0, 6);
        gridPane.add(falseAnswerTextField3, 0, 7);
        gridPane.setStyle("-fx-padding: 10");
        onActionDialogBox(gridPane);

    }

    private  void onActionDialogBox(GridPane gridPane) {

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().setAll(okButton, cancelButton);

        questionTextField.setOnKeyReleased(e -> okayButtonRelease());
        answerTextField.setOnKeyReleased(e -> okayButtonRelease());
        falseAnswerTextField1.setOnKeyReleased(e -> okayButtonRelease());
        falseAnswerTextField2.setOnKeyReleased(e -> okayButtonRelease());
        falseAnswerTextField3.setOnKeyReleased(e -> okayButtonRelease());

        boolean disableButton = questionTextField.getText().isEmpty() || questionTextField.getText().trim().isEmpty() ||
                answerTextField.getText().isEmpty() || answerTextField.getText().trim().isEmpty() ||
                falseAnswerTextField1.getText().isEmpty() || falseAnswerTextField1.getText().trim().isEmpty() ||
                falseAnswerTextField2.getText().isEmpty() || falseAnswerTextField2.getText().trim().isEmpty() ||
                falseAnswerTextField3.getText().isEmpty() || falseAnswerTextField3.getText().trim().isEmpty();

        dialog.getDialogPane().lookupButton(okButton).setDisable(disableButton);

        dialog.setResultConverter(new Callback<ButtonType, MultipleChoiceQuestion>() {
            @Override
            public MultipleChoiceQuestion call(ButtonType param) {
//                dialog.getDialogPane().lookupButton(okButton).setDisable(true);
                if (param == okButton) {
                    String answerText = answerTextField.getText();
                    String questionText = questionTextField.getText();
                    List<String> answerOptions = Arrays.asList(answerText,falseAnswerTextField1.getText(),
                            falseAnswerTextField2.getText(),
                            falseAnswerTextField3.getText());
                    dialog.getDialogPane().lookupButton(okButton).setDisable(true);
                    clearFields();
                    return new MultipleChoiceQuestion(questionText,answerText, answerOptions);
                }
                clearFields();
                return null;
            }
        });
    }
    private void clearFields(){
        questionTextField.clear();
        answerTextField.clear();
        falseAnswerTextField1.clear();
        falseAnswerTextField2.clear();
        falseAnswerTextField3.clear();

    }

    public  Dialog<MultipleChoiceQuestion> getDialog() {
        createDialogBox();
        return dialog;
    }

    private  void okayButtonRelease(){
        boolean disableButton = questionTextField.getText().isEmpty() || questionTextField.getText().trim().isEmpty() ||
                answerTextField.getText().isEmpty() || answerTextField.getText().trim().isEmpty() ||
                falseAnswerTextField1.getText().isEmpty() || falseAnswerTextField1.getText().trim().isEmpty() ||
                falseAnswerTextField2.getText().isEmpty() || falseAnswerTextField2.getText().trim().isEmpty() ||
                falseAnswerTextField3.getText().isEmpty() || falseAnswerTextField3.getText().trim().isEmpty();
        dialog.getDialogPane().lookupButton(okButton).setDisable(disableButton);

    }
}
