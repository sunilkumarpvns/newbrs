package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResultCode;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.service.Service;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.BasePackage;
import com.elitecore.netvertex.pm.quota.GyPolicyContextImpl;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.elitecore.corenetvertex.constants.CommonConstants.MILLISECONDS_FOR_24_HOUR;

public class DataRnCHandler extends ServiceHandler {
	private static final String MODULE = "DATA-RNC-HNDLR";
	private static final Random RANDOM = new Random();
	private ReservationHandler reservationHandler;
    private ABMFReservationOperationHandler abmfReservationOperationHandler;

	public DataRnCHandler(PCRFServiceContext serviceContext) {
		super(serviceContext);
		reservationHandler = new ReservationHandler(serviceContext);
        abmfReservationOperationHandler = new ABMFReservationOperationHandler();
	}

	@Override
	public void init() throws InitializationFailedException {
		// No-op
	}

	@Override
	protected void preProcess(ServiceRequest serviceRequest, ServiceResponse serviceResponse,
			ExecutionContext executionContext) {
		// No-op
	}

	@Override
	protected void process(ServiceRequest serviceRequest, ServiceResponse serviceResponse,
			ExecutionContext executionContext) {
		PCRFRequest request = (PCRFRequest) serviceRequest;
		PCRFResponse response = (PCRFResponse) serviceResponse;

        String requestedServiceId = request.getAttribute(PCRFKeyConstants.CS_SERVICE.val);
        Service requestedService = getServerContext().getPolicyRepository().getService().byId(requestedServiceId);

        if(requestedService == null){
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Rejecting request for subscriber ID:"
                        +request.getSPRInfo().getSubscriberIdentity()+". Reason: Service "+ requestedServiceId+" is Inactive.");
            }
            response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
            response.setFurtherProcessingRequired(false);
            return;
        }

		BasePackage basePackage = getBasePackage(request);
        if (Objects.isNull(basePackage)) {
            response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
            response.setFurtherProcessingRequired(false);
            return;
        }

        try {
            String productOfferId = getServerContext().getPolicyRepository().getProductOffer().byName(request.getSPRInfo().getProductOffer()).getId();
            GyPolicyContextImpl gyPolicyContextImpl = new GyPolicyContextImpl(request, response, basePackage, executionContext, getServerContext().getPolicyRepository(), getServiceContext().getRevalidationTimeDelta(), productOfferId);
            executionContext.getDDFTable().checkForBillDateChange(request.getSPRInfo());
            reservationHandler.handle(request, executionContext, gyPolicyContextImpl);
            if (getLogger().isInfoLogLevel() && Strings.isNullOrBlank(gyPolicyContextImpl.getTrace()) == false) {
                getLogger().info(MODULE, gyPolicyContextImpl.getTrace());
            }
            abmfReservationOperationHandler.handle(request, response, executionContext);

            setRevalidationTime(response, gyPolicyContextImpl);
            setSessionLevelQuotaInfo(response);
        } catch (Exception e) {
            LogManager.getLogger().trace(e);
        }
    }

    private void setSessionLevelQuotaInfo(PCRFResponse response) {

        List<MSCC> grantedMSCCs = response.getGrantedMSCCs();

        for (MSCC grantedMSCC: grantedMSCCs) {
            GyServiceUnits grantedServiceUnits = grantedMSCC.getGrantedServiceUnits();

            if (grantedServiceUnits == null) {
                continue;
            }

            if(grantedMSCC.getRatingGroup() != 0) {
                return;
            }

            if (grantedServiceUnits.getVolume() > 0) {

                VolumeUnitType volumeUnitType = getVolumeUnitType(grantedMSCC.getGrantedServiceUnits());

                if(VolumeUnitType.TOTAL == volumeUnitType) {
                    response.setAttribute(PCRFKeyConstants.SESS_GSU_TOTAL.val, String.valueOf(grantedServiceUnits.getVolume()));
                }else if(VolumeUnitType.DOWNLOAD == volumeUnitType){
                    response.setAttribute(PCRFKeyConstants.SESS_GSU_DOWNLOAD.val, String.valueOf(grantedServiceUnits.getVolume()));
                }else if(VolumeUnitType.UPLOAD == volumeUnitType){
                    response.setAttribute(PCRFKeyConstants.SESS_GSU_UPLOAD.val, String.valueOf(grantedServiceUnits.getVolume()));
                }
            }

            if(grantedServiceUnits.getTime() > 0) {
                response.setAttribute(PCRFKeyConstants.SESS_GSU_TIME.val, String.valueOf(grantedServiceUnits.getTime()));
            }
        }
    }

    private VolumeUnitType getVolumeUnitType(GyServiceUnits grantedServiceUnits) {
        QuotaProfile quotaProfile = getServerContext().getPolicyRepository().getQuotaProfile(grantedServiceUnits.getPackageId(), grantedServiceUnits.getQuotaProfileIdOrRateCardId());

        if (quotaProfile != null) {
            RnCQuotaProfileDetail rnCQuotaProfileDetail = (RnCQuotaProfileDetail) quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails().values().iterator().next();
            return rnCQuotaProfileDetail.getUnitType();
        }

        DataRateCard dataRateCard = getServerContext().getPolicyRepository().getDataRateCard(grantedServiceUnits.getPackageId(), grantedServiceUnits.getQuotaProfileIdOrRateCardId());

        if (dataRateCard != null) {
            return VolumeUnitType.TOTAL;
        }

        return null;
    }

    private void setRevalidationTime(PCRFResponse response, GyPolicyContextImpl policyContext) {

		long maxRevalidationTimeInMillis  = policyContext.getCurrentTime().getTimeInMillis() + MILLISECONDS_FOR_24_HOUR;
		Timestamp revalidationTime = null;

		if (policyContext.getTimeout() > 0 && policyContext.getRevalidationTime() != null) {

			revalidationTime = calculateRevalidationTime(policyContext.getTimeout(), policyContext.getCurrentTime().getTimeInMillis());

			if (revalidationTime.after(policyContext.getRevalidationTime())) {
				revalidationTime = policyContext.getRevalidationTime();
			}

		} else if (policyContext.getTimeout() > 0) {
			revalidationTime = calculateRevalidationTime(policyContext.getTimeout(), policyContext.getCurrentTime().getTimeInMillis());
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
	}

	@Nonnull
	private Timestamp calculateRevalidationTime(long timeout, long currentTime) {
		return new Timestamp(currentTime + TimeUnit.SECONDS.toMillis(timeout));
	}

	private BasePackage getBasePackage(PCRFRequest pcrfRequest) {
        String subscriberPackage = null;

        if(Objects.nonNull(pcrfRequest.getSPRInfo())) {
            subscriberPackage = pcrfRequest.getSPRInfo().getProductOffer();
        }

        String subscriberId = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

        if (Objects.isNull(subscriberPackage)) {
            if (getLogger().isErrorLogLevel())
                getLogger().error(MODULE, "Rejecting request for subscriber ID: " + subscriberId + ". Reason: package information not found in subscriber profile");
            return null;
        }

        ProductOffer productOffer = getServerContext().getPolicyRepository().getProductOffer().byName(subscriberPackage);

        if (Objects.isNull(productOffer)) {
            if (getLogger().isErrorLogLevel())
                getLogger().error(MODULE, "Rejecting request for subscriber ID: " + subscriberId + ". Reason: subscriber product offer(" + subscriberPackage + ") not found in policy repository");
            return null;
        }

        BasePackage basePackage = (BasePackage) productOffer.getDataServicePkgData();

        if (Objects.isNull(basePackage)) {
            if (getLogger().isErrorLogLevel())
                getLogger().error(MODULE, "Rejecting request for subscriber ID: " + subscriberId + ". Reason: subscriber package(" + subscriberPackage + ") not found in policy repository");
            return null;
        }

        if (basePackage.getStatus() == PolicyStatus.FAILURE) {
            if (getLogger().isErrorLogLevel())
                getLogger().error(MODULE, "Rejecting request for subscriber ID: " + subscriberId
                        + ". Reason: subscriber package(" + subscriberPackage + ") has status FAILURE. Reason: "
                        + basePackage.getFailReason());
            return null;
        }

        return basePackage;
    }

	@Override
	protected void postProcess(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {

	    PCRFResponse response = (PCRFResponse) serviceResponse;

	    /// Adding random delta in revalidation time
		int revalidationTimeDeltaInSeconds = getServiceContext().getRevalidationTimeDelta();
		Date revalidationTime = response.getRevalidationTime();
		if (revalidationTime != null && revalidationTimeDeltaInSeconds > 0) {
			response.setRevalidationTime(new Timestamp(revalidationTime.getTime() + TimeUnit.SECONDS.toMillis(RANDOM.nextInt(revalidationTimeDeltaInSeconds))), executionContext.getCurrentTime().getTimeInMillis());
		}

        if(Objects.isNull(response.getGrantedMSCCs())) {
            return;
        }

        boolean isSuccess = false;

        for(MSCC mscc : response.getGrantedMSCCs()){
            if(ResultCode.SUCCESS == mscc.getResultCode()){
                isSuccess = true;
                break;
            }
        }

        if(isSuccess == false){
            response.setGrantedMSCCs(null);
            response.setAttribute(PCRFKeyConstants.RESULT_CODE.val, ResultCode.DIAMETER_END_USER_SERVICE_DENIED.val);
        }
	}

	@Override
	protected boolean isApplicable(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
        PCRFResponse pcrfResponse = (PCRFResponse) serviceResponse;
        PCRFRequest pcrfRequest = (PCRFRequest) serviceRequest;

        boolean isGySession = SessionTypeConstant.GY.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val));
        boolean isRadiusSession = SessionTypeConstant.RADIUS.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val));

        boolean isDataServiceRequest = isGySession|| (isRadiusSession && PCRFKeyValueConstants.DATA_SERVICE_ID.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SERVICE.val)));
        return isDataServiceRequest
                && pcrfResponse.isEmergencySession() == false && pcrfRequest.getPCRFEvents().contains(PCRFEvent.QUOTA_MANAGEMENT)
                && (pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_START) || pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_UPDATE));
    }

	@Override
	public String getName() {
		return "Data-RnC-Handler";
	}



}
