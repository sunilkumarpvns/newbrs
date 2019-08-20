package com.elitecore.ssp.web.bod;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDPackage;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionData;
import com.elitecore.ssp.util.constants.RequestAttributeKeyConstant;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.bod.forms.BodForm;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.webservice.bod.BodDataManager;
import com.elitecore.ssp.webservice.subscriptionhistory.SubscriptionHistoryDataManager;

public class PendingBodSubReqAction extends BaseWebAction {
	private static final String MODULE = PendingBodSubReqAction.class.getSimpleName();
	private static final String FAILURE = "failure";	
	private static final String VIEW_CHILD_BOD = "viewChildBod";
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {		
		try{
			Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());			
						
			request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK,"serviceactivation");
			
			BodForm bodForm=(BodForm)form;
			BodDataManager bodDataManager = new BodDataManager();
			SubscriberProfile childObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
			SubscriberProfile loggedInUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
			
			String subscriberIdentity=childObj.getSubscriberID();		
			Logger.logTrace(MODULE, " subscriberIdentity : "+ subscriberIdentity);
			
			if(childObj != null ){
				Logger.logInfo(MODULE, " Retrieving pending request data ");
				List<BoDSubscriptionData> pendingBodReqData = bodDataManager.getPendingBodReq(childObj);
				bodForm.setPendingBodReqData(pendingBodReqData);
				request.getSession().setAttribute(SessionAttributeKeyConstant.PENDING_BOD_REQUEST, pendingBodReqData);
			}
			
			List<BoDPackage> availableBodPackages = bodDataManager.getBodPackageData(childObj);
			bodForm.setAvailableBodPackages(availableBodPackages);
			request.getSession().setAttribute(SessionAttributeKeyConstant.BOD_OFFERS,availableBodPackages);
			
			List<BoDSubscriptionData> activeBodPackages = bodDataManager.getActiveBodService(childObj);
			bodForm.setActiveBodPackages(activeBodPackages);
			request.getSession().setAttribute(SessionAttributeKeyConstant.ACTIVE_BOD_SUBSCRIPTIONS,activeBodPackages);
			
			SubscriptionHistoryDataManager subscriptionHistoryDataManager = new SubscriptionHistoryDataManager();
			List<BoDSubscriptionData> bodSubscriptionHistories = subscriptionHistoryDataManager.getBodSubscriptionHistory(childObj); 
			bodForm.setBodSubscriptionHistories(bodSubscriptionHistories);
			
			request.setAttribute(RequestAttributeKeyConstant.BOD_FORM, bodForm);			
			return mapping.findForward(VIEW_CHILD_BOD);			
		}catch(Exception e){
			Logger.logError(MODULE,e.getMessage());
		}
		return mapping.findForward(FAILURE);
	}
}