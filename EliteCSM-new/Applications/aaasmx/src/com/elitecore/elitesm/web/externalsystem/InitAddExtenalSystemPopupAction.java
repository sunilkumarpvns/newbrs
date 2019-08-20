package com.elitecore.elitesm.web.externalsystem;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.externalsystem.forms.AddExternalSystemPopupForm;

public class InitAddExtenalSystemPopupAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "addExternalSystemPopup";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "InitAddExtenalSystemPopupAction";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		try{
		AddExternalSystemPopupForm addExternalSystemPopupForm = (AddExternalSystemPopupForm)form;
		ExternalSystemInterfaceBLManager externalSystemBLmanager = new ExternalSystemInterfaceBLManager();
		String strExternalSystemTypeId = request.getParameter("externalSystemTypeId");
		String strWeightageSelection = request.getParameter("weightageSelection");
		List<ExternalSystemInterfaceInstanceData> externalSystemInstanceList = new ArrayList<ExternalSystemInterfaceInstanceData>(1); 
		Logger.logDebug(MODULE, "strExternalSystemTypeId :"+strExternalSystemTypeId);
		if(strExternalSystemTypeId!=null){
			
			long externalSystemTypeId = Long.parseLong(strExternalSystemTypeId);
			Logger.logDebug(MODULE, "externalSystemTypeId :"+externalSystemTypeId);
			externalSystemInstanceList = externalSystemBLmanager.getExternalSystemInstanceDataList(externalSystemTypeId);		
			addExternalSystemPopupForm.setExtenalSystemList(externalSystemInstanceList);
			if(strWeightageSelection!=null && strWeightageSelection.length()>0){
				addExternalSystemPopupForm.setWeightageSelection(new Boolean(strWeightageSelection));
			}
		}
		Logger.logDebug(MODULE, "externalSystemInstanceList :"+externalSystemInstanceList);;
		request.setAttribute("externalSystemInstanceList",externalSystemInstanceList);
		request.setAttribute("addExternalSystemPopupForm",addExternalSystemPopupForm);

		return mapping.findForward(SUCCESS_FORWARD);
		}catch(DataManagerException e){
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("general.error");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
		}catch(Exception e){
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("general.error");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
}
