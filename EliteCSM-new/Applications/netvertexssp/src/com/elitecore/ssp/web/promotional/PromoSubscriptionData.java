package com.elitecore.ssp.web.promotional;

public class PromoSubscriptionData {
	
    private java.lang.String addOnName;

    private long addOnPackageId;

    private long offerEndDate;

    private long offerStartDate;

    private java.lang.String status;

    private long validityPeriod;

    private java.lang.String validityPeriodUnit;
    
    private String startDate;
    
    private String endDate;

    public PromoSubscriptionData() {
    	
    }

	public PromoSubscriptionData(String addOnName, long addOnPackageId,
			long offerEndDate, long offerStartDate, String status,
			long validityPeriod, String validityPeriodUnit, String startDate,
			String endDate) {
		super();
		this.addOnName = addOnName;
		this.addOnPackageId = addOnPackageId;
		this.offerEndDate = offerEndDate;
		this.offerStartDate = offerStartDate;
		this.status = status;
		this.validityPeriod = validityPeriod;
		this.validityPeriodUnit = validityPeriodUnit;
		this.startDate = startDate;
		this.endDate = endDate;
	}






	public java.lang.String getAddOnName() {
		return addOnName;
	}

	public void setAddOnName(java.lang.String addOnName) {
		this.addOnName = addOnName;
	}

	public long getAddOnPackageId() {
		return addOnPackageId;
	}

	public void setAddOnPackageId(long addOnPackageId) {
		this.addOnPackageId = addOnPackageId;
	}

	public long getOfferEndDate() {
		return offerEndDate;
	}

	public void setOfferEndDate(long offerEndDate) {
		this.offerEndDate = offerEndDate;
	}

	public long getOfferStartDate() {
		return offerStartDate;
	}

	public void setOfferStartDate(long offerStartDate) {
		this.offerStartDate = offerStartDate;
	}

	public java.lang.String getStatus() {
		return status;
	}

	public void setStatus(java.lang.String status) {
		this.status = status;
	}

	public long getValidityPeriod() {
		return validityPeriod;
	}

	public void setValidityPeriod(long validityPeriod) {
		this.validityPeriod = validityPeriod;
	}

	public java.lang.String getValidityPeriodUnit() {
		return validityPeriodUnit;
	}

	public void setValidityPeriodUnit(java.lang.String validityPeriodUnit) {
		this.validityPeriodUnit = validityPeriodUnit;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}