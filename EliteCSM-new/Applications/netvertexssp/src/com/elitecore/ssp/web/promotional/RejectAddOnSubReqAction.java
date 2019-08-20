package com.elitecore.ssp.web.promotional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;


import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.webservice.promotional.PromotionalDataManager;

public class RejectAddOnSubReqAction extends BaseWebAction {
	private static final String MODULE = PromotionalAction.class.getSimpleName();
	private static final String VIEW_PROMOTIONAL = "viewPromotional";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
		try{
			String selectedLink = request.getParameter(SessionAttributeKeyConstant.SELECTED_LINK);
			Logger.logTrace(MODULE, " selectedLink  : "+ selectedLink );		
			request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK,selectedLink);
			PromotionalDataManager promotinalDataManager = new PromotionalDataManager();
			SubscriberProfile currentUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
			String subscriberIdentity=null;
			if (currentUser != null) {
				subscriberIdentity=currentUser.getSubscriberID();			
			}
			Logger.logTrace(MODULE, " subscriberIdentity : "+ subscriberIdentity);
			
			String rejectReason = request.getParameter("rejectReason");
			String addOnSubReq = request.getParameter("addOnSubReqID");
			Long addOnSubRequestID = null;
			
			if(addOnSubReq != null){
				addOnSubRequestID = Long.parseLong(addOnSubReq);
			}
			
			if(currentUser != null){
				promotinalDataManager.rejectAddOnSubReq(subscriberIdentity,addOnSubRequestID,rejectReason);
			}
			
 			return mapping.findForward(VIEW_PROMOTIONAL);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			ActionMessage message = new ActionMessage("promotional.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}
}