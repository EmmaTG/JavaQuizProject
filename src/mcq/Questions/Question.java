package mcq.Questions;

import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public abstract class Question {

    private final String question;
    private final String correctAnswer;
    private final int time;
    private final Image questionImage;

    public Question(String question, String correctAnswer, int time, Image questionImage) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.time = time;
        this.questionImage = questionImage;
    }

    public Question(String question, String correctAnswer, int time) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.time = time;
        this.questionImage = null;
    }

    public Question(String question, String correctAnswer, Image questionImage) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.time = 30;
        this.questionImage = questionImage;
    }

    public Question(String question, String correctAnswer) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.time = 30;
        this.questionImage = null;
    }

    public boolean isCorrect(String answer){
        if (this instanceof WriteInQuestion){
            String[] splitAnswer = answer.trim().split(" ");
            for (String word : splitAnswer){
                if (word.equalsIgnoreCase(correctAnswer)){
                    return true;
                }
            }
            return false;
        }
        return answer.equalsIgnoreCase(correctAnswer);
    }

    public String result(boolean result){
        if (result){
            return "Correct!";
        } else {
            return "Incorrect. The correct answer is : " + correctAnswer;
        }
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public int getTime() {
        return time;
    }

    public Image getQuestionImage() {
        return questionImage;
    }

    public abstract ObservableList<String> getOptions();

    @Override
    public String toString() {
        return this.question;
    }
}
