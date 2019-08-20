package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.base.Maps;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.constants.UnknownUserAction;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateCardVersion;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.servicepolicy.authprotocol.CHAP;
import com.elitecore.netvertex.service.pcrf.servicepolicy.authprotocol.PAP;
import com.elitecore.netvertex.service.pcrf.servicepolicy.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.netvertex.service.pcrf.servicepolicy.authprotocol.exception.InvalidPasswordException;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.PccServicePolicyConfiguration;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

public class AuthenticationHandler extends ServiceHandler {
	private static final String MODULE = "AUTH-HDLR";
	private PccServicePolicyConfiguration servicePolicyConfiguration;
	private List<Authenticator> authenticators;
	private static ThreadLocal<SimpleDateFormat> simpleDateFormatPool = ThreadLocal.withInitial(() -> new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss"));
	private static final String AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD = "Authentication failed due to Invalid Password";
	private static final String AUTHENTICATION_FAILED_DUE_TO_INVALID_ENCRYPTION_TYPE = "Authentication failed due to Invalid Encryption type";
	private static final String AUTHENTICATION_FAILED_DUE_TO_UNSUPPORTED_ENCRYPTION = "Authentication failed due to Unsupported Encryption";
	private static final String AUTHENTICATION_FAILED_DUE_TO_IMPROPER_PASSWORD_ENCRYPTION_FORMAT = "Authentication failed due to Improper password Encryption Format";
	private static final String AUTHENTICATION_FAILED_DUE_TO_EXCEPTION_IN_DECRYPTION = "Authentication failed due to exception in Decryption";
	private static final String AUTHENTICATION_FAILED_DUE_TO_EXCEPTION_IN_ENCRYPTION = "Authentication failed due to exception in Encryption";

	public AuthenticationHandler(PCRFServiceContext pcrfServiceContext, PccServicePolicyConfiguration servicePolicyConfiguration) {
		super(pcrfServiceContext);
		this.servicePolicyConfiguration = servicePolicyConfiguration;
		authenticators =  new ArrayList<Authenticator>();
		authenticators.add(new CallingStationIdAuthenticator());
		authenticators.add(new NASPortIdAuthenticator());

	}

	@Override
	public void init() throws InitializationFailedException {
		//not required
	}

	@Override
	protected void preProcess(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {
		//IGNORED
	}

	@Override
	protected void process(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {
		PCRFRequest request = (PCRFRequest) serviceRequest;
		PCRFResponse response = (PCRFResponse) serviceResponse;
		
		SPRInfo userProfile = request.getSPRInfo();

		if (userProfile == null) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Skipping password check. UserProfile not found for core-session-ID : " + request.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal()) );
			}
			return;
		}
		try {

			if (userProfile.isUnknownUser()) {
				handleUnknownUser(request,response, userProfile);
			} else if (SPRInfo.UNAVAILABLE.equals(userProfile.getStatus())) {
				handleUnattainableUser(request,response, userProfile);
			} else {
				handleKnownUser(response, userProfile, executionContext);
			}

			if (PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equals(response.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal())) == false) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Skipping password check. Reason: UserProfile not found for core-session-ID: " + request.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal()) );
				}
				return;
			}

			checkPassword(request, userProfile);

