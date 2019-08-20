package com.elitecore.nvsmx.ws.subscriberprovisioning;

import java.util.List;
import java.util.Map;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfile;
import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfileData;
import com.elitecore.nvsmx.ws.subscriberprovisioning.response.SubscriberProvisioningResponse;
import com.elitecore.nvsmx.ws.util.StringToIntegerAdapterForReauth;

@WebService(name="SubscriberProvisioningWS")
public interface ISubscriberProvisioningWS {
	
	public static final String WS_ADD_SUBSCRIBERS 		= "wsAddSubscribers";
	public static final String WS_ADD_SUBSCRIBER 		= "wsAddSubscriber";
	public static final String WS_MIGRATE_SUBSCRIBER 	= "wsMigrateSubscriber";
	public static final String WS_RESTORE_SUBSCRIBERS 	= "wsRestoreSubscribers";
	public static final String WS_RESTORE_SUBSCRIBER 	= "wsRestoreSubscriber";
	public static final String WS_PURGE_ALL_SUBSCRIBER 	= "wsPurgeAllSubscriber";
	public static final String WS_PURGE_SUBSCRIBERS 	= "wsPurgeSubscribers";
	public static final String WS_PURGE_SUBSCRIBER 		= "wsPurgeSubscriber";
	public static final String WS_LIST_SUBSCRIBER_PROFILES 		= "wsListSubscriberProfiles";
	public static final String WS_GET_SUBSCRIBER_PROFILE_BY_ID 	= "wsGetSubscriberProfileByID";
	public static final String WS_ADD_SUBSCRIBER_BULK 			= "wsAddSubscriberBulk";
	public static final String WS_DELETE_SUBSCRIBER_PROFILES 	= "wsDeleteSubscriberProfiles";
	public static final String WS_DELETE_SUBSCRIBER_PROFILE 	= "wsDeleteSubscriberProfile";
	public static final String WS_UPDATE_SUBSCRIBER_PROFILE 	= "wsUpdateSubscriberProfile";
	public static final String WS_ADD_SUBSCRIBER_PROFILE 		= "wsAddSubscriberProfile";
	public static final String WS_CHANGE_IMS_PACKAGE = "wsChangeImsPackage";
	public static final String WS_CHANGE_BASE_PRODUCT_OFFER = "wsChangeBaseProductOffer";

	@WebMethod( operationName = WS_ADD_SUBSCRIBER_PROFILE )
	public SubscriberProvisioningResponse wsAddSubscriberProfile(
			@WebParam(name="subscriberProfile")SubscriberProfile subscriberProfile,	 
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);
	
	@WebMethod( operationName = WS_UPDATE_SUBSCRIBER_PROFILE )
	public SubscriberProvisioningResponse wsUpdateSubscriberProfile(
			@WebParam(name="subscriberProfile")SubscriberProfile subscriberProfile,
			@WebParam(name="subscriberID")String subscriberID,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="updateAction")@XmlJavaTypeAdapter(StringToIntegerAdapterForReauth.class)Integer updateAction,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod( operationName = WS_DELETE_SUBSCRIBER_PROFILE )
	public SubscriberProvisioningResponse wsDeleteSubscriberProfile(
			@WebParam(name="subscriberID")String subscriberID,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);
	
	@WebMethod( operationName = WS_DELETE_SUBSCRIBER_PROFILES )
	public SubscriberProvisioningResponse wsDeleteSubscriberProfiles(
			@WebParam(name="subscriberIDs")List<String> subscriberIDs,
			@WebParam(name="alternateIds")List<String> alternateIds,
			@WebParam(name="param1")String param1,
			@WebParam(name="param2")String param2);

