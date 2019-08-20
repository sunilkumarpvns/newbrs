package com.elitecore.nvsmx.ws.subscription.data;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class Usage {

	private Long uploadOctets;
	private Long downloadOctets;
	private Long totalOctets;
	private Long time;
	private Long event;

	public Usage(){}
	public Usage(Long uploadOctets, Long downloadOctets, Long totalOctets, Long time, Long event) {
		super();
		this.uploadOctets = uploadOctets;
		this.downloadOctets = downloadOctets;
		this.totalOctets = totalOctets;
		this.time = time;
		this.event = event;
	}
	
	public Long getUploadOctets() {
		return uploadOctets;
	}

	public void setUploadOctets(Long uploadOctets) {
		this.uploadOctets = uploadOctets;
	}

	public Long getDownloadOctets() {
		return downloadOctets;
	}

	public void setDownloadOctets(Long downloadOctets) {
		this.downloadOctets = downloadOctets;
	}

	public Long getTotalOctets() {
		return totalOctets;
	}

	public void setTotalOctets(Long totalOctets) {
		this.totalOctets = totalOctets;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Long getEvent() {
		return event;
	}

	public void setEvent(Long event) {
		this.event = event;
	}
}