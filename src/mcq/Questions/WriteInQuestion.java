package mcq.Questions;

import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class WriteInQuestion extends Question {

    public WriteInQuestion(String question, String correctAnswer, int time) {
        super(question, correctAnswer, time);
    }

    public WriteInQuestion(String question, String correctAnswer) {
        super(question, correctAnswer);
    }

    public WriteInQuestion(String question, String correctAnswer, int time, Image questionImage) {
        super(question, correctAnswer, time,questionImage);
    }

    public WriteInQuestion(String question, String correctAnswer, Image questionImage) {
        super(question, correctAnswer,questionImage);
    }

    @Override
    public ObservableList<String> getOptions() {
        return null;
    }
}
