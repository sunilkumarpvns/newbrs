package com.elitecore.ssp.web.parentalcontrol;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.UsageMonotoringInformation;
import com.elitecore.ssp.util.constants.RequestAttributeKeyConstant;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.web.parentalcontrol.forms.ChildAccountUsageInfoForm;
import com.elitecore.ssp.webservice.parentalcontrol.ChildAccountDataManager;

public class ChildAccountUsageInfoAction extends BaseWebAction {
	private static final String VIEW_USAGE_PAGE="childAccountUsageInfoSuccess";
	private static final String VIEW_ENTERPRISE_MEMBER_USAGE_PAGE="enterpriseMemberAccountUsageInfoSuccess";
	private static final String MODULE = ChildAccountUsageInfoAction.class.getSimpleName();
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {				
		Logger.logTrace(MODULE, "Entered in execute() method");
		Map<String,ServiceUsageInfo> serviceUsageInfoDataList=null;
		ServiceUsageInfo serviceUsageInfo = null;
		ChildAccountUsageInfoForm childAccountUsageInfoForm = (ChildAccountUsageInfoForm)form;
		
		ChildAccountDataManager childAccountDataManager = new ChildAccountDataManager();				
		String[] subscriberIdentityArray = request.getParameterValues("select");
		request.setAttribute(RequestAttributeKeyConstant.SELECTED_SUBSCRIBER, subscriberIdentityArray);
		String subscriberIdentity= request.getParameter("subscriberIdentity");
		String subscriberName= request.getParameter("subscriberName");
		String selectedLink = request.getParameter(SessionAttributeKeyConstant.SELECTED_LINK);
		Logger.logTrace(MODULE, " selectedLink  : "+ selectedLink );		
		request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK,selectedLink);
		Map<String,SubscriberProfile> SubscriberProfileMap = (Map<String,SubscriberProfile>)request.getSession().getAttribute(SessionAttributeKeyConstant.SUBSCRIBER_PROFILE_MAP);
		
		HttpSession session=request.getSession();
		if(subscriberIdentity!=null){	
			session.setAttribute("subscriberIdentity", subscriberIdentity);
			session.setAttribute("subscriberName", subscriberName);
		}
		subscriberIdentity=(String)session.getAttribute("subscriberIdentity");
		
		SubscriberProfile childObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
		subscriberIdentity=childObj.getSubscriberID();		
		Logger.logTrace(MODULE, " subscriberIdentity : "+ subscriberIdentity);
		
