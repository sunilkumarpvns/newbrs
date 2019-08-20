package com.elitecore.aaa.radius.service.auth.handlers;

import java.util.ArrayList;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.threegpp.auth.RadAuth3GPP2Handler;
import com.elitecore.aaa.radius.wimax.auth.RadAuthWimaxHandler;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RadAuthVendorSpecificServiceHandler  implements RadAuthServiceHandler{
	public static final String MODULE = "RAD-VS-HANDLER";
	private RadAuthServiceContext serviceContext;
	private ArrayList<RadAuthVendorSpecificHandler> radServiceHandlers;
	private ArrayList<RadAuthVendorSpecificHandler> dummyRatingHandlers;
	private boolean isWimaxEnabled;
	private boolean is3GPPEnabled;
	
 	public RadAuthVendorSpecificServiceHandler(RadAuthServiceContext serviceContext, boolean isWimaxEnabled, boolean is3GPPEnabled) {	
		this.serviceContext = serviceContext;
		radServiceHandlers = new ArrayList<RadAuthVendorSpecificHandler>(3);
		dummyRatingHandlers = new ArrayList<RadAuthVendorSpecificHandler>(3);
		this.isWimaxEnabled = isWimaxEnabled;
		this.is3GPPEnabled = is3GPPEnabled;
		if(isWimaxEnabled){
			radServiceHandlers.add(new RadAuthWimaxHandler(serviceContext, 
					serviceContext.getServerContext().getServerConfiguration().getWimaxConfiguration(),
					serviceContext.getServerContext().getServerConfiguration().getSpiKeysConfiguration(), 
					serviceContext.getServerContext().getWimaxSessionManager(),
					serviceContext.getServerContext().getEapSessionManager(), 
					serviceContext.getServerContext().getKeyManager()));
		}
		if(is3GPPEnabled){
			radServiceHandlers.add(new RadAuth3GPP2Handler(serviceContext));
		}
		
	}
	public void init() throws InitializationFailedException{
		final int size = radServiceHandlers.size();
		for(int i=0;i<size;i++){
			this.radServiceHandlers.get(i).init();
		}
		
		/* special care has been taken for dummy rating handlers as if it cannot initialize the service policy
		 * should successfully initialize
		 * 
		 */
		initializeDummyRatingHandlers();
	}

	private void initializeDummyRatingHandlers(){
		boolean bIsDummyRatingEnabled = false;
		dummyRatingHandlers = new ArrayList<RadAuthVendorSpecificHandler>(2);
		if(System.getProperty(AAAServerConstants.DUMMY_RATING)!=null){
			String strDummyEnabled = System.getProperty(AAAServerConstants.DUMMY_RATING);
			 bIsDummyRatingEnabled = strDummyEnabled.equalsIgnoreCase("true") || strDummyEnabled.equalsIgnoreCase("yes");
		}
		
		if(bIsDummyRatingEnabled){
			if(isWimaxEnabled){
				RadAuthVendorSpecificHandler wimaxDummyRatingHandler = new WiMAXDummyRatingHandler(serviceContext); 
				try{
					wimaxDummyRatingHandler.init();
					dummyRatingHandlers.add(wimaxDummyRatingHandler);
				}catch(InitializationFailedException ex){
					LogManager.getLogger().debug(MODULE, ex.getMessage());
					LogManager.getLogger().trace(ex);
				}
			}
			if(is3GPPEnabled){
				RadAuthVendorSpecificHandler threeGPP2DummyRatingHandler = new ThreeGPP2DummyRatingHandler(serviceContext); 
				try{
					threeGPP2DummyRatingHandler.init();
					dummyRatingHandlers.add(threeGPP2DummyRatingHandler);
				}catch(InitializationFailedException ex){
					LogManager.getLogger().debug(MODULE, ex.getMessage());
					LogManager.getLogger().trace(ex);
				}
			}
		}
	}
	@Override
	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		return true;
	}
	
	@Override
	public void handleRequest(RadAuthRequest request,RadAuthResponse response, ISession session){
		request.setParameter(AAAServerConstants.WIMAX_ENABLED, isWimaxEnabled);
		
		if(response.getPacketType() == RadiusConstants.ACCESS_ACCEPT_MESSAGE){
			for (RadAuthVendorSpecificHandler authServiceHandlers : radServiceHandlers) {
				if (authServiceHandlers.isEligible(request)) {				
					authServiceHandlers.handleRequest(request, response);
					break;				
				}
			}
			Integer hotlineApplicable = (Integer)response.getParameter(RadiusConstants.HOTLINE_APPLICABLE);
			//this check will do the task of skipping the dummy rating processing if hotlining was applied
			if(hotlineApplicable == null || hotlineApplicable != RadiusConstants.PROFILE_BASED_HOTLINING){
				for (RadAuthVendorSpecificHandler dummyRatingHandler : dummyRatingHandlers) {
					if (dummyRatingHandler.isEligible(request)) {				
						dummyRatingHandler.handleRequest(request, response);
						break;				
					}
				}
			}
		}
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		
	}
	
	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}
}
