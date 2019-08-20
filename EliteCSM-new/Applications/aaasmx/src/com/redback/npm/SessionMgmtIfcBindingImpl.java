/**
 * SessionMgmtIfcBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redback.npm;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.elitesm.ws.SessionManagerWS;
import com.elitecore.elitesm.ws.SubscriberProfileWS;
import com.elitecore.elitesm.ws.logger.Logger;

public class SessionMgmtIfcBindingImpl implements com.redback.npm.SessionMgmtIfc{
	private static final String MODULE = "Session-Mgmt-Ifc-Binding-Impl";
	private SessionManagerWS sessionManagerWS = new SessionManagerWS();
	private SubscriberProfileWS subscriberProfileWS = new SubscriberProfileWS();
	private Map<String,Object> subscriberAccountMap = new HashMap<String, Object>();
	
    public SubscriberSession[] getSubscriberSessions(java.lang.String string_1) throws java.rmi.RemoteException, com.redback.npm.InvalidSubscriberAccountException, com.redback.npm.SQLException {
    	Logger.logInfo(MODULE, "Enter into getSubscriberSessions Method :");
		Map<String,Map<String,String>> subscriberSessionMap = sessionManagerWS.findByUserName(string_1.trim());
		
		if(subscriberSessionMap == null || subscriberSessionMap.isEmpty()){
			throw new InvalidSubscriberAccountException("Unknown Subscriber:"+string_1.trim());
		}
		
		SubscriberSession subscriberSession = new SubscriberSession();
		
		SubscriberSession[] subscriberSessionsArray = new SubscriberSession[1];
		
		if(subscriberSession != null && !subscriberSessionMap.isEmpty()){
			for(String key : subscriberSessionMap.keySet()){
				Map<String,String> map = subscriberSessionMap.get(key);				
				if(map != null && !map.isEmpty()){											
					if(map.get("circuitType") != null){
						subscriberSession.setCircuitType(map.get("circuitType").trim());
					}else{
						subscriberSession.setCircuitType("");
					}
					
					if(map.get("nasId") != null){
						subscriberSession.setNasId(map.get("nasId").trim());
					}else{
						subscriberSession.setNasId("");
					}
					
					if(map.get("NASPortId") != null){
						subscriberSession.setNASPortId(map.get("NASPortId").trim());
					}else{
						subscriberSession.setNASPortId("");
					}
					
					if(map.get("NASPortType") != null){
						subscriberSession.setNASPortType(map.get("NASPortType").trim());
					}else{
						subscriberSession.setNASPortType("");
					}					
					
					if(map.get("acctSessionId") != null){
						subscriberSession.setAcctSessionId(map.get("acctSessionId").trim());
					}else{
						subscriberSession.setAcctSessionId("");
					}
					
					if(map.get("callingStationId") != null){
						subscriberSession.setCallingStationId(map.get("callingStationId").trim());
					}else{
						subscriberSession.setCallingStationId("");
					}
					
					if(map.get("context") != null){
						subscriberSession.setContext(map.get("context").trim());
					}else{
						subscriberSession.setContext("");
					}
					
					if(map.get("macAddress") != null){
						subscriberSession.setMacAddress(map.get("macAddress").trim());
					}else{
						subscriberSession.setMacAddress("");
					}					
					
					if(map.get("medium") != null){
						subscriberSession.setMedium(map.get("medium").trim());
					}else{
						subscriberSession.setMedium("");
					}
					
					if(map.get("sessionIp") != null){
						subscriberSession.setSessionIp(map.get("sessionIp").trim());
					}else{
						subscriberSession.setSessionIp("");
					}
					
					if(map.get("subscriberAccount") != null){
						subscriberSession.setSubscriberAccount(map.get("subscriberAccount").trim());
					}else{
						subscriberSession.setSubscriberAccount("");
					}
					
					if(map.get("nasType") != null){
						subscriberSession.setNasType(map.get("nasType").trim());
					}else{
						subscriberSession.setNasType("");
					}
					
					if(map.get("sessionId") != null){
						subscriberSession.setSessionId(map.get("sessionId").trim());
					}else{
						subscriberSession.setSessionId("");
					}

					if(map.get("startTime") != null){
						Calendar cal = Calendar.getInstance();						
						try {
							Timestamp timestamp = Timestamp.valueOf(map.get("startTime"));
							cal.setTime(timestamp);
						} catch (Exception e) {
							Logger.logError(MODULE, "error while parsing starttime : Reason "+e.getMessage());
							Logger.logTrace("MODULE",e);
						}
						subscriberSession.setStartTime(cal);
					}
					
				}
			}
		}
		subscriberSessionsArray[0] = subscriberSession;
		return subscriberSessionsArray;
    }

    public void setStaticIpAddresses(java.lang.String string_1, com.redback.npm.SessionIP[] arrayOfSessionIP_2) throws java.rmi.RemoteException, com.redback.npm.InvalidSubscriberAccountException, com.redback.npm.SQLException {
    	Logger.logInfo(MODULE, "Enter into setSubscriberSessions Method :");
		subscriberAccountMap.put("IPAddress",arrayOfSessionIP_2[0].getIPAddress().trim());
		int noOfUpdates = subscriberProfileWS.updateSubscriber(subscriberAccountMap,string_1.trim());
		if(noOfUpdates == 0){
			throw new InvalidSubscriberAccountException("Unknown Subscriber:"+string_1.trim());
		}
    }
}