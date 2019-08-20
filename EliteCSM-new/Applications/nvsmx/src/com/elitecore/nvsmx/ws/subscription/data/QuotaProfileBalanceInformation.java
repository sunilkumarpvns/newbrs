package com.elitecore.nvsmx.ws.subscription.data;

import java.util.List;

public class QuotaProfileBalanceInformation  {
	
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


	private Long uploadOctets;
	private Long downloadOctets;
	private Long totalOctets;
	private Long time;


	public QuotaProfileBalanceInformation(String quotaProfileId,
			String quotaProfileName, UsageInfo allServiceBalance,
			List<UsageInfo> balanceInfos, Long uploadOctets,
			Long downloadOctets, Long totalOctets, Long time) {
		
		this.uploadOctets = uploadOctets;
		this.downloadOctets = downloadOctets;
		this.totalOctets = totalOctets;
		this.time = time;
	}



	public QuotaProfileBalanceInformation() {
		// TODO Auto-generated constructor stub
	}




	

}
