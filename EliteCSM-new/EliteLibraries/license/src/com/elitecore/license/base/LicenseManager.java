package com.elitecore.license.base;

import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.crypt.LicenseDecryptor;

import java.io.File;

public interface LicenseManager {
    void add(String licenseKey, String version) throws InvalidLicenseKeyException;
    void add(String licenseKey, String version, LicenseDecryptor decryptor) throws InvalidLicenseKeyException;
    void add(File licenseFile, String version) throws InvalidLicenseKeyException;
    boolean validateLicense(String key, String value);
    String getLicenseExpiryDate();
    long getRemainedDaysToExpire() throws Exception;
    String getLicenseValue(String key);
    void uploadLicense(String licenseText, String version);
    void registerObserver(LicenseObserver observer);
    void deregisterLicense();
    String getLicenseKey();
    License.LicenseGenerator upgrade();
}
