package com.elitecore.nvsmx.ws.subscriberprovisioning;

import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.StringUtil;
import com.elitecore.nvsmx.ws.subscriberprovisioning.blmanager.SubscriberProfileWSBLManager;
import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfile;
import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfileData;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddBulkSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddSubscriberProfileBulkWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddSubscriberProfileWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.ChangeBaseProductOfferWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.ChangeImsPackageWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.DeleteSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.DeleteSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.GetSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.MigrateSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.PurgeSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.PurgeSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.RestoreSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.RestoreSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.SubscriberProvisioningWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.UpdateSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.response.SubscriberProvisioningResponse;
import com.elitecore.nvsmx.ws.util.StringToIntegerAdapterForReauth;

public class SubscriberProvisioningWS implements ISubscriberProvisioningWS {
	//SubscriberProvisioningWS#wsGetSubscriberProfileByID
	private static final String SERVICE_TEMPORARY_UNAVAILABLE = "Service is down for maintenance";
	private static final String MODULE = "SUBSCRIBER-PROVI-WS";
	private SubscriberProfileWSBLManager subscriberProfileWSBLManager;
	private static final String WEB_SERVICE_NAME = SubscriberProvisioningWS.class.getSimpleName();
	
	public SubscriberProvisioningWS() {
		subscriberProfileWSBLManager = new SubscriberProfileWSBLManager();
	}

