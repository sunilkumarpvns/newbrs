package com.elitecore.core.util.mbean.data.live;

import java.io.Serializable;
import java.util.Date;

public class EliteNetRemoteSystemData implements Serializable {
	
	private String identifier;
	private String status;
	private Date statusChangeDate;
	private Date lastStatusCheckDate;
	
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public Date getLastStatusCheckDate() {
		return lastStatusCheckDate;
	}
	public void setLastStatusCheckDate(Date lastStatusCheckDate) {
		this.lastStatusCheckDate = lastStatusCheckDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getStatusChangeDate() {
		return statusChangeDate;
	}
	public void setStatusChangeDate(Date statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}	
	
	
}
