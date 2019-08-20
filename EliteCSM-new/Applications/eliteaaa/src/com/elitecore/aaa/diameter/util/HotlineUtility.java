package com.elitecore.aaa.diameter.util;

import java.util.List;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.diameter.policies.diameterpolicy.DiameterPolicyManager;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;

public class HotlineUtility {

	private static String MODULE = "HOTLINE-UTL";

	private static HotlineProcessor hotlineProcessor =
			new HotlineProcessor(DiameterPolicyManager.getInstance(DiameterPolicyManager.DIAMETER_AUTHORIZATION_POLICY));

	public static  boolean isEligibleForHotlining(ApplicationRequest applicationRequest, ApplicationResponse applicationResponse) {
		return hotlineProcessor.isEligibleForHotlining(applicationRequest, applicationResponse);
	}

	public static void doHotlining(ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse) {
		hotlineProcessor.doHotlining(applicationRequest, applicationResponse);
	}

	public static void applyHotlinePolicy(ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse, String strHotlinePolicy) throws ParserException, PolicyFailedException {
		hotlineProcessor.applyHotlinePolicy(applicationRequest, applicationResponse, strHotlinePolicy);
	}

	/*
	 * An indirection introduced in Hotline Utility to allow test cases to customize the behavior for testing.
	 * For test cases we needed policy manager to be injected from outside. 
	 */
	public static class HotlineProcessor {
		private DiameterPolicyManager policyManager;

		public HotlineProcessor(DiameterPolicyManager policyManager) {
			this.policyManager = policyManager;
		}

