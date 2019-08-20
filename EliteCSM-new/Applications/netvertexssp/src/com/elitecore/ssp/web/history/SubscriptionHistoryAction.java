package com.elitecore.ssp.web.history;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubscriptionData;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionData;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.web.history.forms.SubscriptionHistoryForm;
import com.elitecore.ssp.webservice.subscriptionhistory.SubscriptionHistoryDataManager;

public class SubscriptionHistoryAction extends BaseWebAction {

	private static final String MODULE = SubscriptionHistoryAction.class.getSimpleName();
	private static final String SUBSCRIPTON_HISTORY="subscriptionHistory";

	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
		SubscriptionHistoryForm historyForm = (SubscriptionHistoryForm)form;
		try
		{
			SubscriptionHistoryDataManager historyDataManager = new SubscriptionHistoryDataManager();
			SubscriberProfile subscriberProfileData = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
			List<BoDSubscriptionData> bodSubscriptions =  historyDataManager.getBodSubscriptionHistory(subscriberProfileData);
			List<AddOnSubscriptionData> promotionSubscriptions =historyDataManager.getPromotionSubscriptionHistory(subscriberProfileData);
			historyForm.setBodSubscriptions(bodSubscriptions);
			historyForm.setPromotionSubscriptions(promotionSubscriptions);
			request.setAttribute("historyForm", historyForm);
			request.setAttribute("bodSubscriptions", bodSubscriptions);
			request.setAttribute("promotionSubscriptions", promotionSubscriptions);
			return mapping.findForward(SUBSCRIPTON_HISTORY);
			 
		}catch(Exception e){
			e.printStackTrace();
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}


}
