package com.elitecore.nvsmx.ws.resetusage.request;

import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Nonnull;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class ResetUsageRestRequest {

	private String subscriberId;
	private String alternateId;
	private String productOfferName;
	@Nonnull
	@Min(1)
	@Max(28)
    @ApiModelProperty
	private Long resetBillingCycleDate;
	private String resetReason;

	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getAlternateId() {
		return alternateId;
	}

	public void setAlternateId(String alternateId) {
		this.alternateId = alternateId;
	}

	public String getProductOfferName() {
		return productOfferName;
	}

	public void setProductOfferName(String productOfferName) {
		this.productOfferName = productOfferName;
	}

	public Long getResetBillingCycleDate() {
		return resetBillingCycleDate;
	}

	public void setResetBillingCycleDate(Long resetBillingCycleDate) {
		this.resetBillingCycleDate = resetBillingCycleDate;
	}

	public String getResetReason() {
		return resetReason;
	}

	public void setResetReason(String resetReason) {
		this.resetReason = resetReason;
	}
}
