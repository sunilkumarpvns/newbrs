package com.elitecore.elitesm.web.rm.ippool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.rm.ippool.IPPoolBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.IPPoolConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.rm.ippool.forms.IPPoolForm;

public class SearchIPPoolAction extends BaseWebAction {

	private static final String LIST_FORWARD = "searchIPPool";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_IP_POOL_ACTION;

	@Override
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter Execute method of "+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){	
			try{
				IPPoolForm searchIPPoolForm = (IPPoolForm)actionForm;
				IPPoolBLManager blManager = new IPPoolBLManager();
				
				int requiredPageNo = Integer.parseInt(String.valueOf(searchIPPoolForm.getPageNumber()));
				if(requiredPageNo == 0)
					requiredPageNo = 1;
				
				IIPPoolData ipPoolSearchData = new IPPoolData();
				
				String strName = searchIPPoolForm.getName();
				if(strName !=null)
					ipPoolSearchData.setName("%"+strName+"%");
				else
					ipPoolSearchData.setName("");
	
				ipPoolSearchData.setNasIPAddress(searchIPPoolForm.getNasIPAddress());
				if(searchIPPoolForm.getStatus() != null){
					if(searchIPPoolForm.getStatus().equalsIgnoreCase("Show"))
						ipPoolSearchData.setCommonStatusId("CST01");
		    		else if(searchIPPoolForm.getStatus().equalsIgnoreCase("Hide"))
		    			ipPoolSearchData.setCommonStatusId("CST02");
				}
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				PageList pageList = blManager.search(ipPoolSearchData,requiredPageNo,pageSize,staffData);
				
				searchIPPoolForm.setName(strName);
				searchIPPoolForm.setPageNumber(pageList.getCurrentPage());
				searchIPPoolForm.setTotalPages(pageList.getTotalPages());
				searchIPPoolForm.setTotalRecords(pageList.getTotalItems());
				searchIPPoolForm.setIpPoolList(pageList.getListData());
				searchIPPoolForm.setAction(IPPoolConstant.LISTACTION);
				
				return mapping.findForward(LIST_FORWARD);
			}catch(Exception managerExp){
				managerExp.printStackTrace();
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);

			}
			return mapping.findForward(FAILURE_FORWARD);
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	} 
}
