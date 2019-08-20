package com.elitecore.aaa.radius.threegpp.auth;

import java.util.List;

import com.elitecore.aaa.core.threegpp.Base3GPPHandler;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthVendorSpecificHandler;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.Constants3GPP2;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RadAuth3GPP2Handler extends Base3GPPHandler implements RadAuthVendorSpecificHandler {

	private String MODULE = "RAD_AUTH_3GPP2_HANDLER";
	private ServiceContext serviceContext;
	private RadHA3GPP2RequestHandler haRequest;
	public RadAuth3GPP2Handler(ServiceContext context) {
		super(context);
		this.serviceContext = context;
		haRequest = new RadHA3GPP2RequestHandler(this.serviceContext);
	}

	@Override
	public void init() throws InitializationFailedException {
		
	}

	@Override
	public boolean isEligible(RadAuthRequest request) {
		
		if(request.getRadiusAttribute(RadiusConstants.VENDOR_3GPP2_ID,
					Constants3GPP2.CORRELATION_ID) != null 
				|| ((RadAuthRequest)request).getRadiusAttribute(RadiusConstants.VENDOR_3GPP2_ID,
						Constants3GPP2.MN_HA_SPI) != null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
				LogManager.getLogger().trace(MODULE, "3GPP2 Correlation ID or MN-HA-SPI present in request.");
			}
			return true;
		}
		return false;
	
	}

	@Override
	public void handleRequest(RadAuthRequest request, RadAuthResponse response) {

		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Handling 3GPP2 Request");
		}
		
		if(response.getPacketType() == RadiusConstants.ACCESS_CHALLENGE_MESSAGE){
			LogManager.getLogger().trace(MODULE, "Response Packet is ACCESS_CHALLENGE, Skipping 3gpp2 handling");
			return;
		}
									
		String ha_ip_address = response.getClientData().getHAAddress(); 
	
		//Map<String,Map<String,String>> spiGroupMap = ((AAAServerContext)serviceContext.getServerContext()).getServerConfiguration().getSpiKeysConfiguration().getSPIgroupMap();
		
		if(haRequest.isEligible(request, response)){
			haRequest.handleRequest(request, response);			
		}		

		boolean add_HA_IPintoResponse = false;
		
		if(request.getRadiusAttribute(RadiusConstants.VENDOR_3GPP2_ID, Constants3GPP2.HA_IP_ADDRESS) == null
				&& response.getRadiusAttribute(Constants3GPP2.HA_IP_ADDRESS) == null){
			add_HA_IPintoResponse = true;
		}
		
		if(add_HA_IPintoResponse && ha_ip_address != null && !ha_ip_address.equals(CommonConstants.RESERVED_IPV_4_ADDRESS)){
			
			IRadiusAttribute radiusAttribute = Dictionary.getInstance().getAttribute(RadiusConstants.VENDOR_3GPP2_ID, Constants3GPP2.HA_IP_ADDRESS);
	
			radiusAttribute.setStringValue(ha_ip_address);
			response.addAttribute(radiusAttribute);			
		}
			
//		adding DNS IP Address attribute(s)
		if(response.getRadiusAttribute(Constants3GPP2.DNS_SERVER_IP_ADDRESS) == null){
			List<IRadiusAttribute> dnsAttributes =  null ;
			dnsAttributes = response.getClientData().get3GPP2DnsList();			
			if(dnsAttributes != null && !dnsAttributes.isEmpty()){
				int size = dnsAttributes.size();
				for(int i =0 ; i < size ; i++){
					try {
						response.addAttribute((IRadiusAttribute)dnsAttributes.get(i).clone());
					} catch (CloneNotSupportedException e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, "Failed while adding 3GPP2 DNS attribute, Reason : improper dictionary for " + dnsAttributes.get(i).getVendorID() + ":" + dnsAttributes.get(i).getType());
						break;
					}					
				}
			}
		}
	
		//adding the Message authenticator attribute
		IRadiusAttribute messageAuthenticator = response.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
		if(messageAuthenticator == null){
			messageAuthenticator = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
			if(messageAuthenticator == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Message Authenticator(0:80) attribute not found in dictionary.");
				}
			}
		}
		if(messageAuthenticator != null){
			messageAuthenticator.setValueBytes(new byte[16]);
			messageAuthenticator.setValueBytes(RadiusUtility.HMAC("MD5", response.getResponseBytes(), response.getClientData().getSharedSecret(request.getPacketType())));
			response.addAttribute(messageAuthenticator);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Message Authenticator(0:80) added in response.");
			}
		}
	}
}