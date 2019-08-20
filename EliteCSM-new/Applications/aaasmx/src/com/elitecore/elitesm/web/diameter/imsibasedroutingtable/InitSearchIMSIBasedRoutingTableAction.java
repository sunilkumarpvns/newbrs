package com.elitecore.elitesm.web.diameter.imsibasedroutingtable;

import java.util.ArrayList;
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

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.imsibasedroutingtable.IMSIBasedRoutingTableBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.imsibasedroutingtable.form.IMSIBasedRoutingTableForm;

public class InitSearchIMSIBasedRoutingTableAction extends BaseWebAction { 

	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-ROUTING-CONF";
	private static final String SEARCH_IMSI_ROUTINGTABLE = "searchIMSIRoutingTable"; 
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_IMSI_BASED_ROUTING_TABLE;
	private static final String DELETE_ALIAS = ConfigConstant.DELETE_IMSI_BASED_ROUTING_TABLE;
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			
			IMSIBasedRoutingTableForm imsiBasedRoutingTableForm = (IMSIBasedRoutingTableForm)form;
			IMSIBasedRoutingTableBLManager blManager = new IMSIBasedRoutingTableBLManager();
			
			String requestAction = request.getParameter("action");
			
			if( requestAction != null && requestAction.equals("deleteRoutingTable")){
				try{
					checkActionPermission(request, DELETE_ALIAS);
					String[] strSelectedIds = request.getParameterValues("select");
					 List<String> listSelectedIDs = new ArrayList<String>();
					  if(strSelectedIds != null){
						 for(int i=0;i<strSelectedIds.length;i++){
							listSelectedIDs.add(strSelectedIds[i]);
						 }
					 } 
					blManager.deleteRoutingTable(listSelectedIDs);
					request.setAttribute("responseUrl", "/initSearchIMSIBasedRoutingTable");
					ActionMessage message = new ActionMessage("imsibased.routingtable.delete.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);
				}catch(ActionNotPermitedException e){
						Logger.logError(MODULE,"Error :-" + e.getMessage());
						printPermitedActionAlias(request);
						ActionMessages messages = new ActionMessages();
						messages.add("information", new ActionMessage("general.user.restricted"));
						saveErrors(request, messages);
						return mapping.findForward(INVALID_ACCESS_FORWARD);
				}catch(Exception exp){
					Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
					ActionMessage message = new ActionMessage("imsibased.routingtable.delete.failure");                                                         
					ActionMessages messages = new ActionMessages();                                                                                 
					messages.add("information", message);                                                                                           
					saveErrors(request, messages); 
					return mapping.findForward(FAILURE_FORWARD); 
				}
			}else{
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				
				int requiredPageNo;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(imsiBasedRoutingTableForm.getPageNumber()).intValue();
				}
				if(requiredPageNo == 0)
					requiredPageNo =1;
				
				
				IMSIBasedRoutingTableData imsiBasedRoutingTable = new  IMSIBasedRoutingTableData();
				String strRoutingTableName = imsiBasedRoutingTableForm.getImsiBasedRoutingTableName();
				if(strRoutingTableName!=null)
					imsiBasedRoutingTable.setRoutingTableName(strRoutingTableName);
				else
					imsiBasedRoutingTable.setRoutingTableName("");
				
				String routingTableId = imsiBasedRoutingTableForm.getRoutingTableId();
				if(Strings.isNullOrBlank(routingTableId) == false)
					imsiBasedRoutingTable.setRoutingTableId(routingTableId);
					
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				Map infoMap= new HashedMap();
			    infoMap.put("pageNo", requiredPageNo);
			    infoMap.put("pageSize", pageSize);
			    
			   /* PageList pageList = blManager.searchImsiBasedRoutingTable(imsiBasedRoutingTable,infoMap); 
			    doAuditing(staffData, ACTION_ALIAS);
				imsiBasedRoutingTableForm.setPageNumber(pageList.getCurrentPage());
				imsiBasedRoutingTableForm.setTotalPages(pageList.getTotalPages());
				imsiBasedRoutingTableForm.setTotalRecords(pageList.getTotalItems());
				imsiBasedRoutingTableForm.setListIMSIBasedRoutingTable(pageList.getListData());
				imsiBasedRoutingTableForm.setAction(BaseConstant.LISTACTION);*/
				request.setAttribute("imsiBasedRoutingTableForm", imsiBasedRoutingTableForm);
				return mapping.findForward(SEARCH_IMSI_ROUTINGTABLE);  
			}
			        
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
			ActionMessage message = new ActionMessage("imsibased.routingtable.search.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}  
		
		return mapping.findForward(FAILURE_FORWARD); 
	}
}