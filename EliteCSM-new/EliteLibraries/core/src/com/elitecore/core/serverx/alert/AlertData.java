package com.elitecore.core.serverx.alert;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.serverx.alert.listeners.SystemAlertProcessor;


public class AlertData implements IAlertData {
	private String alertId;
	private List<SystemAlertProcessor> alertProcessorList; // List of Alert Processors  (File, Trap etc)
	private AlertStatistics statistics;
	
	public AlertData() {		
		alertProcessorList = new ArrayList<SystemAlertProcessor>();
		statistics = new AlertStatistics();
	}
	
	public void setAlertId(String alertId) {
		this.alertId = alertId;
	}
	
	@Override
	public String getAlertId() {
		return this.alertId;
	}

	@Override
	public List<SystemAlertProcessor> getAlertProcessorsList() {		
		return alertProcessorList;
	}
	
	public void addAlertListener(SystemAlertProcessor systemAlertListener) {
		if(systemAlertListener != null)
			this.alertProcessorList.add(systemAlertListener);
	}

	@Override
	public AlertStatistics getStatistics() {
		return statistics;
	}

	@Override
	public void handleSystemAlert(SystemAlert alert) {
		
		for (SystemAlertProcessor alertProcessor : alertProcessorList) {
			alertProcessor.handleSystemAlert(alert);
		}
		statistics.incrementCounters();
	}
}
