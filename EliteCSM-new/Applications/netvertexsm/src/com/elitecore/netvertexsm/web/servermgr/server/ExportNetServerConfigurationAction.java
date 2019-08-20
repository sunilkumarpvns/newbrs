package com.elitecore.netvertexsm.web.servermgr.server;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;
import com.elitecore.core.util.mbean.data.config.EliteNetServerData;
import com.elitecore.core.util.mbean.data.config.EliteNetServiceData;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigurationInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;

public class ExportNetServerConfigurationAction extends BaseWebAction {
    private static final String SUCCESS_FORWARD = "exportNetServerDetail";
    private static final String FAILURE_FORWARD = "failure";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.EXPORT_SERVER_ACTION;

    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());

        try{
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
            String strNetServerId = request.getParameter("netserverid");
            long netServerId=0L;
            if(strNetServerId!=null){
            	netServerId = Long.parseLong(strNetServerId.trim());
            }
            if(netServerId>0){
                INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);
                INetServerTypeData netServerTypeData = netServerBLManager.getNetServerType(netServerInstanceData.getNetServerTypeId());
                List lstServerConfigInstance = netServerBLManager.getServerConfigurationInstance(netServerId);
                EliteNetServerData eliteNetServerData = new EliteNetServerData();

                eliteNetServerData.setNetServerId(Long.toString(netServerId));
                eliteNetServerData.setNetConfigurationList(new ArrayList());
                eliteNetServerData.setNetServiceList(new ArrayList());
                eliteNetServerData.setNetServerName(netServerTypeData.getAlias());
                eliteNetServerData.setVersion(netServerInstanceData.getVersion());
                eliteNetServerData.setPluginList(new ArrayList());
                for(int i=0;i<lstServerConfigInstance.size();i++){
                    NetConfigurationInstanceData netConfigInstanceData = (NetConfigurationInstanceData)lstServerConfigInstance.get(i);

                    byte[] returnServerBytes = netServerBLManager.getServerConfigurationStream(netServerId,netConfigInstanceData.getConfigId());
                    EliteNetConfigurationData eliteNetConfigData = new EliteNetConfigurationData();
                    eliteNetConfigData.setNetConfigurationData(returnServerBytes);
                    eliteNetConfigData.setNetConfigurationId(netConfigInstanceData.getConfigId());
                    eliteNetConfigData.setNetConfigurationKey(netConfigInstanceData.getNetConfiguration().getAlias());
                    eliteNetServerData.getNetConfigurationList().add(eliteNetConfigData);

                }
             
                List lstServicesInstance = netServiceBLManager.getNetServiceInstanceList(netServerId);
                for(int j=0;j<lstServicesInstance.size();j++){				
                    NetServiceInstanceData netServiceInstanceData = (NetServiceInstanceData)lstServicesInstance.get(j);

                    EliteNetServiceData eliteNetServiceData = new EliteNetServiceData();
                    INetServiceTypeData netServiceTypeData = netServiceBLManager.getNetServiceType(netServiceInstanceData.getNetServiceTypeId());
                    long serviceInstanceId = netServiceInstanceData.getNetServiceId();
                    eliteNetServiceData.setNetServiceId(Long.toString(serviceInstanceId));
                    eliteNetServiceData.setNetInstanceId(netServiceInstanceData.getInstanceId());
                    eliteNetServiceData.setNetConfigurationList(new ArrayList());
                    eliteNetServiceData.setNetDriverList(new ArrayList());
                    eliteNetServiceData.setNetSubServiceList(new ArrayList());
                    eliteNetServiceData.setNetServiceName(netServiceTypeData.getAlias());
                    eliteNetServiceData.setNetServiceInstanceName(netServiceInstanceData.getName());
                    eliteNetServiceData.setDescription(netServiceInstanceData.getDescription());

                    List lstServiceConfigInstance = netServiceBLManager.getServiceConfigurationInstance(serviceInstanceId);

                    for(int ja=0;ja<lstServiceConfigInstance.size();ja++){
                        NetConfigurationInstanceData netConfigInstanceData = (NetConfigurationInstanceData)lstServiceConfigInstance.get(ja);

                        byte[] returnServiceBytes = netServiceBLManager.getServiceConfigurationStream(serviceInstanceId,netConfigInstanceData.getConfigId());
                        EliteNetConfigurationData eliteNetConfigData = new EliteNetConfigurationData();
                        eliteNetConfigData.setNetConfigurationData(returnServiceBytes);
                        eliteNetConfigData.setNetConfigurationId(netConfigInstanceData.getConfigId());
                        eliteNetConfigData.setNetConfigurationKey(netConfigInstanceData.getNetConfiguration().getAlias());
                        eliteNetServiceData.getNetConfigurationList().add(eliteNetConfigData);
                    }

                  
                    eliteNetServerData.getNetServiceList().add(eliteNetServiceData);
                }
                
                
                String exportName = netServerInstanceData.getName()+"_"+netServerTypeData.getName();
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition","attachment;filename=\""+exportName+".serverconfig");
                ServletOutputStream out = response.getOutputStream();

                ObjectOutputStream objOutputStream = new ObjectOutputStream(out);
                objOutputStream.writeObject(eliteNetServerData);
                objOutputStream.close();

                request.setAttribute("responseUrl","/viewNetServerInstance.do?netserverid="+netServerId);
                ActionMessage message = new ActionMessage("servermgr.server.export.success");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveMessages(request,messages);
                return mapping.findForward(SUCCESS_FORWARD);
            }else{
                Logger.logTrace(MODULE,"Returning error forward from "+getClass().getName());
                ActionMessage message = new ActionMessage("servermgr.server.export.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveErrors(request,messages);
                return mapping.findForward(FAILURE_FORWARD);
            }
        }
        catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }
        catch(Exception managerExp){
            Logger.logTrace(MODULE,managerExp);
            Logger.logTrace(MODULE,"Error during data Manager operation,reason :"+managerExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
        }
        Logger.logTrace(MODULE,"Returning error forward from "+getClass().getName());
        ActionMessage message = new ActionMessage("servermgr.server.export.failure");
        ActionMessages messages = new ActionMessages();
        messages.add("information",message);
        saveErrors(request,messages);
        return mapping.findForward(FAILURE_FORWARD);
    }
    
}

