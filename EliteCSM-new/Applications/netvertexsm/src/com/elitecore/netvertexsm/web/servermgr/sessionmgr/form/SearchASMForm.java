/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SearchASMForm.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.netvertexsm.web.servermgr.sessionmgr.form;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class SearchASMForm extends BaseWebForm{

    private long id;
    private Long sessionManagerId;
    
 
    private String framedIpAddress;

    private String paramStr0;
    private String paramStr1;
    private String paramStr2;
    private String paramStr3;
    private String paramStr4;
    private String paramStr5;

    
    private Date lastUpdatedTime;
    
    private long pageNumber;
	private int multiFactor;
	private long totalPages;
	private long totalRecords;
	
	private List asmList;
	private List asmListGroupBy;
	
	private String groupbyCriteria;
	private String nameCount;
	
	private String action;
	private String status;
	
	private String idleTime;
	private String startTime;
	
	private String subscriptionId;
	private String gxSessionId;
    private String location;
    private String gatewayURL;
    private String userEquipment;
    
	private Map<String,Object>mappingMap;
   
	
    public Long getSessionManagerId() {
		return sessionManagerId;
	}

	public void setSessionManagerId(Long sessionManagerId) {
		this.sessionManagerId = sessionManagerId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getGroupbyCriteria() {
		return groupbyCriteria;
	}

	public void setGroupbyCriteria(String groupbyCriteria) {
		this.groupbyCriteria = groupbyCriteria;
	}

	public List getAsmListGroupBy() {
		return asmListGroupBy;
	}

	public void setAsmListGroupBy(List asmListGroupBy) {
		this.asmListGroupBy = asmListGroupBy;
	}

	public int getMultiFactor() {
		return multiFactor;
	}

	public void setMultiFactor(int multiFactor) {
		this.multiFactor = multiFactor;
	}

	public long getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}

	public long getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}

	public long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}

	public List getAsmList() {
		return asmList;
	}

	public void setAsmList(List asmList) {
		this.asmList = asmList;
	}

	public String getNameCount() {
		return nameCount;
	}

	public void setNameCount(String nameCount) {
		this.nameCount = nameCount;
	}

	public String getFramedIpAddress() {
		return framedIpAddress;
	}

	public void setFramedIpAddress(String framedIpAddress) {
		this.framedIpAddress = framedIpAddress;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public String getGxSessionId() {
		return gxSessionId;
	}

	public void setGxSessionId(String gxSessionId) {
		this.gxSessionId = gxSessionId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getGatewayURL() {
		return gatewayURL;
	}

	public void setGatewayURL(String gatewayURL) {
		this.gatewayURL = gatewayURL;
	}

	public String getUserEquipment() {
		return userEquipment;
	}

	public void setUserEquipment(String userEquipment) {
		this.userEquipment = userEquipment;
	}

	public String getParamStr0() {
		return paramStr0;
	}

	public void setParamStr0(String paramStr0) {
		this.paramStr0 = paramStr0;
	}

	public String getParamStr1() {
		return paramStr1;
	}

	public void setParamStr1(String paramStr1) {
		this.paramStr1 = paramStr1;
	}

	public String getParamStr2() {
		return paramStr2;
	}

	public void setParamStr2(String paramStr2) {
		this.paramStr2 = paramStr2;
	}

	public String getParamStr3() {
		return paramStr3;
	}

	public void setParamStr3(String paramStr3) {
		this.paramStr3 = paramStr3;
	}

	public String getParamStr4() {
		return paramStr4;
	}

	public void setParamStr4(String paramStr4) {
		this.paramStr4 = paramStr4;
	}

	public String getParamStr5() {
		return paramStr5;
	}

	public void setParamStr5(String paramStr5) {
		this.paramStr5 = paramStr5;
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
		return mappingMap;
	}

	public void setMappingMap(Map<String, Object> mappingMap) {
		this.mappingMap = mappingMap;
	}
	
	//"asmList.get("+ iIndex+1 +").getMappingMap()" %>

}
