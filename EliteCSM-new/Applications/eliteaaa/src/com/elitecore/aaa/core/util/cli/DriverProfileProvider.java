package com.elitecore.aaa.core.util.cli;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;

public class DriverProfileProvider extends ProfileProvider{

	public static final String HELP = "-help";
	
	private static DriverProfileProvider driverProfileDetailProvider;

	private DriverProfileProvider () {
		super(MBeanConstants.DRIVER_PROFILE);
	}

	public static DriverProfileProvider getInstance() {
		if (driverProfileDetailProvider == null) {
			synchronized (DriverProfileProvider.class) {
				if (driverProfileDetailProvider == null) {
					driverProfileDetailProvider = new DriverProfileProvider();
				}
			}
		}
		return driverProfileDetailProvider;
	}

	@Override
	public String getKey() {
		return "driver";
	}
}

