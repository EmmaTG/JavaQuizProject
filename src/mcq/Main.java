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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mcq.Data.PreMadeQuizOptions;
import mcq.Data.QuestionDataSource;
import mcq.Data.SelectCategoryStage;
import mcq.QuestionScenes.QuestionScene;
import mcq.QuestionScenes.WriteInQuestionScene;
import mcq.Questions.*;

import java.util.*;

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

        homeScreen("Welcome!");

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
        ObservableList<Question> observableListOfQuestions = FXCollections.observableArrayList(listOfQuestions);
        FXCollections.shuffle(observableListOfQuestions);

        ObservableList<QuestionScene> questionScenesObservList;
        List<QuestionScene> questionScenes = new ArrayList<>();


        QuestionScene questionScene;
        QuestionScene.resetQuestionNumbers();
        WriteInQuestionScene writeInQuestionScene;


        for (Question q : observableListOfQuestions) {
            if (q instanceof MultipleChoiceQuestion) {
                questionScene = new QuestionScene(q);
                questionScenes.add(questionScene);
            } else if (q instanceof WriteInQuestion) {
                writeInQuestionScene = new WriteInQuestionScene(q);
                questionScenes.add(writeInQuestionScene);
            }
        }

        questionScenesObservList = FXCollections.observableArrayList(questionScenes);
        return questionScenesObservList;
    }

    private static boolean createQuestion(Quiz newQuiz, questionType type){
        Dialog<? extends Question> dialog = null;
        if (type == questionType.MCQ){
            dialog = new NewMCQDialog().getDialog();
        } else if ( type == questionType.TF){
            dialog = new NewTrueFalseDialog().getDialog();
        } else if (type == questionType.WI){
            dialog= new NewWriteInDialog().getDialog();
        }
        if (dialog != null) {;
            Optional<? extends Question> result1 = dialog.showAndWait();
            if (result1.isPresent()) {
                Question question = result1.get();
                newQuiz.addQuestion(question);
                return true;
            }
        }
        return true;
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

//        doneButton.setDisable(true);

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
                //        Stage qsStage;
                Stage qsStage;
                qs.setPossibleAnswers();
                ObservableList<Button> questionButtons = qs.getPossibleAnswers();

                ProgressBar progressBar = new ProgressBar(((double) count / (double) listOfQuestionScenes.size()));
                progressBar.prefWidth(Double.MAX_VALUE);
                Label progressLabel = new Label();
                IntegerProperty intProperty = new SimpleIntegerProperty(count);
                progressLabel.textProperty().bind(intProperty.asString());
                Label progressLabel2 = new Label("/" + listOfQuestionScenes.size());
                count++;
                HBox progressHBox = new HBox(progressBar,progressLabel,progressLabel2);
                progressHBox.setAlignment(Pos.CENTER);
//                gridPane.add(progressHBox, 0, 1);

                qs.setQuestionWindow(questionButtons,progressHBox);

                qsStage = qs.getQuestionStage();

                qsStage.setOnCloseRequest(we -> {
                    windowClose = true;
                    homeScreen = true;

                });

                Alert correctAlert = new Alert(Alert.AlertType.INFORMATION);
                correctAlert.setHeaderText("Correct!");
                Alert inCorrectAlert = new Alert(Alert.AlertType.INFORMATION);
                inCorrectAlert.setHeaderText("Incorrect!");

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

            Alert finalAlert = new Alert(Alert.AlertType.CONFIRMATION);
            finalAlert.setTitle("Results");
            finalAlert.setHeaderText("Quiz Complete!");
            finalAlert.setContentText(sb.toString());

            ButtonType quitButton = new ButtonType("Quit");
            ButtonType newQuiz = new ButtonType("Start a new quiz");

            finalAlert.getButtonTypes().setAll(newQuiz, quitButton);

            finalAlert.setOnCloseRequest(e -> finalAlert.close());

            Optional<ButtonType> result = finalAlert.showAndWait();
            if (result.get() == newQuiz) {
                finalAlert.close();
                homeScreen("");
            } else {
                Platform.exit();
            }
        } else if (windowClose && homeScreen) {
            homeScreen("");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}