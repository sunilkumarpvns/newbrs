package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.fileio.EliteFileWriter;
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.driverx.cdr.data.CSVStripMapping;
import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;
import com.elitecore.core.driverx.cdr.deprecated.CSVFieldMapping;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DriverTypes;
import com.elitecore.corenetvertex.spr.ProcessFailException;
import com.elitecore.corenetvertex.spr.RecordProcessor;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

/**
 * 
 * @author Jay Trivedi
 * 
 */
@SuppressWarnings("deprecation")
public class CSVFailOverDriver<T> extends BaseCSVDriver<T> implements RecordProcessor<T>{
	
	private static final String MODULE = "CSV-FAIL-OVER";
	private static final String LOOP_BACK_ADDRESS = "127.0.0.1";
	private static final String LOCAL = "LOCAL";
	private static final String DELETE = "Delete";
	private static final String SUFFIX = "suffix";
	private static final String FILE_NAME = "CDRs.csv";
	
	private final String csvHeaderLine;
	private final String sprName;

	public CSVFailOverDriver(String serverHome, String sprName, CSVDataBuilder<T> csvDataBuilder, TaskScheduler schedular){
		
		super(serverHome,schedular, null);
		this.sprName = sprName;
		this.csvHeaderLine = csvDataBuilder.getHeader();
		registerCSVLineBuilder(csvDataBuilder.getLineBuilder(new TimeSource() {
			
			@Override
			public long currentTimeInMillis() {
				return System.currentTimeMillis();
			}
		}));
	}
	
	@Override
	public String getCSVHeaderLine() {
		return csvHeaderLine;
	}
	
	@Override
	public CSVStripMapping getStripMapping(String key) {
		return null;
	}
	
	@Override
	public String getCounterFileName() {
		return serverHome + File.separator + "system" + File.separator + "cdr_sequence_csv_driver";
	}

	@Override
	public String getDriverName() {
		return sprName;
	}

	@Override
	public int getDriverType() {
		return DriverTypes.CSV_CDR_DRIVER;
	}
	
	@Override
	public String getTypeName() {
		return MODULE;
	}
	
	@Override
	public String getName() {
		return getDriverName();
	}
	
	@Override
	public String getDriverInstanceId() {
		return "0";
	}
	
	@Override
	public String getDriverInstanceUUID() {
		return UUID.randomUUID().toString();
	}

	@Override
	public SimpleDateFormat getCDRTimeStampFormat() {
		return new SimpleDateFormat(CommonConstants.DEFAULT_TIMESTAMP_FORMAT);
	}
	
	@Override
	public long getRollingUnit() {
		return EliteFileWriter.TIME_BASED_ROLLING_EVERY_DAY;
	}

	@Override
	public int getRollingType() {
		return EliteFileWriter.TIME_BASED_ROLLING;
	}

	@Override
	public int getPort() {
		return 22;
	}

	@Override
	public int getFailOverTime() {
		return 3;
	}

	@Override
	public boolean isHeader() {
		return true;
	}

	@Override
	public boolean isSequenceGlobalization() {
		return false;
	}

	@Override
	public String getDelimiter() {
		return ",";
	}

	@Override
	public String getFileName() {
		return FILE_NAME;
	}

	@Override
	public String getFileLocation() {
		return "data" + File.separator + "csvfiles";
	}

	@Override
	public String getPrefixFileName() {
		return sprName;
	}

	@Override
	public String getDefaultDirectoryName() {
		return null;
	}

	@Override
	public String getDirectoryName() {
		return null;
	}
	
	@Override
	public String getSequenceRange() {
		return null;
	}

	@Override
	public String getSequencePosition() {
		return SUFFIX;
	}

	@Override
	public String getAllocatingProtocol() {
		return LOCAL;
	}

	@Override
	public String getIPAddress() {
		return LOOP_BACK_ADDRESS;
	}

	@Override
	public String getRemoteLocation() {
		return null;
	}

	@Override
	public String getUserName() {
		return null;
	}

	@Override
	public String getPassword() {
		return null;
	}
	
	@Override
	public String decrypt(String encriptedPassword, int encriptionType) {
		String password = null;
		try {
			password = PasswordEncryption.getInstance().decrypt(encriptedPassword, encriptionType);
		} catch (NumberFormatException | NoSuchEncryptionException | DecryptionNotSupportedException | DecryptionFailedException e) {
			LogManager.getLogger().warn(MODULE, e.getMessage());
			ignoreTrace(e);
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, e);
		}
		return password;
	}

	@Override
	public String getPostOperation() {
		return DELETE;
	}

	@Override
	public String getArchiveLocation() {
		return null;
	}
	
	@Override
	public List<CSVFieldMapping> getCSVFieldMappings() {
		return Collections.emptyList();
	}

	@Override
	public Map<RollingTypeConstant, Integer> getRollingTypeMap() {
		return null;
	}

	@Override
	protected int getStatusCheckDuration() {
		return ESCommunicator.NO_SCANNER_THREAD;
	}

	@Override
	public String getMultipleDelimiter() {
		return null;
	}

	@Override
	public String getParameterValue(T request, String key) {
		return null;
	}
	
	@Override
	public void process(T t) throws ProcessFailException{
		
		try {
			handleRequest(t);
		} catch (DriverProcessFailedException e) {
			throw new ProcessFailException(e);
		}
	}

}
