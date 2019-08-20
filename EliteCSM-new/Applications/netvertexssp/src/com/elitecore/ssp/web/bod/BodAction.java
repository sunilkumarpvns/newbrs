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

public class BodAction extends BaseWebAction {
	private static final String MODULE = BodAction.class.getSimpleName();
	private static final String FAILURE = "failure";
	private static final String BOD="bod";
	private static final String VIEW_CHILD_BOD = "childBod";
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {		
		try{
			Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());			
						
			request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK,"serviceactivation");
			String[] subscriberIdentityArray = request.getParameterValues("select");
			request.setAttribute(RequestAttributeKeyConstant.SELECTED_SUBSCRIBER, subscriberIdentityArray);
			BodForm bodForm=(BodForm)form;
			BodDataManager bodDataManager= new BodDataManager();
			SubscriberProfile selectedUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
			String subscriberIdentity = selectedUser.getSubscriberID();
			SubscriberProfile loggedInUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
			
			Logger.logTrace(MODULE, " subscriberIdentity : "+ subscriberIdentity);	
		    String selectedPortal = (String)getServlet().getServletContext().getAttribute("selectedPortal");
		    Logger.logTrace(MODULE, " selectedPortal : "+ selectedPortal);
		    
			if(selectedPortal!=null && selectedPortal.equalsIgnoreCase(RequestAttributeKeyConstant.ENTERPRISE_PORTAL)){
				if(loggedInUser != null){
					Logger.logTrace(MODULE, " Loggedin user ID  "+ loggedInUser .getSubscriberID());
					Logger.logTrace(MODULE, " selectedUser user ID : "+ selectedUser .getSubscriberID());
					List<BoDSubscriptionData> pendingPromotionalReqData = bodDataManager.getPendingBodReq(selectedUser);
					bodForm.setPendingBodReqData(pendingPromotionalReqData);
					request.getSession().setAttribute(SessionAttributeKeyConstant.PENDING_PROMOTIONAL_REQUEST, pendingPromotionalReqData);
				}			
			}
			
			List<BoDPackage> availableBodPackages = bodDataManager.getBodPackageData(selectedUser);
			bodForm.setAvailableBodPackages(availableBodPackages);
			request.getSession().setAttribute(SessionAttributeKeyConstant.BOD_OFFERS,availableBodPackages);
			//2 is for Active BOD
			List<BoDSubscriptionData> activeBodPackages = bodDataManager.getActiveBodService(selectedUser);
			bodForm.setActiveBodPackages(activeBodPackages);
			request.getSession().setAttribute(SessionAttributeKeyConstant.ACTIVE_BOD_SUBSCRIPTIONS,activeBodPackages);
			
			SubscriptionHistoryDataManager subscriptionHistoryDataManager = new SubscriptionHistoryDataManager();
			List<BoDSubscriptionData> bodSubscriptionHistories = bodDataManager.getBoDSubscriptionHistory(selectedUser.getSubscriberID()); 
			bodForm.setBodSubscriptionHistories(bodSubscriptionHistories);
			
			request.setAttribute(RequestAttributeKeyConstant.BOD_FORM, bodForm);
			Logger.logTrace(MODULE, " Forwarding to BoD Parent View ");
			return mapping.findForward(BOD);			
		}catch(Exception e){
			Logger.logError(MODULE,e.getMessage());
		}
		return mapping.findForward(FAILURE);
	}
}