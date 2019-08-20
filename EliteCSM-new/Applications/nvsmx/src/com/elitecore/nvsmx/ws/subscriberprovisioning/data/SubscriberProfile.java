package com.elitecore.nvsmx.ws.subscriberprovisioning.data;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.util.LongToStringAdapter;

import javax.jws.WebParam;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

public class SubscriberProfile {
	
	private List<Entry> entry;
	private transient String subscriberIdentity;
	private transient Long creditLimit;
	
	public SubscriberProfile() {
	}

	public SubscriberProfile(List<Entry> entry) {
		this.entry = entry;
	}

	public List<Entry> getEntry() {
		return entry;
	}

	public void setEntry(List<Entry> entry) {
		this.entry = entry;
	}

	public Long getCreditLimit() {
		return creditLimit;
	}

	@XmlElement
	@XmlJavaTypeAdapter(type=long.class, value=LongToStringAdapter.class)
	public void setCreditLimit(@WebParam(name="creditLimit")Long creditLimit) {
		this.creditLimit = creditLimit;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);
		if (Collectionz.isNullOrEmpty(entry) == false) {
			for (int i = 0; i < entry.size(); i++) {
				builder.append(entry.get(i).getKey(), entry.get(i).getValue());
			}
		}

		return builder.toString();
	}

	@XmlTransient
	public String getSubscriberIdentity() {
		return subscriberIdentity;
	}

	
	public void setSubscriberIdentity(String subscriberIdentity) {
		this.subscriberIdentity = subscriberIdentity;
	}
	
}
