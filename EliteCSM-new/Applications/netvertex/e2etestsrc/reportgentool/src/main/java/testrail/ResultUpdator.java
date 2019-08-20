package testrail;

import com.gurock.testrail.APIClient;
import com.gurock.testrail.APIException;

import java.io.IOException;
import java.util.Objects;

public class ResultUpdator {


    public void update(TestResult  testResult) throws IOException, APIException {
        APIClient client = new APIClient("http://192.168.2.138/csmqa/");
        client.setUser("nirav.modhiya@elitecore.com");
        client.setPassword("elitecore");


        if(Objects.isNull(testResult.getTestRunId()) ||  testResult.getTestRunId().trim().isEmpty()) {
            System.out.println("test-run-id not provided for " + testResult);
            return;
        }

        if(Objects.isNull(testResult.getTestCaseId()) || testResult.getTestCaseId().trim().isEmpty()) {
            System.out.println("test-case-id not provided");
            return;
        }

        System.out.println("Add_result_for_case/" + testResult.getTestRunId() + "/" + testResult.getTestCaseId());

        //client.sendPost("Add_result_for_case/" + testResult.getTestRunId() + "/" + testResult.getTestCaseId(), testResult.getData());
    }

}
