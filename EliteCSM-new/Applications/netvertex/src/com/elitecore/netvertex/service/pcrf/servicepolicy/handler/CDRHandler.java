package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.netvertex.core.driver.cdr.CDRCommunicatorGroup;
import com.elitecore.netvertex.core.driver.cdr.ValueProviderExtImpl;
import com.elitecore.netvertex.core.driver.cdr.impl.CDRCommunicatorGroupImpl;
import com.elitecore.netvertex.core.driver.spr.DriverConfiguration;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;

/**
 * @author Manjil Purohit
 *
 */
public class CDRHandler extends ServiceHandler {
	
	private static final String MODULE = "CDR-HDLR";
	
	private CDRCommunicatorGroup communicatorGroup;

	private DriverConfiguration driverConfiguration;
	
	public CDRHandler(PCRFServiceContext serviceContext, DriverConfiguration driverConfiguration) {
		super(serviceContext);
		communicatorGroup = new CDRCommunicatorGroupImpl();
		this.driverConfiguration = driverConfiguration;
	}
	
	@Override
	public void init() throws InitializationFailedException {
		CDRDriver cdrDriver = getServiceContext().getCDRDriver(driverConfiguration);

		communicatorGroup.addCommunicator(cdrDriver, 1);
	}
		
	@Override
	protected boolean isApplicable(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
		PCRFRequest pcrfRequest = (PCRFRequest)serviceRequest;
		
		return pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_START) || 
				 pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_UPDATE) ||
				 pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_STOP) ||
				 pcrfRequest.getPCRFEvents().contains(PCRFEvent.DIRECT_DEBITING) ||
				 pcrfRequest.getPCRFEvents().contains(PCRFEvent.REFUND_ACCOUNT) ||
				 pcrfRequest.getPCRFEvents().contains(PCRFEvent.USAGE_REPORT);
	}
	
	
	@Override
	protected void process(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) 
			LogManager.getLogger().debug(MODULE,"Processing request for CDR");

		try {
			communicatorGroup.handleRequest(new ValueProviderExtImpl((PCRFRequest) serviceRequest, (PCRFResponse) serviceResponse));
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error while processing request for CDR. Reason: " + e.getMessage());
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, e);
		}

	}

	@Override
	protected void preProcess(ServiceRequest serviceRequest,
			ServiceResponse serviceResponse, ExecutionContext executionContext) {
		// IGNORED
		
	}

	@Override
	protected void postProcess(ServiceRequest serviceRequest,
			ServiceResponse serviceResponse, ExecutionContext executionContext) {
		// IGNORED
		
	}

	@Override
	public String getName() {
		return "CDR";
	}

}
