package mcq;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mcq.Data.PreMadeQuizOptions;
import mcq.Data.QuestionDataSource;
import mcq.Data.SelectCategoryStage;
import mcq.QuestionScenes.QuestionScene;
import mcq.QuestionScenes.WriteInQuestionScene;
import mcq.Questions.MultipleChoiceQuestion;
import mcq.Questions.Question;
import mcq.Questions.TrueFalse;
import mcq.Questions.WriteInQuestion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main extends Application {
//public class Main {

    public static int correctAnswers;
    public static int sceneWidth = 400;
    public static int sceneHeight = 500;
    public static Stage window = new Stage();
    public static boolean windowClose;
    public static boolean homeScreen;
    public static Stage typeOfQStage = new Stage();

    enum questionType {
        MCQ,
        TF,
        WI
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Quiz testQuiz = new Quiz("Test");
        List<Question> questionList = new ArrayList<>();
//        MCQDialog dialogObject = new MCQDialog();
//       Optional<MultipleChoiceQuestion> result = dialogObject.getDialog().showAndWait();
//       if (result.isPresent()){
//           MultipleChoiceQuestion mcq = result.get();
//            questionList.add(mcq);
//       }
//       WriteInDialog dialogObject2 = new WriteInDialog();
//        Optional<WriteInQuestion> result2 = dialogObject2.getDialog().showAndWait();
//        if (result2.isPresent()){
//            WriteInQuestion q = result2.get();
//            questionList.add(q);
//        }
//
        TrueFalseDialog dialog3 = new TrueFalseDialog();
        Optional<TrueFalse> tfResult = dialog3.getDialog().showAndWait();
        if (tfResult.isPresent()){
            TrueFalse tf = tfResult.get();
            questionList.add(tf);
        }

        runQuiz(FXCollections.observableArrayList(createQuestionScenes(questionList)));

//        homeScreen("Welcome!");

    }

    @Override
    public void init() throws Exception {

        if (!QuestionDataSource.getInstance().open()) {
            System.out.println("Fatal Error! Cannot open Data base");
            Platform.exit();
        }
    }

    @Override
    public void stop() throws Exception {
        QuestionDataSource.getInstance().close();
    }

    public static void homeScreen(String message) {
        windowClose = false;
        homeScreen = false;

        OpeningScene openingScene = new OpeningScene();
        Button createQuizButton = openingScene.getCreateQuiz();
        createQuizButton.setMaxWidth(Double.MAX_VALUE);
        openingScene.setWelcomeLabelText(message);
        Button existingQuizButton = openingScene.getExistingQuiz();
        existingQuizButton.setMaxWidth(Double.MAX_VALUE);
        Button quitQuizButton = openingScene.getQuitQuiz();
        quitQuizButton.setMaxWidth(Double.MAX_VALUE);


        createQuizButton.onActionProperty().setValue(e -> {
            window.close();
            CreateNewQuizStage nameQuiz = new CreateNewQuizStage();
            Stage nameQuizStage = nameQuiz.getNewStage();
            nameQuizStage.showAndWait();

            CreateQuizQuestionsStage createQuizQuestions = new CreateQuizQuestionsStage();
            createQuizQuestions.setCreatedQuiz(nameQuiz.getQuizTitle());
            Stage createQuizQuestionsStage = createQuizQuestions.getNewStage();
            createQuizQuestionsStage.showAndWait();
        });

        existingQuizButton.onActionProperty().setValue(e -> {
            window.close();
            selectQuiz();
        });
        quitQuizButton.onActionProperty().setValue(event -> {
            window.close();
        });


        window.setScene(openingScene.getScene());
        window.show();
    }

    public static ObservableList<QuestionScene> createQuestionScenes(List<Question> listOfQuestions) {

        QuestionScene.resetQuestionNumbers();

        ObservableList<Question> observableListOfQuestions = FXCollections.observableArrayList(listOfQuestions);
        FXCollections.shuffle(observableListOfQuestions);

        QuestionScene questionScene;
        WriteInQuestionScene writeInQuestionScene;
        List<QuestionScene> questionScenes = new ArrayList<>();
        for (Question q : observableListOfQuestions) {
            if (q instanceof MultipleChoiceQuestion) {
                questionScene = new QuestionScene(q);
                questionScenes.add(questionScene);
            } else if (q instanceof WriteInQuestion) {
                writeInQuestionScene = new WriteInQuestionScene(q);
                questionScenes.add(writeInQuestionScene);
            }
        }

        ObservableList<QuestionScene> questionScenesObservList = FXCollections.observableArrayList(questionScenes);
        return questionScenesObservList;
    }

    private static boolean createQuestion(Quiz newQuiz, questionType type){
        Dialog<? extends Question> dialog = null;
        if (type == questionType.MCQ){
            dialog = new MCQDialog().getDialog();
        } else if ( type == questionType.TF){
            dialog = new TrueFalseDialog().getDialog();
        } else if (type == questionType.WI){
            dialog= new WriteInDialog().getDialog();
        }
        if (dialog != null) {;
            Optional<? extends Question> result1 = dialog.showAndWait();
            if (result1.isPresent()) {
                Question question = result1.get();
                newQuiz.addQuestion(question);
                return true;
            }
        }
        return false;
    }

    private static void selectCategory(){

    }

    private static void selectQuiz() {

        Stage selectCategoryStage = SelectCategoryStage.getCategoryStage();
        selectCategoryStage.showAndWait();

        Stage selectQuizStage = PreMadeQuizOptions.getNewStage();
        selectQuizStage.showAndWait();

        List<Question> selectedQuizQuestions = QuestionDataSource.getInstance().getQuizQuestions();

        if (selectedQuizQuestions != null){
        ObservableList<QuestionScene> questionScenes = createQuestionScenes(selectedQuizQuestions);
        runQuiz(questionScenes);
        }

        homeScreen("");

    }

    public static void typeOfQuestionToCreate(Quiz newQuiz) {
        IntegerProperty property = new SimpleIntegerProperty(newQuiz.getQuestions().size());
        typeOfQStage = new Stage();
        Label typeofQ = new Label("What type of question would you like to create?");
        typeofQ.setWrapText(true);
        Button mcqButton = new Button("Multiple Choice");
        mcqButton.setMaxWidth(Double.MAX_VALUE);
        Button tfButton = new Button("True/False");
        tfButton.setMaxWidth(Double.MAX_VALUE);
        Button wiButton = new Button("Write in Question");
        wiButton.setMaxWidth(Double.MAX_VALUE);

        Button doneButton = new Button("Done");
        doneButton.setMaxWidth(Double.MAX_VALUE);
        doneButton.disableProperty().bind(property.lessThan(1));

        Button quitButton = new Button("Cancel");
        quitButton.setMaxWidth(Double.MAX_VALUE);

        Label numberOfQuestions = new Label();
        numberOfQuestions.textProperty().bind(property.asString());
        Label numberOfQuestions2 = new Label(" Questions created");

        GridPane gridPane = new GridPane();
        VBox vBox = new VBox(typeofQ, mcqButton, tfButton, wiButton, new HBox(quitButton, doneButton, numberOfQuestions, numberOfQuestions2));
        gridPane.add(vBox, 0, 0);
        gridPane.setVgap(20);
        gridPane.setHgap(20);
        gridPane.setStyle("-fx-padding: 50");

        mcqButton.setOnAction(e -> {
            if (createQuestion(newQuiz, questionType.MCQ)) {
                property.set(property.get() + 1);
            }
            return;
        });
        tfButton.setOnAction(e -> {
            if (createQuestion(newQuiz, questionType.TF)) {
                property.set(property.get() + 1);
            }
            return;
        });
        wiButton.setOnAction(e -> {
            if (createQuestion(newQuiz,questionType.WI)) {
                property.set(property.get() + 1);
            }
            return;
        });
        doneButton.setOnAction(e -> {
            CreateQuizQuestionsStage.setObservableQuestionList(newQuiz.getQuestions());
//            createQuestionScenes(newQuiz.getQuestions());
            windowClose = true;
            homeScreen = true;
            typeOfQStage.close();
            return;
        });

        quitButton.setOnAction(e -> {
            typeOfQStage.close();
            windowClose = true;
            homeScreen = true;
            return;
        });


        typeOfQStage.setOnCloseRequest(e -> {
            windowClose = true;
            homeScreen = true;
            return;
        });

        Scene typeOfQScene = new Scene(gridPane);
        typeOfQStage.setScene(typeOfQScene);
        typeOfQStage.setTitle("Type of question");
        typeOfQStage.showAndWait();
    }

    public static void runQuiz(ObservableList<QuestionScene> listOfQuestionScenes) {
        windowClose = false;
        correctAnswers = 0;

        int count = 0;
        for (QuestionScene qs : listOfQuestionScenes) {
            if (!windowClose) {

                Stage qsStage;
                qs.setPossibleAnswers();

                ProgressBar progressBar = new ProgressBar(((double) count / (double) listOfQuestionScenes.size()));
                progressBar.prefWidth(Double.MAX_VALUE);
                Label progressLabel = new Label();
                IntegerProperty intProperty = new SimpleIntegerProperty(count);
                progressLabel.textProperty().bind(intProperty.asString());
                Label progressLabel2 = new Label("/" + listOfQuestionScenes.size());
                count++;
                HBox progressHBox = new HBox(progressBar,progressLabel,progressLabel2);
                progressHBox.setAlignment(Pos.CENTER);

                ObservableList<Button> questionButtons = qs.getPossibleAnswers();
                qs.setQuestionWindow(questionButtons, progressHBox);

                qsStage = qs.getQuestionStage();

                qsStage.setOnCloseRequest(we -> {
                    windowClose = true;
                    homeScreen = true;

                });

                // Correct answer dialog
                Alert correctAlert = createAlert("Correct!","Well done!","/home/etg/Desktop/GIT/JavaQuizProject/src/mcq/correctImage.png");
                correctAlert.getDialogPane().getStyleClass().add("correctDialog");

                // Incorrect answer dialog
                Alert inCorrectAlert = createAlert("Incorrect!", "Whoops!", "/home/etg/Desktop/GIT/JavaQuizProject/src/mcq/incorrectImage.png");
                inCorrectAlert.getDialogPane().getStyleClass().add("incorrectDialog");

                FXCollections.shuffle(questionButtons);


                if (qs.getQuestion() instanceof MultipleChoiceQuestion || qs.getQuestion() instanceof TrueFalse) {
                    for (Button b : questionButtons) {
                        if (b.getText().equalsIgnoreCase(qs.getQuestion().getCorrectAnswer())) {
                            b.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    correctAlert.showAndWait();
                                    correctAnswers++;
                                    qsStage.close();
                                }
                            });
                        } else {
                            b.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    inCorrectAlert.setContentText("The answer is " + qs.getQuestion().getCorrectAnswer());
                                    inCorrectAlert.showAndWait();
                                    qsStage.close();
                                }
                            });
                        }
                    }
                }


                if (qs.getQuestion() instanceof WriteInQuestion) {
                    TextField answerField = ((WriteInQuestionScene) qs).getAnswerField();
                    answerField.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            if (answerField.getText().equalsIgnoreCase(qs.getQuestion().getCorrectAnswer())) {
                                correctAlert.showAndWait();
                                correctAnswers++;
                                qsStage.close();
                            } else {
                                inCorrectAlert.setContentText("The answer is " + qs.getQuestion().getCorrectAnswer());
                                inCorrectAlert.showAndWait();
                                qsStage.close();
                            }
                        }
                    });
                }

                qsStage.setTitle("Question " + count + "of " +listOfQuestionScenes.size());
                qsStage.showAndWait();
