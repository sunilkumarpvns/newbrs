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
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebServiceAuthDriverData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.UpdateWebServiceAuthDriverForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewWebServiceAuthDriverHistoryAction extends BaseWebAction{

	private static final String VIEW_HISTORY_FORWARD = "viewWebServiceAuthDriverHistory";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;
	private static final String MODULE=UpdateRadiusDBAuthDriverAction.class.getSimpleName();
	

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			UpdateWebServiceAuthDriverForm updateWebServiceAuthDriverForm=(UpdateWebServiceAuthDriverForm)form;
			if("view".equals(updateWebServiceAuthDriverForm.getAction())) {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}
			
			DriverBLManager driverBLManager = new DriverBLManager();

			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(updateWebServiceAuthDriverForm.getDriverInstanceId());

			WebServiceAuthDriverData webServiceAuthDriverData=driverBLManager.getWebServiceDriverByDriverInstanceId(updateWebServiceAuthDriverForm.getDriverInstanceId());
			
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

			updateWebServiceAuthDriverForm.setDriverInstanceId(webServiceAuthDriverData.getDriverInstanceId());
			updateWebServiceAuthDriverForm.setServiceAddress(webServiceAuthDriverData.getServiceAddress());
			updateWebServiceAuthDriverForm.setDriverInstanceName(driverInstanceData.getName());
			updateWebServiceAuthDriverForm.setDriverInstanceDesc(driverInstanceData.getDescription());
			updateWebServiceAuthDriverForm.setImsiAttribute(webServiceAuthDriverData.getImsiAttribute());
			updateWebServiceAuthDriverForm.setMaxQueryTimeoutCnt(webServiceAuthDriverData.getMaxQueryTimeoutCnt());
			updateWebServiceAuthDriverForm.setStatusChkDuration(webServiceAuthDriverData.getStatusChkDuration());
			updateWebServiceAuthDriverForm.setWebMethodKeyList(driverBLManager.getWebMethodKeyDataList());
			updateWebServiceAuthDriverForm.setLogicalNameList(driverBLManager.getLogicalNameValuePoolList());
			updateWebServiceAuthDriverForm.setUserIdentityAttributes(webServiceAuthDriverData.getUserIdentityAttributes());
			updateWebServiceAuthDriverForm.setAuditUId(driverInstanceData.getAuditUId());
			request.getSession().setAttribute("webservicedata",webServiceAuthDriverData);
			request.getSession().setAttribute("driverInstanceData", driverInstanceData);
			
			List<String> logicalNameMultipleAllowList = driverBLManager.getLogicalValueDriverRelList(driverInstanceData.getDriverTypeId());
			updateWebServiceAuthDriverForm.setLogicalNameMultipleAllowList(logicalNameMultipleAllowList);
				
			HistoryBLManager historyBlManager= new HistoryBLManager();
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			String driverInstanceId = strDriverInstanceId;
			if(driverInstanceId == null){
				driverInstanceId = updateWebServiceAuthDriverForm.getDriverInstanceId();
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
			
			request.setAttribute("logicalNameData", driverBLManager.getLogicalNameJson(driverBLManager.getLogicalNameValuePoolList(),logicalNameMultipleAllowList));
			request.getSession().setAttribute("webServiceAuthDriverFieldMapList", webServiceAuthDriverData.getWebMethodKeyDataList());
			
			return mapping.findForward(VIEW_HISTORY_FORWARD);
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
