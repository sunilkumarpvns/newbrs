package com.elitecore.ssp.web.bod;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Map;

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
import com.elitecore.ssp.util.EliteUtility;
import com.elitecore.ssp.util.constants.ErrorMessageConstants;
import com.elitecore.ssp.util.constants.RequestAttributeKeyConstant;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.bod.forms.SubscribeBodForm;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.webservice.bod.BodDataManager;

public class SubscribeBodAction extends BaseWebAction {
	private static final String MODULE = SubscribeBodAction.class.getSimpleName();
	private static final String BOD = "bod";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
	
		try
		{
			SubscribeBodForm subscribeBodForm = (SubscribeBodForm)form;			
			//request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK, request.getParameter(SessionAttributeKeyConstant.SELECTED_LINK));
			Long bodPackageId = subscribeBodForm.getBodPackageId();
			String endTimeParam = (String)request.getParameter("endTime");	
			String subscriberIdentity = (String)request.getParameter("subscriberIdentity");
			//String selectedPortal = request.getParameter("selectedPortal");	
			String selectedPortal = (String)request.getSession().getServletContext().getAttribute("selectedPortal");
			Logger.logTrace(MODULE, "Portal is: " + selectedPortal);
			SubscriberProfile currentUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
			
			BoDSubscriptionData bodServiceData = new BoDSubscriptionData();		
			BodDataManager bodDataManager = new BodDataManager();
			SubscriberProfile subscriberProfileData = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
			request.getSession().getServletContext().setAttribute("selectedPortal", selectedPortal);
			//subscriberProfileData.setSelectedPortel(selectedPortal);
			BoDPackage bodPackageData = getBodPackageData(bodPackageId, request);
			Map<String,SubscriberProfile> subscriberProfileMap = (Map<String,SubscriberProfile>)request.getSession().getAttribute(SessionAttributeKeyConstant.SUBSCRIBER_PROFILE_MAP);
			SubscriberProfile childObj = subscriberProfileMap.get(subscriberIdentity);
			
			if(bodPackageData==null){ throw new Exception("BoDPackage Data Not Found"); }

			if(currentUser.getParentID() != null && selectedPortal !=null && selectedPortal.equalsIgnoreCase(RequestAttributeKeyConstant.ENTERPRISE_PORTAL)){
				 
				request.getSession().getServletContext().setAttribute("selectedPortal", RequestAttributeKeyConstant.ENTERPRISE_PORTAL);
				//parentObj.setSelectedPortel(subscriberProfileData.getSelectedPortel());
				bodDataManager.submitBodRequest(bodPackageData,subscriberProfileData,subscribeBodForm.getStartTime(),subscribeBodForm.getDuration(), subscribeBodForm.getDurationUnit());
				Logger.logDebug(MODULE," Subscribe BoD Request Sent");				
				request.setAttribute("responseUrl","/childBOD.do?"+SessionAttributeKeyConstant.SELECTED_LINK+"bodPackage");
				ActionMessage message = new ActionMessage("enterprise.bod.subscribe.request.submit.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS);		
				
			}else {
				
				String bodPkgSubReqId = request.getParameter("bodPkgSubReqId");
				 
				String startTime = request.getParameter("startTime");
				subscribeBodForm.setStartTime(startTime);
			 
				//bodServiceData.setStartDateTime(startTime);
				
				if(bodPkgSubReqId!=null){ 
					bodServiceData.setBodSubscriptionID(Long.parseLong(bodPkgSubReqId));
				} else{ 
					bodServiceData.setBodPackageID(0L);
				}
																
				Logger.logDebug(MODULE," Approve BoD Pending Request Sent");
				ActionMessage 	message = null;
				if(childObj != null && selectedPortal !=null && selectedPortal.equalsIgnoreCase(RequestAttributeKeyConstant.ENTERPRISE_PORTAL)){
					Logger.logDebug(MODULE,"Child is subscribing BoD ,  Child is: "+childObj .getUserName());
					request.getSession().getServletContext().setAttribute("selectedPortal", RequestAttributeKeyConstant.ENTERPRISE_PORTAL);
					//childObj.setSelectedPortel(selectedPortal);
					Logger.logDebug(MODULE,"BoD Subscription ID is: "+bodServiceData.getBodSubscriptionID());
					bodDataManager.approveBodService(childObj,bodServiceData,subscribeBodForm.getStartTime(),subscribeBodForm.getDuration(), subscribeBodForm.getDurationUnit());
					message = new ActionMessage("enterprise.bod.approve.success");
				}else{					
					Logger.logDebug(MODULE,"Parent is subscribing BoD , Parent is: "+subscriberProfileData.getUserName());
					Logger.logDebug(MODULE,"BoD ID is: "+bodPackageData.getBodPackageID());					
					String[] selectedSubscriberIDs = request.getParameterValues("select");
					if(selectedSubscriberIDs != null){											
						for(String subscriberID: selectedSubscriberIDs){																			
							Logger.logDebug(MODULE,"Parent is subscribing BoD for: "+subscriberID);
							bodDataManager.subscribeBodService(subscriberProfileData.getParentID(), subscriberID,bodPackageData,subscribeBodForm.getStartTime(),subscribeBodForm.getDuration(), subscribeBodForm.getDurationUnit());
						}
					}else{
						Logger.logDebug(MODULE,"Parent is subscribing BoD for: "+subscriberProfileData.getSubscriberID());
						bodDataManager.subscribeBodService(subscriberProfileData.getParentID(), subscriberProfileData.getSubscriberID(),bodPackageData,subscribeBodForm.getStartTime(),subscribeBodForm.getDuration(), subscribeBodForm.getDurationUnit());						
					}
					//bodDataManager.subscribeBodService(subscriberProfileData, bodServiceData);
					message = new ActionMessage("bod.subscribe.success");
				}
				
								
				request.setAttribute("responseUrl","/bod.do");				
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS);
			}
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