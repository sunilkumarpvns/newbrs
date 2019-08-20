package com.elitecore.nvsmx.ws.resetusage.blmanager;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.policydesigner.model.subscriber.SubscriberDAO;
import com.elitecore.nvsmx.system.groovy.GroovyManager;
import com.elitecore.nvsmx.system.groovy.ResetUsageWsScript;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextWebServiceInterceptor;
import com.elitecore.nvsmx.ws.interceptor.WebServiceAuditInterceptor;
import com.elitecore.nvsmx.ws.interceptor.WebServiceInterceptor;
import com.elitecore.nvsmx.ws.interceptor.WebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatisticsManager;
import com.elitecore.nvsmx.ws.resetusage.request.ResetBillingCycleWSRequest;
import com.elitecore.nvsmx.ws.resetusage.response.ResetBillingCycleResponse;

public class ResetUsageWSBLManager {
	
	private static final String MODULE = "RESET-USAGE-WSBL-MNGR";
	private StaffData adminStaff;
	private List<WebServiceInterceptor> interceptors = new ArrayList<WebServiceInterceptor>();
	public ResetUsageWSBLManager() {
		adminStaff = new StaffData();
		adminStaff.setUserName("admin");
		interceptors.add(WebServiceStatisticsManager.getInstance());
		interceptors.add(WebServiceAuditInterceptor.getInstance());
		interceptors.add(DiagnosticContextWebServiceInterceptor.getInstance());
	}
	
	public ResetBillingCycleResponse resetBillingCycle(ResetBillingCycleWSRequest request){
		
		applyRequestInterceptors(request);
		List<ResetUsageWsScript> groovyScripts = GroovyManager.getInstance().getResetUsageGroovyScripts();
		for (ResetUsageWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preResetBillingCycle(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		ResetBillingCycleResponse response = resetBillingCycleInfo(request);
		
		for (ResetUsageWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postpreResetBillingCycle(request, response);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		applyResponseInterceptors(response);
		
		return response;
	}

	private ResetBillingCycleResponse resetBillingCycleInfo(ResetBillingCycleWSRequest request) {
		if (Strings.isNullOrBlank(request.getSubscriberID())) {
			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE,"Subscriber ID not received");
			}
			
			String alternateId = request.getAlternateID();
			
			if (Strings.isNullOrBlank(alternateId)) {
				getLogger().error(MODULE,  "Unable to reset billing cycle for subscriber. Reason: Identity parameter missing");
				int resultCode = ResultCode.INPUT_PARAMETER_MISSING.code;
				return new ResetBillingCycleResponse(resultCode, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
					
		}
		
		if(Strings.isNullOrBlank(request.getProductOfferName())){
			getLogger().error(MODULE, "Unable to reset billing cycle for subscriber. Reason: Product Offer Name is not received");
			int resultCode = ResultCode.INPUT_PARAMETER_MISSING.code;
			return new ResetBillingCycleResponse(resultCode, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Package Name not received", null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		}
		
		try {
			ProductOffer productOffer = null;
			if(Strings.isNullOrBlank(request.getProductOfferName()) == false){
				productOffer = DefaultNVSMXContext.getContext().getPolicyRepository().getProductOffer().byName(request.getProductOfferName());
			}
			
			if(productOffer == null){
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Unable to reset usage for Identity Parameter: " + request.getSubscriberID() == null ? request.getAlternateID() : request.getSubscriberID() + 
						". Reason: Product Offer: " + request.getProductOfferName() + " does not exist");
				}
				int resultCode = ResultCode.INVALID_INPUT_PARAMETER.code;
				return new ResetBillingCycleResponse(resultCode,
						"Product Offer : " + request.getProductOfferName() + " does not exist" , null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
			
			if(productOffer.getPolicyStatus() == PolicyStatus.FAILURE){
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Unable to reset usage for Identity Parameter: " + request.getSubscriberID() == null ? request.getAlternateID() : request.getSubscriberID() + 
						". Reason: Product Offer: " + request.getProductOfferName() + " is failed product Offer");
				}
				int resultCode = ResultCode.INVALID_INPUT_PARAMETER.code;
				return new ResetBillingCycleResponse(resultCode,
						"Product Offer : " + request.getProductOfferName() + " is failed product Offer" , null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
			
			boolean isRealTimeUsageReset = false;
		
			if(request.getResetBillingCycleDate() == null || request.getResetBillingCycleDate()<=0){
				isRealTimeUsageReset = true;
			} else {
			
				Timestamp billingCycleDate = new Timestamp(request.getResetBillingCycleDate());
				Timestamp currentTime = new Timestamp(System.currentTimeMillis());
				
				if(billingCycleDate.before(currentTime)){
					isRealTimeUsageReset = true;
				}
			}
			
			if(Strings.isNullOrBlank(request.getSubscriberID())){
				if (isRealTimeUsageReset) {
					SubscriberDAO.getInstance().resetAllUsageByAlternateId(request.getAlternateID(), productOffer.getId());
				} else {
					SubscriberDAO.getInstance().resetBillingCycleByAlternateID(request.getAlternateID(), productOffer.getId(), request.getResetBillingCycleDate(), request.getResetReason(), request.getParameter1(), request.getParameter2(), request.getParameter3());
				}
			}else{
				if (isRealTimeUsageReset) {
					SubscriberDAO.getInstance().resetAllUsageBySubscriberId(request.getSubscriberID(), productOffer.getId());
				} else {
					SubscriberDAO.getInstance().resetBillingCycleBySubscriberID(request.getSubscriberID(), request.getAlternateID(), productOffer.getId(), request.getResetBillingCycleDate(), request.getResetReason(), request.getParameter1(), request.getParameter2(), request.getParameter3());
				}
			}
			
			int resultCode = ResultCode.SUCCESS.code;
			return new ResetBillingCycleResponse(resultCode,
					"Success" , null, null, request.getWebServiceName(), request.getWebServiceMethodName());

		
		}catch(OperationFailedException oe) {
			getLogger().error(MODULE, "Error while inserting usage reset information for Identity parameter: " + request.getSubscriberID() == null ? request.getAlternateID() : request.getSubscriberID() + 
					". Reason: " + oe.getMessage());
			if (ResultCode.INTERNAL_ERROR == oe.getErrorCode()) {
			getLogger().trace(MODULE, oe);
			}
			
			int resultCode = ResultCode.INTERNAL_ERROR.code;
			return new ResetBillingCycleResponse(resultCode,
					oe.getMessage() , null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch (Exception e) {
			getLogger().error(MODULE, "Error while inserting usage reset information for Identity Parameter: " + request.getSubscriberID() == null ? request.getAlternateID() : request.getSubscriberID() + 
					". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			
			int resultCode = ResultCode.INTERNAL_ERROR.code;
			return new ResetBillingCycleResponse(resultCode,
					e.getMessage() , null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
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
	
}
