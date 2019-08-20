package com.elitecore.elitesm.web.servermgr.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.commons.tls.TLSVersion;
import com.elitecore.core.commons.tls.cipher.CipherSuites;
import com.elitecore.elitesm.blmanager.core.base.GenericBLManager;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeerprofile.DiameterPeerProfileBLManager;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.SYSLogNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationParameterData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationValuesData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.DriverTypeConstants;
import com.elitecore.elitesm.util.constants.PluginFlow;
import com.elitecore.elitesm.util.constants.ServiceTypeConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.BaseUpdateConfigurationAction;
import com.elitecore.elitesm.web.servermgr.server.forms.NetConfParameterValueBean;
import com.elitecore.elitesm.web.servermgr.service.forms.UpdateServiceConfigurationForm;

public class UpdateServiceConfigurationAction extends BaseUpdateConfigurationAction {

    private static final String MODULE = "UPDATE_SERVICE_CONFIGURATION";
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String UPDATE_SERVICE = "updateService";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.UPDATE_SERVICE_CONFIGURATION_ACTION;
    private static final String VIEW_CONFIGURATION_ACTION = ConfigConstant.VIEW_CONFIGURATION_ACTION;


    private String cnfInstanceId = null;
    List lstNetDriverType = null;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response){
        List<NetConfParameterValueBean> lstParameters = new ArrayList<NetConfParameterValueBean>();
        Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
        NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
        NetServerBLManager netServerBLManager = new NetServerBLManager();		
        
        UpdateServiceConfigurationForm updateServiceConfForm = (UpdateServiceConfigurationForm)form;
        
        
        INetServiceInstanceData netServiceInstanceData = new NetServiceInstanceData();
        cnfInstanceId = updateServiceConfForm.getConfInstanceId();
        
        String netServiceId  = updateServiceConfForm.getNetServiceId();	
      
        if(cnfInstanceId != null && netServiceId != null){	
            try{
            	DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
            	AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();
                checkActionPermission(request, VIEW_CONFIGURATION_ACTION);
                PluginBLManager pluginBLManager = new PluginBLManager();
                DriverBLManager driverBLManager = new DriverBLManager();
                DiameterPeerProfileBLManager diameterPeerProfileBLManager = new DiameterPeerProfileBLManager();
                SessionManagerBLManager sessionManagerBLManger = new SessionManagerBLManager();
                updateServiceConfForm.setConfInstanceId(cnfInstanceId);
                updateServiceConfForm.setConfigurationName(netServiceBLManager.getNetConfigurationName(cnfInstanceId));
                updateServiceConfForm.setServerName(netServerBLManager.getNetServerNameServiceConfig(cnfInstanceId));
                updateServiceConfForm.setServiceName(netServiceBLManager.getNetServiceNameServiceConfig(cnfInstanceId));
                netServiceInstanceData.setNetServiceId(netServiceId);
                netServiceInstanceData = netServiceBLManager.getNetServiceInstance(netServiceInstanceData.getNetServiceId());
                List netServiceTypeList = netServiceBLManager.getNetServiceTypeList();
                List netServerInstanceList = netServerBLManager.getNetServerInstanceList();
                INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServiceInstanceData.getNetServerId());
                List netParamStartupModeList = netServerBLManager.getNetConfigParamStartupModeList("CONFIG_PARAM_STARTUP_TYPE");
                
                List<PluginInstData> pluginInstDataList = pluginBLManager.getPluginInstanceDataList(netServiceInstanceData.getName());
                List classicCSVDriverList=driverBLManager.getDriverInstanceByDriverTypeList(DriverTypeConstants.RADIUS_CLASSICCSV_ACCT_DRIVER);
                List datasourceList = databaseDSBLManager.getDatabaseDSList();
                List localSessionManagerList = sessionManagerBLManger.getSessionManagerInstanceList();
                List<SYSLogNameValuePoolData> sysLogNameValuePoolDataList = alertListenerBLManager.getSysLogNameValuePoolList();
                DiameterRoutingConfBLManager diameterRoutingConfBLManager= new DiameterRoutingConfBLManager();
                List diameterRoutingTableList=diameterRoutingConfBLManager.getDiameterRoutingConfList();
                List<ServerCertificateData> serverCertificateList = diameterPeerProfileBLManager.getListOfServerCertificate();
                Collection<TLSVersion> tlsVersionCollection = Arrays.asList(TLSVersion.values());
                Set<String> tlsVersionSet=new HashSet<String>();
                
                for(TLSVersion tlsVersion: tlsVersionCollection){
                	tlsVersionSet.add(tlsVersion.version);
                }
                
                IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
    			
                INetConfigurationInstanceData netConfigurationInstanceData=netServerBLManager.getConfigurationInstance(cnfInstanceId);
                Map<String,String> servicePolicyMap = getServicePolicyMap(netConfigurationInstanceData.getConfigId());
                
                request.setAttribute("netServiceInstanceData",netServiceInstanceData);
                request.setAttribute("netServiceTypeList",netServiceTypeList);
                request.setAttribute("netServerInstanceList",netServerInstanceList);
                request.setAttribute("startUpModeList",netParamStartupModeList);
                request.setAttribute("pluginInstDataList", pluginInstDataList);
                request.setAttribute("classicCSVDriverList",classicCSVDriverList);
                request.setAttribute("datasourceList",datasourceList);
                request.setAttribute("localSessionManagerList",localSessionManagerList);
                request.setAttribute("sysLogNameValuePoolDataList",sysLogNameValuePoolDataList);
                request.setAttribute("diameterRoutingTableList",diameterRoutingTableList);
                request.setAttribute("servicePolicyMap", servicePolicyMap);
                request.setAttribute("serverCerficateList", serverCertificateList);
                request.setAttribute("tlsVersionSet", tlsVersionSet);
                String strAction =request.getParameter("action");

                if(strAction!=null){ 
                    Logger.logTrace(MODULE," Action : "+request.getParameter("action"));
                    // action : addNode

                    if(strAction.equalsIgnoreCase("addNode")){
                    	checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
                        String strNodeParameterId = request.getParameter("nodeParameterId");
                        String strNodeInstanceId =request.getParameter("nodeInstanceId");
                        String strChildTotalInstanceVal=request.getParameter("childTotalInstanceVal");

                        INetConfigurationParameterData netConfParameterData = netServiceBLManager.getNetConfigurationParameterData(strNodeParameterId,netConfigurationInstanceData.getConfigId());
                        lstParameters = updateServiceConfForm.getLstParameterValue();
                        lstParameters = getRecursiveNewNetConfParameterData(netConfParameterData,strNodeParameterId,lstParameters,cnfInstanceId,strNodeInstanceId,strChildTotalInstanceVal,strNodeInstanceId);
                        lstParameters = updateValuePoolList(lstParameters);
                        Collections.<NetConfParameterValueBean>sort(lstParameters);
                        updateDivStatus(lstParameters);
                        incrementTotalInstance(strNodeParameterId,lstParameters,strNodeInstanceId);
                        updateServiceConfForm.setLstParameterValue(lstParameters);
                        doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
                        return mapping.findForward(UPDATE_SERVICE);

                    }else if(strAction.equalsIgnoreCase("save")){
                    	checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
                        lstParameters = updateServiceConfForm.getLstParameterValue();
                        
                        /* convert String to Code */
                        
                        StringBuffer strCipherSuiteList = new StringBuffer();
                        if(lstParameters != null && lstParameters.size() >0){
                        	for(NetConfParameterValueBean netConfParamValueBean:lstParameters){
                        		if(netConfParamValueBean.getAlias().equals("enabled-cipher-suites")){
                        			String listOfCipherSuit=netConfParamValueBean.getValue();
                        			listOfCipherSuit = listOfCipherSuit.replace(System.getProperty("line.separator"), "");
                        			if(listOfCipherSuit !=null){
                        				if(listOfCipherSuit !="" && listOfCipherSuit.length() > 0){
                            				String [] strCiphersuit=listOfCipherSuit.split(",");
                            				String[] strcipherSuitArray =new String[4000];
                            				Collection<CipherSuites> cipherSuites = Arrays.asList(CipherSuites.values());
                            				
                            				for(int i=0;i<strCiphersuit.length;i++){
                            					if(strCiphersuit[i] != null || strCiphersuit[i] != " "){
                            						for(CipherSuites cipherSuit:cipherSuites){
                            							if(strCiphersuit[i].trim().equals(cipherSuit.name().trim())){
                            								strcipherSuitArray[i]=String.valueOf(cipherSuit.code);
                            							}
                            						}
                            					}
                            				}
                            				
                            				for (int i = 0; i < strcipherSuitArray.length; i++) {
                            					if(strcipherSuitArray[i] != null){
                            						if(strCipherSuiteList.length()>0){
                            					   		strCipherSuiteList.append(",");
                            					   		strCipherSuiteList.append(strcipherSuitArray[i]);
                            					   	}else{
                            					   		strCipherSuiteList.append(strcipherSuitArray[i]);
                            					   	}
                            					}
                            				}
                            			}
                            			netConfParamValueBean.setValue(strCipherSuiteList.toString());
                        			}
                        		}
                        		if(netConfParamValueBean.getAlias().equals("server-certificate-id")){
                        			String listOfCipherSuit=netConfParamValueBean.getValue();
                        			if(!(listOfCipherSuit.equals("NONE"))){
                        				if(serverCertificateList != null && serverCertificateList.size() > 0){
                            				for(ServerCertificateData seString:serverCertificateList){
                            					if(seString.getServerCertificateName().equals(listOfCipherSuit)){
                            						netConfParamValueBean.setValue(String.valueOf(seString.getServerCertificateId()));
                            					}
                            				}
                        				}
                        			}else{
                        				netConfParamValueBean.setValue(null);
                        			}
                        		}
                        	}
                        }
                        
                        
                        
                        
                        List<INetConfigurationValuesData> lstValueData = new ArrayList<INetConfigurationValuesData>();
                        if(lstParameters != null){
                            for(int i=0; i< lstParameters.size() ; i++){
                                lstValueData.add(makeValueDataFromBean((NetConfParameterValueBean)lstParameters.get(i)));
                            }
                        }
                        doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
                        request.setAttribute("responseUrl","/listNetServiceConfiguration.do?netserviceid="+netServiceId);
                        netServiceBLManager.saveNetConfigurationValues(lstValueData,cnfInstanceId,netServiceId);
                        ActionMessage message = new ActionMessage("servermgr.update.service.configuration.success");
                        ActionMessages messages = new ActionMessages();
                        messages.add("information",message);
                        saveMessages(request,messages);
                        return mapping.findForward(SUCCESS_FORWARD);

                    }else if(strAction.equalsIgnoreCase("delete")){
                    	checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
                        lstParameters = updateServiceConfForm.getLstParameterValue();
                        String strNodeInstanceId=request.getParameter("nodeInstanceId");
                        if(lstParameters != null){
                            Iterator itrParameters = lstParameters.iterator();
                            while(itrParameters.hasNext()){
                                NetConfParameterValueBean bean = (NetConfParameterValueBean)itrParameters.next();
                                String strInstanceId = bean.getInstanceId();
                                if(strInstanceId.startsWith(strNodeInstanceId)){
                                    itrParameters.remove();
                                    decrementTotalInstance(bean.getParameterId(),lstParameters,strNodeInstanceId);
                                }
                            }
                            lstParameters = updateValuePoolList(lstParameters);
                            updateDivStatus(lstParameters);
                            updateServiceConfForm.setLstParameterValue(lstParameters);
                            doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
                            return mapping.findForward(UPDATE_SERVICE);
                        }
                    }

                }


                String configInstatnceId = cnfInstanceId;
                INetConfigurationParameterData netConfParamData = netServiceBLManager.getConfigurationParameterValues(configInstatnceId);


                boolean existFlag = false;				
                if(netConfParamData != null){
                    if(netConfParamData.getNetConfigParamValues() != null &&  netConfParamData.getNetConfigParamValues().size() > 0){
                        Iterator itr = netConfParamData.getNetConfigParamValues().iterator();
                        while(itr.hasNext()){
                            INetConfigurationValuesData netConfigValuesData = (INetConfigurationValuesData)itr.next();
                            if(netConfigValuesData.getConfigInstanceId().equals(configInstatnceId)){
                                existFlag = true;
                            }
                        }
                    }
                    if(existFlag == true){
                        lstParameters = getRecursiveNetConfigurationParameterValues(netConfParamData,configInstatnceId,lstParameters);
                    }else{
                        lstParameters = getRecursiveNetConfigurationRootParameterValues(netConfParamData,configInstatnceId,lstParameters,"1");					
                    }
                    Collections.<NetConfParameterValueBean>sort(lstParameters);
                    updateDivStatus(lstParameters);
                    lstParameters = updateValuePoolListForAttributeId(lstParameters);
                    
                    StringBuffer strCipherSuiteList = new StringBuffer();
                    if(lstParameters != null && lstParameters.size() >0){
                    	for(NetConfParameterValueBean netConfParamValueBean:lstParameters){
                    		if(netConfParamValueBean.getAlias().equals("enabled-cipher-suites")){
                    			String listOfCipherSuit=netConfParamValueBean.getValue();
                    			if(listOfCipherSuit !=null){
                    				if(listOfCipherSuit !="" && listOfCipherSuit.length() > 0){
                        				String [] strCiphersuit=listOfCipherSuit.split(",");
                        				String[] strcipherSuitArray =new String[4000];
                        				Collection<CipherSuites> cipherSuites = Arrays.asList(CipherSuites.values());
                        				
                        				for(int i=0;i<strCiphersuit.length;i++){
                        					if(strCiphersuit[i] != null || strCiphersuit[i] != " "){
                        						for(CipherSuites cipherSuit:cipherSuites){
                        							int valueInInt=Integer.parseInt(strCiphersuit[i]);
                        							if(valueInInt == cipherSuit.code){
                        								strcipherSuitArray[i]=cipherSuit.name();
                        							}
                        						}
                        					}
                        				}
                        				
                        				for (int i = 0; i < strcipherSuitArray.length; i++) {
                        					if(strcipherSuitArray[i] != null){
                        						if(strCipherSuiteList.length()>0){
                        					   		strCipherSuiteList.append(",\n");
                        					   		strCipherSuiteList.append(strcipherSuitArray[i]);
                        					   	}else{
                        					   		strCipherSuiteList.append(strcipherSuitArray[i]);
                        					   	}
                        					}
                        				}
                        			}
                        			netConfParamValueBean.setValue(strCipherSuiteList.toString());
                    			}
                    		}
                    		
                    		if(netConfParamValueBean.getAlias().equals("server-certificate-id")){
                        			String listOfCipherSuit=netConfParamValueBean.getValue();
                        			if(listOfCipherSuit != null){
                        				if(serverCertificateList != null && serverCertificateList.size() > 0){
                            				for(ServerCertificateData seString:serverCertificateList){
                            						if(seString.getServerCertificateId().equals(Long.parseLong(listOfCipherSuit))){
                                						netConfParamValueBean.setValue(seString.getServerCertificateName());
                                					}
                            				}
                        				}
                        			}else{
                        				netConfParamValueBean.setValue("NONE");
                        			}
                        			
                    	  }
                     }
                    }
                    
                    updateServiceConfForm.setLstParameterValue(lstParameters);
                }
                return mapping.findForward(UPDATE_SERVICE);

            }catch(ActionNotPermitedException e){
                Logger.logError(MODULE,"Error :-" + e.getMessage());
                printPermitedActionAlias(request);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);
                ActionMessages messages = new ActionMessages();
                messages.add("information", new ActionMessage("general.user.restricted"));
                saveErrors(request, messages);
                return mapping.findForward(INVALID_ACCESS_FORWARD);
            }catch(Exception exception){
                Logger.logError(MODULE,"Error in action operation,reason :"+exception.getMessage());
                Logger.logTrace(MODULE,exception);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exception);
    			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("servermgr.udpate.service.configuration.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveErrors(request,messages);
                return mapping.findForward(FAILURE_FORWARD);

            }
        }
        Logger.logTrace(MODULE,"Returning error forward from "+getClass().getName());
        ActionMessage message = new ActionMessage("servermgr.udpate.service.configuration.failure");
        ActionMessages messages = new ActionMessages();
        messages.add("information",message);
        saveErrors(request,messages);
        return mapping.findForward(FAILURE_FORWARD);
    }
    
	
    @SuppressWarnings("unchecked")
	private Map<String,String> getServicePolicyMap(String configId) throws DataManagerException{
		Map<String,String> servicePolicyMap = new LinkedHashMap<String, String>();
    	GenericBLManager genericBLManager = new GenericBLManager();
    	if(ServiceTypeConstants.RAD_AUTH_CONFIG_ID.equals(configId)){ 
    		List<RadServicePolicyData> radServicePolicyList = (List<RadServicePolicyData>) genericBLManager.getAllRecords(RadServicePolicyData.class, "status", "name", true);
    		for(RadServicePolicyData radiusPolicyInstData : radServicePolicyList){
    			servicePolicyMap.put(radiusPolicyInstData.getRadiusPolicyId(), radiusPolicyInstData.getName());
    		}
    	}else if(ServiceTypeConstants.RAD_ACCT_CONFIG_ID.equals(configId)){
    		List<RadServicePolicyData> radServicePolicyList = (List<RadServicePolicyData>) genericBLManager.getAllRecords(RadServicePolicyData.class, "status", "name", true);
    		for(RadServicePolicyData radiusPolicyInstData : radServicePolicyList){
    			servicePolicyMap.put(radiusPolicyInstData.getRadiusPolicyId(), radiusPolicyInstData.getName());
    		}
    	}else if(ServiceTypeConstants.RAD_DYN_AUTH_CONFIG_ID.equals(configId)){
    		List<DynAuthPolicyInstData> dynAuthPolicyInstDataList = (List<DynAuthPolicyInstData>) genericBLManager.getAllRecords(DynAuthPolicyInstData.class, "status", "name", true);
    		for(DynAuthPolicyInstData dynAuthPolicyInstData : dynAuthPolicyInstDataList){
    			servicePolicyMap.put(dynAuthPolicyInstData.getDynAuthPolicyId(),dynAuthPolicyInstData.getName());
    		}
    	}else if(ServiceTypeConstants.NAS_APPL_CONF_CONFIG_ID.equals(configId)){
    		List<NASPolicyInstData> nasPolicyInstDataList = (List<NASPolicyInstData>) genericBLManager.getAllRecords(NASPolicyInstData.class, "status", "name", true);
    		for(NASPolicyInstData nasPolicyInstData : nasPolicyInstDataList){
    			servicePolicyMap.put(nasPolicyInstData.getNasPolicyId(), nasPolicyInstData.getName());
    		}
    	}else if(ServiceTypeConstants.CREDIT_CONTROL_CONFIG_ID.equals(configId)){
    		List<CreditControlPolicyData> creditControlPolicyDataList = (List<CreditControlPolicyData>) genericBLManager.getAllRecords(CreditControlPolicyData.class, "status", "name", true);
    		for(CreditControlPolicyData creditControlPolicyData : creditControlPolicyDataList){
    			servicePolicyMap.put(creditControlPolicyData.getPolicyId(), creditControlPolicyData.getName());
    		}
    	}else if(ServiceTypeConstants.EAP_POLICY_CONFIG_ID.equals(configId)){
    		List<EAPPolicyData> eapPolicyDataList = (List<EAPPolicyData>) genericBLManager.getAllRecords(EAPPolicyData.class, "status", "name", true);
    		for(EAPPolicyData eapPolicyData : eapPolicyDataList){
    			servicePolicyMap.put(eapPolicyData.getEapPolicyId(), eapPolicyData.getName());
    		}
    	}else if(ServiceTypeConstants.RM_CHARGING_POLICY_CONFIG_ID.equals(configId)){ 
    		List<CGPolicyData> cgPolicyDataList = (List<CGPolicyData>) genericBLManager.getAllRecords(CGPolicyData.class, "status", "name", true);
    		for(CGPolicyData cgPolicyData : cgPolicyDataList){
    			servicePolicyMap.put(cgPolicyData.getPolicyId(), cgPolicyData.getName());
    		}
    	}else if(ServiceTypeConstants.EAP_SERVICE_CONFIG_ID.equals(configId)){
    		List<EAPPolicyData> eapPolicyDataList = (List<EAPPolicyData>) genericBLManager.getAllRecords(EAPPolicyData.class, "status", "name", true);
    		for(EAPPolicyData eapPolicyData : eapPolicyDataList){
    			servicePolicyMap.put(eapPolicyData.getEapPolicyId(), eapPolicyData.getName());
    		}
    	}else if(ServiceTypeConstants.NAS_SERVICE_CONFIG_ID.equals(configId)){
    		List<NASPolicyInstData> nasPolicyInstDataList = (List<NASPolicyInstData>) genericBLManager.getAllRecords(NASPolicyInstData.class, "status", "name", true);
    		for(NASPolicyInstData nasPolicyInstData : nasPolicyInstDataList){
    			servicePolicyMap.put(nasPolicyInstData.getNasPolicyId(), nasPolicyInstData.getName());
    		}
    	}else if(ServiceTypeConstants.CREDIT_CONTROL_SERVICE_CONFIG_ID.equals(configId)){
    		List<CreditControlPolicyData> creditControlPolicyDataList = (List<CreditControlPolicyData>) genericBLManager.getAllRecords(CreditControlPolicyData.class, "status", "name", true);
    		for(CreditControlPolicyData creditControlPolicyData : creditControlPolicyDataList){
    			servicePolicyMap.put(creditControlPolicyData.getPolicyId(), creditControlPolicyData.getName());
    		}
    	}else if(ServiceTypeConstants.TGPP_AAA_SERVER.equals(configId)){
    		List<TGPPAAAPolicyData> tgppAAAPolicyDataList = (List<TGPPAAAPolicyData>) genericBLManager.getAllRecords(TGPPAAAPolicyData.class, "status", "name", true);
    		for(TGPPAAAPolicyData tgppAAAPolicyData : tgppAAAPolicyDataList){
    			servicePolicyMap.put(tgppAAAPolicyData.getTgppAAAPolicyId(), tgppAAAPolicyData.getName());
    		}
    	}
    	
    	return servicePolicyMap;
    }
    
    private Integer getPluginFlowType(String configurationName) {
    	Integer flowType = null;
    	if( configurationName != null){
    		configurationName = configurationName.trim();
    		for(PluginFlow pluginFLow : PluginFlow.values()){
    			if(pluginFLow.pluginFlowStr.equals(configurationName)){
    				flowType = pluginFLow.pluginFlowType;
    			}
    		}
    	}else{
    		flowType = 0;
    	}
		return flowType;
	}
   
}
