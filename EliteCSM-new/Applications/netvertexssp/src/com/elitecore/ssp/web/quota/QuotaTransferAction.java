package com.elitecore.ssp.web.quota;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.util.ChildAccountUtility;
import com.elitecore.ssp.util.DataManagerUtils;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.web.quota.forms.QuotaTransferForm;

public class QuotaTransferAction extends BaseWebAction {
	private static final String MODULE = QuotaTransferAction.class.getSimpleName();
	private static final String QUOTA_TRANSFER_PAGE = "quotaTransferPage";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
		QuotaTransferForm promotionalForm = (QuotaTransferForm)form;
		
		Logger.logTrace(MODULE, " getQuotaToBeRequested: " + promotionalForm.getQuotaToBeRequested() + ", getUserIdToBeRequested: " + promotionalForm.getUserIdToBeRequested());
		
		try{
			String selectedLink = request.getParameter(SessionAttributeKeyConstant.SELECTED_LINK);
			Logger.logTrace(MODULE, " selectedLink  : "+ selectedLink );
			SubscriberProfile childObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
			SubscriberProfile loggedInUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
			String loggedInSubscriberId = loggedInUser.getSubscriberID();
			 
			String subscriberIdentity=childObj.getSubscriberID();
			Logger.logDebug(MODULE, " subscriberIdentity : "+ subscriberIdentity);
			Logger.logDebug(MODULE, " loggedInUser subscriberIdentity : "+ loggedInUser.getSubscriberID());
			String selectedPortal = (String)request.getSession().getServletContext().getAttribute("selectedPortal");
			Logger.logInfo(MODULE, " selectedPortal : "+ selectedPortal);

			DataManagerUtils dmUtils = new DataManagerUtils();
			if(promotionalForm.getQuotaToBeRequested() != null && promotionalForm.getUserIdToBeRequested() != null){
				
				String quota = promotionalForm.getQuotaToBeRequested();
				String userId = promotionalForm.getUserIdToBeRequested();
				long quotaLong = ChildAccountUtility.convertMBToBytes(Integer.parseInt(quota));
				String updateQuery = "update tblnetvertexcustomer set param7='" + userId  +"', " +
						"param8='" + quotaLong + "' where subscriberidentity='" + loggedInSubscriberId + "'";
				System.out.println("****** updateQuery, " + updateQuery);
				dmUtils.executeUpdateOrInsert(updateQuery);
				request.getSession().setAttribute("QuotaTransferRequestMessage", "Your request has been submitted");
			}
			
			/************** PENDING REQUEST CODE - START *************/
			
			Map<String, String> userRequestMap =  dmUtils.getUsersRequestsForQuotaTransfer(loggedInSubscriberId);
			
			if(userRequestMap.size() > 0){
				request.getSession().setAttribute("QuotaTransferRequestMap", userRequestMap);
			}
			
			/************** PENDING REQUEST CODE - END *************/
			
			return mapping.findForward(QUOTA_TRANSFER_PAGE);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			ActionMessage message = new ActionMessage("quota.transfer.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}
	
}