	@Override
	public SubscriberProvisioningResponse wsAddSubscriberProfile(
			SubscriberProfile subscriberProfile,
			String parameter1,
			String parameter2) {

		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Called wsAddSubscriberProfile with Request Parameters: Subscriber Profile: " + subscriberProfile
				+ ", Parameter1: " + parameter1 +
				", Parameter2: " + parameter2);
		}
		return subscriberProfileWSBLManager.addSubscriberProfile(new AddSubscriberWSRequest(subscriberProfile, parameter1, parameter2, WEB_SERVICE_NAME, WS_ADD_SUBSCRIBER_PROFILE));
	}

	@Override
	public SubscriberProvisioningResponse wsUpdateSubscriberProfile(
			SubscriberProfile subscriberProfile, 
			String subscriberId, 
			String alternateId,
			@XmlJavaTypeAdapter(StringToIntegerAdapterForReauth.class) Integer updateAction,
			String parameter1,
			String parameter2) {

		subscriberId = StringUtil.trimParameter(subscriberId);
		alternateId = StringUtil.trimParameter(alternateId);
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Called wsUpdateSubscriber with Request Parameters: Subscriber Profile: " + subscriberProfile
				+ " Subscriber ID: " + subscriberId
				+ ", Alternate ID: " + alternateId
				+ ", Update Action: " + updateAction
				+ ", Parameter1: " + parameter1
				+ ", Parameter2: " + parameter2);
		}
		return subscriberProfileWSBLManager
				.updateSubscriberProfile(new UpdateSubscriberWSRequest(subscriberProfile, subscriberId, alternateId, updateAction, parameter1, parameter2, WEB_SERVICE_NAME, WS_UPDATE_SUBSCRIBER_PROFILE));
	}

	@Override
	public SubscriberProvisioningResponse wsDeleteSubscriberProfile(
			String subscriberID,
			String alternateId,
			String parameter1,
			String parameter2) {

		subscriberID = StringUtil.trimParameter(subscriberID);
		alternateId = StringUtil.trimParameter(alternateId);
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Called wsDeleteSubscriberProfile with Request Parameters: "
				+ " Subscriber ID: " + subscriberID
				+ " Alternate Id: " + alternateId
				+ " Parameter1: " + parameter1
				+ ", Parameter2: " + parameter2);
		}
		return subscriberProfileWSBLManager.deleteSubscriberProfile(new DeleteSubscriberWSRequest(subscriberID, alternateId, parameter1, parameter2, WEB_SERVICE_NAME, WS_DELETE_SUBSCRIBER_PROFILE));
	}
	
	@Override
	public SubscriberProvisioningResponse wsDeleteSubscriberProfiles(List<String> subscriberIdentities, List<String> alternateIds, String parameter1, String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Called wsDeleteSubscriberProfiles with Request Parameters: "
				+ " Subscriber Identities: " + subscriberIdentities
				+ " Alternate Ids: " + alternateIds
				+ " Parameter1: " + parameter1
				+ ", Parameter2: " + parameter2);
		}
		return subscriberProfileWSBLManager.deleteSubscriberProfiles(new DeleteSubscribersWSRequest(subscriberIdentities, alternateIds, parameter1, parameter2, WEB_SERVICE_NAME, WS_DELETE_SUBSCRIBER_PROFILES));
	}

	@Override
	public SubscriberProvisioningResponse wsAddSubscriberBulk(
			List<SubscriberProfile> subscriberProfile,
			String parameter1,
			String parameter2) {
		
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Total Subscriber Profiles: ");
		int noOfSubscriberProfiles = 0;
		if (subscriberProfile != null) {
			noOfSubscriberProfiles = subscriberProfile.size();
		}
		stringBuilder.append(noOfSubscriberProfiles);
		stringBuilder.append(", Parameter1: ");
		stringBuilder.append(parameter1);
		stringBuilder.append(", Parameter2: ");
		stringBuilder.append(parameter2);

		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Called wsAddSubscriberBulk with Request Parameters: " + stringBuilder.toString());
		}
		return subscriberProfileWSBLManager.addSubscriberProfiles(new AddBulkSubscriberWSRequest(subscriberProfile, parameter1, parameter2, WEB_SERVICE_NAME, WS_ADD_SUBSCRIBER_BULK));
	}

	@Override
	public SubscriberProvisioningResponse wsGetSubscriberProfileByID(
			String subscriberId,
			String alternateId,
			String parameter1,
			String parameter2) {
		
		subscriberId = StringUtil.trimParameter(subscriberId);
		alternateId = StringUtil.trimParameter(alternateId);
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Called wsGetSubscriberProfileByID with Request Parameters: "
				+ " Subscriber ID: " + subscriberId
				+ " Alternate ID: " + alternateId
				+ " Parameter1: " + parameter1
				+ ", Parameter2: " + parameter2);
		}
		return subscriberProfileWSBLManager.getSubscriberByID(new GetSubscriberWSRequest(subscriberId, alternateId, parameter1, parameter2,WEB_SERVICE_NAME, WS_GET_SUBSCRIBER_PROFILE_BY_ID));
	}

	@Override
	public SubscriberProvisioningResponse wsListSubscriberProfiles(
			Map<String, String> subscriberProfileCriteria,
			String parameter1,
			String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, SERVICE_TEMPORARY_UNAVAILABLE);
		}
		return getSubcriberProvisioningResponse();

	}

	private SubscriberProvisioningResponse getSubcriberProvisioningResponse() {
		long l = ResultCode.SERVICE_UNAVAILABLE.code;
		SubscriberProvisioningResponse subscriberProvisioningResponse = new SubscriberProvisioningResponse((int) l, SERVICE_TEMPORARY_UNAVAILABLE, null, null, null, WEB_SERVICE_NAME, WS_LIST_SUBSCRIBER_PROFILES);
		return subscriberProvisioningResponse;
	}

	@Override
	public SubscriberProvisioningResponse wsPurgeSubscriber(String subscriberId, String alternateId, String parameter1, String parameter2) {

		subscriberId = StringUtil.trimParameter(subscriberId);
		alternateId=StringUtil.trimParameter(alternateId);
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Called wsPurgeSubscriber with Request Parameters: "
				+ " Subscriber Identity: " + subscriberId
				+ " Alternate Identity: "  +alternateId
				+ " Parameter1: " + parameter1
				+ ", Parameter2: " + parameter2);
		}
		return subscriberProfileWSBLManager.purgeSubscriber(new PurgeSubscriberWSRequest(subscriberId, alternateId, parameter1, parameter2, WEB_SERVICE_NAME, WS_PURGE_SUBSCRIBER));

	}

	@Override
	public SubscriberProvisioningResponse wsPurgeSubscribers(List<String> subscriberIdentities, List<String> alternateIds, String parameter1, String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Called wsPurgeSubscribers with Request Parameters: "
				+ " Subscriber Identities: " + subscriberIdentities
				+ " Alternate Ids: " + alternateIds
				+ " Parameter1: " + parameter1
				+ ", Parameter2: " + parameter2);
		}
		return subscriberProfileWSBLManager.purgeSubscribers(new PurgeSubscribersWSRequest(subscriberIdentities, alternateIds, parameter1, parameter2, WEB_SERVICE_NAME, WS_PURGE_SUBSCRIBERS));
	}

	@Override
	public SubscriberProvisioningResponse wsPurgeAllSubscriber(String parameter1, String parameter2) {
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Called wsPurgeAllSubscriber with Request Parameters: "
				+ " Parameter1: " + parameter1
				+ ", Parameter2: " + parameter2);
		}
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		
		return subscriberProfileWSBLManager.purgeAllSubscribers(new SubscriberProvisioningWSRequest(parameter1, parameter2, WEB_SERVICE_NAME, WS_PURGE_ALL_SUBSCRIBER));
	}

	@Override
	public SubscriberProvisioningResponse wsRestoreSubscriber(String subscriberId, String alternateId, String parameter1, String parameter2) {
		subscriberId = StringUtil.trimParameter(subscriberId);
		alternateId=StringUtil.trimParameter(alternateId);
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Called wsRestoreSubscriber with Request Parameters: "
					+ " Subscriber Identity: " + subscriberId
					+ " Alternate Identity: "+alternateId
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return subscriberProfileWSBLManager.restoreSubscriber(new RestoreSubscriberWSRequest(subscriberId, alternateId, parameter1, parameter2, WEB_SERVICE_NAME, WS_RESTORE_SUBSCRIBER));
	}

	@Override
	public SubscriberProvisioningResponse wsRestoreSubscribers(List<String> subscriberIdentities, List<String> alternateIds, String parameter1, String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Called wsRestoreSubscribers with Request Parameters: "
				+ " Subscriber Identities: " + subscriberIdentities
				+ " Alternate Ids: " + alternateIds
				+ " Parameter1: " + parameter1
				+ ", Parameter2: " + parameter2);
		}
		return subscriberProfileWSBLManager.restoreSubscribers(new RestoreSubscribersWSRequest(subscriberIdentities, alternateIds, parameter1, parameter2, WEB_SERVICE_NAME, WS_RESTORE_SUBSCRIBERS));
	}


	private ILogger getLogger() {
		return LogManager.getLogger();

	}

	@Override
	@WebMethod(operationName = "wsMigrateSubscriber")
	public SubscriberProvisioningResponse wsMigrateSubscriber(@WebParam(name = "currentSubscriberIdentity") String currentSubscriberIdentity,
			@WebParam(name = "newSubscriberIdentity") String newSubscriberIdentity, @WebParam(name = "param1") String parameter1,
			@WebParam(name = "param2") String parameter2) {
		
		currentSubscriberIdentity = StringUtil.trimParameter(currentSubscriberIdentity);
		newSubscriberIdentity = StringUtil.trimParameter(newSubscriberIdentity);
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Called wsMigrateSubscriber with Request Parameters: "
				+ " Current Subscriber Identity: " + currentSubscriberIdentity
				+ ", New Subscriber Identity: " + newSubscriberIdentity
				+ ", Parameter1: " + parameter1
				+ ", Parameter2: " + parameter2);
		}
		
		return subscriberProfileWSBLManager.migrateSubscriber(new MigrateSubscriberWSRequest(currentSubscriberIdentity, newSubscriberIdentity, parameter1, parameter2, WEB_SERVICE_NAME, WS_MIGRATE_SUBSCRIBER));
	}

	@Override
	public SubscriberProvisioningResponse wsAddSubscriber(
			SubscriberProfileData subscriberProfile, String parameter1,
			String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Called wsAddSubscriber with Request Parameters: Subscriber Profile: " + subscriberProfile
				+ ", Parameter1: " + parameter1 +
				", Parameter2: " + parameter2);
		}
		return subscriberProfileWSBLManager.addSubscriber(new AddSubscriberProfileWSRequest(subscriberProfile, parameter1, parameter2, WEB_SERVICE_NAME, WS_ADD_SUBSCRIBER));
	}

	@Override
	public SubscriberProvisioningResponse wsAddSubscribers(
			List<SubscriberProfileData> subscriberProfile, String parameter1,
			String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		if(getLogger().isDebugLogLevel()){
			
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Total Subscriber Profiles: ");
			int noOfSubscriberProfiles = 0;
			if (subscriberProfile != null) {
				noOfSubscriberProfiles = subscriberProfile.size();
			}
			stringBuilder.append(noOfSubscriberProfiles);
			stringBuilder.append(", Parameter1: ");
			stringBuilder.append(parameter1);
			stringBuilder.append(", Parameter2: ");
			stringBuilder.append(parameter2);

			getLogger().debug(MODULE, "Called wsAddSubscribers with Request Parameters: " + stringBuilder.toString());
		}
		return subscriberProfileWSBLManager.addSubscribers(new AddSubscriberProfileBulkWSRequest(subscriberProfile, parameter1, parameter2, WEB_SERVICE_NAME, WS_ADD_SUBSCRIBERS));
	}
	
	@Override
	public SubscriberProvisioningResponse wsChangeImsPackage(String subscriberID, String alternateID, String currentPackageName
			, String newPackageName, String packageType, Integer updateAction
			, String parameter1, String parameter2, String parameter3) {

		subscriberID = StringUtil.trimParameter(subscriberID);
		alternateID = StringUtil.trimParameter(alternateID);
		currentPackageName = StringUtil.trimParameter(currentPackageName);
		newPackageName = StringUtil.trimParameter(newPackageName);
		packageType = StringUtil.trimParameter(packageType);
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		parameter3 = StringUtil.trimParameter(parameter3);
		if(getLogger().isDebugLogLevel()){
			
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Subscriber Identity: ");
			stringBuilder.append(subscriberID);
			stringBuilder.append(", Alternate Identity: ");
			stringBuilder.append(alternateID);
			stringBuilder.append(", Current Package Name: ");
			stringBuilder.append(currentPackageName);
			stringBuilder.append(", New Package Name: ");
			stringBuilder.append(newPackageName);
			stringBuilder.append(", Package Type: ");
			stringBuilder.append(packageType);
			stringBuilder.append(", Update Action: ");
			stringBuilder.append(updateAction);
			stringBuilder.append(", Parameter1: ");
			stringBuilder.append(parameter1);
			stringBuilder.append(", Parameter2: ");
			stringBuilder.append(parameter2);
			stringBuilder.append(", Parameter3: ");
			stringBuilder.append(parameter3);

			getLogger().debug(MODULE, "Called wsChangePackage with Request Parameters: " + stringBuilder.toString());
		}

		return subscriberProfileWSBLManager.changeImsPackage(new ChangeImsPackageWSRequest(currentPackageName, newPackageName, packageType, subscriberID, alternateID, updateAction, WEB_SERVICE_NAME, WS_CHANGE_IMS_PACKAGE, parameter1, parameter2, parameter3));
	}

    @Override
    public SubscriberProvisioningResponse wsChangeBaseProductOffer(String subscriberID, String alternateID,
                                                                   String currentProductOfferName, String newProductOfferName,
                                                                   Integer updateAction, String parameter1,
                                                                   String parameter2, String parameter3) {
        subscriberID = StringUtil.trimParameter(subscriberID);
        alternateID = StringUtil.trimParameter(alternateID);
        currentProductOfferName = StringUtil.trimParameter(currentProductOfferName);
        newProductOfferName = StringUtil.trimParameter(newProductOfferName);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);
        parameter3 = StringUtil.trimParameter(parameter3);

        if (getLogger().isDebugLogLevel()) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Subscriber Identity: ");
            stringBuilder.append(subscriberID);
            stringBuilder.append(", Alternate Identity: ");
            stringBuilder.append(alternateID);
            stringBuilder.append(", Current Product Offer: ");
            stringBuilder.append(currentProductOfferName);
            stringBuilder.append(", New Product Offer: ");
            stringBuilder.append(newProductOfferName);
            stringBuilder.append(", Update Action: ");
            stringBuilder.append(updateAction);
            stringBuilder.append(", Parameter1: ");
            stringBuilder.append(parameter1);
            stringBuilder.append(", Parameter2: ");
            stringBuilder.append(parameter2);
            stringBuilder.append(", Parameter3: ");
            stringBuilder.append(parameter3);

            getLogger().debug(MODULE, "Called wsChangePackage with Request Parameters: " + stringBuilder.toString());
        }

        return subscriberProfileWSBLManager.changeBaseProductOffer(new ChangeBaseProductOfferWSRequest(currentProductOfferName, newProductOfferName,
                 subscriberID, alternateID, updateAction, WEB_SERVICE_NAME, WS_CHANGE_BASE_PRODUCT_OFFER, parameter1, parameter2, parameter3));
    }

}