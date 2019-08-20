package com.elitecore.elitesm.web.servermgr.copypacket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.copypacket.forms.ViewCopyPacketMappingConfigForm;

public class ViewCopyPacketMappingConfigAction extends BaseWebAction {

	private static final String MODULE = ViewCopyPacketMappingConfigAction.class.getSimpleName();
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_COPY_PACKET_TRANSLATION_MAPPING_CONFIG;
	private static final String VIEW_FORWARD = "viewCopyPacketMappingConfigforward";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		ViewCopyPacketMappingConfigForm copyPacketMappingConfigForm =(ViewCopyPacketMappingConfigForm) form;
		try{
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			String strCopyPacketMapConfigId = request.getParameter("copyPacketTransConfId");
			String copyPacketMapConfigId = null;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if(Strings.isNullOrBlank(strCopyPacketMapConfigId) == false){
				copyPacketMapConfigId = strCopyPacketMapConfigId;
			}
			if (Strings.isNullOrBlank(copyPacketMapConfigId)) {
				copyPacketMapConfigId = copyPacketMappingConfigForm.getCopyPacketMapConfigId();
			}
			
			CopyPacketTransMapConfBLManager blManager = new CopyPacketTransMapConfBLManager();
			CopyPacketTranslationConfData copyPacketMappingConfData = blManager.getCopyPacketTransMapConfigDetailDataById(copyPacketMapConfigId);
			if(Strings.isNullOrBlank(copyPacketMappingConfData.getCopyPacketTransConfId()) == false) {
				CopyPacketTranslationConfData baseCopyPacketMappingConfData = blManager.getCopyPacketTransMapConfigDetailDataById(copyPacketMappingConfData.getCopyPacketTransConfId());
				
				copyPacketMappingConfigForm.setCopyPacketTranslationConfData(baseCopyPacketMappingConfData);
			}
			request.setAttribute("copyPacketMappingConfData",copyPacketMappingConfData);
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(VIEW_FORWARD);
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
			ActionMessage message = new ActionMessage("copypacket.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
	   return mapping.findForward(FAILURE);
	}
	
}
