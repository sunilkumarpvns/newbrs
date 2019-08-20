package com.elitecore.core.serverx.alert.listeners;

import com.elitecore.core.serverx.ServerContext;

public abstract class BaseAlertProcessor implements SystemAlertProcessor {
	
	public static final String FILE_ALERT_PROCESSOR_ID = "ALT0001";
	public static final String TRAP_ALERT_PROCESSOR_ID = "ALT0002";
	public static final String SYSLOG_ALERT_PROCESSOR_ID = "ALT0003";
	
	protected ServerContext serverContext;

	public BaseAlertProcessor(ServerContext serverContext) {
		this.serverContext = serverContext;
	}
	
	public ServerContext getServerContext() {
		return this.serverContext;
	}
}
