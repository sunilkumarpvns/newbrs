package com.elitecore.ssp.web.promotional;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubRequestData;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubscriptionData;
import com.elitecore.ssp.util.constants.RequestAttributeKeyConstant;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.web.promotional.forms.PromotionalForm;
import com.elitecore.ssp.webservice.promotional.PromotionalDataManager;

public class ViewPendingAddOnSubReqAction extends BaseWebAction {
	private static final String MODULE = PromotionalAction.class.getSimpleName();
	private static final String VIEW_PENDING_PRM_OFFER = "viewPendingAddOnSubRequest";

	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
		PromotionalForm promotionalForm = (PromotionalForm)form;	
		try{
			String selectedLink = request.getParameter(SessionAttributeKeyConstant.SELECTED_LINK);
			Logger.logTrace(MODULE, " selectedLink  : "+ selectedLink );		
			request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK,selectedLink);
			PromotionalDataManager promotinalDataManager = new PromotionalDataManager();
			SubscriberProfile childObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
			String subscriberIdentity=childObj.getSubscriberID();		
			Logger.logTrace(MODULE, " subscriberIdentity : "+ subscriberIdentity);
			String prmOfferID = request.getParameter("prmOfferID");
			SubscriberProfile loggedInUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER); 
			
			//List<AddOnSubRequestData> pendingPromotionalReqList = promotinalDataManager.getPendingPromotionalReq(childObj,childObj);
			List<AddOnSubRequestData> pendingPromotionalReqList=(List<AddOnSubRequestData>) request.getSession().getAttribute(SessionAttributeKeyConstant.PENDING_PROMOTIONAL_REQUEST);
			if(pendingPromotionalReqList != null && pendingPromotionalReqList.size() > 0){
				for (AddOnSubRequestData pendingPromotionalSubReqData : pendingPromotionalReqList) {
					if(pendingPromotionalSubReqData.getAddOnSubReqID().equals(Long.valueOf(prmOfferID))){
						promotionalForm.setPendingPromotionalSubReqData(pendingPromotionalSubReqData);
						AddOnPackage promotionalData = getAvailablePromotionData(pendingPromotionalSubReqData.getAddOnID(),request);
						promotionalForm.setPromotionalData(promotionalData);
						break;
					}
				}
			}
			request.setAttribute(RequestAttributeKeyConstant.PROMOTIONAL_OFFER_FORM, promotionalForm);
			return mapping.findForward(VIEW_PENDING_PRM_OFFER);
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