package testrail;

import util.ConfProperties;
import util.ConfigurationReader;
import util.JSonFormatter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static util.ConfProperties.TEST_CASE_ID_INDEX;
import static util.ConfProperties.TEST_ELAPSE_TIME_INDEX;
import static util.ConfProperties.TEST_FAIL_MESSAGE_INDEX;
import static util.ConfProperties.TEST_RESULT_INDEX;
import static util.ConfProperties.TEST_RUN_ID;

public class TestResult {


    private Map<String , Object> data = new HashMap();

    private String testRunId;
    private String testCaseId;


    public void setPass(boolean pass) {
        if(pass) {
            data.put(TestRailKeys.STATUS_ID.getId(), 1);
        } else {
            data.put(TestRailKeys.STATUS_ID.getId(), 5);
        }
    }

    public void setCustomVersion(int customVersion) {
        data.put(TestRailKeys.CUSTOM_VERSION.getId(), customVersion);
    }

    public void setCustomRevision(int customRevision) {
        data.put(TestRailKeys.CUSTOM_REVESION.getId(), customRevision);
    }

    public void setTestRunId(String testRunId) {
        this.testRunId = testRunId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public void setElapseTime(String elapseTime) {
        data.put(TestRailKeys.ELAPSED.getId(), elapseTime);
    }

    public void setInput(String input) {

        String formattedInput = formatData(input, "Input Data");

        data.compute(TestRailKeys.COMMENT.getId(), (k,v) -> v == null? formattedInput : formattedInput + System.lineSeparator() + v);
    }

    private String formatData(String data, String heading) {

        StringWriter stringWriter = new StringWriter();

        PrintWriter printWriter = new PrintWriter(stringWriter);

        printWriter.println(heading);
        printWriter.println("---");
        printWriter.println(data);

        return stringWriter.toString();
    }

    public void setOutput(String output, String failMessage) {
        String formattedOutput = formatData(output, "Output Data");
        String formattedFailMessage = failMessage == null?  "" : formatData(failMessage, "Fail Message");
        data.compute(TestRailKeys.COMMENT.getId(), (k,v) -> v == null? formattedOutput : v + System.lineSeparator() + formattedOutput + System.lineSeparator() + formattedFailMessage);
    }


    public Map<String, Object> getData() {
        return data;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public String getTestRunId() {
        return testRunId;
    }


    public static TestResult fromResult(List<String> results) {

        ConfigurationReader configurationReader = ConfigurationReader.getInstance();

        String result = results.get(configurationReader.getIntVal(TEST_RESULT_INDEX));

        Objects.requireNonNull(result, "Result not found");

        String failMessage = null;
        TestResult testResult = new TestResult();
        if(result.equalsIgnoreCase("true")) {
            testResult.setPass(true);
        } else {
            testResult.setPass(false);

            failMessage = results.get(configurationReader.getIntVal(TEST_FAIL_MESSAGE_INDEX));
        }


        String testCaseId = results.get(configurationReader.getIntVal(TEST_CASE_ID_INDEX));
        Objects.requireNonNull(testCaseId, "Test case id not found");
        testResult.setTestCaseId(testCaseId);

        String testRunId = System.getProperty(TEST_RUN_ID.getKey());
        Objects.requireNonNull(testRunId, "Test Run id not provided in JVM parameter");
        testResult.setTestRunId(testRunId);

        String customVersion = System.getProperty(ConfProperties.CHECK_VERSION.getKey());
        Objects.requireNonNull(customVersion, "Check version id not provided in JVM parameter");
        testResult.setCustomVersion(Integer.parseInt(customVersion));

        String checkRevision = System.getProperty(ConfProperties.CHECK_REVISION.getKey());
        Objects.requireNonNull(checkRevision, "Test Run id not provided in JVM parameter");
        testResult.setCustomRevision(Integer.parseInt(checkRevision));

        String elapseTime = results.get(configurationReader.getIntVal(TEST_ELAPSE_TIME_INDEX));
        Objects.requireNonNull(elapseTime, "Elapse time not found");
        long seconds = TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(elapseTime));
        if(seconds == 0) {
            seconds = 1;
        }
        testResult.setElapseTime(seconds+"s");


        String fileName = results.get(configurationReader.getIntVal(ConfProperties.TEST_INOUT_FILE_NAME_INDEX));


        Path inputFilePath = Paths.get(configurationReader.getInputFileLocation(), fileName + ".json");
        if(Files.exists(inputFilePath)) {
            try {
                String input = Files.readAllLines(inputFilePath, StandardCharsets.UTF_8).stream().collect(Collectors.joining(System.lineSeparator()));

                testResult.setInput(JSonFormatter.format(input));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("[WARN] Input file not found at location:" + inputFilePath.toString());
        }

        Path responseFilePath = Paths.get(configurationReader.getResponseFileLocation(), fileName + ".json");
        if(Files.exists(responseFilePath)) {
            try {
                String output = Files.readAllLines(responseFilePath, StandardCharsets.UTF_8).stream().collect(Collectors.joining(System.lineSeparator()));
                testResult.setOutput(JSonFormatter.format(output), failMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("[WARN] Response file not found at location:" + responseFilePath.toString());
        }


        return testResult;
    }


}
