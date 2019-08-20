package com.elitecore.netvertexsm.util.driver.cdr.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.engine.spi.SessionImplementor;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.fileio.EliteFileWriter;
import com.elitecore.core.commons.fileio.loactionalloactor.FileLocationAllocatorFactory;
import com.elitecore.core.driverx.cdr.data.CSVStripMapping;
import com.elitecore.core.driverx.cdr.deprecated.CSVFieldMapping;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;
import com.elitecore.corenetvertex.constants.DriverTypes;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.util.driver.cdr.conf.CSVDriverConfiguration;
import com.elitecore.netvertexsm.util.logger.Logger;

public class CSVDriverConfigurationImpl implements CSVDriverConfiguration {

	public static final String MODULE = "CSV-CDRD-CNF";
	
	private static final String DEFAULT_TIMESTAMP_FORMAT = "dd-MMM-yy hh.mm.ss.S a";
	private int driverInstanceId;
	private int rollingType = EliteFileWriter.TIME_BASED_ROLLING;
	private long rollingUnit = EliteFileWriter.TIME_BASED_ROLLING_EVERY_DAY;
	private int port;
	private int failOverTime = 3;
	
	private boolean header = true;
	private boolean sequenceGlobalization = false;
	
	private String driverName;
	private String delimiter = ",";
	private String fileName = "Netvertex-CDR.csv";
	private String fileLocation = "data/csvfiles";
	private String prefixFileName;
	private String defaultDirName;
	private String dirName;
	private String sequenceRange;
	private String sequencePosition;
	private String allocatingProtocol = "Local";
	private String address = "0.0.0.0";
	private String remoteLocation;
	private String userName;
	private String password;
	private String postOperation = "Delete";
	private String archiveLocation;
	private SimpleDateFormat cdrTimeStampFormat;
	private String reportingType;
	private String usageKeyHeader;
	private String inputOctetsHeader;
	private String outputOctetsHeader;
	private String totalOctetsHeader;
	private String usageTimeHeader;
	
	private List<CSVFieldMapping> fieldMappings;
	private Map<String, CSVStripMapping> stripMappings;
	
	public CSVDriverConfigurationImpl(int driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
		fieldMappings = new ArrayList<CSVFieldMapping>();
		stripMappings = new HashMap<String, CSVStripMapping>();
	}
	
