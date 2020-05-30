package mcq;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import mcq.Questions.WriteInQuestion;

public class NewWriteInDialog {

    private  Dialog<WriteInQuestion> dialog;
    private  Label label1 = new Label("Question: ");
    private  Label label2 = new Label("Answer: ");
    private  TextField questionTextField = new TextField();
    private  TextField answerTextField = new TextField();
    private  ButtonType okButton = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
    private  ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    public NewWriteInDialog() {
        this.dialog = new Dialog<>();
    }

    public TextField getQuestionTextField() {
        return questionTextField;
    }

    public TextField getAnswerTextField() {
        return answerTextField;
    }

    private  void createDialogBox() {
        GridPane gridPane = new GridPane();

        dialog.setTitle("New Question");
        dialog.setResizable(true);

        gridPane.add(label1, 0, 0);
        gridPane.add(questionTextField, 0, 1);
        gridPane.add(label2, 0, 2);
        gridPane.add(answerTextField, 0, 3);
        gridPane.setStyle("-fx-padding: 10");


        onActionDialogBox(gridPane);
    }

    private  void onActionDialogBox(GridPane gridPane) {

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().setAll(okButton, cancelButton);


        dialog.getDialogPane().lookupButton(okButton).setDisable(true);
        questionTextField.setOnKeyReleased(e -> okayButtonRelease());
        answerTextField.setOnKeyReleased(e -> okayButtonRelease());
        dialog.setResultConverter(new Callback<ButtonType, WriteInQuestion>() {
            @Override
            public WriteInQuestion call(ButtonType param) {
                if (param == okButton) {
                    WriteInQuestion newWIQ = new WriteInQuestion(questionTextField.getText(), answerTextField.getText());
                    clearFields();
                    return newWIQ;

                }
                clearFields();
                return null;
            }
        });

    }

    private void clearFields(){
        questionTextField.clear();
        answerTextField.clear();
    }

    public  Dialog<WriteInQuestion> getDialog() {
        createDialogBox();
        return dialog;
    }
    private  void okayButtonRelease(){
        boolean disableButton = questionTextField.getText().isEmpty() || questionTextField.getText().trim().isEmpty() ||
                answerTextField.getText().isEmpty() || answerTextField.getText().trim().isEmpty();
        dialog.getDialogPane().lookupButton(okButton).setDisable(disableButton);
    }
}


