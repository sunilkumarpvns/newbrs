package com.elitecore.netvertexsm.web.datasource.esiradius;

import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.netvertexsm.blmanager.datasource.EsiRadiusBLManager;
import com.elitecore.netvertexsm.datamanager.datasource.esiradius.data.EsiRadiusData;
import com.elitecore.netvertexsm.datamanager.datasource.esiradius.data.IEsiRadiusData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.datasource.esiradius.form.CreateEsiRadiusForm;

public class CreateEsiRadiusAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String SEARCH_FORWARD = "searchProfile";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_ESI_RADIUS_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		try{
		    CreateEsiRadiusForm createEsiRadiusForm = (CreateEsiRadiusForm) form;
		    EsiRadiusBLManager esiRadiusBLManager = new EsiRadiusBLManager();
		    IEsiRadiusData esiRadiusData = new EsiRadiusData();
		    		   		
		    esiRadiusData.setName(createEsiRadiusForm.getName());
		    esiRadiusData.setDescription(createEsiRadiusForm.getDescription());
		    esiRadiusData.setSharedSecret(createEsiRadiusForm.getSharedSecret());
		    esiRadiusData.setAddress(createEsiRadiusForm.getAddress());
		    esiRadiusData.setTimeout(createEsiRadiusForm.getTimeout());
		    esiRadiusData.setMinLocalPort(createEsiRadiusForm.getMinLocalPort());
		    esiRadiusData.setExpiredReqLimitCnt(createEsiRadiusForm.getExpiredReqLimitCnt());
		    esiRadiusData.setRetryLimit(createEsiRadiusForm.getRetryLimit());
		    esiRadiusData.setStatusCheckDuration(createEsiRadiusForm.getStatusCheckDuration());
		    
		    Date currentDate = new Date();
			esiRadiusData.setCreateDate(new Timestamp(currentDate.getTime()));
			esiRadiusData.setStatus("ON");
		    
		    esiRadiusBLManager.create(esiRadiusData, ACTION_ALIAS);
		    request.setAttribute("responseUrl","/initSearchEsiRadius.do");
		    return mapping.findForward(SUCCESS_FORWARD);
		}catch (Exception e) {
			e.printStackTrace();
		}    
		/*}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}*/
		return mapping.findForward(FAILURE_FORWARD);
	}	
}
