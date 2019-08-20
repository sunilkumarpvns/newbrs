package com.elitecore.ssp.web.subscriber.forms;

import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class SubscriberProfilerForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	private String action;
	private String addChildAction;
	private String[] propertyFields;
	private SubscriberProfile[] subscriberProfileDatas;
	
	public String getAddChildAction() {
		return addChildAction;
	}
	public void setAddChildAction(String addChildAction) {
		this.addChildAction = addChildAction;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String[] getPropertyFields() {
		return propertyFields;
	}
	public void setPropertyFields(String[] propertyFields) {
		this.propertyFields = propertyFields;
	}
	public SubscriberProfile[] getSubscriberProfileDatas() {
		return subscriberProfileDatas;
	}
	public void setSubscriberProfileDatas(
			SubscriberProfile[] subscriberProfileDatas) {
		this.subscriberProfileDatas = subscriberProfileDatas;
	}
}