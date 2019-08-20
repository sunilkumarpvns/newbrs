package com.elitecore.elitesm.datamanager.dashboard.data.chartdata;

import java.io.Serializable;
import java.sql.Timestamp;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class TotalRequestStatistics extends BaseData implements Serializable {

	private static final long serialVersionUID = 1L;
	private Timestamp timestamp;
	private Long epochTime;
	private String esi;
	private Integer accessChallenge;
	private Integer accessAccept;
	private Integer accessReject;
	private Integer requestDrop;
	

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		
		this.timestamp = timestamp;
		if(timestamp != null)
		 setEpochTime(timestamp.getTime());
		
	}


	public String getEsi() {
		return esi;
	}

	public void setEsi(String esi) {
		this.esi = esi;
	}

	public Integer getAccessChallenge() {
		return accessChallenge;
	}


	public void setAccessChallenge(Integer accessChallenge) {
		this.accessChallenge = accessChallenge;
	}


	public Integer getAccessAccept() {
		return accessAccept;
	}


	public void setAccessAccept(Integer accessAccept) {
		this.accessAccept = accessAccept;
	}

	public Integer getAccessReject() {
		return accessReject;
	}

	public void setAccessReject(Integer accessReject) {
		this.accessReject = accessReject;
	}


	public Integer getRequestDrop() {
		return requestDrop;
	}


	public void setRequestDrop(Integer requestDrop) {
		this.requestDrop = requestDrop;
	}

	public Long getEpochTime() {
		return epochTime;
	}

	public void setEpochTime(Long epochTime) {
		this.epochTime = epochTime;
	}
	

}
