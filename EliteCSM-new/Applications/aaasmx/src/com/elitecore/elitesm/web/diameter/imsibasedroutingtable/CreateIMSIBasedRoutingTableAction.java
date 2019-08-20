package com.elitecore.elitesm.web.diameter.imsibasedroutingtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.imsibasedroutingtable.IMSIBasedRoutingTableBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.imsibasedroutingtable.form.IMSIBasedRoutingTableForm;

public class CreateIMSIBasedRoutingTableAction extends BaseWebAction { 

	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="IMSI-BASED-ROUTING-CONF";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_IMSI_BASED_ROUTING_TABLE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			
			IMSIBasedRoutingTableForm imsiBasedRoutingTableForm = (IMSIBasedRoutingTableForm)form;
			IMSIBasedRoutingTableBLManager blManager = new IMSIBasedRoutingTableBLManager();
			IMSIBasedRoutingTableData imsiBasedRoutingTableData = new  IMSIBasedRoutingTableData();
			imsiBasedRoutingTableData.setRoutingTableName(imsiBasedRoutingTableForm.getImsiBasedRoutingTableName());
			imsiBasedRoutingTableData.setImsiIdentityAttributes(imsiBasedRoutingTableForm.getImsiIdentityAttributes());
			blManager.create(imsiBasedRoutingTableData);
			
			request.setAttribute("responseUrl", "/searchSubscriberRoutingTable.do?method=initSearch");
			ActionMessage message = new ActionMessage("imsibased.routingtable.create.success");
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
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
			ActionMessage message = new ActionMessage("imsibased.routingtable.create.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}  
		
		return mapping.findForward(FAILURE_FORWARD); 
	}
}