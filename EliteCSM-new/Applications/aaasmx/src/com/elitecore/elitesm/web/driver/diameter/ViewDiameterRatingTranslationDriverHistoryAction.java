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
import com.elitecore.elitesm.datamanager.DataManagerException;
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
import com.elitecore.elitesm.web.driver.diameter.forms.UpdateDiameterRatingTranslationDriverForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewDiameterRatingTranslationDriverHistoryAction extends BaseWebAction{

	private static final String FAILURE_FORWARD = "failure";
	private static final String VIEW_HISTORY_FORWARD = "viewDiameterRatingTranslationDriverHistory";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());		
		try{
			UpdateDiameterRatingTranslationDriverForm updateDiameterRatingTranslationDriverForm=(UpdateDiameterRatingTranslationDriverForm)form;
			if("view".equals(updateDiameterRatingTranslationDriverForm.getAction())){
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}
			 
			DriverBLManager driverBlManager = new DriverBLManager();
			
			CrestelRatingDriverData crestelRatingDriverData= driverBlManager.getCrestelRatingDriverData(updateDiameterRatingTranslationDriverForm.getDriverInstanceId());
			
			DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(updateDiameterRatingTranslationDriverForm.getDriverInstanceId());
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			convertBeanToForm(updateDiameterRatingTranslationDriverForm,driverInstanceData,crestelRatingDriverData);

			TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
			List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.CRESTEL_RATING, TranslationMappingConfigConstants.DIAMETER);
			updateDiameterRatingTranslationDriverForm.setTranslationMappingConfDataList(translationMappingConfDataList);
				
			updateDiameterRatingTranslationDriverForm.setTranslationMappingConfDataList(translationMappingConfDataList);
			
			HistoryBLManager historyBlManager= new HistoryBLManager();
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			String driverInstanceId = strDriverInstanceId;
			if(driverInstanceId == null){
				driverInstanceId = updateDiameterRatingTranslationDriverForm.getDriverInstanceId();
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
			
			request.setAttribute("diameterRatingTranslationData", crestelRatingDriverData);
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


	private void convertBeanToForm(UpdateDiameterRatingTranslationDriverForm updateDiameterRatingTranslationDriverForm,
			DriverInstanceData driverInstanceData,
			CrestelRatingDriverData createRatingDriverData) {
		
		updateDiameterRatingTranslationDriverForm.setTranslationMapConfigId(createRatingDriverData.getTransMapConfId());
		updateDiameterRatingTranslationDriverForm.setSelectedTranslationMappingConfData(createRatingDriverData.getTranslationMappingConfData());
		updateDiameterRatingTranslationDriverForm.setInstanceNumber(createRatingDriverData.getInstanceNumber());
		
		updateDiameterRatingTranslationDriverForm.setDriverInstanceName(driverInstanceData.getName());
		updateDiameterRatingTranslationDriverForm.setDriverInstanceDesp(driverInstanceData.getDescription());
		updateDiameterRatingTranslationDriverForm.setDriverRelatedId(String.valueOf(driverInstanceData.getDriverInstanceId()));
		updateDiameterRatingTranslationDriverForm.setAuditUId(driverInstanceData.getAuditUId());
	}
}