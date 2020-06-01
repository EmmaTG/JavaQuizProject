package mcq;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import mcq.Questions.TrueFalse;

public class NewTrueFalseDialog{

    private  Dialog<TrueFalse> dialog;
    private  Label label1 = new Label("Question: ");
    private  Label label2 = new Label("Answer: ");
    private  TextField questionTextField = new TextField();
//    private  ToggleButton trueToggle = new ToggleButton("True");
//    private  ToggleButton falseToggle = new ToggleButton("False");
    private  RadioButton trueToggle = new RadioButton("True");
    private  RadioButton falseToggle = new RadioButton("False");
    private  ButtonType okButton = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
    private  ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    public NewTrueFalseDialog() {
        this.dialog = new Dialog<>();
    }

    public TextField getQuestionTextField() {
        return questionTextField;
    }

    public RadioButton getTrueToggle() {
        return trueToggle;
    }

    public RadioButton getFalseToggle() {
        return falseToggle;
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
        gridPane.add(trueToggle, 0, 3);
        gridPane.add(falseToggle, 1, 3);
        gridPane.setStyle("-fx-padding: 10");

        onActionDialogBox(gridPane);
    }
    private  void onActionDialogBox(GridPane gridPane){

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().setAll(okButton,cancelButton);

        questionTextField.setOnKeyReleased(e-> okayButtonRelease());
        trueToggle.setOnAction(e -> okayButtonRelease());
        falseToggle.setOnAction(e -> okayButtonRelease());
        dialog.getDialogPane().lookupButton(okButton).setDisable(questionTextField.getText().isEmpty() || questionTextField.getText().trim().isEmpty() ||
                (!trueToggle.isSelected() && !falseToggle.isSelected()));

        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().setAll(trueToggle,falseToggle);
        dialog.setResultConverter(new Callback<ButtonType, TrueFalse>() {
            @Override
            public TrueFalse call(ButtonType param) {
                if (param==okButton){
                    TrueFalse newTFQ;
                    if (trueToggle.isSelected()) {
                        newTFQ = new TrueFalse(questionTextField.getText(), "True");
                        dialog.getDialogPane().lookupButton(okButton).setDisable(true);
                        clearFields();
                        return newTFQ;
                    } else if (falseToggle.isSelected()){
                        newTFQ =new TrueFalse(questionTextField.getText(), "False");
                        dialog.getDialogPane().lookupButton(okButton).setDisable(true);
                        clearFields();
                        return newTFQ;
                    }
                }
                clearFields();
                return null;
            }
        });
    }

    private void clearFields(){
        questionTextField.clear();
        trueToggle.setSelected(false);
        falseToggle.setSelected(false);
    }

    public  Dialog<TrueFalse> getDialog() {
        createDialogBox();
        return dialog;
    }


    private  void okayButtonRelease(){
        boolean disableButton = questionTextField.getText().isEmpty() || questionTextField.getText().trim().isEmpty() ||
                (!trueToggle.isSelected() && !falseToggle.isSelected());
        dialog.getDialogPane().lookupButton(okButton).setDisable(disableButton);
    }
}
