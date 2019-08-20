package com.elitecore.elitesm.datamanager.core.system.license.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class SMLicenseData extends BaseData {

	private String id;
	private byte[] license;
	private String instanceName;
	private String digest;
	private String status;
	
	public SMLicenseData() {
		
	}
	
	public SMLicenseData(String id, byte[] license, String instanceName, String digest, String status) {
		this.id = id;
		this.license = license;
		this.instanceName = instanceName;
		this.digest = digest;
		this.status = status;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public byte[] getLicense() {
		return license;
	}
	public void setLicense(byte[] license) {
		this.license = license;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public String getDigest() {
		return digest;
	}
	public void setDigest(String digest) {
		this.digest = digest;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
