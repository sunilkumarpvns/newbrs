package com.elitecore.core.util.mbean.data.dictionary;

import java.io.Serializable;

public class EliteDictionaryData implements Serializable  {

	
	private static final long serialVersionUID = 1L;
	private String vendorName;
	private long vendorId;
	private long applicationId;
	public long getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(long applicationId) {
		this.applicationId = applicationId;
	}
	private String version;
	private byte[] dictionaryData;
	private String type;
	
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public long getVendorId() {
		return vendorId;
	}
	public void setVendorId(long vendorId) {
		this.vendorId = vendorId;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public byte[] getDictionaryData() {
		return dictionaryData;
	}
	public void setDictionaryData(byte[] dictionaryData) {
		this.dictionaryData = dictionaryData;
	}
	
	
	
	

}
