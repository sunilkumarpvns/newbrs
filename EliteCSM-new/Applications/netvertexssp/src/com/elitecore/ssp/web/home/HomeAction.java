package com.elitecore.ssp.web.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.util.constants.RequestAttributeKeyConstant;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.web.home.forms.HomeForm;
import com.elitecore.ssp.webservice.parentalcontrol.ChildAccountDataManager;
import com.elitecore.ssp.webservice.parentalcontrol.ParentalControlDataManager;

public class HomeAction extends BaseWebAction {

	private static final String MODULE = HomeAction.class.getSimpleName();
	private static final String WELCOME="welcome";

	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
		 HomeForm homeForm = (HomeForm) form;
		try{
			//get plan detail and  service detail
			ParentalControlDataManager  dataManager = new ParentalControlDataManager();			
			//ParentalWSController controller = new ParentalWSController();
			SubscriberProfile currentUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
			SubscriberProfile[] childAccounts=dataManager.getChildAccounts(currentUser);
			Logger.logInfo(MODULE, "childAccounts: " + childAccounts);
			
			Map<String,SubscriberProfile> subscriberProfileMap = new HashMap<String, SubscriberProfile>();
			Map<String,Set<SubscriberProfile>> deptWiseSubscriberProfileMap = new HashMap<String, Set<SubscriberProfile>>();
			
			//deptWiseSubscriberProfileMap.put(currentUser.getDepartment(), currentUser);
			List<SubscriberProfile> childAccountDataList = new ArrayList<SubscriberProfile>();			
			if(childAccounts != null && childAccounts.length > 0){
				Set<SubscriberProfile> noDeptSubscriberProfileSet = new HashSet<SubscriberProfile>();
				for (SubscriberProfile firstSubscriberProfileData : childAccounts) {
					if(firstSubscriberProfileData.getDepartment()==null){
						noDeptSubscriberProfileSet.add(firstSubscriberProfileData);
						continue;
					}
					if(deptWiseSubscriberProfileMap.get(firstSubscriberProfileData.getDepartment()) == null){
						Set<SubscriberProfile> subscriberProfileSet = new HashSet<SubscriberProfile>();						
						for (SubscriberProfile secondSubscriberProfileData : childAccounts) {							
							if(secondSubscriberProfileData.getDepartment()!=null){														
								if( firstSubscriberProfileData.getDepartment().equalsIgnoreCase(secondSubscriberProfileData.getDepartment())){
									subscriberProfileSet.add(secondSubscriberProfileData);
								}							
							} 
						}					
						deptWiseSubscriberProfileMap.put(firstSubscriberProfileData.getDepartment(), subscriberProfileSet);
					}				
				}
				if(noDeptSubscriberProfileSet.size()>0){
					deptWiseSubscriberProfileMap.put("Others", noDeptSubscriberProfileSet);
				}
				
				for (SubscriberProfile subscriberProfileData : childAccounts) {
					//subscriberProfileData.setSelectedPortel("");					
					subscriberProfileMap.put(subscriberProfileData.getSubscriberID(), subscriberProfileData);														
					childAccountDataList.add(subscriberProfileData);					
				}
			}
			
			if(currentUser != null){
				subscriberProfileMap.put(currentUser.getSubscriberID(),currentUser);				
			}
				
			request.getSession().setAttribute(SessionAttributeKeyConstant.SUBSCRIBER_PROFILE_MAP, subscriberProfileMap);
			request.getSession().setAttribute(SessionAttributeKeyConstant.TEMP_CHILD_OBJECT_LIST, childAccountDataList);
			request.getSession().setAttribute(SessionAttributeKeyConstant.DEPT_WISE_SUBSCRIBERS, deptWiseSubscriberProfileMap);
			
			homeForm.setChildAccounts(childAccounts);
			
			homeForm.setCurrentUser(currentUser);			
			//QuotaUsageData[] quotaUsageDatas = null;//dataManager.getQuotaUsage(currentUser);
			//homeForm.setQuotaUsageDatas(quotaUsageDatas);
			
			ChildAccountDataManager childAccountDataManager=new ChildAccountDataManager();
  			long[] allowedUsageInfo =  childAccountDataManager.getSubscriberAccountAllowedUsageInfo(currentUser);
  			
			//long[] allowedUsageInfo= null;//childAccountDataManager.getChildAccountAllowedUsageInfo(currentUser);
			request.setAttribute(RequestAttributeKeyConstant.ALLOWED_USAGE_INFO, allowedUsageInfo);
			
			request.getSession().setAttribute(SessionAttributeKeyConstant.HOME_FORM, homeForm);
			//request.setAttribute(RequestAttributeKeyConstant.QUOTA_USAGE_DATAS, quotaUsageDatas);
			
			return mapping.findForward(WELCOME);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}	
}