	public void readConfiguration() throws LoadConfigurationException {
		Logger.logInfo(MODULE, "Reading CSV driver configuration for DriverInstanceId: " + driverInstanceId);
		Connection connection = null;
		PreparedStatement preparedStatement = null, psFields = null, psStripMapping = null;
		ResultSet resultSet = null, rsFields = null, rsStripMapping = null;
		List<CSVFieldMapping> tempFieldMappings; 
		String tempValue;
		
		try {
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			SessionImplementor sim  = (SessionImplementor) session;
			//SessionFactoryImplementor impl = (SessionFactoryImplementor)((HibernateDataSession)session).getSession().getSessionFactory();
			connection = sim.connection();// DBConnectionManager.getInstance().getSMDatabaseConection();
			if(connection == null) {
				Logger.logError(MODULE,"No connection available while reading CSV driver configuration");
				throw new LoadConfigurationException("Cannot establish connection to database");
			}
			preparedStatement = connection.prepareStatement(getQueryForCSVDriver());
			preparedStatement.setInt(1, driverInstanceId);
			resultSet = preparedStatement.executeQuery();

			while(resultSet.next()) {
				int csvDriverId = resultSet.getInt("CSVDRIVERID");
				driverName = resultSet.getString("NAME").trim();
				header = Boolean.parseBoolean(resultSet.getString("HEADER"));
				
				tempValue = resultSet.getString("CDRTIMESTAMPFORMAT");
				if(tempValue != null && tempValue.trim().isEmpty() == false) {
					try {
						cdrTimeStampFormat = new  SimpleDateFormat(tempValue);
					} catch (IllegalArgumentException e) {
						cdrTimeStampFormat = new SimpleDateFormat(DEFAULT_TIMESTAMP_FORMAT);
						Logger.logWarn(MODULE, "Using default timestamp format: " + DEFAULT_TIMESTAMP_FORMAT + ". Reason: Invalid cdr timestamp format: " + tempValue);
						Logger.logTrace(MODULE, e);
					}
				} else {
					Logger.logInfo(MODULE, "Timestamp format not configured. timestamp will not going to be dumped for csv file");
				}
				
				tempValue = resultSet.getString("DELIMITER");
				if(tempValue != null && !tempValue.trim().isEmpty())
					delimiter = tempValue;
				
				tempValue = resultSet.getString("FILENAME");
				if(tempValue != null && !tempValue.trim().isEmpty())
					fileName = tempValue;
				
				tempValue = resultSet.getString("FILELOCATION");
				if(tempValue != null && !tempValue.trim().isEmpty())
					fileLocation = tempValue;
				
				tempValue = resultSet.getString("DEFAULTFOLDERNAME");
				if(tempValue != null && !tempValue.trim().isEmpty())
					defaultDirName = tempValue;
				
				tempValue = resultSet.getString("FOLDERNAME");
				if(tempValue != null && !tempValue.trim().isEmpty())
					dirName = tempValue;
				
				tempValue = resultSet.getString("PREFIXFILENAME");
				if(tempValue != null && !tempValue.trim().isEmpty())
					prefixFileName = tempValue;
				
				rollingType = Integer.parseInt(resultSet.getString("FILEROLLINGTYPE"));
				rollingUnit = Long.parseLong(resultSet.getString("ROLLINGUNIT"));
					
				tempValue = resultSet.getString("SEQUENCERANGE");
				if(tempValue != null && !tempValue.trim().isEmpty())
					sequenceRange = tempValue;
				
				tempValue = resultSet.getString("SEQUENCEPOSITION");
				if(tempValue != null && !tempValue.trim().isEmpty())
					sequencePosition = tempValue;

				sequenceGlobalization = Boolean.parseBoolean(resultSet.getString("SEQUENCEGLOBALIZATION"));
				
				tempValue = resultSet.getString("ALLOCATINGPROTOCOL"); 
				if(tempValue != null && !tempValue.trim().isEmpty())
					allocatingProtocol = tempValue;
				
				tempValue = resultSet.getString("ADDRESS");
				if(!allocatingProtocol.equalsIgnoreCase("local")) {
					if(tempValue != null && !tempValue.trim().isEmpty()) {
						try {
							URLData urlData = URLParser.parse(tempValue);
							address = urlData.getHost();
							port = urlData.getPort();
							if(urlData.getPort() == URLParser.UNKNOWN_PORT) {
								if(FileLocationAllocatorFactory.FTPS.equalsIgnoreCase(allocatingProtocol))
									port = 22;
								else if(FileLocationAllocatorFactory.SMTP.equalsIgnoreCase(allocatingProtocol))
									port = 25;
							}
						} catch (InvalidURLException e) {
							Logger.logError(MODULE, "Error while parsing address. Reason: Invalid address");
						}
					} else {
						Logger.logError(MODULE, "Using default file allocator address " + address + ":" + port + ". Reason: Address not configured for remote file allocator");
					}
				}
				
				tempValue = resultSet.getString("REMOTEFILELOCATION");
				if(tempValue != null && !tempValue.trim().isEmpty())
					remoteLocation = tempValue;
				
				userName = resultSet.getString("USERNAME");
				password = resultSet.getString("PASSWORD");
				
				tempValue = resultSet.getString("POSTOPERATION");
				if(tempValue != null && !tempValue.trim().isEmpty())
					postOperation = tempValue;
				
				tempValue = resultSet.getString("ARCHIVELOCATION");
				if(tempValue != null && !tempValue.trim().isEmpty())
					archiveLocation = tempValue;
				
				failOverTime = Integer.parseInt(resultSet.getString("FAILOVERTIME"));
				reportingType = resultSet.getString("REPORTINGTYPE");
				
				if(reportingType != null) {
					tempValue = resultSet.getString("USAGEKEYHEADER");
					if(tempValue != null && !tempValue.trim().isEmpty())
						usageKeyHeader = tempValue;
					
					tempValue = resultSet.getString("INPUTOCTETSHEADER");
					if(tempValue != null && !tempValue.trim().isEmpty())
						inputOctetsHeader = tempValue;
					
					tempValue = resultSet.getString("OUTPUTOCTETSHEADER");
					if(tempValue != null && !tempValue.trim().isEmpty())
						outputOctetsHeader = tempValue;
					
					tempValue = resultSet.getString("TOTALOCTETSHEADER");
					if(tempValue != null && !tempValue.trim().isEmpty())
						totalOctetsHeader = tempValue;
					
					tempValue = resultSet.getString("USAGETIMEHEADER");
					if(tempValue != null && !tempValue.trim().isEmpty())
						usageTimeHeader = tempValue;
				}
				
				psFields = connection.prepareStatement(getQueryForFieldMap());
				if(psFields == null){
					Logger.logDebug(MODULE,"PreparedStatement is null.");
					throw new LoadConfigurationException("Prepared statement is null.");
				}
				psFields.setInt(1, csvDriverId);
				rsFields = psFields.executeQuery();
				tempFieldMappings = new ArrayList<CSVFieldMapping>();
				while (rsFields.next()) {
					tempFieldMappings.add(new CSVFieldMapping(rsFields.getString("HEADERFIELD"), rsFields.getString("PCRFKEY")));
				}
				fieldMappings = tempFieldMappings; 
				
				psStripMapping = connection.prepareStatement(getQueryForStripMapping());
				if(psStripMapping == null){
					Logger.logDebug(MODULE,"PreparedStatement is null.");
					throw new LoadConfigurationException("Prepared statement is null.");
				}
				psStripMapping.setInt(1, csvDriverId);
				rsStripMapping = psStripMapping.executeQuery();
				Map<String, CSVStripMapping> tempStripMappings = new HashMap<String, CSVStripMapping>();
				while (rsStripMapping.next()) {
					String key = rsStripMapping.getString("PCRFKEY");
					String pattern = rsStripMapping.getString("PATTERN");
					String separator = rsStripMapping.getString("SEPARATOR");
					tempStripMappings.put(key, new CSVStripMapping(key, pattern, separator));
				}
				stripMappings = tempStripMappings; 
			}
		} catch (SQLException sqlEx) {
			throw new LoadConfigurationException("Error while reading CSV driver configuration. Reason: " + sqlEx.getMessage() , sqlEx);
		} catch (Exception ex) {
			Logger.logError(MODULE, "Error while reading CSV driver = "+ driverInstanceId +". Reason: " + ex.getMessage());
			Logger.logTrace(MODULE, ex);
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(rsFields);
			DBUtility.closeQuietly(rsStripMapping);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(psFields);
			DBUtility.closeQuietly(psStripMapping);
			DBUtility.closeQuietly(connection);
		}
	}

