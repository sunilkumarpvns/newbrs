package com.elitecore.netvertexsm.web.servermgr.drivers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.drivers.form.CreateDriverInstanceForm;

public class CreateDriverAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";	
	private static final String MODULE = "CREATE_DRIVER_ACTION";
	
	private static final String CSV_FORWARD = "initCreateCSVDriver";
	private static final String DBCDR_FORWARD = "initCreateDBCDRDriver";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{		
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		CreateDriverInstanceForm driverForm = (CreateDriverInstanceForm) form;
		Long driverType = driverForm.getDriverTypeId();
		
		if(driverType != null){
			HttpSession session = request.getSession(true);			
			session.setAttribute("createDriverInstanceForm",driverForm);
			
			if(driverType==4){
				return mapping.findForward(CSV_FORWARD);
			}else if (driverType==5) {
				return mapping.findForward(DBCDR_FORWARD);
			}
		}
		return mapping.findForward(FAILURE_FORWARD);	
	}
}
