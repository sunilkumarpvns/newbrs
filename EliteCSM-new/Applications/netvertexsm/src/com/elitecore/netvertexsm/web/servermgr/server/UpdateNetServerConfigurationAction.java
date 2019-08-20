
package com.elitecore.netvertexsm.web.servermgr.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.sessionmgr.SessionBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationParameterData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationValuesData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.servermgr.BaseUpdateConfigurationAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.NetConfParameterValueBean;
import com.elitecore.netvertexsm.web.servermgr.server.form.UpdateNetServerConfigurationForm;

public class UpdateNetServerConfigurationAction extends BaseUpdateConfigurationAction {
    private static final String MODULE = "UPDATE_NET_SERVER_CONFIGURATION";
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String UPDATE_SERVER = "updateServer";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.UPDATE_CONFIGURATION_ACTION;
    private static final String VIEW_CONFIGURATION_ACTION = ConfigConstant.VIEW_CONFIGURATION_ACTION;

    private Long cnfInstanceId = null;
    private Long netServerId = null;  
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
                DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
                GatewayBLManager gatewayBLManager = new GatewayBLManager();
//                ClientProfileBLManager profileBLManager = new ClientProfileBLManager();
//                AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();
                SessionBLManager sessionBLManager = new SessionBLManager();
                DriverBLManager driverBLManager = new DriverBLManager();
                
                updateServerConfForm.setConfInstanceId(cnfInstanceId);
                updateServerConfForm.setConfigurationName(netServerBLManager.getNetConfigurationName(cnfInstanceId));
                updateServerConfForm.setServerName(netServerBLManager.getNetServerNameServerConfig(cnfInstanceId));
                List<DriverInstanceData> driverInstanceList = driverBLManager.getDriverInstanceList();
                

                String strAction =request.getParameter("action");
                INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
                netServerInstanceData.setNetServerId(netServerId);
                netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
                netServerTypeList = netServerBLManager.getNetServerTypeList();
                List netParamStartupModeList = netServerBLManager.getNetConfigParamStartupModeList("CONFIG_PARAM_STARTUP_TYPE");
                List<DatabaseDSData> datasourceList = databaseDSBLManager.getDatabaseDSList();
                List<GatewayData> gatewayDataList = gatewayBLManager.getDiameterGatewayDataList(CommunicationProtocol.DIAMETER.id);
//              List clientProfileList = profileBLManager.getRadiusClientProfileList();
//              List alertListenerList = alertListenerBLManager.getAlertListenerList("","");
               
                
                request.setAttribute("netServerInstanceData",netServerInstanceData);
                request.setAttribute("gatewayDataList",gatewayDataList);
                updateServerConfForm.setGatewayDataLists(gatewayDataList);
                request.setAttribute("netServerTypeList",netServerTypeList);
                request.setAttribute("driverInstanceList",driverInstanceList);
                updateServerConfForm.setDriverInstanceList(driverInstanceList);
                updateServerConfForm.setDatasourceList(datasourceList);
                request.setAttribute("datasourceList",datasourceList);
                request.setAttribute("startUpModeList",netParamStartupModeList);
                updateServerConfForm.setStartUpModeList(netParamStartupModeList);                
                
//                request.setAttribute("clientProfileList",clientProfileList);
//                request.setAttribute("alertListenerList",alertListenerList);
               
                
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
                        return mapping.findForward(UPDATE_SERVER);

                    }else if(strAction.equalsIgnoreCase("save")){
                    	checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
                        lstParameters = updateServerConfForm.getLstParameterValue();
                        List<INetConfigurationValuesData> lstValueData = new ArrayList<INetConfigurationValuesData>();
                        if(lstParameters != null){
                            for(int i=0; i< lstParameters.size() ; i++){
                                lstValueData.add(makeValueDataFromBean((NetConfParameterValueBean)lstParameters.get(i)));
                            }
                        }
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
                            return mapping.findForward(UPDATE_SERVER);
                        }
                    }

                }


                Long configInstatnceId = cnfInstanceId;

                INetConfigurationParameterData netConfParamData = netServerBLManager.getConfigurationParameterValues(cnfInstanceId);
                boolean existFlag = false;	
                Logger.logDebug(MODULE,"netConfParamData :" + netConfParamData );

                if(netConfParamData != null){
                    Logger.logDebug(MODULE,"netConfParamData.getNetConfigParamValues :" + netConfParamData.getNetConfigParamValues());

                    if(netConfParamData.getNetConfigParamValues() != null && netConfParamData.getNetConfigParamValues().size() > 0){

                        Iterator itr = netConfParamData.getNetConfigParamValues().iterator();


                        while(itr.hasNext()){

                            INetConfigurationValuesData netConfigValuesData = (INetConfigurationValuesData)itr.next();
                            if(netConfigValuesData.getConfigInstanceId()==cnfInstanceId){
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



}
