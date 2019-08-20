package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.gracepolicy.GracePolicyBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyBroadcastESIRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyExternalSystemRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyRMParamsData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicySMRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicySecDriverRelData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form.ViewRadiusServicePolicyForm;

public class ViewRadiusServicePolicyAdvanceDetailsAction extends BaseWebAction{
	private static final String VIEW_FORWARD = "viewAuthPolicy";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE="ViewAuthServicePolicy";
	private static final String ACTION_ALIAS=ConfigConstant.VIEW_RADIUS_SERVICE_POLICY;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){		
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			try{
				ViewRadiusServicePolicyForm viewRadiusServicePolicyForm = (ViewRadiusServicePolicyForm)form;

				ServicePolicyBLManager servicePolicyBLManager = new ServicePolicyBLManager();
				SessionManagerBLManager sessionManagerBLManager = new SessionManagerBLManager();
				GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();
				
				String strAuthPolicyID = request.getParameter("authpolicyid");
				String authPolicyID;
				
				if(strAuthPolicyID != null){
					authPolicyID = strAuthPolicyID;
				}else{
					authPolicyID=viewRadiusServicePolicyForm.getRadiusPolicyId();
				}
				
				if(Strings.isNullOrBlank(authPolicyID) == false){
					AuthPolicyInstData authPolicyInstData = new AuthPolicyInstData();
					
					authPolicyInstData.setAuthPolicyId(authPolicyID);
					
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					
					authPolicyInstData = servicePolicyBLManager.getAuthPolicyInstData(authPolicyInstData,staffData,actionAlias);
					
										
					List<AuthMethodTypeData> authMethodTypeDataList = servicePolicyBLManager.getSupportedAuthMethods(authPolicyInstData);
					DigestConfigInstanceData digestConfigInstanceData = servicePolicyBLManager.getHttpDigestInstanceData(authPolicyInstData);
					EAPConfigData eapConfigData = servicePolicyBLManager.getEAPConfigInstance(authPolicyInstData);
					GracepolicyData gracePolicyData = gracePolicyBLManager.getGracePolicyById(authPolicyInstData.getGracePolicyId());
					
					if(digestConfigInstanceData==null){
						digestConfigInstanceData= new DigestConfigInstanceData();
						digestConfigInstanceData.setName("");
					}
					if(eapConfigData==null){
						eapConfigData= new EAPConfigData();
						eapConfigData.setName("");
					}
					if(gracePolicyData==null){
							gracePolicyData= new GracepolicyData();
							gracePolicyData.setName("");
					}
					List<AuthPolicySMRelData> smRelDataList = servicePolicyBLManager.getAuthPolicySMRelData(authPolicyID);
					//session manager instnce data.
					AuthPolicySMRelData authPolicySMRelData=null;
					if(smRelDataList!=null && !smRelDataList.isEmpty()){
						authPolicySMRelData= (AuthPolicySMRelData)smRelDataList.get(0);
					}
					
					ISessionManagerInstanceData  sessionManagerInstanceData= null;
					if(authPolicySMRelData != null)
					{
					  sessionManagerInstanceData = sessionManagerBLManager.getSessionManagerDataById(authPolicySMRelData.getSessionManagerInstanceId());
          			}else{
          				sessionManagerInstanceData= new SessionManagerInstanceData();
          				sessionManagerInstanceData.setName("");
          			}
					
					//driver group data
					List<DriverInstanceData> mainDriverList = servicePolicyBLManager.getMainDriverList(authPolicyInstData);					
					List<AuthPolicySecDriverRelData> authPolicySecDriverRelDataList = servicePolicyBLManager.getAuthPolicySecDriverList(authPolicyInstData);
					
					
					// additional drivers
					List<AuthPolicyAdditionalDriverRelData> additionalDriverList = servicePolicyBLManager.getAuthPolicyAdditionalDriverList(authPolicyInstData);
					authPolicyInstData.setAdditionalDriverList(additionalDriverList);
					
					//esi data
					
					List<AuthPolicyExternalSystemRelData> ippoolServerRelList = servicePolicyBLManager.getExternalSystemRelList(authPolicyInstData,ExternalSystemConstants.IPPOOL_COMMUNICATION);
					List<ExternalSystemInterfaceInstanceData> ippoolServerList =getExternalSystemInstanceList(ippoolServerRelList);
					
					List<AuthPolicyExternalSystemRelData> prepaidServerRelList = servicePolicyBLManager.getExternalSystemRelList(authPolicyInstData,ExternalSystemConstants.PREPAID_COMMUNICATION);
					List<ExternalSystemInterfaceInstanceData> prepaidServerList = getExternalSystemInstanceList(prepaidServerRelList);
					
					List<AuthPolicyExternalSystemRelData> proxyServerRelList = servicePolicyBLManager.getExternalSystemRelList(authPolicyInstData,ExternalSystemConstants.AUTH_PROXY);
					List<ExternalSystemInterfaceInstanceData> proxyServerList = getExternalSystemInstanceList(proxyServerRelList);
					
					List<AuthPolicyExternalSystemRelData> chargingGatewayServerRelList = servicePolicyBLManager.getExternalSystemRelList(authPolicyInstData,ExternalSystemConstants.CHARGING_GATEWAY_COMMUNICATION);
					List<ExternalSystemInterfaceInstanceData> chargingGatewayServerList = getExternalSystemInstanceList(chargingGatewayServerRelList);
				
					List<AuthPolicyBroadcastESIRelData> broadcastingServerRelList = servicePolicyBLManager.getBroadcastingESIRelList(authPolicyInstData,ExternalSystemConstants.AUTH_PROXY);
				//	List<ExternalSystemInterfaceInstanceData> broadcastingServerList = getBroadcastESIList(broadcastingServerRelList);
					
					AuthPolicyRMParamsData ipPoolRMParamsData = servicePolicyBLManager.getRMParamsData(authPolicyInstData, ExternalSystemConstants.IPPOOL_COMMUNICATION);
					AuthPolicyRMParamsData prepaidRMParamsData = servicePolicyBLManager.getRMParamsData(authPolicyInstData, ExternalSystemConstants.PREPAID_COMMUNICATION);
					AuthPolicyRMParamsData chargingGatewayRMParamsData = servicePolicyBLManager.getRMParamsData(authPolicyInstData, ExternalSystemConstants.CHARGING_GATEWAY_COMMUNICATION);
					
					TranslationMappingConfData proxyTranslationMappingConfData = null;
					if(authPolicyInstData.getProxyTranslationMapConfigId() != null){
						TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
						proxyTranslationMappingConfData = translationMappingConfBLManager.getTranslationMappingConfData(authPolicyInstData.getProxyTranslationMapConfigId());
					}
					
					
					request.setAttribute("authPolicyInstData",authPolicyInstData);
					request.setAttribute("sessionManagerInstanceData",sessionManagerInstanceData);
					request.setAttribute("mainDriverInstanceList",mainDriverList);
					request.setAttribute("authPolicySecDriverRelDataList",authPolicySecDriverRelDataList);
					
					request.setAttribute("authMethodTypeDataList",authMethodTypeDataList);
					request.setAttribute("digestConfigInstanceData",digestConfigInstanceData);
					request.setAttribute("eapConfigData",eapConfigData);
					
					request.setAttribute("ipPoolRMParamsData",ipPoolRMParamsData);
					request.setAttribute("prepaidRMParamsData",prepaidRMParamsData);
					request.setAttribute("chargingGatewayRMParamsData",chargingGatewayRMParamsData);
					request.setAttribute("gracePolicyData",gracePolicyData);
					request.setAttribute("ippoolServerInstanceList",ippoolServerList);
					request.setAttribute("prepaidServerInstanceList",prepaidServerList);
					request.setAttribute("proxyServerInstanceList",proxyServerList);
					request.setAttribute("chargingGatewayServerInstanceList",chargingGatewayServerList);
					request.setAttribute("broadcastingServerInstanceList",broadcastingServerRelList);
					request.setAttribute("proxyTranslationMappingConfData", proxyTranslationMappingConfData);
				}
				
				return mapping.findForward(VIEW_FORWARD);
			}catch(Exception e){
				e.printStackTrace();
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);

			}
			return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	private List<ExternalSystemInterfaceInstanceData> getExternalSystemInstanceList(List<AuthPolicyExternalSystemRelData> externalSytemRelList){
		List<ExternalSystemInterfaceInstanceData> instanceList=null;
		if(externalSytemRelList!=null && !externalSytemRelList.isEmpty()){
			instanceList = new ArrayList<ExternalSystemInterfaceInstanceData>();
			for (Iterator iterator = externalSytemRelList.iterator(); iterator.hasNext();) {
				AuthPolicyExternalSystemRelData authPolicyExternalSystemRelData = (AuthPolicyExternalSystemRelData) iterator.next();
				ExternalSystemInterfaceInstanceData systemInterfaceInstanceData = authPolicyExternalSystemRelData.getExternalSystemData();
				instanceList.add(systemInterfaceInstanceData);
			}
		}
		return instanceList;

	}
	private List<ExternalSystemInterfaceInstanceData> getBroadcastESIList(List<AuthPolicyBroadcastESIRelData> externalSytemRelList){
		List<ExternalSystemInterfaceInstanceData> instanceList=null;
		if(externalSytemRelList!=null && !externalSytemRelList.isEmpty()){
			instanceList = new ArrayList<ExternalSystemInterfaceInstanceData>();
			for (Iterator iterator = externalSytemRelList.iterator(); iterator.hasNext();) {
				AuthPolicyBroadcastESIRelData authPolicyBroadcastESIRelData = (AuthPolicyBroadcastESIRelData) iterator.next();
				ExternalSystemInterfaceInstanceData systemInterfaceInstanceData = authPolicyBroadcastESIRelData.getExternalSystemData();
				instanceList.add(systemInterfaceInstanceData);
			}
		}
		return instanceList;
	}
}
