package com.elitecore.elitesm.web.diameter.imsibasedroutingtable;

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
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.diameter.imsibasedroutingtable.form.IMSIBasedRoutingTableForm;

public class ViewIMSIBasedRoutingTableAction extends BaseWebAction { 

	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="IMSI-BASED-ROUTING-CONF";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_IMSI_BASED_ROUTING_TABLE;
	private static final String VIEW_FORWARD = "viewIMSIRoutingTable";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			
			IMSIBasedRoutingTableForm imsiBasedRoutingTableForm = (IMSIBasedRoutingTableForm)form;
			IMSIBasedRoutingTableBLManager blManager = new IMSIBasedRoutingTableBLManager();
			
			String strRoutingTableId = request.getParameter("routingTableId");
			IMSIBasedRoutingTableData imsiBasedRoutingTableData = blManager.getImsiBasedRoutingTableData(strRoutingTableId);
			convertBeanToForm(imsiBasedRoutingTableForm,imsiBasedRoutingTableData);
					
			/* Fetch Diameter Peer List*/
			DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
			List<DiameterPeerData> diameterPeerDataList = diameterPeerBLManager.getDiameterPeerList();
			imsiBasedRoutingTableForm.setDiameterPeerDataList(diameterPeerDataList);
			
			request.setAttribute("diameterPeerDataList", diameterPeerDataList);
			request.setAttribute("imsiBasedRoutingTableData", imsiBasedRoutingTableData);
			request.setAttribute("imsiBasedRoutingTableForm", imsiBasedRoutingTableForm);
			return mapping.findForward(VIEW_FORWARD);
		
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
			ActionMessage message = new ActionMessage("imsibased.routingtable.update.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}  
		
		return mapping.findForward(FAILURE_FORWARD); 
	}

	private void convertBeanToForm(IMSIBasedRoutingTableForm searchIMSIBasedRoutingTableForm,IMSIBasedRoutingTableData imsiBasedRoutingTableData) {
		searchIMSIBasedRoutingTableForm.setRoutingTableId(imsiBasedRoutingTableData.getRoutingTableId());
		searchIMSIBasedRoutingTableForm.setImsiBasedRoutingTableName(imsiBasedRoutingTableData.getRoutingTableName());
		searchIMSIBasedRoutingTableForm.setAuditUId(imsiBasedRoutingTableData.getAuditUId());
		searchIMSIBasedRoutingTableForm.setImsiIdentityAttributes(imsiBasedRoutingTableData.getImsiIdentityAttributes());
		searchIMSIBasedRoutingTableForm.setImsiFieldMappingSet(imsiBasedRoutingTableData.getImsiFieldMappingDataSet());
	}
}