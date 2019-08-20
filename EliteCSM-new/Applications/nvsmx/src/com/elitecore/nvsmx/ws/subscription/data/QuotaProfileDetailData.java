package com.elitecore.nvsmx.ws.subscription.data;


public class QuotaProfileDetailData {

	
	private String quotaProfileId;
	private String serviceId;
	private int fupLevel;
	private String serviceName;
	private Long total;
	private String totalUnit;
	private Long download;
	private String downloadUnit;
	private Long upload;
	private String uploadUnit;
	private Long time;
	private String timeUnit;
	private String aggregationKey;
    
	public QuotaProfileDetailData(){}
	
	public QuotaProfileDetailData(String quotaProfileId, String name,
			String serviceId, int fupLevel, String serviceName, Long total,
			String totalUnit, Long download, String downloadUnit, Long upload,
			String uploadUnit, Long time, String timeUnit, String aggregationKey) {
		super();
		this.quotaProfileId = quotaProfileId;
		this.serviceId = serviceId;
		this.fupLevel = fupLevel;
		this.serviceName = serviceName;
		this.total = total;
		this.totalUnit = totalUnit;
		this.download = download;
		this.downloadUnit = downloadUnit;
		this.upload = upload;
		this.uploadUnit = uploadUnit;
		this.time = time;
		this.timeUnit = timeUnit;
		this.aggregationKey = aggregationKey;
	}
	
	
	

	public QuotaProfileDetailData(String quotaProfileId, String name,
			String serviceId, int fupLevel, String serviceName) {
		super();
		this.quotaProfileId = quotaProfileId;
		this.serviceId = serviceId;
		this.fupLevel = fupLevel;
		this.serviceName = serviceName;
	}




	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public String getTotalUnit() {
		return totalUnit;
	}

	public void setTotalUnit(String totalUnit) {
		this.totalUnit = totalUnit;
	}

	public Long getDownload() {
		return download;
	}

	public void setDownload(Long download) {
		this.download = download;
	}

	public String getDownloadUnit() {
		return downloadUnit;
	}

	public void setDownloadUnit(String downloadUnit) {
		this.downloadUnit = downloadUnit;
	}

	public Long getUpload() {
		return upload;
	}

	public void setUpload(Long upload) {
		this.upload = upload;
	}

	public String getUploadUnit() {
		return uploadUnit;
	}

	public void setUploadUnit(String uploadUnit) {
		this.uploadUnit = uploadUnit;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}

	public String getAggregationKey() {
		return aggregationKey;
	}

	public void setAggregationKey(String aggregationKey) {
		this.aggregationKey = aggregationKey;
	}

	public void setQuotaProfileId(String quotaProfileId) {
		this.quotaProfileId = quotaProfileId;
	}

	
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public void setFupLevel(int fupLevel) {
		this.fupLevel = fupLevel;
	}





	public String getServiceName() {
		return serviceName;
	}


	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}


	public String getQuotaProfileId() {
		return quotaProfileId;
	}


	
	public String getServiceId() {
		return serviceId;
	}


	public int getFupLevel() {
		return fupLevel;
	}
	
}
