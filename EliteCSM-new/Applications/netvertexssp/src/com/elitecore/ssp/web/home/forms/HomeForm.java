package com.elitecore.ssp.web.home.forms;



import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class HomeForm extends BaseWebForm {
		
	private static final long serialVersionUID = 1L;
	
	private String policyname;
	private String duration;
	//private QuotaUsageData[] quotaUsageData;
	private SubscriberProfile[] childAccounts;
	private SubscriberProfile currentUser;
	
	public SubscriberProfile getCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(SubscriberProfile currentUser) {
		this.currentUser = currentUser;
	}
	public String getPolicyname() {
		return policyname;
	}
	public void setPolicyname(String policyname) {
		this.policyname = policyname;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	/*public QuotaUsageData[] getQuotaUsageDatas() {
		return quotaUsageData;
	}
	public void setQuotaUsageDatas(QuotaUsageData[] quotaUsageData) {
		this.quotaUsageData = quotaUsageData;
	}*/
	public SubscriberProfile[] getChildAccounts() {
		return childAccounts;
	}
	public void setChildAccounts(SubscriberProfile[] subscriberProfile) {
		this.childAccounts = subscriberProfile;
	}	
}
