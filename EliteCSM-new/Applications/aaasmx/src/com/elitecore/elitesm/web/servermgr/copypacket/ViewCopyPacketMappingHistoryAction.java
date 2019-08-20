package com.elitecore.elitesm.web.servermgr.copypacket;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.elitesm.web.servermgr.copypacket.forms.UpdateCopyPacketMappingConfigForm;
import com.elitecore.elitesm.web.servermgr.transmapconf.ViewTranslationMappingConfigHistoryAction;

public class ViewCopyPacketMappingHistoryAction  extends BaseWebAction{
	private static final String MODULE = ViewTranslationMappingConfigHistoryAction.class.getSimpleName();
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG;
	private static final String ACTION_FORWARD="viewCopyPacketMappingHistory";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				UpdateCopyPacketMappingConfigForm updateCopyPacketMappingConfigForm = (UpdateCopyPacketMappingConfigForm)form;
				CopyPacketTransMapConfBLManager blManager = new CopyPacketTransMapConfBLManager();
				
				String strCopyPacketMapConfigId = request.getParameter("copyPacketMappingConfigId");
				String copyPacketMappingConfigId = null;
				
				if(strCopyPacketMapConfigId != null){
					copyPacketMappingConfigId = strCopyPacketMapConfigId;
				}
				if (copyPacketMappingConfigId == null) {
					copyPacketMappingConfigId = updateCopyPacketMappingConfigForm.getCopyPacketTransConfId();
				}
				CopyPacketTranslationConfData copyPacketMapConfigData = blManager.getCopyPacketTransMapConfigData(copyPacketMappingConfigId);
				
				HistoryBLManager historyBlManager= new HistoryBLManager();
			
				

				String strAuditUid = request.getParameter("auditUid");
				String strSytemAuditId=request.getParameter("systemAuditId");
				String name=request.getParameter("name");
				
				if(strSytemAuditId != null){
					request.setAttribute("systemAuditId", strSytemAuditId);
				}
				
				if(Strings.isNullOrBlank(copyPacketMappingConfigId) == false && Strings.isNullOrBlank(strAuditUid) == false){
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					staffData.setAuditName(copyPacketMapConfigData.getName());
					staffData.setAuditId(copyPacketMapConfigData.getAuditUid());
					List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
					request.setAttribute("name", name);
					request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
				}
				request.setAttribute("copyPacketMappingConfData", copyPacketMapConfigData);
				return mapping.findForward(ACTION_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message1 = new ActionMessage("databaseds.viewdatasource.failure");
				messages.add("information",message1);
				saveErrors(request,messages);
			} 
			return mapping.findForward(FAILURE);
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

}
