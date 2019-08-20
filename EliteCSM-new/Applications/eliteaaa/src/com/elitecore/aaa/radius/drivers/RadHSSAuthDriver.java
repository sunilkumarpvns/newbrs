package com.elitecore.aaa.radius.drivers;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.drivers.HSSAuthDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RadHSSAuthDriver extends HSSAuthDriver {

	private static final String MODULE = "RAD-HSS-AUTH-DRV";

	public RadHSSAuthDriver(AAAServerContext serverContext, String driverInstanceId) {
		super(serverContext, serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(driverInstanceId));
	}

	@Override
	public String getTypeName() {
		return DriverTypes.RAD_HSS_AUTH_DRIVER.name();
	}

	@Override
	public int getType() {
		return DriverTypes.RAD_HSS_AUTH_DRIVER.value;
	}

	@Override
	protected String getModule() {
		return MODULE;
	}

	@Override
	protected String getValueForIdentityAttribute(
			ServiceRequest serviceRequest, String userIdentity) {
		IRadiusAttribute identityAttribute =  ((RadAuthRequest)serviceRequest).getRadiusAttribute(userIdentity, true);
		if(identityAttribute != null)
			return identityAttribute.getStringValue();
		return null;
	}

	@Override
	protected ValueProvider getServiceRequestValueProvider(final ServiceRequest serviceRequest) {
		return new ValueProvider() {
			
			@Override
			public String getStringValue(String identifier) {
				if(identifier == null)
					return null;
				
				identifier = identifier.trim();
				if(identifier.startsWith("\"") && identifier.endsWith("\""))
					return identifier.substring(1, identifier.length() -1);
				
				IRadiusAttribute identityAttribute =  ((RadAuthRequest)serviceRequest).getRadiusAttribute(identifier, true);
				if(identityAttribute == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Attribute: " + identifier + " not found in Request.");
	}
					return null;
				}
				return identityAttribute.getStringValue(false);
				
			}
		};
	}

	@Override
	protected int getEAPMethodType(ServiceRequest request) {
		IRadiusAttribute eapMethodTypeAttr = ((RadAuthRequest)request).getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,
				RadiusAttributeConstants.ELITE_EAP_METHOD);
		if (eapMethodTypeAttr == null){
			return 0;
	}
		return eapMethodTypeAttr.getIntValue();
	}

}
