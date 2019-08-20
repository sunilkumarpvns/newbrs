package com.elitecore.netvertex.gateway.diameter.utility.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="result-code")
public class ResultCodeEntryData {
	
	private String pcrfKey;
	private Integer resultCode;
	private List<ApplicationData> applicationData;
	
	public ResultCodeEntryData() { 
		this.applicationData = new ArrayList<ApplicationData>();
	}

	@XmlElement(name="pcrf-key")
	public String getPcrfKey() {
		return pcrfKey;
	}

	public void setPcrfKey(String pcrfKey) {
		this.pcrfKey = pcrfKey;
	}

	@XmlElement(name = "diameter-result-code")
	public Integer getResultCode() {
		return resultCode;
	}

	public void setResultCode(Integer resultCode) {
		this.resultCode = resultCode;
	}

	@XmlElement(name="application")
	public List<ApplicationData> getApplicationData() {
		return applicationData;
	}

	public void setApplicationData(List<ApplicationData> applicationData) {
		this.applicationData = applicationData;
	}
}
