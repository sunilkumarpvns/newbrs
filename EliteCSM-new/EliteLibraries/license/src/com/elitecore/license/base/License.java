package com.elitecore.license.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.license.formatter.Formatter;

public class License {
	
	public static final String UNLIMITED_ACCESS = "-1";
	
	private String version;
	private Map<String,LicenseData> licenseParameters;
	
	private License(License license) {
		this.version = license.version;
		
		this.licenseParameters = new HashMap<String, LicenseData>();
		for (Entry<String, LicenseData> entry: license.licenseParameters.entrySet()) {
			this.licenseParameters.put(entry.getKey(), new LicenseData(entry.getValue()));
		}
	}
	
	public License (String version, Map<String, LicenseData> licenseParameters) {
		this.version = version;
		this.licenseParameters = licenseParameters;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public List<LicenseData> getLicenseParameterList() {
		List<LicenseData> licenseParametersList = new ArrayList<LicenseData>();
		if (this.licenseParameters.isEmpty() == false) {
			Iterator<Entry<String, LicenseData>> iterator = licenseParameters.entrySet().iterator();
			while (iterator.hasNext()) {
				
				licenseParametersList.add(iterator.next().getValue());
			}
		} 	
		return licenseParametersList;
	}
	
	public boolean validateLicense(String key, String value) {

		LicenseData licData= licenseParameters.get(key);

		if(licData==null)
			return false;
		else if (licData.getValue().trim().equals(UNLIMITED_ACCESS) || licData.getValue().trim().contains(UNLIMITED_ACCESS))
			return true;

		LicenseData tempLicData = new LicenseData();
		tempLicData.setName(key);
		tempLicData.setValue(value);
		return licData.equals(tempLicData);

	}

	public String getLicenseExpiryDate(){
		LicenseData licData= licenseParameters.get("SYSTEM_EXPIRY");
		String strLicenseExpiryDate=null;
		if(licData != null) {
			strLicenseExpiryDate = licData.getValue();
		}
		return strLicenseExpiryDate;
	}
	
	public String getLicenseValue(String key){
		LicenseData licData = licenseParameters.get(key);
		if(licData!=null)

			return licData.getValue();
		return null;    
	}
	
	public LicenseGenerator upgrade() {
		return new LicenseGenerator(this);
	}
	
	public String format(Formatter formatter) {
		formatter.startLicense();
		for (LicenseData data : licenseParameters.values()) {
			formatter.startModule()
				.append(data.getName())
				.append(data.getModule())
				.append(data.getType());
			
			if ("SYSTEM_NODE".equals(data.getName())) {
				formatter.appendPublicKey(data.getValue());
			} else {
				formatter.append(data.getValue());
			}
			formatter.append(data.getVersion())
				.append(data.getStatus())
				.append(data.getAdditionalKey())
				.append(data.getDisplayName())
				.append(data.getValueType())
				.append(data.getOperator())
				.endModule();
		}
		formatter.endLicense();
		
		return formatter.format();
	}
	
	public class LicenseGenerator {
		
		private License newLicense;

		/*
		 * construction is private because to generate a new license 
		 * from existing the one should have access to original license object
		 * 
		 */
		private LicenseGenerator(License license) {
			this.newLicense = new License(license);
		}
		
		public LicenseGenerator modifyValue(String key, String value) {
			LicenseData licenseData = this.newLicense.licenseParameters.get(key);
			licenseData.setValue(value);
			return this;
		}
		
		public LicenseGenerator remove(String key) {
			this.newLicense.licenseParameters.remove(key);
			return this;
		}
		
		public License generate() {
			return newLicense;
		}
		
	}
}
