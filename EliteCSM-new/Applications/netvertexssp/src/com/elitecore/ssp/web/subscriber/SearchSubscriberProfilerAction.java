package com.elitecore.ssp.web.subscriber;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.elitecore.ssp.web.subscriber.forms.SubscriberProfilerForm;
import com.elitecore.ssp.webservice.subscriber.SubscriberProfilerDataManager;

public class SearchSubscriberProfilerAction extends BaseWebAction {
	private static final String MODULE = SearchSubscriberProfilerAction.class.getSimpleName();
	private static final String INITSEARCHSUBSCRIBER = "initSearchSubscriber";
	private static final String SEARCHSUBSCRIBER = "searchSubscriber";
	private static final String SEARCHALLSUBSCRIBER = "searchAllSubscriber";

	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		Logger.logInfo(MODULE, "Enter execute method of "+getClass().getName());
		try{
			SubscriberProfilerForm subscriberProfilerForm = (SubscriberProfilerForm) form;
			String[] propertyFields = (String[]) request.getSession().getAttribute(SessionAttributeKeyConstant.SUBSCRIBEPROPERTYFIELD);
			Map<String,String> searchCriteriaMap = getSearchCriteriaMap(propertyFields, request);
			
			String selectedLink = request.getParameter(SessionAttributeKeyConstant.SELECTED_LINK);
			Logger.logTrace(MODULE, " selectedLink  : "+ selectedLink );		
			request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK,selectedLink);
			
			HomeForm homeForm = (HomeForm)request.getSession().getAttribute(SessionAttributeKeyConstant.HOME_FORM);
			
			if(subscriberProfilerForm.getAction() != null && "search".equalsIgnoreCase(subscriberProfilerForm.getAction())){
				Logger.logDebug(MODULE, "Search Condition.");				
				setSubscriberProfileList(searchCriteriaMap, propertyFields ,request, subscriberProfilerForm);
				return mapping.findForward(SEARCHSUBSCRIBER);
			}else if(subscriberProfilerForm.getAction() != null && "searchall".equalsIgnoreCase(subscriberProfilerForm.getAction())){
				Logger.logDebug(MODULE, "SearchAll Condition.");
				setSubscriberProfileList(searchCriteriaMap, propertyFields ,request, subscriberProfilerForm);
				return mapping.findForward(SEARCHALLSUBSCRIBER);
			}else if(subscriberProfilerForm.getAction() != null && "addmember".equalsIgnoreCase(subscriberProfilerForm.getAction())){
				Logger.logDebug(MODULE, "Add Member Condition.");				
				Map<String,SubscriberProfile> subscriberProfileDataMap = (Map<String,SubscriberProfile>)request.getSession().getAttribute(SessionAttributeKeyConstant.SUBSCRIBER_PROFILE_MAP);
				String[] tempChildSubscriberIdentityArr = request.getParameterValues("selectall");
				List<SubscriberProfile> childAccountDataList = (List<SubscriberProfile>)request.getSession().getAttribute(SessionAttributeKeyConstant.TEMP_CHILD_OBJECT_LIST);			
				SubscriberProfile currentUser = (SubscriberProfile) request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);					
				
				if(tempChildSubscriberIdentityArr != null && tempChildSubscriberIdentityArr.length > 0){
					for(int i=0;i<tempChildSubscriberIdentityArr.length;i++){
						String tempChildId = tempChildSubscriberIdentityArr[i];
						if(subscriberProfileDataMap != null && !subscriberProfileDataMap.isEmpty()){
							for(Map.Entry<String, SubscriberProfile> subscriber : subscriberProfileDataMap.entrySet()){
								if(tempChildId.equalsIgnoreCase(subscriber.getKey())){
									if(childAccountDataList == null){
										childAccountDataList = new ArrayList<SubscriberProfile>();
										childAccountDataList.add(subscriber.getValue());
									}else if(!childAccountDataList.contains(subscriber.getValue())){								
										childAccountDataList.add(subscriber.getValue());
									}									
								}
							}
						}
					}
				}

				request.getSession().setAttribute(SessionAttributeKeyConstant.TEMP_CHILD_OBJECT_LIST, childAccountDataList);
				
				SubscriberProfile[] childAcctArray = null;
				if(childAccountDataList != null){
					Object[] childAcctObjArray = childAccountDataList.toArray();                            
					childAcctArray = Arrays.copyOf(childAcctObjArray, childAcctObjArray.length, SubscriberProfile[].class);                                                                           
				}
				
