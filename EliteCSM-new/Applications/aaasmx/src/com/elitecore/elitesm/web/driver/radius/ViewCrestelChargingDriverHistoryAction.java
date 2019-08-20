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
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data.CrestelChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.UpdateCrestelChargingDriverForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewCrestelChargingDriverHistoryAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";
	private static final String VIEW_HISTORY_FORWARD = "viewCrestelChargingDriverHistory";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());		
		try{
			UpdateCrestelChargingDriverForm updateCrestelChargingDriverForm = (UpdateCrestelChargingDriverForm)form;
			if("view".equals(updateCrestelChargingDriverForm.getAction())) {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}

			DriverBLManager driverBlManager = new DriverBLManager();
			
			CrestelChargingDriverData crestelChargingDriverData = driverBlManager.getCrestelChargingDriverData(updateCrestelChargingDriverForm.getDriverInstanceId());
			
			DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(updateCrestelChargingDriverForm.getDriverInstanceId());
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			convertBeanToForm(updateCrestelChargingDriverForm,driverInstanceData,crestelChargingDriverData);
                				
			TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
			List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.CRESTEL_RATING, TranslationMappingConfigConstants.RADIUS);
			updateCrestelChargingDriverForm.setTranslationMappingConfDataList(translationMappingConfDataList);
			
			HistoryBLManager historyBlManager= new HistoryBLManager();
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			String driverInstanceId = strDriverInstanceId;
			if(driverInstanceId == null){
				driverInstanceId = updateCrestelChargingDriverForm.getDriverInstanceId();
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
			
			request.setAttribute("crestelChargingDriverData", crestelChargingDriverData);
			request.getSession().setAttribute("driverInstance",driverInstanceData);
				
			return mapping.findForward(VIEW_HISTORY_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(DataManagerException e){

			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}catch(Exception dme){
			dme.printStackTrace();
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,dme);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dme);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}		
	}


	private void convertBeanToForm(UpdateCrestelChargingDriverForm updateCrestelChargingDriverForm,DriverInstanceData driverInstanceData,CrestelChargingDriverData crestelChargingDriverData) {
		updateCrestelChargingDriverForm.setTranslationMapConfigId(crestelChargingDriverData.getTransMapConfId());
		updateCrestelChargingDriverForm.setSelectedTranslationMappingConfData(crestelChargingDriverData.getTranslationMappingConfData());
		updateCrestelChargingDriverForm.setInstanceNumber(crestelChargingDriverData.getInstanceNumber());
		updateCrestelChargingDriverForm.setAuditUId(driverInstanceData.getAuditUId());
		
		updateCrestelChargingDriverForm.setDriverInstanceName(driverInstanceData.getName());
		updateCrestelChargingDriverForm.setDriverInstanceDesp(driverInstanceData.getDescription());
		updateCrestelChargingDriverForm.setDriverRelatedId(String.valueOf(driverInstanceData.getDriverInstanceId()));	
		updateCrestelChargingDriverForm.setAuditUId(driverInstanceData.getAuditUId());
	}
}
