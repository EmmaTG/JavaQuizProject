package mcq;

import mcq.Questions.Question;

import java.util.ArrayList;
import java.util.List;

public class Quiz {

    private final String name;
    private List<Question> questions;

    public Quiz(String name) {
        this.name = name;
        this.questions = new ArrayList<>();
    }

    public <T extends Question> int addQuestion(T question){
        this.questions.add(question);
        return this.questions.size();
    }

    public void updateQuestion(int index, Question updatedQuestion){
        this.questions.set(index,updatedQuestion);
    }
    public void removeQuestion(Question deletedQuestion){
        this.questions.remove(deletedQuestion);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public String getName() {
        return name;
    }
}
