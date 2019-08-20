package com.elitecore.aaa.license;

import java.io.File;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.license.nfv.NFVLicenseManager;
import com.elitecore.license.base.commons.LicenseConstants;

public class LicenseManagerFactory {

	public static AAALicenseManager createLicenseManager(AAAServerContext serverContext, LicenseExpiryListener expiryListener) {
		String licFile = serverContext.getServerHome() + File.separator+ LicenseConstants.LICENSE_DIRECTORY + File.separator + LicenseConstants.LICENSE_FILE_NAME + LicenseConstants.LICESE_FILE_EXT;
		if (new File(licFile).exists()) {
			return new ClassicLicenseManager(serverContext, expiryListener);
		} else {
			return new NFVLicenseManager(serverContext, expiryListener);
		}
	}
}
