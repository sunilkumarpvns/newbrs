/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ASMData.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.sessionmanager.data;
import java.util.Date;
import java.util.Map;
   
public class ASMData implements IASMData{

    private long concUserId;
    private String userName;
    private String nasIpAddress;
    private String framedIpAddress;
    private String acctSessionId;
    private String nasPortType;
    private String groupName;
    private String userIdentity;
    private Date lastUpdatedTime;
    private String groupbyCriteria;
    private String nameCount;
    private String idleTime;
    private Map<String,Object>mappingMap;    
    private String startTime;
    private String protocolType;


	public String getNameCount() {
		return nameCount;
	}

	public void setNameCount(String nameCount) {
		this.nameCount = nameCount;
	}

	public long getConcUserId(){
        return concUserId;
    }

	public void setConcUserId(long concUserId) {
		this.concUserId = concUserId;
	}

	public String getGroupbyCriteria() {
		return groupbyCriteria;
	}

	public void setGroupbyCriteria(String groupbyCriteria) {
		this.groupbyCriteria = groupbyCriteria;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNasIpAddress() {
		return nasIpAddress;
	}

	public void setNasIpAddress(String nasIpAddress) {
		this.nasIpAddress = nasIpAddress;
	}

	public String getFramedIpAddress() {
		return framedIpAddress;
	}

	public void setFramedIpAddress(String framedIpAddress) {
		this.framedIpAddress = framedIpAddress;
	}

	public String getAcctSessionId() {
		return acctSessionId;
	}

	public void setAcctSessionId(String acctSessionId) {
		this.acctSessionId = acctSessionId;
	}

	public String getNasPortType() {
		return nasPortType;
	}

	public void setNasPortType(String nasPortType) {
		this.nasPortType = nasPortType;
	}

	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	public Date getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setLastUpdatedTime(Date lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	public String getIdleTime() {
		return idleTime;
	}

	public void setIdleTime(String idleTime) {
		this.idleTime = idleTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public Map<String, Object> getMappingMap() {
		return this.mappingMap;
	}

	public void setMappingMap(Map<String, Object> mappingMap) {
		this.mappingMap = mappingMap;
	}

	public String getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}
}
