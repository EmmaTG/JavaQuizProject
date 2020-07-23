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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Main extends Application {
//public class Main {

    private static int correctAnswers;
//    public static int sceneWidth = 400;
//    public static int sceneHeight = 500;
    private static Stage window;
    private static boolean windowClose;
    private static boolean homeScreen;


    @Override
    public void start(Stage primaryStage) throws Exception {

//        window = new Stage();
//        Map<String,Long> categoryMap = getCategoriesList();
//        if (categoryMap != null) {
//            List<String> listOfCategories = new ArrayList<>();
//            listOfCategories.addAll(categoryMap.keySet());
//            listOfCategories.sort((s1, s2) -> s1.compareTo(s2));
//            ListView<String> listView = new ListView<>(FXCollections.observableArrayList(listOfCategories));
//            Button doneButton = new Button("Select");
//            BorderPane bp = new BorderPane(listView);
//            bp.setRight(new VBox(doneButton));
//            doneButton.setOnAction((event) -> {
//                String selected = listView.getSelectionModel().getSelectedItem();
//                System.out.println("Category: " + selected);
//                System.out.println("Id: " + categoryMap.get(selected));
//                String apiRequest = createAPIRequest(10,categoryMap.get(selected).intValue());
//                retrieveQuestions(apiRequest);
//            });
//            Scene scene = new Scene(bp);
//            window.setScene(scene);
//            window.show();
//            String apiRequest = createAPIRequest(10,);
//            retrieveQuestions(apiRequest);

        window = new Stage();
        homeScreen("Welcome!",window);
//        }

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
        createQuizButton.onActionProperty().setValue(event -> createQuiz(stage));

        //2. Use an existing quiz from the database
        Button existingQuizButton = openingScene.getExistingQuiz();
        existingQuizButton.onActionProperty().setValue(e -> selectQuiz(stage));

        //3. Quit Application
        Button quitQuizButton = openingScene.getQuitQuiz();
        quitQuizButton.onActionProperty().setValue(event -> stage.close());
//            stage.setScene(op);
        stage.setScene(openingScene.getScene());
        stage.setTitle("Main Menu");
        stage.show();
    }

    private static void createQuiz(Stage stage) {

        // Get scene for create quiz name
        CreateNewQuizStage nameQuiz = new CreateNewQuizStage();
        Scene nameQuizStage = nameQuiz.getNewScene();
        nameQuiz.getBackButton().setOnAction((e2) -> homeScreen("", stage));
        TextField quizNameTextField = nameQuiz.getQuizNameTextField();
        EventHandler<ActionEvent> continueEvent = (e) -> {
            String quizTitle = quizNameTextField.getText();
            if (!quizNameTextField.getText().isEmpty() && !quizNameTextField.getText().trim().isEmpty()) {
                if (QuestionDataSource.getInstance().quizNameExists(quizTitle)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "This quiz name already exists. \nPlease try another name");
                    alert.show();
                } else {
                    nameQuiz.setQuizTitle(quizNameTextField.getText());

                    CreateQuizQuestionsStage createQuizQuestions = new CreateQuizQuestionsStage(nameQuiz.getQuizTitle());
                    createQuizQuestions.getCancel().setOnAction((e1) -> homeScreen("", stage));
                    createQuizQuestions.setvBox(FXCollections.observableArrayList(createQuizQuestions.getNewQuestion(),
                            createQuizQuestions.getEditButton(),
                            createQuizQuestions.getDeleteButton(),
                            createQuizQuestions.getPlayQuiz(),
                            createQuizQuestions.getSaveQuiz(),
                            createQuizQuestions.getCancel()));
                    Scene createQuizQuestionsStage = createQuizQuestions.getNewScene(stage);
                    stage.setScene(createQuizQuestionsStage);
                    stage.setTitle(nameQuiz.getQuizTitle());
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a quiz name");
                alert.show();
            }
        };

        nameQuiz.getOkButton().setOnAction(continueEvent);
        quizNameTextField.setOnAction(continueEvent);

        stage.setOnCloseRequest((e1) -> homeScreen("Welcome", stage));
        stage.setScene(nameQuizStage);
        stage.setTitle("Name of New Quiz");
    }


    private static ObservableList<QuestionScene> createQuestionScenes(List<Question> listOfQuestions) {

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
        return FXCollections.observableArrayList(questionScenes);
    }

    private static void selectQuiz(Stage stage) {
        Scene newScene;

        newScene = SelectQuizList.createScene();

        Button startButton = SelectQuizList.getStartButton();
        startButton.setOnAction((e) -> {
            stage.close();
            List<Question> selectedQuizQuestions = new ArrayList<>();
            if (SelectQuizList.getSelectedCategory().equalsIgnoreCase("Other")){
                String selected = SelectQuizList.getSelectedItem();
                Map<String,Long> categoryMap = getCategoriesList();
                if (categoryMap!=null) {
//                    System.out.println("Id: " + categoryMap.get(SelectQuizList.getSelectedItem()));
                    String apiRequest = createAPIRequest(10, categoryMap.get(selected).intValue());
                    selectedQuizQuestions =retrieveQuestions(apiRequest);
                }
            } else {
                QuestionDataSource.getInstance().queryQuizQuestion(SelectQuizList.getSelectedItem());
                selectedQuizQuestions = QuestionDataSource.getInstance().getQuizQuestions();
            }
                if (selectedQuizQuestions != null) {
                    runQuiz(selectedQuizQuestions);
                }
        });

        SelectQuizList.getHomeButton().setOnAction((e1) -> homeScreen("", stage));

        Button editButton = SelectQuizList.getEditButton();
        editButton.setOnAction((e) -> {
                    Stage newStage = new Stage();
                    String selectedQuizTitle = SelectQuizList.getSelectedItem();
                    QuestionDataSource.getInstance().queryQuizQuestion(selectedQuizTitle);
                    List<Question> listOfQuestions = QuestionDataSource.getInstance().getQuizQuestions();
                    Quiz selectedQuiz = new Quiz(selectedQuizTitle, listOfQuestions);
                    CreateQuizQuestionsStage createQuizQuestions = new CreateQuizQuestionsStage(selectedQuiz);
//                    VBox vBox = createQuizQuestions.getvBox();
                    Button doneButton = createQuizQuestions.getCancel();
                    doneButton.setText("Cancel");
                    doneButton.setOnAction((ev) -> {
                        newStage.close();
//                        selectQuiz(stage);
                    });
                    Button mainMenu = new Button("Main Menu");
                    mainMenu.setOnAction((e2) -> {
                        newStage.close();
                        homeScreen("", stage);
                    });
                    mainMenu.setMaxWidth(Double.MAX_VALUE);
                    Button saveButton = new Button("Save");
                    saveButton.setMaxWidth(Double.MAX_VALUE);
                    saveButton.setOnAction((e3) -> {
                        new Thread(() -> QuestionDataSource.getInstance().saveNewQuiz(selectedQuiz)).start();
                        newStage.close();
//                        selectQuiz(stage);
                    });
                    createQuizQuestions.setvBox(FXCollections.observableArrayList(createQuizQuestions.getNewQuestion(),
                            createQuizQuestions.getEditButton(),
                            createQuizQuestions.getDeleteButton(),
                            saveButton,
                            doneButton,
                            mainMenu));
                    Scene createQuizQuestionsStage = createQuizQuestions.getNewScene(newStage);
                    newStage.setScene(createQuizQuestionsStage);
                    newStage.showAndWait();
                }
        );
        stage.setScene(newScene);
        stage.setTitle("Select Quiz");

    }

    private static HBox setProgressBar(int counter, int listOfQuestionScenes) {
        ProgressBar progressBar = new ProgressBar(((double) counter / (double) listOfQuestionScenes));
        progressBar.prefWidth(Double.MAX_VALUE);
        Label progressLabel = new Label();
        IntegerProperty intProperty = new SimpleIntegerProperty(counter);
        progressLabel.textProperty().bind(intProperty.asString());
        Label progressLabel2 = new Label("/" + listOfQuestionScenes);
        HBox progressHBox = new HBox(progressBar, progressLabel, progressLabel2);
        progressHBox.setAlignment(Pos.CENTER);
        return progressHBox;
    }

    public static void runQuiz(List<Question> listOfQuestions) {

        window.close();

        ObservableList<QuestionScene> listOfQuestionScenes = Main.createQuestionScenes(listOfQuestions);

        windowClose = false;
        correctAnswers = 0;
        int count = 0;
        for (QuestionScene qs : listOfQuestionScenes) {
            if (!windowClose) {

                HBox progressBar = setProgressBar(count, listOfQuestionScenes.size());
                count++;

                qs.setPossibleAnswers();
                ObservableList<Button> questionButtons = qs.getPossibleAnswers();
                qs.setQuestionWindow(questionButtons, progressBar);


                window.setOnCloseRequest(we -> {
                    windowClose = true;
                    homeScreen = true;
                });

                // Correct answer dialog
                Alert correctAlert = createAlert("Correct!", "Well done!", "/home/etg/Desktop/GIT/JavaQuizProject/src/mcq/correctImage.png");
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
                                    window.close();

                                } else {
                                    inCorrectAlert.setContentText("The answer is " + qs.getQuestion().getCorrectAnswer());
                                    inCorrectAlert.showAndWait();
                                    window.close();
                                }
                            }
                        });
                    }
                }


                if (qs.getQuestion() instanceof WriteInQuestion) {
                    TextField answerField = ((WriteInQuestionScene) qs).getAnswerField();
                    answerField.setOnAction(event -> {
                            if (answerField.getText().equalsIgnoreCase(qs.getQuestion().getCorrectAnswer())) {
                                correctAlert.showAndWait();
                                correctAnswers++;
                                window.close();
                            } else {
                                inCorrectAlert.setContentText("The answer is " + qs.getQuestion().getCorrectAnswer());
                                inCorrectAlert.showAndWait();
                                window.close();
                            }
                        });
                }
                window.setScene(qs.getQuestionScene());
                window.setTitle("Question " + count + "of " + listOfQuestionScenes.size());
                window.showAndWait();
                window.close();
            }
        }

        if (!windowClose) {
            String s = String.format("Your Score: %d / %d \n \t %d %%", correctAnswers, listOfQuestionScenes.size(),
                    Math.round(((double) correctAnswers / listOfQuestionScenes.size()) * 100.00));
            resultsSummary(s);

        } else if (homeScreen) {
            homeScreen("", window);
        }


    }

    private static Alert createAlert(String headerText, String titleText, String imagePath) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(headerText);
        alert.setTitle(titleText);
        try {
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

    private static void resultsSummary(String contentText) {
        Alert finalAlert = new Alert(Alert.AlertType.CONFIRMATION);
        finalAlert.setTitle("Results");
        finalAlert.setHeaderText("Quiz Complete!");
        finalAlert.setContentText(contentText);
        String quit = "Quit";
        String mainMenu = "Main Menu";
        ButtonType quitButton = new ButtonType(quit);
        ButtonType newQuiz = new ButtonType(mainMenu);

        finalAlert.getButtonTypes().setAll(newQuiz, quitButton);

        finalAlert.setOnCloseRequest(e -> finalAlert.close());
        Optional<ButtonType> result = finalAlert.showAndWait();
        if (result.isPresent()) {
            if (result.get().getText().equalsIgnoreCase(mainMenu)) {
                finalAlert.close();
                homeScreen("", window);
            } else {
                Platform.exit();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static ImageView getImageNode(String filePath) throws FileNotFoundException {
        Image image = new Image(new FileInputStream(filePath));
        // Setting new image
        return new ImageView(image);

    }

    public static Map<String,Long> getCategoriesList(){
        String categories = apiRequest("https://opentdb.com/api_category.php");
        JSONObject jObj = getJsonArrayResults(categories);
        if (jObj != null) {
            JSONArray categoryArray = (JSONArray) jObj.get("trivia_categories");
            Map<String, Long> categoryMap = new HashMap<>();
            for (int i = 0; i < categoryArray.size(); i++) {
                jObj = (JSONObject) categoryArray.get(i);
                categoryMap.put((String) jObj.get("name"), (Long) jObj.get("id"));
            }
            return categoryMap;
        }
        return null;
    }

    private static String getToken() {
        String result = apiRequest("https://opentdb.com/api_token.php?command=request");
        JSONObject jsonObj = getJsonArrayResults(result);
        if (jsonObj != null) {
            Long responseCode = (Long) jsonObj.get("response_code");
            if (responseCode != 0) {
                System.out.println("Error getting questions.");
                System.out.println("Response code received: " + responseCode);
            } else {
                return (String) jsonObj.get("token");
            }
        }
        System.out.println("Array empty");
        return "Error";
    }

    public static List<Question> retrieveQuestions(String apiURL) {
        String userToken = getToken();
        String result = apiRequest(apiURL + "&token=" + userToken);
        JSONObject jObj = getJsonArrayResults(result);
        if (jObj != null || (Long) jObj.get("response_code") == 0) {
                JSONArray resultsArray = (JSONArray) jObj.get("results");
                if (resultsArray != null) {
                    JSONObject jsonObject = (JSONObject) resultsArray.get(0);
//                System.out.println(jsonObject.keySet());
                    jsonToQuestion(jsonObject);
                    List<Question> listOfQuestions = new ArrayList<>();
                    resultsArray.forEach(el -> listOfQuestions.add(jsonToQuestion((JSONObject) el)));
                    return listOfQuestions;
//                    System.out.println(listOfQuestions.size());
//                    runQuiz(listOfQuestions);
                }
        } else {
            Long responseCode = (Long) jObj.get("response_code");
            if (responseCode == 4) {
                System.out.println("All questions within this category have been answered");
                System.out.println("Resetting token");
                apiRequest("https://opentdb.com/api_token.php?command=reset&token=" + userToken);
                retrieveQuestions(apiURL);
            } else {
                System.out.println("Error getting questions.");
                System.out.println("Response code received: " + responseCode);
            }
        }
        return null;
    }

    private static String apiRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int code = connection.getResponseCode();
            if (code != 200) {
                throw new RuntimeException("Http Response code: " + code);
            } else {
                InputStream input = connection.getInputStream();
                BufferedReader ioReader = new BufferedReader(new InputStreamReader(input));
                return ioReader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error in apiRequest: " + e.getMessage());
        }
        return null;
    }

    private static Question jsonToQuestion(JSONObject jObj) {
        if (jObj.get("type").equals("multiple")) {
            String question = jObj.get("question").toString();
            String correctAnswer = jObj.get("correct_answer").toString();
            String incorrectAnswersOriginal = jObj.get("incorrect_answers").toString();
            String incorrectAnswers = incorrectAnswersOriginal.replaceAll("[\\[\\]\"]", "");
            String[] incorrectAnswerList = incorrectAnswers.split(",");
            List<String> incorrectList = new ArrayList<>();
            incorrectList.add(correctAnswer);
            Collections.addAll(incorrectList, incorrectAnswerList);
            return new MultipleChoiceQuestion(question, correctAnswer, incorrectList);
        } else if (jObj.get("type").equals("boolean")) {
            String question = jObj.get("question").toString();
            String correctAnswer = jObj.get("correct_answer").toString();
            return new TrueFalse(question, correctAnswer);
        }
        return null;
    }

    private static JSONObject getJsonArrayResults(String readString) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(readString);
            return jsonObject;
        } catch (ParseException jsonE) {
            System.out.println("Json-simple error: " + jsonE.getMessage());
            return null;
        }
    }

    private static String createAPIRequest(int noOfQuestions, int categoryID, String difficulty) {
        return String.format("https://opentdb.com/api.php?amount=%d&category=%d&difficulty=%s", noOfQuestions, categoryID, difficulty);
    }

    public static String createAPIRequest(int noOfQuestions, int categoryID) {
        return String.format("https://opentdb.com/api.php?amount=%d&category=%d", noOfQuestions, categoryID);
    }

    private static String createAPIRequest(int noOfQuestions) {
        return String.format("https://opentdb.com/api.php?amount=%d", noOfQuestions);
    }
}