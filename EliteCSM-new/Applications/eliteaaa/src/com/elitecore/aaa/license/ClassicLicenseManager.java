package com.elitecore.aaa.license;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.io.Files;
import com.elitecore.license.base.MultiLicenseManager;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.exception.licensetype.InvalidLicenseException;

import java.io.File;

/**
 * License manager which manages task regarding license when the classic license
 * is loaded, i.e., local_node.lic.
 * 
 * @author vicky.singh
 * @author malav.desai
 *
 */
public class ClassicLicenseManager implements AAALicenseManager {
	
	private final AAAServerContext serverContext;
	private final String fileName;
	private final LicenseExpiryListener expiryListener;
	private MultiLicenseManager licenseManager;

	public ClassicLicenseManager(AAAServerContext serverContext, LicenseExpiryListener expiryListener) {
		this.serverContext = serverContext;
		this.fileName = serverContext.getServerHome() + File.separator + LicenseConstants.LICENSE_DIRECTORY + 
				File.separator + LicenseConstants.LICENSE_FILE_NAME + LicenseConstants.LICESE_FILE_EXT;
		this.expiryListener = expiryListener;
	}

	@Override
	public void init() throws InvalidLicenseException {
		licenseManager = new MultiLicenseManager();
		licenseManager.add(new File(this.fileName), serverContext.getServerMajorVersion());
	}

	@Override
	public void startLicenseValidationTask() {
		EliteExpiryDateValidationTask task = new EliteExpiryDateValidationTask(serverContext, expiryListener, licenseManager);
		serverContext.getTaskScheduler().scheduleIntervalBasedTask(task);
	}

	@Override
	public void removeLicenseFile() {
		//TODO Vicky confirm with devang that classic license shold be removed or not
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean validateLicense(String key, String value) {
		return licenseManager.validateLicense(key, value);
	}

	@Override
	public String getLicenseValue(String key) {
		return licenseManager.getLicenseValue(key);
	}

	@Override
	public String getLicenseKey() {
		try {
			return new String(Files.readFully(fileName));
		} catch (Exception e) {
			/*
			 * This will happen when the license file local_node.lic has been moved or deleted or modified
			 * in such a way that rendered  unreadable; Such a case should never occur as modifications to 
			 * such sensitive files should be prohibited.
			 */
		}
		return null;
	}
}
