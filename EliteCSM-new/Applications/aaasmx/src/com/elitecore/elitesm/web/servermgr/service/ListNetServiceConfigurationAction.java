package com.elitecore.elitesm.web.servermgr.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationParameterData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationValuesData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.exception.AddNewDriverConfigOpFailedException;
import com.elitecore.elitesm.datamanager.servermgr.exception.AddNewServiceConfigOpFailedException;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.service.forms.ListNetServiceConfigurationForm;

public class ListNetServiceConfigurationAction extends BaseDictionaryAction{
    private static final String SUCCESS_FORWARD = "listNetServiceConfiguration";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE = "LIST NET SERVICE CONFIGURATION ";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.LIST_SERVICE_CONFIGURATION_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
        String strNetServiceId = request.getParameter("netserviceid");

        try {
        	String netServiceId=null;
			if(strNetServiceId != null){
				netServiceId = strNetServiceId;
			}
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
            
            INetServiceInstanceData netServiceInstanceData = new NetServiceInstanceData();
            List configInstanceList = new ArrayList();
            List lstConfigInstanceId = new ArrayList();
            ListNetServiceConfigurationForm listNetServiceConfigurationForm = (ListNetServiceConfigurationForm)form;

            netServiceInstanceData.setNetServiceId(netServiceId);
            netServiceInstanceData = netServiceBLManager.getNetServiceInstance(netServiceInstanceData.getNetServiceId());

            Logger.logInfo(MODULE, "--START-- Checking New Configuration(ServiceType|DriverType) is/are added for "+netServiceId);
            netServiceBLManager.addNewServiceConfiguration(netServiceId);
            Logger.logInfo(MODULE, "--END--Checking New Configuration(ServiceType|DriverType) is/are added for "+netServiceId);

            String netConfigMapTypeId = "S";
            List lstconfigInstanceList = netServiceBLManager.getNetServiceConfigInstanceList(netServiceId,netConfigMapTypeId);

            String alias = "plugin-name";
            List<INetConfigurationParameterData> netConfigurationParameterDataList = netServiceBLManager.getNetConfigParameterDataList(netServiceInstanceData,alias);
            
            if(netConfigurationParameterDataList != null && netConfigurationParameterDataList.size() > 0 ){
            	
            	 for( INetConfigurationParameterData netConfigurationParameterData : netConfigurationParameterDataList){
            		for(int i=0;i<lstconfigInstanceList.size();i++){
                    	
            			if( netConfigurationParameterData != null ){
            			
	                        INetConfigurationInstanceData netConfigurationInstanceData = (INetConfigurationInstanceData)lstconfigInstanceList.get(i);
	                        configInstanceList.add(netConfigurationInstanceData);
	                        if(netConfigurationParameterData.getNetConfigParamValues() != null){
	                            Iterator iterator=netConfigurationParameterData.getNetConfigParamValues().iterator();
	
	                            while(iterator.hasNext()){
	                                INetConfigurationValuesData netConfigurationValuesData = (INetConfigurationValuesData)iterator.next();
	
	                                if(netConfigurationInstanceData.getConfigInstanceId()==netConfigurationValuesData.getConfigInstanceId()){
	                                    lstConfigInstanceId.add(netConfigurationValuesData);
	                                }
	                            }
	                        }
            			}
            		}
                    String  netConfigMapPluginTypeId = "P";
                    List lstPluginConfigInstanceList = netServiceBLManager.getNetServiceConfigInstanceList(netServiceId,netConfigMapPluginTypeId);
                    for(int i=0;i<lstPluginConfigInstanceList.size();i++){
                        INetConfigurationInstanceData netConfigurationInstanceData = (INetConfigurationInstanceData)lstPluginConfigInstanceList.get(i);
                        INetConfigurationData netConfigurationData =  netServiceBLManager.getRootParameterConfigurationData(netConfigurationInstanceData.getConfigId());

                        for(int j=0;j<lstConfigInstanceId.size();j++){
                            INetConfigurationValuesData netConfigurationValuesData = (INetConfigurationValuesData)lstConfigInstanceId.get(i);
                            if(netConfigurationValuesData.getValue() != null){
                                if(netConfigurationValuesData.getValue().equalsIgnoreCase(netConfigurationData.getAlias())){
                                    configInstanceList.add(netConfigurationInstanceData);
                                    Logger.logInfo(MODULE, " ::: netConfigurationInstanceData.toString(} ::: " + netConfigurationInstanceData.toString());
                                }
                            }
                        }
                    }
            	}
                
            }else{
                configInstanceList.addAll(lstconfigInstanceList);
                Logger.logInfo(MODULE, " ::: lstconfigInstanceList.size() ::: " + lstconfigInstanceList.size());
            }

            for (Iterator iterator = configInstanceList.iterator(); iterator
					.hasNext();) {
				INetConfigurationInstanceData temp = (INetConfigurationInstanceData) iterator.next();
				
				Logger.logInfo(MODULE, " ::: temp ::: " + temp.toString());
			}
            
            List configInstanceListWithoutDuplicates = new ArrayList();
            if( configInstanceList != null && configInstanceList.size() > 0 ){
            	 HashSet configInstanceListToSet = new LinkedHashSet(configInstanceList);
                 configInstanceListWithoutDuplicates = new ArrayList(configInstanceListToSet);
            }
            
            //driverConfigInstanceList
            //List configInstanceList = netServiceBLManager.getNetServiceConfigInstanceList(netServiceId);
            List netServiceTypeList = netServiceBLManager.getNetServiceTypeList();
            List netServerInstanceList = netServerBLManager.getNetServerInstanceList();
            listNetServiceConfigurationForm.setConfigInstanceList(configInstanceListWithoutDuplicates);
            listNetServiceConfigurationForm.setNetServiceId(netServiceId);

            //driver
            request.setAttribute("netServiceInstanceData",netServiceInstanceData);
            request.setAttribute("netServerInstanceList",netServerInstanceList);			
            request.setAttribute("netServiceTypeList",netServiceTypeList);
            
            /* Retrieving sub service list & sub driver list */
            Logger.logInfo(MODULE, "netServiceInstanceId:::"+netServiceId);
            request.setAttribute("listNetServiceConfigurationForm",listNetServiceConfigurationForm);
	        doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
            return mapping.findForward(SUCCESS_FORWARD);

        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch (AddNewServiceConfigOpFailedException hExp) {
            Logger.logError(MODULE, "Error during data Manager Operation, reasib :" + hExp.getMessage());
            Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.update.server.configuration.failure");
            ActionMessage messageReason = new ActionMessage("servermgr.server.addnewconfig.failed","Service");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            messages.add("information1", messageReason);
            saveErrors(request, messages);         
        }catch (AddNewDriverConfigOpFailedException hExp) {
            Logger.logError(MODULE, "Error during data Manager Operation, reasib :" + hExp.getMessage());
            Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.update.server.configuration.failure");
            ActionMessage messageReason = new ActionMessage("servermgr.server.addnewconfig.failed","Driver");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            messages.add("information1", messageReason);
            saveErrors(request, messages);         
        }
        catch (DataManagerException hExp) {
            Logger.logError(MODULE,"Error during data Manager Operation, reason :"+hExp.getMessage());
            Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.udpate.service.configuration.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
        }
        catch (Exception e) {
            Logger.logError(MODULE,"Error during data Manager Operation, reason :"+e.getMessage());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.udpate.service.configuration.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
        }
        return mapping.findForward(FAILURE_FORWARD);
    }
}
