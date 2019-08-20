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

public class InitPendingBodSubReqAction extends BaseWebAction {
	private static final String MODULE = InitPendingBodSubReqAction.class.getSimpleName();
	private static final String FAILURE = "failure";
	private static final String INIT_PENDING_BOD_REQ="initPendingBodReq";	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {		
		try{
			Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());									
			request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK,"serviceactivation");
			
			BodForm bodForm=(BodForm)form;
			BodDataManager bodDataManager = new BodDataManager();
			SubscriberProfile childObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
			String subscriberIdentity=childObj.getSubscriberID();		
			Logger.logDebug(MODULE, "SubscriberIdentity : "+ subscriberIdentity);
			SubscriberProfile loggedInUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
			
			if(loggedInUser != null){
				List<BoDSubscriptionData> pendingBodReqData = bodDataManager.getPendingBodReq(childObj);
				
				bodForm.setPendingBodReqData(pendingBodReqData);
				request.getSession().setAttribute(SessionAttributeKeyConstant.PENDING_BOD_REQUEST, pendingBodReqData);
			}
			
			request.setAttribute(RequestAttributeKeyConstant.BOD_FORM, bodForm);
 			return mapping.findForward(INIT_PENDING_BOD_REQ);			
		}catch(Exception e){
			Logger.logError(MODULE,e.getMessage());
		}
		return mapping.findForward(FAILURE);
	}
}