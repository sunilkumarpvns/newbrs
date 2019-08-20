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
import com.elitecore.elitesm.web.driver.radius.forms.CrestelOCSv2DriverForm;

public class CreateCrestelOCSv2DriverAction extends BaseWebAction{
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	private static final String CRESTEL_OCSv2_DRIVER = "cCrestelOCSv2Driver"; 
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response){
		Logger.logInfo(MODULE, "Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			CrestelOCSv2DriverForm crestelOCSv2DriverForm = (CrestelOCSv2DriverForm) form;
			if("create".equals(crestelOCSv2DriverForm.getAction())){
				DriverBLManager driverBlManager = new DriverBLManager();
				String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				DriverInstanceData driverInstanceData = new DriverInstanceData();
				CrestelChargingDriverData crestleChargingDriverData =  new CrestelChargingDriverData();
				
				convertFromFormToBean(crestelOCSv2DriverForm, crestleChargingDriverData, driverInstanceData);
				crestleChargingDriverData.setJndiPropValMapList(getSelectedCrestelChargingDriverPropsDataSet(request));
				driverInstanceData.setLastModifiedByStaffId(currentUser);
				driverInstanceData.setCreatedByStaffId(currentUser);
				
				driverBlManager.createCrestelChargingDriver(crestleChargingDriverData, driverInstanceData ,staffData);
				
				request.setAttribute("responseUrl", "/initSearchDriver");
				ActionMessage message = new ActionMessage("driver.create.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS);
			}else{
				if(crestelOCSv2DriverForm.getDriverRelatedId() == null || crestelOCSv2DriverForm.getDriverInstanceName() == null || crestelOCSv2DriverForm.getDriverInstanceDesp() == null){
					crestelOCSv2DriverForm.setDriverInstanceName((String)request.getAttribute("instanceName"));
					crestelOCSv2DriverForm.setDriverInstanceDesp((String)request.getAttribute("desp"));
					Long driverId =(Long)request.getAttribute("driverId");
					crestelOCSv2DriverForm.setDriverRelatedId(driverId.toString());
				}
				
				TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
				List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.CRESTEL_OCSv2, TranslationMappingConfigConstants.RADIUS);
				crestelOCSv2DriverForm.setTranslationMappingConfDataList(translationMappingConfDataList);
				
				crestelOCSv2DriverForm.setDefaultChargingDriverPropsDataList(getDefaultChargingDriverPropsDataList());
				
				return mapping.findForward(CRESTEL_OCSv2_DRIVER);
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
	
	private List<CrestelChargingDriverPropsData> getDefaultChargingDriverPropsDataList(){
		List<CrestelChargingDriverPropsData> defaultChargingDriverPropsDataList = new ArrayList<CrestelChargingDriverPropsData>();
		String[] attributeName = {"java.naming.factory.initial","java.naming.factory.url.pkgs","java.naming.provider.url","response.time.calc.required"};
		String[] value = {"org.jnp.interfaces.NamingContextFactory","org.jboss.naming:org.jnp.interfaces","127.0.0.1:4099","true"};
		for(int index=0; index<attributeName.length; index++){
			CrestelChargingDriverPropsData chargingDriverPropsData = new CrestelChargingDriverPropsData();
			chargingDriverPropsData.setName(attributeName[index]);
			chargingDriverPropsData.setValue(value[index]);
			defaultChargingDriverPropsDataList.add(chargingDriverPropsData);
		}
		return defaultChargingDriverPropsDataList;
	}
	
	private List<CrestelChargingDriverPropsData> getSelectedCrestelChargingDriverPropsDataSet(HttpServletRequest request){
		List<CrestelChargingDriverPropsData> crestelChargingDriverPropsSet = new ArrayList<CrestelChargingDriverPropsData>();
		String jndiProperty[] = request.getParameterValues("jndiProperty");
		String jndiValue[] = request.getParameterValues("jndiPropertyValue");
		if(jndiProperty != null){
			for(int index=0; index<jndiProperty.length; index++){
				CrestelChargingDriverPropsData chargingDriverPropsData = new CrestelChargingDriverPropsData();
				chargingDriverPropsData.setName(jndiProperty[index]);
				chargingDriverPropsData.setValue(jndiValue[index]);
				crestelChargingDriverPropsSet.add(chargingDriverPropsData);
			}
		}
		return crestelChargingDriverPropsSet;
	}
	
	private void convertFromFormToBean(CrestelOCSv2DriverForm form,CrestelChargingDriverData data,DriverInstanceData driverInstanceData) {
		data.setTransMapConfId(form.getTranslationMapConfigId());
		data.setInstanceNumber(form.getInstanceNumber());
		// driverInstanceRelated
		driverInstanceData.setName(form.getDriverInstanceName());
		driverInstanceData.setDescription(form.getDriverInstanceDesp());
		driverInstanceData.setDriverTypeId(Long.parseLong(form.getDriverRelatedId()));
		driverInstanceData.setCreateDate(getCurrentTimeStemp());
		driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());		
		driverInstanceData.setStatus("CST01");
		driverInstanceData.setAuditUId(form.getAuditUId());
	}
}