	private String getQueryForStripMapping() {
		return "SELECT * FROM TBLMCSVSTRIPMAPPING WHERE CSVDRIVERID=?";
	}

	private String getQueryForCSVDriver() {
		return "SELECT A.*,B.NAME FROM TBLMCSVDRIVER A, TBLMDRIVERINSTANCE B WHERE A.DRIVERINSTANCEID=B.DRIVERINSTANCEID AND B.DRIVERINSTANCEID=?";
	}
	
	private String getQueryForFieldMap() {
		return "SELECT * FROM TBLMCSVDRIVERFIELDMAP WHERE CSVDRIVERID=?";
	}

	@Override
	public int getDriverInstanceId() {
		return driverInstanceId;
	}

	@Override
	public int getDriverTypeId() {
		return DriverTypes.CSV_CDR_DRIVER;
	}

	@Override
	public String getDriverName() {
		return driverName;
	}
	
	@Override
	public long getRollingUnit() {
		return rollingUnit;
	}

	@Override
	public int getRollingType() {
		return rollingType;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public int getFailOverTime() {
		return failOverTime;
	}

	@Override
	public boolean isHeader() {
		return header;
	}
	
	@Override
	public SimpleDateFormat getCDRTimeStampFormat() {
		return cdrTimeStampFormat;
	}

	@Override
	public boolean isSequenceGlobalization() {
		return sequenceGlobalization;
	}

	@Override
	public String getDelimiter() {
		return delimiter;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public String getFileLocation() {
		return fileLocation;
	}

	@Override
	public String getPrefixFileName() {
		return prefixFileName;
	}

	@Override
	public String getDefaultFolderName() {
		return defaultDirName;
	}

	@Override
	public String getDirectoryName() {
		return dirName;
	}
	
	@Override
	public String getSequenceRange() {
		return sequenceRange;
	}

	@Override
	public String getSequencePosition() {
		return sequencePosition;
	}

	@Override
	public String getAllocatingProtocol() {
		return allocatingProtocol;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public String getRemoteLocation() {
		return remoteLocation;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getPostOperation() {
		return postOperation;
	}

	@Override
	public String getArchiveLocation() {
		return archiveLocation;
	}
	
	@Override
	public String getReportingType() {
		return reportingType;
	}
	
	@Override
	public String getUsageKeyHeader() {
		return usageKeyHeader;
	}

	@Override
	public String getInputOctetsHeader() {
		return inputOctetsHeader;
	}
	
	@Override
	public String getOutputOctetsHeader() {
		return outputOctetsHeader;
	}

	@Override
	public String getTotalOctetsHeader() {
		return totalOctetsHeader;
	}

	@Override
	public String getUsageTimeHeader() {
		return usageTimeHeader;
	}

	@Override
	public List<CSVFieldMapping> getCDRFieldMappings() {
		return fieldMappings;
	}
	
	@Override
	public Map<String, CSVStripMapping> getStripMappings() {
		return stripMappings;
	}
}
