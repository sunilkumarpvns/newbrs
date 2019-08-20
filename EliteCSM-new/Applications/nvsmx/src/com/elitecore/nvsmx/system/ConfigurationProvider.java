package com.elitecore.nvsmx.system;


import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.systemparameter.SystemParameter;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.constants.NVSMXDefaults;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;


public class ConfigurationProvider {

    private static final String MODULE = "DEF-CONF-PRVDR";
    private static final String CONFIG_FILE_LOCATION = "/WEB-INF/NVSMXConfiguration.properties";

    private static final String LOGGER_LOCATION 	= "logger.location";
    private static final String LOGGER_LEVEL 		= "logger.loglevel";
    private static final String LOGGER_FILE_NAME 	= "logger.filename";
    private static final String MAX_FAILURE_COUNT 	= "max.failure.count";
    private static final String BLOCKING_PERIOD 	= "blocking.period";

    private static final String KEY_WEBSERVICE_TPS 		= "webservice.tps";
    private static final String SSO_CONFIG_KEY = "sso.enable";
    private static final String SSO_URL="sso.url";
    private static final String SSO_TITLE="sso.title";
    private static final int DEFAULT_WEB_SERVICE_TPS 	= 100;

    private static final int DEFAULT_MAX_FAILURE_COUNT 	= 10; // default no of failure count before blocking ip address
    private static final int DEFAULT_BLOCKING_PERIOD 	= 2; // values in minutes
    private static final int DEFAULT_PAGE_ROW_SIZE 		= 5; // default no of rows to display on a page

    private static final int MIN_FAILURE_COUNT		= 1;
    private static final int MIN_BLOCKING_PERIOD 	= 0;
    private static final int MIN_PAGE_ROW_SIZE 		= 5;
    private static final int MAX_PAGE_ROW_SIZE 		= 500;

    private static final String DEFAULT_SSO_TITLE = "EXTERNAL LINK";
    private static final String DEFAULT_SSO_URL = "#";
    private int maxFailureCount;
    private int blockingPeriod;
    private String logFileName;
    private String logLevel;
    private String logFileLocation;


    private static ConfigurationProvider configurationProvider;

    private Properties properties;
    private String contextFilePath;
    private String nvsmxContextPath;

    private String deploymentPath;

    private static final String DEFAULT_SNMP_IP = CommonConstants.ALL_IP_ADDRESS;
    private static final int DEFAULT_SNMP_PORT	 = 1162;
    private static final String SNMP_URL = "snmp.url";
    private String snmpAddress = DEFAULT_SNMP_IP;
    private int snmpPort = DEFAULT_SNMP_PORT;
	private static final int MIN_PORT = 1025;
	private static final int MAX_PORT = 65535;
    private int pageRowSize;

	private int webServiceTps;
	private static final boolean DEFAULT_SSO_ENABLE = false;
	private boolean ssoEnable;


	static {
        configurationProvider = new ConfigurationProvider();
    }

    public void init(String deploymentPath) {
        LogManager.getLogger().info(MODULE, "Reading Configuration properties for NVSMX");
        this.deploymentPath = deploymentPath;
        try(InputStream inputStream = new FileInputStream(deploymentPath + CONFIG_FILE_LOCATION) ) {
            properties = new Properties();
            properties.load(inputStream);
            readConfiguration();
            LogManager.getLogger().debug(MODULE, "Read Configuration properties successfully for NVSMX");
        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Considering default values. Reason : Error while reading configuration properties " + e.getMessage());
            LogManager.getLogger().trace(e);
            setDefaultValues();
        }
    }

    private void readConfiguration() {
        blockingPeriod 	= stringParamToInteger(BLOCKING_PERIOD, DEFAULT_BLOCKING_PERIOD, MIN_BLOCKING_PERIOD, Integer.MAX_VALUE);
        maxFailureCount = stringParamToInteger(MAX_FAILURE_COUNT, DEFAULT_MAX_FAILURE_COUNT, MIN_FAILURE_COUNT, Integer.MAX_VALUE);
        logFileLocation = readLogFileLocation();
        logFileName = readLogFileName();
        logLevel 	= readLogLevel();
        readSnmpURL();
        webServiceTps =  stringParamToInteger(KEY_WEBSERVICE_TPS, DEFAULT_WEB_SERVICE_TPS, 1, 1000);
        ssoEnable = readSSOConfig();
    }

