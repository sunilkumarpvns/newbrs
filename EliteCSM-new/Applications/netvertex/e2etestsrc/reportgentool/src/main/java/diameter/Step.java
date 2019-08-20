package diameter;

import java.util.List;

public class Step {

    private List<String> testCaseSteps;

    public Step(List<String> testCaseSteps) {
        this.testCaseSteps = testCaseSteps;
    }

    public String getAt(int index){
        return testCaseSteps.get(index);
    }

    public List<String> getAll() {
        return testCaseSteps;
    }

}
