package com.elitecore.aaa.core.threegpp;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.elitecore.aaa.core.conf.SPIKeyConfiguration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.CommonConstants;
import com.elitecore.coreradius.commons.util.constants.Constants3GPP2;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public abstract class HA3GPP2Request implements ThreeGPP2Request{
	
	private ServiceContext serviceContext;
	private String MODULE = "3GPP2_HA_REQUEST";
	
	public HA3GPP2Request(ServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}
	
	public void handleRequest(ServiceRequest request,ServiceResponse response){			
		// processing request from HA		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Handling 3GPP2Request from HA.");
		}
		long lVendorId = RadiusConstants.VENDOR_3GPP2_ID;		
		Map<String,Map<String,String>> spiGroupMap = getSPIGroupMap(); 
		String sharedSecret = ((RadAuthResponse)response).getClientData().getSharedSecret(((RadAuthRequest)request).getPacketType());

//		
//		Collection vendorSpecificAttributes = radiusRequestPacket.getVendorSpeficAttributes();
//		IRadiusAttribute radiusAttribute = null;
//		if(vendorSpecificAttributes != null){
//			Iterator itr = vendorSpecificAttributes.iterator();
//			while(itr.hasNext()){
//				VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute)itr.next();
//				if(vsaAttribute.getVendorID() == RadiusConstants.VENDOR_3GPP2_ID){
//					IVendorSpecificAttribute vsaTypeAttribute = vsaAttribute.getVendorTypeAttribute();
//					if(vsaTypeAttribute.getAttribute(Constants3GPP2.MN_HA_SPI) != null){
//						radiusAttribute = vsaTypeAttribute.getAttribute(Constants3GPP2.MN_HA_SPI);
//						break;
//					}
//				}
//				
//			}
//		}
		
		IRadiusAttribute radiusAttribute = ((RadAuthRequest)request).getRadiusAttribute(lVendorId,Constants3GPP2.MN_HA_SPI);
		
		if(radiusAttribute != null){
			
			String strNASIPAddr = ((RadAuthRequest)request).getClientIp();
			IRadiusAttribute nasIPAddrAttr = ((RadAuthRequest)request).getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
			
			if(nasIPAddrAttr != null){
				strNASIPAddr = nasIPAddrAttr.getStringValue();
			}
			
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Searching SPI table for IP-Address : " + strNASIPAddr);
			
			if(spiGroupMap != null){
				Map<String,String> spiKeyMap = spiGroupMap.get(strNASIPAddr);
				
				if(spiKeyMap == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "SPI entry not found for IP-Address : " + strNASIPAddr + ",considering 0.0.0.0 as IP-Address");					
					spiKeyMap = spiGroupMap.get("0.0.0.0");
				}
				if(spiKeyMap != null){									
					int spiValue = radiusAttribute.getIntValue();
					String keyValue = null;
					if((keyValue = spiKeyMap.get(String.valueOf(spiValue))) != null){
						byte[] keyValueBytes = null;
						try{
							keyValueBytes =keyValue.getBytes(CommonConstants.UTF8);
						}catch(UnsupportedEncodingException e){
							keyValueBytes =keyValue.getBytes();
						}
						IRadiusAttribute spiAttribute = Dictionary.getInstance().getAttribute(lVendorId,Constants3GPP2.MN_HA_SPI);
						spiAttribute.setIntValue(spiValue);
						((RadAuthResponse)response).addAttribute(spiAttribute);						
						
						//TODO - Require to enhance the support of key2868 attribute to support tag field in attribute.- Devang.
						IRadiusAttribute mn_ha_key = Dictionary.getInstance().getAttribute(lVendorId, Constants3GPP2.MN_HA_KEY);
						mn_ha_key.setValue(keyValueBytes, 32768, sharedSecret, ((RadAuthRequest)request).getAuthenticator());
						((RadAuthResponse)response).addAttribute(mn_ha_key);						
						
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(MODULE, "Radius packet after adding 3GPP2-MN-HA-SPI: " + response);
						
						
//						byte[] decKey = RadiusUtility.decryptKeyRFC2868(encKey, strSharedSecret, radiusRequestPacket.getAuthenticator(), saltBytes);
//						Logger.logTrace(MODULE, "decrypted key: " + new String(decKey));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, "3GPP2-MN-HA-SPI not found into a SPI-Key configuration,Radius packet without 3GPP2-MN-HA-Shared-Key: " + response);
					}
				}
			}
		}
		
	}
	
	private Map<String,Map<String,String>> getSPIGroupMap(){
		SPIKeyConfiguration spiKeyConfiguration = ((AAAServerContext)this.serviceContext.getServerContext()).getServerConfiguration().getSpiKeysConfiguration();
		return spiKeyConfiguration != null ? spiKeyConfiguration.getSPIgroupMap() : null;
	}
	
	protected ServiceContext getServiceContext(){
		return serviceContext;
	}
}
