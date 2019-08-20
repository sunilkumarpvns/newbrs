package com.elitecore.aaa.diameter.service.application.drivers;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.drivers.HSSAuthDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

public class DiameterHSSAuthDriver extends HSSAuthDriver {

	private static final String MODULE = "DIA-HSS-AUTH-DRV";

	public DiameterHSSAuthDriver(AAAServerContext serverContext, String driverInstanceId) {
		super(serverContext, serverContext.getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(driverInstanceId));
	}
	

	@Override
	public String getTypeName() {
		return DriverTypes.DIA_HSS_AUTH_DRIVER.name();
	}

	@Override
	public int getType() {
		return DriverTypes.DIA_HSS_AUTH_DRIVER.value;
	}

	@Override
	protected String getValueForIdentityAttribute(ServiceRequest serviceRequest, String userIdentity) {
		IDiameterAVP identityAttribute =  ((ApplicationRequest)serviceRequest).getDiameterRequest().getAVP(userIdentity, true);
		if(identityAttribute != null)
			return identityAttribute.getStringValue();
		return null;
	}
	
	@Override
	protected String getModule() {
		return MODULE;
	}


	@Override
	protected ValueProvider getServiceRequestValueProvider(final ServiceRequest serviceRequest) {
		return new ValueProvider() {
			
			@Override
			public String getStringValue(String identifier) {
				
				if(identifier == null){
					return null;
	}
				identifier = identifier.trim();
				if(identifier.startsWith("\"") && identifier.endsWith("\""))
					return identifier.substring(1, identifier.length() -1);
				
				IDiameterAVP avp = ((ApplicationRequest)serviceRequest).getAVP(identifier, true);
				if(avp == null) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "AVP: " + identifier + " not found in Request.");
					}
					return null;
				}
				return avp.getStringValue();
			}
		};
	}


	@Override
	protected int getEAPMethodType(ServiceRequest request) {
		IDiameterAVP eapMethodTypeAttr = ((ApplicationRequest)request).getInfoAvp(DiameterAVPConstants.ELITE_EAP_METHOD);
		if (eapMethodTypeAttr == null) {
			return 0;
		}
		return (int) eapMethodTypeAttr.getInteger();
	}

}
