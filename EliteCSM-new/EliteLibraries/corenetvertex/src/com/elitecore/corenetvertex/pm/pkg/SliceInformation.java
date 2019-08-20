package com.elitecore.corenetvertex.pm.pkg;

import java.io.Serializable;

public class SliceInformation implements Serializable{
	
	private long sliceTotal;
	private long sliceUpload;
	private long sliceDownload;
	private long sliceTime;
	
	public SliceInformation(long sliceTotal, long sliceUpload, long sliceDownload, long sliceTime) {
		
		this.sliceTotal = sliceTotal;
		this.sliceUpload = sliceUpload;
		this.sliceDownload = sliceDownload;
		this.sliceTime = sliceTime;
	}
	
	public long getSliceTotal() {
		return sliceTotal;
	}

	public long getSliceUpload() {
		return sliceUpload;
	}

	public long getSliceDownload() {
		return sliceDownload;
	}

	public long getSliceTime() {
		return sliceTime;
	}
}
