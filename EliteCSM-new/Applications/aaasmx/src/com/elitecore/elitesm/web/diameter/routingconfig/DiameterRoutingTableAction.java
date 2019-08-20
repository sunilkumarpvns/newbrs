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

import com.elitecore.diameterapi.core.stack.constant.OverloadAction;
import com.elitecore.elitesm.blmanager.core.base.GenericBLManager;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.DiameterConstant;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.routingconfig.forms.DiameterRoutingTableForm;
import com.elitecore.elitesm.web.diameter.routingconfig.forms.SearchDiameterRoutingConfForm;

public class DiameterRoutingTableAction extends BaseWebAction {
	private static final String MODULE = "DIAMETER-ROUTING-TABLE";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DIAMETER_ROUTING_TABLE;
	private static final String INITSEARCHROUTINGCONF = "searchDiameterRoutingConfig"; 
	private static final String LIST_FORWARD = "searchDiameterRoutingConfList";
	private static final String LIST_ENTRIES=ConfigConstant.LIST_DIAMETER_ROUTING_ENTRIES;
	private static final String SEARCH_ACTION_ALIAS=ConfigConstant.SEARCH_DIAMETER_ROUTING_TABLE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		DiameterRoutingTableForm diameterRoutingTableForm = (DiameterRoutingTableForm)form;
		try{
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			DiameterRoutingConfBLManager diameterRoutingConfBLManager = new DiameterRoutingConfBLManager();
			DiameterRoutingTableData diameterRoutingTableData = new DiameterRoutingTableData();
			if(diameterRoutingTableForm.getActionType() != null) {
				if(diameterRoutingTableForm.getActionType().equalsIgnoreCase("create")){
					diameterRoutingTableData.setRoutingTableName(diameterRoutingTableForm.getRoutingTableName());
					diameterRoutingTableData.setOverloadAction(diameterRoutingTableForm.getOverloadAction());
					
					if(diameterRoutingTableForm.getOverloadAction().equals(OverloadAction.REJECT.val)){
						diameterRoutingTableData.setResultCode(diameterRoutingTableForm.getResultCode());
					}else{
						diameterRoutingTableData.setResultCode(0);
					}
					
					diameterRoutingTableData.setRoutingScript(diameterRoutingTableForm.getRoutingScript());
					
					diameterRoutingConfBLManager.createRoutingTable(diameterRoutingTableData, staffData);
					
					request.setAttribute("responseUrl", "/initSearchDiameterRoutingConfig");
					ActionMessage message = new ActionMessage("diameter.routingtable.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);
				}
				else if(diameterRoutingTableForm.getActionType().equalsIgnoreCase("pagination")) 
				{
					int requiredPageNo;
					if(request.getParameter("pageNo") != null){
						requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
					}else{
						requiredPageNo = 1;
					}
					if(requiredPageNo == 0)
						requiredPageNo =1;
					Map infoMap= new HashedMap();
				    infoMap.put("pageNo", requiredPageNo);
				    infoMap.put("pageSize", pageSize);
				    infoMap.put("actionAlias", ACTION_ALIAS);
				    infoMap.put("staffData", staffData);
				    
				    SearchDiameterRoutingConfForm searchDiameterRoutingConfForm = new SearchDiameterRoutingConfForm();
				 	PageList pageList = diameterRoutingConfBLManager.searchRoutingTable(staffData, infoMap); 
				 	searchDiameterRoutingConfForm.setPageNumber(pageList.getCurrentPage());
					searchDiameterRoutingConfForm.setTotalPages(pageList.getTotalPages());
					searchDiameterRoutingConfForm.setTotalRecords(pageList.getTotalItems());
					searchDiameterRoutingConfForm.setListDiameterRoutingTable(pageList.getListData());
					searchDiameterRoutingConfForm.setAction(DiameterConstant.LISTACTION);
					
					GenericBLManager genericBLManager=new GenericBLManager();
					pageList = genericBLManager.getAllRecords(DiameterRoutingConfData.class,"name",true);
					searchDiameterRoutingConfForm.setListDiameterRoutingConf(pageList.getListData());
					request.setAttribute("searchDiameterRoutingConfForm", searchDiameterRoutingConfForm);
					return mapping.findForward(INITSEARCHROUTINGCONF);    
				}
				else if(diameterRoutingTableForm.getActionType().equalsIgnoreCase("tablewiserouting")) 
				{
					SearchDiameterRoutingConfForm searchDiameterRoutingConfForm = new SearchDiameterRoutingConfForm();
					ScriptBLManager scriptBLManager = new ScriptBLManager();
					
					DiameterRoutingTableData diameterRoutingTable = diameterRoutingConfBLManager.getDiameterRoutingTableData(diameterRoutingTableForm.getRoutingTableId());
					searchDiameterRoutingConfForm.setRoutingTableName(diameterRoutingTable.getRoutingTableName());
					searchDiameterRoutingConfForm.setOverloadAction(diameterRoutingTable.getOverloadAction());
					
					if(diameterRoutingTable.getResultCode() != null){
						searchDiameterRoutingConfForm.setResultCode(diameterRoutingTable.getResultCode());
					}else{
						searchDiameterRoutingConfForm.setResultCode(0);
					}
					searchDiameterRoutingConfForm.setRoutingScript(diameterRoutingTable.getRoutingScript());
					PageList pageList = diameterRoutingConfBLManager.searchRoutingFromRoutingTable(diameterRoutingTableForm.getRoutingTableId(), staffData);
					searchDiameterRoutingConfForm.setPageNumber(pageList.getCurrentPage());
					searchDiameterRoutingConfForm.setTotalPages(pageList.getTotalPages());
					searchDiameterRoutingConfForm.setTotalRecords(pageList.getTotalItems());
					searchDiameterRoutingConfForm.setListDiameterRoutingConf(pageList.getListData());
					searchDiameterRoutingConfForm.setAction(BaseConstant.LISTACTION);
					
					List<ScriptInstanceData> scriptDataList = scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.DIAMETER_ROUTER_SCRIPT);
					 
					searchDiameterRoutingConfForm.setScriptDataList(scriptDataList);
					request.setAttribute("searchDiameterRoutingConfForm", searchDiameterRoutingConfForm);
					request.setAttribute("routingTableId",diameterRoutingTableForm.getRoutingTableId());
					return mapping.findForward(LIST_FORWARD);
				}
			}	   
			
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message=null;
			if(diameterRoutingTableForm.getActionType() != null && diameterRoutingTableForm.getActionType().equalsIgnoreCase("create")) {
			   message = new ActionMessage("diameter.routingtable.failure");
			}
			else
			{
				
			}	
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}

}