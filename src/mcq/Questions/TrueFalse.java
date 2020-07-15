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

    public TrueFalse(String question, String correctAnswer, int time, String imagePath,  Image questionImage) {
        super(question, correctAnswer, time, Arrays.asList("True","False"),imagePath, questionImage);
    }

    public TrueFalse(String question, String correctAnswer, String imagePath,  Image questionImage) {
        super(question, correctAnswer, Arrays.asList("True","False"),imagePath, questionImage);
    }
}
