package com.elitecore.netvertexsm.web.servermgr.service;

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

import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.spinterface.SPInterfaceBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.sprdriver.SPRBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationParameterData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationValuesData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.spr.data.SPRData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.servermgr.BaseUpdateConfigurationAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.NetConfParameterValueBean;
import com.elitecore.netvertexsm.web.servermgr.service.form.UpdateServiceConfigurationForm;

public class UpdateServiceConfigurationAction extends BaseUpdateConfigurationAction {

    private static final String MODULE = "UPDATE_SERVICE_CONFIGURATION";
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String UPDATE_SERVICE = "updateService";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.UPDATE_CONFIGURATION_ACTION;
    private static final String VIEW_CONFIGURATION_ACTION = ConfigConstant.VIEW_CONFIGURATION_ACTION;


    private Long cnfInstanceId = null;
    List lstNetDriverType = null;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response){
        List<NetConfParameterValueBean> lstParameters = new ArrayList<NetConfParameterValueBean>();
        Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
        NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
        NetServerBLManager netServerBLManager = new NetServerBLManager();		
        SPInterfaceBLManager sprDriverBLManager = new SPInterfaceBLManager();
        SPRBLManager sprBLManager = new SPRBLManager();
        UpdateServiceConfigurationForm updateServiceConfForm = (UpdateServiceConfigurationForm)form;
        
        
        INetServiceInstanceData netServiceInstanceData = new NetServiceInstanceData();
        cnfInstanceId = updateServiceConfForm.getConfInstanceId();
        
        Long netServiceId  = updateServiceConfForm.getNetServiceId();	
      
        if(cnfInstanceId != null && netServiceId != null){	
            try{

                checkActionPermission(request, VIEW_CONFIGURATION_ACTION);

                updateServiceConfForm.setConfInstanceId(cnfInstanceId);
                updateServiceConfForm.setConfigurationName(netServiceBLManager.getNetConfigurationName(cnfInstanceId));
                updateServiceConfForm.setServerName(netServerBLManager.getNetServerNameServiceConfig(cnfInstanceId));
                updateServiceConfForm.setServiceName(netServiceBLManager.getNetServiceNameServiceConfig(cnfInstanceId));
                netServiceInstanceData.setNetServiceId(netServiceId);
                netServiceInstanceData = netServiceBLManager.getNetServiceInstance(netServiceInstanceData.getNetServiceId());
                List netServiceTypeList = netServiceBLManager.getNetServiceTypeList();
                List netServerInstanceList = netServerBLManager.getNetServerInstanceList();
                List netParamStartupModeList = netServerBLManager.getNetConfigParamStartupModeList("CONFIG_PARAM_STARTUP_TYPE");	
                List<DriverInstanceData> driverInstanceList = sprDriverBLManager.getDriverInstanceList();
                List<SPRData> sprList = sprBLManager.getSPRDataList();
                
                request.setAttribute("netServiceInstanceData",netServiceInstanceData);
                request.setAttribute("netServiceTypeList",netServiceTypeList);
                request.setAttribute("netServerInstanceList",netServerInstanceList);
                request.setAttribute("startUpModeList",netParamStartupModeList);
                request.setAttribute("driverInstanceList",driverInstanceList);
                request.setAttribute("sprList", sprList);
                updateServiceConfForm.setStartUpModeList(netParamStartupModeList);
                updateServiceConfForm.setDriverInstanceList(driverInstanceList);
                String strAction =request.getParameter("action");

                if(strAction!=null){
                    Logger.logTrace(MODULE," Action : "+request.getParameter("action"));
                    // action : addNode

                    if(strAction.equalsIgnoreCase("addNode")){
                    	checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
                        String strNodeParameterId = request.getParameter("nodeParameterId");
                        String strNodeInstanceId =request.getParameter("nodeInstanceId");
                        String strChildTotalInstanceVal=request.getParameter("childTotalInstanceVal");

                        INetConfigurationInstanceData netConfigurationInstanceData=netServerBLManager.getConfigurationInstance(cnfInstanceId);
                        INetConfigurationParameterData netConfParameterData = netServiceBLManager.getNetConfigurationParameterData(strNodeParameterId,netConfigurationInstanceData.getConfigId());
                        lstParameters = updateServiceConfForm.getLstParameterValue();
                        lstParameters = getRecursiveNewNetConfParameterData(netConfParameterData,strNodeParameterId,lstParameters,cnfInstanceId,strNodeInstanceId,strChildTotalInstanceVal,strNodeInstanceId);
                        lstParameters = updateValuePoolList(lstParameters);
                        Collections.<NetConfParameterValueBean>sort(lstParameters);
                        updateDivStatus(lstParameters);
                        incrementTotalInstance(strNodeParameterId,lstParameters,strNodeInstanceId);
                        updateServiceConfForm.setLstParameterValue(lstParameters);
                        return mapping.findForward(UPDATE_SERVICE);

                    }else if(strAction.equalsIgnoreCase("save")){
                    	checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
                        lstParameters = updateServiceConfForm.getLstParameterValue();
                        List<INetConfigurationValuesData> lstValueData = new ArrayList<INetConfigurationValuesData>();
                        if(lstParameters != null){
                            for(int i=0; i< lstParameters.size() ; i++){
                                lstValueData.add(makeValueDataFromBean((NetConfParameterValueBean)lstParameters.get(i)));
                            }
                        }
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
                            return mapping.findForward(UPDATE_SERVICE);
                        }
                    }

                }


                Long configInstatnceId = cnfInstanceId;
                INetConfigurationParameterData netConfParamData = netServiceBLManager.getConfigurationParameterValues(configInstatnceId);


                boolean existFlag = false;				
                if(netConfParamData != null){
                    if(netConfParamData.getNetConfigParamValues() != null &&  netConfParamData.getNetConfigParamValues().size() > 0){
                        Iterator itr = netConfParamData.getNetConfigParamValues().iterator();
                        while(itr.hasNext()){
                            INetConfigurationValuesData netConfigValuesData = (INetConfigurationValuesData)itr.next();
                            if(netConfigValuesData.getConfigInstanceId()==configInstatnceId){
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


}
