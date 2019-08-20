package com.elitecore.netvertexsm.datamanager.gateway.profile.data;

public class VendorData implements IVendorData {
	private Long vendorId;
	private String vendorName;
	private String description;
	
	public Long getVendorId() {
		return vendorId;
	}
	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
