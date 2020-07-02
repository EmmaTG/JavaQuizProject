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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import mcq.Data.QuestionDataSource;
import mcq.Data.SelectQuizList;
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




    @Override
    public void start(Stage primaryStage) throws Exception {
//        Quiz testQuiz = new Quiz("Test");
//        List<Question> questionList = new ArrayList<>();
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
//        TrueFalseDialog dialog3 = new TrueFalseDialog();
//        Optional<TrueFalse> tfResult = dialog3.getDialog().showAndWait();
//        if (tfResult.isPresent()){
//            TrueFalse tf = tfResult.get();
//            questionList.add(tf);
//        }
//
//        runQuiz(FXCollections.observableArrayList(createQuestionScenes(questionList)));

        Stage newStage = new Stage();
        homeScreen("Welcome!",newStage);

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

    public static void homeScreen(String message, Stage stage) {
        windowClose = false;
        homeScreen = false;

        OpeningScene openingScene = new OpeningScene();
        openingScene.setWelcomeLabelText(message);

        //1. Create a new quiz
        Button createQuizButton = openingScene.getCreateQuiz();
        createQuizButton.onActionProperty().setValue(event -> {
//            stage.close();
//            window.close();
            createQuiz(window);
        });

        //2. Use an existing quiz from the database
        Button existingQuizButton = openingScene.getExistingQuiz();
        existingQuizButton.onActionProperty().setValue(e -> {
//            window.close();
            selectQuiz(window);
        });

        //3. Quit Application
        Button quitQuizButton = openingScene.getQuitQuiz();
        quitQuizButton.onActionProperty().setValue(event -> {
            window.close();
        });
//            stage.setScene(op);
        window.setScene(openingScene.getScene());
        window.show();
    }

    private static void createQuiz(Stage stage){

        // Get scene for create quiz name
        CreateNewQuizStage nameQuiz = new CreateNewQuizStage();
        Scene nameQuizStage = nameQuiz.getNewScene();
        TextField quizNameTextField = nameQuiz.getQuizNameTextField();
        quizNameTextField.setOnAction((e) -> {
            nameQuiz.setQuizTitle(quizNameTextField.getText());
            CreateQuizQuestionsStage createQuizQuestions = new CreateQuizQuestionsStage(nameQuiz.getQuizTitle());
            Scene createQuizQuestionsStage = createQuizQuestions.getNewScene(stage);
            stage.setScene(createQuizQuestionsStage);
        });

        stage.setScene(nameQuizStage);
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



    private static void selectCategory(){

    }

    private static void selectQuiz(Stage stage) {
        Scene newScene;

        newScene = SelectQuizList.createScene();

        Button startButton = SelectQuizList.getStartButton();
        startButton.setOnAction((e) -> {
            stage.close();
            QuestionDataSource.getInstance().queryQuizQuestion(SelectQuizList.getSelectedItem());
            List<Question> selectedQuizQuestions = QuestionDataSource.getInstance().getQuizQuestions();

            if (selectedQuizQuestions != null){
                ObservableList<QuestionScene> questionScenes = createQuestionScenes(selectedQuizQuestions);
                runQuiz(questionScenes);
            };
        });

        Button editButton = SelectQuizList.getEditButton();
        editButton.setOnAction((e) -> {
                    String selectedQuiz = SelectQuizList.getSelectedItem();
                    QuestionDataSource.getInstance().queryQuizQuestion(selectedQuiz);
                    List<Question> listOfQuestions = QuestionDataSource.getInstance().getQuizQuestions();
                    CreateQuizQuestionsStage createQuizQuestions = new CreateQuizQuestionsStage(selectedQuiz,listOfQuestions);
                    Scene createQuizQuestionsStage = createQuizQuestions.getNewScene(stage);
                    stage.setScene(createQuizQuestionsStage);
                }
                );
        stage.setScene(newScene);

    }

    private static HBox setProgressBar(int counter, int listOfQuestionScenes){
        ProgressBar progressBar = new ProgressBar(((double) counter / (double) listOfQuestionScenes));
        progressBar.prefWidth(Double.MAX_VALUE);
        Label progressLabel = new Label();
        IntegerProperty intProperty = new SimpleIntegerProperty(counter);
        progressLabel.textProperty().bind(intProperty.asString());
        Label progressLabel2 = new Label("/" + listOfQuestionScenes);
        HBox progressHBox = new HBox(progressBar,progressLabel,progressLabel2);
        progressHBox.setAlignment(Pos.CENTER);
        return  progressHBox;
    }

    public static void runQuiz(ObservableList<QuestionScene> listOfQuestionScenes) {
        windowClose = false;
        correctAnswers = 0;
        int count = 0;
        for (QuestionScene qs : listOfQuestionScenes) {
            if (!windowClose) {

                Stage qsStage = new Stage();

                HBox progressBar = setProgressBar(count, listOfQuestionScenes.size());
                count++;

                qs.setPossibleAnswers();
                ObservableList<Button> questionButtons = qs.getPossibleAnswers();
                qs.setQuestionWindow(questionButtons, progressBar);

                qsStage.setScene(qs.getQuestionScene());

                qsStage.setTitle("Question " + count + "of " +listOfQuestionScenes.size());

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
                        b.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                if (b.getText().equalsIgnoreCase(qs.getQuestion().getCorrectAnswer())) {
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
            qsStage.showAndWait();
//                window.showAndWait();
            }
        }

        if (!windowClose) {
            String s =String.format("Your Score: %d / %d \n \t %d %%", correctAnswers, listOfQuestionScenes.size(),
                    Math.round(((double) correctAnswers / listOfQuestionScenes.size()) * 100.00));
            resultsSummary(s);

        } else if (windowClose && homeScreen) {
            homeScreen("",window);
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
        if (result.isPresent()) {
            if (result.get().getText().equalsIgnoreCase("Start a new quiz")) {
                finalAlert.close();
                homeScreen("",window);
            } else {
                Platform.exit();
            }
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