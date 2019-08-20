package com.elitecore.elitesm.web.sessionmanager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.sessionmanager.forms.UpdateSessionManagerBasicDetailForm;


public class InitUpdateSessionManagerBasicDetailAction extends BaseWebAction {

	private static final String UPDATE_FORWARD = "updateSessionManagerInstance";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS=ConfigConstant.UPDATE_SESSION_MANAGER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		try{
			checkActionPermission(request, ACTION_ALIAS);
			UpdateSessionManagerBasicDetailForm  updateSessionManagerBasicDetailForm =  (UpdateSessionManagerBasicDetailForm) form;
			String strSmInstancId = request.getParameter("sminstanceid");
			Logger.logDebug(MODULE,"strSmInstancId: "+strSmInstancId);
			String sessionManagerId=null;

			if(strSmInstancId!=null){
				sessionManagerId = strSmInstancId;
			}
			
			SessionManagerBLManager blManager = new SessionManagerBLManager();
			ISessionManagerInstanceData sessionManagerInstanceData = blManager.getSessionManagerDataById(sessionManagerId);
			updateSessionManagerBasicDetailForm = convertBeanToForm(sessionManagerInstanceData);
			request.setAttribute("updateSessionManagerBasicDetailForm",updateSessionManagerBasicDetailForm);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			return mapping.findForward(UPDATE_FORWARD);

		} catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch(DataManagerException managerExp){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());            
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("sessionmanager.update.failure"));
			saveErrors(request, messages);
			return mapping.findForward(FAILURE);
		
		}catch(Exception e){
			Logger.logError(MODULE, "Error, reason : " + e.getMessage());            
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("sessionmanager.update.failure"));
			saveErrors(request, messages);
			return mapping.findForward(FAILURE);
		}

	}

	private UpdateSessionManagerBasicDetailForm convertBeanToForm(ISessionManagerInstanceData sessionManagerInstanceData) {
		UpdateSessionManagerBasicDetailForm form= new UpdateSessionManagerBasicDetailForm();
		if(sessionManagerInstanceData!=null){
			form.setSmInstanceId(sessionManagerInstanceData.getSmInstanceId());
			form.setName(sessionManagerInstanceData.getName());
			form.setDescription(sessionManagerInstanceData.getDescription());
			//form.setSmInstanceId(ManagerInstanceData.getSmtype());
		}
		return form;
	}
	 

}
