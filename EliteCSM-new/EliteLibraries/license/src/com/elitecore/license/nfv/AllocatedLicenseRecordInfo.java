package com.elitecore.license.nfv;

public class AllocatedLicenseRecordInfo{
	private final String id;
	private final String name;
	private final String status;
	
	public AllocatedLicenseRecordInfo (String id, String name, String status) {
		this.id = id;
		this.name = name;
		this.status = status;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getStatus() {
		return status;
	}
}