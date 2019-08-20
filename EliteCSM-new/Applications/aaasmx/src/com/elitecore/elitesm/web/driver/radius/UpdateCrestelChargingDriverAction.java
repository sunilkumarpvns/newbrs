package com.elitecore.elitesm.web.driver.radius;

import java.util.ArrayList;
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
import com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data.CrestelChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data.CrestelChargingDriverPropsData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.UpdateCrestelChargingDriverForm;

public class UpdateCrestelChargingDriverAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "viewCrestelChargingDriverInstance";
	private static final String INIT_UPDATE_FORWARD = "openUpdateCrestelChargingDriver";
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
			if(updateCrestelChargingDriverForm.getAction() != null && !"view".equals(updateCrestelChargingDriverForm.getAction())){
				CrestelChargingDriverData driverData = new CrestelChargingDriverData();
				DriverInstanceData driverInstdata = new DriverInstanceData();
				convertFormToBean(updateCrestelChargingDriverForm,driverData,driverInstdata);
				driverInstdata.setLastModifiedByStaffId(currentUser);
				driverData.setJndiPropValMapList(getSelectedCrestelChargingDriverPropsDataSet(request));
				driverInstanceData.setLastModifiedByStaffId(currentUser);
				
				staffData.setAuditId(driverInstanceData.getAuditUId());
				staffData.setAuditName(driverInstanceData.getName());
				
				driverBlManager.updateCrestelChargingDriverDataById(driverInstdata,driverData,staffData);
				
				driverData=driverBlManager.getCrestelChargingDriverData(updateCrestelChargingDriverForm.getDriverInstanceId());
				driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(updateCrestelChargingDriverForm.getDriverInstanceId());
				convertBeanToForm(updateCrestelChargingDriverForm, driverInstanceData, driverData);
				updateCrestelChargingDriverForm.setAction(null);
				return mapping.findForward(UPDATE_FORWARD);
			}else{
			
				convertBeanToForm(updateCrestelChargingDriverForm,driverInstanceData,crestelChargingDriverData);
                				
				TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
				List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.CRESTEL_RATING, TranslationMappingConfigConstants.RADIUS);
				updateCrestelChargingDriverForm.setTranslationMappingConfDataList(translationMappingConfDataList);
				request.setAttribute("crestelChargingDriverData", crestelChargingDriverData);
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


	private void convertBeanToForm(UpdateCrestelChargingDriverForm updateCrestelChargingDriverForm,DriverInstanceData driverInstanceData,CrestelChargingDriverData crestelChargingDriverData) {
		updateCrestelChargingDriverForm.setTranslationMapConfigId(crestelChargingDriverData.getTransMapConfId());
		updateCrestelChargingDriverForm.setSelectedTranslationMappingConfData(crestelChargingDriverData.getTranslationMappingConfData());
		updateCrestelChargingDriverForm.setInstanceNumber(crestelChargingDriverData.getInstanceNumber());
		updateCrestelChargingDriverForm.setAuditUId(driverInstanceData.getAuditUId());
		
		updateCrestelChargingDriverForm.setDriverInstanceName(driverInstanceData.getName());
		updateCrestelChargingDriverForm.setDriverInstanceDesp(driverInstanceData.getDescription());
		updateCrestelChargingDriverForm.setDriverRelatedId(String.valueOf(driverInstanceData.getDriverInstanceId()));		
	}

	private void convertFormToBean(UpdateCrestelChargingDriverForm form,CrestelChargingDriverData driverData,DriverInstanceData driverInstdata) {
		driverData.setTransMapConfId(form.getTranslationMapConfigId());
		driverData.setInstanceNumber(form.getInstanceNumber());
		// driver related 
		driverInstdata.setName(form.getDriverInstanceName());
		driverInstdata.setDescription(form.getDriverInstanceDesp());
		driverInstdata.setLastModifiedDate(getCurrentTimeStemp());
		driverInstdata.setDriverInstanceId(form.getDriverRelatedId());
		driverInstdata.setAuditUId(form.getAuditUId());
	}
	
	private List<CrestelChargingDriverPropsData> getSelectedCrestelChargingDriverPropsDataSet(HttpServletRequest request){
		List<CrestelChargingDriverPropsData> crestelChargingDriverPropsDataSet = new ArrayList<CrestelChargingDriverPropsData>();
		String jndiProperty[] = request.getParameterValues("jndiProperty");
		String jndiValue[] = request.getParameterValues("jndiPropertyValue");
		if(jndiProperty != null){
			for(int index=0; index<jndiProperty.length; index++){
				CrestelChargingDriverPropsData chargingDriverPropsData = new CrestelChargingDriverPropsData();
				chargingDriverPropsData.setName(jndiProperty[index]);
				chargingDriverPropsData.setValue(jndiValue[index]);
				crestelChargingDriverPropsDataSet.add(chargingDriverPropsData);
			}
		}
		return crestelChargingDriverPropsDataSet;
	}
}
