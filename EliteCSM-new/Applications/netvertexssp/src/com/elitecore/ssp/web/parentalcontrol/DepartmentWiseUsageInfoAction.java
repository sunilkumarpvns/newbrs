package com.elitecore.ssp.web.parentalcontrol;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.UsageMonotoringInformation;
import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.util.constants.RequestAttributeKeyConstant;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.webservice.parentalcontrol.ChildAccountDataManager;

public class DepartmentWiseUsageInfoAction extends BaseWebAction {
	private static final String MODULE = DepartmentWiseUsageInfoAction.class.getSimpleName();
	private static final String SUCCESS ="deptWiseUsageInfoSuccess";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {							
		Logger.logTrace(MODULE, " entered into execute method of : "+ getClass().getName());
		String departmentName = request.getParameter("departmentName");
		
		System.out.println("departmentName:--> "+departmentName);
		ChildAccountDataManager dataManager  =  new ChildAccountDataManager();
		
		Map<String,Set<SubscriberProfile>> deptWiseSubscriberProfileMap = (Map<String,Set<SubscriberProfile>>)request.getSession().getAttribute(SessionAttributeKeyConstant.DEPT_WISE_SUBSCRIBERS);
		Set<SubscriberProfile> subscriberSet = deptWiseSubscriberProfileMap.get(departmentName);
		request.setAttribute(RequestAttributeKeyConstant.DEPARTMENT_TOTAL_SUBSCRIBER, (subscriberSet.size()+1));
		SubscriberProfile loggedInUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
		System.out.println("loggedInUser--->"+loggedInUser.getUserName());
		try {
			long totalUsage = 0L;
			for(SubscriberProfile subscriberProfile: subscriberSet){		
				long[] usageInforArray = dataManager.getSubscriberAccountAllowedUsageInfo(subscriberProfile);
				totalUsage += usageInforArray[5];
				System.out.println("username : "+subscriberProfile.getUserName()+"-- total usage : "+usageInforArray[5]);
			}
			long[] loggedInUserUsageInfoArray  = dataManager.getSubscriberAccountAllowedUsageInfo(loggedInUser);
			totalUsage += loggedInUserUsageInfoArray[5];
			 request.setAttribute(RequestAttributeKeyConstant.DEPARTMENT_TOTAL_USAGE, totalUsage);	
		} catch (RemoteException e) {
			e.printStackTrace();
		}
  		return mapping.findForward(SUCCESS);
	}
 }
