package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CommonHtmlContents {

    public static final String PASS= "PASS";
    public static final String FAIL ="FAIL";


    private CommonHtmlContents(){

    }

    public static String getCommonIncludedFiles(){
        StringBuilder header = new StringBuilder("<head>");
        header.append("<link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css'>");
        header.append("<link rel='stylesheet' href='https://cdn.datatables.net/1.10.16/css/dataTables.bootstrap.min.css'>");
        header.append("<link rel='stylesheet' href='report.css'>");
        header.append("<script src='https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js'></script>");
        header.append("<script src='https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js'></script>");
        header.append("<script src='https://cdn.datatables.net/1.10.16/js/dataTables.bootstrap.min.js'></script>");
        header.append("<script src='status_report.js'></script>");
        header.append("</head>");
        return header.toString();
    }


    public static String injectScript(int timeLapsedColumn,String baseUnit) {
        StringBuilder script = new StringBuilder("<script src='data-tables-utils.js'></script>");
        return script.toString();
    }


    public static String statusReportDiv(){
        StringBuilder statusReportDiv= new StringBuilder(" <div id='statusDiv' class='container'>");
        statusReportDiv.append("<table id='summaryReportTable' class='table table-bordered table-blue'><thead class='thead-light'>");
        statusReportDiv.append("<tr><td>Total Test Cases Executed</td><td>Passed</td><td>Failed</td><td>Total Execution Time</td></tr></thead><tbody></tbody></table></div>");
        return statusReportDiv.toString();

    }



    ///this is for mailer status
    public static String generateStatusSummary(String moduleName, int total, int pass, int fail){
        StringBuilder statusHtml = new StringBuilder("<html><head> <style>");
        statusHtml.append("table,td,th{border:1px solid #000;border-collapse:collapse;background-color:#ECF1F8}td{background-color:#fff}</style></head><body>");
        statusHtml.append("<table><tr><th>MODULE</th><th>TOTAL</th><th>PASS</th><th>FAIL</th></tr>");
        statusHtml.append("<tr>").append("<td>").append(moduleName).append("</td>")
                .append("<td>").append(total).append("</td>")
                .append("<td>").append(pass).append("</td>")
                .append("<td>").append(fail).append("</td></tr></table></body></html>");

        return  statusHtml.toString();

    }


    public static void createStatusReport(String fileName, String moduleName, int total, int pass, int fail){
        File file = new File(fileName);
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.append(CommonHtmlContents.generateStatusSummary(moduleName,total,pass,fail));
            bufferedWriter.flush();
            fileWriter.flush();
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(bufferedWriter!=null){
                    bufferedWriter.close();
                }
                if(fileWriter!=null){
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }


}
