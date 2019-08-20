package com.elitecore.aaa.radius.service.dynauth.handlers;

import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthRequest;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthResponse;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthServiceContext;
import com.elitecore.aaa.radius.service.dynauth.handlers.conf.StaticNasCommunicationHandlerData;
import com.elitecore.aaa.radius.service.dynauth.policy.handlers.DynAuthFilteredHandler;
import com.elitecore.aaa.radius.service.dynauth.policy.handlers.NasCommunicationHandler;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * <p>When any NAS is configured in Radius Dynauth Policy, this class is responsible to handle the COA
 * or DM request received.</p>
 * 
 * <p>If the filter is satisfied, then the communication handler for the configured NAS will be created.
 * And if the filter is not satisfied, then it will skip this communicator and hand the control over
 * Dynamic NAS Communicator.</p>
 * 
 * <pre>
 *                     STATIC NAS COMMUNICATOR
 * +-------------------------------------------------------+
 * |          FILTERED HANDLER                             |      
 * |   +-------------------+        +-------------------+  |      
 * |   | FILTER            |        | FILTER            |  |      
 * |   |      +---------+  |        |      +---------+  |  |      
 * |REQ|      |         |  |   NO   |      |         |  |  |  NO  
 * +-->|      | HANDLER |  |------->|      | HANDLER |  |--------> DYNAMIC NAS COMMUNICATOR 
 * |   |      |         |  |        |      |         |  |  |      
 * |   |      +----+----+  |        |      +---------+  |  |      
 * |   |           |       |        |           |       |  |      
 * |   +-----------|-------+        +-----------|-------+  |      
 * |        YES    |                        YES |          |      
 * +---------------|----------------------------|----------+      
 *                 |                            |                 
 *                 V                            V                 		
 *                 +-------> SKIP OTHER COMMUNICATOR ---> POST PROCESSING                    
 *  
 * </pre>
 * 
 * @author narendra pathai
 *
 */
public class StaticNASCommunicationHandler extends DynAuthChainHandler {

	private static final String MODULE = "STATIC-NAS-COMM-HNDLR";
	
	private final RadDynAuthServiceContext serviceContext;
	private final StaticNasCommunicationHandlerData data;

	public StaticNASCommunicationHandler(RadDynAuthServiceContext serviceContext, StaticNasCommunicationHandlerData data,
			ProcessingStrategy processingStrategy) {
		super(processingStrategy);
		this.serviceContext = serviceContext;
		this.data = data;
	}
	
	@Override
	public boolean isEligible(RadDynAuthRequest request, RadDynAuthResponse response) {
		return true;
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isInfoLogLevel()){
			LogManager.getLogger().info(MODULE, "Initializing Static Nas Communication handler for policy: " + data.getPolicyName());
		}
		for (ExternalCommunicationEntryData externalHandlerData : data.getProxyCommunicatioEntries()) {
			NasCommunicationHandler nasHandler = new NasCommunicationHandler(serviceContext,externalHandlerData);
			DynAuthFilteredHandler filterHandler = new DynAuthFilteredHandler(externalHandlerData.getRuleset(), nasHandler);
			filterHandler.init();
			addHandler(filterHandler);
		}
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Static Nas Communication handler for policy: " + data.getPolicyName());
		}
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
	
	}
}
