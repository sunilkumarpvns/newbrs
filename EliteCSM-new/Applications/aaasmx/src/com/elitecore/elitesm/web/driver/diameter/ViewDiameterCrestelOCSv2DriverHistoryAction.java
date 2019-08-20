package com.elitecore.elitesm.web.driver.diameter;

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
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ratingdriver.data.CrestelRatingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.diameter.forms.DiameterCrestelOCSv2DriverForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewDiameterCrestelOCSv2DriverHistoryAction extends BaseWebAction{
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String UPDATE_FORWARD = "viewCrestelOCSv2DriverHistory";

	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		Logger.logInfo(MODULE, "Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			DiameterCrestelOCSv2DriverForm diameterCrestelOCSv2DriverForm = (DiameterCrestelOCSv2DriverForm) form;
			DriverInstanceData driverInstanceData = null;
			CrestelRatingDriverData crestelRatingDriverData = null;
			DriverBLManager driverBLManager = new DriverBLManager();  

			driverInstanceData = (DriverInstanceData) request.getSession().getAttribute("driverInstance");
			driverInstanceData.setDriverTypeData(driverBLManager.getDriverTypeDataById(driverInstanceData.getDriverTypeId()));
			crestelRatingDriverData = driverBLManager.getCrestelRatingDriverData(driverInstanceData.getDriverInstanceId());
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
			List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.CRESTEL_OCSv2, TranslationMappingConfigConstants.DIAMETER);
			diameterCrestelOCSv2DriverForm.setTranslationMappingConfDataList(translationMappingConfDataList);
			
			diameterCrestelOCSv2DriverForm.setDriverInstanceId(driverInstanceData.getDriverInstanceId());
			diameterCrestelOCSv2DriverForm.setDriverInstanceDesp(driverInstanceData.getDescription());
			diameterCrestelOCSv2DriverForm.setDriverInstanceName(driverInstanceData.getName());
			diameterCrestelOCSv2DriverForm.setDriverRelatedId(Long.toString(driverInstanceData.getDriverTypeId()));
			diameterCrestelOCSv2DriverForm.setAuditUId(driverInstanceData.getAuditUId());
			
			HistoryBLManager historyBlManager= new HistoryBLManager();
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			String driverInstanceId = strDriverInstanceId;
			if(driverInstanceId == null){
				driverInstanceId = diameterCrestelOCSv2DriverForm.getDriverInstanceId();
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
				
			request.setAttribute("driverInstanceData", driverInstanceData);
			request.setAttribute("crestelRatingDriverData", crestelRatingDriverData);
				
			return mapping.findForward(UPDATE_FORWARD);
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
	
	private void convertFromBeanToForm(DriverInstanceData data,CrestelRatingDriverData crestelRatingDriverData,DiameterCrestelOCSv2DriverForm diameterCrestelOCSv2DriverForm){
		diameterCrestelOCSv2DriverForm.setDriverInstanceId(data.getDriverInstanceId());
		diameterCrestelOCSv2DriverForm.setDriverInstanceDesp(data.getDescription());
		diameterCrestelOCSv2DriverForm.setDriverInstanceName(data.getName());
		diameterCrestelOCSv2DriverForm.setDriverRelatedId(Long.toString(data.getDriverTypeId()));
		diameterCrestelOCSv2DriverForm.setAuditUId(data.getAuditUId());
		
		diameterCrestelOCSv2DriverForm.setTranslationMapConfigId(crestelRatingDriverData.getTransMapConfId());
		diameterCrestelOCSv2DriverForm.setInstanceNumber(crestelRatingDriverData.getInstanceNumber());
		
	}
}
