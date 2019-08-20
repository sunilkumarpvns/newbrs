package data;

/**
 * Created by aditya on 16-09-17.
 */
public class Question {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String complexity;
    private String active;
    private String question;
    private String opt1;
    private String ans1;
    private String opt2;
    private String ans2;
    private String opt3;
    private String ans3;
    private String opt4;
    private String ans4;

    public String getOpt5() {
        return opt5;
    }

    public void setOpt5(String opt5) {
        this.opt5 = opt5;
    }

    public String getAns5() {
        return ans5;
    }

    public void setAns5(String ans5) {
        this.ans5 = ans5;
    }

    private String opt5;
    private String ans5;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOpt1() {
        return opt1;
    }

    public void setOpt1(String opt1) {
        this.opt1 = opt1;
    }

    public String getOpt2() {
        return opt2;
    }

    public void setOpt2(String opt2) {
        this.opt2 = opt2;
    }

    public String getOpt3() {
        return opt3;
    }

    public void setOpt3(String opt3) {
        this.opt3 = opt3;
    }

    public String getOpt4() {
        return opt4;
    }

    public void setOpt4(String opt4) {
        this.opt4 = opt4;
    }

    public String getAns1() {
        return ans1;
    }

    public void setAns1(String ans1) {
        this.ans1 = ans1;
    }

    public String getAns2() {
        return ans2;
    }

    public void setAns2(String ans2) {
        this.ans2 = ans2;
    }

    public String getAns3() {
        return ans3;
    }

    public void setAns3(String ans3) {
        this.ans3 = ans3;
    }

    public String getAns4() {
        return ans4;
    }

    public void setAns4(String ans4) {
        this.ans4 = ans4;
    }

    public String getComplexity() {
        return complexity;
    }

    public void setComplexity(String complexity) {
        this.complexity = complexity;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    @Override
    public String toString() {
        if(active.equals("0")){
            return "";
        }
        StringBuilder questionBuilder = new StringBuilder(id+"-").append(question).append("<font color=\"red\">(Complexity-"+complexity+")</font>");
        questionBuilder.append("<div style='padding-left:1em;'>(i). ");
        if(ans1.equals("0")){
            questionBuilder.append(opt1).append("<br/>");
        }else {
            questionBuilder.append("<font color=\"red\"><b><i>"+opt1+"</i></b></font><br>");
        }
        questionBuilder.append("(ii). ");
        if(ans2.equals("0")){
            questionBuilder.append(opt2).append("<br/>");
        }else {
            questionBuilder.append("<font color=\"red\"><b><i>"+opt2+"</i></b></font><br>");
        }
        questionBuilder.append("(iii). ");
        if(ans3.equals("0")){
            questionBuilder.append(opt3).append("<br/>");
        }else {
            questionBuilder.append("<font color=\"red\"><b><i>"+opt3+"</i></b></font><br>");
        }
        questionBuilder.append("(iv). ");
        if(ans4.equals("0")){
            questionBuilder.append(opt4).append("<br/>");;
        }else {
            questionBuilder.append("<font color=\"red\"><b><i>"+opt4+"</i></b></font><br>");
        }
        if(opt5 == null || opt5.trim().length() == 0){
            return questionBuilder.append("</div><br>").toString();
        }
        questionBuilder.append("(v). ");
        if(ans5.equals("0")){
            questionBuilder.append(opt5);
        }else {
            questionBuilder.append("<font color=\"red\"><b><i>"+opt5+"</i></b></font><br>");
        }
        return questionBuilder.append("</div><br>").toString();
    }
}
