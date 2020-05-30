package mcq.Questions;

import java.util.Arrays;

public class TrueFalse extends MultipleChoiceQuestion {
    public TrueFalse(String question, String correctAnswer, int time) {
        super(question, correctAnswer, time, Arrays.asList("True","False"));
    }

    public TrueFalse(String question, String correctAnswer) {
        super(question, correctAnswer, Arrays.asList("True","False"));
    }
}
