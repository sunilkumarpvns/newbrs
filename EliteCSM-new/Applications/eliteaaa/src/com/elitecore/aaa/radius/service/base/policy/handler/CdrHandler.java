package com.elitecore.aaa.radius.service.base.policy.handler;

import javax.annotation.Nonnull;

import com.elitecore.aaa.core.drivers.AcctCommunicatorGroup;
import com.elitecore.aaa.core.drivers.AcctCommunicatorGroupImpl;
import com.elitecore.aaa.core.drivers.IEliteAcctDriver;
import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;
import com.elitecore.aaa.radius.conf.impl.SecondaryAndCacheDriverDetail;
import com.elitecore.aaa.radius.service.RadServiceContext;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.CdrHandlerEntryData;
import com.elitecore.aaa.radius.service.handlers.RadServiceHandler;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.plugins.script.DriverScript;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseSingleExecutionAsyncTask;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * 
 * Provides CDR dumping capability using accounting drivers. 
 * 
 * <p>Supported Features:
 * 
 * <ul>
 * <li>sync as well as async dumping of CDRs</li>
 * <li>pre and post driver script</li>
 * </ul>
 * 
 * @author kuldeep.panchal
 * @author narendra.pathai
 *
 * @param <T> type of request packet
 * @param <V> type of response packet
 * 
 */
public abstract class CdrHandler<T extends RadServiceRequest, V extends RadServiceResponse>
implements RadServiceHandler<T, V> {

	private final RadServiceContext<T, V> serviceContext;
	private final CdrHandlerEntryData data;
	private AcctCommunicatorGroup communicatorGroup;
	
	public CdrHandler(RadServiceContext<T, V> serviceContext, CdrHandlerEntryData cdrHandlerEntryData) {
		this.serviceContext = serviceContext;
		this.data = cdrHandlerEntryData;
	}
	
	@Override
	public boolean isEligible(T request, V response) {
		return true;
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return communicatorGroup.isAlive() == false;
	}

	
	/**
	 * Initializes the group of drivers configured
	 * 
	 * @throws InitializationFailedException if all drivers fail to initialize
	 */
	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(getModuleName(), "Initializing Accounting CDR Handler for policy: " + data.getPolicyName());
		}
		communicatorGroup = new AcctCommunicatorGroupImpl(serviceContext);
		
		int failedDriverCount = 0;
		for (PrimaryDriverDetail primaryDriverDetail : data.getDriverDetails().getPrimaryDriverGroup()) {
			ESCommunicator driver = serviceContext.getDriver(primaryDriverDetail.getDriverInstanceId());
			if (driver == null) {
				failedDriverCount++;
				continue;
			}
			communicatorGroup.addCommunicator((IEliteAcctDriver) driver, primaryDriverDetail.getWeightage());
		}
		
		for (SecondaryAndCacheDriverDetail secondaryDriverDetail : data.getDriverDetails().getSecondaryDriverGroup()) {
			ESCommunicator driver = serviceContext.getDriver(secondaryDriverDetail.getSecondaryDriverId());
			if (driver == null) {
				failedDriverCount++;
				continue;
			}
			communicatorGroup.addCommunicator((IEliteAcctDriver) driver, 0);
		}
		
		if (failedDriverCount == data.getDriverDetails().getPrimaryDriverGroup().size()
				+ data.getDriverDetails().getSecondaryDriverGroup().size()) {
			throw new InitializationFailedException("Problem in initializing CDR Generation Handler, " +
					"Reason: No primary or secondary driver found.");
		}
		
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(getModuleName(), "Successfully initialized Accounting CDR Handler for policy: " + data.getPolicyName());
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void handleRequest(T request, V response, ISession session) {
		IRadiusAttribute acctStatusTypeAttr = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE);
		if(acctStatusTypeAttr!=null){
			int iAcctStatusType = acctStatusTypeAttr.getIntValue();
			if(iAcctStatusType == RadiusAttributeValuesConstants.ACCOUNTING_ON || iAcctStatusType == RadiusAttributeValuesConstants.ACCOUNTING_OFF) {
				//In case of accounting on and off the processing of this handler will be skipped
				response.setPacketType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE);
				return;
			}	
		}
		
		if (data.isWait()) {
			process(request, response);
		} else {
			T clonedRequest = (T) request.clone();
			V clonedResponse = (V) response.clone();
			serviceContext.getServerContext().getTaskScheduler()
			.scheduleSingleExecutionTask(new CDRGenerationTask(clonedRequest, clonedResponse));
		}
	}

	private void process(T request, V response) {
		try {
			preDriverProcessing(request, response);
			communicatorGroup.handleAccountingRequest(request, response);
		} catch (DriverProcessFailedException e) {
			LogManager.getLogger().trace(getModuleName(), "Driver Process Failed: Reason:" + e.getMessage());
			response.markForDropRequest();
			response.setFurtherProcessingRequired(false);
		} finally {
			//the call to the script MUST be there even if successful or failed driver processing
			postDriverProcessing(request, response);
		}
	}

	private void preDriverProcessing(T serviceRequest, V serviceResponse){
		if(data.getDriverDetails().getDriverScript() != null && data.getDriverDetails().getDriverScript().trim().length() > 0){
			try {
				serviceContext.getServerContext().getExternalScriptsManager().execute(data.getDriverDetails().getDriverScript(), DriverScript.class, "preDriverProcessing", new Class<?>[]{ServiceRequest.class, ServiceResponse.class}, new Object[]{serviceRequest, serviceResponse});
			} catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(getModuleName(), "Error in executing  \"pre\" method of accounting driver script" + data.getDriverDetails().getDriverScript() + ". Reason: " + e.getMessage());
				
				LogManager.getLogger().trace(e);
			}
		}
	}
	
	private void postDriverProcessing(ServiceRequest serviceRequest, ServiceResponse serviceResponse){
		if(data.getDriverDetails().getDriverScript() != null && data.getDriverDetails().getDriverScript().trim().length() > 0){
			try {
				serviceContext.getServerContext().getExternalScriptsManager().execute(data.getDriverDetails().getDriverScript(), DriverScript.class, "postDriverProcessing", new Class<?>[]{ServiceRequest.class, ServiceResponse.class}, new Object[]{serviceRequest, serviceResponse});
			} catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(getModuleName(), "Error in executing  \"post\" method of accounting driver script" + data.getDriverDetails().getDriverScript() + ". Reason: " + e.getMessage());
				
				LogManager.getLogger().trace(e);
			}
		}
	}

	class CDRGenerationTask extends BaseSingleExecutionAsyncTask {

		@Nonnull private final T clonedRequest;
		@Nonnull private final V clonedResponse;

		public CDRGenerationTask(@Nonnull T clonedRequest,
				@Nonnull V clonedResponse) {
			this.clonedRequest = clonedRequest;
			this.clonedResponse = clonedResponse;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			process(clonedRequest, clonedResponse);
		}
	}
	
	public abstract String getModuleName(); 
}
