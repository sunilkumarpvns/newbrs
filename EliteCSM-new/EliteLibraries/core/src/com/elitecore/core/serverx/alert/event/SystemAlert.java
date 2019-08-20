package com.elitecore.core.serverx.alert.event;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.elitecore.core.serverx.alert.Alerts;
import com.elitecore.core.serverx.alert.IAlertEnum;

public class SystemAlert {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	
	private String alertGeneratorIdentity;
	private String description;
	private String severity;
	private int alertIntValue;
	private String alertStringValue;
	private IAlertEnum alert;
	private Date alertGenerationTime;
	private Map<com.elitecore.core.serverx.alert.Alerts,Object> alertDataMap; 
	
	public SystemAlert(IAlertEnum alert, String alertGeneratorIdentity, String alertSeverity,String alertMessage) {
		this.alert = checkNotNull(alert, "alert is null");
		this.alertGeneratorIdentity = alertGeneratorIdentity;
		this.alertGenerationTime = new Date();
		this.description = alertMessage;
		this.severity = alertSeverity;
	}
	
	public SystemAlert(IAlertEnum alert, String alertGeneratorIdentity, String alertSeverity,String alertMessage, int alertIntValue, String alertStringValue) {
		this.alert = checkNotNull(alert, "alert is null");
		this.alertGeneratorIdentity = alertGeneratorIdentity;
		this.alertGenerationTime = new Date();
		this.description = alertMessage;
		this.severity = alertSeverity;
		this.alertIntValue = alertIntValue;
		this.alertStringValue = alertStringValue;
	}

	public SystemAlert(IAlertEnum alert, String alertGeneratorIdentity, String alertSeverity,String alertMessage,
			Map<com.elitecore.core.serverx.alert.Alerts,Object> alertData) {
		this.alert = checkNotNull(alert, "alert is null");
		this.alertGeneratorIdentity = alertGeneratorIdentity;
		this.alertGenerationTime = new Date();
		this.description = alertMessage;
		this.severity = alertSeverity;
		this.alertDataMap = alertData;
	}

	public Map<Alerts, Object> getAlertDataMap() {
		return this.alertDataMap;
	}
	
	public String getAlertGeneratorIdentity() {
		return alertGeneratorIdentity;
	}

	public String getDescription() {
		return sdf.format(alertGenerationTime) + ": ["+ alert.name() + "] :" + description;
	}

	public Date getAlertGenerationTime() {
		return alertGenerationTime;
	}
	
	public String getSeverity() {
		return severity;
	}
	
	public int getAlertIntValue() {
		return alertIntValue;
	}
	
	public String getAlertStringValue() {
		return alertStringValue;
	}
	
	public IAlertEnum getAlert() {
		return alert;
	}
}
