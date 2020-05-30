package mcq.Questions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mcq.Questions.Question;

import java.util.List;

public class MultipleChoiceQuestion extends Question {

    private final ObservableList<String> options;

    public MultipleChoiceQuestion(String question, String correctAnswer, int time, List<String> options) {
        super(question, correctAnswer, time);
        this.options = FXCollections.observableArrayList(options);
    }

    public MultipleChoiceQuestion(String question, String correctAnswer, List<String> options) {
        super(question, correctAnswer);
        this.options = FXCollections.observableArrayList(options);

    }

    public ObservableList<String> getOptions() {
        return options;
    }
}
