package com.elitecore.nvsmx.system.util.migrate;





import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class NV648SubscriptionInfo {

	@SerializedName("SUBSCRIBERID")
	private String subscriberid;
	@SerializedName("STARTTIME")
	private Timestamp startTime;
	@SerializedName("ENDTIME")
	private Timestamp endTime;
	@SerializedName("TOTALOCTETS")
	private long totalOctets = -1;
	@SerializedName("UPLOADOCTETS")
	private long uploadOctets = -1;
	@SerializedName("DOWNLOADOCTETS")
	private long downloadOctets = -1;
	@SerializedName("USAGETIME")
	private long usageTime = -1;
	@SerializedName("NAME")
	private String packageName;
	@SerializedName("PARAM1")
	private String param1;
	@SerializedName("PARAM2")
	private String param2;
	@SerializedName("SUBSCRIPTION_ID")
	private String subscription;
	
	public NV648SubscriptionInfo() {
	
	}

	public String getSubscriberid() {
		return subscriberid;
	}

	public void setSubscriberid(String subscriberid) {
		this.subscriberid = subscriberid;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
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

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

	public boolean isUsageExist() {
		return totalOctets >= 0 || uploadOctets >= 0 || downloadOctets >= 0 || usageTime >= 0;
	}
}
