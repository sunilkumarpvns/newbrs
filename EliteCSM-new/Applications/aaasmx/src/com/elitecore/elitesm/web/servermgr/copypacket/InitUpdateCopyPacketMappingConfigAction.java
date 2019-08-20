package com.elitecore.elitesm.web.servermgr.copypacket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.copypacket.forms.UpdateCopyPacketMappingConfigForm;

public class InitUpdateCopyPacketMappingConfigAction extends BaseWebAction {
	
	private static final String ACTION_FORWARD = "initUpdateCopyPacketMappingConfigDetail";
	private static final String MODULE = InitUpdateCopyPacketMappingConfigAction.class.getSimpleName();
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG;
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {

		UpdateCopyPacketMappingConfigForm copyPacketMappingConfigForm = (UpdateCopyPacketMappingConfigForm)form;

		try{
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			String strCopyPacjetMapConfigId = request.getParameter("copyPacketMappingConfigId");
			String copyPacketMappingConfigId = null;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if(strCopyPacjetMapConfigId != null){
				copyPacketMappingConfigId = strCopyPacjetMapConfigId;
			}
			if (copyPacketMappingConfigId == null) {
				copyPacketMappingConfigId = copyPacketMappingConfigForm.getCopyPacketTransConfId();
			}

			if(copyPacketMappingConfigForm.getAction()==null || copyPacketMappingConfigForm.getAction().equals("")){
				CopyPacketTransMapConfBLManager blManager = new CopyPacketTransMapConfBLManager();
				CopyPacketTranslationConfData copyPacketMappingConfData = blManager.getCopyPacketTransMapConfigDetailDataById(copyPacketMappingConfigId);
			
				request.setAttribute("copyPacketMappingConfData",copyPacketMappingConfData);
				doAuditing(staffData, ACTION_ALIAS);
				return mapping.findForward(ACTION_FORWARD);
			}

		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("transmapconf.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);

	}

}
