package com.elitecore.netvertex.service.pcrf.impl;

import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.netvertex.core.transaction.Transaction;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.scripts.DiameterGroovyScript;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFResponseListner;

public class PCRFResponseListnerImpl implements PCRFResponseListner {
	private static final String MODULE = "PCRF-RES-LSNR";
	private DiameterGatewayControllerContext context;
	public PCRFResponseListnerImpl(DiameterGatewayControllerContext context){
		this.context = context;
	}
	
	@Override
	public void responseReceived(PCRFResponse response) {
		Transaction transaction =  this.context.removeTransaction(response.getAttribute(PCRFKeyConstants.TRANSACTION_ID.getVal()));
		
		DiameterGatewayConfiguration configuration = context.getGatewayConfigurationByName(response.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val));
		if(configuration != null){
			List<DiameterGroovyScript> scripts = context.getDiameterGroovyScripts(configuration.getName());
			if(scripts != null && !scripts.isEmpty()){
				for(DiameterGroovyScript script : scripts){
					try{
						script.preSend(response);
					}catch(Exception ex){
						LogManager.getLogger().error(MODULE, "Error in executing script \""+script.getName()+"\" for PCRF-Packet with Session-ID= " 
							+ response.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val) +" for gateway = "+ configuration.getName() +". Reason: "+ ex.getMessage());
						LogManager.getLogger().trace(MODULE, ex);
					}
					
				}
			}
		}
		
		//FIXME-Harsh, Vicky, Fetch Application ID from PCRFResponse packet instead of using application id of Gx.
		if(context.getStackContext().hasSession(response.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val), ApplicationIdentifier.TGPP_GX_29_212_18.getApplicationId())){
			//adding addOns
			//FIXME-Harsh, Vicky, Fetch Application ID from PCRFResponse packet instead of using application id of Gx.
			Session session = context.getStackContext().getOrCreateSession(response.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val), ApplicationIdentifier.TGPP_GX_29_212_18.getApplicationId());
			String addOnString = response.getAttribute(PCRFKeyConstants.CS_ADD_ONS.val);
			if(addOnString != null && addOnString.isEmpty() == false){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Set addOns "+ addOnString +" in diameter session for Core-Session-ID:" + response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));
				session.setParameter(PCRFKeyConstants.CS_ADD_ONS.val,addOnString);
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "No addOn found from PCRFResponse for Core-Session-ID:" 
							+ response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val) 
							+", remove previously provided addOns from diameter session");
				session.removeParameter(PCRFKeyConstants.CS_ADD_ONS.val);
			}
			
			//adding Subscriber Package
			String subscriberPackage = response.getAttribute(PCRFKeyConstants.SUB_DATA_PACKAGE.val);
			if(subscriberPackage != null && subscriberPackage.isEmpty() == false){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Set Subscriber Package "+ subscriberPackage +" in diameter session for Core-Session-ID:" + response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));
				session.setParameter(PCRFKeyConstants.SUB_DATA_PACKAGE.val,subscriberPackage);
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Subscriber Package not found from PCRFResponse for Core-Session-ID:" 
							+ response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val) 
							+", remove previously provided subscriber package from diameter session");
				session.removeParameter(PCRFKeyConstants.SUB_DATA_PACKAGE.val);
			}
			
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Unable to set addOns,subscriberPacakge in diameter session. Reason: Diameter session not found for Core-Session-ID:" + response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));
		}
		

		
		if(transaction != null){
			transaction.resume(response);
		}else{
			LogManager.getLogger().warn(MODULE, "Could not resume transaction : " + response.getAttribute(PCRFKeyConstants.TRANSACTION_ID.getVal()));
		}
	}

}
