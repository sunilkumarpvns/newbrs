package com.elitecore.ssp.web.parentalcontrol.forms;

import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class ParentalControlForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	
	private SubscriberProfile[] subscriberProfileData;

	public SubscriberProfile[] getSubscriberProfileData() {
		return subscriberProfileData;
	}
	public void setSubscriberProfileData(SubscriberProfile[] subscriberProfileData) {
		this.subscriberProfileData = subscriberProfileData;
	}	
}
