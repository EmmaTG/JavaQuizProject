package mcq;

import mcq.Questions.MultipleChoiceQuestion;
import mcq.Questions.Question;
import mcq.Questions.WriteInQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Quiz {

    private final String name;
    private List<Question> questions;
    private int mcqs = 0;
    private int trueFalseQs = 0;
    private int writeInQs = 0;
    private Scanner sc = new Scanner(System.in);

    public Quiz(String name) {
        this.name = name;
        this.questions = new ArrayList<>();
    }

    public boolean setQuestion() {
        int questionType = questionOptions();
        sc.nextLine();
        if (questionType != 4) {
            int questionNumber = (length() + 1);
            Question newQuestion;
            System.out.println("Question " + questionNumber + ": ");
            String question = sc.nextLine();
            if (questionType == 1) {
                List<String> choices = new ArrayList<>();
                System.out.println("Correct answer: ");
                String correctAnswer = sc.nextLine();
                System.out.println("Please enter 3 false answers");
                choices.add(sc.nextLine());
                choices.add(sc.nextLine());
                choices.add(sc.nextLine());
                choices.add(correctAnswer);
                newQuestion = new MultipleChoiceQuestion(question, choices.get(3), 30, choices);
                mcqs += 1;
            } else if (questionType == 2) {
                List<String> choices = new ArrayList<>();
                choices.add("True");
                choices.add("False");
                System.out.println("Is this question true of false");
                newQuestion = new MultipleChoiceQuestion(question, sc.nextLine(), 30, choices);
                trueFalseQs += 1;
            } else if (questionType == 3) {
                System.out.println("Correct answer using one word: ");
                String correctAnswer = sc.nextLine();
                newQuestion = new WriteInQuestion(question, correctAnswer, 30);
                writeInQs += 1;
            } else {
                return true;
            }
            System.out.println("Total number of questions: " + questionNumber);
            return this.questions.add(newQuestion);
        } else {
            quizSummary();
//            sc.close();
            return false;
        }

    }

    private int questionOptions() {
        System.out.println("Which type of question would you like to add");
        System.out.println("\t1 - Multiple choice question");
        System.out.println("\t2 - True/False question");
        System.out.println("\t3 - Write in question");
        System.out.println("\t4 - Finished");
        int result = sc.nextInt();
        return result;

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

    public int length() {
        return questions.size();
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public String getName() {
        return name;
    }

    private void quizSummary() {
        System.out.println("\n");
        System.out.println("Quiz created!");
        System.out.println("\n");
        System.out.println("\tTotal number of questions: " + (length()));
        System.out.println("\tMultiple Choice questions: \t " + mcqs);
        System.out.println("\tTrue/False questions: \t " + trueFalseQs);
        System.out.println("\tWrite in questions: \t " + writeInQs);

    }
}
