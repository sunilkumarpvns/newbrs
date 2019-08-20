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
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDPackage;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionData;
import com.elitecore.ssp.util.constants.ErrorMessageConstants;
import com.elitecore.ssp.util.constants.RequestAttributeKeyConstant;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.bod.forms.SubscribeBodForm;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.webservice.bod.BodDataManager;

public class RejectBodSubReqAction extends BaseWebAction {
	private static final String MODULE = RejectBodSubReqAction.class.getSimpleName();
	private static final String BOD = "bod";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
	
		try
		{
			SubscribeBodForm subscribePromotionForm = (SubscribeBodForm)form;			
			request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK, request.getParameter(SessionAttributeKeyConstant.SELECTED_LINK));
			Long bodPackageId = subscribePromotionForm.getBodPackageId();
			String rejectReason = request.getParameter(RequestAttributeKeyConstant.REJECT_REASON);
			Logger.logDebug(MODULE, "rejectReason---->"+rejectReason);			
			BoDSubscriptionData bodServiceData = new BoDSubscriptionData();					
			BodDataManager bodDataManager = new BodDataManager();
			SubscriberProfile subscriberProfileData = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
			bodServiceData.setRejectReason(rejectReason);			
			BoDPackage bodPackageData = getBodPackageData(bodPackageId, request);
									
			if(bodPackageData==null){
				throw new Exception("BoDPackage Data Not Found");
			}
			
			String bodPkgSubReqId = request.getParameter("bodPkgSubReqId");
			String startTime = request.getParameter("startTime");			
			Logger.logDebug(MODULE, "bodPkgSubReqId : "+bodPkgSubReqId);
				
			if(bodPkgSubReqId!=null){
				bodServiceData.setBodSubscriptionID(Long.parseLong(bodPkgSubReqId));
			}else{
				bodServiceData.setBodSubscriptionID(0L);
			}
											
			Logger.logDebug(MODULE," Reject BoD Pending Request");
			bodDataManager.rejectBodServiceRequest(subscriberProfileData,bodServiceData,startTime, null, null);
			//bodDataManager.rejectBodServiceRequest(subscriberProfileData, bodServiceData);
			ActionMessage message = new ActionMessage("enterprise.bod.reject.success");				
			request.setAttribute("responseUrl","/bod.do");				
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
				ActionMessage message = new ActionMessage("bod.subscribe.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			ActionMessage message = new ActionMessage("bod.subscribe.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}
}