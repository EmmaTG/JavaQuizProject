package mcq.Data;

import javafx.scene.image.Image;
import mcq.Questions.MultipleChoiceQuestion;
import mcq.Questions.Question;
import mcq.Questions.TrueFalse;
import mcq.Questions.WriteInQuestion;
import mcq.Quiz;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDataSource {
    private static QuestionDataSource ourInstance = new QuestionDataSource();
    private Connection conn;
    private List<Question> quizQuestions;
    private List<String> quizzesInDatabase;

    public static final String DATABASE ="quizQuestions.db";
    private static final String CURRENT_DIRECTORY = System.getProperty("user.dir");
    public static final String CONNECTION_STRING = "jdbc:sqlite:" + CURRENT_DIRECTORY +"/" + DATABASE;

    public static final String TABLE_CATEGORIES = "Categories";
    public static final String COLUMN_CATEGORIES_ID = "_id";
    public static final String COLUMN_CATEGORIES_NAME = "name";

    public static final String TABLE_QUIZ = "quizzes";
    public static final String COLUMN_QUIZ_ID = "_id";
    public static final String COLUMN_QUIZ_NAME = "name";
    public static final String COLUMN_QUIZ_CATEGORIES = "category";

    public static final String TABLE_QUESTIONS = "questions";
    public static final String COLUMN_QUESTIONS_ID = "_id";
    public static final String COLUMN_QUESTIONS_TITLE = "title";
    public static final String COLUMN_QUESTIONS_TYPE = "type";
    public static final String COLUMN_QUESTIONS_ANSWER = "answer";
    public static final String COLUMN_QUESTIONS_OPTION1 = "option1";
    public static final String COLUMN_QUESTIONS_OPTION2 = "option2";
    public static final String COLUMN_QUESTIONS_OPTION3 = "option3";
    public static final String COLUMN_QUESTIONS_QUIZ = "quiz";
    public static final String COLUMN_QUESTIONS_IMAGES = "image";

    public static final int MCQ = 1;
    public static final int TF = 2;
    public static final int WI = 3;

    private static String queryQuizSQL = "SELECT (" + COLUMN_QUIZ_ID +
            ") FROM " + TABLE_QUIZ +
            " WHERE " + COLUMN_QUIZ_NAME +
            "= ?";

    private static String addQuiz = "INSERT INTO " + TABLE_QUIZ +
            " (" + COLUMN_QUIZ_NAME + "," + COLUMN_QUIZ_CATEGORIES + ") " +
            "VALUES(?,2)";

    private static String sqlInsert1 = "INSERT INTO " + TABLE_QUESTIONS +
            " (" + COLUMN_QUESTIONS_TITLE + "," + COLUMN_QUESTIONS_TYPE + "," + COLUMN_QUESTIONS_ANSWER +
            "," + COLUMN_QUESTIONS_QUIZ + "," + COLUMN_QUESTIONS_IMAGES + ") " +
            " VALUES(?,?,?,?,?)";

    private static String sqlInsert2 = "INSERT INTO " + TABLE_QUESTIONS +
            " (" + COLUMN_QUESTIONS_TITLE + "," + COLUMN_QUESTIONS_TYPE + "," + COLUMN_QUESTIONS_ANSWER +
            "," + COLUMN_QUESTIONS_OPTION1 + "," + COLUMN_QUESTIONS_OPTION2 + "," +
            COLUMN_QUESTIONS_OPTION3 + "," + COLUMN_QUESTIONS_QUIZ + "," + COLUMN_QUESTIONS_IMAGES +  ") " +
            " VALUES(?,?,?,?,?,?,?,?)";

    private static String quizNameQuery = "SELECT COUNT(" + COLUMN_QUIZ_ID + ") FROM " +
            TABLE_QUIZ + " WHERE " + COLUMN_QUIZ_NAME +
            "=?";

    private static PreparedStatement queryQuizPrep;
    private static PreparedStatement saveQuizPrep;
    private static PreparedStatement saveQuestionInsertPrep1;
    private static PreparedStatement saveQuestionInsertPrep2;
    private static PreparedStatement quizNameQueryPrep;


    public static QuestionDataSource getInstance() {
        return ourInstance;
    }

    private QuestionDataSource() {
    }

    public boolean open(){
        try{
            conn = DriverManager.getConnection(CONNECTION_STRING);
            queryQuizPrep = conn.prepareStatement(queryQuizSQL);
            saveQuizPrep = conn.prepareStatement(addQuiz);
            saveQuestionInsertPrep1 = conn.prepareStatement(sqlInsert1);
            saveQuestionInsertPrep2 = conn.prepareStatement(sqlInsert2);
            quizNameQueryPrep = conn.prepareStatement(quizNameQuery);
            return true;
        } catch (SQLException e){
            System.out.println("Error opening SQL connection: " + e.getMessage());
            return false;
        }
    }

    public boolean close(){
        try{
            if (conn != null){
                conn.close();
                return true;
            }

            if (queryQuizPrep != null){
                queryQuizPrep.close();
            }

            if (saveQuizPrep != null){
                saveQuizPrep.close();
            }

            if (saveQuestionInsertPrep1 != null){
                saveQuestionInsertPrep1.close();
            }

            if (saveQuestionInsertPrep2 != null){
                saveQuestionInsertPrep2.close();
            }
            if (quizNameQueryPrep != null){
                quizNameQueryPrep.close();
            }
            return false;
        } catch (SQLException e){
            System.out.println("Error closing the connection: " + e.getMessage());
            return false;
        }
    }

    public void deleteQuiz(String quizName) {
        int QuizID = queryQuizID(quizName);
        if (QuizID == -1) {
            System.out.println("Error getting quiz _id");
            return;
        }
        String sql = "DELETE FROM " + TABLE_QUIZ +
                " WHERE " + COLUMN_QUIZ_NAME + "=\"" + quizName + "\"";
        String sql2 = "DELETE FROM " + TABLE_QUESTIONS +
                " WHERE " + COLUMN_QUESTIONS_QUIZ + "=" + QuizID;
        try (Statement statement = conn.createStatement()) {
            statement.execute(sql);
            statement.execute(sql2);
        } catch (SQLException e) {
            System.out.println("Error deleting questions: " + e.getMessage());
            return;
        }
    }


    public void queryQuizQuestion(String quizName){
        quizQuestions = new ArrayList<>();
        int quizID = queryQuizID(quizName);
        if (quizID==-1){
            System.out.println("Error getting quiz _id");
            return;
        }
        String sql = "SELECT * FROM " + TABLE_QUESTIONS +
                " WHERE " + COLUMN_QUESTIONS_QUIZ +
                "=" + quizID;
        try(Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql)){
            while(results.next()){
            int questiontype = results.getInt(COLUMN_QUESTIONS_TYPE);
            if (questiontype ==MCQ){
                //MCQ question
                String title = results.getString(COLUMN_QUESTIONS_TITLE);
                String answer = results.getString(COLUMN_QUESTIONS_ANSWER);
                String option1 = results.getString(COLUMN_QUESTIONS_OPTION1);
                String option2 = results.getString(COLUMN_QUESTIONS_OPTION2);
                String option3 = results.getString(COLUMN_QUESTIONS_OPTION3);
                String imagePath = results.getString(COLUMN_QUESTIONS_IMAGES);
                List<String> listOfAnswers = new ArrayList<>();
                listOfAnswers.add(answer);
                listOfAnswers.add(option1);
                listOfAnswers.add(option2);
                listOfAnswers.add(option3);
                MultipleChoiceQuestion mcq;
                if (imagePath == null) {
                    mcq = new MultipleChoiceQuestion(title, answer, listOfAnswers);
                } else {
                    try {
                        Image image = new Image(new FileInputStream(imagePath));
                        mcq = new MultipleChoiceQuestion(title, answer, listOfAnswers,imagePath,image);
                    } catch (IOException e){
                        System.out.println("File Not Found: " + e.getMessage());
                        mcq = new MultipleChoiceQuestion(title, answer, listOfAnswers);
                    }
                }
                quizQuestions.add(mcq);
            }
            if (questiontype ==TF){
                // True/False Question
                String title = results.getString(COLUMN_QUESTIONS_TITLE);
                String answer = results.getString(COLUMN_QUESTIONS_ANSWER);
                TrueFalse tfq = new TrueFalse(title,answer);
                quizQuestions.add(tfq);

            }
            if (questiontype ==WI){
                // Write in question
                String title = results.getString(COLUMN_QUESTIONS_TITLE);
                String answer = results.getString(COLUMN_QUESTIONS_ANSWER);
                WriteInQuestion wiq = new WriteInQuestion(title,answer);
                quizQuestions.add(wiq);
            }
            }
        } catch (SQLException e){
            System.out.println("Error getting questions: " + e.getMessage());
            return;
        }
    }

    public List<Question> getQuizQuestions() {
        return quizQuestions;
    }

    private int queryQuizID(String quizName){
        try {
            queryQuizPrep.setString(1,quizName);
            ResultSet results = queryQuizPrep.executeQuery();
            return results.getInt(1);
        } catch (SQLException e){
            System.out.println("Error getting quiz _id: " + e.getMessage());
            return -1;
        }
    }

    public void selectedQuizzes(String category){
        int categoryID = getCategoryID(category);
        if (categoryID==-1) {
            System.out.println("Fatal Error!");
            return;
        }
        quizzesInDatabase = new ArrayList<>();
        String sql = "SELECT " + TABLE_QUIZ + "." + COLUMN_QUIZ_NAME +
                " FROM " + TABLE_QUIZ + " WHERE " + COLUMN_QUIZ_CATEGORIES +
                "=" + categoryID;
        try(Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql)){
            while (results.next()) {
                quizzesInDatabase.add(results.getString(1));
            }
            return;
        } catch (SQLException e){
            System.out.println("Error getting quizzes in selected category: " + e.getMessage());
            return;
        }
    }

    private int getCategoryID(String categoryName){
        String sql = "SELECT " + TABLE_CATEGORIES + "." + COLUMN_CATEGORIES_ID +
                " FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_CATEGORIES_NAME +
                "=\"" + categoryName + "\"";
        try(Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery(sql)){
            return result.getInt(1);
        } catch (SQLException e){
            System.out.println("Cannot find Category ID for " + categoryName + " : " + e.getMessage());
            return -1;
        }
    }

    public List<String> getQuizzesInDatabase() {
        return quizzesInDatabase;
    }

    public void saveNewQuiz(Quiz newQuiz){
        int quizID =-1;
        try {
            saveQuizPrep.setString(1,newQuiz.getName());
            saveQuizPrep.execute();
            quizID = queryQuizID(newQuiz.getName());
        } catch (SQLException e){
            System.out.println("SQL Error creating quiz: " + newQuiz.getName() );
            System.out.println(e.getMessage());
        }

        for (Question question : newQuiz.getQuestions()){
            String title = question.getQuestion();
            String answer = question.getCorrectAnswer();
            if (question instanceof TrueFalse || question instanceof WriteInQuestion){
                int type;
                if (question instanceof WriteInQuestion){
                    type = WI;
                } else{
                    type = TF;
                }
                try {
                    saveQuestionInsertPrep1.setString(1, title);
                    saveQuestionInsertPrep1.setInt(2, type);
                    saveQuestionInsertPrep1.setString(3, answer);
                    saveQuestionInsertPrep1.setInt(4, quizID);
                    if (!question.getQuestionImagePath().isEmpty()){
                        saveQuestionInsertPrep1.setString(5,question.getQuestionImagePath());
                    } else {
                        saveQuestionInsertPrep1.setString(5, null);
                    }
                    saveQuestionInsertPrep1.execute();
                } catch (SQLException e){
                    System.out.println("SQL Error adding question titled: " + question.getQuestion());
                    System.out.println(e.getMessage());
                }
            }
            else if (question instanceof MultipleChoiceQuestion){
                List<String> options = question.getOptions();
                try {
                    saveQuestionInsertPrep2.setString(1,title);
                    saveQuestionInsertPrep2.setInt(2,MCQ);
                    saveQuestionInsertPrep2.setString(3,answer);
                    saveQuestionInsertPrep2.setString(4,options.get(1));
                    saveQuestionInsertPrep2.setString(5,options.get(2));
                    saveQuestionInsertPrep2.setString(6,options.get(3));
                    saveQuestionInsertPrep2.setInt(7,quizID);
                    System.out.println(question.getQuestionImagePath());
                    if (!question.getQuestionImagePath().equalsIgnoreCase("")){
                        saveQuestionInsertPrep2.setString(8,question.getQuestionImagePath());
                    } else {
                        saveQuestionInsertPrep2.setString(8, null);
                    }
                    saveQuestionInsertPrep2.execute();
                } catch (SQLException e){
                    System.out.println("SQL Error adding question titled: " + question.getQuestion());
                    System.out.println(e.getMessage());
                }

            } else {
                System.out.println("Error unknown type of question");
                return;
            }

        }
    }

    public List<String> getCategories(){
        String getCategories = "SELECT (" + TABLE_CATEGORIES + "." + COLUMN_CATEGORIES_NAME +
                ") FROM " + TABLE_CATEGORIES;
        try (Statement statement = conn.createStatement();
        ResultSet results = statement.executeQuery(getCategories)){
            List<String> categories = new ArrayList<>();
            while (results.next()){
                categories.add(results.getString(1));
            }
            return categories;

        } catch (SQLException e){
            System.out.println("Error getting categories: " + e.getMessage());
            return null;
        }
    }

    public boolean quizNameExists(String quizName) {
        try {
            quizNameQueryPrep.setString(1,quizName);
            ResultSet results = quizNameQueryPrep.executeQuery();
                if (results.getInt(1) > 0){
                    results.close();
                    return true;
                }
                else {
                    results.close();
                    return false;
                }
        } catch (SQLException e) {
            System.out.println("Error counting quiz Id's: " + e.getMessage());
            return false;
        }
    }
}
