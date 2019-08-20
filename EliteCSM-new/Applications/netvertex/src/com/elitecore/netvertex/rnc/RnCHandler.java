package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.data.ResultCode;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferServicePkgRel;
import com.elitecore.corenetvertex.pm.service.Service;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.gy.ReportingReason;
import com.elitecore.netvertex.pm.quota.RoPolicyContextImpl;
import com.elitecore.netvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RnCHandler extends ServiceHandler {
	
	private static final String MODULE = "RNC-HNDLR";
	private RnCReservationHandler rncReservationHandler;
    private ABMFRnCReservationOperationHandler abmfRnCReservationOperationHandler;
    private static final long MILLISECONDS_IN_24_HOUR = TimeUnit.HOURS.toMillis(24);

    public RnCHandler(PCRFServiceContext serviceContext) {
		super(serviceContext);
		rncReservationHandler = new RnCReservationHandler(serviceContext);
        abmfRnCReservationOperationHandler = new ABMFRnCReservationOperationHandler();
	}

	@Override
	public void init() throws InitializationFailedException {
		// No-op
	}

	@Override
	protected void preProcess(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {
		// No-op
	}

	@Override
	protected void process(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {

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

		RnCPackage rncPackage = getBasePackage(request);

        try {

            String productOfferId = getServerContext().getPolicyRepository().getProductOffer().byName(request.getSPRInfo().getProductOffer()).getId();

            RoPolicyContextImpl roPolicyContextImpl = new RoPolicyContextImpl(request, response, rncPackage, executionContext, getServerContext().getPolicyRepository(), productOfferId);

            if(Collectionz.isNullOrEmpty(request.getReportedMSCCs()) == false){
                for (MSCC mscc : request.getReportedMSCCs()) {

                    if (ReportingReason.FINAL == mscc.getReportingReason()) {

                        if (LogManager.getLogger().isDebugLogLevel()) {
                            roPolicyContextImpl.getTraceWriter().println();
                            roPolicyContextImpl.getTraceWriter().print(" Reporting Reason FINAL. Skipping Granted service unit reservation.");
                        }

                        response.setQuotaReservation(roPolicyContextImpl.getGrantedAllMSCC());
                        return;
                    }
                }
            }

            rncReservationHandler.handle(request, response, roPolicyContextImpl, executionContext);


            if(response.isFurtherProcessingRequired()==false){
                return;
            }

            abmfRnCReservationOperationHandler.handle(response, executionContext);

            setRevalidationTime(executionContext, response, roPolicyContextImpl);

            if (getLogger().isInfoLogLevel()){
                getLogger().info(MODULE, roPolicyContextImpl.getTrace());
            }
            
        } catch (Exception e) {
            LogManager.getLogger().trace(e);
        }
    }

    private void setRevalidationTime(ExecutionContext executionContext, PCRFResponse response, RoPolicyContextImpl roPolicyContextImpl) {

        Date revalidationTime = new Timestamp(executionContext.getCurrentTime().getTimeInMillis() + MILLISECONDS_IN_24_HOUR);
        if (roPolicyContextImpl.getTimeout() > 0) {
            Timestamp calculatedRevalidationTime = new Timestamp(roPolicyContextImpl.getCurrentTime().getTimeInMillis()
                    + TimeUnit.SECONDS.toMillis(roPolicyContextImpl.getTimeout()));
            if (calculatedRevalidationTime.before(revalidationTime)) {
                revalidationTime = calculatedRevalidationTime;
            } else {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Setting max value as re-validation time. Reason: Selected re-validation time(" + revalidationTime + ") is after 24 hour");
                }
            }
        }

        response.setRevalidationTime(revalidationTime, executionContext.getCurrentTime().getTimeInMillis());

        List<MSCC> msccs = response.getGrantedMSCCs();
        for(MSCC mscc:msccs){
            if(ResultCode.SUCCESS == mscc.getResultCode()) {
                long calculatedPulse = RnCPulseCalculator.ceil(response.getSessionTimeOut(), mscc.getGrantedServiceUnits().getTimePulse());
                long calculatedTime = RnCPulseCalculator.multiply(calculatedPulse, mscc.getGrantedServiceUnits().getTimePulse());
                if(calculatedTime < mscc.getGrantedServiceUnits().getTime()){
                    mscc.getGrantedServiceUnits().setTime(calculatedTime);
                    mscc.setFinalUnitIndiacation(null);
                }else {
                    mscc.setValidityTime(response.getSessionTimeOut());
                }
            }
        }
    }

    private RnCPackage getBasePackage(PCRFRequest pcrfRequest) {
    	
        String subscriberPackage = null;

        if(Objects.nonNull(pcrfRequest.getSPRInfo())) {
            subscriberPackage = pcrfRequest.getSPRInfo().getProductOffer();
        }

        String subscriberId = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

        if (Objects.isNull(subscriberPackage)) {
            if (getLogger().isErrorLogLevel()){
                getLogger().error(MODULE, "Rejecting request for subscriber ID: " + subscriberId +
                        ". Reason: RnC package information not found in subscriber profile");
            }
            return null;
        }

        ProductOffer productOffer = getServerContext().getPolicyRepository().getProductOffer().byName(subscriberPackage);
        if(Objects.isNull(productOffer)){
            if (getLogger().isErrorLogLevel()){
                getLogger().error(MODULE, "Rejecting request for subscriber ID: " + subscriberId + ". Reason: product offer(" + subscriberPackage + ") not found in policy repository");
            }
            return null;
        }

        List<ProductOfferServicePkgRel> productOfferServicePkgRelList = productOffer.getProductOfferServicePkgRelDataList();
        if(Collectionz.isNullOrEmpty(productOfferServicePkgRelList)){
            if (getLogger().isErrorLogLevel()){
                getLogger().error(MODULE, "Not applying base RnC for subscriber ID: " + subscriberId + ". Reason: RnC package not configured in product offer(" + subscriberPackage + ")");
            }
            return null;
        }

        RnCPackage rncPackage = null;
        for (ProductOfferServicePkgRel productOfferServicePkgRel : productOfferServicePkgRelList) {
            Service service = productOfferServicePkgRel.getServiceData();
            RnCPackage selectedRncPackage = (RnCPackage) productOfferServicePkgRel.getRncPackageData();

            if (service.getId().equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SERVICE.val))) {
                rncPackage = selectedRncPackage;
                break;
            } else {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Not applying base RnC package(" + selectedRncPackage.getName() + ") for subscriber ID: " + subscriberId + ". Reason: RnC package is for service " + service.getId());
                }
            }
        }

        if (Objects.isNull(rncPackage)) {
            if (getLogger().isDebugLogLevel()){
                getLogger().debug(MODULE, "Not applying base RnC package for subscriber ID: " + subscriberId + ". Reason: subscriber RnC package(" + subscriberPackage + ") not found in policy repository");
            }
            return null;
        }

        if (rncPackage.getPolicyStatus() == PolicyStatus.FAILURE) {
            if (getLogger().isDebugLogLevel()){
                getLogger().debug(MODULE, "Not applying base RnC package for subscriber ID: " + subscriberId
                        + ". Reason: subscriber RnC package(" + subscriberPackage + ") has status FAILURE. Reason: "
                        + rncPackage.getFailReason());
            }
            return null;
        }

        return rncPackage;
    }

	@Override
	protected void postProcess(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {

	    PCRFResponse response = (PCRFResponse) serviceResponse;
	    boolean isSuccess = false;

        List<MSCC> grantedMSCC = response.getGrantedMSCCs();
        if(grantedMSCC != null) {
            for (MSCC mscc : grantedMSCC) {
                if (ResultCode.SUCCESS == mscc.getResultCode()) {
                    isSuccess = true;
                    break;
                }
            }
        }

        if(isSuccess == false){
	        response.setGrantedMSCCs(null);
            response.setAttribute(PCRFKeyConstants.RESULT_CODE.val, ResultCode.DIAMETER_END_USER_SERVICE_DENIED.val);
        }
	}

	@Override
	protected boolean isApplicable(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
        PCRFRequest pcrfRequest = (PCRFRequest) serviceRequest;

        boolean isRadiusSession = SessionTypeConstant.RADIUS.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val));
        boolean isRoSession = SessionTypeConstant.RO.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val));

        if(isRadiusSession==false && isRoSession==false){
            return false;
        }

        boolean isNotRadiusDataServiceRequest = isRadiusSession && PCRFKeyValueConstants.DATA_SERVICE_ID.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SERVICE.val)) == false;
        return ((isRoSession || isNotRadiusDataServiceRequest)
                && (pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_START) || pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_UPDATE)));
       
	}

	@Override
	public String getName() {
		return "RnC-HANDLER";
	}
}

