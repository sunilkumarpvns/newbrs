package com.elitecore.netvertexsm.web.servermgr.spinterface;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.spinterface.SPInterfaceBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.spinterface.form.CreateSPInterfaceForm;

public class InitCreateSPInterfaceAction extends BaseWebAction {
	private static final String CREATE_DRIVER_FORWARD = "initCreateSPInterface";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_SP_INTERFACE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		if((checkAccess(request, ACTION_ALIAS))){

			try{
				CreateSPInterfaceForm createSPInterfaceForm = (CreateSPInterfaceForm) form;
				setDefaultValues(createSPInterfaceForm,request);
				SPInterfaceBLManager driverBLManager = new SPInterfaceBLManager();
				List<DriverTypeData> driverTypeList = driverBLManager.getSPInterfaceTypeList();
				createSPInterfaceForm.setDriverTypeList(driverTypeList);
				return mapping.findForward(CREATE_DRIVER_FORWARD);

			}catch(DataManagerException managerExp){
				managerExp.printStackTrace();
				Logger.logTrace(MODULE,"Error during data Manager operation, reason :"+managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("spinterface.opencreatepage.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}catch(Exception exp){
				exp.printStackTrace();
				Logger.logTrace(MODULE,"Error during data Manager operation, reason :"+exp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("spinterface.opencreatepage.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}

			return mapping.findForward(FAILURE);
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	private void setDefaultValues(CreateSPInterfaceForm form, HttpServletRequest request){
		if(form!=null){
			form.setDescription(getDefaultDescription(request));
		}
		
	}
}
