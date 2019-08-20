package com.elitecore.aaa.core.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
@XmlType(propOrder = {})
public class RRDDetail {
	
	private boolean rrdSummaryEnabled;
	private boolean rrdErrorsEnabled;
	private boolean rrdRejectReasonsEnabled;
	private boolean rrdResponseTimeEnabled;

	public RRDDetail(){
		//required by Jaxb.
	}
	@XmlElement(name = "response-time",type = boolean.class)
	public boolean getIsRrdResponseTimeEnabled() {
		return rrdResponseTimeEnabled;
	}
	public void setIsRrdResponseTimeEnabled(boolean rrdResponseTimeEnabled) {
		this.rrdResponseTimeEnabled = rrdResponseTimeEnabled;
	}
	@XmlElement(name = "summary",type = boolean.class)
	public boolean getIsRrdSummaryEnabled() {
		return rrdSummaryEnabled;
	}
	public void setIsRrdSummaryEnabled(boolean rrdSummaryEnabled) {
		this.rrdSummaryEnabled = rrdSummaryEnabled;
	}
	@XmlElement(name = "errors",type = boolean.class)
	public boolean getIsRrdErrorsEnabled() {
		return rrdErrorsEnabled;
	}
	public void setIsRrdErrorsEnabled(boolean rrdErrorsEnabled) {
		this.rrdErrorsEnabled = rrdErrorsEnabled;
	}
	@XmlElement(name = "reject-with-reasons",type =boolean.class)
	public boolean getIsRrdRejectReasonsEnabled() {
		return rrdRejectReasonsEnabled;
	}
	public void setIsRrdRejectReasonsEnabled(boolean rrdRejectReasonsEnabled) {
		this.rrdRejectReasonsEnabled = rrdRejectReasonsEnabled;
	}



}
