package com.elitecore.corenetvertex.spr.balance;


import java.io.Serializable;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class Usage implements Cloneable,Serializable{

	private long uploadOctets;
	private long downloadOctets;
	private long totalOctets;
	private long time;
	
	/* for zero usage creation */
	public Usage() { }
	
	public Usage(long uploadOctets, long downloadOctets, long totalOctets, long time) {

		this.uploadOctets = uploadOctets;
		this.downloadOctets = downloadOctets;
		this.totalOctets = totalOctets;
		this.time = time;
	}

	public long getUploadOctets() {
		return uploadOctets;
	}

	public long getDownloadOctets() {
		return downloadOctets;
	}

	public long getTotalOctets() {
		return totalOctets;
	}

	public long getTime() {
		return time;
	}

	public void setUploadOctets(long uploadOctets) {
		this.uploadOctets = uploadOctets;
	}

	public void setDownloadOctets(long downloadOctets) {
		this.downloadOctets = downloadOctets;
	}

	public void setTotalOctets(long totalOctets) {
		this.totalOctets = totalOctets;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}