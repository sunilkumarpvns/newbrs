package com.elitecore.elitesm.web.servermgr.copypacket;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.copypacket.forms.UpdateCopyPacketMappingConfigForm;
import com.elitecore.elitesm.ws.logger.Logger;

public class UpdateCopyPacketMappingConfigAction extends BaseWebAction{
	private static final String MODULE = UpdateCopyPacketMappingConfigAction.class.getSimpleName();
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG;
	private static final String ACTION_FORWARD="updateCopyPacketMappingConf";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
			
			UpdateCopyPacketMappingConfigForm updateCopyPacketMapForm = (UpdateCopyPacketMappingConfigForm) form;
			
			try {
				checkActionPermission(request, ACTION_ALIAS);
				Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
				String strCopyPacketMapConfigId = request.getParameter("copyPacketMappingConfigId");
				String copyPacketMapConfigId = null;
				if(strCopyPacketMapConfigId != null){
					copyPacketMapConfigId = strCopyPacketMapConfigId;
				}
				if(copyPacketMapConfigId == null){
					copyPacketMapConfigId = updateCopyPacketMapForm.getCopyPacketTransConfId();
				}
				if(updateCopyPacketMapForm.getAction() == null || updateCopyPacketMapForm.equals("")){
					CopyPacketTransMapConfBLManager blManager = new CopyPacketTransMapConfBLManager();
					CopyPacketTranslationConfData copyPacketTranslationConfData = blManager.getCopyPacketTransMapConfigData(copyPacketMapConfigId);
					convertBeanToForm(updateCopyPacketMapForm,copyPacketTranslationConfData);
					
					/* Driver Script */
					ScriptBLManager scriptBLManager = new ScriptBLManager();
					List<ScriptInstanceData> externalScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.EXTERNAL_RADIUS_SCRIPT);
				
					updateCopyPacketMapForm.setExternalScriptList(externalScriptList);
					
					request.setAttribute("copyPacketMappingConfData", copyPacketTranslationConfData);
					return mapping.findForward(ACTION_FORWARD);
					
				}else if(updateCopyPacketMapForm.getAction()!=null && updateCopyPacketMapForm.getAction().equals("update")){
					CopyPacketTransMapConfBLManager blManager = new CopyPacketTransMapConfBLManager();
					CopyPacketTranslationConfData copyPacketTransMapConfData = blManager.getCopyPacketTransMapConfigData(copyPacketMapConfigId);
					copyPacketTransMapConfData = convertFormToBean(updateCopyPacketMapForm,copyPacketTransMapConfData);
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					request.setAttribute("copyPacketMappingConfData", copyPacketTransMapConfData);
					staffData.setAuditId(copyPacketTransMapConfData.getAuditUid());
					staffData.setAuditName(copyPacketTransMapConfData.getName());
					blManager.updateBasicDetail(copyPacketTransMapConfData, copyPacketMapConfigId, staffData, ACTION_ALIAS);
					request.setAttribute("responseUrl","/viewCopyPacketConfigBasicDetail.do?copyPacketTransConfId="+copyPacketTransMapConfData.getCopyPacketTransConfId());
					ActionMessage message = new ActionMessage("copypacket.update.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request,messages);
					return mapping.findForward(SUCCESS);
					
				}
			} catch(ActionNotPermitedException e){
				Logger.logError(MODULE, "Restricted to do action.");
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(INVALID_ACCESS_FORWARD);	
			}catch (DuplicateInstanceNameFoundException dpfExp) {
		        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
		        Logger.logTrace(MODULE,dpfExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
				request.setAttribute("errorDetails", errorElements);
		        ActionMessage message = new ActionMessage("copypacket.duplicate.failure",updateCopyPacketMapForm.getName());
		        ActionMessages messages = new ActionMessages();
		        messages.add("information",message);
		        saveErrors(request,messages);
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

		private CopyPacketTranslationConfData convertFormToBean(
				UpdateCopyPacketMappingConfigForm form,
				CopyPacketTranslationConfData data) {
			data.setCopyPacketTransConfId(form.getCopyPacketTransConfId());
			data.setName(form.getName());
			data.setDescription(form.getDescription());
			data.setScript(form.getScript());
			data.setAuditUid(form.getAuditId());
			return data;
		}
		private void convertBeanToForm(
				UpdateCopyPacketMappingConfigForm form,
				CopyPacketTranslationConfData data) {
				form.setName(data.getName());
				form.setDescription(data.getDescription());
				form.setScript(data.getScript());
				form.setCopyPacketTransConfId(data.getCopyPacketTransConfId());
				form.setAuditId(data.getAuditUid());
			
		}

}
