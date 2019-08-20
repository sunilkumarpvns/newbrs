package com.elitecore.core.commons.utilx.mbean;

import com.elitecore.core.server.data.EliteSystemDetail;

public class SystemDetailController extends BaseMBeanController implements SystemDetailControllerMBean{
	
	@Override
	public String getName() {
		return MBeanConstants.SYSTEM;
	}

	@Override
	public String getDescription() {
		return EliteSystemDetail.getDescription();
	}

	@Override
	public String getContact() {
		return EliteSystemDetail.getContact();
	}

	@Override
	public String getLocation() {
		return EliteSystemDetail.getLocation();
	}

	@Override
	public int getSupportedServices() {
		return EliteSystemDetail.getSupportedServices();
	}


}
