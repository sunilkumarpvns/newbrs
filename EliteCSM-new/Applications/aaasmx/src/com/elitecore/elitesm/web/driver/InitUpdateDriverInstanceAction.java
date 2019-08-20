package com.elitecore.elitesm.web.driver;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.util.constants.DriverTypeConstants;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.driver.forms.UpdateDriverInstanceForm;

public class InitUpdateDriverInstanceAction extends BaseWebAction{


	private static final String SUCCESS_FORWARD = "InitUpdateDriverInstance";
	private static final String FAILURE_FORWARD = "failure";
	//		private static final String ACTION_ALIAS = "INIT_UPDATE_DRIVER_INSTANCE";
	private static final String RADIUS_DBAUTH_FORWARD = "radiusDBAuthDriver";
	private static final String RADIUS_HTTPAUTH_FORWARD = "radiusHttpAuthDriver";
	private static final String RADIUS_USERFILEAUTH_FORWARD = "radiusUserFileAuthDriver";
	private static final String RADIUS_WEBSERVICEAUTH_FORWARD = "radiusWebServiceAuthDriver";
	private static final String RADIUS_LDAPAUTH_FORWARD = "radiusLDAPAuthDriver";
	private static final String RADIUS_DBACCT_FORWARD = "radiusDBAcctDriver";
	private static final String RADIUS_DETAILLOCALACCT_FORWARD = "radiusDetailLocalAcctDriver";
	private static final String RADIUS_CLASSICCSVCCT_FORWARD = "radiusClassicCSVAcctDriver";
	private static final String RADIUS_MAPGWAUTH_FORWARD = "radiusMappingGWAuthDriver";
	private static final String PARLEY_CHARGING_GATEWAY_FORWARD = "radiusMappingPCDriver";
	private static final String DIAMETER_CHARGING_FORWARD = "radiusDCDriver";
	private static final String CRESTEL_CHARGING_FORWARD = "crestelChargingDriver";
	private static final String RADIUS_HSS_AUTH_FORWARD="radiusHssAuthDriver";
	
	private static final String DIAMETER_DBAUTH_FORWARD = "diameterDBAuthDriver";
	private static final String DIAMETER_USERFILEAUTH_FORWARD = "diameterUserFileAuthDriver";
	private static final String DIAMETER_LDAPAUTH_FORWARD = "diameterLDAPAuthDriver";
	private static final String DIAMETER_DBACCT_FORWARD = "diameterDBAcctDriver";
	private static final String DIAMETER_DETAILLOCALACCT_FORWARD = "diameterDetailLocalAcctDriver";
	private static final String DIAMETER_CLASSICCSVCCT_FORWARD = "diameterClassicCSVAcctDriver";
	private static final String DIAMETER_RATING_TRANSLATION_FORWARD = "diameterRatingTranslationDriver";
	private static final String DIAMETER_MAPGWAUTH_FORWARD = "diameterMappingGWAuthDriver";
	private static final String DIAMETER_HTTPAUTH_FORWARD = "diameterHttpAuthDriver";
	private static final String CRESTEL_OCSv2_FORWARD = "crestelOCSv2Driver";
	private static final String DIAMETER_CRESTEL_OCSV2_Driver = "diameterCrestelOCSv2Driver";
	private static final String DIAMETER_HSS_AUTH_FORWARD="diameterHssAuthDriver";
	

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			UpdateDriverInstanceForm updateDriverInstanceForm = (UpdateDriverInstanceForm)form;


			String driverInstanceId = updateDriverInstanceForm.getDriverInstanceId();
			if (driverInstanceId == null) {
				driverInstanceId = request.getParameter("driverInstanceId");
			}

			List<DriverInstanceData> driverList = (List)request.getSession().getAttribute("driverList");

			DriverBLManager blManager = new DriverBLManager();
			DriverInstanceData tempDriverInstanceData = blManager.getDriverInstanceByDriverInstanceId(driverInstanceId);

			String forwardPath = null;

