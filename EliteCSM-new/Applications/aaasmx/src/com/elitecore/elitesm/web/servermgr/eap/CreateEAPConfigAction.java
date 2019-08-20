package com.elitecore.elitesm.web.servermgr.eap;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import  com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.util.constants.tls.KeyExchangeAlgorithm;
import com.elitecore.coreeap.util.constants.tls.SignatureAlgorithm;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.IEAPConfigData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.EAPConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.eap.forms.CreateEAPConfigForm;

public class CreateEAPConfigAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String TLS_TTLS_PEAP_FORWARD = "createEAPConfigDetail";
	private static final String GSM_FORWARD = "createEAPGsmConfig";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_EAP_CONFIGURATION;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			ActionErrors errors = new ActionErrors();
			String action=null;  
			CreateEAPConfigForm createEAPConfigForm = (CreateEAPConfigForm)form; 
			try{
				checkActionPermission(request, ACTION_ALIAS);
				Logger.logDebug(MODULE,"ACtion is:"+request.getParameter("action"));
				action=request.getParameter("action");

				String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();


				if(action==null){
					action=createEAPConfigForm.getAction();
				}



				IEAPConfigData eapConfigData = new EAPConfigData();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;

				//createSessionManagerForm.setLstDatasource(lstdatasource);
				Date currentDate = new Date(); 

				eapConfigData.setName(createEAPConfigForm.getName());
				eapConfigData.setDescription(createEAPConfigForm.getDescription());
				eapConfigData.setSessionCleanupInterval(createEAPConfigForm.getSessionCleanupInterval());
				eapConfigData.setSessionDurationForCleanup(createEAPConfigForm.getSessionDurationForCleanup());
				eapConfigData.setSessionTimeout(createEAPConfigForm.getSessionTimeout());
				
				if( createEAPConfigForm.getMskRevalidationTime() == null || createEAPConfigForm.getMskRevalidationTime().length() <= 0 ){
					eapConfigData.setMskRevalidationTime(null);
				}else{
					eapConfigData.setMskRevalidationTime(Long.parseLong(createEAPConfigForm.getMskRevalidationTime()));
				}
				
				eapConfigData.setTreatInvalidPacketAsFatal(createEAPConfigForm.getTreatInvalidPacketAsFatal());
				eapConfigData.setDefaultNegiotationMethod(createEAPConfigForm.getDefaultNegiotationMethod());
				eapConfigData.setNotificationSuccess(createEAPConfigForm.getNotificationSuccess());
				eapConfigData.setNotificationFailure(createEAPConfigForm.getNotificationFailure());
				eapConfigData.setEapTtlsCertificateRequest(createEAPConfigForm.getEapTtlsCertificateRequest());
				eapConfigData.setMaxEapPacketSize(createEAPConfigForm.getMaxEapPacketSize());


				eapConfigData.setCreatedByStaffId(currentUser);
				//eapConfigData.setLastModifiedByStaffId(Long.parseLong(currentUser));
				eapConfigData.setCreateDate(getCurrentTimeStemp());
				//eapConfigData.setLastModifiedDate(getCurrentTimeStemp());

				String tls=createEAPConfigForm.getTls();
				String ttls=createEAPConfigForm.getTtls();
				String md5=createEAPConfigForm.getMd5();
				String peap=createEAPConfigForm.getPeap();
				String gtc=createEAPConfigForm.getGtc();
				String mschapV2=createEAPConfigForm.getMschapv2();
				String sim = createEAPConfigForm.getSim();
				String aka = createEAPConfigForm.getAka();
				String akaPrime = createEAPConfigForm.getAkaPrime();

				StringBuffer enabledMethod=new StringBuffer();
				String[] enableAuthMethodarry=new String[9];
				enableAuthMethodarry[0]=tls;
				enableAuthMethodarry[1]=ttls;
				enableAuthMethodarry[2]=md5;
				enableAuthMethodarry[3]=peap;
				enableAuthMethodarry[4]=gtc;
				enableAuthMethodarry[5]=mschapV2;
				enableAuthMethodarry[6]=sim;
				enableAuthMethodarry[7]=aka;
				enableAuthMethodarry[8]=akaPrime;
				/*for (int i = 0; i < enableAuthMethodarry.length-1; i++) {

	        		enabledMethod.append(enableAuthMethodarry[i]);
	        		enabledMethod.append(",");
				}
	  			if(enableAuthMethodarry.length>0){
	  				enabledMethod.append( enableAuthMethodarry.length-1);
	  			}*/
				int len=enableAuthMethodarry.length;
				for (int i = 0; i <len ; i++) {

					if(enableAuthMethodarry[i]!=null && enableAuthMethodarry[i].length()>0){

						if(enabledMethod.length()>0){
							enabledMethod.append(",");
							enabledMethod.append(enableAuthMethodarry[i]);
						}else{
							enabledMethod.append(enableAuthMethodarry[i]);
						}

					}

				}



				Logger.logDebug(MODULE,"EnabledAuthMethodStr is:"+enabledMethod.toString());

				eapConfigData.setEnabledAuthMethods(enabledMethod.toString());


				if("create".equalsIgnoreCase(action)){

					EAPConfigBLManager eapConfigBLManager=new EAPConfigBLManager();
					eapConfigBLManager.defaultEAPConfigCreate(eapConfigData,staffData,actionAlias);
					request.setAttribute("responseUrl","/initSearchEAPConfig.do?");
					ActionMessage message = new ActionMessage("servermgr.eap.create.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request,messages);
					return mapping.findForward(SUCCESS_FORWARD);

				}else if("next".equalsIgnoreCase(action)){
					
					EAPConfigBLManager eapConfigBLManager=new EAPConfigBLManager();
					List<String> enalbedMethodList = new ArrayList<String>();
					enalbedMethodList.add(tls);
					enalbedMethodList.add(ttls);
					enalbedMethodList.add(peap);
					enalbedMethodList.add(sim);
					enalbedMethodList.add(aka);
					enalbedMethodList.add(akaPrime);
					
					
					List<ServerCertificateData> serverCertificateList = eapConfigBLManager.getListOfServerCertificate();
					ProtocolVersion minTLSVer=ProtocolVersion.fromVersion(EAPConfigConstant.TLSv_1);
					ProtocolVersion maxTLSVer=ProtocolVersion.fromVersion(EAPConfigConstant.TLSv_1_2);
					
					Set<CipherSuites> cipherSuiteSet = new LinkedHashSet<CipherSuites>(CipherSuites.getSupportedCipherSuites(minTLSVer,maxTLSVer));
					
					final Set<SignatureAlgorithm> signatures = EnumSet.of(SignatureAlgorithm.RSA);
					final Set<KeyExchangeAlgorithm> keyExchanges = EnumSet.of(KeyExchangeAlgorithm.RSA);
					
					Collectionz.filter(cipherSuiteSet, new Predicate<CipherSuites>() {

						@Override
						public boolean apply(CipherSuites input) {
							return signatures.contains(input.getSignatureAlgorithm())
									&& keyExchanges.contains(input.getKeyExchangeAlgorithm());
						}
					});
					
					createEAPConfigForm.setServerCertificateDataList(serverCertificateList);
					
					request.setAttribute("cipherSuiteSet", cipherSuiteSet);
					request.getSession().setAttribute("enalbedMethodList",enalbedMethodList);
					request.setAttribute("serverCertificateDataList", serverCertificateList);
					request.setAttribute("createEAPConfigForm", createEAPConfigForm);
					Logger.logDebug(MODULE,"TLS Value is: "+tls);
					Logger.logDebug(MODULE,"TTLS Value is: "+ttls);
					Logger.logDebug(MODULE,"PEAP Value is: "+peap);
					Logger.logDebug(MODULE,"SIM Value is: "+sim);
					Logger.logDebug(MODULE,"AKA Value is: "+aka);
					Logger.logDebug(MODULE,"AKA' Value is: "+akaPrime);
						
					request.getSession().setAttribute("eapConfigData",eapConfigData);
					
					return mapping.findForward(getNextForward(enalbedMethodList));	

				}

			}catch(ActionNotPermitedException e){
				Logger.logError(MODULE, "Restricted to do action.");
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(INVALID_ACCESS_FORWARD);	

			}catch (DuplicateInstanceNameFoundException dpfExp) {
				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE,dpfExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("servermgr.eap.create.duplicate.failure",createEAPConfigForm.getName());
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
			}catch(DataManagerException e){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("servermgr.eap.create.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}catch(Exception e){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("servermgr.eap.create.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			return mapping.findForward(FAILURE_FORWARD);
		}
		private String getNextForward(List<String> enalbedMethodList){
			if(enalbedMethodList!=null){
				if(enalbedMethodList.contains(EAPConfigConstant.TLS_STR) || 
						enalbedMethodList.contains(EAPConfigConstant.TTLS_STR) ||
						enalbedMethodList.contains(EAPConfigConstant.PEAP_STR) ){
					return TLS_TTLS_PEAP_FORWARD;
				}
				
				if(enalbedMethodList.contains(EAPConfigConstant.SIM_STR) || enalbedMethodList.contains(EAPConfigConstant.AKA_STR) || enalbedMethodList.contains(EAPConfigConstant.AKA_PRIME_STR)){
					return GSM_FORWARD; 
				}
									
			}
			return TLS_TTLS_PEAP_FORWARD;
			
		}
		
	}