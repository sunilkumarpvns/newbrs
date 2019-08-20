package com.elitecore.elitesm.web.driver.diameter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ratingdriver.data.CrestelRatingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ratingdriver.data.RatingDriverPropsData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.diameter.forms.DiameterCrestelOCSv2DriverForm;

public class UpdateDiameterCrestelOCSv2DriverAction extends BaseWebAction{
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String UPDATE_FORWARD = "updateDiameterCrestelOCSv2DriverInstance";
	private static final String MODULE_NAME = "Diameter Crestel OCS V2 Driver";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		Logger.logInfo(MODULE, "Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			DiameterCrestelOCSv2DriverForm diameterCrestelOCSv2DriverForm = (DiameterCrestelOCSv2DriverForm) form;
			DriverInstanceData driverInstanceData = null;
			CrestelRatingDriverData crestelRatingDriverData = null;
			DriverBLManager driverBLManager = new DriverBLManager();  

			
			if("update".equals(diameterCrestelOCSv2DriverForm.getAction())){
				driverInstanceData = new DriverInstanceData();
				crestelRatingDriverData = new CrestelRatingDriverData();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				convertFromFormToData(diameterCrestelOCSv2DriverForm, crestelRatingDriverData, driverInstanceData);
				crestelRatingDriverData.setJndiPropValMapList(getSelectedRatingDriverPropsDataList(request));
				
				staffData.setAuditId(driverInstanceData.getAuditUId());
				staffData.setAuditName(driverInstanceData.getName());
				
				driverBLManager.updateDiameterRatingTranslationDriverDataById(driverInstanceData, crestelRatingDriverData, staffData , MODULE_NAME);
				
				request.setAttribute("responseUrl", "/viewDriverInstance.do?driverInstanceId="+diameterCrestelOCSv2DriverForm.getDriverInstanceId());
				ActionMessage message = new ActionMessage("driver.update.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS);
			}else{
				driverInstanceData = (DriverInstanceData) request.getSession().getAttribute("driverInstance");
				driverInstanceData.setDriverTypeData(driverBLManager.getDriverTypeDataById(driverInstanceData.getDriverTypeId()));
				crestelRatingDriverData = driverBLManager.getCrestelRatingDriverData(driverInstanceData.getDriverInstanceId());
				
				TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
				List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.CRESTEL_OCSv2, TranslationMappingConfigConstants.DIAMETER);
				diameterCrestelOCSv2DriverForm.setTranslationMappingConfDataList(translationMappingConfDataList);
				
				convertFromBeanToForm(driverInstanceData, crestelRatingDriverData, diameterCrestelOCSv2DriverForm);
				
				request.setAttribute("driverInstanceData", driverInstanceData);
				request.setAttribute("crestelRatingDriverData", crestelRatingDriverData);
				
				return mapping.findForward(UPDATE_FORWARD);
			}
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
	private List<RatingDriverPropsData> getSelectedRatingDriverPropsDataList(HttpServletRequest request){
		List<RatingDriverPropsData> crestelRatingDriverPropsList = new ArrayList<RatingDriverPropsData>();
		String jndiProperty[] = request.getParameterValues("jndiProperty");
		String jndiValue[] = request.getParameterValues("jndiPropertyValue");
		if(jndiProperty != null){
			for(int index=0; index<jndiProperty.length; index++){
				RatingDriverPropsData ratingDriverPropsData = new RatingDriverPropsData();
				ratingDriverPropsData.setName(jndiProperty[index]);
				ratingDriverPropsData.setValue(jndiValue[index]);
				crestelRatingDriverPropsList.add(ratingDriverPropsData);
			}
		}
		return crestelRatingDriverPropsList;
	}

	private void convertFromFormToData(DiameterCrestelOCSv2DriverForm form,CrestelRatingDriverData data,DriverInstanceData driverInstanceData) {
		data.setTransMapConfId(form.getTranslationMapConfigId());
		data.setInstanceNumber(form.getInstanceNumber());
		// driverInstanceRelated
		driverInstanceData.setDriverInstanceId(form.getDriverInstanceId());
		driverInstanceData.setName(form.getDriverInstanceName());
		driverInstanceData.setDescription(form.getDriverInstanceDesp());
		driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());
		driverInstanceData.setAuditUId(form.getAuditUId());
	}
}
