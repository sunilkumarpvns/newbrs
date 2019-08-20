package com.elitecore.ssp.web.promotional;

import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubscriptionData;
import com.elitecore.ssp.util.constants.ErrorMessageConstants;
import com.elitecore.ssp.util.constants.RequestAttributeKeyConstant;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.web.promotional.forms.UnsubscribePromotionForm;
import com.elitecore.ssp.webservice.promotional.PromotionalDataManager;

public class UnsubscribePromotionAction extends BaseWebAction {
	private static final String MODULE = UnsubscribePromotionAction.class.getSimpleName();
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
		try{
			//request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK,(String)request.getSession().getAttribute(SessionAttributeKeyConstant.SELECTED_LINK));
			UnsubscribePromotionForm unsubscribePromotionForm = (UnsubscribePromotionForm)form;
			Long addOnSubscriptionId=unsubscribePromotionForm.getAddOnPackageId();
			Logger.logDebug(MODULE, "Unsubscriber Promotion, id:"+unsubscribePromotionForm.getAddOnPackageId());
			AddOnSubscriptionData promotionalData = null;
			PromotionalDataManager promotinalDataManager = new PromotionalDataManager();
			SubscriberProfile subscriberProfileData = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
			promotionalData = getActivePromotionData(addOnSubscriptionId,request);			
			String selectedPortal =(String)request.getSession().getServletContext().getAttribute("selectedPortal");
			
			if(selectedPortal != null && selectedPortal.equalsIgnoreCase(RequestAttributeKeyConstant.ENTERPRISE_PORTAL)){
			//	subscriberProfileData.setSelectedPortel(selectedPortal);
				if(subscriberProfileData.getParentID() != null){
					Logger.logDebug(MODULE, "Unsubscriber Promotion: "+promotionalData);
					promotinalDataManager.unsubscribePromotion(addOnSubscriptionId, subscriberProfileData);
					String selectedStatus = unsubscribePromotionForm.getSelectedAddonStatus();
					if(selectedStatus != null){
						request.setAttribute("SelectedStatus", selectedStatus);
						request.setAttribute("responseUrl","/childPromotional.do?SelectedStatus=" + selectedStatus);
					}else{
						request.setAttribute("responseUrl","/childPromotional.do");
					}
					ActionMessage message = new ActionMessage("promotional.unsubscribe.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request,messages);
					return mapping.findForward(SUCCESS);
				}/*else{					
					promotinalDataManager.unsubscribePromotion(promotionalData, subscriberProfileData);
					request.setAttribute("responseUrl","/promotional.do");
					ActionMessage message = new ActionMessage("promotional.unsubscribe.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request,messages);
					return mapping.findForward(SUCCESS);
				}*/
			}
			
			Logger.logDebug(MODULE, "Unsubscriber Promotion: "+promotionalData);
			promotinalDataManager.unsubscribePromotion(addOnSubscriptionId, subscriberProfileData);
			
			String selectedStatus = unsubscribePromotionForm.getSelectedAddonStatus();
			if(selectedStatus != null){
				request.setAttribute("SelectedStatus", selectedStatus);
				request.setAttribute("responseUrl","/promotional.do?SelectedStatus=" + selectedStatus);
			}else{
				request.setAttribute("responseUrl","/promotional.do");
			}
			
			ActionMessage message = new ActionMessage("promotional.unsubscribe.success");
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
				ActionMessage message = new ActionMessage("promotional.unsubscribe.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}			
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			ActionMessage message = new ActionMessage("promotional.unsubscribe.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}
}