package com.elitecore.ssp.web.promotional;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubRequestData;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubscriptionData;
import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.util.constants.ApprovalStates;
import com.elitecore.ssp.util.constants.RequestAttributeKeyConstant;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.constants.SubscriptionState;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.web.promotional.constants.PromotionalOfferConstants;
import com.elitecore.ssp.web.promotional.forms.PromotionalForm;
import com.elitecore.ssp.webservice.promotional.PromotionalDataManager;
import com.elitecore.ssp.webservice.subscriptionhistory.SubscriptionHistoryDataManager;

public class PendingAddOnSubRequestAction extends BaseWebAction {
	private static final String MODULE = PendingAddOnSubRequestAction.class.getSimpleName();
	private static final String VIEW_PROMOTIONAL = "viewChildPromotional";
	private static final String VIEW_PROMOTIONAL_HISTORY = "viewPromotionalHistory";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
		PromotionalForm promotionalForm = (PromotionalForm)form;	
		try{
			request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK,"prmoffer");
			PromotionalDataManager promotinalDataManager = new PromotionalDataManager();
			SubscriberProfile childObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
			String subscriberIdentity=null;
			if (childObj != null) {
				subscriberIdentity=childObj.getSubscriberID();			
			}
			Logger.logTrace(MODULE, " subscriberIdentity : "+ subscriberIdentity);
			
			List<AddOnSubRequestData> pendingPromotionalReqList = null;
			List<AddOnSubRequestData> pendingHistoryRequestsList=Collectionz.newArrayList();
			if(childObj != null){
				pendingPromotionalReqList = promotinalDataManager.getPendingPromotionalReq(childObj,null);
				pendingHistoryRequestsList.addAll(pendingPromotionalReqList);
				filterApprovalPendingAddOns(pendingPromotionalReqList);
				promotionalForm.setPendingPromotionalReqData(pendingPromotionalReqList);
				request.getSession().setAttribute(SessionAttributeKeyConstant.PENDING_PROMOTIONAL_REQUEST, pendingPromotionalReqList);
			}
			List<AddOnPackage> availableAddons=getAvailableAddOns(promotinalDataManager,childObj);
			promotionalForm.setAvailableAddons(availableAddons);
			request.getSession().setAttribute(SessionAttributeKeyConstant.PROMOTIONAL_OFFERS,availableAddons);

			Map<Long,AddOnPackage> addOnPackageMap=convertAddOnListToMap(availableAddons);
			
			promotionalForm.setAddOnPackageMapById(addOnPackageMap);
			request.getSession().setAttribute(SessionAttributeKeyConstant.ADDONMAP, addOnPackageMap);
			
			List<AddOnSubscriptionData> activePromotions = promotinalDataManager.getActivatedPromotions(childObj);
			promotionalForm.setActicvePromotions(activePromotions);
			request.getSession().setAttribute(SessionAttributeKeyConstant.ACTIVE_PROMOTIONAL_OFFERS,activePromotions);
			
			
			request.setAttribute(RequestAttributeKeyConstant.PROMOTIONAL_OFFER_FORM, promotionalForm);
			
			String requestedPageStr = request.getParameter(RequestAttributeKeyConstant.REQUESTED_PAGE);
			if(requestedPageStr == null){
				request.setAttribute(RequestAttributeKeyConstant.CURRENT_PAGE, 1);
			}else{
				Logger.logDebug(MODULE, "Requested Page Parameter is : "+ requestedPageStr);
				Integer requestedPage = Integer.parseInt(requestedPageStr);
				if(requestedPage == null){
					request.setAttribute(RequestAttributeKeyConstant.CURRENT_PAGE, 1);
				}
				request.setAttribute(RequestAttributeKeyConstant.CURRENT_PAGE, requestedPage);
			}
			
			String selectedStatus = request.getParameter("SelectedStatus");
			Logger.logDebug(MODULE, "Selected Status Parameter is : "+ selectedStatus);
		    request.setAttribute("SelectedStatus", selectedStatus);
		   //if no status is selected return on same page
		    
		    if(Strings.isNullOrEmpty(selectedStatus)){
		       List<AddOnSubscriptionData> subscriptionDatas=promotinalDataManager.getSubscription(childObj, childObj);
			   promotionalForm.setSubscriptionHistoriesList(subscriptionDatas);
		       return mapping.findForward(VIEW_PROMOTIONAL);
		   }
		    // if all status is selected return to history page
		   if(PromotionalOfferConstants.ALL.equalsIgnoreCase(selectedStatus)==true){
			   SubscriptionHistoryDataManager historyManager = new SubscriptionHistoryDataManager();
			   List<AddOnSubscriptionData> subscriptionDatas =  historyManager.getPromotionSubscriptionHistory(childObj);
			   promotionalForm.setSubscriptionHistoriesList(subscriptionDatas);
			   return mapping.findForward(VIEW_PROMOTIONAL_HISTORY);
		   }
		   if((selectedStatus.equalsIgnoreCase(PromotionalOfferConstants.APPROVED)) ||
				   selectedStatus.equalsIgnoreCase(PromotionalOfferConstants.APPROVAL_PENDING) ||
				   selectedStatus.equalsIgnoreCase(PromotionalOfferConstants.REJECTED) ){
			   List<AddOnSubscriptionData> subscriptionDatas=getSubscriptionHistoryFromPendingAddOns(pendingHistoryRequestsList, addOnPackageMap);
			   if(Collectionz.isNullOrEmpty(subscriptionDatas)==false){
				   filterSusbcriptionHistory(subscriptionDatas, ApprovalStates.fromName(selectedStatus).getStringVal());
				   promotionalForm.setSubscriptionHistoriesList(subscriptionDatas);
				   return mapping.findForward(VIEW_PROMOTIONAL_HISTORY);
			   }
		   }
		   if((selectedStatus.equalsIgnoreCase(PromotionalOfferConstants.ACTIVE))){
			   List<AddOnSubscriptionData> subscriptionDatas=promotinalDataManager.getSubscription(childObj, childObj);
			   if(Collectionz.isNullOrEmpty(subscriptionDatas)==false){
				   filterSusbcriptionHistory(subscriptionDatas, ApprovalStates.fromName(selectedStatus).getStringVal());
				   promotionalForm.setSubscriptionHistoriesList(subscriptionDatas);
				   return mapping.findForward(VIEW_PROMOTIONAL_HISTORY);
			   }
		   }
		   SubscriptionHistoryDataManager historyManager = new SubscriptionHistoryDataManager();
		   List<AddOnSubscriptionData> subscriptionDatas =  historyManager.getPromotionSubscriptionHistory(childObj);
		   if(Collectionz.isNullOrEmpty(subscriptionDatas)==false){
				Logger.logInfo(MODULE, "No Promotional Subscription History found for subscriberIdentity : "+ subscriberIdentity );
				filterSusbcriptionHistory(subscriptionDatas, SubscriptionState.fromName(selectedStatus).getName());
			}   
		      promotionalForm.setSubscriptionHistoriesList(subscriptionDatas);
			  return mapping.findForward(VIEW_PROMOTIONAL_HISTORY);
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