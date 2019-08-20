package com.elitecore.netvertexsm.web.servermgr.sessionmgr;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.sessionmgr.SessionBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionConfData;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionFieldMapData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.sessionmgr.form.CreateSessionConfForm;


public class EditSessionConfAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_SESSION_MANAGER;
	private static final String MODULE = "EDIT_SESSION_CONF";
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				CreateSessionConfForm createSessionConfForm=(CreateSessionConfForm)form;
				SessionBLManager sessionBLManager = new SessionBLManager();
				
				String[] coreDBFieldName = request.getParameterValues("coreDBField");
				String[] coreReferingAttr = request.getParameterValues("corerefrngattr");
				String[] coreDataType = request.getParameterValues("coredatatype");
				
				String[] subDBFieldName = request.getParameterValues("subDBField");
				String[] subReferingAttr = request.getParameterValues("subrefrngattr");
				String[] subDataType = request.getParameterValues("subdatatype");
				
				SessionConfData sessionConfData=sessionBLManager.getSessionConfData();
				
				Set<SessionFieldMapData> sessionFieldMapSet=new HashSet<SessionFieldMapData>();
				
				if(coreDBFieldName!=null){
					for(int i=0;i<coreDBFieldName.length;i++){
						SessionFieldMapData sessionFieldMapData=new SessionFieldMapData();
						sessionFieldMapData.setSessionConfID(createSessionConfForm.getSessionConfID());
						sessionFieldMapData.setFieldName(coreDBFieldName[i]);
						sessionFieldMapData.setReferringAttr(coreReferingAttr[i]);
						sessionFieldMapData.setDatatype(Integer.parseInt(coreDataType[i]));	
						sessionFieldMapData.setType("Core");
						sessionFieldMapSet.add(sessionFieldMapData);
				    }
				}
				if(subDBFieldName!=null){
					for(int i=0;i<subDBFieldName.length;i++){
						SessionFieldMapData sessionFieldMapData=new SessionFieldMapData();
						sessionFieldMapData.setSessionConfID(createSessionConfForm.getSessionConfID());
						sessionFieldMapData.setFieldName(subDBFieldName[i]);
						sessionFieldMapData.setReferringAttr(subReferingAttr[i]);
						sessionFieldMapData.setDatatype(Integer.parseInt(subDataType[i]));	
						sessionFieldMapData.setType("SubSession");
						sessionFieldMapSet.add(sessionFieldMapData);
				    }
				}
				
				
				sessionConfData.setSessionConfID(createSessionConfForm.getSessionConfID());
				sessionConfData.setDataSourceID(createSessionConfForm.getDataSourceID());
				sessionConfData.setBatchUpdate(createSessionConfForm.getBatchUpdate());
				sessionConfData.setBatchSize(createSessionConfForm.getBatchSize());
				sessionConfData.setBatchUpdateInterval(createSessionConfForm.getBatchUpdateInterval());
				sessionConfData.setDbQueryTimeout(createSessionConfForm.getDbQueryTimeout());
				sessionConfData.setSessionFieldMapDataset(sessionFieldMapSet);
				if(!createSessionConfForm.getSecondaryDataSourceID().equals(0)){
					sessionConfData.setSecondaryDataSourceID(createSessionConfForm.getSecondaryDataSourceID());
				}else{
					sessionConfData.setSecondaryDataSourceID(null);
				}
				IStaffData staffData =getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				sessionBLManager.deleteOldMapping(sessionConfData);
				sessionBLManager.createNewMapping(sessionConfData);
				sessionBLManager.updateSessionConf(sessionConfData,staffData,ACTION_ALIAS);
			
			    
			    ActionMessages messages = new ActionMessages();
			    ActionMessage message = new ActionMessage("gxsession.update.success", "Session Configuration");
			    messages.add("information", message);
			    saveMessages(request,messages);
			    request.setAttribute("responseUrl","/viewSessionConf.do");
			    
				return mapping.findForward(SUCCESS_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information",new ActionMessage("gxsession.update.failure"));
				saveErrors(request,messages);
				
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("session.error.heading","updating");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);
				return mapping.findForward(FAILURE_FORWARD);
			} 
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("session.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}	
	}
}