package com.elitecore.elitesm.web.diameter.routingconfig;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.routingconfig.forms.SearchDiameterRoutingConfForm;

public class SearchDiameterRoutingConfAction extends BaseWebAction { 
	                                                                       
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-ROUTING-CONF";
	private static final String LIST_FORWARD = "searchDiameterRoutingConfList";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DIAMETER_ROUTING_TABLE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			SearchDiameterRoutingConfForm searchDiameterRoutingConfForm = (SearchDiameterRoutingConfForm)form;
			DiameterRoutingConfBLManager diameterRoutingConfBLManager = new DiameterRoutingConfBLManager();
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			
			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(searchDiameterRoutingConfForm.getPageNumber()).intValue();
			}
			if(requiredPageNo == 0)
				requiredPageNo =1;

			DiameterRoutingConfData diameterRoutingConfData = new DiameterRoutingConfData();
			String strSearchBy = searchDiameterRoutingConfForm.getSearchBy();
			String strSearchValue = searchDiameterRoutingConfForm.getSearchValue();
			if(strSearchValue==null)
				strSearchValue="";
			
			if(strSearchBy != null && (!strSearchBy.equalsIgnoreCase("0")) && strSearchValue!=null && (!strSearchValue.trim().equalsIgnoreCase("")))
			{	
			    if(strSearchBy.equalsIgnoreCase("TableName"))
			    { 
			    	DiameterRoutingTableData diameterRoutingTableData= new DiameterRoutingTableData();
			    	diameterRoutingTableData.setRoutingTableName(strSearchValue);
			    	diameterRoutingConfData.setDiameterRoutingTableData(diameterRoutingTableData);
			    } 
			    else if(strSearchBy.equalsIgnoreCase("RoutingName"))
			    {
			    	diameterRoutingConfData.setName(strSearchValue);
			    }
			    else if(strSearchBy.equalsIgnoreCase("DestinationRealm"))
			    {
			    	diameterRoutingConfData.setRealmName(strSearchValue);
			    }
			    else if(strSearchBy.equalsIgnoreCase("OriginHost"))
			    {
			    	diameterRoutingConfData.setOriginHost(strSearchValue);
			    }
			    else if(strSearchBy.equalsIgnoreCase("OriginRealm"))
			    {
			    	diameterRoutingConfData.setOriginRealm(strSearchValue);
			    }
			    else if(strSearchBy.equalsIgnoreCase("ApplicationIds"))
			    {
			    	diameterRoutingConfData.setAppIds(strSearchValue);
			    }
			}	
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		    Map infoMap= new HashedMap();
		    infoMap.put("pageNo", requiredPageNo);
		    infoMap.put("pageSize", pageSize);
		    
			PageList pageList = diameterRoutingConfBLManager.search(diameterRoutingConfData, staffData, infoMap);
			doAuditing(staffData, ACTION_ALIAS);
			
			searchDiameterRoutingConfForm.setSearchBy(strSearchBy);
			searchDiameterRoutingConfForm.setSearchValue(strSearchValue.trim());
			searchDiameterRoutingConfForm.setPageNumber(pageList.getCurrentPage());
			searchDiameterRoutingConfForm.setTotalPages(pageList.getTotalPages());
			searchDiameterRoutingConfForm.setTotalRecords(pageList.getTotalItems());
			searchDiameterRoutingConfForm.setListDiameterRoutingConf(pageList.getListData());
			searchDiameterRoutingConfForm.setAction(BaseConstant.LISTACTION);
			request.setAttribute("searchDiameterRoutingConfForm", searchDiameterRoutingConfForm);
			return mapping.findForward(LIST_FORWARD);

		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD); 
		}catch(Exception managerExp){
			managerExp.printStackTrace();
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("diameter.routingconf.search.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);          
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
}