package com.elitecore.elitesm.ws.cxf;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.ws.core.data.KeyValueList;
import com.elitecore.elitesm.ws.core.data.KeyValuePair;
import com.elitecore.elitesm.ws.exception.DatabaseConnectionException;
import com.elitecore.elitesm.ws.exception.EmptyAttributeFieldMappingException;
import com.elitecore.elitesm.ws.exception.SessionMgmtWebServiceException;
import com.elitecore.elitesm.ws.exception.SubscriberProfileWebServiceException;
import com.elitecore.elitesm.ws.logger.Logger;
import com.elitecore.elitesm.ws.sessionmgmt.SessionMgmtWebServiceBLManager;
import com.elitecore.elitesm.ws.subscriber.SubscriberProfileWebServiceBLManager;

public class EliteCSMWS implements IEliteCSMWS{
	private static final String MODULE = "ELITE-CSM-WS";
	private SessionMgmtWebServiceBLManager sessMgmtBLManager = new SessionMgmtWebServiceBLManager();
	private SubscriberProfileWebServiceBLManager subProfileBLManager = new SubscriberProfileWebServiceBLManager();
	
	@Override
	public List<KeyValueList> findSessionByUserName(String userName) {
		Logger.logInfo(MODULE, "Processing findByUserName operation, UserName: " + userName);
		Map<String,Map<String,String>> resultData;
		List<KeyValueList> keyValueLists = null;
		try {
			resultData = sessMgmtBLManager.findByUserName(userName);
			keyValueLists = getResultListFromMap(resultData);
		} catch (SQLException e) {
			Logger.logInfo(MODULE, "Error while processing findByUserName operation,Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (DatabaseConnectionException e) {
			Logger.logInfo(MODULE, "Error while processing findByUserName operation,Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		}		
		return keyValueLists;
	}

	@Override
	public List<KeyValueList> findSessionByFramedIPAddress(String ipAddress) {
		Logger.logInfo(MODULE, "Processing findByFramedIPAddress operation, IP Address: "+ipAddress);
		Map<String,Map<String,String>> resultData;
		List<KeyValueList> keyValueLists = null;
		try {
			resultData = sessMgmtBLManager.findByFramedIPAddress(ipAddress);
			keyValueLists = getResultListFromMap(resultData);
		} catch (SQLException e) {
			Logger.logInfo(MODULE, "Error while processing findByFramedIPAddress operation,Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (DatabaseConnectionException e) {
			Logger.logInfo(MODULE, "Error while processing findByFramedIPAddress operation,Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		}		
		return keyValueLists;
	}

	@Override
	public List<KeyValueList> findSessionByServiceType(String serviceType) {
		Logger.logInfo(MODULE, "Processing findByServiceType operation, Service Type: " + serviceType);
		Map<String,Map<String,String>> resultData;
		List<KeyValueList> keyValueLists = null;
		try {
			resultData = sessMgmtBLManager.findByServiceType(serviceType);
			keyValueLists = getResultListFromMap(resultData);
		} catch (SQLException e) {
			Logger.logInfo(MODULE, "Error while processing findByServiceType operation,Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (DatabaseConnectionException e) {
			Logger.logInfo(MODULE, "Error while processing findByServiceType operation,Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		}		
		return keyValueLists;
	}

	@Override
	public List<KeyValueList> findSessionByAttribute(String attribute, String value) {
		Logger.logInfo(MODULE, "Processing findByAttribute operation, Attribute: " + attribute + ", value: " + value);
		Map<String,Map<String,String>> resultData;
		List<KeyValueList> keyValueLists = null;
		
		try {
			resultData = sessMgmtBLManager.findByAttribute(attribute, value);
			keyValueLists = getResultListFromMap(resultData);
		} catch (SQLException e) {
			Logger.logInfo(MODULE, "Error while processing findByAttribute operation,Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (DatabaseConnectionException e) {
			Logger.logInfo(MODULE, "Error while processing findByAttribute operation,Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (EmptyAttributeFieldMappingException e) {
			Logger.logInfo(MODULE, "Error while processing findByAttribute operation,Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (SessionMgmtWebServiceException e) {
			Logger.logInfo(MODULE, "Error while processing findByAttribute operation,Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch(Exception e) {
			Logger.logInfo(MODULE, "Error while processing findByAttribute operation,Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return keyValueLists;
	}
	
	@Override
	public List<KeyValueList> findSubscriberByUserIdentity(String userIdentity) throws SubscriberProfileWebServiceException {
		Logger.logInfo(MODULE, "Processing findByUserIdentity operation of subscriber profile webservice.");
		Logger.logInfo(MODULE, "UserIdentity: "+ userIdentity);
		Map<String,Map<String,String>> resultData;
		List<KeyValueList> keyValueLists = null;
		try {
			resultData = subProfileBLManager.findByUserIdentity(userIdentity);
			keyValueLists = getResultListFromMap(resultData);	
		} catch (SQLException e) {
			Logger.logError(MODULE, "Error while processing findByUserIdentity operation, Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (DatabaseConnectionException e) {
			Logger.logError(MODULE, "Error while processing findByUserIdentity operation, Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (Exception e) {
			Logger.logError(MODULE, "Error while processing findByUserIdentity operation, Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return keyValueLists;
	}
	
	public int delSubscriber(String userIdentity) throws SQLException, SubscriberProfileWebServiceException,DatabaseConnectionException{
		Logger.logInfo(MODULE, "Processing delete Subscriber Operation of subscriber profile webservice.");
		Logger.logInfo(MODULE, "UserIdentity: "+ userIdentity);
		int result = subProfileBLManager.delSubscriber(userIdentity);
		Logger.logInfo(MODULE, "No of records to be deleted: " + result);
		return result;
	}
	
	private List<KeyValueList> getResultListFromMap(Map<String,Map<String,String>> resultMap){
		List<KeyValueList> keyValueLists = null;
		if(resultMap != null && !resultMap.isEmpty()){
			keyValueLists = new ArrayList<KeyValueList>();
			for(Map<String, String> map : resultMap.values()){
				List<KeyValuePair> keyValuePairList = new ArrayList<KeyValuePair>();
				for(Map.Entry<String,String> entry : map.entrySet()){
					KeyValuePair keyValuePair = new KeyValuePair();
					keyValuePair.setKey(entry.getKey());
					keyValuePair.setValue(entry.getValue());
					keyValuePairList.add(keyValuePair);
				}
				KeyValueList keyValueList = new KeyValueList();
				keyValueList.setKeyValuePairList(keyValuePairList);
				keyValueLists.add(keyValueList);
			}
		}
		return keyValueLists;
	}

	@Override
	public int addSubscriber(List<KeyValueList> addSubscriberProfileData) {
		int addCount = 0;
		try {
			List<Map<String, Object>> subscribersListToAdd = convertToMapFromListKeyValueList(addSubscriberProfileData);
			for (Map<String, Object> subscriberToAdd : subscribersListToAdd) {
				addCount += subProfileBLManager.addSubscriber(subscriberToAdd);
			}
			if(addCount > 0){
				Logger.logInfo(MODULE, "Number of subscriber: "+addCount+" added successfully.");
			}
		} catch (SubscriberProfileWebServiceException e) {
			Logger.logError(MODULE, "Error while adding subcriber profile, Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (SQLException e) {
			Logger.logError(MODULE, "Error while adding subcriber profile, Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (DatabaseConnectionException e) {
			Logger.logError(MODULE, "Error while adding subcriber profile, Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return addCount;
	}

	/**
	 * Used for Add Subscriber Operation.
	 * @param subscriberProfileData
	 */
	private List<Map<String, Object>> convertToMapFromListKeyValueList(List<KeyValueList> subscriberProfileData) {
		List<Map<String, Object>> subscribersDataList = new ArrayList<Map<String,Object>>();
		for (KeyValueList keyValueList : subscriberProfileData) {
			List<KeyValuePair> keyValuePairList = keyValueList.getKeyValuePairList();
			Map<String, Object> subscriberProfileDataMap = new HashMap<String, Object>();
			for (KeyValuePair keyValuePair : keyValuePairList) {
				subscriberProfileDataMap.put(keyValuePair.getKey(), keyValuePair.getValue());
			}
			subscribersDataList.add(subscriberProfileDataMap);
		}
		return subscribersDataList;
	}

	@Override
	public int updateSubscriber(KeyValueList updateSubscriberProfileData,String userIdentity) {
		int updateCount = 0;
		try {
			Map<String, Object> subScriberToUpdate = convertToMapFromKeyValueList(updateSubscriberProfileData);
			updateCount = subProfileBLManager.updateSubscriber(subScriberToUpdate, userIdentity);
			if(updateCount > 0){
				Logger.logInfo(MODULE, "Subscriber profile updated successfully for userIdentity: "+ userIdentity);
			}
		} catch (SubscriberProfileWebServiceException e) {
			Logger.logError(MODULE, "Error while updating subcriber profile for user: "+userIdentity+", Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (SQLException e) {
			Logger.logError(MODULE, "Error while updating subcriber profile for user: "+userIdentity+", Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (DatabaseConnectionException e) {
			Logger.logError(MODULE, "Error while updating subcriber profile for user: "+userIdentity+", Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return updateCount;
	}
	
	/**
	 * Used for Update Subscriber Operation.
	 * @param subscriberProfileData
	 */
	private Map<String, Object> convertToMapFromKeyValueList(KeyValueList subscriberProfileData){
		Map<String, Object> subScriberData = new HashMap<String, Object>();
		List<KeyValuePair> keyValuePairList = subscriberProfileData.getKeyValuePairList();
		for (KeyValuePair keyValuePair : keyValuePairList) {
			subScriberData.put(keyValuePair.getKey(), keyValuePair.getValue());
		}
		return subScriberData;
	}

	/**
	 * returns the current version of 
	 * CXF-WS Version.
	 */
//	@Override
//	public String getVersion() {
//		return org.apache.cxf.version.Version.getCompleteVersionString();
//	}
}