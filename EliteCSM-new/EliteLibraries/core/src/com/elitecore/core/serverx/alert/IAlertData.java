package com.elitecore.core.serverx.alert;

import java.util.List;

import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.serverx.alert.listeners.SystemAlertProcessor;

public interface IAlertData {
	
	public String getAlertId();
	public List<SystemAlertProcessor> getAlertProcessorsList();
	public AlertStatistics getStatistics();
	public void handleSystemAlert(SystemAlert alert);
}
