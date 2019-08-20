package com.elitecore.elitesm.web.driver.diameter;

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

public class CreateDiameterCrestelOCSv2DriverAction  extends BaseWebAction{
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	private static final String CREATE_FORWARD = "cCreateDiameterCrestelOCSv2Driver";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		Logger.logInfo(MODULE, "Enter execute Method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			DiameterCrestelOCSv2DriverForm diameterCrestelOCSv2DriverForm = (DiameterCrestelOCSv2DriverForm) form;
			if("create".equals(diameterCrestelOCSv2DriverForm.getAction())){
				DriverBLManager driverBlManager = new DriverBLManager();
				String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				DriverInstanceData driverInstanceData = new DriverInstanceData();
				CrestelRatingDriverData crestelRatingDriverData = new CrestelRatingDriverData();
				
				convertFromFormToData(diameterCrestelOCSv2DriverForm, crestelRatingDriverData, driverInstanceData);
				crestelRatingDriverData.setJndiPropValMapList(getSelectedRatingDriverPropsDataList(request));
				driverInstanceData.setLastModifiedByStaffId(currentUser);
				driverInstanceData.setCreatedByStaffId(currentUser);
				
				driverBlManager.createCrestelRatingDriver(crestelRatingDriverData, driverInstanceData,staffData);
				
				request.setAttribute("responseUrl", "/initSearchDriver");
				ActionMessage message = new ActionMessage("driver.create.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS);
			}else{
				if(diameterCrestelOCSv2DriverForm.getDriverRelatedId() == null || diameterCrestelOCSv2DriverForm.getDriverInstanceName() == null || diameterCrestelOCSv2DriverForm.getDriverInstanceDesp() == null){
					diameterCrestelOCSv2DriverForm.setDriverInstanceName((String)request.getAttribute("instanceName"));
					diameterCrestelOCSv2DriverForm.setDriverInstanceDesp((String)request.getAttribute("desp"));
					Long driverId =(Long)request.getAttribute("driverId");
					diameterCrestelOCSv2DriverForm.setDriverRelatedId(driverId.toString());
				}
				
				TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
				List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.CRESTEL_OCSv2, TranslationMappingConfigConstants.DIAMETER);
				diameterCrestelOCSv2DriverForm.setTranslationMappingConfDataList(translationMappingConfDataList);
				
				diameterCrestelOCSv2DriverForm.setDefaultRatingDriverPropsDataList(getDefaultRatingDriverPropsDataList());
				return mapping.findForward(CREATE_FORWARD);
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
	
	private List<RatingDriverPropsData> getDefaultRatingDriverPropsDataList(){
		List<RatingDriverPropsData> defaultRatingDriverPropsDataList = new ArrayList<RatingDriverPropsData>();
		String[] attributeName = {"java.naming.factory.initial","java.naming.factory.url.pkgs","java.naming.provider.url","response.time.calc.required"};
		String[] value = {"org.jnp.interfaces.NamingContextFactory","org.jboss.naming:org.jnp.interfaces","127.0.0.1:4099","true"};
		for(int index=0; index<attributeName.length; index++){
			RatingDriverPropsData ratingDriverPropsData = new RatingDriverPropsData();
			ratingDriverPropsData.setName(attributeName[index]);
			ratingDriverPropsData.setValue(value[index]);
			defaultRatingDriverPropsDataList.add(ratingDriverPropsData);
		}
		return defaultRatingDriverPropsDataList;
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
		driverInstanceData.setName(form.getDriverInstanceName());
		driverInstanceData.setDescription(form.getDriverInstanceDesp());
		driverInstanceData.setDriverTypeId(Long.parseLong(form.getDriverRelatedId()));
		driverInstanceData.setCreateDate(getCurrentTimeStemp());
		driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());		
		driverInstanceData.setStatus("CST01");
		driverInstanceData.setAuditUId(form.getAuditUId());
	}
}

