package com.elitecore.aaa.core.drivers;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.systemx.esix.ESCommunicatorGroupImpl;
import com.elitecore.diameterapi.core.common.TranslationFailedException;

public class TranslatorCommunicatorGroupImpl extends ESCommunicatorGroupImpl<IEliteCrestelRatingDriver> implements TranslatorCommunicatorGroup {
	private static final String MODULE = "TRNSLTR-COMM-GRP";
	public TranslatorCommunicatorGroupImpl(ServerContext serverContext) {
		super();
	}
	@Override
	public void translate(ApplicationRequest request,ApplicationResponse response) throws TranslationFailedException{
		IEliteCrestelRatingDriver communicator =  getCommunicator();
		try {
			if(communicator == null){
				LogManager.getLogger().error(MODULE, "No Alive Communicator found in group.");
				throw new TranslationFailedException("No Alive Communicator Found in the group");
			}
			communicator.handleRequest(request,response);
		} catch (TranslationFailedException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Primary Driver processing failed. Reason: " + e.getMessage());
			
			IEliteCrestelRatingDriver secondaryCommunicator = getSecondaryCommunicator(communicator);
			if(secondaryCommunicator == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "No secondary communicator found in group to handle request");
				throw e;
			}
			try{
				secondaryCommunicator.handleRequest(request,response); 
			}catch (TranslationFailedException e1) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Secondary Driver processing failed. Reason: " + e1.getMessage());
				throw e1;
			}
		}
	
		
	}


}
