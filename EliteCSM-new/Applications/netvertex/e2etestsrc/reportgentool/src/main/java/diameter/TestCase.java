package diameter;

import static java.time.Duration.between;
import static java.time.Instant.ofEpochMilli;

import com.sterlite.pcc.Logger;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TestCase {
    private List<Step> steps = new ArrayList<>(4);
    private long durationInSec = -1;
    private long startTime;
    private String title;
    private String testRailTestCaseId;
    private DiameterReportConfigurationReader diameterReportConfigurationReader;
    private Logger logger = new Logger();

    public static TestCase create(List<Step> steps, Map<String, String> titleToTestRailTestCaseId) {
        int testCaseIdColumnIndex = DiameterReportConfigurationReader.getInstance().getTestCaseIdColumnIndex();
        String title = getTitle(steps, testCaseIdColumnIndex);
        String testRailTestCaseId = findTestRailTestCaseId(title,titleToTestRailTestCaseId);

        return new TestCase(steps, title, testRailTestCaseId, DiameterReportConfigurationReader.getInstance());
    }

    public TestCase(List<Step> steps, String title, String testRailTestCaseId, DiameterReportConfigurationReader diameterReportConfigurationReader) {
        this.title = title;
        this.testRailTestCaseId = testRailTestCaseId;
        this.diameterReportConfigurationReader = diameterReportConfigurationReader;
        addSteps(steps);
    }

    public String getTestRailTestCaseId() {
        return testRailTestCaseId;
    }

    public static String findTestRailTestCaseId(String title,Map<String, String> testRailTestCaseId){
        if(testRailTestCaseId == null){
            return null;
        }
        return testRailTestCaseId.get(title);
    }

    private void addSteps(List<Step> steps) {
        for (Step step : steps) {
            this.steps.add(step);
            if(isFail(step)) {
                break;
            }
        }
    }

    private boolean isFail(Step step) {
        String status = step.getAt(diameterReportConfigurationReader.getStatusIndex());

        if(status.equalsIgnoreCase("true")){
            return false;
        }

        return true;
    }

    private static String getTitle(List<Step> steps, int testCaseIdColumnIndex) {
        String title = steps.get(0).getAt(testCaseIdColumnIndex);
        if(title.contains(" ") == false) {
            new Logger().info(String.format("%s does not contains any space", title));
            return title;
        }
        int endIndex = title.lastIndexOf(" ");
        return title.substring(0, endIndex);
    }

    public List<Step> getSteps() {
        return steps;
    }

    public long getDuration() {

        if(durationInSec == -1) {
            if(steps.size() > 1) {
                Step firstStep = steps.get(0);
                long firstStepStartTime = getStartTime(firstStep);
                Step lastStep = steps.get(steps.size() - 1);
                long lastStepStartTime = getStartTime(lastStep);

                durationInSec = between(ofEpochMilli(firstStepStartTime), ofEpochMilli(lastStepStartTime)).getSeconds();
            } else {
                durationInSec = 0;
            }
        }

        return durationInSec;
    }

    public void setDurationInSec(long durationInSec) {
        this.durationInSec = durationInSec;
    }

    private static long getStartTime(Step step) {
        return Long.parseLong(step.getAt(DiameterReportConfigurationReader.getInstance().getTimeStampIndex()));
    }

    public Timestamp getStartTime() {
        if(startTime == 0) {
            startTime = steps.stream().mapToLong(TestCase::getStartTime).min().getAsLong();
        }

        return Timestamp.from( ofEpochMilli( startTime) );
    }

    public int noOfSteps() {
        return steps.size();
    }


    public String getTitle() {
        return title;
    }

    public boolean isFail() {

        return isFail(lastStep());

    }

    public String getFailedTestSetp() {
        return lastStep().getAt(diameterReportConfigurationReader.getFailedStepColoumIndex());
    }

    public String getFailMessage() {
        return lastStep().getAt(diameterReportConfigurationReader.getFaileMessageIndex());

    }

    public Step lastStep() {
        return steps.get(noOfSteps() - 1);
    }
}
