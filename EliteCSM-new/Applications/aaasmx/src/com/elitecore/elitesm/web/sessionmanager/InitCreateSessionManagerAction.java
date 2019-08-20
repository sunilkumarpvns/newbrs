package com.elitecore.elitesm.web.sessionmanager;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.sessionmanager.forms.CreateSessionManagerForm;


public class InitCreateSessionManagerAction extends BaseWebAction {

	private static final String SUCCESS_FORWARD = "createSessionManager";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_SESSION_MANAGER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			request.getSession().removeAttribute("createSessionManagerDetailForm");
			request.getSession().removeAttribute("dbFieldMapList");
			request.getSession().removeAttribute("createSessionManagerForm");
			CreateSessionManagerForm createSessionManagerForm =  (CreateSessionManagerForm) form;
			String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			createSessionManagerForm.setDescription(getDefaultDescription(userName));
			
			//createSessionManagerForm.setName("");
			ExternalSystemInterfaceBLManager externalSystemBLmanager = new ExternalSystemInterfaceBLManager();
			List<ExternalSystemInterfaceInstanceData> sessionManagerInstanceList = externalSystemBLmanager.getExternalSystemInstanceDataList(ExternalSystemConstants.SESSION_MANAGER);
			String sessionManagerInstanceIds[] = new String[sessionManagerInstanceList.size()];
			String sessionManagerInstanceNames[][] = new String[sessionManagerInstanceList.size()][4]; 
			for(int i=0;i<sessionManagerInstanceList.size();i++) {
				ExternalSystemInterfaceInstanceData	externalSystemData = sessionManagerInstanceList.get(i);
				sessionManagerInstanceNames[i][0] = String.valueOf(externalSystemData.getName());
				sessionManagerInstanceNames[i][1] = String.valueOf(externalSystemData.getAddress());
				sessionManagerInstanceNames[i][2] = String.valueOf(externalSystemData.getMinLocalPort());
				sessionManagerInstanceNames[i][3] = String.valueOf(externalSystemData.getExpiredRequestLimitCount());
				sessionManagerInstanceIds[i] = String.valueOf(externalSystemData.getEsiInstanceId());
			}
			
			TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
			createSessionManagerForm.setTranslationMappingConfDataList(translationMappingConfBLManager.getRadiusToRadiusTranslationMapping());
			CopyPacketTransMapConfBLManager copyPacketTranslationConfBLManager = new CopyPacketTransMapConfBLManager();
			List<CopyPacketTranslationConfData> copyPacketMappingConfDataList = copyPacketTranslationConfBLManager.getCopyPacketTransMapConfigList(TranslationMappingConfigConstants.RADIUS, TranslationMappingConfigConstants.RADIUS);
			createSessionManagerForm.setCopyPacketMappingConfDataList(copyPacketMappingConfDataList);
			
			request.setAttribute("createSessionManagerForm", createSessionManagerForm);
			request.setAttribute("sessionManagerInstanceIds", sessionManagerInstanceIds);
			request.setAttribute("sessionManagerInstanceNames", sessionManagerInstanceNames);
			request.setAttribute("sessionManagerInstanceList", sessionManagerInstanceList);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			return mapping.findForward(SUCCESS_FORWARD);

		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

}
