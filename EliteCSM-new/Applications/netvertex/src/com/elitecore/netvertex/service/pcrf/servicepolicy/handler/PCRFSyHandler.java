package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.systemx.esix.LoadBalancerType;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.constants.SyMode;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.SyHandlerResponseListener;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.apache.logging.log4j.ThreadContext;
public class PCRFSyHandler extends ServiceHandler{
	
	private static final String MODULE = "PCRF-SY-HDLR";
	private String servicePolicyName;
	private String syGateway;
	private SyMode syMode;
	private SessionLocator sessionLocator;
	private DiameterPeerGroupParameter diameterPeerGroupParameter;
	private boolean isLicenseValid=true;

	public PCRFSyHandler(String servicePolicyName, 
			PCRFServiceContext serviceContext,
			String syGateway,
			SyMode syMode, 
			SessionLocator sessionLocator) {
		super(serviceContext);
		this.servicePolicyName = servicePolicyName;
		this.syGateway = syGateway;
		this.syMode = syMode;
		this.sessionLocator = sessionLocator;
		serviceContext.getServerContext().registerLicenseObserver(this::checkLicenseValidity);

	}

	private void checkLicenseValidity(){
		isLicenseValid
				= getServiceContext().getServerContext().isLicenseValid(LicenseNameConstants.NV_SY_INTERFACE,String.valueOf(System.currentTimeMillis()));
		if(isLicenseValid==false && LogManager.getLogger().isLogLevel(LogLevel.WARN)){
			LogManager.getLogger().warn(MODULE, " License for Sy Application is either not acquired or has expired.");
		}
	}
	
	@Override
	public void init() throws InitializationFailedException{
		if(syGateway == null){
			throw new InitializationFailedException("Sy gateway is empty");
		}
		Map<String, Integer> syGatewayNames = new LinkedHashMap<String, Integer>();
		checkLicenseValidity();
		syGatewayNames.put(syGateway, 1);
		diameterPeerGroupParameter = new DiameterPeerGroupParameter(servicePolicyName, syGatewayNames, LoadBalancerType.ROUND_ROBIN, true, 2, 0);
	}

	/* 
	 * 
	 * Request is received in PCRFSyHandler that means Sy Request should be send.
	 *
	 * 
	 * So, We check that sySessionId found for that request. 
	 * If sySessionId found that means SyRequest had been already sent.So in this scenario we only need to check following things
	 * 			1) if event is session-stop
	 * 				--> Need to send STR to sy gateway to disconnect syRequest
	 * 			
	 * 			2) if event is not sesson-stop
	 * 				--> check Sy Mode
	 * 					i) if mode == PUSH
	 * 						call diameter to get Sy counters
	 * 					ii) if mode == PULL
	 * 						send SLR for SY Counters
	 * 
	 * If sySessionId not found that means we need to send Sy Request. So we just need to check event is other than session-stop.
	 * 
	 * 
	 * In Gateway-Reboot case, we need to fetch all the session for that gateway and and send STR for each session for which we find sySessionId in session.
	 */
	