				Map<String,Set<SubscriberProfile>> deptWiseSubscriberProfileMap = new HashMap<String, Set<SubscriberProfile>>();
				Set<SubscriberProfile> noDeptSubscriberProfileSet = new HashSet<SubscriberProfile>();
				for (SubscriberProfile firstSubscriberProfileData : childAcctArray) {
					if(firstSubscriberProfileData.getDepartment()==null){
						noDeptSubscriberProfileSet.add(firstSubscriberProfileData);
						continue;
					}
					if(deptWiseSubscriberProfileMap.get(firstSubscriberProfileData.getDepartment()) == null){
						Set<SubscriberProfile> subscriberProfileSet = new HashSet<SubscriberProfile>();						
						for (SubscriberProfile secondSubscriberProfileData : childAcctArray) {							
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
				request.getSession().setAttribute(SessionAttributeKeyConstant.DEPT_WISE_SUBSCRIBERS, deptWiseSubscriberProfileMap);
				
				homeForm.setChildAccounts(childAcctArray);
				setSubscriberProfileList(searchCriteriaMap, propertyFields ,request, subscriberProfilerForm);
				return mapping.findForward(SEARCHSUBSCRIBER);
			}else if(subscriberProfilerForm.getAction() != null && "clearfilter".equals(subscriberProfilerForm.getAction())){
				Logger.logDebug(MODULE, "ClearFilter Condition.");				
				if(homeForm != null){
					homeForm.setChildAccounts(null);
					homeForm.setCurrentUser(null);
				}else{
					Logger.logDebug(MODULE, "ClearFilter: HomeForm getting null,so ChildAccounts not set to null");
				}
				request.getSession().setAttribute(SessionAttributeKeyConstant.TEMP_CHILD_OBJECT_LIST, null);
				request.getSession().setAttribute(SessionAttributeKeyConstant.TEMP_CHILD_OBJECT, null);
				subscriberProfilerForm.setPropertyFields(propertyFields);
				return mapping.findForward(INITSEARCHSUBSCRIBER);
			}else{
				subscriberProfilerForm.setPropertyFields(propertyFields);
				return mapping.findForward(INITSEARCHSUBSCRIBER);
			}
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			ActionMessage message = new ActionMessage("Subscriber Profile Search Failed.");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}

	private Map<String, String> getSearchCriteriaMap(String[] propertyFields, HttpServletRequest request ){
		Map<String, String> criteriaMap = new HashMap<String, String>();
		for(String property : propertyFields){
			String propertyVal = request.getParameter(property);
			if(propertyVal != null &&  propertyVal.trim().length() > 0  && !property.equalsIgnoreCase(propertyVal)){
				criteriaMap.put(property, propertyVal);
			}
		}
		return criteriaMap;
	}

	private void setSubscriberProfileList(Map<String,String> searchCriteriaMap, String[] propertyFields,HttpServletRequest request,SubscriberProfilerForm subscriberProfilerForm){
		try{
			SubscriberProfilerDataManager subscriberProfilerDataManager = new SubscriberProfilerDataManager();
			SubscriberProfile currentUser = (SubscriberProfile) request.getSession().getAttribute(SessionAttributeKeyConstant.TEMP_CURRENT_USER);
			SubscriberProfile[] subscriberProfileDatas = null;

			if(searchCriteriaMap != null && !searchCriteriaMap.isEmpty()){
				subscriberProfileDatas = subscriberProfilerDataManager.getChildAccountsExt(currentUser, getSearchCriteriaMap(propertyFields, request));				
			}else{
				subscriberProfileDatas = subscriberProfilerDataManager.getChildAccounts(currentUser);
			}

			List<SubscriberProfile> subscriberProfileDataList = null;
			if(subscriberProfileDatas != null && subscriberProfileDatas.length > 0){
				subscriberProfileDataList = new ArrayList<SubscriberProfile>(Arrays.asList(subscriberProfileDatas));				
			}
			
			if(subscriberProfilerForm.getAction().equalsIgnoreCase("searchall") || searchCriteriaMap.isEmpty()){
				subscriberProfileDataList.add(currentUser);
			}
			
			subscriberProfilerForm.setPropertyFields(propertyFields);
			request.setAttribute("subscriberProfileDataList", subscriberProfileDataList);
			subscriberProfilerForm.setSubscriberProfileDatas(subscriberProfileDatas);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			ActionMessage message = new ActionMessage("Subscriber Profile Search Failed.");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
	}
}