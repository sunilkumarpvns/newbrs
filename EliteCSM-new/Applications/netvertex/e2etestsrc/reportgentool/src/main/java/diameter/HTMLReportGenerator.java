package diameter;

import com.sterlite.pcc.parsing.csv.CsvHeader;
import com.sterlite.pcc.Logger;
import util.CommonHtmlContents;
import util.ValidationUtility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static j2html.TagCreator.attrs;
import static j2html.TagCreator.each;
import static j2html.TagCreator.table;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.td;
import static j2html.TagCreator.th;
import static j2html.TagCreator.thead;
import static j2html.TagCreator.tr;

public class HTMLReportGenerator {

    private static final String TOTAL = "TOTAL";
    private static final String PASS = "PASS";
    private static final String FAIL = "FAIL";
    private static final String PATH_DELMITER = "/";
    private Map<String, Integer> statusMap = null;
    private Logger logger = new Logger();

    public HTMLReportGenerator() {
        this.statusMap = new HashMap<>();
        statusMap.put(PASS, 0);
        statusMap.put(FAIL, 0);
        statusMap.put(TOTAL, 0);

    }

    public void create(Map<String, TestCase> questions, CsvHeader csvHeader, String outputPath, String fileName) {

        Path path = Paths.get(outputPath, fileName);


        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path.toFile()))) {


            bufferedWriter.write("<!DOCTYPE html><html>" + CommonHtmlContents.getCommonIncludedFiles() + "<body>");
            bufferedWriter.append(CommonHtmlContents.statusReportDiv());
            bufferedWriter.append(getTableHeaders());
            int index = 1;
            for (TestCase testCase : questions.values()) {
                try {
                    String rows = getRows(testCase, csvHeader ,index, outputPath);

                    logger.debug(rows);
                    bufferedWriter.append(rows);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                index++;
            }

            bufferedWriter.append("</table></div></body>" + CommonHtmlContents.injectScript(3, "SEC") + "</html>");
            bufferedWriter.append("</table></div></body></html>");
            bufferedWriter.flush();



        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private String getTableHeaders() {
        StringBuilder headers = new StringBuilder("<div class='container'><table id='detailReportTable' class='table table-bordered table-blue'><thead class='thead-light'><tr><td>#</td>");
        addColoum(headers, "");
        addColoum(headers, "Title");
        addColoum(headers, "Failed Step");
        addColoum(headers, "Start Time");
        addColoum(headers, "Duration");
        addColoum(headers, "Status");
        for (String header : getReader().tableHeaders()) {
            addColoum(headers, header);
        }

        addColoum(headers, "Server Logs");
        addColoum(headers, "PD Logs");
        addColoum(headers, "PCAPs");
        addColoum(headers, "Failed Message");
        headers.append("</tr></thead>");

        return headers.toString();
    }

    private DiameterReportConfigurationReader getReader() {
        return DiameterReportConfigurationReader.getInstance();
    }

    private String getRows(TestCase testCase,
        CsvHeader csvHeader,
        int index,
        String outputPath) {

        StringBuilder row = new StringBuilder("<tr><td>").append(index).append("</td>");

        addColoum(row, "<span class=\"glyphicon glyphicon-expand\"></span>");

        String title = getTestStatus(testCase.getTitle());

        addColoum(row, title, testCase.getTestRailTestCaseId());

        if (testCase.isFail()) {
            String failedTestStep = getTestStatus(testCase.getFailedTestSetp());
            addColoum(row, failedTestStep);
        } else {
            row.append("<td></td>");
        }

        addColoum(row, testCase.getStartTime().toString());

        addColoum(row, String.valueOf(testCase.getDuration()));

        if (testCase.isFail()) {
            incrementFail();
            addColoum(row, "<span class='fail'>FAIL</span>");
        } else {
            incrementPass();
            addColoum(row, "<span class='pass'>PASS</span>");
        }

        for (String columnNo : getReader().tableColumns()) {
            String testStatus = getTestStatus(testCase.lastStep().getAt(Integer.parseInt(columnNo)));
            addColoum(row, testStatus);
        }

        File folder = new File(outputPath+File.separator+"netvertex/" + getTestStatus(testCase.getTitle()));

        File serverLog = new File(outputPath+File.separator+"netvertex/" + getTestStatus(testCase.getTitle()) + "/" + getTestStatus(testCase.getTitle()) + "-server.log");
        File policyDesignerLog = new File(outputPath+File.separator+"netvertex/" + getTestStatus(testCase.getTitle()) + "/" + getTestStatus(testCase.getTitle()) + "-policy-designer.log");
        File pcap = new File(outputPath+File.separator+"netvertex/" + getTestStatus(testCase.getTitle()) + "/" + getTestStatus(testCase.getTitle()) + ".pcap");


        boolean folderExist = folder.exists();

        boolean serverLogExists = serverLog.exists();
        boolean policyDesignerLogExists = policyDesignerLog.exists();
        boolean pcapExist = pcap.exists();


        if (folderExist) {
            if (serverLogExists) {
                row.append("<td><a href='netvertex/" + getTestStatus(testCase.getTitle()) + "/" + getTestStatus(testCase.getTitle()) + "-server.log' target='_blank'>Logs</a></td>");
            } else {
                logger.info("Server log file " + serverLog.getAbsolutePath() + " not exit");
                row.append("<td><a href='#'>Logs</a></td>");
            }
            if (policyDesignerLogExists) {
                row.append("<td><a href='netvertex/" + getTestStatus(testCase.getTitle()) + "/" + getTestStatus(testCase.getTitle()) + "-policy-designer.log' target='_blank'>Logs</a></td>");
            } else {
                logger.info("PD log file " + policyDesignerLog.getAbsolutePath() + " not exit");
                row.append("<td><a href='#'>Logs</a></td>");
            }
            if (pcapExist) {
                row.append("<td><a href='netvertex/" + getTestStatus(testCase.getTitle()) + "/" + getTestStatus(testCase.getTitle()) + ".pcap'>PCAP</a></td>");
            } else {
                logger.info("PCAP file " + pcap.getAbsolutePath() + " not exit");
                row.append("<td><a href='#'>PCAP</a></td>");
            }

            addColoum(row, testCase.getFailMessage());
            row.append("</tr>");
        } else {
            logger.info("Log folder "+ folder.getAbsolutePath() +" not exit");
            row.append("<td><a href='#'>Logs</a></td>");
            row.append("<td><a href='#'>Logs</a></td>");
            row.append("<td><a href='#'>PCAP</a></td>");
            addColoum(row, testCase.getFailMessage());
            row.append("</tr>");
        }

        String childTable = addChild(testCase, csvHeader);
        Path path = Paths.get(folder.getAbsolutePath(), testCase.getTitle() + ".html");
        folder.mkdir();
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path.toFile()))) {
            bufferedWriter.write(childTable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return row.toString();

    }

    private String addChild(TestCase testCase, CsvHeader csvHeader) {

        return table(attrs("#table-example .table table-bordered table-blue"),
                thead(attrs(".thead-light"),
                        tr(
                                each(
                                        toIndexes(testCase), index -> th(
                                                csvHeader.getHeaderAt(index)
                                        )
                                )
                        )
                ),
                tbody(
                        each(
                            testCase.getSteps(), step -> tr(
                                    each(
                                            toIndexes(step), index -> th(
                                                    createColoumVal(index, step)
                                            )
                                    )
                            )
                        )
                )
        ).renderFormatted();
    }

    private String createColoumVal(int col, Step step) {

        if(col == DiameterReportConfigurationReader.getInstance().getTimeStampIndex()) {
            long startTime = Long.parseLong(step.getAt(DiameterReportConfigurationReader.getInstance().getTimeStampIndex()));
            Timestamp starTimeStamp = Timestamp.from(Instant.ofEpochMilli(startTime));
            return starTimeStamp.toString();
        } else {
            return  step.getAt(col);
        }

    }

    private List<Integer> toIndexes(TestCase testCase) {
        return IntStream.range(0, testCase.getSteps().get(0).getAll().size()).boxed().collect(Collectors.toList());
    }

    private List<Integer> toIndexes(Step step) {
        return IntStream.range(0, step.getAll().size()).boxed().collect(Collectors.toList());
    }

    private void addColoum(StringBuilder stringBuilder, String val, String testRailId) {
        if (ValidationUtility.isNullOrBlank(testRailId) == false && testRailId.equalsIgnoreCase("null") == false) {
            stringBuilder.append("<td>").append("<a href='http://192.168.2.138/csmqa/index.php?/cases/view/" + testRailId + "' target='_blank' >" + val + "</a>").append("</td>");//http://192.168.2.138/csmqa/index.php?/cases/view
        } else {
            stringBuilder.append("<td>").append(val).append("</td>");
        }
    }

    private void addColoum(StringBuilder stringBuilder, String val) {
        stringBuilder.append("<td>").append(val).append("</td>");
    }


    private String getTestStatus(String input) {
        if (input == null) {
            return input;
        }
        if (input.trim().length() == 0) {
            return input;
        }
        if (input.equalsIgnoreCase("true")) {
            return "PASS";
        }
        if (input.equalsIgnoreCase("false")) {
            return "FAIL";
        }
        return input;

    }


    private void incrementPass() {
        int passTestCases = statusMap.get(PASS);
        passTestCases++;
        statusMap.put(PASS, passTestCases);
    }

    private void incrementFail() {
        int failTestCases = statusMap.get(FAIL);
        failTestCases++;
        statusMap.put(FAIL, failTestCases);
    }

    public int getTotal() {
        return statusMap.get(PASS) + statusMap.get(FAIL);
    }

    public int getPass() {
        return statusMap.get(PASS);
    }

    public int getFail() {
        return statusMap.get(FAIL);
    }

}
