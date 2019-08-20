package com.elitecore.license.base;

import com.elitecore.license.base.License.LicenseGenerator;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.crypt.DefaultDecryptor;
import com.elitecore.license.crypt.LicenseDecryptor;
import com.elitecore.license.crypt.PlaintextDecryptor;
import com.elitecore.license.parser.LicFileParser;
import com.elitecore.license.util.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class SingleLicenseManager implements LicenseManager {

	private static final String MODULE = "SINGLE-LICENSE-MANAGER";

	private License license;
	private String licenseKey;

	private List<LicenseObserver> observers;

	public SingleLicenseManager() {
		this(new CopyOnWriteArrayList<>());
	}

	public SingleLicenseManager(List<LicenseObserver> observers) {
		this.observers = observers;
	}

	public void add(String licenseKey, String version) throws InvalidLicenseKeyException {
		LicFileParser parser = new LicFileParser(licenseKey, new DefaultDecryptor(), version);
		license = parser.parse();
	}

	public void add(String licenseKey, String version, LicenseDecryptor decryptor) throws InvalidLicenseKeyException {
		LicFileParser parser = new LicFileParser(licenseKey, decryptor, version);
		license = parser.parse();
	}

	public void add(File licenseFile, String version) throws InvalidLicenseKeyException {
		String licenseKeyFromFile = "";
		try {
			licenseKeyFromFile = SystemUtil.readStoredLicense(licenseFile);
		} catch (IOException e) {
			throw new InvalidLicenseKeyException("License loading failed. Reason: " + e.getMessage(), e);
		}

		if ((LicenseConstants.NFV_LICENSE_FILE_NAME + LicenseConstants.LICESE_FILE_EXT).
				equals(licenseFile.getName())) {
			LicFileParser parser = new LicFileParser(licenseKeyFromFile, new PlaintextDecryptor(), version);
			license = parser.parse();
		} else {
			add(licenseKeyFromFile, version);
		}
	}

	public boolean validateLicense(String key, String value) {
		return license!=null && license.validateLicense(key, value);
	}

	public String getLicenseExpiryDate(){
		if (license == null) {
			return null;
		}
		return license.getLicenseExpiryDate();
	}

	public long getRemainedDaysToExpire() throws Exception{

		String strLicenseExpiryDate = getLicenseExpiryDate();
		strLicenseExpiryDate = strLicenseExpiryDate + " 23:59:59";
		Date expiryDate;
		SimpleDateFormat dateFormat = new SimpleDateFormat(LicenseConstants.DATE_FORMAT + " hh:mm:ss");
		expiryDate = dateFormat.parse(strLicenseExpiryDate);
		Date currentDate = new Date();
		Calendar expiryDateCalendar = Calendar.getInstance();
		expiryDateCalendar.setTime(expiryDate);

		Calendar currentDateCalendar = Calendar.getInstance();
		currentDateCalendar.setTime(currentDate);

		long milliSecondToDay = 1000*60*60*24L;
		float remainedDays = (float)(expiryDateCalendar.getTimeInMillis() - currentDateCalendar.getTimeInMillis())/milliSecondToDay;
		return (long)remainedDays;
	}

	public String getLicenseValue(String key){
		if (license == null) {
			return null;
		}
		return license.getLicenseValue(key);
	}

	public LicenseGenerator upgrade() {
		if(license==null){
			return null;
		}

		return license.upgrade();
	}

	public void uploadLicense(String licenseText, String version) {

		try {
			LicFileParser parser = new LicFileParser(licenseText, new DefaultDecryptor(), version);
			License parsedLicense = parser.parse();
			license = parsedLicense;
			licenseKey = licenseText;

		} catch (InvalidLicenseKeyException e) {
			getLogger().error(MODULE, "Error while parsing license. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}

		notifyObservers();
	}

	private void notifyObservers(){
		for(LicenseObserver observer:observers){
			observer.setStatus();
		}
	}

	public void registerObserver(LicenseObserver observer){
		if(observer!=null){
			observers.add(observer);
		}
	}

	public void deregisterLicense(){
		license = null;
		licenseKey = null;
		notifyObservers();
	}

	public String getLicenseKey(){
		return licenseKey;
	}

}