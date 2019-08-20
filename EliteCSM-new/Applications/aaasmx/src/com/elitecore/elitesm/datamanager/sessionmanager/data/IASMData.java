/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   IASMData.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.sessionmanager.data;

import java.util.Date;
import java.util.Map;
   
public interface IASMData{

	public String getNameCount();
	public void setNameCount(String nameCount);

	public long getConcUserId();
	public void setConcUserId(long concUserId);

	public String getGroupbyCriteria();
	public void setGroupbyCriteria(String groupbyCriteria);

	public String getUserName();
	public void setUserName(String userName);

	public String getNasIpAddress();
	public void setNasIpAddress(String nasIpAddress);

	public String getFramedIpAddress();
	public void setFramedIpAddress(String framedIpAddress);

	public String getAcctSessionId();
	public void setAcctSessionId(String acctSessionId); 

	public String getNasPortType();
	public void setNasPortType(String nasPortType);

	public String getGroupName();
	public void setGroupName(String groupName);

	public String getUserIdentity();
	public void setUserIdentity(String userIdentity);

	public Date getLastUpdatedTime();
	public void setLastUpdatedTime(Date lastUpdatedTime);

	public String getIdleTime();
	public void setIdleTime(String idleTime);

	public String getStartTime();
	public void setStartTime(String startTime);
	
	public Map<String,Object>getMappingMap();
	public void setMappingMap(Map<String,Object> mappingMap);
	
	public String getProtocolType();
	public void setProtocolType(String protocolType);
    
}
