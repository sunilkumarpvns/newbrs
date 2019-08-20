package com.elitecore.netvertex.gateway.radius;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyEnforcementMethod;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMapping;
import com.elitecore.netvertex.gateway.radius.RadiusGatewayController.RadiusGatewayEventListner;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.mapping.PCCtoRadiusMappingValueProvider;
import com.elitecore.netvertex.gateway.radius.scripts.RadiusGroovyScript;
import com.elitecore.netvertex.gateway.radius.utility.AvpAccumalators;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFResponseListner;

import java.util.List;

public class AcctPCRFResponseListnerImpl implements PCRFResponseListner {

	private static final String MODULE = "RAD-ACCT-RSPL";
	private RadiusGatewayControllerContext context;
	private RadiusGatewayEventListner radiusGatewayEventListner;

	public AcctPCRFResponseListnerImpl(RadiusGatewayControllerContext context,RadiusGatewayEventListner radiusGatewayEventListner){
		this.context = context;
		this.radiusGatewayEventListner = radiusGatewayEventListner;
	}

	@Override
	public void responseReceived(PCRFResponse response) {

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "PCRF Response: " + response);
		
		RadiusGateway gateway = context.getRadiusGateway(response.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal()));
		if(gateway == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Gateway configuration not found for Address: " + 
						response.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal()));
			}
			gateway = context.getRadiusGateway(response.getAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.getVal()));
		}

		if(gateway == null){
			LogManager.getLogger().error(MODULE, "CoA packet generation skipped. Reason: Gateway configuration not found for " +
						"Address: " + response.getAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.getVal()));
			return;
		}
		
		List<RadiusGroovyScript> scripts = context.getRadiusGroovyScripts(gateway.getIPAddress());
		if(scripts != null && scripts.isEmpty() == false){
			for(RadiusGroovyScript script : scripts){
				try{
					script.preSend(response);
				}catch(Exception ex){
					LogManager.getLogger().error(MODULE, "Error in executing script \""+script.getName()+"\" for gateway = " + gateway.getIPAddress() +". Reason: "+ ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
				}
				
			}
		}

		if(response.isMarkedForDropRequest()){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Dyna Auth Response sending skipped. Reason: " +
						"PCRF Response dropped");
			return;
		}
		
		if(PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equals(response.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal())) == false) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Sending Disconnection-Request-Message. Reason: PCRF " +
						"Result Code = "+ response.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
			}
			radiusGatewayEventListner.sendDisconnectMessage(response);
			return;
		}

		PolicyEnforcementMethod enforcementMethod = gateway.getConfiguration().getPolicyEnforcementMethod();
		if(PolicyEnforcementMethod.COA == enforcementMethod || PolicyEnforcementMethod.ACCESS_ACCEPT == enforcementMethod){
			
			/* This flag will be TRUE, when Session Auth Rule changed for Subscriber Package or BoD Package.
			 * isPolicyChanged() states , whether session Auth rule is changed or not, from PolicyGroup and BoD (not from AddOns).  
			 */
			if(PCRFKeyValueConstants.FORCEFUL_SESSION_RE_AUTH.val.equalsIgnoreCase(response.getAttribute(PCRFKeyConstants.SESSION_RE_AUTH.val))){	
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Generating COA. Reason: Forceful session re-authorization for Subscriber Identity: " + response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val));
				}	
			}else if(response.isPolicyChanged() || response.isQuotaReservationChanged()){
				
				if(getLogger().isLogLevel(LogLevel.DEBUG)){
					getLogger().debug(MODULE, "Generating CoA. Reason: Change in policy or reservation for Subscriber Identity: " + response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val));
				}
				
			}else{
				
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Policy not changed for Subscriber Identity: " + response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val));
				}
				
				/* AddOn changes will be determined by checking Installable or Removable PCC Rules.
				 * Here, it is assumed that at least one PCC Rule(s) must be present in AddOn Session Auth Rule.
				 */
				if((response.getInstallablePCCRules() == null || response.getInstallablePCCRules().isEmpty()) 
						&& (response.getRemovablePCCRules() == null || response.getRemovablePCCRules().isEmpty())){
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "COA packet generation skipped. Reason: Installable and Removable PCC Rules not found for Subscriber Identity: " + response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val));
					}
					return;
				}else{
					if(getLogger().isLogLevel(LogLevel.DEBUG)){
						getLogger().debug(MODULE, "Generating CoA. Reason: Installable or Removable PCC Rules found for Subscriber Identity: " + response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val));
					}
				}
			}

			RadiusGatewayConfiguration configuration = gateway.getConfiguration();
			PCCToRadiusMapping coaMappings = configuration.getCOAMappings();

			if(coaMappings == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Sending COA skipped. Reason: No mapping found for PCRF response in " +
							"gateway = " + gateway.getIPAddress());
				}
				return;
			}

			RadiusPacket radiusPacket = new RadiusPacket();

			if (coaMappings.apply(new PCCtoRadiusMappingValueProvider(response, radiusPacket, configuration, context),
					AvpAccumalators.of(radiusPacket)) == false) {

				if(LogManager.getLogger().isInfoLogLevel()){
					LogManager.getLogger().info(MODULE, "Sending COA skipped. Reason: No mapping found for PCRF response in " +
							"gateway = " + gateway.getIPAddress());
				}
				return;

			}

			if(scripts != null && scripts.isEmpty() == false){
				for(RadiusGroovyScript script : scripts){
					try{
						script.preSend(response, radiusPacket);
					}catch(Exception ex){
						LogManager.getLogger().error(MODULE, "Error in executing script \""+script.getName()+"\" for gateway = " + gateway.getIPAddress() +". Reason: "+ ex.getMessage());
						LogManager.getLogger().trace(MODULE, ex);
					}
					
				}
			}
			
			if(response.isMarkedForDropRequest()){
				if(getLogger().isLogLevel(LogLevel.INFO))
					getLogger().info(MODULE, "Dyna Auth Response sending skipped. Reason: " +
							"PCRF Response dropped");
				return;
			}
			
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, radiusPacket.toString());

			gateway.sendRequest(radiusPacket);

		}else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().warn(MODULE, "COA packet generation skipped. Reason: Policy Enforcement Method for " +
						"Radius Gateway: " + gateway.getConfiguration().getConnectionURL() +" is " 
						+ enforcementMethod.getVal()+" So, Skipping further processing");
			}
		}
	}
		
	private ILogger getLogger() {
		return LogManager.getLogger();
	}
}

