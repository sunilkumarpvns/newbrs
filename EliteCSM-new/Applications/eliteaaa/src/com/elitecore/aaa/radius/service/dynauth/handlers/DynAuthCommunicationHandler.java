package com.elitecore.aaa.radius.service.dynauth.handlers;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;	
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthServiceContext;
import com.elitecore.aaa.radius.service.dynauth.handlers.conf.StaticNasCommunicationHandlerData;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * Responsible for creating Static NAS communication handler and Dynamic NAS communication handler
 * and initializing them. Both the handlers are added to the chain in the sequence, first static and 
 * then dynamic because if we have configured NAS in Dynauth policy then request should be send to
 * the configured NAS and dynamic NAS creation is not required.
 * 
 * <pre>
 *                     STATIC NAS COMMUNICATOR
 * +-------------------------------------------------------+
 * |          FILTERED HANDLER                             |      DYNAMIC NAS COMMUNICATOR
 * |   +-------------------+        +-------------------+  |      +-----------------------+
 * |   | FILTER            |        | FILTER            |  |      |                       |
 * |   |      +---------+  |        |      +---------+  |  |      |      +---------+      |
 * |REQ|      |         |  |   NO   |      |         |  |  |  NO  |      |         |      |  DYNAMIC NAS
 * +-->|      | HANDLER |  |------->|      | HANDLER |  |-------->|      | HANDLER |      |--------------> DROP 
 * |   |      |         |  |        |      |         |  |  |      |      |         |      |   NOT FOUND
 * |   |      +----+----+  |        |      +---------+  |  |      |      +---------+      |
 * |   |           |       |        |           |       |  |      |        YES |          |
 * |   +-----------|-------+        +-----------|-------+  |      +------------|----------+
 * |        YES    |                        YES |          |                   |
 * +---------------|----------------------------|----------+                   |
 *                 |                            |                              |
 *                 V                            V                              V
 *                 +----------------> SKIP OTHER COMMUNICATOR ---------> POST PROCESSING                    
 *  
 * </pre>
 * 
 * @author narendra pathai
 *
 */
public class DynAuthCommunicationHandler extends DynAuthChainHandler {

	private final StaticNasCommunicationHandlerData staticNASCommunicationData;
	private final RadDynAuthServiceContext serviceContext;
	
	public DynAuthCommunicationHandler(RadDynAuthServiceContext serviceContext, 
			StaticNasCommunicationHandlerData staticNASCommunicationData) {
		super(new FirstCommunicatorSelectedStrategy());
		this.serviceContext = serviceContext;
		this.staticNASCommunicationData = staticNASCommunicationData;
	}
	
	@Override
	public void init() throws InitializationFailedException {
		super.init();
		
		StaticNASCommunicationHandler staticNASCommunicationHandler = new StaticNASCommunicationHandler(serviceContext,
						staticNASCommunicationData, new FirstCommunicatorSelectedStrategy());
		staticNASCommunicationHandler.init();
		
		DynamicNASCommunicationHandler dynamicNASCommunicationHandler = new DynamicNASCommunicationHandler(serviceContext);
		dynamicNASCommunicationHandler.init();
		
		addHandler(staticNASCommunicationHandler);
		addHandler(dynamicNASCommunicationHandler);
	}
	
	/**
	 * This Strategy is used to check that, further execution for request is required or not. 
	 * If any communicator selected, then it will skip further handler(NAS Communicator) executions.
	 * 
	 */
	static class FirstCommunicatorSelectedStrategy implements ProcessingStrategy {

		@Override
		public boolean shouldContinue(RadServiceRequest request, RadServiceResponse response) {
			
			Boolean isNasCommunicationFilterSelected = (Boolean)request.getParameter(RadiusConstants.NAS_COMMUNICATOR_SELECTED);
			
			return response.isFurtherProcessingRequired() && !response.isMarkedForDropRequest()
					&& isNasCommunicationFilterSelected == null ? true : isNasCommunicationFilterSelected == false;
		}
	}
}
