package com.elitecore.core.driverx.cdr;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.driverx.ExpressionFactory;
import com.elitecore.core.driverx.ValueExpression;
import com.elitecore.core.driverx.cdr.data.CSVFieldMapping;

public class CSVBuilderImpl  implements CSVBuilder{
	
	
	private static final String MODULE="CSV-BUILDER";
	

	private CSVDriverConfiguration driverConfiguration;
	private ExpressionFactory expressionFactory;

	public CSVBuilderImpl(CSVDriverConfiguration driverConfiguration,
			ExpressionFactory expressionFactory) {
		super();
		this.driverConfiguration = driverConfiguration;
		this.expressionFactory = expressionFactory;
	}



	@Override
	public CSVLineBuilder buildLineBuilder() throws Exception {
		
		List<ValueExpression> expressions = new ArrayList<ValueExpression>();
		Map<ValueExpression, String> defaultValueMap = new IdentityHashMap<ValueExpression, String>();
		
		
		for(CSVFieldMapping mappings : driverConfiguration.getCSVFieldMappings()){
			String key = mappings.getKey();
			ValueExpression expression = expressionFactory.newInstance(key);
			expressions.add(expression);
			defaultValueMap.put(expression,mappings.getDefaultValue());
		}
	
		SimpleDateFormat cdrTimestampFormat = new SimpleDateFormat();
		try{
			if(driverConfiguration.getCDRTimeStampFormat() != null){
				cdrTimestampFormat = driverConfiguration.getCDRTimeStampFormat();
			}
		} catch (IllegalArgumentException e) { 
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE,	"Invalid CDRTimeStampFormat: "
						+ driverConfiguration.getCDRTimeStampFormat()
						+ " for driver :" + driverConfiguration.getDriverName() +". So Applying Default Time-Stamp format");
			}
			ignoreTrace(e);
		}

		CSVLineBuilderParams.ParamBuilders csvParamBuilder = new CSVLineBuilderParams.ParamBuilders();
		CSVLineBuilderParams csvLineBuilderParams = csvParamBuilder.withCdrTimeStampFormat(cdrTimestampFormat).
													withDelimiter(driverConfiguration.getDelimiter()).
													withEnclosingCharacter(driverConfiguration.getEnclosingCharacter()).
													withMultiValueDelimiter(driverConfiguration.getMultipleDelimiter()).build();

		return new CSVLineBuilderImpl(csvLineBuilderParams,expressions,defaultValueMap);
	}



	@Override
	public CSVPathAllocator buildPathAllocator(String serverHome) throws Exception {
		String currentLocation = driverConfiguration.getFileLocation();
		/*
		 * current location will be the dynamic path provided in the driver configuration.
		 * Here the write permission at current location is been checked. If the write permission is not provided at the currentLocation, 
		 * then the current location will be the default location i.e. serverHome/data/csvfiles
		 * At the default location, the CSV files will be dumped. 
		 */
		if(currentLocation != null) {
			File file = new File(currentLocation);
			try{
				if(file.isAbsolute() == true) {
					if(file.exists() == true) {
						if(file.canWrite() == false) {
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Driver: " + driverConfiguration.getDriverName() + ", no write access at locaiton: "+currentLocation 
										+ ". Using default location");
							}
							currentLocation = createFolderAtDefaultLocation(serverHome);
						}
					} else {
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
							LogManager.getLogger().info(MODULE, "Driver: " + driverConfiguration.getDriverName() + ", directory : " + currentLocation + " does not exist. Creating the directory.");
						}
						try {
							if(file.mkdirs() == false) {
								if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
									LogManager.getLogger().warn(MODULE, "Driver: " + driverConfiguration.getDriverName() + ", problem in creating the directory: "
											+currentLocation);
								}
								createFolderAtDefaultLocation(serverHome);
							}
						} catch(SecurityException ex) { 
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Driver: " + driverConfiguration.getDriverName() + ", problem in creating the directory: "
										+currentLocation + ". Using default location");
							}
							ignoreTrace(ex);
							currentLocation= createFolderAtDefaultLocation(serverHome);
						}
					}
				} else {
					currentLocation = serverHome + File.separator + currentLocation;
					file = new File(currentLocation);
					if(file.exists() == true) {
						if(file.canWrite() == false) {	
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Driver: " + driverConfiguration.getDriverName() + ", no write access at locaiton: "
										+currentLocation + ". Using default location");
							}
							currentLocation= createFolderAtDefaultLocation(serverHome);
						}
					} else {
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
							LogManager.getLogger().info(MODULE, "Driver: " + driverConfiguration.getDriverName() + ", directory : " + currentLocation 
									+ " does not exist. Creating the directory.");
						}
						try {
							if(file.mkdirs() == false) {
								if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
									LogManager.getLogger().warn(MODULE, "Driver: " + driverConfiguration.getDriverName() + ", problem in creating the directory: "
										+currentLocation + ". Using default location");
								}
								currentLocation= createFolderAtDefaultLocation(serverHome);
							}
						} catch(SecurityException ex) { 
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Driver: " + driverConfiguration.getDriverName() 
										+ ", problem in creating the directory: "+currentLocation + ". Using default location");
							}
							ignoreTrace(ex);
							currentLocation= createFolderAtDefaultLocation(serverHome);
						}
					}
				}
			} catch(SecurityException ex) {	
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Driver: " + driverConfiguration.getDriverName() + ", no read permission at location: " 
							+ currentLocation + ". Using default location");
				}
				ignoreTrace(ex);
				currentLocation= createFolderAtDefaultLocation(serverHome);
			}
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Driver: " + driverConfiguration.getDriverName() + ", file location not configured. Using default location.");
			}
			currentLocation= createFolderAtDefaultLocation(serverHome);
		}
		CSVPathAllocatorParams.CSVPathAllocatorBuilder csvPathAllocatorBuilder= new CSVPathAllocatorParams.CSVPathAllocatorBuilder();
		CSVPathAllocatorParams csvPathAllocatorParams = csvPathAllocatorBuilder.withDefaultDirectoryName(driverConfiguration.getDefaultDirectoryName()).
														withDirectoryName(driverConfiguration.getDirectoryName()).
														withFileLocation(currentLocation).build();
		
		return CSVPathAllocatorImpl.create(csvPathAllocatorParams, expressionFactory);
	
	}
	
	private String createFolderAtDefaultLocation(String serverHome) throws DriverInitializationFailedException {
		String currentLocation = serverHome + File.separator + "data" + File.separator + "csvfiles";
		
		if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
			LogManager.getLogger().warn(MODULE, "Driver: " + driverConfiguration.getDriverName() + ", creating folder at default location: " + currentLocation);
		}
		
		File file = new File(currentLocation);
		try {
			if(file.exists() == false) {
				if(file.mkdirs() == false) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
						LogManager.getLogger().error(MODULE, "Driver: " + driverConfiguration.getDriverName() + ", cannot create directory at default location: " 
								+ currentLocation);
					}
					throw new DriverInitializationFailedException("Driver: " + driverConfiguration.getDriverName() 
							+ ", problem in creating directory at configured and default location.");
				} else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Driver: " + driverConfiguration.getDriverName() + ", directory created successfully at default location.");
					}
				}
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Driver: " +driverConfiguration.getDriverName() + ", directory:" + currentLocation + " already exists.");
				}
				if(file.canWrite() == false) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
						LogManager.getLogger().error(MODULE, "Driver: " + driverConfiguration.getDriverName() + ", no write access in Default directory: "+ currentLocation);
					}
					throw new DriverInitializationFailedException("Driver: " + driverConfiguration.getDriverName() 
							+ ", problem in creating directory at configured and default location.");
				}
			}
		} catch(SecurityException ex) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Driver: " + driverConfiguration.getDriverName() + ", problem in creating the Default directory: "+ currentLocation);
			}
			throw new DriverInitializationFailedException("Driver: " +driverConfiguration.getDriverName() 
					+ ", problem in creating directory at configured and default location.", ex);
		}
		return currentLocation;
	}
	
}
