package com.elitecore.ssp.web.bod;

import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionData;
import com.elitecore.ssp.util.constants.ErrorMessageConstants;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.bod.forms.UnsubscribeBoDForm;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.webservice.bod.BodDataManager;

public class UnsubscribeBoDAction extends BaseWebAction {
	private static final String MODULE = UnsubscribeBoDAction.class.getSimpleName();
	private static final String BOD = "bod";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
	
		try
		{
			UnsubscribeBoDForm unsubscribeBoDForm = (UnsubscribeBoDForm)form;
			Long bodId=unsubscribeBoDForm.getBodId();
			BoDSubscriptionData bodSubscriptionData =null;
			
			BodDataManager bodDataManager = new BodDataManager();
			SubscriberProfile subscriberProfileData = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
			//Logger.logDebug(MODULE, "Portal: "+subscriberProfileData.getSelectedPortel());
			Logger.logDebug(MODULE, "Unsubscribing BOD (ID: "+bodId+") for SubscriberIdentity :"+subscriberProfileData.getSubscriberID());
			bodSubscriptionData = getBodSubscriptionData(bodId,request);
			if(bodSubscriptionData==null){
				throw new Exception("BOD Subscription Data Not Found for ID: "+bodId);
			}
			bodDataManager.unsubscribeBodService(subscriberProfileData, bodSubscriptionData);
			request.setAttribute("responseUrl","/bod.do");
			ActionMessage message = new ActionMessage("bod.unsubscribe.success");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveMessages(request,messages);
			return mapping.findForward(SUCCESS);
		}catch(RemoteException e){
			if(e.getMessage().equalsIgnoreCase(ErrorMessageConstants.ACCOUNT_EXPIRED)){
				ActionMessage message = new ActionMessage("general.account.expired");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}else{
				Logger.logTrace(MODULE, e);
				ActionMessage message = new ActionMessage("bod.unsubscribe.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			ActionMessage message = new ActionMessage("bod.unsubscribe.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}
}