	@WebMethod( operationName = WS_ADD_SUBSCRIBER_BULK )
	public SubscriberProvisioningResponse wsAddSubscriberBulk(
			@WebParam(name="subscriberProfile")List<SubscriberProfile> subscriberProfile,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod( operationName = WS_GET_SUBSCRIBER_PROFILE_BY_ID)
	public SubscriberProvisioningResponse wsGetSubscriberProfileByID(
			@WebParam(name="subscriberID")String subscriberID,
			@WebParam(name="alternateID")String alternateID,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod( operationName = WS_LIST_SUBSCRIBER_PROFILES )
	public SubscriberProvisioningResponse wsListSubscriberProfiles(
			@WebParam(name="subscriberProfileCriteria")Map<String,String> subscriberProfileCriteria,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);
	
   @WebMethod( operationName = WS_PURGE_SUBSCRIBER )
	public SubscriberProvisioningResponse wsPurgeSubscriber(
			@WebParam(name="subscriberID")String subscriberID,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="param1")String param1,
			@WebParam(name="param2")String param2);
   
   @WebMethod( operationName = WS_PURGE_SUBSCRIBERS )
	public SubscriberProvisioningResponse wsPurgeSubscribers(
			@WebParam(name="subscriberIDs")List<String> subscriberIDs,
			@WebParam(name="alternateIds")List<String> alternateIds,
			@WebParam(name="param1")String param1,
			@WebParam(name="param2")String param2);
   
   @WebMethod( operationName = WS_PURGE_ALL_SUBSCRIBER )
	public SubscriberProvisioningResponse wsPurgeAllSubscriber(
			@WebParam(name="param1")String param1,
			@WebParam(name="param2")String param2);

	@WebMethod( operationName = WS_RESTORE_SUBSCRIBER)
	public SubscriberProvisioningResponse wsRestoreSubscriber(
			@WebParam(name="subscriberID")String subscriberId,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="param1")String parameter1,
			@WebParam(name="param2")String parameter2);


	@WebMethod( operationName = WS_RESTORE_SUBSCRIBERS)
	public SubscriberProvisioningResponse wsRestoreSubscribers(
			@WebParam(name="subscriberIDs")List<String> subscriberIdentities,
			@WebParam(name="alternateIds")List<String> alternateIds,
			@WebParam(name="param1")String parameter1,
			@WebParam(name="param2")String parameter2);

	
	@WebMethod( operationName = WS_MIGRATE_SUBSCRIBER)
	public SubscriberProvisioningResponse wsMigrateSubscriber(
			@WebParam(name="currentSubscriberIdentity")String currentSubscriberIdentity,
			@WebParam(name="newSubscriberIdentity")String newSubscriberIdentity,
			@WebParam(name="param1")String parameter1,
			@WebParam(name="param2")String parameter2);
	
	@WebMethod( operationName = WS_ADD_SUBSCRIBER )
	public SubscriberProvisioningResponse wsAddSubscriber(
			@WebParam(name="subscriberProfile")SubscriberProfileData subscriberProfile,	 
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);
	
	@WebMethod( operationName = WS_ADD_SUBSCRIBERS )
	public SubscriberProvisioningResponse wsAddSubscribers(
			@WebParam(name="subscriberProfile")List<SubscriberProfileData> subscriberProfile,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);
	
	@WebMethod( operationName = WS_CHANGE_IMS_PACKAGE)
	public SubscriberProvisioningResponse wsChangeImsPackage(
			@WebParam(name="subscriberID")String subscriberID,
			@WebParam(name="alternateID")String alternateID,
			@WebParam(name="currentPackageName")String currentPackageName,
			@WebParam(name="newPackageName")String newPackageName,
			@WebParam(name="packageType")String packageType,
			@WebParam(name="updateAction")@XmlJavaTypeAdapter(StringToIntegerAdapterForReauth.class)Integer updateAction,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2, 
			@WebParam(name="parameter3")String parameter3);

	@WebMethod( operationName = WS_CHANGE_BASE_PRODUCT_OFFER)
	public SubscriberProvisioningResponse wsChangeBaseProductOffer(
			@WebParam(name="subscriberID")String subscriberID,
			@WebParam(name="alternateID")String alternateID,
			@WebParam(name="currentProductOfferName")String currentProductOfferName,
			@WebParam(name="newProductOfferName")String newProductOfferName,
			@WebParam(name="updateAction")@XmlJavaTypeAdapter(StringToIntegerAdapterForReauth.class)Integer updateAction,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2,
			@WebParam(name="parameter3")String parameter3);

}