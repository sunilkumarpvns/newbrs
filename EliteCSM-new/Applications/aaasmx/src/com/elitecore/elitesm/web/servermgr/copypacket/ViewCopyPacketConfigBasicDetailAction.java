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
import com.elitecore.elitesm.web.servermgr.copypacket.forms.ViewCopyPacketConfigForm;
import com.elitecore.elitesm.web.servermgr.transmapconf.ViewTranslationMappingConfigBasicDetailAction;

public class ViewCopyPacketConfigBasicDetailAction extends BaseWebAction{
	private static final String MODULE = ViewTranslationMappingConfigBasicDetailAction.class.getSimpleName();
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_COPY_PACKET_TRANSLATION_MAPPING_CONFIG;
	private static final String ACTION_FORWARD="viewCopyPacketConfigBasicDetail";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		ViewCopyPacketConfigForm copyPacketConfigForm = (ViewCopyPacketConfigForm) form;
		try{
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			String strCopyPacketConfigId = request.getParameter("copyPacketTransConfId");
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			String copyPacketConfigId = null;
			if(strCopyPacketConfigId != null){
				copyPacketConfigId = strCopyPacketConfigId;
			}
			if (copyPacketConfigId == null) {
				copyPacketConfigId = copyPacketConfigForm.getCopyPacketTransConfId();
			}
			
			if(copyPacketConfigForm.getAction()==null || copyPacketConfigForm.getAction().equals("")){
				CopyPacketTransMapConfBLManager blManager = new CopyPacketTransMapConfBLManager();
				CopyPacketTranslationConfData copyPacketTranslationConfData = blManager.getCopyPacketTransMapConfigData(copyPacketConfigId);				
				convertBeanToForm(copyPacketConfigForm,copyPacketTranslationConfData);
				request.setAttribute("copyPacketMappingConfData", copyPacketTranslationConfData);
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
			ActionMessage message = new ActionMessage("copypacket.view.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
	   return mapping.findForward(FAILURE);
	}
	private void convertBeanToForm(ViewCopyPacketConfigForm form,CopyPacketTranslationConfData data){
		form.setName(data.getName());
		form.setDescription(data.getDescription());
		form.setCopyPacketTransConfId(data.getCopyPacketTransConfId());
		
	}

}
