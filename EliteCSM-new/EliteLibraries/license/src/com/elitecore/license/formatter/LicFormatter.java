package com.elitecore.license.formatter;

import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.crypt.DefaultEncryptor;
import com.elitecore.license.crypt.LicenseEncryptor;

/**
 * Formats the license file having .lic format. LicFormatter
 * can take {@link LicenseEncryptor} as an input and based 
 * on the type of encryptor can format the license in plain
 * or encrypted format.
 * 
 * @author malav.desai
 * @author vicky.singh
 *
 */
public class LicFormatter implements Formatter {
	
	private StringBuilder license;
	private StringBuilder module;
	private LicenseEncryptor encryptor;
	
	public LicFormatter() {
		this.license = new StringBuilder();
		this.module = new StringBuilder();
		this.encryptor = new DefaultEncryptor();
	}
	
	public LicFormatter(LicenseEncryptor encryptor) {
		this.license = new StringBuilder();
		this.encryptor = encryptor;
	}

	@Override
	public Formatter startLicense() {
		return this;
	}

	@Override
	public Formatter startModule() {
		this.module = new StringBuilder();
		return this;
	}

	@Override
	public Formatter append(String value) {
		module.append(value).append(LicenseConstants.LICENSE_KEY_SEPRATOR);
		return this;
	}
	
	@Override
	public Formatter appendPublicKey(String value) {
		module.append(encryptor.encryptPublicKey(value)).append(LicenseConstants.LICENSE_KEY_SEPRATOR);
		return this;
	}

	@Override
	public Formatter endModule() {
		license.append(encryptor.encryptLicenseKey(module.toString()));
		license.append(LicenseConstants.LICENSE_SEPRATOR);
		return this;
	}

	@Override
	public Formatter endLicense() {
		return this;
	}

	@Override
	public String format() {
		return license.toString();
	}

}
