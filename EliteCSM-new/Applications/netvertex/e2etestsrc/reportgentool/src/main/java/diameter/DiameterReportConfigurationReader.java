package diameter;

import util.ConfProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Created by aditya on 12/11/17.
 */
public class DiameterReportConfigurationReader {
    private static final String PROPERTY_FILE_NAME= "diameter/diameter-conf.properties";
    private static final String LOG_FILE_LOCATION="log.file.location";
    private static final String AUTOMATION_LOG_FILE_LOCATION="automation.log.file.location";
    private static final String CSV_FILE_PATH="csv.file.path";
    private static final String REPORT_FOLDER_PATH = "report.folder.path";
    private static final String TABLE_HEADERS="table.headers";
    private static final String CSV_COLUMN_ORDER="csvfile.column.order";
    public static final String TESTCASE_ID_INDEX = "testcase.id.index";
    public static final String TESTCASE_FAILED_STEP_INDEX = "testcase.failed.stepIndex";
    public static final String TESTCASE_TIMESTAMP_INDEX = "testcase.timestamp.index";
    public static final String TESTCASE_TIMEELAPSED_INDEX = "testcase.timeelapsed.index";
    public static final String TESTCASE_STATUS_INDEX = "testcase.status.index";
    public static final String TESTCASE_FAILMESSAGE_INDEX = "testcase.failmessage.index";

    private String logFileLocation;

    private static final DiameterReportConfigurationReader configurationProvider;
    private Properties properties;

    static {
        configurationProvider = new DiameterReportConfigurationReader();
    }



    public static DiameterReportConfigurationReader getInstance(){
        return configurationProvider;
    }

    public void init() {

        InputStream inputStream = null;

        try {
            properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE_NAME));
            logFileLocation = logFileLocation();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private String logFileLocation(){
        return properties.getProperty(LOG_FILE_LOCATION);
    }

    public String getAutomationLogFileLocation(){
        return properties.getProperty(AUTOMATION_LOG_FILE_LOCATION);
    }

    public String getCsvFileLocation(){
        return properties.getProperty(CSV_FILE_PATH);
    }

    public List<String> tableHeaders(){
        String property = properties.getProperty(TABLE_HEADERS);

        if(property != null && property.trim().isEmpty() == false) {
            return Arrays.asList(property.trim().split(","));
        }

        return Collections.emptyList();
    }

    public List<String> tableColumns(){
        String property = properties.getProperty(CSV_COLUMN_ORDER);

        if(property != null && property.trim().isEmpty() == false) {
            return Arrays.asList(property.trim().split(","));
        }

        return Collections.emptyList();

    }


    public String getLogFileLocation() {
        return logFileLocation;
    }

    public int getIntVal(ConfProperties confProperties) {
        return Integer.parseInt(properties.getProperty(confProperties.getKey()));
    }

    public String getVal(ConfProperties confProperties) {
        return properties.getProperty(confProperties.getKey());
    }

    public String getReportFolderLocation() {
        return properties.getProperty(REPORT_FOLDER_PATH);
    }

    public int getTestCaseIdColumnIndex() {
        return Integer.parseInt(properties.getProperty(TESTCASE_ID_INDEX));
    }

    public int getFailedStepColoumIndex() {
        return Integer.parseInt(properties.getProperty(TESTCASE_FAILED_STEP_INDEX));
    }

    public int getElapedTimeIndex() {
        return Integer.parseInt(properties.getProperty(TESTCASE_TIMEELAPSED_INDEX));
    }

    public int getTimeStampIndex() {
        return Integer.parseInt(properties.getProperty(TESTCASE_TIMESTAMP_INDEX));
    }

    public int getStatusIndex() {
        return Integer.parseInt(properties.getProperty(TESTCASE_STATUS_INDEX));
    }

    public int getFaileMessageIndex() {
        return Integer.parseInt(properties.getProperty(TESTCASE_FAILMESSAGE_INDEX));
    }
}