	@Override
	protected boolean isApplicable(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
		if(isLicenseValid == false){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Application: Sy" +
						" is not supported. Reason: License expired or not acquired.");
			}
			return false;
		}

		PCRFRequest pcrfRequest = (PCRFRequest) serviceRequest;
		PCRFResponse pcrfResponse = (PCRFResponse) serviceResponse;

		return (SessionTypeConstant.RX.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val)) &&
                pcrfResponse.isEmergencySession())==false;
	}

	@Override
	protected void process(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {
		PCRFRequest pcrfRequest = (PCRFRequest) serviceRequest;
		PCRFResponse pcrfResponse = (PCRFResponse) serviceResponse;
		
		if(getLogger().isLogLevel(LogLevel.DEBUG))
			getLogger().debug(MODULE, "PCRF Response before sending Sy Request:" + pcrfResponse);
		
		/// handling processing of GATEWAY_REBOOT request
		if (pcrfRequest.getPCRFEvents().contains(PCRFEvent.GATEWAY_REBOOT)) {
			handleGatewayReboot(pcrfResponse);
			return;
		}

		String sySessionId = pcrfResponse.getAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val);


		boolean sySessionExist = sySessionId != null;
		
		/// handling processing of Session-Stop request
		if (pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_STOP)) {
			if(sySessionExist == false){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Skipping processing of Sy handler. Reason: Sy SessionId not found in PCRF request");
			} else {
				handleSessionTermination(pcrfRequest, sySessionId, pcrfResponse);
			}
			return;
		}

		if(isContainsSyPackage(executionContext, pcrfResponse) == false) {
			return;
		}

		String primaryGateway = null;
		//isSySession Created
		if (sySessionExist) {
			primaryGateway = selectPrimaryGateway(pcrfRequest, sySessionId);
			if(primaryGateway != null) {
				pcrfRequest.setAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME.val, primaryGateway);
				pcrfResponse.setAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME.val, primaryGateway);
			}

			/*
			when  PCRFrequest is reAuthorization caused by SNR then we should ignore this handler based on misc-configuration
		 */
			if(getServerConfiguration().getMiscellaneousParameterConfiguration().getSLREnabledOnSNR() == false //NOSONAR
					&& PCRFKeyValueConstants.RE_AUTH_CAUSE_SNR.val.equals(pcrfResponse.getAttribute(PCRFKeyConstants.RE_AUTH_CAUSE.val))){
				LogManager.getLogger().info(MODULE, "Skipping SLR on Sy interface for PCRF request with core-session-Id: "
						+ pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val) + ". Reason: Re-Auth cause is SNR");

				addSycounters(sySessionId, pcrfRequest,pcrfResponse);
				return;
			}

			
			if(syMode == SyMode.PUSH) {
				
				if(pcrfRequest.getPCRFEvents().contains(PCRFEvent.REAUTHORIZE) == false 
						&& isConsecutiveRequest(pcrfRequest) == false) {
					if(getLogger().isDebugLogLevel())
						getLogger().debug(MODULE, "Sending SLR. Reason: No consecutive request found");
					sendSLR(pcrfRequest, pcrfResponse, primaryGateway, executionContext);
				}else if (addSycounters(sySessionId, pcrfRequest,pcrfResponse) == false) {
					if(getLogger().isDebugLogLevel())
						getLogger().debug(MODULE, "Sending SLR. Reason: Sy counters not found from diameter session");
					
					sendSLR(pcrfRequest, pcrfResponse, primaryGateway, executionContext);
				}
					
				return;
				
			}
		}

		sendSLR(pcrfRequest, pcrfResponse, primaryGateway, executionContext);

    }

    private boolean isContainsSyPackage(ExecutionContext executionContext, PCRFResponse pcrfResponse) {

		String productOfferName = pcrfResponse.getAttribute(PCRFKeyConstants.SUB_PRODUCT_OFFER.val);
		String subscriberIdentity = pcrfResponse.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
		String coreSessionId = pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val);
		boolean result = false;
		if (productOfferName != null) {
			final UserPackage basePackage = getDataPackage(productOfferName, subscriberIdentity, coreSessionId);

			if (basePackage == null) {
				if(getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Skipping processing. Reason: Data package is not configured in product offer for subscriber: "
							+ subscriberIdentity + " with core-session-ID: " + coreSessionId);
				}
			} else {
				if (basePackage.getQuotaProfileType() == QuotaProfileType.SY_COUNTER_BASED
						&& Collectionz.isNullOrEmpty(basePackage.getQuotaProfiles()) == false) {
					return true;
				}
			}
		} else {
			if(getLogger().isWarnLogLevel())
				getLogger().warn(MODULE, "Product offer not found from pcrf request for subscriber: " + subscriberIdentity + " with core-session-ID: " + coreSessionId);
		}

		try {
			final LinkedHashMap<String, Subscription> subscriptions = executionContext.getSubscriptions();
			result = isContainsSySubscription(subscriptions);
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while fetching addon subscription. Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
		}

		if(result == false) {
			if(getLogger().isDebugLogLevel())
				getLogger().debug(MODULE, "Skipping Sy Communication. Reason: No base package or subscriptions found with sy base quota profile");

		}

		return result;
	}

	private UserPackage getDataPackage(String productOfferName, String subscriberIdentity, String coreSessionId) {
		ProductOffer productOffer = getServerContext().getPolicyRepository().getProductOffer().byName(productOfferName);

		if (productOffer == null) {
			if(getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Skipping processing. Reason: Product offer(" + productOfferName
						+ ") not found from repository for subscriber: " + subscriberIdentity + " with core-session-ID: " + coreSessionId);
			}
			return null;
		}

		return productOffer.getDataServicePkgData();
	}

	private boolean isContainsSySubscription(LinkedHashMap<String, Subscription> subscriptions) {

		if(Maps.isNullOrEmpty(subscriptions) == false) {
			for(Subscription subscription : subscriptions.values()) {

				SubscriptionPackage subscriptionPackage = getServerContext().getPolicyRepository().getAddOnById(subscription.getPackageId());

				if (subscriptionPackage == null) {
					continue;
				}

				if (PolicyStatus.FAILURE == subscriptionPackage.getStatus()) {
					continue;
				}

				if (subscriptionPackage.getQuotaProfileType() == QuotaProfileType.SY_COUNTER_BASED
						&& Collectionz.isNullOrEmpty(subscriptionPackage.getQuotaProfiles()) == false) {
					return  true;
				}

			}
		}

		return  false;
	}

	private String selectPrimaryGateway(PCRFRequest pcrfRequest, String sySessionId) {

		String primaryGateway;

		if(pcrfRequest.getPCRFEvents().contains(PCRFEvent.REAUTHORIZE)) {

			if(getLogger().isDebugLogLevel())
				getLogger().debug(MODULE, "Trying to get sy gateway from diameter sy session");

			primaryGateway = getServerContext().getSyGatewayName(sySessionId);

			if(primaryGateway == null) {
				if(getLogger().isDebugLogLevel())
					getLogger().debug(MODULE, "Sy gateway not found from diameter sy session, take gateway found from PCRFRequest");

				primaryGateway = pcrfRequest.getAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME.val);
			} else {

				String oldSyGateway = pcrfRequest.getAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME.val);
				if(oldSyGateway == null){
					getLogger().info(MODULE, "Sy gateway "+ primaryGateway +" found from diameter sy session");
				} else {
					getLogger().info(MODULE, "Sy gateway changed. Old:" + oldSyGateway +" New:" + primaryGateway);
				}

			}
		} else {
			primaryGateway = pcrfRequest.getAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME.val);
		}


		return primaryGateway;
	}
			

	private String selectPrimaryGateway(SessionData session) {
		return session.getValue(PCRFKeyConstants.CS_SY_GATEWAY_NAME.val);
	}

	private boolean isConsecutiveRequest(PCRFRequest pcrfRequest) {
		
		String previousRequestNo = pcrfRequest.getAttribute(PCRFKeyConstants.PREVIOUS_REQUEST_NUMBER.getVal());
		String currentRequestNo = pcrfRequest.getAttribute(PCRFKeyConstants.REQUEST_NUMBER.getVal());
		
		if(currentRequestNo == null){
			return true;
		}
		
		if(previousRequestNo == null){
			return false;
		}
		
		
		long previousReq;
		long currentReq;
		try{
			previousReq = Long.parseLong(previousRequestNo);
		}catch(NumberFormatException e){
			if(getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE, "Invalid previous request no " + previousRequestNo);
			}
			return false;
		}
		try{
			currentReq = Long.parseLong(currentRequestNo);
		}catch(NumberFormatException e){
			if(getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE, "Invalid current request no " + currentRequestNo);
			}
			return true;
		}

		return currentReq == (previousReq + 1);

		
	}


	private void handleSessionTermination(PCRFRequest pcrfRequest, String sySessionId, PCRFResponse pcrfResponse) {
            String primaryGateway = selectPrimaryGateway(pcrfRequest, sySessionId);

            sendSTR(pcrfRequest, pcrfResponse, primaryGateway);
	}

	private void handleGatewayReboot(PCRFResponse pcrfResponse) {
		String gatewayAddress = pcrfResponse.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val);
		List<SessionData> sessions = sessionLocator.getCoreSessionByGatewayAddress(gatewayAddress);
		if(sessions == null || sessions.isEmpty()){
            if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                LogManager.getLogger().debug(MODULE, "Skipping processing of Sy handler. Reason: No session found for gateway: " + gatewayAddress);
			return;
        }

		PCRFResponse tempPCRFResponse = new PCRFResponseImpl();
		PCRFRequest tempPCRFRequest = new PCRFRequestImpl();
		//no need to provide log. as SessionManager already provide log for no. of session found for gateway
		for(SessionData session : sessions){
			String sySessionId = session.getValue(PCRFKeyConstants.CS_SY_SESSION_ID.val);
			if(sySessionId == null){
                if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                    LogManager.getLogger().debug(MODULE, "Skipping sending STR for Session with core sessionId: "
                            + session.getValue(PCRFKeyConstants.CS_CORESESSION_ID.val) +". Reason: Sy SessionId not found from session");
                continue;
            }

			String primaryGateway = selectPrimaryGateway(session);

            buildPCRFRequestResponse(session, tempPCRFRequest, tempPCRFResponse);

			sendSTR(tempPCRFRequest, tempPCRFResponse, primaryGateway);
        }
	}

	private boolean addSycounters(String sySessionID,PCRFRequest pcrfRequest, PCRFResponse pcrfResponse) {
		Map<String,String> map = getServerContext().getSyCounter(sySessionID);
		
		//counter must be fetch, if it is empty then we need to fetch counters by sending SLR
		if(map == null || map.isEmpty() == true){
			return false;
		}
		

		StringBuilder syCounters = new StringBuilder("Sy Counters: ");

		for(Entry<String,String> entry : map.entrySet()){
			syCounters.append(entry.getKey());
			syCounters.append('=');
			syCounters.append(entry.getValue());
			syCounters.append(',');
			pcrfRequest.setAttribute(entry.getKey(), entry.getValue());
			pcrfResponse.setAttribute(entry.getKey(), entry.getValue());
		}
		syCounters.append(" for SySessionID:");
		syCounters.append(sySessionID);
		
		if(getLogger().isLogLevel(LogLevel.INFO)) {
			getLogger().info(MODULE, syCounters.toString());
		}

		
		return true;
	
	}

	private void buildPCRFRequestResponse(SessionData session,PCRFRequest pcrfRequest, PCRFResponse pcrfResponse) {
		
		String sessionId = session.getSessionId();
		pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_ID.val, sessionId);
		pcrfResponse.setAttribute(PCRFKeyConstants.CS_SESSION_ID.val, sessionId);
		
		String coreSessionId = session.getValue(PCRFKeyConstants.CS_CORESESSION_ID.val);
		pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val, coreSessionId);
		pcrfResponse.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val, coreSessionId);
		
		String sySessionId = session.getValue(PCRFKeyConstants.CS_SY_SESSION_ID.val);
		pcrfRequest.setAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val, sySessionId);
		pcrfResponse.setAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val, sySessionId);
		
		
	}

	private void sendSLR (PCRFRequest pcrfRequest,PCRFResponse pcrfResponse,String primaryGateway, ExecutionContext executionContext){
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Sending SLR on Sy interface for PCRF request with core-session-Id: "
				+ pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));

		pcrfResponse.setFurtherProcessingRequired(false);
		pcrfResponse.setProcessingCompleted(false);

		boolean status  = getServerContext().sendSyRequest(
				pcrfResponse,
				diameterPeerGroupParameter,
				primaryGateway,
				new SyHandlerResponseListener(pcrfRequest, pcrfResponse, executionContext,getServiceContext(), ThreadContext.getContext()), CommandCode.SPENDING_LIMIT);


		if(status){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Pause Processing for PCRF request with core-session-Id: " 
								+ pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val)+ " until Sy Response is not received");


		} else {
			LogManager.getLogger().warn(MODULE, "Continue Processing for PCRF request with core-session-Id: " 
						+ pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val) +". Reason: Negative status for diameter request: false"
						+" while sending SLR" +  (primaryGateway == null ? "" : " to gateway : " + primaryGateway));
		
			pcrfResponse.setAttribute(PCRFKeyConstants.SY_COMMUNICATION.val, PCRFKeyValueConstants.SY_COMMUNICATION_FAIL.val);
			pcrfResponse.setFurtherProcessingRequired(true);
			pcrfResponse.setProcessingCompleted(true);
		}
	}
	
	
	private void sendSTR(PCRFRequest pcrfRequest,PCRFResponse pcrfResponse,String primaryGateway){
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Sending STR on Sy Interface for PCRFRequest with core-session-Id: " 
				+ pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));

		getServerContext().sendSyRequest(pcrfResponse,diameterPeerGroupParameter, primaryGateway, null, CommandCode.SESSION_TERMINATION);
	}

	@Override
	protected void preProcess(ServiceRequest serviceRequest,
			ServiceResponse serviceResponse, ExecutionContext executionContext) {
		// Performs pre-processing if any.
	}

	@Override
	protected void postProcess(ServiceRequest serviceRequest,
			ServiceResponse serviceResponse, ExecutionContext executionContext) {
		// Performs post-processing if any.
	}

	@Override
	public String getName() {
		return "PCRF-Sy";
	}
}
