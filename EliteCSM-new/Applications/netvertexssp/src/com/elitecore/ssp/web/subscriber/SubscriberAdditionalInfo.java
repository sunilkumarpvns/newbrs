package com.elitecore.ssp.web.subscriber;

public class SubscriberAdditionalInfo {

	/**
	 * The Identity of the Subscriber
	 */
	private String subscriberIdentity;
	
	/**
	 * The maximum quota that is allowed to transfer
	 */
	private long maxMBTransferredAllowed;
	
	/**
	 * The no. of times the User has requested quota advance
	 */
	private int advanceQuotaRequestCount;
	
	/**
	 * Max months from which advance quota can be requested
	 */
	private int maxMonthAdvanceQuotaAllowed;
	
	/**
	 * The maximum number of times Quota Transfer is allowed
	 */
	private int maxTimesQuotaTransferAllowed;
	
	/**
	 * max quota of the month according to the plan 
	 */
	private long maxMonthlyQuota;

	public SubscriberAdditionalInfo(String subscriberIdentity,
			long maxMBTransferredAllowed, int advanceQuotaRequestCount,
			int maxMonthAdvanceQuotaAllowed, int maxTimesQuotaTransferAllowed,long maxMonthlyQuota) {
		super();
		this.subscriberIdentity = subscriberIdentity;
		this.maxMBTransferredAllowed = maxMBTransferredAllowed;
		this.advanceQuotaRequestCount = advanceQuotaRequestCount;
		this.maxMonthAdvanceQuotaAllowed = maxMonthAdvanceQuotaAllowed;
		this.maxTimesQuotaTransferAllowed = maxTimesQuotaTransferAllowed;
		this.maxMonthlyQuota = maxMonthlyQuota;
	}

	public String getSubscriberIdentity() {
		return subscriberIdentity;
	}

	public long getMaxMBTransferredAllowed() {
		return maxMBTransferredAllowed;
	}

	public int getAdvanceQuotaRequestCount() {
		return advanceQuotaRequestCount;
	}

	public int getMaxMonthAdvanceQuotaAllowed() {
		return maxMonthAdvanceQuotaAllowed;
	}

	public long getMaxMonthlyQuota() {
		return maxMonthlyQuota;
	}

	public int getMaxTimesQuotaTransferAllowed() {
		return maxTimesQuotaTransferAllowed;
	}
}
