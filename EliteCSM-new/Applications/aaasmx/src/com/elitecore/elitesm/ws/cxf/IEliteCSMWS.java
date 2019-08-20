package com.elitecore.elitesm.ws.cxf;

import java.sql.SQLException;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.elitecore.elitesm.ws.core.data.KeyValueList;
import com.elitecore.elitesm.ws.exception.DatabaseConnectionException;
import com.elitecore.elitesm.ws.exception.SubscriberProfileWebServiceException;

@WebService(name="EliteCSMWS")
public interface IEliteCSMWS{
	
	@WebMethod(operationName="findSubscriberByUserIdentity")
	public List<KeyValueList> findSubscriberByUserIdentity(@WebParam(name="userIdentity") String userIdentity) throws SubscriberProfileWebServiceException;
	
	@WebMethod(operationName="delSubscriber")
	public int delSubscriber(@WebParam(name="userIdentity") String userIdentity) throws SQLException, SubscriberProfileWebServiceException,DatabaseConnectionException;
	
	@WebMethod(operationName="findSessionByUserName")
	public List<KeyValueList> findSessionByUserName(@WebParam(name="userName") String userName);
	
	@WebMethod(operationName="findSessionByFramedIPAddress")
	public List<KeyValueList> findSessionByFramedIPAddress(@WebParam(name="ipAddress") String ipAddress);
	
	@WebMethod(operationName="findSessionByServiceType")
	public List<KeyValueList> findSessionByServiceType(@WebParam(name="serviceType") String serviceType);
	
	@WebMethod(operationName="findSessionByAttribute")
	public List<KeyValueList> findSessionByAttribute(@WebParam(name="attribute") String attribute,@WebParam(name="value") String value);

	@WebMethod(operationName="addSubscriber")
	public int addSubscriber(@WebParam(name="subscriberProfileData") List<KeyValueList> addSubscriberProfileData);

	@WebMethod(operationName="updateSubscriber")
	public int updateSubscriber(@WebParam(name="subscriberProfileData") KeyValueList updateSubscriberProfileData,@WebParam(name="userIdentity")String userIdentity);
	
//	@WebMethod(operationName="version")
//	public String getVersion();
}