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
import com.elitecore.elitesm.datamanager.DataManagerException;
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
import com.elitecore.elitesm.web.driver.diameter.forms.UpdateDiameterRatingTranslationDriverForm;

public class UpdateDiameterRatingTranslationDriverAction extends BaseWebAction{

	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "viewDiameterRatingTranslationDriverInstance";
	private static final String INIT_UPDATE_FORWARD = "openUpdateDiameterRatingTranslationDriver";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;
	private static final String MODULE_NAME = "Diameter Rating Translation Driver";


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
			if(updateDiameterRatingTranslationDriverForm.getAction() != null && !"view".equals(updateDiameterRatingTranslationDriverForm.getAction())){
				CrestelRatingDriverData driverData = new CrestelRatingDriverData();
				DriverInstanceData driverInstdata = new DriverInstanceData();
				
				convertFormToBean(updateDiameterRatingTranslationDriverForm,driverData,driverInstdata);
				driverInstdata.setLastModifiedByStaffId(currentUser);
				
				driverData.setJndiPropValMapList(getSelectedRatingDriverPropsDataList(request));
				driverInstanceData.setLastModifiedByStaffId(currentUser);
				
				staffData.setAuditId(driverInstanceData.getAuditUId());
				staffData.setAuditName(driverInstanceData.getName());
			
				driverBlManager.updateDiameterRatingTranslationDriverDataById(driverInstdata, driverData, staffData, MODULE_NAME);
				
				driverData=driverBlManager.getCrestelRatingDriverData(updateDiameterRatingTranslationDriverForm.getDriverInstanceId());
				driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(updateDiameterRatingTranslationDriverForm.getDriverInstanceId());
				convertBeanToForm(updateDiameterRatingTranslationDriverForm, driverInstanceData, driverData);
				
				updateDiameterRatingTranslationDriverForm.setAction(null);
				
				return mapping.findForward(UPDATE_FORWARD);
			
			}else{

				convertBeanToForm(updateDiameterRatingTranslationDriverForm,driverInstanceData,crestelRatingDriverData);
                /*
                 * fetch  translationMappingConfDataList 				
                 */
				
				TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
				List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.CRESTEL_RATING, TranslationMappingConfigConstants.DIAMETER);
				updateDiameterRatingTranslationDriverForm.setTranslationMappingConfDataList(translationMappingConfDataList);
				

				updateDiameterRatingTranslationDriverForm.setTranslationMappingConfDataList(translationMappingConfDataList);
				request.setAttribute("diameterRatingTranslationData", crestelRatingDriverData);
				request.getSession().setAttribute("driverInstance",driverInstanceData);
				
				return mapping.findForward(INIT_UPDATE_FORWARD);

			}
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


	private void convertFormToBean(UpdateDiameterRatingTranslationDriverForm form,CrestelRatingDriverData driverData,DriverInstanceData driverInstdata) {
		driverData.setTransMapConfId(form.getTranslationMapConfigId());
		driverData.setInstanceNumber(form.getInstanceNumber());
		// driver related 
		driverInstdata.setName(form.getDriverInstanceName());
		driverInstdata.setDescription(form.getDriverInstanceDesp());
		driverInstdata.setLastModifiedDate(getCurrentTimeStemp());
		driverInstdata.setDriverInstanceId(form.getDriverRelatedId());
		driverInstdata.setAuditUId(form.getAuditUId());
	}
}