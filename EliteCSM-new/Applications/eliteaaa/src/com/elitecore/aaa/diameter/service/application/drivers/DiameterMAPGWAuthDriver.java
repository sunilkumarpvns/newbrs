package com.elitecore.aaa.diameter.service.application.drivers;

import com.elitecore.aaa.core.conf.MAPGWAuthDriverConfiguration;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.drivers.MAPGWAuthDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

public class DiameterMAPGWAuthDriver extends MAPGWAuthDriver{

	public DiameterMAPGWAuthDriver(AAAServerContext serverContext,String drvierInstanceId) {
		super(serverContext,(MAPGWAuthDriverConfiguration)((AAAServerContext)serverContext).getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(drvierInstanceId));
	}

	@Override
	protected String getValueForIdentityAttribute(
		ServiceRequest serviceRequest, String userIdentity) {
		IDiameterAVP identityAttribute = ((ApplicationRequest) serviceRequest).getAVP(userIdentity, true);
		if(identityAttribute != null)
			return identityAttribute.getStringValue();
		else
			return null;
	}

	@Override
	public int getType() {
		return DriverTypes.DIAMETER_MAPGW_AUTH_DRIVER.value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.DIAMETER_MAPGW_AUTH_DRIVER.name();
	}

	@Override
	public String getUserIdentityValue(ServiceRequest request) {
		return getValueForIdentityAttribute(request, String.valueOf(DiameterAVPConstants.USER_NAME));
	}

	@Override
	protected int getEAPMethodType(ServiceRequest request) {
		IDiameterAVP eapMethodType = ((ApplicationRequest)request).getInfoAvp(DiameterAVPConstants.ELITE_EAP_METHOD);
		if (eapMethodType == null){
			return 0;
		}
		return (int) eapMethodType.getInteger();
	}
	
	@Override
	public void reInit() {
		this.mapGWAuthDriverConfiguration = (MAPGWAuthDriverConfiguration)((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(getDriverInstanceId());
	}
}
