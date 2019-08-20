
package com.elitecore.elitesm.web.servermgr.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeerprofile.DiameterPeerProfileBLManager;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager;
import com.elitecore.elitesm.blmanager.radius.clientprofile.ClientProfileBLManager;
import com.elitecore.elitesm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.SYSLogNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationParameterData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationValuesData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.PluginFlow;
import com.elitecore.elitesm.util.constants.ServiceTypeConstants;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.BaseUpdateConfigurationAction;
import com.elitecore.elitesm.web.servermgr.server.forms.NetConfParameterValueBean;
import com.elitecore.elitesm.web.servermgr.server.forms.UpdateNetServerConfigurationForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class UpdateNetServerConfigurationAction extends BaseUpdateConfigurationAction {
    private static final String MODULE = "UPDATE_NET_SERVER_CONFIGURATION";
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String UPDATE_SERVER = "updateServer";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.UPDATE_CONFIGURATION_ACTION;
    private static final String VIEW_CONFIGURATION_ACTION = ConfigConstant.VIEW_CONFIGURATION_ACTION;

    private String cnfInstanceId = null;
    private String netServerId = null;  
    List netServerTypeList = null;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response){
        List<NetConfParameterValueBean> lstParameters = new ArrayList<NetConfParameterValueBean>();
        Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
        NetServerBLManager netServerBLManager = new NetServerBLManager();
       
        
        UpdateNetServerConfigurationForm updateServerConfForm = (UpdateNetServerConfigurationForm)form;
       
        
        cnfInstanceId = updateServerConfForm.getConfInstanceId();
        netServerId = updateServerConfForm.getNetServerId();	

        if(cnfInstanceId != null && netServerId != null){			
            try{
                checkActionPermission(request, VIEW_CONFIGURATION_ACTION);
                ClientProfileBLManager profileBLManager = new ClientProfileBLManager();
                AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();
                TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
                PluginBLManager pluginBLManger  = new PluginBLManager();
                DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
                IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
                
                updateServerConfForm.setConfInstanceId(cnfInstanceId);
                updateServerConfForm.setConfigurationName(netServerBLManager.getNetConfigurationName(cnfInstanceId));
                updateServerConfForm.setServerName(netServerBLManager.getNetServerNameServerConfig(cnfInstanceId));

                String strAction =request.getParameter("action");
                INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
                netServerInstanceData.setNetServerId(netServerId);
                netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
                netServerTypeList = netServerBLManager.getNetServerTypeList();
                List netParamStartupModeList = netServerBLManager.getNetConfigParamStartupModeList("CONFIG_PARAM_STARTUP_TYPE");
                
                List clientProfileList = profileBLManager.getRadiusClientProfileList();
                List alertListenerList = alertListenerBLManager.getAlertListernerDataList();
                List databaseList = databaseDSBLManager.getDatabaseDSList();
                
                List<PluginInstData> pluginInstDataList = pluginBLManger.getPluginInstanceDataList("DIAMETER-STACK");
                
                List<SYSLogNameValuePoolData> sysLogNameValuePoolDataList = alertListenerBLManager.getSysLogNameValuePoolList();
                List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.DIAMETER, TranslationMappingConfigConstants.WEB_SERVICE);
                DiameterPeerProfileBLManager diameterPeerProfileBLManager = new DiameterPeerProfileBLManager();
                List<ServerCertificateData> serverCertificateList = diameterPeerProfileBLManager.getListOfServerCertificate();
                Collection<TLSVersion> tlsVersionCollection = Arrays.asList(TLSVersion.values());
                Set<String> tlsVersionSet=new HashSet<String>();
                
                for(TLSVersion tlsVersion: tlsVersionCollection){
                	tlsVersionSet.add(tlsVersion.version);
                }
                
                /*Diameter Session Manager*/
                DiameterSessionManagerBLManager diameterSessionManagerBLManager = new DiameterSessionManagerBLManager();
                List<DiameterSessionManagerData> diameterSessionManagerDataList = diameterSessionManagerBLManager.getDiameterSessionManagerDatas();
                
                DiameterRoutingConfBLManager diameterRoutingConfBLManager= new DiameterRoutingConfBLManager();
                List diameterRoutingTableList=diameterRoutingConfBLManager.getDiameterRoutingConfList();
                String configInstatnceId = cnfInstanceId;

                INetConfigurationParameterData netConfParamData = netServerBLManager.getConfigurationParameterValues(cnfInstanceId);
                boolean existFlag = false;	
                Logger.logDebug(MODULE,"netConfParamData :" + netConfParamData );
                
                request.setAttribute("diameterPeerList", getDiameterPeerList(netConfParamData.getConfigId()));
                request.setAttribute("diameterRoutingTableList",diameterRoutingTableList);
                request.setAttribute("netServerInstanceData",netServerInstanceData);
                request.setAttribute("netServerTypeList",netServerTypeList);
                request.setAttribute("startUpModeList",netParamStartupModeList);
                request.setAttribute("clientProfileList",clientProfileList);
                request.setAttribute("alertListenerList",alertListenerList);
                request.setAttribute("sysLogNameValuePoolDataList",sysLogNameValuePoolDataList);
                request.setAttribute("translationMappingConfDataList",translationMappingConfDataList);
                request.setAttribute("pluginInstDataList",pluginInstDataList);
                request.setAttribute("serverCerficateList", serverCertificateList);
                request.setAttribute("tlsVersionSet", tlsVersionSet);
                request.setAttribute("databaseDSList", databaseList);
                request.setAttribute("diameterSessionManagerDataList", diameterSessionManagerDataList);
                
                
                if(strAction!=null){
                    Logger.logTrace(MODULE," Action : "+request.getParameter("action"));
                    // action : addNode
                    if(strAction.equalsIgnoreCase("addNode")){
                    	checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
                        String strNodeParameterId = request.getParameter("nodeParameterId");
                        String strNodeInstanceId=request.getParameter("nodeInstanceId");
                        String strChildTotalInstanceVal=request.getParameter("childTotalInstanceVal");
                        
                        INetConfigurationInstanceData netConfigurationInstanceData=netServerBLManager.getConfigurationInstance(cnfInstanceId);
                        INetConfigurationParameterData netConfParameterData = netServerBLManager.getNetConfigurationParameterData(strNodeParameterId,netConfigurationInstanceData.getConfigId());
                        netConfParameterData.getNetConfigParamValues();
                        lstParameters = updateServerConfForm.getLstParameterValue();
                        lstParameters = getRecursiveNewNetConfParameterData(netConfParameterData,strNodeParameterId,lstParameters,cnfInstanceId,strNodeInstanceId,strChildTotalInstanceVal,strNodeInstanceId);
                        lstParameters = updateValuePoolList(lstParameters);
                        Collections.<NetConfParameterValueBean>sort(lstParameters);
                        updateDivStatus(lstParameters);
                        incrementTotalInstance(strNodeParameterId,lstParameters,strNodeInstanceId);
                        updateServerConfForm.setLstParameterValue(lstParameters);
                        doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
                        return mapping.findForward(UPDATE_SERVER);

                    }else if(strAction.equalsIgnoreCase("save")){
                    	checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
                        lstParameters = updateServerConfForm.getLstParameterValue();
                        
                        /* convert String to Code */
                        
                        StringBuffer strCipherSuiteList = new StringBuffer();
                        if(lstParameters != null && lstParameters.size() >0){
                        	for(NetConfParameterValueBean netConfParamValueBean:lstParameters){
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
                        
                        
                        if(lstParameters !=null){
                        	for(int i=0;i<lstParameters.size() ;i++){
                        		if(lstParameters.get(i).getAlias().equals("password") && lstParameters.get(i).getName().equals("Password")){
                        			/* Encrypt server password */
                        			String encryptedPassword = PasswordEncryption.getInstance().crypt(lstParameters.get(i).getValue(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
                        			lstParameters.get(i).setValue(encryptedPassword);
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
                        request.setAttribute("responseUrl","/listNetServerConfiguration.do?netserverid="+netServerId);
                        netServerBLManager.saveNetConfigurationValues(lstValueData,cnfInstanceId,netServerId);
                        ActionMessage message = new ActionMessage("servermgr.update.server.configuration.success");
                        ActionMessages messages = new ActionMessages();
                        messages.add("information",message);
                        saveMessages(request,messages);
                        return mapping.findForward(SUCCESS_FORWARD);

                    }else if(strAction.equalsIgnoreCase("delete")){
                    	checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
                        lstParameters = updateServerConfForm.getLstParameterValue();
                        String strNodeInstanceId=request.getParameter("nodeInstanceId");

                        if(lstParameters != null){
                            Iterator<NetConfParameterValueBean> itrParameters = lstParameters.iterator();
                            while(itrParameters.hasNext()){
                                NetConfParameterValueBean bean = itrParameters.next();
                                if(bean.getInstanceId().startsWith(strNodeInstanceId)){
                                    decrementTotalInstance(bean.getParameterId(),lstParameters,strNodeInstanceId);
                                    itrParameters.remove();
                                }
                            }
                            updateDivStatus(lstParameters);
                            lstParameters = updateValuePoolList(lstParameters);
                            updateServerConfForm.setLstParameterValue(lstParameters);
                            doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
                            return mapping.findForward(UPDATE_SERVER);
                        }
                    }

                }



                if(netConfParamData != null){
                    Logger.logDebug(MODULE,"netConfParamData.getNetConfigParamValues :" + netConfParamData.getNetConfigParamValues());

                    if(netConfParamData.getNetConfigParamValues() != null && netConfParamData.getNetConfigParamValues().size() > 0){

                        Iterator itr = netConfParamData.getNetConfigParamValues().iterator();


                        while(itr.hasNext()){

                            INetConfigurationValuesData netConfigValuesData = (INetConfigurationValuesData)itr.next();
                            if(netConfigValuesData.getConfigInstanceId().equals(cnfInstanceId)){
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
                    
                    if(lstParameters !=null){
                    	for(int i=0;i<lstParameters.size() ;i++){
                    		if(lstParameters.get(i).getAlias().equals("password") && lstParameters.get(i).getName().equals("Password")){
                    			/* Encrypt server password */
                    			String encryptedPassword = PasswordEncryption.getInstance().decrypt(lstParameters.get(i).getValue(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
                    			lstParameters.get(i).setValue(encryptedPassword);
                    		}
                    	}
                    }
                    
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
                            						if(listOfCipherSuit.trim().equals("NONE")){
                            							netConfParamValueBean.setValue("NONE");
                            						}else if(seString.getServerCertificateId().equals(Long.parseLong(listOfCipherSuit))){
                                						netConfParamValueBean.setValue(seString.getServerCertificateName());
                                					}
                            				}
                        				}
                        			}else{
                        				netConfParamValueBean.setValue("NONE");
                        			}
                        			
                    	  }
                    		
                    		if(netConfParamValueBean.getAlias().equals("certificate-profile")){
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
                    
                    updateServerConfForm.setLstParameterValue(lstParameters);
                }
                return mapping.findForward(UPDATE_SERVER);
            }
            catch(ActionNotPermitedException e){
                Logger.logError(MODULE,"Error :-" + e.getMessage());
                printPermitedActionAlias(request);
                ActionMessages messages = new ActionMessages();
                messages.add("information", new ActionMessage("general.user.restricted"));
                saveErrors(request, messages);
                return mapping.findForward(INVALID_ACCESS_FORWARD);
            }
            catch(Exception exception){
                Logger.logError(MODULE,"Error in action operation,reason :"+exception.getMessage());
                Logger.logTrace(MODULE,exception);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exception);
    			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("servermgr.update.server.configuration.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveErrors(request,messages);
                return mapping.findForward(FAILURE_FORWARD);
            }
        }
        Logger.logTrace(MODULE,"Returning error forward from "+getClass().getName());
        ActionMessage message = new ActionMessage("servermgr.update.server.configuration.failure");
        ActionMessages messages = new ActionMessages();
        messages.add("information",message);
        saveErrors(request,messages);
        return mapping.findForward(FAILURE_FORWARD);
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

	private List<DiameterPeerData> getDiameterPeerList(String configId) throws DataManagerException{
    	List<DiameterPeerData> diameterPeerDataList = new ArrayList<DiameterPeerData>();
    	if(ServiceTypeConstants.DIAMETER_STACK_CONFIG_ID.equals(configId) || ServiceTypeConstants.RM_DIAMETER_STACK_CONFIG_ID.equals(configId) ){
    		DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
    		diameterPeerDataList = diameterPeerBLManager.getDiameterPeerList();
    	}
    	return diameterPeerDataList;
    }


}
