package com.elitecore.ssp.web.bod;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionData;
import com.elitecore.ssp.util.constants.RequestAttributeKeyConstant;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.bod.forms.BodForm;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.webservice.bod.BodDataManager;

public class ViewPendingBodSubReqAction extends BaseWebAction {
	private static final String MODULE = ViewPendingBodSubReqAction.class.getSimpleName();
	private static final String FAILURE = "failure";
	private static final String VIEW_PENDING_BOD_OFFER = "viewPendingBodSubRequest";	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {		
		try{
			Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());									
			String selectedLink = request.getParameter(SessionAttributeKeyConstant.SELECTED_LINK);
			Logger.logTrace(MODULE, " selectedLink  : "+ selectedLink );		
			request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK,selectedLink);
			
			BodForm bodForm=(BodForm)form;
			BodDataManager bodDataManager = new BodDataManager();
			SubscriberProfile childObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
			String subscriberIdentity=childObj.getSubscriberID();		
			Logger.logDebug(MODULE, "SubscriberIdentity : "+ subscriberIdentity);
			String bodID = request.getParameter("bodID");
			Long bodSubscriptionID = Long.parseLong(bodID);
			Logger.logDebug(MODULE, "bodID : "+ bodID);
			String bodStartTime = request.getParameter("bodStartTime");
			long bodStartTimeLong = Long.parseLong(bodStartTime);			
			Logger.logDebug(MODULE, "bodStartTimeLong : "+ bodStartTimeLong);
			SubscriberProfile loggedInUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
			
			if(loggedInUser != null){				
				List<BoDSubscriptionData>  pendingBodReqList = bodDataManager.getPendingBodReq(childObj);
				if(pendingBodReqList != null && pendingBodReqList.size() > 0){
					for (BoDSubscriptionData pendingBodData : pendingBodReqList) {
						if(pendingBodData.getBodSubscriptionID().equals(bodSubscriptionID) &&  pendingBodData.getBodStartTime().equals(bodStartTimeLong)){							
							bodForm.setPendingBodSubReqData(pendingBodData);
							break;
						}
					}
				}									
			}					
			request.setAttribute(RequestAttributeKeyConstant.BOD_FORM, bodForm);
 			return mapping.findForward(VIEW_PENDING_BOD_OFFER);
 			
		}catch(Exception e){
			Logger.logError(MODULE,e.getMessage());
		}
		return mapping.findForward(FAILURE);
	}
}