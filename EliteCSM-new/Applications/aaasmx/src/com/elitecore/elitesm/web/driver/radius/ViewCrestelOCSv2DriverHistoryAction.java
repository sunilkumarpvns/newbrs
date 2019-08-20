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
import com.elitecore.elitesm.web.driver.radius.forms.CrestelOCSv2DriverForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewCrestelOCSv2DriverHistoryAction extends BaseWebAction{
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;
	private static final String VIEW_HISTORY_FORWARD = "viewCrestelOCSv2DriverHistory";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		Logger.logInfo(MODULE, "Enter execute method of "+getClass().getName());
		try{
			CrestelOCSv2DriverForm crestelOCSv2DriverForm = (CrestelOCSv2DriverForm) form;
			if("view".equals(crestelOCSv2DriverForm.getAction())) {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}
			DriverBLManager driverBlManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = null;
			CrestelChargingDriverData crestelChargingDriverData = null;
			
			crestelChargingDriverData = driverBlManager.getCrestelChargingDriverData(crestelOCSv2DriverForm.getDriverInstanceId());
			driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(crestelOCSv2DriverForm.getDriverInstanceId());
			driverInstanceData.setDriverTypeData(driverBlManager.getDriverTypeDataById(driverInstanceData.getDriverTypeId()));
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
			TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
			List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.CRESTEL_OCSv2, TranslationMappingConfigConstants.RADIUS);
			crestelOCSv2DriverForm.setTranslationMappingConfDataList(translationMappingConfDataList);
			
			//convertFromBeanToForm(crestelOCSv2DriverForm, driverInstanceData, crestelChargingDriverData);
			
			crestelOCSv2DriverForm.setDriverInstanceId(driverInstanceData.getDriverInstanceId());
			crestelOCSv2DriverForm.setDriverInstanceName(driverInstanceData.getName());
			crestelOCSv2DriverForm.setDriverInstanceDesp(driverInstanceData.getDescription());
			crestelOCSv2DriverForm.setDriverRelatedId(Long.toString(driverInstanceData.getDriverTypeId()));
			crestelOCSv2DriverForm.setAuditUId(driverInstanceData.getAuditUId());
			
			HistoryBLManager historyBlManager= new HistoryBLManager();
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			String driverInstanceId = strDriverInstanceId;
			if(driverInstanceId == null){
				driverInstanceId = crestelOCSv2DriverForm.getDriverInstanceId();
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
			
			request.setAttribute("driverInstanceData",driverInstanceData);
			request.setAttribute("crestelChargingDriverData", crestelChargingDriverData);
			return mapping.findForward(VIEW_HISTORY_FORWARD);
		
		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (Exception e) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
		}
		return mapping.findForward(FAILURE);
	}
	
	private void convertFromBeanToForm(CrestelOCSv2DriverForm form, DriverInstanceData driverInstanceData, CrestelChargingDriverData crestelChargingDriverData) {
		form.setDriverInstanceId(driverInstanceData.getDriverInstanceId());
		form.setDriverInstanceName(driverInstanceData.getName());
		form.setDriverInstanceDesp(driverInstanceData.getDescription());
		form.setDriverRelatedId(Long.toString(driverInstanceData.getDriverTypeId()));
		form.setAuditUId(driverInstanceData.getAuditUId());
		
		form.setTranslationMapConfigId(crestelChargingDriverData.getTransMapConfId());
		form.setInstanceNumber(crestelChargingDriverData.getInstanceNumber());
		
	}
}
