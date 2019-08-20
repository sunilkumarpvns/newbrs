package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.driverx.cdr.data.CSVStripMapping;
import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;
import com.elitecore.core.driverx.cdr.deprecated.CSVFieldMapping;
import com.elitecore.core.driverx.cdr.deprecated.FileParametersResolvers;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.constants.DriverTypes;
import com.elitecore.netvertex.core.driver.cdr.conf.CSVDriverConfiguration;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

@SuppressWarnings("deprecation")
public class NetvertexCSVDriver extends BaseCSVDriver<ValueProviderExtImpl> {
	
	private static final String MODULE = "NV-CSVD";
	private String csvHeaderLine;
	private CSVDriverConfiguration csvDriverConf;
	private CSVLineBuilder<ValueProviderExtImpl> csvLineBuilder;
	
	public NetvertexCSVDriver(String serverHome,
							  CSVDriverConfiguration csvDriverConf,
							  TaskScheduler schedular,
							  CSVLineBuilder<ValueProviderExtImpl> csvLineBuilder,
							  String csvHeaderLine,
							  FileParametersResolvers<ValueProviderExtImpl> fileParametersResolvers){
		super(serverHome,schedular, fileParametersResolvers);
		this.csvDriverConf = csvDriverConf;
		this.csvLineBuilder = csvLineBuilder;
		this.csvHeaderLine = csvHeaderLine;
	}
	
	@Override
	public void init() throws DriverInitializationFailedException {
		LogManager.getLogger().info(MODULE, "Initializing CSV driver: " + csvDriverConf.getDriverName());
		try {
			super.init();

			registerCSVLineBuilder(csvLineBuilder);
		} catch (InitializationFailedException e) {
			throw new DriverInitializationFailedException("Error while initialising CSV driver " + csvDriverConf.getDriverName() + ". Reason: " + e.getMessage(), e);
		}
	}

	@Override
	public String getCSVHeaderLine() {
		return csvHeaderLine;
	}

	@Override
	public CSVStripMapping getStripMapping(String key) {
		return csvDriverConf.getStripMappings().get(key);
	}
	
	@Override
	public String getParameterValue(ValueProviderExtImpl valueProvider, String key) {
		return valueProvider.getRequest().getAttribute(key);
	}
	
	@Override
	public String getCounterFileName() {
		return serverHome + File.separator + "system" + File.separator + "cdr_sequence_csv_driver";
	}

	@Override
	public String getDriverName() {
		return csvDriverConf.getDriverName();
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
	public Map<RollingTypeConstant, Integer> getRollingTypeMap() {
		return csvDriverConf.getRollingTypeMap();
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
	public void handleRequest(ValueProviderExtImpl response) throws DriverProcessFailedException {
		response.getResponse().setChargingCDRDateFormat(csvDriverConf.getCDRTimeStampFormat());
		super.handleRequest(response);
	}
}
