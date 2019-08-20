package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS.IPCANQoSBuilder;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.pm.FinalQoSSelectionData;
import com.elitecore.netvertex.pm.PCRFPolicyContextImpl;
import com.elitecore.netvertex.pm.PCRFQoSProcessor;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QoSInformation;
import com.elitecore.netvertex.pm.QoSProfileDetail;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class DataPolicyHandler extends ServiceHandler{

	private static final String MODULE = "DATA-PLC-HDLR";
	private static final long MILLISECONDS_IN_24_HOUR = TimeUnit.HOURS.toMillis(24);
	private static final Random RANDOM = new Random();
	private UsageReservationProcessor usageReservationProcessor;
	private RuleInstaller ruleInstaller;
	private UMSliceProcessor umSliceProcessor;
	private RatingGroupSectionStateProcessor ratingGroupSectionStateProcessor;

	public DataPolicyHandler(PCRFServiceContext pcrfServiceContext) {
		super(pcrfServiceContext);
		usageReservationProcessor = new UsageReservationProcessor(pcrfServiceContext.getServerContext());
		ruleInstaller = new RuleInstaller(pcrfServiceContext.getServerContext());
		umSliceProcessor = new UMSliceProcessor(pcrfServiceContext.getServerContext());
		ratingGroupSectionStateProcessor = new RatingGroupSectionStateProcessor(pcrfServiceContext.getServerContext());
	}

	@Override
	public void process(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {

		PCRFRequest pcrfRequest = (PCRFRequest) serviceRequest;
		PCRFResponse response = (PCRFResponse) serviceResponse;
		String subscriberIdentity = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);


		QoSInformation qosInformation = new QoSInformation();

		PolicyContext policyContext = new PCRFPolicyContextImpl(pcrfRequest, response, null, executionContext, new PCRFQoSProcessor(qosInformation), getServerContext().getPolicyRepository());

		if (getLogger().isInfoLogLevel()) {

			policyContext.getTraceWriter().println("QoS selection summary for subscriber(" +
					subscriberIdentity + "):");
			policyContext.getTraceWriter().incrementIndentation();
		}
		
		FinalQoSSelectionData finalQoSInformationData = applyPackage(policyContext, qosInformation);
		if (finalQoSInformationData != null) {
			setQoSToResponse(pcrfRequest, response, finalQoSInformationData, qosInformation, policyContext);
		}

        if (getLogger().isInfoLogLevel()) {
		    getLogger().debug(MODULE, qosInformation.getTrace());
        }
		
		long maxRevalidationTimeInMillis  = policyContext.getCurrentTime().getTimeInMillis() + MILLISECONDS_IN_24_HOUR;
		Timestamp revalidationTime = null;
		
		if (policyContext.getTimeout() > 0 && policyContext.getRevalidationTime() != null) {
			
			revalidationTime = new Timestamp(policyContext.getCurrentTime().getTimeInMillis() + TimeUnit.SECONDS.toMillis(policyContext.getTimeout()));
			
			if (revalidationTime.after(policyContext.getRevalidationTime())) {
				revalidationTime = policyContext.getRevalidationTime();
			}
			
		} else if (policyContext.getTimeout() > 0) {
			revalidationTime = new Timestamp(policyContext.getCurrentTime().getTimeInMillis() + TimeUnit.SECONDS.toMillis(policyContext.getTimeout()));
		} else if (policyContext.getRevalidationTime() != null) {
			revalidationTime = policyContext.getRevalidationTime();
		}
		
		if (revalidationTime != null) {
			if (revalidationTime.getTime() > maxRevalidationTimeInMillis) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Setting max value as revalidation time. Reason: Selected revalidation time(" + revalidationTime + ") is after 24 hour");
				}
				revalidationTime = new Timestamp(maxRevalidationTimeInMillis);
			}
			response.setRevalidationTime(revalidationTime, policyContext.getCurrentTime().getTimeInMillis());
		}
		
		logQosSelectionSummary(finalQoSInformationData, policyContext);
	}
	
	private void setQoSToResponse(PCRFRequest pcrfRequest, PCRFResponse response, FinalQoSSelectionData finalQoSInfomationData, QoSInformation qosInformation, PolicyContext policyContext) {

		QoSProfileDetail qosProfileDetail = finalQoSInfomationData.getQosProfileDetail();

		String newQoSProfileName = qosProfileDetail.getPackageName() + CommonConstants.HASH + qosProfileDetail.getName() + CommonConstants.HASH
				+ qosProfileDetail.getFUPLevel();
		String previousQosProfileName = pcrfRequest.getAttribute(PCRFKeyConstants.QOS_PROFILE_NAME.val);
		if (previousQosProfileName == null || previousQosProfileName.equalsIgnoreCase(newQoSProfileName) == false) {
			response.setPolicyChanged(true);
			response.setAttribute(PCRFKeyConstants.EXPIRED_QOS_PROFILE.val, previousQosProfileName);
		} else {
			response.setPolicyChanged(false);
		}

		if(qosProfileDetail.getRedirectURL() != null) {
			response.setAttribute(PCRFKeyConstants.IPCAN_REDIRECT_URL.val, qosProfileDetail.getRedirectURL());
		}

		if (qosProfileDetail.getAction() == QoSProfileAction.REJECT) {
			if (getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Skipping further processing. Reason: Subscriber session is rejected");
			response.setAttribute(PCRFKeyConstants.SESSION_RELEASE_CAUSE.val, finalQoSInfomationData.getQosProfileDetail().getRejectCause());
			response.setAttribute(PCRFKeyConstants.RESULT_CODE.val, PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
			response.setAttribute(PCRFKeyConstants.QOS_PROFILE_NAME.val, newQoSProfileName);

			response.setQosProfileDetail(qosProfileDetail);
			response.setFurtherProcessingRequired(false);
			return;
		}		

		IPCANQoS requestedIPCanQoS = pcrfRequest.getRequestedQoS();
		if (requestedIPCanQoS != null && requestedIPCanQoS.isQosUpgrade() == false) {

			IPCANQoS paasedIPCanQoS = qosProfileDetail.getSessionQoS();

			response.setSessionQoS(createIpCanQoS(requestedIPCanQoS, paasedIPCanQoS));

		} else {
			response.setSessionQoS(qosProfileDetail.getSessionQoS());
		}

		response.setQosProfileDetail(qosProfileDetail);
		response.setAttribute(PCRFKeyConstants.QOS_PROFILE_NAME.val, newQoSProfileName);

		if (getLogger().isInfoLogLevel()) {
			String fupLevel = qosProfileDetail.getFUPLevel() == 0 ? "HSQ" : "FUP" + qosProfileDetail.getFUPLevel();
			getLogger().info(MODULE, "Selected QoS profile: " + qosProfileDetail.getName() + ", Level: " + fupLevel);
		}

		ruleInstaller.installRules(pcrfRequest, response, finalQoSInfomationData);
		usageReservationProcessor.process(response, finalQoSInfomationData);
		ratingGroupSectionStateProcessor.process(response, finalQoSInfomationData);
		
		setRevalidationTimeForSubscriptionExpiry(finalQoSInfomationData, policyContext);

		umSliceProcessor.process(response, qosInformation, finalQoSInfomationData);

	}

	private IPCANQoS createIpCanQoS(IPCANQoS requestedIPCanQoS, IPCANQoS paasedIPCanQoS) {

		if (getLogger().isLogLevel(LogLevel.DEBUG)) {
			getLogger().debug(MODULE, "Comparing  QoS, "
					+ "Download AAMBR( Requested = '" + requestedIPCanQoS.getAAMBRDLInBytes() + "' and Authorized " + paasedIPCanQoS.getAAMBRDLInBytes()
					+ ") ,"
					+ "Upload AAMBR( Requested = '" + requestedIPCanQoS.getAAMBRULInBytes() + "' and Authorized " + paasedIPCanQoS.getAAMBRULInBytes() + ") ,"
					+ "Download MBR( Requested = '" + requestedIPCanQoS.getMBRDLInBytes() + "' and Authorized " + paasedIPCanQoS.getMBRDLInBytes() + ") ,"
					+ "Upload MBR( Requested = '" + requestedIPCanQoS.getMBRULInBytes() + "' and Authorized " + paasedIPCanQoS.getMBRULInBytes()
					+ "). Lower will be applied (\"0\" is considered as no value)");
		}


		long aambrdlInBytes = paasedIPCanQoS.getAAMBRDLInBytes();
		if(requestedIPCanQoS.getAAMBRDLInBytes() > 0 && requestedIPCanQoS.getAAMBRDLInBytes() < aambrdlInBytes) {
			aambrdlInBytes = requestedIPCanQoS.getAAMBRDLInBytes();
		}


		long aambrulInBytes = paasedIPCanQoS.getAAMBRULInBytes();
		if(requestedIPCanQoS.getAAMBRULInBytes() > 0 && requestedIPCanQoS.getAAMBRULInBytes() < aambrulInBytes) {
			aambrulInBytes = requestedIPCanQoS.getAAMBRULInBytes();
		}

		long mbrulInBytes = paasedIPCanQoS.getMBRULInBytes();
		if(requestedIPCanQoS.getMBRULInBytes() > 0 && requestedIPCanQoS.getMBRULInBytes() < mbrulInBytes) {
			mbrulInBytes = requestedIPCanQoS.getMBRULInBytes();
		}

		long mbrdlInBytes = paasedIPCanQoS.getMBRDLInBytes();
		if(requestedIPCanQoS.getMBRDLInBytes() > 0 && requestedIPCanQoS.getMBRDLInBytes() < mbrdlInBytes) {
			mbrdlInBytes = requestedIPCanQoS.getMBRDLInBytes();
		}


		IPCANQoSBuilder ipCanQoS = new IPCANQoSBuilder()
				.withAAMBRDL(aambrdlInBytes)
				.withAAMBRUL(aambrulInBytes)
				.withMBRUL(mbrulInBytes)
				.withMBRDL(mbrdlInBytes);

		if (requestedIPCanQoS.getPriorityLevel() != null) {
			ipCanQoS.withPriorityLevel(requestedIPCanQoS.getPriorityLevel())
					.withPreEmptionCapability(requestedIPCanQoS.getPreEmptionCapability())
					.withPreEmptionValnerability(requestedIPCanQoS.getPreEmptionVulnerability());
		} else {
			ipCanQoS.withPriorityLevel(paasedIPCanQoS.getPriorityLevel())
					.withPreEmptionCapability(paasedIPCanQoS.getPreEmptionCapability())
					.withPreEmptionValnerability(paasedIPCanQoS.getPreEmptionVulnerability());
		}

		if (requestedIPCanQoS.getQCI() != null) {
			ipCanQoS.withQCI(requestedIPCanQoS.getQCI());
		} else {
			ipCanQoS.withQCI(paasedIPCanQoS.getQCI());
		}

		return ipCanQoS.build();
	}



	private void logQosSelectionSummary(@Nullable FinalQoSSelectionData finalQoSSelectionData, PolicyContext policyContext) {
		
		
		if (getLogger().isInfoLogLevel()) {
			policyContext.getTraceWriter().println("QoS selection summary:");
			policyContext.getTraceWriter().incrementIndentation();
			
			if (finalQoSSelectionData == null) {
				policyContext.getTraceWriter().print("No policy satisfied");
			} else {
				
				QoSProfileDetail qosProfileDetail = finalQoSSelectionData.getQosProfileDetail();
				IPCANQoS sessionQoS = qosProfileDetail.getSessionQoS();
				policyContext.getTraceWriter().print("QoS Profile: " + qosProfileDetail.getName());

				if (qosProfileDetail.getAction() == QoSProfileAction.REJECT) {
					policyContext.getTraceWriter().append("[Action: Reject, RejectCause: " + qosProfileDetail.getRejectCause() + "]");
				} else {
					policyContext.getTraceWriter().append("[QCI:" + sessionQoS.getQCI().stringVal
							+ ",AAMBRUL:" + sessionQoS.getAAMBRULInBytes()
							+ ",AAMBRDL:" + sessionQoS.getAAMBRDLInBytes()
							+ ",MBRUL:" + sessionQoS.getMBRULInBytes()
							+ ",MBRDL:" + sessionQoS.getMBRDLInBytes() + "]");
				}

				policyContext.getTraceWriter().println();

				Map<Long, PCCRule> pccRules = finalQoSSelectionData.getPccRules();
				if (pccRules == null || pccRules.isEmpty()) {
					policyContext.getTraceWriter().println("PCCRules: No PCCRules");
				} else {
					policyContext.getTraceWriter().print("PCCRules: ");
					for (PCCRule pccRule : pccRules.values()) {
						policyContext.getTraceWriter().append(pccRule.getName() + "[Service:" + pccRule.getServiceName()
								+ ",QCI:" + pccRule.getQCI().stringVal
								+ ",GBRUL:" + pccRule.getGBRUL()
								+ ",GBRDL:" + pccRule.getGBRDL()
								+ ",MBRUL:" + pccRule.getMBRUL()
								+ ",MBRDL:" + pccRule.getMBRDL()
								+ "], ");
					}
					policyContext.getTraceWriter().println();
				}

				List<ChargingRuleBaseName> chargingRuleBaseNames = finalQoSSelectionData.getChargingRuleBaseNames();
				if (Collectionz.isNullOrEmpty(chargingRuleBaseNames)) {
					policyContext.getTraceWriter().println("ChargingRuleBaseNames: No ChargingRuleBaseNames");
				} else {
					policyContext.getTraceWriter().print("ChargingRuleBaseNames: ");
					for (ChargingRuleBaseName chargingRuleBaseName : chargingRuleBaseNames) {
						chargingRuleBaseName.printToQosSelectionSummary(policyContext.getTraceWriter());
					}
					policyContext.getTraceWriter().println();
				}
			}
			policyContext.getTraceWriter().decrementIndentation();

			getLogger().info(MODULE, policyContext.getTrace());
		}
	}
	
	/*
	 * NetVertex should consider only applied subscription to calculate Revalidation Time.
	 * 
	 * So here we iterate all selected PCC Rules and finds its subscriptions to calculate Revalidation.
	 * 
	 * In case PCCRules is not configured for AddOn, We check subscription from which QoS applied
	 * 
	 */
	private void setRevalidationTimeForSubscriptionExpiry(FinalQoSSelectionData finalQoSInfomationData, PolicyContext policyContext) {
		
		LinkedHashMap<String, Subscription> subscriptions;
		try {
			subscriptions = policyContext.getSubscriptions();
		} catch (OperationFailedException e) {
			
			String subscriberId = policyContext.getPCRFRequest().getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
			String coresessionId = policyContext.getPCRFRequest().getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val);
			
			getLogger().error(MODULE, "Skipping revalidation time calculation for subscription expiry for subscriber ID: " 
					+ subscriberId + ", coresession ID: " + coresessionId + " . Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
			getLogger().trace(MODULE, e);
			}
			return;
		}
		
		Set<String> uniqueSusbcriptionIds = new HashSet<String>();
		
		/*
		 * There can be scenario where addOn is without PCCRules, 
		 * in this case we have to check QoS is applied from which subscription
		 */
		if (uniqueSusbcriptionIds.add(finalQoSInfomationData.getSelectedSubscriptionIdOrPkgId())) {
			setTimeOutForSubscription(finalQoSInfomationData.getSelectedSubscriptionIdOrPkgId(), policyContext, subscriptions);
		}
		
		Map<String, String> pccRuleIdToSubscriptionOrPackageId = finalQoSInfomationData.getPccRuleIdToSubscriptionOrPackageId();
		
		if (Maps.isNullOrEmpty(pccRuleIdToSubscriptionOrPackageId)) {
			return;
		}
		
		for (String subscriptionId : pccRuleIdToSubscriptionOrPackageId.values()) {
			
			if (uniqueSusbcriptionIds.add(subscriptionId)) {
				setTimeOutForSubscription(subscriptionId, policyContext, subscriptions);
			}
		}
		
	}
	
	private void setTimeOutForSubscription(String subscriptionIdOrPackageId, PolicyContext policyContext,
			LinkedHashMap<String, Subscription> subscriptions) {
		Subscription subscription = subscriptions.get(subscriptionIdOrPackageId);
		
		//subscription will be null in case of base/promotional PCCRule selected
		if (subscription == null) {
			
			PromotionalPackage promotionalPackage = getServerContext().getPolicyRepository().getPromotionalPackageById(subscriptionIdOrPackageId);
			
			//only promotional package's endtime should be considered for revalidation time calculation
			if (promotionalPackage == null) {
				return;
			}
			
			if (promotionalPackage.getAvailabilityEndDate() == null) {
				return;
			}
			getLogger().debug(MODULE, "Considering end-time(" + promotionalPackage.getAvailabilityEndDate()
					+ ") of promotional package(id: " + subscriptionIdOrPackageId + ") for session revalidation");
			policyContext.setRevalidationTime(promotionalPackage.getAvailabilityEndDate());
		} else {
			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Considering end-time("+subscription.getEndTime()+") of subscription(id: " + subscriptionIdOrPackageId + ") for session revalidation");
			}
			
			policyContext.setRevalidationTime(subscription.getEndTime());
		}
		
	}

	@Override
	protected void preProcess(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {
		
	}

	@Override
	protected void postProcess(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {
		PCRFResponse pcrfResponse = (PCRFResponse) serviceResponse;
		int revalidationTimeDeltaInSeconds = getServiceContext().getRevalidationTimeDelta();
		Date revalidationTime = pcrfResponse.getRevalidationTime();
		if (revalidationTime != null && revalidationTimeDeltaInSeconds > 0) {
			pcrfResponse.setRevalidationTime(new Timestamp(revalidationTime.getTime() + TimeUnit.SECONDS.toMillis(RANDOM.nextInt(revalidationTimeDeltaInSeconds))), executionContext.getCurrentTime().getTimeInMillis());
		}
	}

	@Override
	protected boolean isApplicable(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
		PCRFRequest pcrfRequest = (PCRFRequest) serviceRequest;
		PCRFResponse pcrfResponse = (PCRFResponse) serviceResponse;

		String sessionType = pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val);

		if(SessionTypeConstant.GY.val.equals(sessionType) || SessionTypeConstant.RX.val.equals(sessionType)){
			return false;
		}

		boolean isDataServiceRequest = SessionTypeConstant.GX.val.equals(sessionType) || (SessionTypeConstant.RADIUS.val.equals(sessionType) && PCRFKeyValueConstants.DATA_SERVICE_ID.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SERVICE.val)));

		if(isDataServiceRequest == false) {
			return false;
		}

		return (pcrfRequest.getPCRFEvents().contains(PCRFEvent.AUTHORIZE)
				|| pcrfRequest.getPCRFEvents().contains(PCRFEvent.REAUTHORIZE)) && pcrfResponse.isEmergencySession() == false;
	}

	@Override
	public String getName() {
		return MODULE;
	}
	
	public abstract @Nullable FinalQoSSelectionData applyPackage(PolicyContext policyContext, QoSInformation qoSInformation);
}
