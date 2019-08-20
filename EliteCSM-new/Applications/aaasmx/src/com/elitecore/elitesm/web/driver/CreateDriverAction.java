package com.elitecore.elitesm.web.driver;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.DriverTypeConstants;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.forms.CreateDriverForm;

public class CreateDriverAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "CreateDriver";
//	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	private static final String MODULE = "CREATE_DRIVER_ACTION";
	
	private static final String RADIUS_WEBSERVICEAUTH_FORWARD = "createWebServiceAuthDriver";
	private static final String RADIUS_DBAUTH_FORWARD = "createRadiusDBAuthDriver";
	private static final String RADIUS_HTTP_AUTH_FORWARD = "createRadiusHttpAuthDriver";
	private static final String RADIUS_USERFILEAUTH_FORWARD = "createRadiusUserFileAuthDriver";
	private static final String RADIUS_LDAPAUTH_FORWARD = "createRadiusLDAPAuthDriver";
	private static final String RADIUS_DBACCT_FORWARD = "createRadiusDBAcctDriver";
	private static final String RADIUS_DETAILLOCALACCT_FORWARD = "createRadiusDetailLocalAcctDriver";
	private static final String RADIUS_CLASSICCSVCCT_FORWARD = "createRadiusClassicCSVAcctDriver";
	private static final String RADIUS_MAP_GWAUTH_FORWARD = "createRadiusMappingGatewayAuthDriver";
	private static final String PARLEY_CHARGING_GATEWAY_FORWARD = "createRadiusParleyChargingDriver";
	private static final String DIAMETER_CHARGING_FORWARD = "createRadiusDCDriver";
	private static final String CRESTEL_CHARGING_FORWARD = "createCrestelChargingDriver";
	
	private static final String DIAMETER_DBAUTH_FORWARD = "createDiameterDBAuthDriver";
	private static final String DIAMETER_USERFILEAUTH_FORWARD = "createDiameterUserFileAuthDriver";
	private static final String DIAMETER_LDAPAUTH_FORWARD = "createDiameterLDAPAuthDriver";
	private static final String DIAMETER_DBACCT_FORWARD = "createDiameterDBAcctDriver";
	private static final String DIAMETER_DETAILLOCALACCT_FORWARD = "createDiameterDetailLocalAcctDriver";
	private static final String DIAMETER_CLASSICCSVCCT_FORWARD = "createDiameterClassicCSVAcctDriver";
	private static final String DIAMETER_RATING_TRANSLATION_FORWARD = "createDiameterRatingTranslationDriver";
	private static final String DIAMETER_MAP_GWAUTH_FORWARD = "createDiameterMappingGatewayAuthDriver";
	private static final String DIAMETER_HTTP_AUTH_FORWARD = "createDiameterHttpAuthDriver";
	private static final String CRESTEL_OCSV2_DRIVER = "createCrestelOCSv2Driver";
	private static final String DIAMETER_CRESTEL_OCSv2_DRIVER = "createDiameterCrestelOCSv2Driver";
	private static final String RAD_HSS_AUTH_DRIVER="createRadiusHSSAuthDriver";
	private static final String DIA_HSS_AUTH_DRIVER="createDiameterHSSAuthDriver";


	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		CreateDriverForm driverForm = (CreateDriverForm)form;
		DriverBLManager driverBLManager = new DriverBLManager();
		DriverInstanceData driverInstanceData = new DriverInstanceData();
		
		String action = driverForm.getAction();
	
		if(action != null){
			if(action.equals("next")){
				// setting attributes in request parametre
				request.setAttribute("desp",driverForm.getDescription());
				request.setAttribute("driverId", driverForm.getSelecteDriver());				
				request.setAttribute("instanceName", driverForm.getName());
						
			}				
		}
		if(driverForm.getSelecteDriver() != null){
			if(driverForm.getSelecteDriver().longValue()==DriverTypeConstants.RADIUS_DB_AUTH_DRIVER){
				List feildMapList = new ArrayList();
				request.getSession().setAttribute("dbfeildMapList",feildMapList);
				return mapping.findForward(RADIUS_DBAUTH_FORWARD);
			}else if(driverForm.getSelecteDriver().longValue()==DriverTypeConstants.DIAMETER_DB_AUTH_DRIVER){
				List feildMapList = new ArrayList();
				request.getSession().setAttribute("dbfeildMapList",feildMapList);
				return mapping.findForward(DIAMETER_DBAUTH_FORWARD);
			}else if(driverForm.getSelecteDriver().longValue()==DriverTypeConstants.RADIUS_USERFILE_AUTH_DRIVER){
				return mapping.findForward(RADIUS_USERFILEAUTH_FORWARD);
			}else if(driverForm.getSelecteDriver().longValue()==DriverTypeConstants.RADIUS_AIRCEL_WEBSERVICE_AUTH_DRIVER){
				return mapping.findForward(RADIUS_WEBSERVICEAUTH_FORWARD);
			}else if(driverForm.getSelecteDriver().longValue()==DriverTypeConstants.DIAMETER_USERFILE_AUTH_DRIVER){
				return mapping.findForward(DIAMETER_USERFILEAUTH_FORWARD);
			}else if(driverForm.getSelecteDriver().longValue()==DriverTypeConstants.RADIUS_LDAP_AUTH_DRIVER){
				List ldapField = new ArrayList();
				request.getSession().setAttribute("ldapfeildMapList",ldapField);
				return mapping.findForward(RADIUS_LDAPAUTH_FORWARD);
			}else if(driverForm.getSelecteDriver().longValue()==DriverTypeConstants.DIAMETER_LDAP_AUTH_DRIVER){
				List ldapField = new ArrayList();
				request.getSession().setAttribute("ldapfeildMapList",ldapField);
				return mapping.findForward(DIAMETER_LDAPAUTH_FORWARD);
			}else if (driverForm.getSelecteDriver().longValue()==DriverTypeConstants.RADIUS_DB_ACCT_DRIVER){
				List dbAcctFeildMapList = new ArrayList();
				request.getSession().setAttribute("dbacctFeildMapList", dbAcctFeildMapList);
				return mapping.findForward(RADIUS_DBACCT_FORWARD);
			}else if (driverForm.getSelecteDriver().longValue()==DriverTypeConstants.DIAMETER_DB_ACCT_DRIVER){
				List dbAcctFeildMapList = new ArrayList();
				request.getSession().setAttribute("dbacctFeildMapList", dbAcctFeildMapList);
				return mapping.findForward(DIAMETER_DBACCT_FORWARD);
			}else if (driverForm.getSelecteDriver().longValue()==DriverTypeConstants.RADIUS_DETAILLOCAL_ACCT_DRIVER){
				List detailLocalAcctList = new ArrayList();
				request.getSession().setAttribute("detailLocalAcctList", detailLocalAcctList);
				return mapping.findForward(RADIUS_DETAILLOCALACCT_FORWARD);
			}else if (driverForm.getSelecteDriver().longValue()==DriverTypeConstants.DIAMETER_DETAILLOCAL_ACCT_DRIVER){
				List detailLocalAcctList = new ArrayList();
				request.getSession().setAttribute("detailLocalAcctList", detailLocalAcctList);
				return mapping.findForward(DIAMETER_DETAILLOCALACCT_FORWARD);
			}else if (driverForm.getSelecteDriver().longValue()==DriverTypeConstants.RADIUS_CLASSICCSV_ACCT_DRIVER || driverForm.getSelecteDriver().longValue()==DriverTypeConstants.RM_CLASSICCSV_ACCT_DRIVER){
				List classicCSVFeildMapList = new ArrayList();
				List classicCSVStipMapList = new ArrayList();
				request.getSession().setAttribute("classicCsvFeildMap", classicCSVFeildMapList);
				request.getSession().setAttribute("classicCsvStipMap", classicCSVStipMapList);
				return mapping.findForward(RADIUS_CLASSICCSVCCT_FORWARD);
			}else if (driverForm.getSelecteDriver().longValue()==DriverTypeConstants.DIAMETER_CLASSICCSV_ACCT_DRIVER){
				List classicCSVFeildMapList = new ArrayList();
				List classicCSVStipMapList = new ArrayList();
				request.getSession().setAttribute("classicCsvFeildMap", classicCSVFeildMapList);
				request.getSession().setAttribute("classicCsvStipMap", classicCSVStipMapList);
				return mapping.findForward(DIAMETER_CLASSICCSVCCT_FORWARD);
			}else if (driverForm.getSelecteDriver().longValue()==DriverTypeConstants.DIAMETER_RATING_TRANSLATION_DRIVER){
				return mapping.findForward(DIAMETER_RATING_TRANSLATION_FORWARD);
			}else if (driverForm.getSelecteDriver().longValue()==DriverTypeConstants.RADIUS_MAPGATEWAY_AUTH_DRIVER){
				return mapping.findForward(RADIUS_MAP_GWAUTH_FORWARD);
			}else if (driverForm.getSelecteDriver().longValue()==DriverTypeConstants.PARLEY_CHARGING_DRIVER){
				return mapping.findForward(PARLEY_CHARGING_GATEWAY_FORWARD);
			}else if (driverForm.getSelecteDriver().longValue()==DriverTypeConstants.DIAMETER_CHARGING_DRIVER){
				return mapping.findForward(DIAMETER_CHARGING_FORWARD);
			}else if (driverForm.getSelecteDriver().longValue()==DriverTypeConstants.CRESTEL_CHARGING_DRIVER){
				return mapping.findForward(CRESTEL_CHARGING_FORWARD);
			}else if (driverForm.getSelecteDriver().longValue()==DriverTypeConstants.RADIUS_HTTP_AUTH_DRIVER){
				return mapping.findForward(RADIUS_HTTP_AUTH_FORWARD);
			} else if (driverForm.getSelecteDriver().longValue()==DriverTypeConstants.DIAMETER_MAP_GWAUTH_FORWARD){
				return mapping.findForward(DIAMETER_MAP_GWAUTH_FORWARD);
			}else if (driverForm.getSelecteDriver().longValue()==DriverTypeConstants.DIAMETER_HTTP_AUTH_FORWARD){
				return mapping.findForward(DIAMETER_HTTP_AUTH_FORWARD);
			}else if (driverForm.getSelecteDriver().longValue()==DriverTypeConstants.CRESTEL_OCSv2_DRIVER){
				return mapping.findForward(CRESTEL_OCSV2_DRIVER);
			}else if(driverForm.getSelecteDriver().longValue() == DriverTypeConstants.DIAMETER_CRESTEL_OCSv2_DRIVER) {
				return mapping.findForward(DIAMETER_CRESTEL_OCSv2_DRIVER);
			}else if(driverForm.getSelecteDriver().longValue() == DriverTypeConstants.RADIUS_HSS_AUTH_DRIVER){
				List feildMapList = new ArrayList();
				request.getSession().setAttribute("dbfeildMapList",feildMapList);
				return mapping.findForward(RAD_HSS_AUTH_DRIVER);
			}else if(driverForm.getSelecteDriver().longValue() == DriverTypeConstants.DIAMETER_HSS_AUTH_DRIVER){
				List feildMapList = new ArrayList();
				request.getSession().setAttribute("dbfeildMapList",feildMapList);
				return mapping.findForward(DIA_HSS_AUTH_DRIVER);
			}
		}
		return mapping.findForward(SUCCESS_FORWARD);
		
	}
	public void convertFromFormToBean(DriverInstanceData driverInstanceData , CreateDriverForm driverForm,HttpServletRequest request){
		
		driverInstanceData.setName(driverForm.getName());
		driverInstanceData.setDescription(driverForm.getDescription());
		driverInstanceData.setDriverTypeId(driverForm.getSelecteDriver());
		String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
		driverInstanceData.setLastModifiedByStaffId(currentUser);
		driverInstanceData.setCreatedByStaffId(currentUser);
		driverInstanceData.setStatus("CST01");
		driverInstanceData.setCreateDate(getCurrentTimeStemp());
		driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());			
	}
}