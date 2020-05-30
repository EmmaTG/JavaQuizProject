package mcq.Questions;

import javafx.collections.ObservableList;

public class WriteInQuestion extends Question {

    public WriteInQuestion(String question, String correctAnswer, int time) {
        super(question, correctAnswer, time);
    }

    public WriteInQuestion(String question, String correctAnswer) {
        super(question, correctAnswer);
    }

    @Override
    public ObservableList<String> getOptions() {
        return null;
    }
}