			for(Authenticator authenticator : authenticators){
				authenticator.authenticate(request);
			}

		} catch (AuthenticationFailedException e) {
			getLogger().error(MODULE, "Authentication Failed for Subscriber: "+userProfile.getSubscriberIdentity()+". Reason: "+e.getMessage());
			ignoreTrace(e);
			
			response.setAttribute(PCRFKeyConstants.AUTH_FAILED_REASON.getVal(), e.getFailureReason());
			response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_AUTHENTICATION_FAILED.val);
			response.setFurtherProcessingRequired(false);

		} catch (Exception e) {
			getLogger().error(MODULE, "Error while Authenticating Subscriber: "+userProfile.getSubscriberIdentity()+". Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);

			response.setAttribute(PCRFKeyConstants.AUTH_FAILED_REASON.getVal(), "Authentication failed due to unknown internal error");
			response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_AUTHENTICATION_FAILED.val);
			response.setFurtherProcessingRequired(false);
		}
	}

	private void checkPassword(PCRFRequest request, SPRInfo userProfile) throws AuthenticationFailedException{
		if (userProfile.getPasswordCheck() == false) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping PAP/CHAP authentication. Reason: Password check flag is set to: " + userProfile.getPasswordCheck());
			}
			return;
		}

		if (userProfile.getPassword() == null) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping PAP/CHAP authentication. Reason: Subscriber Profile missing password.");
			}
			return;
		}

		int encryptionType = 1;
		if (userProfile.getEncryptionType() != null) {
			try {
				encryptionType = Integer.parseInt(userProfile.getEncryptionType());
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Found EncryptionType : " + encryptionType);
				}
			} catch (NumberFormatException nfe) {
				throw new AuthenticationFailedException("Invalid encryption type",nfe, AUTHENTICATION_FAILED_DUE_TO_INVALID_ENCRYPTION_TYPE);
			}
		} else {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "User Profile EncryptionType is " + userProfile.getEncryptionType() + ". Taking Default Encryption Type : " + encryptionType);
			}
		}

		String papPassword = request.getPapPassword();
		if (papPassword != null) {
			try {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Performing PAP Authentication");
				}

				PAP.handlePAPAuthentication(papPassword, userProfile.getPassword(), encryptionType);

				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "PAP Authentication successful.");
				}

			} catch (InvalidPasswordException ipe) {
				throw new AuthenticationFailedException("PAP Authentication failed. Reason: Invalid User-Password received",ipe, AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD);

			} catch (NoSuchEncryptionException e) {
				throw new AuthenticationFailedException("PAP Authentication failed. Reason: Unsupported encryption: "+encryptionType, e, AUTHENTICATION_FAILED_DUE_TO_UNSUPPORTED_ENCRYPTION);

			} catch (EncryptionFailedException e){
				throw new AuthenticationFailedException("PAP Authentication failed. Reason: Encryption Failed", e, AUTHENTICATION_FAILED_DUE_TO_EXCEPTION_IN_ENCRYPTION);
			}

			return;
		} else {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "PAP authentication is not performed. Reason: PAP password not received");
			}
		}
 
		byte[] chapPasswordBytes = request.getChapPasswordBytes();
		if ( chapPasswordBytes != null ) {
			byte[] chapChallengeBytes = request.getChapChallengeBytes();
 			try{
				CHAP.handleCHAPAuthentication(userProfile.getPassword(), chapPasswordBytes, encryptionType, chapChallengeBytes);

				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "CHAP Authentication successful.");
				}
			} catch (NoSuchEncryptionException e) {
				throw new AuthenticationFailedException("CHAP Authentication failed. Reason: Unsupported encryption: "+encryptionType, e,
						AUTHENTICATION_FAILED_DUE_TO_UNSUPPORTED_ENCRYPTION);

			} catch (com.elitecore.passwordutil.DecryptionNotSupportedException e) {
 				throw new AuthenticationFailedException("CHAP Authentication failed. Reason: Improper password encryption format: "+encryptionType, e,
						AUTHENTICATION_FAILED_DUE_TO_IMPROPER_PASSWORD_ENCRYPTION_FORMAT);
						
			} catch (InvalidPasswordException ipe) {
				throw new AuthenticationFailedException("CHAP Authentication failed. Reason: Invalid Password", ipe,
						AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD);
						
			} catch (DecryptionFailedException dfe){
				throw new AuthenticationFailedException("CHAP Authentication failed. Reason: Decryption failed", dfe,
						AUTHENTICATION_FAILED_DUE_TO_EXCEPTION_IN_DECRYPTION);
			}
			return;
		} else {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "CHAP authentication is not performed. Reason: CHAP password not received");
			}
		}
	}

	private void handleUnattainableUser(PCRFRequest request,PCRFResponse response, SPRInfo userProfile) {

		String identityAttribute = servicePolicyConfiguration.getIdentityAttribute();
		String userIdentity = response.getAttribute(identityAttribute);
		if (getLogger().isWarnLogLevel())
			getLogger().warn(MODULE, "User profile cannot be fetched with " + servicePolicyConfiguration.getIdentityAttribute() + " = "
					+ userIdentity
					+ ". Reason: Subscriber profile unavailable. Applying unknown user action");

		if (servicePolicyConfiguration.getUnknownUserAction() == UnknownUserAction.ALLOW_UNKNOWN_USER) {

			String basePackageId = servicePolicyConfiguration.getUnknownUserPkgId();
			if (basePackageId == null) {
				if (getLogger().isWarnLogLevel())
					getLogger().warn(MODULE, "Rejecting unattainable user: " + servicePolicyConfiguration.getIdentityAttribute() + " = "
							+ userIdentity
							+ " . Reason: Missing Base-Package information");
				response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_UNAVAILABLE.val);
				response.setFurtherProcessingRequired(false);
				return;
			}

			ProductOffer productOffer = getServerContext().getPolicyRepository().getProductOffer().byId(basePackageId);
			if (Objects.isNull(productOffer)) {
				if (getLogger().isWarnLogLevel())
					getLogger().warn(MODULE, "Rejecting unattainable user: " + servicePolicyConfiguration.getIdentityAttribute() + " = "
							+ userIdentity
							+ ". Reason: Missing Product-Offer for id: " + basePackageId);
				response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_UNAVAILABLE.val);
				response.setFurtherProcessingRequired(false);
				return;
			}

			if (validateNonMonetaryPackageForUnknownSubscriber(request, response, basePackageId, productOffer)){
				return;
			}

			if (getLogger().isInfoLogLevel())
				getLogger().info(MODULE, "Allowing unattainable user: " + servicePolicyConfiguration.getIdentityAttribute() + " = "
						+ userIdentity
						+ " with default package " + productOffer.getName() + " provided in service policy.");

			userProfile.setProductOffer(productOffer.getName());
			response.setAttribute(PCRFKeyConstants.SUB_PRODUCT_OFFER.val, productOffer.getName());

			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Unattainable user virtual profile");
				getLogger().info(MODULE, userProfile.toString());
			}
		} else if (servicePolicyConfiguration.getUnknownUserAction() == UnknownUserAction.DROP_REQUEST) {

			if (getLogger().isInfoLogLevel())
				getLogger().info(MODULE, "Dropping unattainable user: " + servicePolicyConfiguration.getIdentityAttribute() + " = "
						+ userIdentity);

			response.markForDropRequest();
			response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_UNAVAILABLE.val);
			response.setFurtherProcessingRequired(false);
			return;
		} else {

			if (getLogger().isInfoLogLevel())
				getLogger().info(MODULE, "Rejecting unattainable user: " + servicePolicyConfiguration.getIdentityAttribute() + " = "
						+ userIdentity
						+ " with result code set to " + PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_UNAVAILABLE.val);
			response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_UNAVAILABLE.val);
			response.setFurtherProcessingRequired(false);
			return;
		}
	}

	private void handleKnownUser(PCRFResponse response, SPRInfo userProfile, ExecutionContext executionContext) {

		String identityAttribute = servicePolicyConfiguration.getIdentityAttribute();
		String userIdentity = response.getAttribute(identityAttribute);
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "User Profile received.");
			getLogger().info(MODULE, userProfile.toString());
		}
		Date expiryDate = userProfile.getExpiryDate();
		if (expiryDate != null) {
			if (expiryDate.getTime() <= executionContext.getCurrentTime().getTimeInMillis()) {
				if (getLogger().isErrorLogLevel()) {
					getLogger().error(MODULE, "Rejecting subscriber request with identity  " + userIdentity +
							". Reson: Subscription has been expired on Date(dd-MMM-yyyy HH:mm:ss) : "
							+ simpleDateFormatPool.get().format(expiryDate));
				}
				response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_PROFILE_EXPIRED.val);
				response.setFurtherProcessingRequired(false);
				return;
			}
		}

		if (SubscriberStatus.INACTIVE.name().equals(userProfile.getStatus())) {
			if (getLogger().isErrorLogLevel())
				getLogger().error(MODULE, " Subscription is In-Active for User : " + userIdentity + " Status : "
						+ userProfile.getStatus());
			response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_PROFILE_INACTIVE.val);
			response.setFurtherProcessingRequired(false);
			return;
		}
	}

	private void handleUnknownUser(PCRFRequest request, PCRFResponse response, SPRInfo userProfile) {

		String identityAttribute = servicePolicyConfiguration.getIdentityAttribute();
		String userIdentity = response.getAttribute(identityAttribute);

		if (getLogger().isWarnLogLevel())
			getLogger().warn(MODULE, "User profile not found with " + servicePolicyConfiguration.getIdentityAttribute() + " = "
					+ userIdentity + ". so considering user as UNKNOWN");

		if (servicePolicyConfiguration.getUnknownUserAction() == UnknownUserAction.ALLOW_UNKNOWN_USER) {
			getServerContext().generateSystemAlert("", Alerts.UNKNOWN_USER, MODULE, "Request received from Unknown User: " +
					userIdentity + ". Action: User allowed", 0, userIdentity);
			String basePackageId = servicePolicyConfiguration.getUnknownUserPkgId();
			if (basePackageId == null) {
				if (getLogger().isWarnLogLevel())
					getLogger().warn(MODULE, "Rejecting UNKNOWN USER. Reason: Missing Base-Package information");
				response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val);
				response.setFurtherProcessingRequired(false);
				return;
			}

			ProductOffer productOffer = getServerContext().getPolicyRepository().getProductOffer().byId(basePackageId);
			if (productOffer == null) {
				if (getLogger().isWarnLogLevel())
					getLogger().warn(MODULE, "Rejecting UNKNOWN USER. Reason: Missing Base-Package for id: " + basePackageId);
				response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val);
				response.setFurtherProcessingRequired(false);
				return;
			}

			if (validateNonMonetaryPackageForUnknownSubscriber(request, response, basePackageId, productOffer)){
				return;
			}

			if (getLogger().isInfoLogLevel())
				getLogger().info(MODULE, "Allowing UNKNOWN user " + servicePolicyConfiguration.getIdentityAttribute() + " = "
						+ userIdentity
						+ " with default package (" + productOffer.getName() + ") provided in service policy.");

			userProfile.setProductOffer(productOffer.getName());
			response.setAttribute(PCRFKeyConstants.SUB_PRODUCT_OFFER.val, productOffer.getName());
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Unknown user virtual profile");
				getLogger().info(MODULE, userProfile.toString());
			}
		} else if (servicePolicyConfiguration.getUnknownUserAction() == UnknownUserAction.DROP_REQUEST) {

			getServerContext().generateSystemAlert("", Alerts.UNKNOWN_USER, MODULE, "Request received from Unknown User: " +
					userIdentity + ". Action: Request dropped", 0, userIdentity);

			if (getLogger().isInfoLogLevel())
				getLogger().info(MODULE, "Dropping UNKNOWN USER " + servicePolicyConfiguration.getIdentityAttribute() + " = "
						+ userIdentity);

			response.markForDropRequest();
			response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val);
			response.setFurtherProcessingRequired(false);
			return;
		} else {

			getServerContext().generateSystemAlert("", Alerts.UNKNOWN_USER, MODULE, "Request received from Unknown User: " +
					userIdentity + ". Action: Rejected", 0, userIdentity);

			if (getLogger().isInfoLogLevel())
				getLogger().info(MODULE, "Rejecting UNKNOWN USER " + servicePolicyConfiguration.getIdentityAttribute() + " = "
						+ userIdentity
						+ " with result code set to " + PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val);
			response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val);
			response.setFurtherProcessingRequired(false);
			return;
		}
	}

	private boolean validateNonMonetaryPackageForUnknownSubscriber(PCRFRequest request, PCRFResponse response, String basePackageId, ProductOffer productOffer) {
		if(SessionTypeConstant.GX.getVal().equalsIgnoreCase(request.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.getVal())) ||
                SessionTypeConstant.GY.getVal().equalsIgnoreCase(request.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.getVal())) ||
                        SessionTypeConstant.RADIUS.getVal().equalsIgnoreCase(request.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.getVal()))) {
            if (productOffer.getDataServicePkgData() != null && productOffer.getDataServicePkgData().getQuotaProfileType() == QuotaProfileType.RnC_BASED) {
				if (validateQuotaProfileRateConfiguration(response, basePackageId, productOffer)) {
					return true;
				}
				if (validateDataRateCardConfiguration(response, basePackageId, productOffer)) {
					return true;
				}
			}
        }
		return false;
	}

	private boolean validateQuotaProfileRateConfiguration(PCRFResponse response, String basePackageId, ProductOffer productOffer) {
		List<QuotaProfile> quotaProfiles = productOffer.getDataServicePkgData().getQuotaProfiles();
		for (QuotaProfile quotaProfile : quotaProfiles) {
            boolean isRateConfigured = isRateConfiguredForQuota(quotaProfile.getServiceWiseQuotaProfileDetails(0));
            if (isRateConfigured) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Rejecting UNKNOWN USER. Reason: Rate is configured with Data Package : " + basePackageId);
                }
                response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val);
                response.setFurtherProcessingRequired(false);
				return true;
            }
            isRateConfigured = isRateConfiguredForQuota(quotaProfile.getServiceWiseQuotaProfileDetails(1));
            if (isRateConfigured) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Rejecting UNKNOWN USER. Reason: Rate is configured with Data Package : " + basePackageId);
                }
                response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val);
                response.setFurtherProcessingRequired(false);
				return true;
            }
            isRateConfigured = isRateConfiguredForQuota(quotaProfile.getServiceWiseQuotaProfileDetails(2));
            if (isRateConfigured) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Rejecting UNKNOWN USER. Reason: Rate is configured with Data Package : " + basePackageId);
                }
                response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val);
                response.setFurtherProcessingRequired(false);
				return true;
            }
        }
		return false;
	}

	private boolean validateDataRateCardConfiguration(PCRFResponse response, String basePackageId, ProductOffer productOffer) {
		List<DataRateCard> dataRateCards = ((Package)productOffer.getDataServicePkgData()).getDataRateCards();
		for (DataRateCard dataRateCard : dataRateCards) {
			for(RateCardVersion rateCardVersion : dataRateCard.getRateCardVersions()){
				for(VersionDetail versionDetail : rateCardVersion.getVersionDetails()){
					if (isRateConfiguredWithRateSlab(response, basePackageId, versionDetail)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isRateConfiguredWithRateSlab(PCRFResponse response, String basePackageId, VersionDetail versionDetail) {
		for(RateSlab rateSlab : versionDetail.getSlabs()){
            if(rateSlab.isFree() == false){
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Rejecting UNKNOWN USER. Reason: Rate is configured with Data Package : " + basePackageId);
                }
                response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val);
                response.setFurtherProcessingRequired(false);
				return true;
            }
        }
		return false;
	}


	@Override
	protected void postProcess(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {
		//IGNORED
	}

	@Override
	protected boolean isApplicable(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
		PCRFRequest pcrfRequest = (PCRFRequest) serviceRequest;
		PCRFResponse pcrfResponse = (PCRFResponse) serviceResponse;
		return pcrfRequest.getPCRFEvents().contains(PCRFEvent.AUTHENTICATE) && pcrfResponse.isEmergencySession() == false; 
	}
	
	@Override
	public String getName() {
		return "Authentication ";
	}

	private boolean isRateConfiguredForQuota(Map<String, QuotaProfileDetail> quotaProfileDetails) {
		if(Maps.isNullOrEmpty(quotaProfileDetails)) {
			return false;
		}
		for (QuotaProfileDetail quotaProfileDetail : quotaProfileDetails.values()) {
			RncProfileDetail rncProfile  = (RncProfileDetail) quotaProfileDetail;
			if (rncProfile.isRateConfigured()) {
				return true;
			}
		}
		return false;
	}
	
}