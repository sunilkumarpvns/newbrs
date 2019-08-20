package rest;

import com.gurock.testrail.APIException;
import com.sterlite.pcc.Logger;
import com.sterlite.pcc.parsing.csv.CsvResult;
import testrail.ResultUpdator;
import testrail.TestResult;
import util.CSVReader;
import util.CommonHtmlContents;
import util.ConfigurationReader;
import util.CreateHtmlpage;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;


public class RestResultParser {
    public static void parse(String inputFolderPath, String outputFolderPath){
        Logger logger = new Logger();
        logger.info("Reading Result file Html Report Generation.");
        ConfigurationReader.getInstance().init();
        CsvResult resultTableRowData = CSVReader.processInputFile(inputFolderPath+"../csv/rest_automation_result.csv");
        CsvResult inputTableRowData = CSVReader.processInputFile(inputFolderPath+"../csv/rest_automation.csv");

        FileUtil.copyMappingAndResultFile(inputFolderPath+"../csv/rest_automation_result.csv", outputFolderPath);
        FileUtil.copyMappingAndResultFile(inputFolderPath+"../csv/rest_automation.csv", outputFolderPath);

        IntStream.range(0, resultTableRowData.getCsvData().getCsvLines().size()).forEach(index -> {
            List<String> coloms = resultTableRowData.getCsvData().getCsvLines().get(index).getColoumData();
            if (inputTableRowData.getCsvData().getCsvLines().size() > index) {
                coloms.addAll(inputTableRowData.getCsvData().getCsvLines().get(index).getColoumData());
            } else {
                logger.info("[WARN] Input raw not found for records: " + coloms);
            }
        });

        CreateHtmlpage createHtmlpage = new CreateHtmlpage();
        createHtmlpage.create(resultTableRowData, outputFolderPath+ File.separator+"PCC_Rest_Automation_Result.html", outputFolderPath);
        CommonHtmlContents.createStatusReport(outputFolderPath+ File.separator+"PCC_Rest_Automation_Summary_Result.html", "REST", createHtmlpage.getTotal(), createHtmlpage.getPass(), createHtmlpage.getFail());
        logger.info("Html Report Generated successfully");

        logger.info("Preparing Result for TestRail status update.");
        ResultUpdator resultUpdator = new ResultUpdator();
        resultTableRowData.getCsvData().getCsvLines().parallelStream().map(line -> TestResult.fromResult(line.getColoumData())).forEach(testResult -> {
            try {
                resultUpdator.update(testResult);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (APIException e) {
                e.printStackTrace();
            }
        });
        logger.info("TestRail Result Updated successfully.");

        long failCount = resultTableRowData.getCsvData().getCsvLines().stream().filter(resultColumns ->
           Boolean.toString(false).equalsIgnoreCase(resultColumns.getColoumData().get(ConfigurationReader.getInstance().getTestResultIndex()))
        ).count();

        if(failCount>0){
            System.exit(1);
        }
    }
}
