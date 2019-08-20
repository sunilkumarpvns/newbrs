package com.elitecore.corenetvertex.core.alerts;



public interface AlertListener {

	void generateSystemAlert(String severity, Alerts alertEnum, String alertGeneratorIdentity, String alertMessage);
	void generateSystemAlert(String severity, Alerts alertEnum, String alertGeneratorIdentity, String alertMessage, int alertIntValue, String alertStringValue);
}
