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
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ASMData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.IASMData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerDBConfiguration;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.sessionmanager.forms.SearchASMForm;

public class PurgeAllSessionAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	//private static final String RETURN_FORWARD = "/searchASM.do";
	private static final String LIST_FORWARD = "searchASM";
	private static final String ACTION_ALIAS = ConfigConstant.PURGE_ALL_SESSION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		String sessionManagerId=null;
		if(checkAccess(request, ACTION_ALIAS)){		
			try{

				SearchASMForm searchASMForm = (SearchASMForm)form;
				SessionManagerBLManager asmBLManager = new SessionManagerBLManager();

				String strSmInstancId = request.getParameter("sminstanceid");
				Logger.logDebug(MODULE,"strSmInstancId: "+strSmInstancId);
				
				if(strSmInstancId!=null){
					sessionManagerId = strSmInstancId;
				}else{
					sessionManagerId = searchASMForm.getSessionManagerId();
				}
				searchASMForm.setSessionManagerId(sessionManagerId);
				
				
				
				String strUserName = request.getParameter("strUserName");
				searchASMForm.setUserName(strUserName);
				String nasIp = request.getParameter("nasIp");
				searchASMForm.setNasIpAddress(nasIp);
				String framedIp = request.getParameter("framedIp");
				searchASMForm.setFramedIpAddress(framedIp);
				String groupName  = request.getParameter("groupName");
				searchASMForm.setGroupName(groupName);
				String idletime = request.getParameter("idleTime");
				searchASMForm.setIdleTime(idletime);
				String action = request.getParameter("action");
				searchASMForm.setAction(action);
				String groupByCriteria = request.getParameter("groupByCriteria");
				searchASMForm.setGroupbyCriteria(groupByCriteria);
				SessionManagerDBConfiguration sessionManagementDBConfiguration = asmBLManager.getSessionManagerDBConfiguration(sessionManagerId);
				ISessionManagerInstanceData sessionManagerInstanceData = asmBLManager.getSessionManagerDataById(sessionManagerId);
				
				request.setAttribute("searchASMForm", searchASMForm);
				request.setAttribute("sessionManagerInstanceData",sessionManagerInstanceData);

				int requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNumber")));
				if(requiredPageNo == 0)
					requiredPageNo = 1;

				IASMData asmSearchData = new ASMData();

				if(strUserName !=null && !(strUserName.equalsIgnoreCase("")))
					asmSearchData.setUserName(strUserName);
				else
					asmSearchData.setUserName("");

				if(nasIp !=null && !(nasIp.equalsIgnoreCase("")))
					asmSearchData.setNasIpAddress(nasIp);
				else
					asmSearchData.setNasIpAddress("");
				
				if(framedIp !=null && !(framedIp.equalsIgnoreCase("")))
					asmSearchData.setFramedIpAddress(framedIp);
				else
					asmSearchData.setFramedIpAddress("");

				if(groupName !=null && !(groupName.equalsIgnoreCase("")))
					asmSearchData.setGroupName(groupName);
				else
					asmSearchData.setGroupName("");
				if(idletime !=null && !(idletime.equalsIgnoreCase("")))
					asmSearchData.setIdleTime(idletime);
				else
					asmSearchData.setIdleTime("");

				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;

				asmBLManager.purgeAllSession(sessionManagementDBConfiguration,staffData);
				PageList pageList = asmBLManager.search(asmSearchData,requiredPageNo,10,sessionManagementDBConfiguration);
				searchASMForm.setUserName(strUserName);

				if(pageList!=null){
					searchASMForm.setPageNumber(pageList.getCurrentPage());
					searchASMForm.setTotalPages(pageList.getTotalPages());
					searchASMForm.setTotalRecords(pageList.getTotalItems());								
					// this is List without Group BY
					searchASMForm.setAsmList(pageList.getListData());

				}
				request.setAttribute("responseUrl","/initSearchASM.do?sminstanceid="+sessionManagerId);
				ActionMessage message = new ActionMessage("servermgr.asm.purge.all.session.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS_FORWARD);

			}
			catch(DataManagerException dnisExp){
				Logger.logError(MODULE, "Error during data Manager operation,reason : " + dnisExp.getMessage());
				Logger.logTrace(MODULE,dnisExp);
				request.setAttribute("responseUrl", "/searchASM.do?sminstanceid="+sessionManagerId);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dnisExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("asm.purgeall.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			catch (Exception managerExp) {
				Logger.logError(MODULE, "Error during data Manager operation,reason : " + managerExp.getMessage());
				Logger.logTrace(MODULE,managerExp);
				request.setAttribute("responseUrl", "/searchASM.do?sminstanceid="+sessionManagerId);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("asm.purgeall.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			return mapping.findForward(FAILURE_FORWARD);
		}
		else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

}
