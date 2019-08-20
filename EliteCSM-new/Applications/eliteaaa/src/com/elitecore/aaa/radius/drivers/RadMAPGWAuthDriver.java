package com.elitecore.aaa.radius.drivers;

import com.elitecore.aaa.core.conf.MAPGWAuthDriverConfiguration;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.drivers.MAPGWAuthDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RadMAPGWAuthDriver extends MAPGWAuthDriver{
	
	private final AAAServerContext serverContext;
	
	public RadMAPGWAuthDriver(AAAServerContext serverContext, String driverInstanceId) {
		super(serverContext,(MAPGWAuthDriverConfiguration)serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(driverInstanceId));
		this.serverContext = serverContext;
	}

	@Override
	public String getUserIdentityValue(ServiceRequest request) {
		return getValueForIdentityAttribute(request, String.valueOf(RadiusAttributeConstants.USER_NAME));
	}
	
	@Override
	protected String getValueForIdentityAttribute(ServiceRequest serviceRequest, String userIdentity) {
		IRadiusAttribute identityAttribute =  ((RadServiceRequest)serviceRequest).getRadiusAttribute(userIdentity,true);
		if(identityAttribute != null)
			return identityAttribute.getStringValue();
		else
			return null;
	}
	
	@Override
	public int getType() {
		return DriverTypes.RAD_MAPGW_AUTH_DRIVER.value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.RAD_MAPGW_AUTH_DRIVER.name();
	}

	@Override
	protected int getEAPMethodType(ServiceRequest request) {
		IRadiusAttribute eapMethodTypeAttr = ((RadServiceRequest)request).getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_EAP_METHOD);
		if (eapMethodTypeAttr == null){
			return 0;
		}
		return eapMethodTypeAttr.getIntValue();
	}
	
	@Override
	public void reInit() {
		this.mapGWAuthDriverConfiguration = (MAPGWAuthDriverConfiguration)serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(getDriverInstanceId());
	}
}
