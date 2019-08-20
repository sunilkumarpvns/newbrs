package diameter;

import com.sterlite.pcc.Logger;
import com.sterlite.pcc.parsing.csv.CsvResult;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;
import util.CSVReader;
import util.CommonHtmlContents;
import util.ValidationUtility;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

public class DiameterResultParser {

    private Logger logger = new Logger();

    public void parse(String outputFolder, String fileName,String type, String reportFileName, String moduleName){
        parse(outputFolder, fileName,type,null,reportFileName, moduleName);
    }


    public void parse(String outputFolder, String fileName,String type,String testRailMappingFileName, String reportFileName, String moduleName) {
        logger.info("Reading Result file Html Report Generation.");
        DiameterReportConfigurationReader.getInstance().init();
        CsvResult csvResult = CSVReader.processInputFile(fileName);
        if(Objects.isNull(csvResult)) {
            logger.info("No resutl to create report");
            return;
        }
        Map<String, String> testCaseTitleToTestRailIdMap = getTestCaseTitleToTestRailIdMap(testRailMappingFileName);

        int testCaseIdIndex = DiameterReportConfigurationReader.getInstance().getTestCaseIdColumnIndex();

        Map<String, List<Step>> testCaseIdToStep = csvResult.getCsvData().getCsvLines().stream()
            .map(line -> new Step(line.getColoumData()))
            .collect(groupingBy(step -> step.getAt(testCaseIdIndex), LinkedHashMap::new, Collectors.toList()));;

        Map<String, TestCase> testCaseIdToTestCase = new LinkedHashMap<>();
        List<TestCase> testCases = new ArrayList<>();
        for (Entry<String, List<Step>> testCaseIdToTestCaseEntry : testCaseIdToStep.entrySet()) {
            String testCaseId = testCaseIdToTestCaseEntry.getKey();
            TestCase testCase = TestCase.create(testCaseIdToTestCaseEntry.getValue(), testCaseTitleToTestRailIdMap);
            testCaseIdToTestCase.merge(testCaseId,
                testCase,
                (oldTestCase, newTestCase) -> {
                    logger.warn(
                        String.format("Duplicate key new:%s and old:%s", newTestCase, oldTestCase));
                    return newTestCase;
                });


            if(testCases.size() > 0) {
                TestCase lastTestCase = testCases.get(testCases.size() - 1);
                Timestamp lastTestCaseStartTime = lastTestCase.getStartTime();
                Timestamp currentTestCaseStartTime = testCase.getStartTime();

                long duration = Duration.between(lastTestCaseStartTime.toInstant(),
                    currentTestCaseStartTime.toInstant()).getSeconds();

                lastTestCase.setDurationInSec(duration);
            }

            testCases.add(testCase);
        }



        HTMLReportGenerator createHtmlpage = new HTMLReportGenerator();
        String statusReportFileName = "";

        statusReportFileName = reportFileName + "_Summary.html";
        reportFileName = reportFileName + ".html";

        if (fileName.contains("global_configuration_execution.csv")) {
            statusReportFileName = reportFileName + "_Summary_"+type+".html";
            reportFileName = reportFileName+"_"+type+".html";
        }

        createHtmlpage.create(testCaseIdToTestCase, csvResult.getHeader(), outputFolder, reportFileName);
        CommonHtmlContents.createStatusReport(outputFolder + File.separator+ statusReportFileName,
                moduleName,
                createHtmlpage.getTotal(),
                createHtmlpage.getPass(),
                createHtmlpage.getFail());

        long noOfFailedTestCase = testCaseIdToTestCase.entrySet()
                .stream()
                .filter(stringTestCaseEntry -> stringTestCaseEntry.getKey().endsWith("_SF") == false)
                .filter(testCaseEntry -> testCaseEntry.getValue().isFail())
                .count();

        logger.info(noOfFailedTestCase + " test cases failed");

        logger.info("Html Report Generated successfully");

        if(noOfFailedTestCase>0){
            System.exit(1);
        }
    }
    private Map<String, String> getTestCaseTitleToTestRailIdMap(String testRailMappingFileName) {
        if(ValidationUtility.isNullOrBlank(testRailMappingFileName)){
            return null;
        }
        CsvResult csvResult = CSVReader.processInputFile(testRailMappingFileName, false);
        if(Objects.isNull(csvResult)) {
            return null;
        }
        return csvResult.getCsvData().getCsvLines().stream().collect(
            toMap(csvLine -> csvLine.getColoumData().get(0), csvLine -> csvLine.getColoumData().get(1), (oldKey, newKey) -> {
                logger.warn(
                        String.format("Duplicate key new:%s and old:%s", newKey, oldKey));
                return newKey;
            }));
    }
}