		public boolean isEligibleForHotlining(ApplicationRequest applicationRequest, ApplicationResponse applicationResponse) {
			IDiameterAVP resultCodeAvp = applicationResponse.getAVP(DiameterAVPConstants.RESULT_CODE);
			if (applicationRequest.getApplicationPolicy() == null ) {
				LogManager.getLogger().warn(MODULE, 
						"Skipping Hotlining, Reason: No application policy selected");
				return false;
			} else if (resultCodeAvp == null) {
				LogManager.getLogger().warn(MODULE, 
						"Skipping Hotlining, Reason: " + DiameterDictionary.nameOf(DiameterAVPConstants.RESULT_CODE) + "(" + DiameterAVPConstants.RESULT_CODE + ") is missing from the response packet");
				return false;
			} else {
				long resultCode = resultCodeAvp.getInteger();
				ResultCodeCategory resultCodeCategory = ResultCodeCategory.getResultCodeCategory(resultCode);
				if (resultCodeCategory.isFailureCategory) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE,
							"Request is eligible for hotlining due to result code: " + resultCode);
					}
					return true;
				}
				return false;
			}
		}

		public void doHotlining(ApplicationRequest applicationRequest,
				ApplicationResponse applicationResponse) {
			AccountData accountData = applicationRequest.getAccountData();
			String hotlinePolicy = null;
			if (accountData != null && Strings.isNullOrBlank(hotlinePolicy = accountData.getHotlinePolicy()) == false) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Applying customer profile level hotline policy: " 
							+ hotlinePolicy);
				}
				try {
					applyHotlinePolicy(applicationRequest, applicationResponse, hotlinePolicy);
				} catch (ParserException e) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Failed to apply hotline policy: " +
								"" + hotlinePolicy + ", due to reason: " + e.getMessage());
					}
					LogManager.getLogger().trace(MODULE, e);
				} catch (PolicyFailedException e) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Failed to apply hotline policy: " +
								"" + hotlinePolicy + ", due to reason: " + e.getMessage());
					}
					LogManager.getLogger().trace(MODULE, e);
				}
			} else if (Strings.isNullOrBlank(hotlinePolicy = applicationRequest
					.getDiameterRequest().getPeerData().getHotlinePolicy()) == false) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "No hotline policy configured in customer Profile, " +
							"applying peer profile level hotline policy: " + hotlinePolicy);
				}
				try {
					applyHotlinePolicy(applicationRequest, applicationResponse, hotlinePolicy);
				} catch (ParserException e) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Failed to apply hotline policy: " +
								"" + hotlinePolicy + ", due to reason: " + e.getMessage());
					}
					LogManager.getLogger().trace(MODULE, e);
				} catch (PolicyFailedException e) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Failed to apply hotline policy: " +
								"" + hotlinePolicy + ", due to reason: " + e.getMessage());
					}
					LogManager.getLogger().trace(MODULE, e);
				}
			} else {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Request cannot be hotlined as no hotline " +
							"policy is configured on customer profile or peer profile level");
				}
			}
		}

		public void applyHotlinePolicy(ApplicationRequest applicationRequest,
				ApplicationResponse applicationResponse, String strHotlinePolicy) throws ParserException, PolicyFailedException {

			List<String> satisfiedHotlinePolicies = null;
			satisfiedHotlinePolicies = policyManager.applyPolicies(
					applicationRequest.getDiameterRequest(), 
					applicationResponse.getDiameterAnswer(),
					strHotlinePolicy, null, null, null, false, false, false);

			if (Collectionz.isNullOrEmpty(satisfiedHotlinePolicies) == false) {
				applicationRequest.setParameter(AAAServerConstants.SATISFIED_HOTLINE_POLICIES,
						satisfiedHotlinePolicies);
				Predicate< IDiameterAVP> retainFilter = new Predicate<IDiameterAVP>() {

					@Override
					public boolean apply(IDiameterAVP avp) {
						String avpId = "";
						if (avp != null) {
							avpId = avp.getAVPId();
						}
						return DiameterAVPConstants.ORIGIN_HOST.equalsIgnoreCase(avpId) ||
								DiameterAVPConstants.ORIGIN_REALM.equalsIgnoreCase(avpId) ||
								DiameterAVPConstants.DESTINATION_HOST.equalsIgnoreCase(avpId) ||
								DiameterAVPConstants.DESTINATION_REALM.equalsIgnoreCase(avpId) ||
								DiameterAVPConstants.SESSION_ID.equalsIgnoreCase(avpId) ||
								DiameterAVPConstants.ERROR_MESSAGE.equalsIgnoreCase(avpId);
					}

				};
				applicationResponse.getDiameterAnswer().retain(retainFilter);

				policyManager.applyReplyItems(
						applicationRequest.getDiameterRequest(),
						applicationResponse.getDiameterAnswer(),
						satisfiedHotlinePolicies, null);
			}

			IDiameterAVP hotlineAttribute = DiameterDictionary.getInstance().getAttribute(
					DiameterAVPConstants.EC_SATISFIED_POLICIES);
			if (hotlineAttribute != null) {
				if (Collectionz.isNullOrEmpty(satisfiedHotlinePolicies) == false) {
					String satisfiedPolicyStr = "";
					int size = satisfiedHotlinePolicies.size();
					for (int i = 0; i < size; i++) {
						if (i == size - 1) {
							satisfiedPolicyStr = satisfiedPolicyStr + satisfiedHotlinePolicies.get(i);
						} else {
							satisfiedPolicyStr = satisfiedPolicyStr + satisfiedHotlinePolicies.get(i) + ",";
						}
					}
					hotlineAttribute.setStringValue(satisfiedPolicyStr);
					applicationResponse.addInfoAvp(hotlineAttribute);
					LogManager.getLogger().info(MODULE,"Hotline policy AVP added to response packet");
				}
			} else {
				LogManager.getLogger().info(MODULE,"Hotline policy AVP not found in Elitecore dictionary");
			}

			addHotlineReason(applicationRequest, applicationResponse);
			updateResultCodeToSuccess(applicationResponse);
		}

		private void addHotlineReason(ApplicationRequest applicationRequest, ApplicationResponse applicationResponse) {
			IDiameterAVP errorMessageAVP = applicationResponse.getAVP(DiameterAVPConstants.ERROR_MESSAGE);
			if (errorMessageAVP == null) {
				LogManager.getLogger().warn(MODULE, "Hotline reason AVP not added, reason: Error Message AVP not found in response");
				return;
			}
			applicationResponse.getDiameterAnswer().removeAVP(errorMessageAVP);
			String hotlineReason = errorMessageAVP.getStringValue();
			IDiameterAVP hotlineReasonAVP = DiameterDictionary.getInstance().getAttribute(
					DiameterAVPConstants.EC_HOTLINE_REASON);
			hotlineReasonAVP.setStringValue(hotlineReason);
			applicationRequest.addInfoAvp(hotlineReasonAVP);
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Hotline-Reason-AVP(21067:124) added successfully in request with value: " + hotlineReason);
			}
		}

		private void updateResultCodeToSuccess(ApplicationResponse applicationResponse) {
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, 
					applicationResponse.getDiameterAnswer(), String.valueOf(ResultCode.DIAMETER_SUCCESS.code));
		}
	}
}

