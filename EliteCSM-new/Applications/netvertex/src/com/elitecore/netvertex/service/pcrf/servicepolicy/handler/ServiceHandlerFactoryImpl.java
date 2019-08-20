package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import java.util.Objects;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.core.session.SessionOperation;
import com.elitecore.netvertex.rnc.DataRnCHandler;
import com.elitecore.netvertex.rnc.EventRnCHandler;
import com.elitecore.netvertex.rnc.QuotaReportHandlerFactory;
import com.elitecore.netvertex.rnc.ReportHandler;
import com.elitecore.netvertex.rnc.RnCHandler;
import com.elitecore.netvertex.rnc.RnCQuotaReportHandlerFactory;
import com.elitecore.netvertex.rnc.RnCReportHandler;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.PccServicePolicyConfiguration;
import com.elitecore.netvertex.usagemetering.UMHandler;

/**
 * The ServiceHandlerFactory is responsible for creating service handler of given type.
 * 
 * @author harsh.patel
 * @author chetan.sankhala
 */
public class ServiceHandlerFactoryImpl implements ServiceHandlerFactory {

	private PCRFServiceContext pcrfServiceContext;
	private SessionOperation sessionOperation;
	
	private SessionHandler sessionHandler;
	private UMHandler umHandler;
	private AutoSubscriptionHandler autoSubscriptionHandler;


	public ServiceHandlerFactoryImpl(PCRFServiceContext pcrfServiceContext,
			SessionOperation sessionOperation) {
		
		this.pcrfServiceContext = pcrfServiceContext;
		this.sessionOperation = sessionOperation;
	}
	
	@Override
	public ServiceHandler serviceHandlerOf(ServiceHandlerType serviceHandlerType, PccServicePolicyConfiguration servicePolicyConfiguration) throws InitializationFailedException, IllegalArgumentException {
			
		switch (serviceHandlerType) {
		
		case SUBSCRIBER_PROFILE_HANDLER:
	
			SubscriberProfileHandler subscriberProfileHandler = new SubscriberProfileHandler(pcrfServiceContext, servicePolicyConfiguration);
			subscriberProfileHandler.init();
			return subscriberProfileHandler;

		case EMEREGENCY_POLICY_HANDLER:
			
			EmergencyPolicyHandler emergencyPolicyHandler = new EmergencyPolicyHandler(pcrfServiceContext); 
			emergencyPolicyHandler.init();
			return emergencyPolicyHandler;
		
		case AUTHENTICATION_HANDLER:
			AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, servicePolicyConfiguration);
			authenticationHandler.init();
			return authenticationHandler;
					
		case USAGE_METERING_HANDLER :
				if (umHandler == null) {
					umHandler = new UMHandler(pcrfServiceContext);
					umHandler.init();
				}

			return umHandler;

		case DATA_RNC_REPORT_HANDLER:

			ReportHandler reportHandler = new ReportHandler(pcrfServiceContext, new QuotaReportHandlerFactory(pcrfServiceContext.getServerContext().getPolicyRepository()));
			reportHandler.init();
			return reportHandler;

		case RNC_REPORT_HANDLER:

			RnCReportHandler rncReportHandler = new RnCReportHandler(pcrfServiceContext, new RnCQuotaReportHandlerFactory(pcrfServiceContext.getServerContext().getPolicyRepository()));
			rncReportHandler.init();
			return rncReportHandler;

		case AUTO_SUBSCRIPTION_HANDLER:
			if(Objects.isNull(this.autoSubscriptionHandler)) {
				autoSubscriptionHandler = new AutoSubscriptionHandler(pcrfServiceContext);
			}
			return autoSubscriptionHandler;
			
		case SUBSCRIBER_POLICY_HANDLER:

			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(pcrfServiceContext, new SubscriberPolicySelectionEngine(pcrfServiceContext), new BoDPackageHandler());
			subscriberPolicyHandler.init();
			return subscriberPolicyHandler;
		
		case IMS_POLICY_HANDLER:
			
			IMSPolicyHandler imsPackageProcesser = new  IMSPolicyHandler(pcrfServiceContext);
			imsPackageProcesser.init();
			return imsPackageProcesser;

		case DATA_RNC_HANDLER:

			DataRnCHandler dataRnCHandler = new DataRnCHandler(pcrfServiceContext);
			dataRnCHandler.init();
			return dataRnCHandler;
			
		case RNC_HANDLER:

			RnCHandler rncHandler = new RnCHandler(pcrfServiceContext);
			rncHandler.init();
			return rncHandler;		
				
		case SESSION_HANDLER:
				if(sessionHandler == null){
					sessionHandler = new SessionHandler(pcrfServiceContext, sessionOperation);
					sessionHandler.init();
				}
				return sessionHandler;
			
		case PCRF_SY_HANDLER:
				PCRFSyHandler pcrfSyHandler = new PCRFSyHandler(servicePolicyConfiguration.getName(), pcrfServiceContext, servicePolicyConfiguration.getSyGateway(),servicePolicyConfiguration.getSyMode(), sessionOperation.getSessionLocator());
				pcrfSyHandler.init();
				return pcrfSyHandler;
				
		case POLICY_CDR_HANDLER:
				CDRHandler cdrHandler = new CDRHandler(pcrfServiceContext, servicePolicyConfiguration.getPolicyCdrDriver());
				cdrHandler.init();
				return cdrHandler;

		case CHARGING_CDR_HANDLER:
				cdrHandler = new CDRHandler(pcrfServiceContext, servicePolicyConfiguration.getChargingCdrDriver());
				cdrHandler.init();
				return cdrHandler;

			case EVENT_RNC_HANDLER:
				EventRnCHandler eventRnCHandler = new EventRnCHandler(pcrfServiceContext);
				eventRnCHandler.init();
				return eventRnCHandler;
		
		default: 
				throw new IllegalArgumentException("Invalid Service Handler Type");
		}
	}
}
