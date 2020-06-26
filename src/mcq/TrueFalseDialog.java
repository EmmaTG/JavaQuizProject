package mcq;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import mcq.Questions.TrueFalse;

import java.io.FileInputStream;
import java.io.IOException;

public class TrueFalseDialog {

    private Dialog<TrueFalse> dialog;
    private Label label1 = new Label("Question: ");
    private Label label2 = new Label("Answer: ");
    private TextField questionTextField = new TextField();
    //    private  ToggleButton trueToggle = new ToggleButton("True");
//    private  ToggleButton falseToggle = new ToggleButton("False");
    private RadioButton trueToggle = new RadioButton("True");
    private RadioButton falseToggle = new RadioButton("False");
    private ButtonType okButton = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
    private ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    private Label includeImage = new Label("Image");
    private CheckBox imageCheck = new CheckBox();
    private TextField filePath = new TextField();

    public TrueFalseDialog() {
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

    private void createDialogBox() {
        GridPane gridPane = new GridPane();

        dialog.setTitle("New Question");
        dialog.setResizable(true);


        gridPane.add(label1, 0, 0);
        gridPane.add(questionTextField, 0, 1);
        gridPane.add(label2, 0, 2);
        gridPane.add(trueToggle, 0, 3);
        gridPane.add(falseToggle, 1, 3);
        gridPane.add(new Label(" "), 0, 8);
        HBox imageHbox = new HBox(includeImage, imageCheck);
        imageHbox.setSpacing(20);
        imageCheck.setOnAction(e -> {
            if (imageCheck.isSelected()) {
                gridPane.add(filePath, 0, 10);
                dialog.setHeight(dialog.getHeight() + 30);
            } else {
                gridPane.getChildren().remove(gridPane.getChildren().size() - 1);
                dialog.setHeight(dialog.getHeight() - 30);
            }
        });
        gridPane.add(imageHbox, 0, 9);

        gridPane.setStyle("-fx-padding: 10");

        onActionDialogBox(gridPane);
    }

    private void onActionDialogBox(GridPane gridPane) {

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().setAll(okButton, cancelButton);

        questionTextField.setOnKeyReleased(e -> okayButtonRelease());
        trueToggle.setOnAction(e -> okayButtonRelease());
        falseToggle.setOnAction(e -> okayButtonRelease());
        dialog.getDialogPane().lookupButton(okButton).setDisable(questionTextField.getText().isEmpty() || questionTextField.getText().trim().isEmpty() ||
                (!trueToggle.isSelected() && !falseToggle.isSelected()));

        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().setAll(trueToggle, falseToggle);
        dialog.setResultConverter(new Callback<ButtonType, TrueFalse>() {
            @Override
            public TrueFalse call(ButtonType param) {
                if (param == okButton) {
                    if (trueToggle.isSelected()) {
                        return createTFQuestion("True");
                    } else if (falseToggle.isSelected()) {
                        return createTFQuestion("False");
                    }
                }

                clearFields();
                return null;
            }
        });
    }

    private TrueFalse createTFQuestion(String correctAnswer) {
        TrueFalse newTFQ;
        if (imageCheck.isSelected() && !filePath.getText().trim().isEmpty()) {
            try {
                Image image = new Image(new FileInputStream(filePath.getText()));
                return new TrueFalse(questionTextField.getText(), correctAnswer, image);
            } catch (IOException e) {
                System.out.println("Error: Could not find image - " + e.getMessage());
                return new TrueFalse(questionTextField.getText(), correctAnswer);
            }
        }
        newTFQ = new TrueFalse(questionTextField.getText(), correctAnswer);
        dialog.getDialogPane().lookupButton(okButton).setDisable(true);
        clearFields();
        return newTFQ;
    }

    private void clearFields() {
        questionTextField.clear();
        trueToggle.setSelected(false);
        falseToggle.setSelected(false);
    }

    public Dialog<TrueFalse> getDialog() {
        createDialogBox();
        return dialog;
    }


    private void okayButtonRelease() {
        boolean disableButton = questionTextField.getText().isEmpty() || questionTextField.getText().trim().isEmpty() ||
                (!trueToggle.isSelected() && !falseToggle.isSelected());
        dialog.getDialogPane().lookupButton(okButton).setDisable(disableButton);
    }
}
