package com.elitecore.elitesm.web.diameter.routingconfig;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.core.base.GenericBLManager;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.DiameterConstant;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.routingconfig.forms.SearchDiameterRoutingConfForm;

public class InitSearchDiameterRoutingConfAction extends BaseWebAction { 

	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-ROUTING-CONF";
	private static final String INITSEARCHROUTINGCONF = "searchDiameterRoutingConfig"; 
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DIAMETER_ROUTING_TABLE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			SearchDiameterRoutingConfForm searchDiameterRoutingConfForm = (SearchDiameterRoutingConfForm)form; 	
			DiameterRoutingConfBLManager diameterRoutingConfBLManager = new DiameterRoutingConfBLManager();
			ScriptBLManager scriptBLManager = new ScriptBLManager();
			
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			
			int requiredPageNo=1;
			DiameterRoutingConfData diameterRoutingConfData = new DiameterRoutingConfData();
			String strRoutingConfName = searchDiameterRoutingConfForm.getRoutingConfName();
			
			if(strRoutingConfName!=null)
				diameterRoutingConfData.setName(strRoutingConfName);
			else
				diameterRoutingConfData.setName("");
		
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			Map infoMap= new HashedMap();
		    infoMap.put("pageNo", requiredPageNo);
		    infoMap.put("pageSize", pageSize);
		    infoMap.put("actionAlias", ACTION_ALIAS);
		    infoMap.put("staffData", staffData);
		    
		 	PageList pageList = diameterRoutingConfBLManager.searchRoutingTable(staffData, infoMap); 
		 	doAuditing(staffData, ACTION_ALIAS);
		 	
		 	List<ScriptInstanceData> scriptDataList = scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.DIAMETER_ROUTER_SCRIPT);
		 	
			searchDiameterRoutingConfForm.setPageNumber(pageList.getCurrentPage());
			searchDiameterRoutingConfForm.setTotalPages(pageList.getTotalPages());
			searchDiameterRoutingConfForm.setTotalRecords(pageList.getTotalItems());
			searchDiameterRoutingConfForm.setListDiameterRoutingTable(pageList.getListData());
			searchDiameterRoutingConfForm.setAction(DiameterConstant.LISTACTION);
			searchDiameterRoutingConfForm.setScriptDataList(scriptDataList);
			
			GenericBLManager genericBLManager=new GenericBLManager();
			pageList = genericBLManager.getAllRecords(DiameterRoutingConfData.class,"name",true);
			searchDiameterRoutingConfForm.setListDiameterRoutingConf(pageList.getListData());
			
			request.setAttribute("searchDiameterRoutingConfForm", searchDiameterRoutingConfForm);
			return mapping.findForward(INITSEARCHROUTINGCONF);             
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
			ActionMessage message = new ActionMessage("diameter.peerprofile.search.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}  
		
		return mapping.findForward(FAILURE_FORWARD); 
	}
}