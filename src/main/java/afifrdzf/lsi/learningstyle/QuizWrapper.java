package afifrdzf.lsi.learningstyle;

/**
 * Created by Afifrdzf on 11/22/2016.
 */

public class QuizWrapper {
    private int id;
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;

    public QuizWrapper(String question, String answer1, String answer2, String answer3) {
        this.question = question;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setId(int id) {
        this.id = id;
    }

}
