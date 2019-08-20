package com.elitecore.aaa.core.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class AlertListnersDetail {

	private List<String> alertListnersList;;
	
	public AlertListnersDetail(){
		alertListnersList = new ArrayList<String>();
	}
	
	@XmlElement(name = "alert-listener",type = String.class)
	public List<String> getAlertListnersList() {
		return alertListnersList;
	}

	public void setAlertListnersList(List<String> alertListnersList) {
		this.alertListnersList = alertListnersList;
	} 
}
