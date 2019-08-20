package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by aditya on 12/11/17.
 */
public class ConfigurationReader {

    private static final String PROPERTY_FILE_NAME= "configuration.properties";
    private static final String INPUT_JSON_LOCATION="input.json.location";
    private static final String RESULT_JSON_LOCATION="result.json.location";
    private static final String LOG_FILE_LOCATION="log.file.location";
    private static final String REST_TEST_RAIL_LINK="rest.testrail.report.link";
    private static final String TABLE_HEADERS="table.headers";
    private static final String CSV_COLUMN_ORDER="csvfile.column.order";
    private static final String TEST_RESULT_INDEX="test.result.index";

   private String inputFileLocation;
   private String responseFileLocation;
   private String logFileLocation;



    private String restTestRailLink;

    private static final ConfigurationReader configurationProvider;
    private Properties properties;

    static {
        configurationProvider = new ConfigurationReader();
    }
    public static ConfigurationReader getInstance(){
        return configurationProvider;
    }

    public void init() {

        InputStream inputStream = null;

        try {
            properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE_NAME));
            inputFileLocation = inputJsonlocation();
            responseFileLocation = resultFileLocation();
            logFileLocation = logFileLocation();
            restTestRailLink=readRestTestRailLink();

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

    private String resultFileLocation(){
        return properties.getProperty(RESULT_JSON_LOCATION);
    }
    public int getTestResultIndex(){
        return Integer.parseInt(properties.getProperty(TEST_RESULT_INDEX));
    }

    private String inputJsonlocation(){
        return properties.getProperty(INPUT_JSON_LOCATION);
    }

    private String logFileLocation(){
        return properties.getProperty(LOG_FILE_LOCATION);
    }
    private String readRestTestRailLink(){
        return properties.getProperty(REST_TEST_RAIL_LINK);
    }

    public List<String> tableHeaders(){
        System.out.println(properties.getProperty(TABLE_HEADERS));
        return Arrays.asList(properties.getProperty(TABLE_HEADERS).split(","));
    }

    public List<String> tableColumns(){
        return Arrays.asList(properties.getProperty(CSV_COLUMN_ORDER).split(","));
    }


    public String getInputFileLocation() {
        return inputFileLocation;
    }

    public String getResponseFileLocation() {
        return responseFileLocation;
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

    public String getRestTestRailLink() {
        return restTestRailLink;
    }
}
