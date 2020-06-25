package mcq.Questions;

import javafx.scene.image.Image;

import java.util.Arrays;

public class TrueFalse extends MultipleChoiceQuestion {
    public TrueFalse(String question, String correctAnswer, int time) {
        super(question, correctAnswer, time, Arrays.asList("True","False"));
    }

    public TrueFalse(String question, String correctAnswer) {
        super(question, correctAnswer, Arrays.asList("True","False"));
    }

    public TrueFalse(String question, String correctAnswer, int time,  Image questionImage) {
        super(question, correctAnswer, time, Arrays.asList("True","False"),questionImage);
    }

    public TrueFalse(String question, String correctAnswer,  Image questionImage) {
        super(question, correctAnswer, Arrays.asList("True","False"),questionImage);
    }
}
