package mcq;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import mcq.Questions.WriteInQuestion;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class WriteInDialog {

    private  Dialog<WriteInQuestion> dialog;
    private  Label label1 = new Label("Question: ");
    private  Label label2 = new Label("Answer: ");
    private  TextField questionTextField = new TextField();
    private  TextField answerTextField = new TextField();
    private  ButtonType okButton = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
    private  ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    private Label includeImage = new Label("Image");
    private CheckBox imageCheck = new CheckBox();
    private TextField filePath = new TextField();
    private GridPane gridPane = new GridPane();


    public WriteInDialog() {
        this.dialog = new Dialog<>();
    }


    public void createDialogBox() {
        dialog.setTitle("New Question");
        dialog.setResizable(true);

        gridPane.add(label1, 0, 0);
        gridPane.add(questionTextField, 0, 1);
        gridPane.add(label2, 0, 2);
        gridPane.add(answerTextField, 0, 3);
        gridPane.add(new Label(" "),0,8);
        HBox imageHbox = new HBox(includeImage, imageCheck);
        imageHbox.setSpacing(20);
        imageCheck.setOnAction(e ->{
            if (imageCheck.isSelected()) {
                gridPane.add(filePath, 0, 10);
                dialog.setHeight(dialog.getHeight() + 30);
            }
            else {
                gridPane.getChildren().remove(gridPane.getChildren().size()-1);
                dialog.setHeight(dialog.getHeight() - 30);
            }
        });
        gridPane.add(imageHbox,0,9);
        gridPane.setStyle("-fx-padding: 10");

        onActionDialogBox(gridPane);
    }


    public void onActionDialogBox(GridPane gridPane) {

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().setAll(okButton, cancelButton);


        dialog.getDialogPane().lookupButton(okButton).setDisable(questionTextField.getText().isEmpty() || questionTextField.getText().trim().isEmpty() ||
                answerTextField.getText().isEmpty() || answerTextField.getText().trim().isEmpty());
        questionTextField.setOnKeyReleased(e -> okayButtonRelease());
        answerTextField.setOnKeyReleased(e -> okayButtonRelease());
        dialog.setResultConverter(new Callback<ButtonType, WriteInQuestion>() {
            @Override
            public WriteInQuestion call(ButtonType param) {
                if (param == okButton) {
                    if (imageCheck.isSelected() && !filePath.getText().trim().isEmpty()) {
                        try {
                            String filePathString = filePath.getText();
                            List<String> splitString = Arrays.asList(filePathString.split("/"));
                            String fileName = splitString.get(splitString.size()-1);
                            String newImagePath = "./src/mcq/Images/" + fileName;
                            Files.copy(Paths.get(filePathString),Paths.get(newImagePath), StandardCopyOption.REPLACE_EXISTING);
                            Files.copy(Paths.get(filePathString), Paths.get("./out/production/JavaQuizProject/mcq/Images/" + fileName), StandardCopyOption.REPLACE_EXISTING);

                            Image image = new Image(new FileInputStream(newImagePath));
                            filePath.clear();
                            return new WriteInQuestion(questionTextField.getText(), answerTextField.getText(), filePathString, image);
                        } catch (IOException e) {
                            System.out.println("Error: Could not find image - " + e.getMessage());
                            return new WriteInQuestion(questionTextField.getText(), answerTextField.getText());
                        }
                    }
                    WriteInQuestion newWIQ = new WriteInQuestion(questionTextField.getText(), answerTextField.getText());
                    clearFields();
                    return newWIQ;

                }
                clearFields();
                return null;
            }
        });

    }


    public void clearFields() {
        questionTextField.clear();
        imageCheck.setSelected(false);
        filePath.clear();
        answerTextField.clear();
    }

    public void okayButtonRelease(){
        boolean disableButton = questionTextField.getText().isEmpty() || questionTextField.getText().trim().isEmpty() ||
                answerTextField.getText().isEmpty() || answerTextField.getText().trim().isEmpty();
        dialog.getDialogPane().lookupButton(okButton).setDisable(disableButton);
    }

    public  Dialog<WriteInQuestion> getDialog() {
        createDialogBox();
        return dialog;
    }

    public TextField getQuestionTextField() {
        return questionTextField;
    }

    public TextField getAnswerTextField() {
        return answerTextField;
    }

    public CheckBox getImageCheck() {
        return imageCheck;
    }

    public TextField getFilePath() {
        return filePath;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public ButtonType getOkButton() {
        return okButton;
    }
}


