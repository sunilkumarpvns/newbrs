package com.elitecore.ssp.web.quota;

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
import com.elitecore.ssp.web.quota.forms.QuotaApproveForm;

public class QuotaApproveAction extends BaseWebAction {
	private static final String MODULE = QuotaApproveAction.class.getSimpleName();
	private static final String QUOTA_TRANSFER_PAGE = "quotaTransferPage";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
		QuotaApproveForm quotaApproveForm = (QuotaApproveForm)form;
		String requesterSubscriberId = quotaApproveForm.getRequesterUserName();
		String requestedQuota = quotaApproveForm.getRequestedQuota();
		String resultActionToBePerformed = quotaApproveForm.getActionPerformedForApproval();
		
		
		Logger.logTrace(MODULE, " getActionPerformedForApproval: " + resultActionToBePerformed +
					", requesterSubscriberId: " + requesterSubscriberId +
					", requestedQuota: " + requestedQuota);
		
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
			// Check user has rejected the quota request - Remove the entry from the table and relead the page
			if("Rejected".equalsIgnoreCase(resultActionToBePerformed)){
				Logger.logDebug(MODULE, "Received the Quota Reject action for subscriberId:" + requesterSubscriberId + 
						", for quota:" + requestedQuota);
				removeRequesterUserEntry(requesterSubscriberId, requestedQuota, dmUtils);
				request.getSession().setAttribute("QuotaTransferRequestMessage", "Your have Rejected request");
			}
			// If user has approved the request - check if user has sufficient quota - 
			// if yes - transfer the quota
			// if no - send message to the user that can not transfer because insufficient balance.
			
			if("Approved".equalsIgnoreCase(resultActionToBePerformed)){
				Logger.logDebug(MODULE, "Received the Quota Approve action for subscriberId:" + requesterSubscriberId + 
										", for quota:" + requestedQuota);
				long balance = dmUtils.getUsersBalanceQuota(loggedInSubscriberId);
				
				Logger.logDebug(MODULE, "Logged in user balance is:" + balance);
				long requestedQuotaInBytes = ChildAccountUtility.convertMBToBytes(Integer.parseInt(requestedQuota));
				
				//String requesterSubscriberId = dmUtils.getSubscriberIdFromUserName(requesterSubscriberId);
				if(balance >= requestedQuotaInBytes){
					Logger.logDebug(MODULE, "User has sufficient balance so that transfer can be transferred...");
					// 1. Add the balance usage to donor user and
					addToSessionUsageQuota(loggedInSubscriberId, requestedQuotaInBytes, dmUtils);
					// 2. reduce the balance usage from recipient  user
					
					reduceFromSessionUsageQuota(requesterSubscriberId, requestedQuotaInBytes, dmUtils);
					// 3. Clear transfer request entry
					Logger.logDebug(MODULE, "Quota has been transferred action for subscriberId:" + requesterSubscriberId + 
							", for quota:" + requestedQuota);
					removeRequesterUserEntry(requesterSubscriberId, requestedQuota, dmUtils);
					request.getSession().setAttribute("QuotaTransferRequestMessage", "Your have Approved request");
				} else {
					Logger.logDebug(MODULE, "User has NOT sufficient balance...so transfer can not be approved");
					removeRequesterUserEntry(requesterSubscriberId, requestedQuota, dmUtils);
					request.getSession().setAttribute("QuotaTransferRequestMessage", "Can not approve request since insufficient balance");
				}
				String updateQuery = "update tblnetvertexcustomer set param7='', param8='' where subscriberidentity='" + requesterSubscriberId + "'";
				Logger.logDebug(MODULE, "****** updateQuery in QuotaApprove for rejection:- " + updateQuery);
				dmUtils.executeUpdateOrInsert(updateQuery);
			}
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
	
	private void removeRequesterUserEntry(String requesterSubscriberId, String requestedQuota, DataManagerUtils dmUtils) throws Exception{
		
		String updateQuery = "update tblnetvertexcustomer set param7='', param8='' where subscriberidentity='" + requesterSubscriberId + "'";
		Logger.logDebug(MODULE, "****** updateQuery in QuotaApprove for rejection:- " + updateQuery);
		dmUtils.executeUpdateOrInsert(updateQuery);

	}
	
	private void addToSessionUsageQuota(String userName, long usage, DataManagerUtils dmUtils) throws Exception{
		String query = "update tblmsessionusagesummary set totaloctets = totaloctets + " + usage + " where userid ='" + userName + "'";
		dmUtils.executeUpdateOrInsert(query);
	}
	
	private void reduceFromSessionUsageQuota(String userName, long usage, DataManagerUtils dmUtils) throws Exception{
		String query = "update tblmsessionusagesummary set totaloctets = totaloctets - " + usage + " where userid ='" + userName + "'";
		dmUtils.executeUpdateOrInsert(query);
	}
	
	
}