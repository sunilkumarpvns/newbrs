package com.elitecore.elitesm.web.driver.radius;

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
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.UpdateRadiusHttpAuthDriverForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewRadiusHttpAuthDriverHistoryAction extends BaseWebAction {
	private static final String HTTP_FORWARD = "viewRadiusHttpAuthDriverHistory";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String MODULE=ViewRadiusHttpAuthDriverHistoryAction.class.getSimpleName();
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			UpdateRadiusHttpAuthDriverForm updateRadiusHttpAuthDriverForm = (UpdateRadiusHttpAuthDriverForm)form;
			
			if("view".equals(updateRadiusHttpAuthDriverForm.getAction())) {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, ACTION_ALIAS);
			}
			
			DriverBLManager driverBLManager = new DriverBLManager();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(updateRadiusHttpAuthDriverForm.getDriverInstanceId());
			HttpAuthDriverData httpAuthDriverData = driverBLManager.getHttpDriverByDriverInstanceId(updateRadiusHttpAuthDriverForm.getDriverInstanceId());

			List<LogicalNameValuePoolData> logicalNameList = driverBLManager.getLogicalNameValuePoolList();

			if(httpAuthDriverData != null){
				updateRadiusHttpAuthDriverForm.setHttpAuthDriverId(httpAuthDriverData.getHttpAuthDriverId());
			}
			request.getSession().setAttribute("driverInstanceData", driverInstanceData);

			updateRadiusHttpAuthDriverForm.setDriverInstanceName(driverInstanceData.getName());
			updateRadiusHttpAuthDriverForm.setDriverInstanceDesc(driverInstanceData.getDescription());

			updateRadiusHttpAuthDriverForm.setDriverInstanceId(httpAuthDriverData.getDriverInstanceId());
			updateRadiusHttpAuthDriverForm.setLogicalNameList(logicalNameList);
			updateRadiusHttpAuthDriverForm.setMaxQueryTimeoutCount(httpAuthDriverData.getMaxQueryTimeoutCount());
			updateRadiusHttpAuthDriverForm.setHttp_url(httpAuthDriverData.getHttp_url());
			updateRadiusHttpAuthDriverForm.setStatusCheckDuration(httpAuthDriverData.getStatusCheckDuration());
			updateRadiusHttpAuthDriverForm.setExpiryDateFormats(httpAuthDriverData.getExpiryDateFormat());
			updateRadiusHttpAuthDriverForm.setUserIdentityAttributes(httpAuthDriverData.getUserIdentityAttributes());
			//updateRadiusHttpAuthDriverForm.setAuditUId(driverInstanceData.getAuditUId());
			
			List<String> logicalNameMultipleAllowList = driverBLManager.getLogicalValueDriverRelList(driverInstanceData.getDriverTypeId());
			updateRadiusHttpAuthDriverForm.setLogicalNameMultipleAllowList(logicalNameMultipleAllowList);
			
			HistoryBLManager historyBlManager= new HistoryBLManager();
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			String driverInstanceId = strDriverInstanceId;
			if(driverInstanceId == null){
				driverInstanceId = updateRadiusHttpAuthDriverForm.getDriverInstanceId();
			}

			String strAuditUid = request.getParameter("auditUid");
			String strSytemAuditId=request.getParameter("systemAuditId");
			String name=request.getParameter("name");
			
			if(strSytemAuditId != null){
				request.setAttribute("systemAuditId", strSytemAuditId);
			}
			
			if(driverInstanceId != null && Strings.isNullOrBlank(strAuditUid) == false){
				
				staffData.setAuditName(driverInstanceData.getName());
				staffData.setAuditId(driverInstanceData.getAuditUId());
				
				List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
				
				request.setAttribute("name", name);
				request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
			}
			
			request.setAttribute("logicalNameData", driverBLManager.getLogicalNameJson(logicalNameList,logicalNameMultipleAllowList));
			request.getSession().setAttribute("httpAuthFieldMapList", httpAuthDriverData.getHttpFieldMapList());
			request.getSession().setAttribute("driverInstance",driverInstanceData);
			
			return mapping.findForward(HTTP_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}
}