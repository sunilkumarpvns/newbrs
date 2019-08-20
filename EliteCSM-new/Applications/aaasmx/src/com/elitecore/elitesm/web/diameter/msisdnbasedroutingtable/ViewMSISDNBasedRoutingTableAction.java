package com.elitecore.elitesm.web.diameter.msisdnbasedroutingtable;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.blmanager.diameter.imsibasedroutingtable.IMSIBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.diameter.msisdnbasedroutingtable.MSISDNBasedRoutingTableBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIFieldMappingData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.imsibasedroutingtable.form.IMSIBasedRoutingTableForm;
import com.elitecore.elitesm.web.diameter.msisdnbasedroutingtable.form.MSISDNBasedRoutingTableForm;

public class ViewMSISDNBasedRoutingTableAction extends BaseWebAction { 

	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="IMSI-BASED-ROUTING-CONF";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_MSISDN_BASED_ROUTING_TABLE;
	private static final String VIEW_FORWARD = "viewMSISDNRoutingTable";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			
			MSISDNBasedRoutingTableForm msisdnBasedRoutingTableForm = ( MSISDNBasedRoutingTableForm)form;
			MSISDNBasedRoutingTableBLManager blManager = new  MSISDNBasedRoutingTableBLManager();
			
			String strRoutingTableId = request.getParameter("routingTableId");
			MSISDNBasedRoutingTableData msisdnBasedRoutingTableData = blManager.getMSISDNBasedRoutingTableData(strRoutingTableId);
			convertBeanToForm(msisdnBasedRoutingTableForm,msisdnBasedRoutingTableData);
					
			/* Fetch Diameter Peer List*/
			DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
			List<DiameterPeerData> diameterPeerDataList = diameterPeerBLManager.getDiameterPeerList();
			msisdnBasedRoutingTableForm.setDiameterPeerDataList(diameterPeerDataList);
			
			request.setAttribute("diameterPeerDataList", diameterPeerDataList);
			request.setAttribute("msisdnBasedRoutingTableData", msisdnBasedRoutingTableData);
			request.setAttribute("msisdnBasedRoutingTableForm", msisdnBasedRoutingTableForm);
			return mapping.findForward(VIEW_FORWARD);
		
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
			ActionMessage message = new ActionMessage("msisdnbased.routingtable.view.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}  
		
		return mapping.findForward(FAILURE_FORWARD); 
	}

	private void convertBeanToForm(MSISDNBasedRoutingTableForm msisdnBasedRoutingTableForm,MSISDNBasedRoutingTableData msisdnBasedRoutingTableData) {
		msisdnBasedRoutingTableForm.setRoutingTableId(msisdnBasedRoutingTableData.getRoutingTableId());
		msisdnBasedRoutingTableForm.setMsisdnBasedRoutingTableName(msisdnBasedRoutingTableData.getRoutingTableName());
		msisdnBasedRoutingTableForm.setAuditUId(msisdnBasedRoutingTableData.getAuditUId());
		msisdnBasedRoutingTableForm.setMsisdnIdentityAttributes(msisdnBasedRoutingTableData.getMsisdnIdentityAttributes());
		msisdnBasedRoutingTableForm.setMsisdnFieldMappingSet(msisdnBasedRoutingTableData.getMsisdnFieldMappingDataSet());
		msisdnBasedRoutingTableForm.setMcc(msisdnBasedRoutingTableData.getMcc());
		msisdnBasedRoutingTableForm.setMsisdnLength(msisdnBasedRoutingTableData.getMsisdnLength());
	}
}