    private boolean readSSOConfig() {
        String parameterValue = getParameterValue(SSO_CONFIG_KEY);
        if(StringUtils.isBlank(parameterValue)){
            return DEFAULT_SSO_ENABLE;
        }
        return BooleanUtils.toBoolean(parameterValue.trim());

    }
    private String readSSOUrl() {
        String parameterValue = getParameterValue(SSO_URL);
        if(StringUtils.isNotBlank(parameterValue)){
            return parameterValue.trim();
        }else{
            return DEFAULT_SSO_URL;
        }
    }
    private String readSSOTitle() {
        String parameterValue = getParameterValue(SSO_TITLE);
        if(StringUtils.isNotBlank(parameterValue)){
            return parameterValue.trim();
        }else{
            return DEFAULT_SSO_TITLE;
        }

    }


    public static ConfigurationProvider getInstance() {

        return configurationProvider;
    }


    /**
     * This method will return the default log file Location for nvsmx log
     * default value is "logs" which will be in deployment folder of nvsmx
     *
     * @return
     */
    public String getLogFileLocation() {
     return logFileLocation;
    }

    /**
     * This method will return the default log file name
     * default value is "nvsmx"
     * @return
     */
    public String getLogFileName() {
        return logFileName;
    }
    /**
     * This method will return the default log Level
     * default value is "6" which is for "ALL" log level
     * @return
     */
    public String getLogLevel() {
        return logLevel;
    }

    /**
     * This method will return the WebService TPS value.
     * default value is 100
     * @return
     */
    public int getWebServiceTps(){
    	return webServiceTps;
    }

	public String getLocalHostName(){
		String localHost=null;
		try {
			localHost=InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
            LogManager.getLogger().error(MODULE,"Error while fetching localhost name. Reason: "+e.getMessage());
            LogManager.getLogger().trace(MODULE,e);
		}
		return localHost;
	}

    public String getNvsmxContextPath() {
        return nvsmxContextPath;
    }

    public void setNvsmxContextPath(String nvsmxContextPath) {
        this.nvsmxContextPath = nvsmxContextPath;
    }

    public String getContextFilePath() {
        return contextFilePath;
    }

    public void setContextFilePath(String contextFilePath) {
        this.contextFilePath = contextFilePath;
    }




    public int getMaxFailureCount() {
        return maxFailureCount;
    }


    public int getBlockingPeriod() {
        return blockingPeriod;
    }

    public void readPageSize() {
        int pageSize;
        try {
            pageSize = Integer.parseInt(SystemParameterDAO.get(SystemParameter.TOTAL_ROW.name()));
            if(pageSize == 0 || pageSize > MAX_PAGE_ROW_SIZE || pageSize < MIN_PAGE_ROW_SIZE) {
                pageSize = DEFAULT_PAGE_ROW_SIZE;
            }
        } catch (Exception e) {
            LogManager.getLogger().error(MODULE,"Error while fetching row size from System Parameter. Reason: "+e.getMessage());
            LogManager.getLogger().trace(MODULE,e);
            pageSize = DEFAULT_PAGE_ROW_SIZE;

        }
        pageRowSize = pageSize;
    }

    public void reInit(){
        LogManager.getLogger().info(MODULE, "reloading page size");
        readPageSize();
    }

    public int getPageRowSize() {
        return pageRowSize;
    }

    public String getDeploymentPath() {
        return deploymentPath;
    }

    private void readSnmpURL(){

    	String strSnmpAddress = Strings.isNullOrBlank(properties.getProperty(SNMP_URL)) == false ? properties.getProperty(SNMP_URL) : DEFAULT_SNMP_IP;

		if(Strings.isNullOrBlank(strSnmpAddress)) {
			LogManager.getLogger().warn(MODULE, "SNMP address not configured");
			setDefaultSNMPAddressAndPort();
		} else {
			try {
				URLData urlData = URLParser.parse(strSnmpAddress.trim());
				snmpAddress = urlData.getHost();
				if(isValidPort(urlData.getPort())) {
					snmpPort = urlData.getPort();
				} else {
					LogManager.getLogger().warn(MODULE, "Considering default value " + snmpPort + " for SNMP port. Reason: invalid value: "
											+ urlData.getPort() + ", port must be between ["+ MIN_PORT + " to " + MAX_PORT +"]");
					setDefaultSNMPAddressAndPort();
				}
				LogManager.getLogger().info(MODULE, "Considering SNMP address: "+snmpAddress+":"+snmpPort+" for parameter : "+SNMP_URL);
			} catch (InvalidURLException e) {
				LogManager.getLogger().warn(MODULE, "Error while parsing URL: " + strSnmpAddress+". Reason: (Invalid SNMP URL configured) "+e.getMessage());
				LogManager.getLogger().trace(MODULE,e);
				setDefaultSNMPAddressAndPort();
			}
		}
    }

