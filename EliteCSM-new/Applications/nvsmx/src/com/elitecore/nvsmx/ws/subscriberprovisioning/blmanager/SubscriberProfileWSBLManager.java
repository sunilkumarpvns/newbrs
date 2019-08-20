package com.elitecore.nvsmx.ws.subscriberprovisioning.blmanager;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.SubscriberAlternateIdStatusDetail;
import com.elitecore.corenetvertex.spr.SubscriberAlternateIds;
import com.elitecore.corenetvertex.spr.data.AlternateIdType;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberDetails;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.policydesigner.model.subscriber.SubscriberDAO;
import com.elitecore.nvsmx.system.groovy.GroovyManager;
import com.elitecore.nvsmx.system.groovy.SubscriberProvisioningWsScript;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextWebServiceInterceptor;
import com.elitecore.nvsmx.ws.interceptor.WebServiceAuditInterceptor;
import com.elitecore.nvsmx.ws.interceptor.WebServiceInterceptor;
import com.elitecore.nvsmx.ws.interceptor.WebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatisticsManager;
import com.elitecore.nvsmx.ws.subscriberprovisioning.data.Entry;
import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfile;
import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfileData;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddAlternateIdWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddBulkSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddSubscriberProfileBulkWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddSubscriberProfileWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.ChangeBaseProductOfferWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.ChangeImsPackageWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.DeleteSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.DeleteSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.GetAlternateIdWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.GetSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.MigrateSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.PurgeSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.PurgeSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.RestoreSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.RestoreSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.SubscriberProvisioningWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.UpdateAlternateIdWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.UpdateSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.response.AlternateIdProvisioningResponse;
import com.elitecore.nvsmx.ws.subscriberprovisioning.response.SubscriberProvisioningResponse;
import com.elitecore.nvsmx.ws.util.AlternateIdOperationUtils;
import com.elitecore.nvsmx.ws.util.ConvertStringToDigit;
import com.elitecore.nvsmx.ws.util.ReAuthUtil;
import com.elitecore.nvsmx.ws.util.UpdateActions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class SubscriberProfileWSBLManager {
	
	private static final String  MODULE="SUB-PROF-WSBL-MNGR";
	private static final String IMS = "IMS";
	private StaffData adminStaff;
	private List<WebServiceInterceptor> interceptors = new ArrayList<WebServiceInterceptor>();
	private ChangeBaseProductOfferProcessor changeBaseProductOfferProcessor;

	
	public SubscriberProfileWSBLManager() {
		adminStaff = new StaffData();
		adminStaff.setUserName("admin");
		interceptors.add(WebServiceStatisticsManager.getInstance());
		interceptors.add(WebServiceAuditInterceptor.getInstance());
		interceptors.add(DiagnosticContextWebServiceInterceptor.getInstance());

		this.changeBaseProductOfferProcessor = new ChangeBaseProductOfferProcessor(interceptors, GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts());

	}
	
	public SubscriberProvisioningResponse purgeSubscriber(PurgeSubscriberWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
			
			try {
				groovyScript.prePurgeSubscriber(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberId(), request.getAlternateId())+ " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		 
		SubscriberProvisioningResponse response = doPurgeSubscriber(request);
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.postPurgeSubscriber(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberId(), request.getAlternateId())+ " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		applyResponseInterceptors(response);
		return response;
	}


	private SubscriberProvisioningResponse doPurgeSubscriber(PurgeSubscriberWSRequest request) {
		String subscriberIdentity = request.getSubscriberId();
		String requestIpAddress = request.getRequestIpAddress();
		
		try {
			
			int result;
			if(Strings.isNullOrBlank(subscriberIdentity)==true){

				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE,"Subscriber ID not received");
				}
				String alternateId = request.getAlternateId();
				
				if (Strings.isNullOrBlank(alternateId)) {
					LogManager.getLogger().error(MODULE, "Unable to purge subscriber profile. Reason: Identity parameter missing");
					return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				
				result = SubscriberDAO.getInstance().purgeSubscriberAlternateId(alternateId, adminStaff, requestIpAddress);
			} else {
				result = SubscriberDAO.getInstance().purgeSubscriber(subscriberIdentity, adminStaff, requestIpAddress);
			}
			
			ResultCode resultCode = getResultCode(result);
            return new SubscriberProvisioningResponse(resultCode.code,resultCode.name, null, null,null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch(OperationFailedException e){
			LogManager.getLogger().error(MODULE, "Error while purging subscriber with subscriber Identity :" + subscriberIdentity + ". Reason: "+e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
            return new SubscriberProvisioningResponse(e.getErrorCode().code,e.getErrorCode().name + ". Reason: "+e.getMessage() , null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while purging subscriber with subscriber Identity :" + subscriberIdentity + ". Reason: "+e.getMessage());
			LogManager.getLogger().trace(e);
            return new SubscriberProvisioningResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}


	public SubscriberProvisioningResponse purgeSubscribers(PurgeSubscribersWSRequest request) {
		
		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.prePurgeSubscribers(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		SubscriberProvisioningResponse response = doPurgeSubscribers(request);
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.postPurgeSubscribers(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		applyResponseInterceptors(response);
		return response;
	}
	

	private SubscriberProvisioningResponse doPurgeSubscribers(PurgeSubscribersWSRequest request) {
		List<String> subscriberIdentities = request.getSubscriberIds();
		String requestIpAddress = request.getRequestIpAddress();
		
		try {
			
			Map<String,Integer> responseMap;

			if(Collectionz.isNullOrEmpty(subscriberIdentities)){
				
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE,"Subscriber IDs not received");
				}
				
				List<String> alternateIds = request.getAlternateIds();
				
				if (Collectionz.isNullOrEmpty(alternateIds)) {
					LogManager.getLogger().error(MODULE, "Unable to purge Subscriber Profiles. Reason: Identity parameter missing");
					return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				
				responseMap = SubscriberDAO.getInstance().purgeSubscriberByAlternateId(alternateIds, adminStaff, requestIpAddress);
			} else {
				responseMap = SubscriberDAO.getInstance().purgeSubscriber(subscriberIdentities, adminStaff, requestIpAddress);
			}

			List<SubscriberProfile> subscriberProfiles = new ArrayList<SubscriberProfile>();
			if (Maps.isNullOrEmpty(responseMap)) {
				return new SubscriberProvisioningResponse(ResultCode.NOT_FOUND.code, ResultCode.NOT_FOUND.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
			
			for(java.util.Map.Entry<String, Integer> entry : responseMap.entrySet()){
				subscriberProfiles.add(createSubscriberProfile(entry.getKey(),getResultCode(entry.getValue())));
			}
            return new SubscriberProvisioningResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, subscriberProfiles, null,null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch(OperationFailedException e){
			LogManager.getLogger().error(MODULE, "Error while purging subscribers. Reason: "+e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
            return new SubscriberProvisioningResponse(e.getErrorCode().code, e.getErrorCode().name+ ". Reason: "+e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while purging subscribers. Reason: "+e.getMessage());
			LogManager.getLogger().trace(e);
            return new SubscriberProvisioningResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}

	public SubscriberProvisioningResponse purgeAllSubscribers(SubscriberProvisioningWSRequest request) {
		
		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.prePurgeAllSubscriber(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		SubscriberProvisioningResponse response = doPurgeAllSubscribers(request);
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.postPurgeAllSubscriber(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		applyResponseInterceptors(response);
		return response;
	}


	private SubscriberProvisioningResponse doPurgeAllSubscribers(SubscriberProvisioningWSRequest request) {
		try {
			Map<String, Integer> responseMap  = SubscriberDAO.getInstance().purgeAllSubscribers(adminStaff, request.getRequestIpAddress());

			List<SubscriberProfile> subscriberProfiles = Collectionz.newArrayList();

			for(java.util.Map.Entry<String, Integer> entry : responseMap.entrySet()){
				subscriberProfiles.add(createSubscriberProfile(entry.getKey(),getResultCode(entry.getValue())));
			}

            return new SubscriberProvisioningResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, subscriberProfiles, null,null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch(OperationFailedException e){
			LogManager.getLogger().error(MODULE, "Error while purging subscribers. Reason: "+e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
            return new SubscriberProvisioningResponse(e.getErrorCode().code, e.getErrorCode().name+ ". Reason: "+e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while purging All subscribers. Reason: "+e.getMessage());
			LogManager.getLogger().trace(e);
            return new SubscriberProvisioningResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}

	//RESTORE SUBSCRIBERS
	public SubscriberProvisioningResponse restoreSubscriber(RestoreSubscriberWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();

		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {

			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preRestoreSubscriber(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberId(), request.getAlternateId())+ ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		SubscriberProvisioningResponse response = doRestoreSubscriber(request);
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {

			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postRestoreSubscriber(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberId(), request.getAlternateId())+ ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		applyResponseInterceptors(response);
		return response;
	}


	private SubscriberProvisioningResponse doRestoreSubscriber(RestoreSubscriberWSRequest request) {
		String subscriberIdentity = request.getSubscriberId();
		String requestIpAddress = request.getRequestIpAddress();

		try {

			int result;
			if(Strings.isNullOrBlank(subscriberIdentity)==true){

				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE,"Subscriber ID not received");
				}
				String alternateId = request.getAlternateId();

				if (Strings.isNullOrBlank(alternateId)) {
					LogManager.getLogger().error(MODULE, "Unable to restore subscriber profile. Reason: Identity parameter missing");
					return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				result = SubscriberDAO.getInstance().restoreSubscriberByAlternateId(alternateId, adminStaff, requestIpAddress);
			} else {
				result = SubscriberDAO.getInstance().restoreSubscriber(subscriberIdentity, adminStaff, requestIpAddress);
			}

			ResultCode resultCode = getResultCode(result);
            return new SubscriberProvisioningResponse(resultCode.code,resultCode.name, null, null,null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch(OperationFailedException e){
			LogManager.getLogger().error(MODULE, "Error while restoring subscriber with subscriber Identity :" + subscriberIdentity + ". Reason: "+e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
            return new SubscriberProvisioningResponse(e.getErrorCode().code,e.getErrorCode().name + ". Reason: "+e.getMessage() , null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while restoring subscriber with subscriber Identity :" + subscriberIdentity + ". Reason: "+e.getMessage());
			LogManager.getLogger().trace(e);
            return new SubscriberProvisioningResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}


	public SubscriberProvisioningResponse restoreSubscribers(RestoreSubscribersWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();

		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preRestoreSubscribers(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		SubscriberProvisioningResponse response = doRestoreSubscribers(request);
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postRestoreSubscribers(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		applyResponseInterceptors(response);
		return response;
	}


	private SubscriberProvisioningResponse doRestoreSubscribers(RestoreSubscribersWSRequest request) {
		List<String> subscriberIdentities = request.getSubscriberIds();

		try {

			Map<String,Integer> responseMap;



			if(Collectionz.isNullOrEmpty(subscriberIdentities)){

				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE,"Subscriber IDs not received");
				}

				List<String> alternateIds = request.getAlternateIds();

				if (Collectionz.isNullOrEmpty(alternateIds)) {
					LogManager.getLogger().error(MODULE, "Unable to restore Subscriber Profiles. Reason: Identity parameter missing");
					return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				responseMap = SubscriberDAO.getInstance().restoreSubscriberByAlternateId(alternateIds, adminStaff);
			} else {
				responseMap = SubscriberDAO.getInstance().restoreSubscriber(subscriberIdentities, adminStaff);
			}

			List<SubscriberProfile> subscriberProfiles = new ArrayList<SubscriberProfile>();
			if (Maps.isNullOrEmpty(responseMap)) {
				return new SubscriberProvisioningResponse(ResultCode.NOT_FOUND.code, ResultCode.NOT_FOUND.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}

			for(java.util.Map.Entry<String, Integer> entry : responseMap.entrySet()){
				subscriberProfiles.add(createSubscriberProfile(entry.getKey(),getResultCode(entry.getValue())));
			}
            return new SubscriberProvisioningResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, subscriberProfiles, null,null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch(OperationFailedException e){
			LogManager.getLogger().error(MODULE, "Error while restoring subscribers. Reason: "+e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
            return new SubscriberProvisioningResponse(e.getErrorCode().code, e.getErrorCode().name+ ". Reason: "+e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while restoring subscribers. Reason: "+e.getMessage());
			LogManager.getLogger().trace(e);
            return new SubscriberProvisioningResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}
	
	private SubscriberProvisioningResponse doRestoreAllSubscribers(SubscriberProvisioningWSRequest request) {
		try {
			Map<String, Integer> responseMap  = SubscriberDAO.getInstance().restoreAllSubscribers(adminStaff, request.getRequestIpAddress());

			List<SubscriberProfile> subscriberProfiles = Collectionz.newArrayList();

			for(java.util.Map.Entry<String, Integer> entry : responseMap.entrySet()){
				subscriberProfiles.add(createSubscriberProfile(entry.getKey(),getResultCode(entry.getValue())));
			}

            return new SubscriberProvisioningResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, subscriberProfiles, null,null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch(OperationFailedException e){
			LogManager.getLogger().error(MODULE, "Error while restoring subscribers. Reason: "+e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
            return new SubscriberProvisioningResponse(e.getErrorCode().code, e.getErrorCode().name+ " Reason: "+e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while restoring All subscribers. Reason: "+e.getMessage());
			LogManager.getLogger().trace(e);
            return new SubscriberProvisioningResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}

	public SubscriberProvisioningResponse addSubscriberProfile(AddSubscriberWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.preAddSubscriberProfile(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() +". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		SubscriberProvisioningResponse subscriberResponse = doAddSubscriberProfile(request.getSubscriberProfile(),
				request.getWebServiceName(), request.getWebServiceMethodName(), request.getRequestIpAddress());
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.postAddSubscriberProfile(request, subscriberResponse);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		applyResponseInterceptors(subscriberResponse);
		return subscriberResponse;
	}

	/**
	 * Extended method of addSubscriberProfile. It will add subscriber based on the subscriber identity, data package and list of attributes provided as Entry.
	 * SubscriberIdentity and data package are the mandatory parameters.
	 * @param request that comprises of Subscriber Identity, data package and list of entries that contains key-value pair
	 * @return SubscriberProvisioningResponse with Result code and response message
	 */
	public SubscriberProvisioningResponse addSubscriber(AddSubscriberProfileWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.preAddSubscriber(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() +" for subscriber ID: " + request.getSubscriberProfile().getSubscriberIdentity() +". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		/**
		 * @author Prakashkumar Pala
		 * @since 22-Oct-2018
		 * for NETVERTEX-3068 - START
		 */
		SubscriberProvisioningResponse subscriberProvisioningResponse = doAddSubscriber(request.getSubscriberProfile(), request.getWebServiceName()
				, request.getWebServiceMethodName(), request.getRequestIpAddress());
		/**
		 * for NETVERTEX-3068 - END
		 */
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.postAddSubscriber(request, subscriberProvisioningResponse);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() +" for subscriber ID: "+ request.getSubscriberProfile().getSubscriberIdentity() + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		applyResponseInterceptors(subscriberProvisioningResponse);
		return subscriberProvisioningResponse;
	}
	private SubscriberProvisioningResponse doAddSubscriberProfile(SubscriberProfile subscriberProfile, String webServiceName, String webServiceMethodName, String requestIpAddress) {
		
		if(subscriberProfile == null){
			LogManager.getLogger().error(MODULE, "Unable to add Subscriber Profile. Reason: subscriber profile is null");
			return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: subscriber profile is null" , null, null, null, webServiceName, webServiceMethodName);
		}

		if(Collectionz.isNullOrEmpty(subscriberProfile.getEntry())){
			LogManager.getLogger().error(MODULE, "Unable to add Subscriber Profile. Reason: subscriber profile is empty");
			return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Empty Subscriber profile received" , null, null, null, webServiceName, webServiceMethodName);
		}
		
		String subscriberIdentity = getSubscriberIdentity(subscriberProfile);
		if (Strings.isNullOrBlank(subscriberIdentity)) {
			LogManager.getLogger().error(MODULE, "Unable to add Subscriber Profile. Reason: Missing Subscriber Identity");
			return new SubscriberProvisioningResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Missing Subscriber Identity" , null, null, null, webServiceName, webServiceMethodName);
		}
		
		subscriberProfile.setSubscriberIdentity(subscriberIdentity);
		
		try {
			SPRInfo sprInfo = createSPRInfo(subscriberProfile);
			SubscriberDAO.getInstance().addSubscriber(new SubscriberDetails(sprInfo,subscriberProfile.getCreditLimit()), adminStaff, requestIpAddress);
			return new SubscriberProvisioningResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, null, null, null, webServiceName, webServiceMethodName);
		} catch(ParseException e) {
			LogManager.getLogger().error(MODULE, "Failed to add subscriber profile for subscriber ID: " + subscriberIdentity 
					+ ". ErrorCode: " + ResultCode.INVALID_INPUT_PARAMETER + ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return new SubscriberProvisioningResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER + ". Reason: " + e.getMessage(), null, null, null, webServiceName, webServiceMethodName);
		} catch(OperationFailedException e){
			LogManager.getLogger().error(MODULE, "Failed to add subscriber profile for Subscriber ID: " + subscriberIdentity
					+ ". ErrorCode: " + e.getErrorCode() + ". Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return new SubscriberProvisioningResponse(e.getErrorCode().code, e.getErrorCode() + ". Reason: " + e.getMessage(), null, null, null, webServiceName, webServiceMethodName);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to add subscriber profile for Subscriber ID: " + subscriberIdentity
					+ ". ErrorCode: " + ResultCode.INTERNAL_ERROR + ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return new SubscriberProvisioningResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR + ". Reason: " + e.getMessage(), null, null, null, webServiceName, webServiceMethodName);
		}

	}
	
	private SubscriberProvisioningResponse doAddSubscriber(SubscriberProfileData subscriberProfile, String webServiceName, String webServiceMethodName
			, String requestIpAddress) {
				
		if(subscriberProfile == null){
			LogManager.getLogger().error(MODULE, "Unable to add subscriber profile. Reason: subscriber profile is null");
			return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: subscriber profile is null" , null, null, null, webServiceName, webServiceMethodName);
		}
		
		String subscriberIdentity = subscriberProfile.getSubscriberIdentity();
		String productOfferName = subscriberProfile.getProductOffer();
		
		if(Strings.isNullOrBlank(subscriberIdentity)){
			LogManager.getLogger().error(MODULE, "Unable to add subscriber profile. Reason: Missing subscriber identity");
			return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Subscriber Identity" , null, null, null, webServiceName, webServiceMethodName);
		}
		
		if(Strings.isNullOrBlank(productOfferName)){
			LogManager.getLogger().error(MODULE, "Unable to add subscriber profile. Reason: Missing product offer name for subscriber ID: " + subscriberIdentity);
			return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Product Offer Name for subscriber Identity: "+subscriberIdentity , null, null, null, webServiceName, webServiceMethodName);
		}

		try {
			SPRInfoImpl sprInfo = new SPRInfoImpl();
			if(Collectionz.isNullOrEmpty(subscriberProfile.getEntry()) == false){
				sprInfo =  createSPRInfo(new SubscriberProfile(subscriberProfile.getEntry()));
			}
			
			/*
			 * validation on duplicate entry of Subscriber Identity and Data Package.
			 */
			if(Strings.isNullOrBlank(sprInfo.getSubscriberIdentity()) && Strings.isNullOrBlank(sprInfo.getProductOffer())){
				sprInfo.setSubscriberIdentity(subscriberProfile.getSubscriberIdentity());
				sprInfo.setProductOffer(subscriberProfile.getProductOffer());
				Long creditLimit = ConvertStringToDigit.convertStringToLong(subscriberProfile.getCreditLimit());
				SubscriberDAO.getInstance().addSubscriber(new SubscriberDetails(sprInfo, creditLimit), adminStaff, requestIpAddress);
				return new SubscriberProvisioningResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, null, null, null, webServiceName, webServiceMethodName);
			}else{
				throw new OperationFailedException("Duplicate entry found for Subscriber Identity or Data Package",ResultCode.INVALID_INPUT_PARAMETER);
			}
			
		} catch(ParseException e) {
			LogManager.getLogger().error(MODULE, "Failed to add subscriber profile for subscriber ID: " + subscriberIdentity + ". ErrorCode: " + ResultCode.INVALID_INPUT_PARAMETER + ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return new SubscriberProvisioningResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER + ". Reason: " + e.getMessage(), null, null, null, webServiceName, webServiceMethodName);
		} catch(OperationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to add subscriber profile for subscriber ID: " + subscriberIdentity + ". ErrorCode: " + e.getErrorCode() + ". Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return new SubscriberProvisioningResponse(e.getErrorCode().code, e.getErrorCode() + ". Reason: " + e.getMessage(), null, null, null, webServiceName, webServiceMethodName);
		} catch(Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to add subscriber profile for subscriber ID: " + subscriberIdentity + ". ErrorCode: " + ResultCode.INTERNAL_ERROR + ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return new SubscriberProvisioningResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR + ". Reason: " + e.getMessage(), null, null, null, webServiceName, webServiceMethodName);
		}
	}

	private String getSubscriberIdentity(SubscriberProfile subscriberProfile){
		for(Entry entry : subscriberProfile.getEntry()){
			SPRFields sprField =  SPRFields.fromSPRFields(entry.getKey());
			if(SPRFields.SUBSCRIBER_IDENTITY == sprField){
				if(Strings.isNullOrBlank(entry.getValue()) == false){
					return  entry.getValue();
				}
			}
			
		}	
		return null;
	}

	public SubscriberProvisioningResponse updateSubscriberProfile(UpdateSubscriberWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.preUpdateSubscriberProfile(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberID(), request.getAlternateId())+". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		SubscriberProvisioningResponse response = doUpdateSubscriberProfile(request);
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.postUpdateSubscriberProfile(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberID(), request.getAlternateId())+". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		applyResponseInterceptors(response);		
		return response;
	}
	
	public SubscriberProvisioningResponse changeImsPackage(ChangeImsPackageWSRequest request) {
		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.preChangeImsPackage(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberID(), request.getAlternateId())+". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		SubscriberProvisioningResponse response = doChangeImsPackage(request);
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.postChangeImsPackage(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberID(), request.getAlternateId())+". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		applyResponseInterceptors(response);		
		return response;
	}
	
private SubscriberProvisioningResponse doChangeImsPackage(ChangeImsPackageWSRequest request) {
	
	SubscriberProvisioningResponse subscriberProvisioningResponse=null;
	String subscriberID = request.getSubscriberID();

	try{
		
		int result = 0;
		SPRInfo subscriber = null;
		String newPackageName = request.getNewPackageName();
		
		if (Strings.isNullOrBlank(newPackageName)) {
			getLogger().error(MODULE, "Unable to change ims package for subscriber: " + subscriberID
					+ ". Reason: New package name must be provided");
			return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, 
					ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: New package name must be provided"
					, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		
		if (Strings.isNullOrBlank(subscriberID)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE,"Subscriber ID not received");
			}
			
			String alternateId = request.getAlternateId();
			if (Strings.isNullOrBlank(alternateId)) {
				LogManager.getLogger().error(MODULE, "Unable to change ims package for subscriber. Reason: Identity parameter missing");
				return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
			
			subscriber = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateId, adminStaff);
			
		} else {
			
			subscriber = SubscriberDAO.getInstance().getSubscriberById(subscriberID, adminStaff);
		}
		
			if (subscriber == null) {
				getLogger().error(MODULE, "Unable to change package for subscriber: " + subscriberID 
						+ ". Reason: Subscriber not found from repository");
				return new SubscriberProvisioningResponse(ResultCode.NOT_FOUND.code, 
						ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found from repository"
						, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
			
			String packageType = request.getPackageType();
			
			subscriberID = subscriber.getSubscriberIdentity();
			String currentPackageName = request.getCurrentPackageName();
			
			if (IMS.equalsIgnoreCase(packageType)) {
				
				if (Strings.isNullOrBlank(currentPackageName) == false) {
					
					if (currentPackageName.equals(subscriber.getImsPackage()) == false) {
						getLogger().error(MODULE, "Unable to change ims package for subscriber: " + subscriberID
								+ ". Reason: Subscriber current package: " + subscriber.getImsPackage() + " and current package parameter: " + currentPackageName + " are not same");
						return new SubscriberProvisioningResponse(ResultCode.PRECONDITION_FAILED.code, 
								ResultCode.PRECONDITION_FAILED.name + ". Reason: Subscriber current package: " + subscriber.getImsPackage() +
										" and current package parameter: " + currentPackageName + " are not same"
								, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}
					
				}
				
				if (subscriber.getImsPackage() != null) {
					if (subscriber.getImsPackage().equals(newPackageName)) {
						getLogger().info(MODULE, "Skip changing IMS package. Reason: Current IMS package(" + subscriber.getImsPackage() +") and new IMS package(" + newPackageName +") are same");
						return new SubscriberProvisioningResponse(ResultCode.SUCCESS.code, 
							ResultCode.SUCCESS.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}
				}
				
				result = SubscriberDAO.getInstance().changeIMSPackageBySubscriberId(subscriber.getSubscriberIdentity(), newPackageName, request.getParameter1(), request.getParameter2(), request.getParameter3(), adminStaff);
				
			} else {
				getLogger().error(MODULE, "Unable to change ims package for subscriber: " + subscriberID
						+ ". Reason: Invalid package type(" + packageType +") received. Possible options are IMS");
				return new SubscriberProvisioningResponse(ResultCode.INVALID_INPUT_PARAMETER.code, 
						ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Invalid package type(" + packageType +") received. Possible options is IMS"
						, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
			
		ResultCode resultcode = getResultCode(result);
		subscriberProvisioningResponse= new SubscriberProvisioningResponse(resultcode.code, resultcode.name, null,null, null, request.getWebServiceName(), request.getWebServiceMethodName());

	} catch(OperationFailedException e){
		LogManager.getLogger().error(MODULE, "Change ims package operation failed for Subscriber : "
											+ (subscriberID != null ? subscriberID : request.getAlternateId()) 
											+ ". Reason:"+e.getMessage());
		if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
			getLogger().trace(MODULE, e);
		}
		subscriberProvisioningResponse = new SubscriberProvisioningResponse(e.getErrorCode().code,e.getErrorCode().name+". Reason: "+e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		return subscriberProvisioningResponse;
	} catch(Exception e){
		LogManager.getLogger().error(MODULE, "Change ims package operation operation failed for Subscriber : "
                                    		+ (subscriberID != null ? subscriberID : request.getAlternateId()) 
                                    		+ ".Reason:"+e.getMessage());
		LogManager.getLogger().trace(MODULE, e);
		subscriberProvisioningResponse = new SubscriberProvisioningResponse(ResultCode.INTERNAL_ERROR.code,"Failure", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		return subscriberProvisioningResponse;
	}

	return subscriberProvisioningResponse;
}

	private SubscriberProvisioningResponse doUpdateSubscriberProfile(UpdateSubscriberWSRequest request) {
		SubscriberProvisioningResponse subscriberProvisioningResponse=null;
		SubscriberProfile subscriberProfile = request.getSubscriberProfile();
		String subscriberID = request.getSubscriberID();
		String requestIpAddress = request.getRequestIpAddress();

		if(subscriberProfile == null){
			LogManager.getLogger().error(MODULE, "Unable to update subscriber profile. Reason: subscriber profile is NULL");
			subscriberProvisioningResponse = new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "Subscriber profile not received", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			return subscriberProvisioningResponse;
		}

		if(Collectionz.isNullOrEmpty(subscriberProfile.getEntry())==true){
			LogManager.getLogger().error(MODULE, "Unable to update subscriber profile. Reason: subscriber profile is empty");
			subscriberProvisioningResponse = new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "Subscriber profile is empty", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			return subscriberProvisioningResponse;
		}


		try{
			
			int result = 0;
			if (Strings.isNullOrBlank(subscriberID)) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE,"Subscriber ID not received");
				}
				
				String alternateId = request.getAlternateId();
				if (Strings.isNullOrBlank(alternateId)) {
					LogManager.getLogger().error(MODULE, "Unable to update subscriber profile. Reason: Identity parameter missing");
					return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				EnumMap<SPRFields, String> updatedProfile = createSPRFieldMap(subscriberProfile);
				if (updatedProfile.isEmpty()) {
					return new SubscriberProvisioningResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Valid SPR fields not provided", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				result = SubscriberDAO.getInstance().updateSubscriberByAlternateId(alternateId, updatedProfile, adminStaff, requestIpAddress);
				
				SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateId, adminStaff);
				
				if (updatedProfile.containsKey(SPRFields.PRODUCT_OFFER) || updatedProfile.containsKey(SPRFields.STATUS) || updatedProfile.containsKey(SPRFields.EXPIRY_DATE)) {
					if (sprInfo == null) {
						getLogger().info(MODULE, "Skipping re authorization for subscriber Id: " + subscriberID + ". Reason: sprInfo not found with alternate Id: " + alternateId);
					} else {
						postUpdate(sprInfo.getSubscriberIdentity(), request.getUpdateAction(), getResultCode(result));
					}
				}else{
					
					UpdateActions updateAction = UpdateActions.fromValue(request.getUpdateAction());
					if(updateAction == null){
						if (LogManager.getLogger().isInfoLogLevel()) {
								LogManager.getLogger().warn(MODULE, "Performing NO_ACTION, Invalid value "+(request.getUpdateAction()==null?"":request.getUpdateAction())+" received for parameter 'updateAction'. Valid values are 0/1/2");
						}
					}else{
						postUpdate(sprInfo.getSubscriberIdentity(), request.getUpdateAction(), getResultCode(result));
					}
				}
				
			} else {
				EnumMap<SPRFields, String> updatedProfile = createSPRFieldMap(subscriberProfile);
				if (updatedProfile.isEmpty()) {
					return new SubscriberProvisioningResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Valid SPR fields not provided", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				if(updatedProfile.get(SPRFields.SUBSCRIBER_IDENTITY) !=null && subscriberID.equalsIgnoreCase(updatedProfile.get(SPRFields.SUBSCRIBER_IDENTITY)) == false) {
					
    				getLogger().error(MODULE, "Unable to update subscriber profile."
    						+ " Reason: Subscriber Id("+subscriberID+") provided is different from subscriber Id("+subscriberID+") in profile");
    				return new SubscriberProvisioningResponse(ResultCode.INVALID_INPUT_PARAMETER.code, 
    						ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Subscriber Id provided is different from subscriber Id in profile"
    						, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
    			}
				
				result = SubscriberDAO.getInstance().updateSubscriber(subscriberID, updatedProfile, adminStaff, requestIpAddress);
				
				if (updatedProfile.containsKey(SPRFields.PRODUCT_OFFER) || updatedProfile.containsKey(SPRFields.STATUS) || updatedProfile.containsKey(SPRFields.EXPIRY_DATE)) {
					postUpdate(subscriberID, request.getUpdateAction(), getResultCode(result));
					
				}else{
					
					UpdateActions updateAction = UpdateActions.fromValue(request.getUpdateAction());
					if(updateAction == null){
						if (LogManager.getLogger().isInfoLogLevel()) {
							LogManager.getLogger().warn(MODULE, "Performing NO_ACTION, Invalid value "+(request.getUpdateAction()==null?"":request.getUpdateAction())+" received for parameter 'updateAction'. Valid values are 0/1/2");
						}
					}else{
						postUpdate(subscriberID, request.getUpdateAction(), getResultCode(result));
					}
				}
			}

			ResultCode resultcode = getResultCode(result);
			subscriberProvisioningResponse= new SubscriberProvisioningResponse(resultcode.code, resultcode.name, null,null, null, request.getWebServiceName(), request.getWebServiceMethodName());

		} catch(OperationFailedException e){
			LogManager.getLogger().error(MODULE, "Update subscriber profile operation failed for Subscriber ID : " 
												+ (subscriberID != null ? subscriberID : request.getAlternateId()) 
												+ ". Reason:"+e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			subscriberProvisioningResponse = new SubscriberProvisioningResponse(e.getErrorCode().code,e.getErrorCode().name+". Reason: "+e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			return subscriberProvisioningResponse;
		} catch (ParseException e) {
			LogManager.getLogger().error(MODULE, "Update subscriber profile operation failed for Subscriber ID : " 
									+ (subscriberID != null ? subscriberID : request.getAlternateId()) 
									+ ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
 
			return new SubscriberProvisioningResponse(ResultCode.INVALID_INPUT_PARAMETER.code, e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch(Exception e){
			LogManager.getLogger().error(MODULE, "Update subscriber profile operation failed for Subscriber ID : " 
                                        		+ (subscriberID != null ? subscriberID : request.getAlternateId()) 
                                        		+ ".Reason:"+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			subscriberProvisioningResponse = new SubscriberProvisioningResponse(ResultCode.INTERNAL_ERROR.code,"Failure", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			return subscriberProvisioningResponse;
		}

		return subscriberProvisioningResponse;
	}


	private void postUpdate(String subscriberID, Integer updateAction, ResultCode resultcode) {
		
		if (resultcode == ResultCode.SUCCESS) {
			try {
				ReAuthUtil.doReAuthBySubscriberId(SubscriberDAO.getInstance().getStrippedSubscriberIdentity(subscriberID), updateAction);
			} catch (OperationFailedException e) {
				getLogger().error(MODULE, "Error while performing re-auth for subscriber Id:" + subscriberID + ". Reason: " + e.getMessage());
				if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
		}
	}
	}

	public SubscriberProvisioningResponse deleteSubscriberProfile(DeleteSubscriberWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.preDeleteSubscriberProfile(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberID(), request.getAlternateId())+" . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		SubscriberProvisioningResponse response = doDeleteSubscriberProfile(request);
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			groovyScript.postDeleteSubscriberProfile(request, response);
		}
		
		applyResponseInterceptors(response);
		return response;
	}
	
	public SubscriberProvisioningResponse deleteSubscriberProfiles(DeleteSubscribersWSRequest request) {
		
		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.preDeleteSubscribers(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		SubscriberProvisioningResponse response = doDeleteSubscriberProfiles(request);
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.postDeleteSubscribers(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private SubscriberProvisioningResponse doDeleteSubscriberProfile(DeleteSubscriberWSRequest request) {
		
		String subscriberID = request.getSubscriberID();
		String requestIpAddress = request.getRequestIpAddress();
		
		try{
			int result;

			if(Strings.isNullOrBlank(subscriberID)){
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE,"Subscriber ID not received");
				}
				String alternateId = request.getAlternateId();
				
				if (Strings.isNullOrBlank(alternateId)) {
					LogManager.getLogger().error(MODULE, "Unable to delete subscriber profile. Reason: Identity parameter missing");
					return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				
				result = SubscriberDAO.getInstance().markedForDeletionByAlternateId(alternateId, adminStaff, requestIpAddress);
			} else {
				
				result = SubscriberDAO.getInstance().markedForDeletion(subscriberID, adminStaff, requestIpAddress);
			}
			
		    ResultCode resultCode = getResultCode(result);
            return new SubscriberProvisioningResponse(resultCode.code,resultCode.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch(OperationFailedException e){
			LogManager.getLogger().error(MODULE, "Delete subscriber profile operation failed for subscriber Identity "+subscriberID+", Reason:"+e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return new SubscriberProvisioningResponse(e.getErrorCode().code,e.getErrorCode().name+" Reason: "+e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Failed to delete subscriber profile. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
            return new SubscriberProvisioningResponse(ResultCode.INTERNAL_ERROR.code, "Failure", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}

	private SubscriberProvisioningResponse doDeleteSubscriberProfiles(DeleteSubscribersWSRequest request) {
		List<String> subscriberIdentities = request.getSubscriberIds();
		String requestIpAddress = request.getRequestIpAddress();
		
		try {
			
			Map<String,Integer> responseMap;

			if(Collectionz.isNullOrEmpty(subscriberIdentities)){
				
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE,"Subscriber IDs not received");
				}
				
				List<String> alternateIds = request.getAlternateIds();
				
				if (Collectionz.isNullOrEmpty(alternateIds)) {
					LogManager.getLogger().error(MODULE, "Unable to delete Subscriber Profiles. Reason: Identity parameter missing");
					return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				
				responseMap = SubscriberDAO.getInstance().markedForDeletionByAlternateId(alternateIds, adminStaff, requestIpAddress);
			} else {
				responseMap = SubscriberDAO.getInstance().markedForDeletion(subscriberIdentities, adminStaff, requestIpAddress);
			}

			List<SubscriberProfile> subscriberProfiles = new ArrayList<SubscriberProfile>();
			if (Maps.isNullOrEmpty(responseMap)) {
				return new SubscriberProvisioningResponse(ResultCode.NOT_FOUND.code, ResultCode.NOT_FOUND.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
			
			for(java.util.Map.Entry<String, Integer> entry : responseMap.entrySet()){
				subscriberProfiles.add(createSubscriberProfile(entry.getKey(),getResultCode(entry.getValue())));
			}
            return new SubscriberProvisioningResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, subscriberProfiles, null,null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch(OperationFailedException e){
			LogManager.getLogger().error(MODULE, "Error while deleting subscribers. Reason: "+e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
            return new SubscriberProvisioningResponse(e.getErrorCode().code, e.getErrorCode().name+ ". Reason: "+e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while deleting subscribers. Reason: "+e.getMessage());
			LogManager.getLogger().trace(e);
            return new SubscriberProvisioningResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}	

	public SubscriberProvisioningResponse getSubscriberByID(GetSubscriberWSRequest request) {
		
		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.preGetSubscriberProfileByID(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberId(), request.getAlternateId())+" . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		SubscriberProvisioningResponse response = doGetSubscriberByID(request);
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.postGetSubscriberProfileByID(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberId(), request.getAlternateId())+" . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}


	private SubscriberProvisioningResponse doGetSubscriberByID(GetSubscriberWSRequest request) {

		String subscriberID = request.getSubscriberId();
		
		try {
			SPRInfo  sprInfo;
			if (Strings.isNullOrBlank(subscriberID)) {
				
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE,"Subscriber ID not received");
				}
				
				String alternateId = request.getAlternateId();
				
				if (Strings.isNullOrBlank(alternateId)) {
					LogManager.getLogger().error(MODULE, "Unable to fetch subscriber profile. Reason: Identity parameter missing");
					return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				
				sprInfo = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateId, adminStaff);			
			} else {
				sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberID, adminStaff);
			}
			
			if(sprInfo == null){
				LogManager.getLogger().error(MODULE,"Subscriber Profile not found with subscriber id: "+subscriberID);
				return new SubscriberProvisioningResponse(ResultCode.NOT_FOUND.code, "Subscriber Not found", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}

			SubscriberProfile subscriberProfile = getSubscriberProfile(sprInfo);
			List<SubscriberProfile> subscriberProfiles = new ArrayList<SubscriberProfile>(1);
			subscriberProfiles.add(subscriberProfile);
			return new SubscriberProvisioningResponse(ResultCode.SUCCESS.code, "SUCCESS", subscriberProfiles, null, null, request.getWebServiceName(), request.getWebServiceMethodName());

		}catch(OperationFailedException e){
			LogManager.getLogger().error(MODULE, "Failed to fetch subscriber profile for subscriber Identity "+subscriberID+", Reason: "+e.getMessage());
			
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			
			return new SubscriberProvisioningResponse(e.getErrorCode().code,e.getErrorCode().name+" Reason: "+e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to fetch subscriber profile for Subscriber ID: " + subscriberID+". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return new SubscriberProvisioningResponse(ResultCode.INTERNAL_ERROR.code, "Failure", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}
	

	public SubscriberProvisioningResponse addSubscriberProfiles(AddBulkSubscriberWSRequest request) {
		
		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.preAddSubscriberBulk(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		SubscriberProvisioningResponse response = doAddSubscriberProfiles(request);
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.postAddSubscriberBulk(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}


	private SubscriberProvisioningResponse doAddSubscriberProfiles(AddBulkSubscriberWSRequest request) {
		
		List<SubscriberProfile> subscriberProfiles = request.getSubscriberProfiles();
		
		if(Collectionz.isNullOrEmpty(subscriberProfiles)){
			LogManager.getLogger().error(MODULE, "Unable to add Subscriber Profiles. Reason: subscriber profile is NULL");
            return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Subscriber profiles not received", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Total: " + subscriberProfiles.size() + " Subscriber Profile received");
		}
		int totalSubscriberProfilesAdded = 0;
		int totalSubscriberProfilesFailes = 0;
		List<SubscriberProfile> subscriberProfileResponse = new ArrayList<SubscriberProfile>();
		for(SubscriberProfile subscriberProfile : subscriberProfiles){
		
			SubscriberProvisioningResponse subscriberProvisioningResponse = doAddSubscriberProfile(subscriberProfile, request.getWebServiceName(), request.getWebServiceMethodName(), request.getRequestIpAddress());
			if (ResultCode.SUCCESS.code == subscriberProvisioningResponse.getResponseCode()) {
				subscriberProfileResponse.add(createSubscriberProfile(subscriberProfile.getSubscriberIdentity(), ResultCode.SUCCESS));
				totalSubscriberProfilesAdded++;
			} else {
				subscriberProfileResponse.add(createSubscriberProfile(subscriberProfile.getSubscriberIdentity(), ResultCode.fromVal(subscriberProvisioningResponse.getResponseCode())));
				totalSubscriberProfilesFailes++;
			}
		} 
		String responseMessage = "Total Subcriber Profiles added: " + totalSubscriberProfilesAdded; 
		if(totalSubscriberProfilesFailes > 0){
			responseMessage +=  ", Total Subcriber Profiles failed: " + totalSubscriberProfilesFailes;
		}
		int result = ResultCode.SUCCESS.code;
		if(totalSubscriberProfilesAdded == 0){
			result = ResultCode.INTERNAL_ERROR.code;
		}
        return new SubscriberProvisioningResponse(result, responseMessage, subscriberProfileResponse, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
	}
	
	public SubscriberProvisioningResponse addSubscribers(AddSubscriberProfileBulkWSRequest request) {
		
		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.preAddSubscriberProfileBulk(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		SubscriberProvisioningResponse response = doAddSubscribers(request);
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.postAddSubscriberProfileBulk(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		applyResponseInterceptors(response);
		return response;
	}


	private SubscriberProvisioningResponse doAddSubscribers(AddSubscriberProfileBulkWSRequest request) {
		

		List<SubscriberProfileData> subscriberProfiles = request.getSubscriberProfiles();
		
		if (Collectionz.isNullOrEmpty(subscriberProfiles)) {
			LogManager.getLogger().error(MODULE, "Unable to add Subscriber Profiles. Reason: subscriber profiles is NULL");
            return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Subscriber profiles not received", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Total: " + subscriberProfiles.size() + " Subscriber Profile received");
		}
		int totalSubscriberProfilesAdded = 0;
		int totalSubscriberProfilesFailes = 0;
		List<SubscriberProfile> subscriberProfileResponse = new ArrayList<SubscriberProfile>();
		for(SubscriberProfileData subscriberProfile : subscriberProfiles){

			/**
			 * @author Prakashkumar Pala
			 * @since 22-Oct-2018
			 * for NETVERTEX-3068 - START
			 */
			SubscriberProvisioningResponse subscriberProvisioningResponse = doAddSubscriber(subscriberProfile, request.getWebServiceName()
					, request.getWebServiceMethodName(), request.getRequestIpAddress());
			/**
			 * for NETVERTEX-3068 - END
			 */
			
			if (ResultCode.SUCCESS.code == subscriberProvisioningResponse.getResponseCode()) {
				subscriberProfileResponse.add(createSubscriberProfile(subscriberProfile.getSubscriberIdentity(), ResultCode.SUCCESS));
				totalSubscriberProfilesAdded++;
			} else {
				subscriberProfileResponse.add(createSubscriberProfile(subscriberProfile.getSubscriberIdentity(), ResultCode.fromVal(subscriberProvisioningResponse.getResponseCode())));
				totalSubscriberProfilesFailes++;
			}
		
		} 
		String responseMessage = "Total Subcriber Profiles added: " + totalSubscriberProfilesAdded; 
		if(totalSubscriberProfilesFailes > 0){
			responseMessage +=  ", Total Subcriber Profiles failed: " + totalSubscriberProfilesFailes;
		}
		int result = ResultCode.SUCCESS.code;
		if(totalSubscriberProfilesAdded == 0){
			result = ResultCode.INTERNAL_ERROR.code;
		}
        return new SubscriberProvisioningResponse(result, responseMessage, subscriberProfileResponse, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
	}
	
	public SubscriberProvisioningResponse migrateSubscriber(MigrateSubscriberWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.preMigrateSubscriber(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() +". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		SubscriberProvisioningResponse response = doMigrateSubscriber(request);
		
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}
		
			try {
				groovyScript.postMigrateSubscriber(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		applyResponseInterceptors(response);
		return response;
	}
	
	private SubscriberProvisioningResponse doMigrateSubscriber(MigrateSubscriberWSRequest request) {

		String currentSubscriberIdentity = request.getCurrentSubscriberIdentity();
		String requestIpAddress = request.getRequestIpAddress();
		
		if (Strings.isNullOrBlank(currentSubscriberIdentity)) {
			LogManager.getLogger().error(MODULE, "Unable to change subscriber identity. Reason: Missing subscriber identity");
			return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "Missing subscriber identity", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		
		String newSubscriberIdentity = request.getNewSubscriberIdentity();
		if (Strings.isNullOrBlank(newSubscriberIdentity)) {
			LogManager.getLogger().error(MODULE, "Unable to change subscriber identity. Reason: Missing new subscriber identity");
			return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "Missing new subscriber identity", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		
		try {
			SubscriberDAO.getInstance().migrateSubscriber(currentSubscriberIdentity, newSubscriberIdentity, adminStaff, requestIpAddress);
			return new SubscriberProvisioningResponse(ResultCode.SUCCESS.code, "SUCCESS", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (OperationFailedException e) {
			LogManager.getLogger().error(MODULE, "Unable to change subscriber identity. Reason: "+e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return new SubscriberProvisioningResponse(e.getErrorCode().code,e.getErrorCode().name+" Reason: "+e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Unable to change subscriber identity. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return new SubscriberProvisioningResponse(ResultCode.INTERNAL_ERROR.code, "Failure. Reason: " + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}

	private ResultCode getResultCode(int result) {
		if(result > 0 ){
			return  ResultCode.SUCCESS;
		}else{
			return ResultCode.NOT_FOUND;
		}
	}

	
	private SubscriberProfile createSubscriberProfile(String key, ResultCode value) {
		List<Entry> entries = new ArrayList<Entry>(1);
		entries.add(new Entry(key,value.name));
		return new SubscriberProfile(entries);
	}

	
	private EnumMap<SPRFields, String> createSPRFieldMap(SubscriberProfile subscriberProfile) throws ParseException, OperationFailedException {

		StringBuilder invalidKeys = new StringBuilder();
		EnumMap<SPRFields, String> sprFieldMap = new EnumMap<SPRFields, String>(SPRFields.class);
		
		
		for (Entry entry : subscriberProfile.getEntry()) {
			SPRFields sprField = SPRFields.fromSPRFields(entry.getKey());
			if (sprField == null) {
				invalidKeys.append(entry.getKey()).append(',');
				continue;
			}
			if (Strings.isNullOrBlank(entry.getValue()) && SPRFields.CREATED_DATE.name().equals(sprField.columnName) == false) {
				sprFieldMap.put(sprField, null);
			} else {
				if(sprField.type == Types.VARCHAR){
					sprField.validateStringValue(entry.getValue());
					sprFieldMap.put(sprField, entry.getValue().trim());
				}else if(sprField.type == Types.NUMERIC){
					sprField.validateNumericValue(parseLong(entry.getValue(), null));
					sprFieldMap.put(sprField, entry.getValue().trim());
				}else if(sprField.type == Types.TIMESTAMP){
					if (SPRFields.CREATED_DATE.name().equals(sprField.columnName) || SPRFields.MODIFIED_DATE.name().equals(sprField.columnName)) {
						continue;
					}else{
						Timestamp dateToTimestamp = getTimestampValue(entry.getValue().trim());
						sprField.validateTimeStampValue(dateToTimestamp);
						sprFieldMap.put(sprField, entry.getValue().trim());
					}
				}

			}
		}
		if(invalidKeys.length() > 0) {
			throw new ParseException("Invalid keys("+ invalidKeys.deleteCharAt(invalidKeys.length()-1).toString()+") received",0);
		}
		
		return sprFieldMap;
	}
	//FIXME add argument to provide SPRInfo. --ishani.bhatt 
	//FIXME Change method name --ishani.bhatt
	private SPRInfoImpl createSPRInfo(SubscriberProfile subscriberProfile) throws ParseException, OperationFailedException {

		SPRInfoImpl sprInfo = new SPRInfoImpl();
		Timestamp timestampValue=null;
		boolean validate = true;
		StringBuilder invalidKeys = new StringBuilder();
		for(Entry entry : subscriberProfile.getEntry()){
			
			SPRFields sprField =  SPRFields.fromSPRFields(entry.getKey());
			if (sprField == null) {
				invalidKeys.append(entry.getKey()).append(',');
				continue;
			}

			if(Strings.isNullOrBlank(entry.getValue())){
				LogManager.getLogger().warn(MODULE, "Skipping adding value for key " + entry.getKey() + ". Reason: Value not found");
				continue;
			}

			if (sprField.type == Types.NUMERIC) {
				sprField.setNumericValue(sprInfo,parseLong(entry.getValue().trim(),null), validate);
				
			} else if (sprField.type == Types.VARCHAR) {
				sprField.setStringValue(sprInfo,entry.getValue().trim(), validate);
				
			} else if (sprField.type == Types.TIMESTAMP) {
				if (SPRFields.CREATED_DATE.name().equals(sprField.columnName) || SPRFields.MODIFIED_DATE.name().equals(sprField.columnName)) {
				    continue;
				}else{
					timestampValue = getTimestampValue(entry.getValue().trim());
					if (timestampValue != null) {
						sprField.setTimestampValue(sprInfo, timestampValue, validate);
					}
				}
			}
		}
		
		
		if (invalidKeys.length() > 0) {
			throw new ParseException("Invalid keys(" + invalidKeys.deleteCharAt(invalidKeys.length() - 1).toString() + ") received", 0);
		}
		return sprInfo;
	}
	

	private SubscriberProfile getSubscriberProfile(SPRInfo sprInfo){
		List<Entry> entries = Collectionz.newArrayList();
		SubscriberProfile subscriberProfile = new SubscriberProfile();

		for (SPRFields sprField : SPRFields.values()) {
			Entry entry = new Entry();
			entry.setKey(sprField.name());
			if (sprField.type == Types.TIMESTAMP) {
				Long timeInMillies = sprField.getNumericValue(sprInfo);
				if (timeInMillies != null) {
					entry.setValue(sprField.getNumericValue(sprInfo).toString());
				}
			} else {
				entry.setValue(sprField.getStringValue(sprInfo));
			}
			entries.add(entry);
		}
		
		subscriberProfile.setEntry(entries);
		return subscriberProfile;

	}

	private Long parseLong(String value, Long otherwise) throws OperationFailedException {
		Long result = otherwise;
		try {
			result = Long.parseLong(value);
		} catch (NumberFormatException nfe) {
			LogManager.getLogger().trace(nfe);
			throw new OperationFailedException("Error while converting "+ value +" to Long",ResultCode.INVALID_INPUT_PARAMETER);
		}
		return result;
	}

	private Timestamp getTimestampValue(String expiryDate) throws OperationFailedException {
		Timestamp dateToTimestamp = null;
		Long longExpDate;
		if(Strings.isNullOrEmpty(expiryDate)==false){				
			try{
				longExpDate = Long.parseLong(expiryDate);
				dateToTimestamp = new Timestamp(longExpDate);
				
			}catch(NumberFormatException e){
				LogManager.getLogger().error(MODULE, "Error while converting "+ expiryDate +" to timestamp");
				throw new OperationFailedException("Error while converting "+ expiryDate +" to timestamp",ResultCode.INVALID_INPUT_PARAMETER);
			}catch(Exception e){
				LogManager.getLogger().error(MODULE, "Error while converting "+ expiryDate +" to timestamp");
				throw new OperationFailedException("Error while converting "+ expiryDate +" to timestamp",ResultCode.INVALID_INPUT_PARAMETER);
			}			
		}
		return dateToTimestamp;	
	}
	
	private String getMessage(String subscriberId, String alternateId){
		StringBuilder sb = new StringBuilder();
		sb.append(" for subscriber Identity: "+subscriberId+" and alternate Identity: " + alternateId);
		return sb.toString();
	}
	private String getMessage(String subscriberId){
		StringBuilder sb = new StringBuilder();
		sb.append(" for subscriber Identity: "+subscriberId);
		return sb.toString();
	}
	
	private void applyRequestInterceptors(WebServiceRequest request) {
		for(WebServiceInterceptor interceptor : interceptors){
			interceptor.requestReceived(request);
		}
	}
	
	private void applyResponseInterceptors(WebServiceResponse response) {
		for(WebServiceInterceptor interceptor : interceptors){
			interceptor.responseReceived(response);
		}
	}

    public SubscriberProvisioningResponse changeBaseProductOffer(ChangeBaseProductOfferWSRequest request) {
	    this.changeBaseProductOfferProcessor.preProcess(request);
        SubscriberProvisioningResponse response = this.changeBaseProductOfferProcessor.process(request, adminStaff);
        this.changeBaseProductOfferProcessor.postProcess(request, response);
        return response;
    }

	public AlternateIdProvisioningResponse addAlternateId(AddAlternateIdWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preAddAlternateId(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberId(), request.getAlternateId())+" . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		AlternateIdProvisioningResponse response = doAddAlternateId(request);

		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postAddAlternateId(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberId(), request.getAlternateId())+" . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}



	public AlternateIdProvisioningResponse doAddAlternateId(AddAlternateIdWSRequest request){
	    try {
            if (StringUtils.isEmpty(request.getSubscriberId())) {
                return new AlternateIdProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                        ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: subscriber id not received"
                        , null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }
            if (StringUtils.isEmpty(request.getAlternateId())) {
                return new AlternateIdProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                        ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: alternate id not received"
                        , null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }

            if (StringUtils.length(request.getAlternateId())>255) {
                return new AlternateIdProvisioningResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
                        ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: maximum length of alternate id can be 255"
                        , null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }

            SubscriberDAO.getInstance().addExternalAlternateIdentity(request.getSubscriberId(), request.getAlternateId(),adminStaff);
            return new AlternateIdProvisioningResponse(ResultCode.SUCCESS.code,ResultCode.SUCCESS.name
                    , null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());

        }catch (OperationFailedException e){
            LogManager.getLogger().error(MODULE, "Unable to add alternate id for subscriber identity "+request.getSubscriberId()+". Reason: "+e.getMessage());
            if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                getLogger().trace(MODULE, e);
            }
            return new AlternateIdProvisioningResponse(e.getErrorCode().code,e.getErrorCode().name+" Reason: "+e.getMessage(), null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }catch (Exception e){
            LogManager.getLogger().error(MODULE, "Unable to add alternate id for subscriber identity "+request.getSubscriberId()+". Reason: "+e.getMessage());
            getLogger().trace(MODULE,e);
            return new AlternateIdProvisioningResponse(ResultCode.INTERNAL_ERROR.code,ResultCode.INTERNAL_ERROR.name+" Reason: "+e.getMessage(), null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }
	}

	public AlternateIdProvisioningResponse updateAlternateId(UpdateAlternateIdWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preUpdateAlternateId(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberId(), request.getCurrentAlternateId())+" . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		AlternateIdProvisioningResponse response = doUpdateAlternateId(request);

		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postUpdateAlternateId(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberId(), request.getCurrentAlternateId())+" . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}



	public AlternateIdProvisioningResponse doUpdateAlternateId(UpdateAlternateIdWSRequest request) {
        try {
            if (StringUtils.isEmpty(request.getSubscriberId())) {
                return new AlternateIdProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                        ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: subscriber id not received"
                        , null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }
            if (StringUtils.isEmpty(request.getCurrentAlternateId())) {
                return new AlternateIdProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                        ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: current alternate id not received"
                        , null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }
            if (StringUtils.isEmpty(request.getNewAlternateId())) {
                return new AlternateIdProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                        ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: new alternate id not received"
                        , null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }
            if (StringUtils.length(request.getNewAlternateId())>255) {
                return new AlternateIdProvisioningResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
                        ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: maximum length of new alternate id can be 255"
                        , null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }

			if (StringUtils.equals(request.getCurrentAlternateId(),request.getNewAlternateId())) {
				return new AlternateIdProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
						ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: current & new alternate id can't be same"
						, null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}

            int result = SubscriberDAO.getInstance().updateExternalAlternateIdentity(request.getSubscriberId(), request.getCurrentAlternateId(), request.getNewAlternateId(),adminStaff);
            ResultCode resultCode = getResultCode(result);
            if(ResultCode.SUCCESS == resultCode){
                    getAlternateIdOperationUtils().removeAlternateIdFromCache(request.getCurrentAlternateId());
			}
            return new AlternateIdProvisioningResponse(resultCode.code,resultCode.name, null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }catch (OperationFailedException e){
            LogManager.getLogger().error(MODULE, "Unable to update alternate id for subscriber identity "+request.getSubscriberId()+". Reason: "+e.getMessage());
            if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                getLogger().trace(MODULE, e);
            }
            return new AlternateIdProvisioningResponse(e.getErrorCode().code,e.getErrorCode().name+" Reason: "+e.getMessage(), null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }catch (Exception e){
            LogManager.getLogger().error(MODULE, "Unable to update alternate id"+request.getCurrentAlternateId()+" for subscriber identity "+request.getSubscriberId()+". Reason: "+e.getMessage());
            getLogger().trace(MODULE,e);
            return new AlternateIdProvisioningResponse(ResultCode.INTERNAL_ERROR.code,ResultCode.INTERNAL_ERROR.name+" Reason: "+e.getMessage(), null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }
    }

	public AlternateIdProvisioningResponse deleteAlternateId(AddAlternateIdWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preDeleteAlternateId(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberId(), request.getAlternateId())+" . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		AlternateIdProvisioningResponse response = doDeleteAlternateId(request);

		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postDeleteAlternateId(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberId(), request.getAlternateId())+" . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	public AlternateIdProvisioningResponse doDeleteAlternateId(AddAlternateIdWSRequest request) {
        try {
            if (StringUtils.isEmpty(request.getSubscriberId())) {
                return new AlternateIdProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                        ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: subscriber id not received"
                        , null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }
            if (StringUtils.isEmpty(request.getAlternateId())) {
                return new AlternateIdProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                        ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: current alternate id not received"
                        , null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }

            int result = SubscriberDAO.getInstance().removeExternalAlternateIdentity(request.getSubscriberId(), request.getAlternateId(),adminStaff);
            ResultCode resultCode = getResultCode(result);
            if(ResultCode.SUCCESS == resultCode){
            	getAlternateIdOperationUtils().removeAlternateIdFromCache(request.getAlternateId());
			}
            return new AlternateIdProvisioningResponse(resultCode.code,resultCode.name, null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }catch (OperationFailedException e){
            LogManager.getLogger().error(MODULE, "Unable to delete alternate id "+request.getAlternateId()+" for subscriber identity "+request.getSubscriberId()+". Reason: "+e.getMessage());
            if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                getLogger().trace(MODULE, e);
            }
            return new AlternateIdProvisioningResponse(e.getErrorCode().code,e.getErrorCode().name+" Reason: "+e.getMessage(), null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }catch (Exception e){
            LogManager.getLogger().error(MODULE, "Unable to delete alternate id "+request.getAlternateId()+" for subscriber identity "+request.getSubscriberId()+". Reason: "+e.getMessage());
            getLogger().trace(MODULE,e);
            return new AlternateIdProvisioningResponse(ResultCode.INTERNAL_ERROR.code,ResultCode.INTERNAL_ERROR.name+" Reason: "+e.getMessage(), null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }
	}

	public AlternateIdProvisioningResponse changeAlternateIdStatus(UpdateAlternateIdWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preChangeAlternateIdStatus(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberId(), request.getCurrentAlternateId())+" . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		AlternateIdProvisioningResponse response = doChangeAlternateIdStatus(request);

		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postChangeAlternateIdStatus(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberId(), request.getCurrentAlternateId())+" . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	public AlternateIdProvisioningResponse doChangeAlternateIdStatus(UpdateAlternateIdWSRequest request) {
        try {
            if (StringUtils.isEmpty(request.getSubscriberId())) {
                return new AlternateIdProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                        ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: subscriber id not received"
                        , null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }
            if (StringUtils.isEmpty(request.getCurrentAlternateId())) {
                return new AlternateIdProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                        ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: current alternate id not received"
                        , null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }
            if(isValidStatus(request.getStatus()) == false){
				return new AlternateIdProvisioningResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
						ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: invalid status value "+request.getStatus()+" received,possible value are (ACTIVE/INACTIVE)"
						, null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}

            int result = SubscriberDAO.getInstance().changingExternalAlternateIdentityStatus(request.getSubscriberId(), request.getCurrentAlternateId(), request.getStatus().toUpperCase(),adminStaff);
            ResultCode resultCode = getResultCode(result);
			if(CommonConstants.STATUS_INACTIVE.equalsIgnoreCase(request.getStatus()) && (ResultCode.SUCCESS == resultCode)){
				getAlternateIdOperationUtils().removeAlternateIdFromCache(request.getCurrentAlternateId());
			}
            return new AlternateIdProvisioningResponse(resultCode.code,resultCode.name, null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }catch (OperationFailedException e){
            LogManager.getLogger().error(MODULE, "Unable to update alternate id "+request.getCurrentAlternateId()+" status for subscriber identity "+request.getSubscriberId()+". Reason: "+e.getMessage());
            if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                getLogger().trace(MODULE, e);
            }
            return new AlternateIdProvisioningResponse(e.getErrorCode().code,e.getErrorCode().name+" Reason: "+e.getMessage(), null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }catch (Exception e){
            LogManager.getLogger().error(MODULE, "Unable to update alternate id "+request.getCurrentAlternateId()+" for subscriber identity "+request.getSubscriberId()+". Reason: "+e.getMessage());
            getLogger().trace(MODULE,e);
            return new AlternateIdProvisioningResponse(ResultCode.INTERNAL_ERROR.code,ResultCode.INTERNAL_ERROR.name+" Reason: "+e.getMessage(), null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }

	}

	private boolean isValidStatus(String status) {
		if(StringUtils.isEmpty(status)){
			return false;
		}
		return status.equalsIgnoreCase(CommonConstants.STATUS_ACTIVE) || status.equalsIgnoreCase(CommonConstants.STATUS_INACTIVE);

	}

	public AlternateIdProvisioningResponse getAlternateIds(GetAlternateIdWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts = GroovyManager.getInstance().getSubscriberProvisioningGroovyScripts();
		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preGetAlternateIds(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberId())+" . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		AlternateIdProvisioningResponse response = doGetAlternateIds(request);

		for (SubscriberProvisioningWsScript groovyScript : subscriberProvisioningGroovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postGetAlternateIds(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + getMessage(request.getSubscriberId())+" . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	public AlternateIdProvisioningResponse doGetAlternateIds(GetAlternateIdWSRequest request) {
        try {
            if (StringUtils.isEmpty(request.getSubscriberId())) {
                return new AlternateIdProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                        ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: subscriber id not received"
                        , null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }

            SubscriberAlternateIds externalAlternateIds = SubscriberDAO.getInstance().getExternalAlternateIds(request.getSubscriberId(),adminStaff);
            if(Objects.isNull(externalAlternateIds)){
                return new AlternateIdProvisioningResponse(ResultCode.NOT_FOUND.code,"No alternate Ids found", null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }
            List<Entry> alternateIds = createAlternateIds(externalAlternateIds.getSubscriberAlternateIdStatusList());
            return new AlternateIdProvisioningResponse(ResultCode.SUCCESS.code,ResultCode.SUCCESS.name, request.getSubscriberId(),alternateIds,null,null,request.getWebServiceName(),request.getWebServiceMethodName());

        }catch (OperationFailedException e){
            LogManager.getLogger().error(MODULE, "Unable to get alternate ids for subscriber identity "+request.getSubscriberId()+". Reason: "+e.getMessage());
            if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                getLogger().trace(MODULE, e);
            }
            return new AlternateIdProvisioningResponse(e.getErrorCode().code,e.getErrorCode().name+" Reason: "+e.getMessage(), null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }catch (Exception e){
            LogManager.getLogger().error(MODULE, "Unable to get alternate ids for subscriber identity "+request.getSubscriberId()+". Reason: "+e.getMessage());
            getLogger().trace(MODULE,e);
            return new AlternateIdProvisioningResponse(ResultCode.INTERNAL_ERROR.code,ResultCode.INTERNAL_ERROR.name+" Reason: "+e.getMessage(), null, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }
	}

    private List<Entry> createAlternateIds(List<SubscriberAlternateIdStatusDetail> alternateIdStatusDetailList) {
	    if(CollectionUtils.isEmpty(alternateIdStatusDetailList)) {
            return null;
        }
        List<Entry> alternateIdStatusWiseList = new ArrayList<>();
        for(SubscriberAlternateIdStatusDetail entry:alternateIdStatusDetailList) {
	          if(AlternateIdType.EXTERNAL.name().equalsIgnoreCase(entry.getType())){
				  alternateIdStatusWiseList.add(new Entry(entry.getAlternateId(),entry.getStatus()));
			  }
        }
        return alternateIdStatusWiseList;
    }

    private AlternateIdOperationUtils getAlternateIdOperationUtils(){
		return DefaultNVSMXContext.getContext().getAlternateIdOperationUtils();
	}
}