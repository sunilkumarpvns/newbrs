package com.elitecore.elitesm.web.radius.bwlist;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.bwlist.BWListBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.bwlist.data.BWListData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.bwlist.forms.SearchBWListForm;

public class SearchBWListAction extends BaseWebAction{
	private static final String MODULE = "Search BW List Action";
	private static final String SUCCESS_FORWARD = "success";
	private static final String LIST_FORWARD = "searchBlackList";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_BLACKLIST_CANDIDATES_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
            
                Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
                if(checkAccess(request, ACTION_ALIAS)){
				try{
					SearchBWListForm searchBWListForm = (SearchBWListForm)actionForm;
					BWListBLManager blManager = new BWListBLManager();
	
					int requiredPageNo;
		    		if(request.getParameter("pageNo") != null){
		    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
		    		}else{
		    			requiredPageNo = new Long(searchBWListForm.getPageNumber()).intValue();
		    		}
		    		if(requiredPageNo == 0)
		    			requiredPageNo =1;
					
		    		/*if(request.getParameter("pageNoType") != null){
		    			System.out.println("page No Type :" +request.getParameter("pageNoType"));
		    			searchBWListForm.setPageNumberForType(Long.parseLong(request.getParameter("pageNoType")));
		    			if(request.getParameter("pageNo") == null && requiredPageNo == 1){
		    				requiredPageNo=Integer.parseInt(String.valueOf(request.getParameter("pageNoType")));
		    			}
		    		}*/
		    		
		    		
		    		
					BWListData bwListData = new BWListData();
					String strAttribute = searchBWListForm.getAttribute();
					String strAttributeValue=searchBWListForm.getAttributeValue();
					
				   
					
					if(strAttribute !=null){
						 boolean containsWhitespace = strAttribute.indexOf(" ")!=-1;
						 if(containsWhitespace ==  true){
							bwListData.setAttributeId(strAttribute.trim()); 
						 }else{
							bwListData.setAttributeId("%"+strAttribute+"%"); 
						 }
					}else{
						bwListData.setAttributeId("");
					}

					if(strAttributeValue !=null){
						 boolean containsWhitespace = strAttributeValue.indexOf(" ")!=-1;
						 if(containsWhitespace ==  true){
							bwListData.setAttributeValue(strAttributeValue.trim()); 
						 }else{
							bwListData.setAttributeValue("%"+strAttributeValue+"%"); 
						 }
					}else{
						bwListData.setAttributeValue("");
					}

					
					if(request.getParameter("resultStatus") != null){
						searchBWListForm.setStatus(request.getParameter("resultStatus"));
					}

					/*if(request.getParameter("typeName") != null){
						bwListData.setTypeName(request.getParameter("typeName"));
					}
					
					if(request.getParameter("validity") !=null){
		    			bwListData.setStrValidity(request.getParameter("validity"));
		    		}*/
					
					if(searchBWListForm.getStatus() != null){
						if(searchBWListForm.getStatus().equalsIgnoreCase("Active"))
							bwListData.setCommonStatusId("CST01");
						else if(searchBWListForm.getStatus().equalsIgnoreCase("InActive"))
							bwListData.setCommonStatusId("CST02");
					}
					
					
					
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				
					PageList pageList = blManager.search(bwListData,requiredPageNo,pageSize);
					doAuditing(staffData, actionAlias);
					
					searchBWListForm.setAttribute(strAttribute);
					searchBWListForm.setPageNumber(pageList.getCurrentPage());
					searchBWListForm.setTotalPages(pageList.getTotalPages());
					searchBWListForm.setTotalRecords(pageList.getTotalItems());
					searchBWListForm.setBwList(pageList.getListData());
					searchBWListForm.setAction(BaseConstant.LISTACTION);
					
					return mapping.findForward(LIST_FORWARD);
				}catch(Exception managerExp){
					
					Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
                    Logger.logTrace(MODULE,managerExp);
        			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
        			request.setAttribute("errorDetails", errorElements);
                    ActionMessage message = new ActionMessage("bwlist.search.failure");
                    ActionMessages messages = new ActionMessages();
                    messages.add("information", message);
                    saveErrors(request, messages);
   				}
		        return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logWarn(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
