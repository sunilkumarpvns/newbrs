package com.elitecore.elitesm.web.diameter.imsibasedroutingtable;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.blmanager.diameter.imsibasedroutingtable.IMSIBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.imsibasedroutingtable.form.IMSIBasedRoutingTableForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewIMSIBasedRoutingTableHistoryAction extends BaseWebAction {

	private static final String VIEW_HISTORY_FORWARD = "viewIMSIBasedRoutingTableHistory";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_IMSI_BASED_ROUTING_TABLE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		try {
				checkActionPermission(request, ACTION_ALIAS);
				Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
				IMSIBasedRoutingTableBLManager blManager= new IMSIBasedRoutingTableBLManager();
				IMSIBasedRoutingTableForm imsiBasedRoutingTableForm = (IMSIBasedRoutingTableForm)form;
				
				String routingTableId = "";
				
				String strRoutingTableId = request.getParameter("routingTableId");
				if(Strings.isNullOrBlank(strRoutingTableId) == false){
					routingTableId = strRoutingTableId;
				}	
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				IMSIBasedRoutingTableData routingTableData = blManager.getImsiBasedRoutingTableData(routingTableId);
				convertBeanToForm(imsiBasedRoutingTableForm,routingTableData);
				
				HistoryBLManager historyBlManager= new HistoryBLManager();
				
				String strAuditUid = request.getParameter("auditUid");
				String strSytemAuditId=request.getParameter("systemAuditId");
				String name=request.getParameter("name");
					
				if(strSytemAuditId != null){
					request.setAttribute("systemAuditId", strSytemAuditId);
				}
					
				if( routingTableId != null && Strings.isNullOrBlank(strAuditUid) == false ){
					
					staffData.setAuditName(routingTableData.getRoutingTableName());
					staffData.setAuditId(routingTableData.getAuditUId());
					List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
					
					request.setAttribute("name", name);
					request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
				}
				
				/* Fetch Diameter Peer List*/
				DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
				List<DiameterPeerData> diameterPeerDataList = diameterPeerBLManager.getDiameterPeerList();
				imsiBasedRoutingTableForm.setDiameterPeerDataList(diameterPeerDataList);
				
				request.setAttribute("diameterPeerDataList", diameterPeerDataList);
				request.setAttribute("imsiBasedRoutingTableData",routingTableData);
				request.setAttribute("imsiBasedRoutingTableForm", imsiBasedRoutingTableForm);
			}catch(ActionNotPermitedException e){
				Logger.logError(MODULE, "Restricted to do action.");
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(INVALID_ACCESS_FORWARD);	
			}catch (DataManagerException managerExp) {
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.error");                                                         
				ActionMessages messages = new ActionMessages();                                                                                 
				messages.add("information", message);                                                                                           
				saveErrors(request, messages);                                         
				return mapping.findForward(FAILURE_FORWARD);
			}
			return mapping.findForward(VIEW_HISTORY_FORWARD);
	}
	
	private void convertBeanToForm(IMSIBasedRoutingTableForm searchIMSIBasedRoutingTableForm,IMSIBasedRoutingTableData imsiBasedRoutingTableData) {
		searchIMSIBasedRoutingTableForm.setRoutingTableId(imsiBasedRoutingTableData.getRoutingTableId());
		searchIMSIBasedRoutingTableForm.setImsiBasedRoutingTableName(imsiBasedRoutingTableData.getRoutingTableName());
		searchIMSIBasedRoutingTableForm.setAuditUId(imsiBasedRoutingTableData.getAuditUId());
		searchIMSIBasedRoutingTableForm.setImsiIdentityAttributes(imsiBasedRoutingTableData.getImsiIdentityAttributes());
		searchIMSIBasedRoutingTableForm.setImsiFieldMappingSet(imsiBasedRoutingTableData.getImsiFieldMappingDataSet());
	}
}
