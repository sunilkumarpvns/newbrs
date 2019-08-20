/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   IASMData.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.reports.userstat.data;

import java.util.Date;
   
public interface IUserStatisticsData{

    public long getUserStatisticsId();
	public void setUserStatisticsId(long userStatisticsId);

    public String getUserName();
	public void setUserName(String userName);

    public String getNasIpAddress();
	public void setNasIpAddress(String nasIpAddress);

    public String getFramedIpAddress();
	public void setFramedIpAddress(String framedIpAddress);


    public String getParamStr0();
	public void setParamStr0(String paramStr0);

    public String getParamStr1();
	public void setParamStr1(String paramStr1);

    public String getParamStr2();
	public void setParamStr2(String paramStr2);

    public String getGroupName();
	public void setGroupName(String groupName);

    public String getUserIdentity();
    public void setUserIdentity(String userIdentity);
    
	public Date getCreateDate();
	public void setCreateDate(Date createDate);

	public String getStrCreateDate();
	
	public String getCallingStationId();

	public void setCallingStationId(String callingStationId);

	public String getReplyMessage();

	public void setReplyMessage(String replyMessage);
}
