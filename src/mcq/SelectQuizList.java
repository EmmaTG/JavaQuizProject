package mcq;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import mcq.Data.QuestionDataSource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SelectQuizList {

    private static ListView<String> listView;
    private static Button startButton = new Button("Start Quiz");
    private static Button editButton = new Button("Edit Quiz");
    private static Button deleteButton = new Button("Delete Quiz");
    private static Button backButton = new Button("Back");
    private static Button quizButton = new Button("Open");
    private static Button homeButton = new Button("Main Menu");
    private static VBox vbox;
    private static String selectedCategory;
    private static String selectedQuiz;

    private static void setButtonsStyle(){
        startButton.setId("AllButton");
        editButton.setId("AllButton");
        deleteButton.setId("AllButton");
        backButton.setId("AllButton");
        quizButton.setId("AllButton");
        homeButton.setId("AllButton");
    }

    public static Scene createScene(){
        BorderPane borderPane = new BorderPane();
        setButtonsStyle();

        listView = new ListView<>();
        listView.setOrientation(Orientation.VERTICAL);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setList(getOpeningList());

        startButton.setMaxWidth(Double.MAX_VALUE);
        editButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setMaxWidth(Double.MAX_VALUE);
        homeButton.setMaxWidth(Double.MAX_VALUE);
        quizButton.setMaxWidth(Double.MAX_VALUE);

        setCategoriesButton();

        vbox = new VBox(quizButton, homeButton);
        quizButton.setOnAction((e) ->{
            setQuizButtonAction();
        });

        deleteButton.setOnAction((e) -> {
            QuestionDataSource.getInstance().deleteQuiz(getSelectedItem());
            setList(getQuizzes(selectedCategory));
        });
        borderPane.setCenter(listView);
        borderPane.setRight(vbox);
        Scene scene = new Scene(borderPane);
        String style= SelectQuizList.class.getResource("SelectQuizListCSS.css").toExternalForm();
        scene.getStylesheets().add(style);
        return scene;
    }

    public static Button getEditButton() {
        return editButton;
    }

    public static void setQuizButtonAction(){
        selectedCategory = getSelectedItem();
//        System.out.println(selectedCategory);
        vbox.getChildren().clear();
        vbox.getChildren().addAll(startButton,editButton,deleteButton, backButton, homeButton);
        if (selectedCategory.equalsIgnoreCase("User Created")){
            editButton.setDisable(false);
            deleteButton.setDisable(false);
        } else{
            editButton.setDisable(true);
            deleteButton.setDisable(true);
        }
        setList(getQuizzes(selectedCategory));
    }

    public static ListView<String> getListView() {
        return listView;
    }

    public static String getSelectedItem(){
        return listView.getSelectionModel().getSelectedItem();
    }

    public static Button getStartButton() {
        return startButton;
    }

    public static Button getHomeButton() {
        return homeButton;
    }

    private void setEditButton(){
    }

    private static void setList(ObservableList<String> list){
        listView.setItems(list);
        listView.getSelectionModel().selectFirst();
    }
    private static void setCategoriesButton() {
        backButton.setOnAction((e) -> {
            vbox.getChildren().clear();
            vbox.getChildren().setAll(quizButton, homeButton);
            setList(getOpeningList());
        });
    }

    public static String getSelectedCategory() {
        return selectedCategory;
    }

    private void setQuizButton(){

    }

    private static ObservableList<String> getOpeningList(){
        List<String> openingList = QuestionDataSource.getInstance().getCategories();
        openingList.add("Other");
        return FXCollections.observableArrayList(openingList);
    }

    private static ObservableList<String>  getCategories(){
        List<String> categoriesList = new ArrayList<>();
            Map<String, Long> categoryMap = Main.getCategoriesList();
            if (categoryMap != null) {
                categoriesList.addAll(categoryMap.keySet());
                categoriesList.sort(Comparator.naturalOrder());
            }
        return FXCollections.observableArrayList(categoriesList);

    }

    public static ObservableList<String> getQuizzes(String category) {
        if (category.equalsIgnoreCase("Other")){
            return getCategories();
        } else {
            QuestionDataSource.getInstance().selectedQuizzes(category);
            List<String> quizList = QuestionDataSource.getInstance().getQuizzesInDatabase();
            return FXCollections.observableArrayList(quizList);
        }
    }


}
