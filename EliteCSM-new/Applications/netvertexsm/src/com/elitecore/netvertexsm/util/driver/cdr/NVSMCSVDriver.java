package com.elitecore.netvertexsm.util.driver.cdr;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.driverx.cdr.data.CSVStripMapping;
import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;
import com.elitecore.core.driverx.cdr.deprecated.CSVFieldMapping;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.constants.DriverTypes;
import com.elitecore.netvertexsm.util.BoDServiceRequest;
import com.elitecore.netvertexsm.util.EliteUtility;
import com.elitecore.netvertexsm.util.driver.cdr.conf.CSVDriverConfiguration;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class NVSMCSVDriver extends BaseCSVDriver<BoDServiceRequest> {
	
	private static final String MODULE = "NVSM-CSVD";
	
	private final String CDRTIMESTAMP = "CDRTIMESTAMP";
	
	private String csvHeaderLine;
	private CSVDriverConfiguration csvDriverConf;
	
	public NVSMCSVDriver(CSVDriverConfiguration csvDriverConf, TaskScheduler taskScheduler) {
		super(EliteUtility.getSMHome(), taskScheduler);
		this.csvDriverConf = csvDriverConf;
	}
	
	public void init() throws DriverInitializationFailedException {
		try {
			LogManager.getLogger().info(MODULE, "Initialising CSV Driver");
			super.init();
		} catch (InitializationFailedException e) {
			throw new DriverInitializationFailedException("Error while initialising CSV driver. Reason: " + e.getMessage(), e);
		}
	}
	
	@Override
	public String getCSVHeaderLine() {
		if(csvHeaderLine == null) {
			StringBuilder csvHeaderLine = new StringBuilder();
			for(CSVFieldMapping mapping : getCSVFieldMappings()) 
				csvHeaderLine.append(mapping.getHeaderField()).append(csvDriverConf.getDelimiter());
			
			if ( getCDRTimeStampFormat() != null ) {
				csvHeaderLine.append(CDRTIMESTAMP);
			} else {
				csvHeaderLine.delete(csvHeaderLine.length() - getDelimiter().length(), csvHeaderLine.length());
			}
			this.csvHeaderLine = csvHeaderLine.toString();
		}
		return csvHeaderLine;
	}
	
	@Override
	public CSVStripMapping getStripMapping(String key) {
		return csvDriverConf.getStripMappings().get(key);
	}
	
	@Override
	public String getParameterValue(BoDServiceRequest request, String key) {
		return request.getAttribute(key); 
	}
	
	@Override
	public String getCounterFileName() {
		return EliteUtility.getSMHome() + File.separator + "WEB-INF" + File.separator + "cdr_sequence_csv_driver";
	}

	public String getDriverName() {
		return csvDriverConf.getDriverName();
	}

	@Override
	public int getDriverType() {
		return DriverTypes.CSV_CDR_DRIVER;
	}
	
	@Override
	public long getDriverInstanceId() {
		return csvDriverConf.getDriverInstanceId();
	}
	
	@Override
	public String getDriverInstanceUUID() {
		return null;
	}

	@Override
	public SimpleDateFormat getCDRTimeStampFormat() {
		return csvDriverConf.getCDRTimeStampFormat();
	}
	
	@Override
	public long getRollingUnit() {
		return csvDriverConf.getRollingUnit();
	}

	@Override
	public int getRollingType() {
		return csvDriverConf.getRollingType();
	}

	@Override
	public int getPort() {
		return csvDriverConf.getPort();
	}

	@Override
	public int getFailOverTime() {
		return csvDriverConf.getFailOverTime();
	}

	@Override
	public boolean isHeader() {
		return csvDriverConf.isHeader();
	}

	@Override
	public boolean isSequenceGlobalization() {
		return csvDriverConf.isSequenceGlobalization();
	}

	@Override
	public String getDelimiter() {
		return csvDriverConf.getDelimiter();
	}

	@Override
	public String getFileName() {
		return csvDriverConf.getFileName();
	}

	@Override
	public String getFileLocation() {
		return csvDriverConf.getFileLocation();
	}

	@Override
	public String getPrefixFileName() {
		return csvDriverConf.getPrefixFileName();
	}

	@Override
	public String getDefaultDirectoryName() {
		return csvDriverConf.getDefaultFolderName();
	}

	@Override
	public String getDirectoryName() {
		return csvDriverConf.getDirectoryName();
	}
	
	@Override
	public String getSequenceRange() {
		return csvDriverConf.getSequenceRange();
	}

	@Override
	public String getSequencePosition() {
		return csvDriverConf.getSequencePosition();
	}

	@Override
	public String getAllocatingProtocol() {
		return csvDriverConf.getAllocatingProtocol();
	}

	@Override
	public String getIPAddress() {
		return csvDriverConf.getAddress();
	}

	@Override
	public String getRemoteLocation() {
		return csvDriverConf.getRemoteLocation();
	}

	@Override
	public String getUserName() {
		return csvDriverConf.getUserName();
	}

	@Override
	public String getPassword() {
		return csvDriverConf.getPassword();
	}
	
	@Override
	public String decrypt(String encriptedPassword, int encriptionType) {
		String password = null;
		try {
			password = PasswordEncryption.getInstance().decrypt(encriptedPassword, encriptionType);
		} catch (NumberFormatException e) {
			LogManager.getLogger().warn(MODULE, e.getMessage());
		} catch (NoSuchEncryptionException e) {
			LogManager.getLogger().warn(MODULE, e.getMessage());
		} catch (DecryptionNotSupportedException e) {
			LogManager.getLogger().warn(MODULE, e.getMessage());
		} catch (DecryptionFailedException e) {
			LogManager.getLogger().warn(MODULE, e.getMessage());
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, e);
		}
		return password;
	}

	@Override
	public String getPostOperation() {
		return csvDriverConf.getPostOperation();
	}

	@Override
	public String getArchiveLocation() {
		return csvDriverConf.getArchiveLocation();
	}
	
	@Override
	public List<CSVFieldMapping> getCSVFieldMappings() {
		return csvDriverConf.getCDRFieldMappings();
	}

	@Override
	protected int getStatusCheckDuration() {
		return 0;
	}

	@Override
	public String getMultipleDelimiter() {
		return null;
	}

	@Override
	public String getName() {
		return csvDriverConf.getDriverName();
	}

	@Override
	public String getTypeName() {
		return "CSV_CDR_DRIVER";
	}

}
