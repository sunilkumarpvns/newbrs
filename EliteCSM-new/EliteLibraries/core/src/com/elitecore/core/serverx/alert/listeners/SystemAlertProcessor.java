package com.elitecore.core.serverx.alert.listeners;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.alert.event.SystemAlert;

public interface SystemAlertProcessor {
	public void handleSystemAlert(SystemAlert alert);
	public String getAlertProcessorType();
	public String getAlertProcessorId();
	public void init() throws InitializationFailedException;
}