//            } else {
////                homeScreen(""); // If uncommented it will bring up the homescreen page when the close window button
//                // is pressed, alternatively if commented, the program will exit.
            }
        }

        if (!windowClose) {
            StringBuilder sb = new StringBuilder();
            sb.append("Your Score:" + correctAnswers + "/" + listOfQuestionScenes.size());
            sb.append("\n");
            sb.append("\t" + (((float) correctAnswers / listOfQuestionScenes.size()) * 100) + "%\t");

            resultsSummary(sb.toString());

        } else if (windowClose && homeScreen) {
            homeScreen("");
        }
    }

    private static Alert createAlert(String headerText, String titleText, String imagePath){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(headerText);
        alert.setTitle(titleText);
        try{
            ImageView imageView = getImageNode(imagePath);
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            alert.setGraphic(imageView);
        } catch (FileNotFoundException e) {
            System.out.println("Correct image file not found: " + e.getMessage());
        }

        alert.getDialogPane().getStylesheets().add(Main.class.getResource("AlertsCSS.css").toExternalForm());

        return alert;
    }

    private static void resultsSummary(String contentText){
        Alert finalAlert = new Alert(Alert.AlertType.CONFIRMATION);
        finalAlert.setTitle("Results");
        finalAlert.setHeaderText("Quiz Complete!");
        finalAlert.setContentText(contentText);

        ButtonType quitButton = new ButtonType("Quit");
        ButtonType newQuiz = new ButtonType("Start a new quiz");

        finalAlert.getButtonTypes().setAll(newQuiz, quitButton);

        finalAlert.setOnCloseRequest(e -> finalAlert.close());
        Optional<ButtonType> result = finalAlert.showAndWait();
        if (result.get().getText().equalsIgnoreCase("Start a new quiz")) {
            finalAlert.close();
            homeScreen("");
        } else {
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static ImageView getImageNode(String filePath) throws FileNotFoundException{
        Image image = new Image(new FileInputStream(filePath));
        // Setting new image
        return new ImageView(image);

    }
}