package com.elitecore.aaa.diameter.service.application.handlers;

import javax.annotation.Nonnull;

import com.elitecore.aaa.core.drivers.AcctCommunicatorGroupImpl;
import com.elitecore.aaa.core.drivers.IEliteAcctDriver;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterCDRHandlerEntryData;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;
import com.elitecore.aaa.radius.conf.impl.SecondaryAndCacheDriverDetail;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.plugins.script.DriverScript;
import com.elitecore.core.commons.plugins.script.NullDriverScript;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseSingleExecutionAsyncTask;
import com.elitecore.core.serverx.manager.scripts.ScriptContext;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.ESCommunicator;

/**
 * 
 * @author narendra.pathai
 */
public class DiameterCDRHandler implements DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> {

	private static final String MODULE = "DIAMETER-CDR-HNDLR";
	private DiameterServiceContext context;
	private DiameterCDRHandlerEntryData data;
	private AcctCommunicatorGroupImpl communicatorGroup;
	@Nonnull 
	private DriverScript driverScript;
	
	public DiameterCDRHandler(final DiameterServiceContext context, 
			DiameterCDRHandlerEntryData data) {
		this.context = context;
		this.data = data;
		this.driverScript = new NullDriverScript(new ScriptContext() {
			
			@Override
			public ServerContext getServerContext() {
				return context.getServerContext();
			}
		});
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing CDR Handler for policy: " + data.getPolicyName());
		}
		
		communicatorGroup = new AcctCommunicatorGroupImpl(context);
		
		int failedDriverCount = 0;
		for (PrimaryDriverDetail primaryDriverDetail : data.getDriverDetails().getPrimaryDriverGroup()) {
			ESCommunicator driver = context.getDriver(primaryDriverDetail.getDriverInstanceId());
			if (driver == null) {
				failedDriverCount++;
				continue;
			}
			communicatorGroup.addCommunicator((IEliteAcctDriver) driver, primaryDriverDetail.getWeightage());
		}
		
		for (SecondaryAndCacheDriverDetail secondaryDriverDetail : data.getDriverDetails().getSecondaryDriverGroup()) {
			ESCommunicator driver = context.getDriver(secondaryDriverDetail.getSecondaryDriverId());
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
		
		initDriverScript();
		
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Successfully initialized CDR Handler for policy: " + data.getPolicyName());
		}
	}
	

	private void initDriverScript() {
		if (Strings.isNullOrBlank(data.getDriverDetails().getDriverScript())) {
			return;
		}
		
		try {
			driverScript = context.getServerContext().getExternalScriptsManager().getScript(data.getDriverDetails().getDriverScript().trim(),
					DriverScript.class);
		} catch (IllegalArgumentException ex) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Cannot use driver script for handler: " + data.getHandlerName() + ". Reason: " + ex.getMessage());
			}

			LogManager.getLogger().trace(ex);
		}
	}

	@Override
	public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
		return true;
	}

	@Override
	public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
		if (data.isWait()) {
			processInSync(request, response);
		} else {
			processInBackground(request, response);
		}
	}

	private void processInBackground(ApplicationRequest request, ApplicationResponse response) {
		// TODO implement clone and use it
//		ApplicationRequest clonedRequest = (ApplicationRequest) request.clone();
//		ApplicationResponse clonedResponse = (ApplicationResponse) response.clone();
//		context.getServerContext().getTaskScheduler()
//		.scheduleSingleExecutionTask(new CDRGenerationTask(clonedRequest, clonedResponse));
		
		context.getServerContext().getTaskScheduler()
			.scheduleSingleExecutionTask(new CDRGenerationTask(request, response));
	}

	private void processInSync(ApplicationRequest request, ApplicationResponse response) {
		try {
			preDriverProcessing(request, response);
			process(request, response);
		} catch (DriverProcessFailedException ex) {
			LogManager.getLogger().trace(MODULE, "Driver Process Failed: Reason:" + ex.getMessage());
			DiameterProcessHelper.dropResponse(response);
		} finally {
			postDriverProcessing(request, response);
		}
	}

	private void postDriverProcessing(ApplicationRequest request, ApplicationResponse response) {
		driverScript.postDriverProcessing(request, response);
	}

	private void process(ApplicationRequest request, ApplicationResponse response) throws DriverProcessFailedException {
		communicatorGroup.handleAccountingRequest(request, response);
	}

	private void preDriverProcessing(ApplicationRequest request, ApplicationResponse response) {
		driverScript.preDriverProcessing(request, response);
	}

	@Override
	public void reInit() throws InitializationFailedException {

	}
	
	class CDRGenerationTask extends BaseSingleExecutionAsyncTask {

		@Nonnull private final ApplicationRequest clonedRequest;
		@Nonnull private final ApplicationResponse clonedResponse;

		public CDRGenerationTask(@Nonnull ApplicationRequest clonedRequest,
				@Nonnull ApplicationResponse clonedResponse) {
			this.clonedRequest = clonedRequest;
			this.clonedResponse = clonedResponse;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			processInSync(clonedRequest, clonedResponse);
		}
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return communicatorGroup.isAlive() == false;
	}
}
