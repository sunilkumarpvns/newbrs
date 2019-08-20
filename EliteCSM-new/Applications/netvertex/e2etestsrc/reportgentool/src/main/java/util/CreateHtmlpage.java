package util;

import com.sterlite.pcc.parsing.csv.CsvLine;
import com.sterlite.pcc.parsing.csv.CsvResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateHtmlpage {

    private static final  String TOTAL = "TOTAL";
    private static final  String PASS = "PASS";
    private static final  String FAIL = "FAIL";
    private Map<String,Integer> statusMap;

    public CreateHtmlpage(){
       this.statusMap = new HashMap<>();
        statusMap.put(PASS,0);
        statusMap.put(FAIL,0);
        statusMap.put(TOTAL,0);

    }

    public void create(CsvResult testCases, String fileName, String outputFolderPath) {
        File file = new File(fileName);
        try (FileWriter fileWriter = new FileWriter(file); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                bufferedWriter.write("<!DOCTYPE html><html>" + CommonHtmlContents.getCommonIncludedFiles() + "<body>");
                bufferedWriter.append(CommonHtmlContents.statusReportDiv());
                bufferedWriter.append(testRailLinkDiv());
                bufferedWriter.append(getTableHeaders());
                int index = 1;
            for (CsvLine csvLine : testCases.getCsvData().getCsvLines()) {
                try {
                    bufferedWriter.append(getRows(csvLine.getColoumData(), index, outputFolderPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                index++;
            }


                bufferedWriter.append("</table></div></body>" + CommonHtmlContents.injectScript(2, "") + "</html>");
                bufferedWriter.flush();
                fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private String getTableHeaders() {
        StringBuilder headers = new StringBuilder("<div class='container'><table id='detailReportTable' class='table table-bordered table-blue'><thead class='thead-light'><tr><td>#</td>");
        for (String header : ConfigurationReader.getInstance().tableHeaders()) {
            headers.append("<td>").append(header).append("</td>");
        }
        headers.append("<td>Input JSON</td>").append("<td>Result JSON</td>").append("<td>Logs</td>");
        headers.append("</tr></thead>");
        return headers.toString();
    }

    private String getRows(List<String> columnRows, int index, String outputFolderPath) {
        StringBuilder row = new StringBuilder("<tr><td>").append(index).append("</td>");
        int inoutFileIndex = ConfigurationReader.getInstance().getIntVal(ConfProperties.TEST_INOUT_FILE_NAME_INDEX);
        String resultFileName = columnRows.get(inoutFileIndex);
        for (String columnNo : ConfigurationReader.getInstance().tableColumns()) {

            row.append(getTestStatus(columnRows.get(Integer.parseInt(columnNo))));
        }

        File file = new File(outputFolderPath + File.separator + "server_manager/"+ConfigurationReader.getInstance().getInputFileLocation(), resultFileName + ".json");
        boolean fileExist = file.exists();


        if (fileExist) {

            row.append("<td><a href='server_manager/input_data/"+getFileName(resultFileName) + ".json' target='_blank'>Input</a></td>").
                    append("<td><a href='server_manager/response_data/"+ getFileName(resultFileName) + ".json' target='_blank'>Result</a></td>").
                    append("<td><a href='server_manager/rest_automation_logs/" + getFileName(resultFileName) + ".log' target='_blank'>Logs</a></td>");
            row.append("</tr>");
            return row.toString();
        } else {
            row.append("<td><a href='#'>Input</a></td>").
                    append("<td><a href='server_manager/response_data/" + getFileName(resultFileName) + ".json' target='_blank'>Result</a></td>").
                    append("<td><a href='server_manager/rest_automation_logs/" + getFileName(resultFileName) + ".log' target='_blank'>Logs</a></td>");
            row.append("</tr>");
            return row.toString();

        }

    }

    private String getTestStatus(String input) {
        StringBuilder td = new StringBuilder();
        if (input == null) {
            td.append("<td>").append(input).append("</td>");
        }else if(input.trim().length() == 0) {
            td.append("<td>").append(input).append("</td>");
            //return "<td>"+input+"</td>";
        }else if(input.equalsIgnoreCase("true")) {
            td.append("<td class='pass'>");
            td.append(CommonHtmlContents.PASS);
            incrementPass();
            td.append("</td>");
        }else if (input.equalsIgnoreCase("false")) {
            td.append("<td class='fail'>");
            td.append(CommonHtmlContents.FAIL);
            incrementFail();
            td.append("</td>");
        }else{
            td.append("<td>").append(input).append("</td>");
        }
        return td.toString();

    }

    private String getFileName(String input) {
        return input.trim().toLowerCase();

    }

    private void incrementPass(){
        int passTestCases = statusMap.get(PASS);
        passTestCases++;
        statusMap.put(PASS,passTestCases);
    }

    private void incrementFail(){
        int failTestCases = statusMap.get(FAIL);
        failTestCases++;
        statusMap.put(FAIL,failTestCases);
    }

    public int getTotal(){
        return statusMap.get(PASS)+statusMap.get(FAIL);
    }

    public int getPass(){
        return statusMap.get(PASS);
    }

    public int getFail(){
        return statusMap.get(FAIL);
    }



    public void createStatusReport(String fileName){


        File file = new File("rest_automation_report/" + fileName);
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.append(CommonHtmlContents.generateStatusSummary("REST",getTotal(),getPass(),getFail()));
            bufferedWriter.flush();
            fileWriter.flush();

    }catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {

                bufferedWriter.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }



    public static String testRailLinkDiv(){
        StringBuilder testRailLinkDiv= new StringBuilder(" <div id='testRailDiv' class='container' style='text-align:center'>");
        testRailLinkDiv.append("<span><a href='")
                        .append(ConfigurationReader.getInstance().getRestTestRailLink())
                        .append("' target='_blank' >Test Rail Report")
                        .append("</a></span></div>");
        return testRailLinkDiv.toString();
    }
}

