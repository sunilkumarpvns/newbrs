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
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslatorTypeData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servermgr.copypacket.forms.CreateCopyPacketTransMappingForm;

public class CreateCopyPacketMappingConfigAction  extends BaseWebAction{
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG;
	private static final String CREATE_COPY_PACKET_TRANSLATION_MAPPING_DETAIL = "createCopyPacketTransMapAction";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		CreateCopyPacketTransMappingForm copyPacketMappingConfForm = (CreateCopyPacketTransMappingForm) form; 
		try{
			checkActionPermission(request, ACTION_ALIAS);
			CopyPacketTransMapConfBLManager blManager= new CopyPacketTransMapConfBLManager();
			TranslatorTypeData translatorTo = blManager.getTranslatorTypeData(copyPacketMappingConfForm.getSelectedToTranslatorType());
			TranslatorTypeData translatorFrom = blManager.getTranslatorTypeData(copyPacketMappingConfForm.getSelectedFromTranslatorType());
			copyPacketMappingConfForm.setSelectedToTranslatorTypeData(translatorTo);
			copyPacketMappingConfForm.setSelectedFromTranslatorTypeData(translatorFrom);
			request.getSession().setAttribute("copyPacketMappingConfForm",copyPacketMappingConfForm);
			return mapping.findForward(CREATE_COPY_PACKET_TRANSLATION_MAPPING_DETAIL);
		
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
			ActionMessage message = new ActionMessage("copypacketmapping.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);

	}

}