		try{
			if(subscriberIdentity!=null && (subscriberIdentityArray == null || subscriberIdentityArray.length <= 1)){
				
				if(subscriberIdentityArray != null && subscriberIdentityArray.length == 1){
					subscriberIdentity = subscriberIdentityArray[0];
				}
				
				UsageMonotoringInformation[] usageMeteringInfos = childAccountDataManager.getChildAccountUsageMeteringInfo(subscriberIdentity,childObj.getSubscriberPackage());
								
				serviceUsageInfoDataList = new LinkedHashMap<String, ServiceUsageInfo>();
				if(usageMeteringInfos!=null && usageMeteringInfos.length>0){
					for(UsageMonotoringInformation obj:usageMeteringInfos){
						String meteringLevel=obj.getMeteringLevel();
						String monitoringKey = obj.getMonitoringKey();
												
						if(meteringLevel!=null && meteringLevel.equalsIgnoreCase("PACKAGE")){
							String aggregateKey=obj.getAggregateKey();										
							if(aggregateKey!=null && aggregateKey.contains("-")){
								// FOR POLICY GROUP LEVEL
								if(monitoringKey.equalsIgnoreCase(childObj.getSubscriberPackage())){
 									int index = aggregateKey.lastIndexOf('-');
									String key = aggregateKey.substring(index+1);
									
 									if(key.equalsIgnoreCase("DAILY")){			
										childAccountUsageInfoForm.setLastDayTotalOctets(obj.getTotalOctets());
										childAccountUsageInfoForm.setLastDayUsageTime(obj.getUsageTime());
									 
									}
									if(key.equalsIgnoreCase("WEEKLY")){																	
										childAccountUsageInfoForm.setLastWeekTotalOctets(obj.getTotalOctets());
										childAccountUsageInfoForm.setLastWeekUsageTime(obj.getUsageTime());
									 
									}
									if(key.equalsIgnoreCase("MONTHLY")){									
										childAccountUsageInfoForm.setLastMonthTotalOctets(obj.getTotalOctets());
										childAccountUsageInfoForm.setLastMonthUsageTime(obj.getUsageTime());
									 
									}
								}
								else {
									 
									// FOR ADDONs LEVEL
									int index = aggregateKey.lastIndexOf('-');
									int indexN = aggregateKey.indexOf('-');
									
									String serviceName = aggregateKey.substring(0, indexN);
									String key = aggregateKey.substring(index+1);
									 
									if(serviceUsageInfo == null){										
										serviceUsageInfo = new ServiceUsageInfo();									
									}else if(!serviceName.equals(serviceUsageInfo.getServiceName())){
										serviceUsageInfo = new ServiceUsageInfo();
									}
									
									if(key.equalsIgnoreCase("DAILY")){			
										serviceUsageInfo.setLastDayTotalOctets(obj.getTotalOctets());									 
										serviceUsageInfo.setServiceName(serviceName);
										serviceUsageInfo.setLastDayUsageTime(obj.getUsageTime());
										serviceUsageInfoDataList.put(serviceName, serviceUsageInfo);
									}
									if(key.equalsIgnoreCase("WEEKLY")){																	
										serviceUsageInfo.setLastWeekTotalOctets(obj.getTotalOctets());									 
										serviceUsageInfo.setServiceName(serviceName);
										serviceUsageInfo.setLastWeekUsageTime(obj.getUsageTime());
										serviceUsageInfoDataList.put(serviceName, serviceUsageInfo);
									}
									if(key.equalsIgnoreCase("MONTHLY")){									
										serviceUsageInfo.setLastMonthTotalOctets(obj.getTotalOctets());										 
										serviceUsageInfo.setServiceName(serviceName);
										serviceUsageInfo.setLastMonthUsageTime(obj.getUsageTime());
										serviceUsageInfoDataList.put(serviceName, serviceUsageInfo);
									}									
								}
							}
						}
						
						/*else	if(meteringLevel!=null && meteringLevel.equalsIgnoreCase("SERVICE")){
							String aggregateKey=obj.getAggregateKey();										
							if(aggregateKey!=null && aggregateKey.contains("-")){
								int index = aggregateKey.lastIndexOf('-');
								String serviceName = aggregateKey.substring(0, index);
								String key = aggregateKey.substring(index+1);
								Logger.logDebug(MODULE, "Service Name: " + serviceName);
								Logger.logDebug(MODULE, "Key: " + key);
								serviceUsageInfo = serviceUsageInfoDataList.get(serviceName);
								if(serviceUsageInfo == null){
									serviceUsageInfo = new ServiceUsageInfo();									
								}
								if(key.equalsIgnoreCase("DAILY")){									
									serviceUsageInfo.setLastDayTotalOctets(obj.getTotalOctets());
									serviceUsageInfo.setServiceName(serviceName);
									serviceUsageInfo.setLastDayUsageTime(obj.getUsageTime());
									serviceUsageInfoDataList.put(serviceName, serviceUsageInfo);
								}else if(key.equalsIgnoreCase("WEEKLY")){
									serviceUsageInfo.setLastWeekTotalOctets(obj.getTotalOctets());
									serviceUsageInfo.setServiceName(serviceName);
									serviceUsageInfo.setLastWeekUsageTime(obj.getUsageTime());
									serviceUsageInfoDataList.put(serviceName, serviceUsageInfo);
								}else if(key.equalsIgnoreCase("MONTHLY")){
									serviceUsageInfo.setLastMonthTotalOctets(obj.getTotalOctets());
									serviceUsageInfo.setServiceName(serviceName);
									serviceUsageInfo.setLastMonthUsageTime(obj.getUsageTime());
									serviceUsageInfoDataList.put(serviceName, serviceUsageInfo);
								}								
							}
						}else if(meteringLevel!=null && meteringLevel.equalsIgnoreCase("SUBSCRIBER")){
							if(obj.getAggregateKey().equalsIgnoreCase("DAILY")){								
								Logger.logDebug(MODULE, "key: " + obj.getAggregateKey());
								childAccountUsageInfoForm.setLastDayTotalOctets(obj.getTotalOctets());
								childAccountUsageInfoForm.setLastDayUsageTime(obj.getUsageTime());
							}else if(obj.getAggregateKey().equalsIgnoreCase("WEEKLY")){
								Logger.logDebug(MODULE, "key: " + obj.getAggregateKey());
								childAccountUsageInfoForm.setLastWeekTotalOctets(obj.getTotalOctets());
								childAccountUsageInfoForm.setLastWeekUsageTime(obj.getUsageTime());
							}else if(obj.getAggregateKey().equalsIgnoreCase("MONTHLY")){
								Logger.logDebug(MODULE, "key: " + obj.getAggregateKey());
								childAccountUsageInfoForm.setLastMonthTotalOctets(obj.getTotalOctets());
								childAccountUsageInfoForm.setLastMonthUsageTime(obj.getUsageTime());
							}
						}*/
					}					
				}
				childAccountUsageInfoForm.setServiceUsageInfoDataList(new ArrayList(serviceUsageInfoDataList.values()));
			}else if(subscriberIdentityArray != null && subscriberIdentityArray.length > 1){
				List<EnterpriseMemberAccountUsageInfo> enterpriseMemberAccountUsageInfoDatalist = new ArrayList<EnterpriseMemberAccountUsageInfo>();
				
				for (int i = 0; i < subscriberIdentityArray.length; i++) {					
					String subscriberID = subscriberIdentityArray[i];
					EnterpriseMemberAccountUsageInfo enterpriseMemberAccountUsageInfo = new EnterpriseMemberAccountUsageInfo();
					UsageMonotoringInformation[] usageMeteringInfos=childAccountDataManager.getChildAccountUsageMeteringInfo(subscriberID,childObj.getSubscriberPackage());
					 										
					if(usageMeteringInfos != null && usageMeteringInfos.length > 0){						
						for(UsageMonotoringInformation obj:usageMeteringInfos){
							String meteringLevel=obj.getMeteringLevel();
							if(meteringLevel!=null && meteringLevel.equalsIgnoreCase("SUBSCRIBER")){															 
								if(obj.getAggregateKey().equalsIgnoreCase("DAILY")){								
									Logger.logDebug(MODULE, "key: " + obj.getAggregateKey());
									enterpriseMemberAccountUsageInfo.setLastDayTotalOctets(obj.getTotalOctets());
									enterpriseMemberAccountUsageInfo.setLastDayUsageTime(obj.getUsageTime());								
								}else if(obj.getAggregateKey().equalsIgnoreCase("WEEKLY")){
									Logger.logDebug(MODULE, "key: " + obj.getAggregateKey());
									enterpriseMemberAccountUsageInfo.setLastWeekTotalOctets(obj.getTotalOctets());								
									enterpriseMemberAccountUsageInfo.setLastWeekUsageTime(obj.getUsageTime());																
								}else if(obj.getAggregateKey().equalsIgnoreCase("MONTHLY")){
									Logger.logDebug(MODULE, "key: " + obj.getAggregateKey());								
									enterpriseMemberAccountUsageInfo.setLastMonthTotalOctets(obj.getTotalOctets());
									enterpriseMemberAccountUsageInfo.setLastMonthUsageTime(obj.getUsageTime());																
								}																						
							}
						}												
					}
					if(SubscriberProfileMap != null){
						for(Map.Entry<String, SubscriberProfile> subscriber : SubscriberProfileMap.entrySet()){
							if(subscriber.getKey().equalsIgnoreCase(subscriberID)){
								enterpriseMemberAccountUsageInfo.setSubscriberProfile(subscriber.getValue());
							}
						}
					}
					enterpriseMemberAccountUsageInfoDatalist.add(enterpriseMemberAccountUsageInfo);
				}
				childAccountUsageInfoForm.setEnterpriseMemberAccountUsageInfoDataList(enterpriseMemberAccountUsageInfoDatalist);
				request.setAttribute(RequestAttributeKeyConstant.CHILD_ACCOUNT_USAGE_INFO_FORM,childAccountUsageInfoForm);
				return mapping.findForward(VIEW_ENTERPRISE_MEMBER_USAGE_PAGE);
			}else{			
				childAccountUsageInfoForm.setServiceUsageInfoDataList(null);
			}
			Logger.logDebug(MODULE, childAccountUsageInfoForm.getServiceUsageInfoDataList());
			request.setAttribute(RequestAttributeKeyConstant.CHILD_ACCOUNT_USAGE_INFO_FORM,childAccountUsageInfoForm);
			return mapping.findForward(VIEW_USAGE_PAGE);			
		}catch(Exception e){
			e.printStackTrace();
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
			return mapping.findForward(FAILURE);
	}
}