	private void setDefaultSNMPAddressAndPort() {
		snmpAddress = DEFAULT_SNMP_IP;
		snmpPort = DEFAULT_SNMP_PORT;
	}

	private boolean isValidPort(int port) {

        return port >= MIN_PORT && port <= MAX_PORT;
    }

    private String readLogLevel(){
        return Strings.isNullOrBlank(properties.getProperty(LOGGER_LEVEL)) == true ?
                NVSMXDefaults.LOG_LEVEL.getVal() : properties.getProperty(LOGGER_LEVEL);

    }

    private String readLogFileLocation(){
        return Strings.isNullOrBlank(properties.getProperty(LOGGER_LOCATION)) == true ?
                NVSMXDefaults.LOGFILE_LOCATION.getVal() : properties.getProperty(LOGGER_LOCATION);
    }

    private String readLogFileName(){
        return Strings.isNullOrBlank(properties.getProperty(LOGGER_FILE_NAME)) == true ?
                getLocalHostName() : properties.getProperty(LOGGER_FILE_NAME);
    }

    private int stringParamToInteger(String parameterName, Integer defaultValue, int minValue, int maxValue) {

        String parameterValueString = getParameterValue(parameterName);

        if (Strings.isNullOrBlank(parameterValueString) == true) {
            LogManager.getLogger().warn(MODULE, "Considering default value: " + defaultValue + " for " +
                    "parameter: " + parameterName + ", Reason: parameter value not found");
            return defaultValue;
        }

        try {
            int parameterValueInt = Integer.parseInt(parameterValueString.trim());
            if (parameterValueInt > maxValue) {
                LogManager.getLogger().warn(MODULE, "Considering default value:  " + defaultValue + " for parameter: " + parameterName + ", Reason: value: " +
                        parameterValueInt + " exceeding maximum value: " + maxValue);
                return defaultValue;
            } else if (parameterValueInt < minValue) {
                LogManager.getLogger().warn(MODULE, "Considering default value:  " + defaultValue + " for parameter: " + parameterName + ", Reason: value: " +
                        parameterValueInt + " is lower then minimum value: " + minValue);
                return defaultValue;
            } else {
                LogManager.getLogger().info(MODULE, "Considering value: " + parameterValueInt + " for parameter: " + parameterName);
                return parameterValueInt;
            }
        } catch (Exception e) {
            LogManager.getLogger().trace(MODULE, e);
            LogManager.getLogger().warn(MODULE, "Considering default value: " + defaultValue + " for " +
                    "parameter: " + parameterName + ", Reason: Invalid parameter value: " + parameterValueString +" configured");
            return defaultValue;
        }
    }

    private String getParameterValue(String propertyName) {
        return properties.getProperty(propertyName);
    }

    private void setDefaultValues() {
        blockingPeriod 	= DEFAULT_BLOCKING_PERIOD;
        maxFailureCount = DEFAULT_MAX_FAILURE_COUNT;
        logFileLocation = NVSMXDefaults.LOGFILE_LOCATION.getVal();
        logFileName = NVSMXDefaults.LOGFILE_NAME.getVal();
        logLevel 	= NVSMXDefaults.LOG_LEVEL.getVal();
        pageRowSize = DEFAULT_PAGE_ROW_SIZE;
        setDefaultSNMPAddressAndPort();
    }

	public String getSnmpAddress() {
		return snmpAddress;
	}

	public int getSnmpPort() {
		return snmpPort;
	}

    public boolean isSsoEnable() {
        return ssoEnable;
    }

    public String getSSOUrl()
    {
             return readSSOUrl();
    }
    public String getSsoTitle(){
        return  readSSOTitle();
    }
}