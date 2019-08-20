package com.elitecore.elitesm.datamanager.sessionmanager.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class SessionManagerESIServerData {
	
	List<SMSessionCloserESIRelData> nasServerDataList = new ArrayList<SMSessionCloserESIRelData>();
	List<SMSessionCloserESIRelData> accountServerDataList = new ArrayList<SMSessionCloserESIRelData>();
	
	@XmlElement(name = "nas-server")
	public List<SMSessionCloserESIRelData> getNasServerDataList() {
		return nasServerDataList;
	}
	public void setNasServerDataList(
			List<SMSessionCloserESIRelData> nasServerDataList) {
		this.nasServerDataList = nasServerDataList;
	}
	
	@XmlElement(name = "rad-acct-server")
	public List<SMSessionCloserESIRelData> getAccountServerDataList() {
		return accountServerDataList;
	}
	public void setAccountServerDataList(
			List<SMSessionCloserESIRelData> accountServerDataList) {
		this.accountServerDataList = accountServerDataList;
	}
}
