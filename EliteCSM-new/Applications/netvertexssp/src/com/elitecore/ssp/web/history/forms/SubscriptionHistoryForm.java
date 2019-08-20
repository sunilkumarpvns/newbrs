package com.elitecore.ssp.web.history.forms;

import java.util.List;

import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubscriptionData;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionData;
import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class SubscriptionHistoryForm extends BaseWebForm {
	
	
	private static final long serialVersionUID = 1L;
	
	private List<AddOnSubscriptionData> promotionSubscriptions;
	
	private List<BoDSubscriptionData> bodSubscriptions;
	
	public List<AddOnSubscriptionData> getPromotionSubscriptions() {
		return promotionSubscriptions;
	}
	public void setPromotionSubscriptions(
			List<AddOnSubscriptionData> promotionSubscriptions) {
		this.promotionSubscriptions = promotionSubscriptions;
	}
	public List<BoDSubscriptionData> getBodSubscriptions() {
		return bodSubscriptions;
	}
	public void setBodSubscriptions(List<BoDSubscriptionData> bodSubscriptions) {
		this.bodSubscriptions = bodSubscriptions;
	}
	

}
