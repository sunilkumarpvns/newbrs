package com.elitecore.elitesm.web.servermgr.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.ListNetServerInstanceForm;


public class ListNetServerInstanceAction extends BaseDictionaryAction{
	private static final String SUCCESS_FORWARD ="listNetServerInstance";
	private static final String FAILURE_FORWARD ="failure";
	private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.LIST_NET_SERVER_INSTANCE_ACTION;

	private static final String MODULE = "LIST SERVER ACTION";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of  "+getClass().getName());
		try {
			checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
			boolean bLicenseAlert = false;
			boolean bLicenseAlertForPopup = false;
			boolean bLicenseExpireAlert = false;
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			ListNetServerInstanceForm lstNetServerInstanceForm = (ListNetServerInstanceForm)form;
			List<NetServerInstanceData> serverList = netServerBLManager.getNetServerInstanceList();
			List netServerTypeList = netServerBLManager.getNetServerTypeList();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if(serverList != null) {
				for(NetServerInstanceData netServerInstanceData : serverList) {
					Integer licenseExpireDays = 0;
					if(netServerInstanceData != null) {
						licenseExpireDays = netServerInstanceData.getLicenseExpiryDays();
					}
					if(licenseExpireDays != null) {
						if(licenseExpireDays > 0 && licenseExpireDays <= 30)
							bLicenseAlert = true;
						if(licenseExpireDays < 3)
							bLicenseAlertForPopup = true;
						if(licenseExpireDays < 0)
							bLicenseExpireAlert = true;
					}
				}
			}

			lstNetServerInstanceForm.setListServer(serverList);
			request.setAttribute("netServerTypeList",netServerTypeList);
			request.setAttribute("netServerInstanceList", serverList);
			request.setAttribute("bLicenseAlert", String.valueOf(bLicenseAlert));
			request.setAttribute("bLicenseAlertForPopup", String.valueOf(bLicenseAlertForPopup));
			request.setAttribute("bLicenseExpireAlert", String.valueOf(bLicenseExpireAlert));
			doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
			return mapping.findForward(SUCCESS_FORWARD);
		}
		catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (DataManagerException hExp) {
			Logger.logError(MODULE,"Error during data Manager operation, reason : "+hExp.getMessage());
			Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
		}catch (Exception hExp) {
			Logger.logError(MODULE,"Error during data Manager operation, reason : "+hExp.getMessage());
			Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
		}
		Logger.logTrace(MODULE,"Returning error forward from "+getClass().getName());
		ActionMessage message = new ActionMessage("servermgr.list.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD);
	}
}
