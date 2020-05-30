package mcq.Data;

import mcq.Questions.MultipleChoiceQuestion;
import mcq.Questions.Question;
import mcq.Questions.TrueFalse;
import mcq.Questions.WriteInQuestion;
import mcq.Quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDataSource {
    private static QuestionDataSource ourInstance = new QuestionDataSource();
    private Connection conn;
    private List<Question> quizQuestions;

    public static final String DATABASE ="quizQuestions.db";
    private static final String CURRENT_DIRECTORY = System.getProperty("user.dir");
    public static final String CONNECTION_STRING = "jdbc:sqlite:" + CURRENT_DIRECTORY +"/" + DATABASE;

    public static final String TABLE_QUIZ = "quizzes";
    public static final String COLUMN_QUIZ_ID = "_id";
    public static final String COLUMN_QUIZ_NAME = "name";

    public static final String TABLE_QUESTIONS = "questions";
    public static final String COLUMN_QUESTIONS_ID = "_id";
    public static final String COLUMN_QUESTIONS_TITLE = "title";
    public static final String COLUMN_QUESTIONS_TYPE = "type";
    public static final String COLUMN_QUESTIONS_ANSWER = "answer";
    public static final String COLUMN_QUESTIONS_OPTION1 = "option1";
    public static final String COLUMN_QUESTIONS_OPTION2 = "option2";
    public static final String COLUMN_QUESTIONS_OPTION3 = "option3";
    public static final String COLUMN_QUESTIONS_QUIZ = "quiz";

    public static final int MCQ = 1;
    public static final int TF = 2;
    public static final int WI = 3;


    public static QuestionDataSource getInstance() {
        return ourInstance;
    }

    private QuestionDataSource() {
    }

    public boolean open(){
        try{
            System.out.println(CURRENT_DIRECTORY);
            System.out.println(CONNECTION_STRING);
            conn = DriverManager.getConnection(CONNECTION_STRING);
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
            return false;
        } catch (SQLException e){
            System.out.println("Error closing the connection: " + e.getMessage());
            return false;
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
                List<String> listOfAnswers = new ArrayList<>();
                listOfAnswers.add(answer);
                listOfAnswers.add(option1);
                listOfAnswers.add(option2);
                listOfAnswers.add(option3);
                MultipleChoiceQuestion mcq = new MultipleChoiceQuestion(title,answer,listOfAnswers);
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
        String sql = "SELECT (" + COLUMN_QUIZ_ID +
                ") FROM " + TABLE_QUIZ +
                " WHERE " + COLUMN_QUIZ_NAME +
                "=\"" + quizName + "\"";
        try(Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql)){
            return results.getInt(1);
        } catch (SQLException e){
            System.out.println("Error getting quiz _id: " + e.getMessage());
            return -1;
        }

    }

    public List<String> getQuizzes(){
        List<String> quizzesInDatabase = new ArrayList<>();
        String sql = "SELECT " + TABLE_QUIZ + "." + COLUMN_QUIZ_NAME +
                " FROM " + TABLE_QUIZ;
        try(Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql)){
            while (results.next()) {
                quizzesInDatabase.add(results.getString(1));
            }
            return quizzesInDatabase;
        } catch (SQLException e){
            System.out.println("Error getting quizzes in database: " + e.getMessage());
            return null;
        }
    }

    public void saveNewQuiz(Quiz newQuiz){
        String addQuiz = "INSERT INTO " + TABLE_QUIZ +
                " (" + COLUMN_QUIZ_NAME + ") " +
                "VALUES(\"" + newQuiz.getName() + "\")";
        int quizID =-1;
        try (Statement statement = conn.createStatement()){
            statement.execute(addQuiz);
            quizID = queryQuizID(newQuiz.getName());
        } catch (SQLException e){
            System.out.println("SQL Error creating quiz: " + newQuiz.getName() );
            System.out.println(e.getMessage());
        }


        for (Question question : newQuiz.getQuestions()){
            String title = question.getQuestion();
            String answer = question.getCorrectAnswer();
            String sqlInsert;
            if (question instanceof TrueFalse || question instanceof WriteInQuestion){
                int type;
                if (question instanceof WriteInQuestion){
                    type = WI;
                } else{
                    type = TF;
                }
                sqlInsert = "INSERT INTO " + TABLE_QUESTIONS +
                        " (" + COLUMN_QUESTIONS_TITLE + "," + COLUMN_QUESTIONS_TYPE + "," + COLUMN_QUESTIONS_ANSWER +
                        "," + COLUMN_QUESTIONS_QUIZ + ") " +
                        " VALUES(\"" + title + "\"," + type + ",\"" +
                        answer + "\"," + quizID + ")";
            }
            else if (question instanceof MultipleChoiceQuestion){
                List<String> options = question.getOptions();
                sqlInsert = "INSERT INTO " + TABLE_QUESTIONS +
                        " (" + COLUMN_QUESTIONS_TITLE + "," + COLUMN_QUESTIONS_TYPE + "," + COLUMN_QUESTIONS_ANSWER +
                        "," + COLUMN_QUESTIONS_OPTION1 + "," + COLUMN_QUESTIONS_OPTION2 + "," +
                        COLUMN_QUESTIONS_OPTION3 + "," + COLUMN_QUESTIONS_QUIZ + ") " +
                        " VALUES(\"" + title + "\"," + MCQ + ",\"" + answer + "\",\"" +
                        options.get(1) + "\",\"" + options.get(1) + "\",\"" +
                        options.get(3) + "\"," + quizID + ")";
            } else {
                System.out.println("Error unknown type of question");
                return;
            }

            try(Statement statement = conn.createStatement()){
                    statement.execute(sqlInsert);
            } catch (SQLException e){
                System.out.println("SQL Error adding question titled: " + question.getQuestion());
                System.out.println(e.getMessage());
            }
        }
    }
}
