package com.elitecore.elitesm.web.diameter.prioritytable;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.prioritytable.PriorityTableConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.prioritytable.data.PriorityTableData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.prioritytable.form.PriorityTableForm;


public class ListPriorityTableConfigAction extends BaseWebAction{
	private static final String FAILURE_FORWARD = "failure";      
	private static final String VIEW_PRIORITY_TABLE = "viewPriorityTable";
	private static final String DIAMETER_PRIORITY_TABLE ="DIAMETER-PRIORITY-TABLE";
	private static final String SEARCH_PRIORITY_TABLE = ConfigConstant.SEARCH_PRIORITY_TABLE;
	private static final String UPDATE_PRIORITY_TABLE = ConfigConstant.UPDATE_PRIORITY_TABLE;
	
	@Override
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
		try {
			Logger.logInfo(DIAMETER_PRIORITY_TABLE, "Entered execute method of " + getClass().getName());
			PriorityTableForm priorityTableForm = (PriorityTableForm) form;
			if(priorityTableForm.getAction() == null || priorityTableForm.getAction().equals("")){
				
				checkActionPermission(request, SEARCH_PRIORITY_TABLE);
				
				PriorityTableConfBLManager blManagerForPriorityTable = new PriorityTableConfBLManager();
				List<PriorityTableData> lstPriorityTableData = blManagerForPriorityTable.getPriorityTableList();
				if(lstPriorityTableData != null && lstPriorityTableData.isEmpty() == false){
					request.setAttribute("lstPriorityTableData", lstPriorityTableData);
				}
				return mapping.findForward(VIEW_PRIORITY_TABLE);
			}else if(priorityTableForm.getAction().equalsIgnoreCase("update")){
					checkActionPermission(request, UPDATE_PRIORITY_TABLE);
					PriorityTableConfBLManager blManaConfBLManager = new PriorityTableConfBLManager();
					List<PriorityTableData> lstPriorityTableData = new ArrayList<PriorityTableData>();
					String priorityTableId[] = request.getParameterValues("priorityTableId");
					String applicationId[] = request.getParameterValues("applicationId");
					String commandCode[] = request.getParameterValues("commandCode");
					String ipAddress[] = request.getParameterValues("ipAddress");
					String  newSession[]= request.getParameterValues("newSession");
					String priority[] = request.getParameterValues("priority");
					if(priority != null ){
						for(int i=0; i < priority.length ;i++){
							PriorityTableData data = new PriorityTableData();
							if(priorityTableId[i].isEmpty() == false && priorityTableId !=null){
								data.setPriorityTableId(priorityTableId[i]);
							}else{
								data.setPriorityTableId(null);
							}
							data.setApplicationId(applicationId[i]);
							data.setCommandCode(commandCode[i]);
							data.setIpAddress(ipAddress[i]);
							data.setDiameterSession(Integer.parseInt(newSession[i]));
							data.setPriority(Integer.parseInt(priority[i]));
							lstPriorityTableData.add(data);
						}
					}
					
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					
					blManaConfBLManager.updatePriorityTable(lstPriorityTableData, staffData);
					
					request.setAttribute("responseUrl", "/updatePriorityTableConfig");
					request.setAttribute("action",null);
					ActionMessage message = new ActionMessage("diameter.priority.updatetable.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);
			}
			
		}catch(ActionNotPermitedException e){
			Logger.logError(DIAMETER_PRIORITY_TABLE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (Exception exp) {                                                                                                             
			Logger.logError(DIAMETER_PRIORITY_TABLE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
			ActionMessage message = new ActionMessage("diameter.priority.updatetable.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}  
		
		return mapping.findForward(FAILURE_FORWARD); 
	}
}
