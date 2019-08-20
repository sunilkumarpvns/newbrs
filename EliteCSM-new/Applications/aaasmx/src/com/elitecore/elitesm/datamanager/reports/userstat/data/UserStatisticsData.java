/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ASMData.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.reports.userstat.data;
import java.text.SimpleDateFormat;
import java.util.Date;
   
public class UserStatisticsData implements IUserStatisticsData{

    private long userStatisticsId;
    private String userName;
    private String nasIpAddress;
    private String framedIpAddress;
    private String paramStr0;
    private String paramStr1;
    private String paramStr2;
    private String callingStationId;
    private String replyMessage;
    private String groupName;
    private String userIdentity;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy, HH:mm:ss,SSS");
    private Date createDate;



	public long getUserStatisticsId() {
		return userStatisticsId;
	}

	public void setUserStatisticsId(long userStatisticsId) {
		this.userStatisticsId = userStatisticsId;
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getStrCreateDate(){
		String strDate = "";
		if(this.createDate!=null){
			strDate =  formatter.format(this.createDate);
		}
		return strDate;
	}

	public String getCallingStationId() {
		return callingStationId;
	}

	public void setCallingStationId(String callingStationId) {
		this.callingStationId = callingStationId;
	}

	public String getReplyMessage() {
		return replyMessage;
	}

	public void setReplyMessage(String replyMessage) {
		this.replyMessage = replyMessage;
	}
	
}
