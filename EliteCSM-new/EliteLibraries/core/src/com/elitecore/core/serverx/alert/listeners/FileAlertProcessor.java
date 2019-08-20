package com.elitecore.core.serverx.alert.listeners;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.util.logger.AlertRollingFileLogger;

import java.io.File;

public class FileAlertProcessor extends BaseAlertProcessor {
	private static final String LOGS_FOLDER = "logs";
	
	private String fileName;
	private int rollingType;
	private int rollingUnit;
	private int maxRollingUnit;
	private boolean compRollingUnit;
	private String processorId;
	
	private ILogger alertFileLogger;
	private static final String DFLT_ALRT_FOLDER = "alertlog";
	
	public FileAlertProcessor(ServerContext serverContext,
							  String processorId,
							  String filaName,
							  int rollingType,
							  int rollingUnit,
							  int maxRollingUnit,
							  boolean compRollingUnit) {
		super(serverContext);
		this.processorId = processorId;
		this.fileName = filaName;
		this.rollingType = rollingType;
		this.rollingUnit = rollingUnit;
		this.compRollingUnit = compRollingUnit;
		this.maxRollingUnit = maxRollingUnit;
	}
	

	@Override
	public void handleSystemAlert(SystemAlert alert) {

		if(alert.getSeverity().equalsIgnoreCase(AlertSeverity.CRITICAL.name())){
			alertFileLogger.error(alert.getAlertGeneratorIdentity(), alert.getDescription());
		}else if(alert.getSeverity().equalsIgnoreCase(AlertSeverity.ERROR.name())){
			alertFileLogger.error(alert.getAlertGeneratorIdentity(), alert.getDescription());
		}else if(alert.getSeverity().equalsIgnoreCase(AlertSeverity.WARN.name())){
			alertFileLogger.warn(alert.getAlertGeneratorIdentity(), alert.getDescription());
		}else if(alert.getSeverity().equalsIgnoreCase(AlertSeverity.INFO.name())){		
			alertFileLogger.info(alert.getAlertGeneratorIdentity(), alert.getDescription());
		}else{
			alertFileLogger.info(alert.getAlertGeneratorIdentity(), alert.getDescription());	
		}
	}
	

	public String getAlertProcessorType() {		
		return BaseAlertProcessor.FILE_ALERT_PROCESSOR_ID;
	}

	@Override
	public String getAlertProcessorId() {
		return processorId;
	}
	
	private String getRelativePath(){
		return getServerContext().getServerHome() + File.separator + LOGS_FOLDER + File.separator + DFLT_ALRT_FOLDER + File.separator;
	}
	
	@Override
	public void init() throws InitializationFailedException {
		try {
			File file = new File(this.fileName);
			if(file.isAbsolute()) {
				alertFileLogger = new AlertRollingFileLogger(getServerContext().getServerInstanceName(),this.fileName, rollingType, rollingUnit, maxRollingUnit, compRollingUnit);
			}else {
				alertFileLogger = new AlertRollingFileLogger(getServerContext().getServerInstanceName(),getRelativePath()+this.fileName,rollingType,rollingUnit,maxRollingUnit,compRollingUnit);
			}
			((AlertRollingFileLogger) alertFileLogger).setLogLevel(LogLevel.ALL.name());
		}catch(SecurityException | NullPointerException e) {
			throw new InitializationFailedException(e.getMessage(), e);
		}
	}
	
}
