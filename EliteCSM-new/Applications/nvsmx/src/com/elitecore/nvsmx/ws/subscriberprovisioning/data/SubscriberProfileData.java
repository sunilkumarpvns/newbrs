package com.elitecore.nvsmx.ws.subscriberprovisioning.data;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

import java.util.List;

public class SubscriberProfileData {
	private List<Entry> entry;
	private String subscriberIdentity;
	private String productOffer;

	private String creditLimit;

	public SubscriberProfileData(){
		
	}
	public SubscriberProfileData(String subscriberIdentity, String productOffer, List<Entry> entry, String creditLimit) {
		this.subscriberIdentity = subscriberIdentity;
		this.productOffer = productOffer;
		this.entry = entry;
		this.creditLimit = creditLimit;
	}
	
	public List<Entry> getEntry() {
		return entry;
	}
	public void setEntry(List<Entry> entry) {
		this.entry = entry;
	}
	public String getSubscriberIdentity() {
		return subscriberIdentity;
	}
	public void setSubscriberIdentity(String subscriberIdentity) {
		this.subscriberIdentity = subscriberIdentity;
	}

	public String getProductOffer() {
		return productOffer;
	}

	public void setProductOffer(String productOffer) {
		this.productOffer = productOffer;
	}

	public String getCreditLimit() {
		return creditLimit;
	}
	public void setCreditLimit(String creditLimit) {
		this.creditLimit = creditLimit;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);
		builder.append("Subscriber Id", subscriberIdentity);
		builder.append("Product Offer Name", productOffer);
		builder.append("Credit Limit", creditLimit);
		if (Collectionz.isNullOrEmpty(entry) == false) {
			for (int i = 0; i < entry.size(); i++) {
				builder.append(entry.get(i).getKey(), entry.get(i).getValue());
			}
		}

		return builder.toString();
	}



}
