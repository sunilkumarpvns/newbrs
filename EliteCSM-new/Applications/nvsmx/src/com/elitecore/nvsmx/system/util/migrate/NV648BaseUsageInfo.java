package com.elitecore.nvsmx.system.util.migrate;

import com.google.gson.annotations.SerializedName;

public class NV648BaseUsageInfo {

	@SerializedName("TOTALOCTETS")
	private long totalOctets;
	@SerializedName("UPLOADOCTETS")
	private long uploadOctets;
	@SerializedName("DOWNLOADOCTETS")
	private long downloadOctets;
	@SerializedName("USAGETIME")
	private long usageTime;
	@SerializedName("NAME")
	private String basePackageName;
	
	public NV648BaseUsageInfo() {
	}

	public long getTotalOctets() {
		return totalOctets;
	}

	public void setTotalOctets(long totalOctets) {
		this.totalOctets = totalOctets;
	}

	public long getUploadOctets() {
		return uploadOctets;
	}

	public void setUploadOctets(long uploadOctets) {
		this.uploadOctets = uploadOctets;
	}

	public long getDownloadOctets() {
		return downloadOctets;
	}

	public void setDownloadOctets(long downloadOctets) {
		this.downloadOctets = downloadOctets;
	}

	public long getUsageTime() {
		return usageTime;
	}

	public void setUsageTime(long usageTime) {
		this.usageTime = usageTime;
	}

	public String getBasePackageName() {
		return basePackageName;
	}

	public void setBasePackageName(String basePackageName) {
		this.basePackageName = basePackageName;
	}
}
