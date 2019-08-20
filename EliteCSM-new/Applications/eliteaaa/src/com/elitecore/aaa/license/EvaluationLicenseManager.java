package com.elitecore.aaa.license;

import java.util.concurrent.TimeUnit;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.license.base.exception.licensetype.InvalidLicenseException;

public class EvaluationLicenseManager implements AAALicenseManager{

	private static final String MODULE = "EVALUATION_LICENSE_MANAGER";
	protected static final long LICENSE_EVALUATION_DAYS = 1;
	protected static final long EVALUATION_LICENSE_TPS = 100;
	private LicenseExpiryListener licenseExpiryListener;
	private AAAServerContext serverContext;
	private long licenseExpiryTime;

	public EvaluationLicenseManager(AAAServerContext serverContext, LicenseExpiryListener licenseExpiryListener) {
		this.serverContext= serverContext;
		this.licenseExpiryListener = licenseExpiryListener;
		this.licenseExpiryTime = System.currentTimeMillis()+TimeUnit.DAYS.toMillis(LICENSE_EVALUATION_DAYS);
	}

	@Override
	public void init() throws InvalidLicenseException {
		// no sonar		
	}

	@Override
	public void startLicenseValidationTask() {
		EliteExpiryDateValidationTask task = new EliteExpiryDateValidationTask(serverContext, licenseExpiryListener, null);
		serverContext.getTaskScheduler().scheduleIntervalBasedTask(task);
	}

	@Override
	public void removeLicenseFile() {
		// no sonar

	}

	@Override
	public boolean validateLicense(String key, String value) {
		if(LicenseNameConstants.SYSTEM_EXPIRY.equalsIgnoreCase(key)) {
			if(licenseExpiryTime <= System.currentTimeMillis()) {
				return false;
			}
		}
		if(LicenseNameConstants.SYSTEM_TPS.equalsIgnoreCase(key)) {
			try {
				if(EVALUATION_LICENSE_TPS < Long.parseLong(value)) {
					return false;
				}
			} catch (NumberFormatException e) {
				LogManager.getLogger().error(MODULE, "Error while parsing " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
		}
		return true;
	}

	@Override
	public String getLicenseValue(String key) {
		if(LicenseNameConstants.SYSTEM_EXPIRY.equalsIgnoreCase(key)) {
			return String.valueOf(LICENSE_EVALUATION_DAYS);
		}
		if(LicenseNameConstants.SYSTEM_TPS.equalsIgnoreCase(key)) {
			return String.valueOf(EVALUATION_LICENSE_TPS);
		}
		return null;
	}

	@Override
	public String getLicenseKey() {
		return null;
	}

}