			if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.RADIUS_DB_AUTH_DRIVER){			
				forwardPath = mapping.findForward(RADIUS_DBAUTH_FORWARD).getPath();
			}else if(tempDriverInstanceData.getDriverTypeId()  == DriverTypeConstants.RADIUS_USERFILE_AUTH_DRIVER){
				forwardPath = mapping.findForward(RADIUS_USERFILEAUTH_FORWARD).getPath();
			}else if(tempDriverInstanceData.getDriverTypeId()  == DriverTypeConstants.RADIUS_AIRCEL_WEBSERVICE_AUTH_DRIVER){
				forwardPath = mapping.findForward(RADIUS_WEBSERVICEAUTH_FORWARD).getPath();
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.RADIUS_LDAP_AUTH_DRIVER){				
				forwardPath = mapping.findForward(RADIUS_LDAPAUTH_FORWARD).getPath();				
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.RADIUS_MAPGATEWAY_AUTH_DRIVER){				
				forwardPath = mapping.findForward(RADIUS_MAPGWAUTH_FORWARD).getPath();				
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.RADIUS_DETAILLOCAL_ACCT_DRIVER){				
				forwardPath = mapping.findForward(RADIUS_DETAILLOCALACCT_FORWARD).getPath();
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.RADIUS_DB_ACCT_DRIVER){				
				forwardPath = mapping.findForward(RADIUS_DBACCT_FORWARD).getPath();
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.RADIUS_CLASSICCSV_ACCT_DRIVER || tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.RM_CLASSICCSV_ACCT_DRIVER){				
				forwardPath = mapping.findForward(RADIUS_CLASSICCSVCCT_FORWARD).getPath(); 
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.DIAMETER_DB_AUTH_DRIVER){			
				forwardPath = mapping.findForward(DIAMETER_DBAUTH_FORWARD).getPath();
			}else if(tempDriverInstanceData.getDriverTypeId()  == DriverTypeConstants.DIAMETER_USERFILE_AUTH_DRIVER){
				forwardPath = mapping.findForward(DIAMETER_USERFILEAUTH_FORWARD).getPath();
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.DIAMETER_LDAP_AUTH_DRIVER){				
				forwardPath = mapping.findForward(DIAMETER_LDAPAUTH_FORWARD).getPath();				
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.DIAMETER_DETAILLOCAL_ACCT_DRIVER){				
				forwardPath = mapping.findForward(DIAMETER_DETAILLOCALACCT_FORWARD).getPath();
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.DIAMETER_DB_ACCT_DRIVER){				
				forwardPath = mapping.findForward(DIAMETER_DBACCT_FORWARD).getPath();
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.DIAMETER_CLASSICCSV_ACCT_DRIVER){				
				forwardPath = mapping.findForward(DIAMETER_CLASSICCSVCCT_FORWARD).getPath(); 
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.DIAMETER_RATING_TRANSLATION_DRIVER){				
				forwardPath = mapping.findForward(DIAMETER_RATING_TRANSLATION_FORWARD).getPath(); 
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.PARLEY_CHARGING_DRIVER){				
				forwardPath = mapping.findForward(PARLEY_CHARGING_GATEWAY_FORWARD).getPath(); 
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.DIAMETER_CHARGING_DRIVER){				
				forwardPath = mapping.findForward(DIAMETER_CHARGING_FORWARD).getPath(); 
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.CRESTEL_CHARGING_DRIVER){				
				forwardPath = mapping.findForward(CRESTEL_CHARGING_FORWARD).getPath(); 
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.RADIUS_HTTP_AUTH_DRIVER){				
				forwardPath = mapping.findForward(RADIUS_HTTPAUTH_FORWARD).getPath(); 
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.DIAMETER_MAP_GWAUTH_FORWARD){				
				forwardPath = mapping.findForward(DIAMETER_MAPGWAUTH_FORWARD).getPath(); 
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.DIAMETER_HTTP_AUTH_FORWARD){				
				forwardPath = mapping.findForward(DIAMETER_HTTPAUTH_FORWARD).getPath(); 
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.CRESTEL_OCSv2_DRIVER){				
				forwardPath = mapping.findForward(CRESTEL_OCSv2_FORWARD).getPath(); 
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.DIAMETER_CRESTEL_OCSv2_DRIVER){
				forwardPath = mapping.findForward(DIAMETER_CRESTEL_OCSV2_Driver).getPath();
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.DIAMETER_HSS_AUTH_DRIVER){
				forwardPath = mapping.findForward(DIAMETER_HSS_AUTH_FORWARD).getPath();
			}else if(tempDriverInstanceData.getDriverTypeId() == DriverTypeConstants.RADIUS_HSS_AUTH_DRIVER){
				forwardPath = mapping.findForward(RADIUS_HSS_AUTH_FORWARD).getPath();
			}
			request.getSession().setAttribute("driverInstance",tempDriverInstanceData);

			if(forwardPath!=null){
				ActionForward actionForward = new ActionForward();
				actionForward.setPath(forwardPath + "?driverInstanceId=" + driverInstanceId);
				return actionForward;
			}
			return mapping.findForward(SUCCESS_FORWARD);
		}catch(Exception e){
				e.printStackTrace();
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
}
