package com.elitecore.core.serverx.alert.listeners;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.util.logger.SysLogger;

import java.util.UUID;

public class SysLogAlertProcessor extends BaseAlertProcessor{

	private String processorUUID;

	private SysLogger logger;
	private String syslogHost;
	private String processorName;
	private String facility;

	public SysLogAlertProcessor(ServerContext serverContext,String processorUUID,String processorName,String syslogHost,String facility) {
		super(serverContext);
		this.processorUUID = processorUUID;
		this.syslogHost = syslogHost;
		this.processorName = processorName;
		this.facility = facility;
	}

	public String getProcessorName() {
		return processorName;
	}

	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}
	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}
	
	@Override
	public String getAlertProcessorId() {
		return this.processorUUID ;
	}

	public void setProcessorUUID(String processorUUID) {
		this.processorUUID = processorUUID;
	}
	
	@Override
	public String getAlertProcessorType() {
		return BaseAlertProcessor.SYSLOG_ALERT_PROCESSOR_ID;
	}

	@Override
	public void handleSystemAlert(SystemAlert alert) {
		
		if(alert.getSeverity().equalsIgnoreCase(AlertSeverity.CRITICAL.name())){
			logger.fatal(alert.getDescription());
		}else if(alert.getSeverity().equalsIgnoreCase(AlertSeverity.ERROR.name())){
			logger.error(alert.getDescription());
		}else if(alert.getSeverity().equalsIgnoreCase(AlertSeverity.WARN.name())){
			logger.warn(alert.getDescription());
		}else if(alert.getSeverity().equalsIgnoreCase(AlertSeverity.INFO.name())){		
			logger.info(alert.getDescription());
		}else{
			logger.info(alert.getDescription());	
		}
		
	}

	@Override
	public void init() throws InitializationFailedException {
		try {
			logger = new SysLogger(UUID.randomUUID().toString(), syslogHost, facility);

		}catch(Exception e) {
			throw new InitializationFailedException(e.getMessage(), e);
		}
		
	}

}
