package com.elitecore.aaa.diameter.service.application.handlers;

import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.core.drivers.AcctCommunicatorGroup;
import com.elitecore.aaa.core.drivers.AcctCommunicatorGroupImpl;
import com.elitecore.aaa.core.drivers.IEliteAcctDriver;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.NasServicePolicyConfiguration;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.ReInitializable;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.plugins.script.DriverScript;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

public class NasAcctHandler implements DiameterApplicationHandler<ApplicationRequest, ApplicationResponse>, ReInitializable{
	private static final String MODULE = "NAS-ACCT-HDL";
	private AcctCommunicatorGroup communicatorGroup;
	private DiameterServiceContext serviceContext;
	private NasServicePolicyConfiguration policyConfiguration;
	public NasAcctHandler(DiameterServiceContext serviceContext,NasServicePolicyConfiguration policyConfiguration) {
		this.serviceContext = serviceContext;
		this.policyConfiguration = policyConfiguration;
	}
	
	public void init() throws InitializationFailedException {
		
		this.communicatorGroup = new AcctCommunicatorGroupImpl(serviceContext);
		
		Map<String, Integer> driverInstanceMap = policyConfiguration.getAcctDriverInstanceIdsMap();
		
		for (Entry<String, Integer> driverDetail :driverInstanceMap.entrySet()) {
			
		IEliteAcctDriver acctDriver = (IEliteAcctDriver)serviceContext.getServerContext().getDiameterDriver(driverDetail.getKey());
			if (acctDriver != null) {
				communicatorGroup.addCommunicator(acctDriver, driverDetail.getValue());
			} else {
				LogManager.getLogger().warn(MODULE,"Problem in initializing Driver For Driver " +
						"Instance Id :"+driverDetail.getKey()+" Reason :Driver Not Found");
			}
		}
	}
	
	@Override
	public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {	
		try{
			//calling the pre driver script processing before any validation
			preDriverProcessing(request, response);
			
			IDiameterAVP avpNumber = request.getAVP(DiameterAVPConstants.ACCOUNTING_RECORD_NUMBER);
			if(avpNumber== null)
				throw new DriverProcessFailedException("Invalid request: AccountingRecordNumberAvp Not found in request");
			
			response.addAVP(avpNumber);
			
			communicatorGroup.handleAccountingRequest(request,response);

			IDiameterAVP diameterAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
			diameterAvp.setInteger(ResultCode.DIAMETER_SUCCESS.code);
			response.addAVP(diameterAvp);
			
		}catch(DriverProcessFailedException e){
			LogManager.getLogger().trace(MODULE, "Driver Process Failed: Reason:" + e.getMessage());
			DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_OUT_OF_SPACE, DiameterErrorMessageConstants.DIAMETER_OUT_OF_SPACE);
		}finally{
			postDriverProcessing(request, response);
		}
	}
	
	private void postDriverProcessing(ServiceRequest serviceRequest, ServiceResponse serviceResponse){
		if(policyConfiguration.getAcctDriverScript() != null && policyConfiguration.getAcctDriverScript().trim().length() > 0){
			try {
				serviceContext.getServerContext().getExternalScriptsManager().execute(policyConfiguration.getAcctDriverScript(), DriverScript.class, "postDriverProcessing", new Class<?>[]{ServiceRequest.class, ServiceResponse.class}, new Object[]{serviceRequest, serviceResponse});
			} catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Error in executing  \"post\" method of driver script" + policyConfiguration.getAcctDriverScript() + ". Reason: " + e.getMessage());
				
				LogManager.getLogger().trace(e);
			}
		}
	}
	
	private void preDriverProcessing(ServiceRequest serviceRequest, ServiceResponse serviceResponse){
		if(policyConfiguration.getAcctDriverScript() != null && policyConfiguration.getAcctDriverScript().trim().length() > 0){
			try {
				serviceContext.getServerContext().getExternalScriptsManager().execute(policyConfiguration.getAcctDriverScript(), DriverScript.class, "preDriverProcessing", new Class<?>[]{ServiceRequest.class, ServiceResponse.class}, new Object[]{serviceRequest, serviceResponse});
			} catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Error in executing  \"pre\" method of driver script" + policyConfiguration.getAcctDriverScript() + ". Reason: " + e.getMessage());
				
				LogManager.getLogger().trace(e);
			}
		}
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		this.policyConfiguration = (NasServicePolicyConfiguration)serviceContext.getDiameterServiceConfigurationDetail().getDiameterServicePolicyConfiguration(policyConfiguration.getId());		
	}

	@Override
	public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
		return true;
	}


	@Override
	public boolean isResponseBehaviorApplicable() {
		return communicatorGroup.isAlive() == false;
	}
}
