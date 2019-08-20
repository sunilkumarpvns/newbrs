package com.elitecore.nvsmx.ws.subscription.data;

public class BillingCycleUsage {

	private Long totalOctets;
	private Long uploadOctets;
	private Long downloadOctets;
	private Long timeInSeconds;

	public BillingCycleUsage(){}
	public BillingCycleUsage(Long totalOctets, Long uploadOctets, Long downloadOctets, Long timeInSeconds) {
		super();
		this.totalOctets = totalOctets;
		this.uploadOctets = uploadOctets;
		this.downloadOctets = downloadOctets;
		this.timeInSeconds = timeInSeconds;
	}
	public Long getTotalOctets() {
		return totalOctets;
	}
	public Long getUploadOctets() {
		return uploadOctets;
	}
	public Long getDownloadOctets() {
		return downloadOctets;
	}
	public Long getTimeInSeconds() {
		return timeInSeconds;
	}
	public void setTotalOctets(Long totalOctets) {
		this.totalOctets = totalOctets;
	}
	public void setUploadOctets(Long uploadOctets) {
		this.uploadOctets = uploadOctets;
	}
	public void setDownloadOctets(Long downloadOctets) {
		this.downloadOctets = downloadOctets;
	}
	public void setTimeInSeconds(Long timeInSeconds) {
		this.timeInSeconds = timeInSeconds;
	}
}
