package com.elitecore.elitesm.web.servermgr.service;

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
import com.elitecore.core.util.mbean.data.config.EliteNetServiceData;
import com.elitecore.elitesm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetConfigurationInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;

public class ExportNetServiceConfigurationAction extends BaseWebAction {
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.EXPORT_SERVICE_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());

        try{
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
            String strNetServiceId = request.getParameter("netserviceid");
			String netServiceId=null;
			if(strNetServiceId != null){
				netServiceId = strNetServiceId;
			}
            if(netServiceId != null){
                INetServiceInstanceData netServiceInstanceData = netServiceBLManager.getNetServiceInstance(netServiceId);			
                INetServiceTypeData netServiceTypeData = netServiceBLManager.getNetServiceType(netServiceInstanceData.getNetServiceTypeId());					
                EliteNetServiceData eliteNetServiceData = new EliteNetServiceData();
                String serviceInstanceId = netServiceInstanceData.getNetServiceId();

                eliteNetServiceData.setNetServiceId(serviceInstanceId.toString());
                eliteNetServiceData.setNetInstanceId(netServiceInstanceData.getInstanceId());
                eliteNetServiceData.setNetConfigurationList(new ArrayList());
                eliteNetServiceData.setNetDriverList(new ArrayList());
                eliteNetServiceData.setNetServiceName(netServiceTypeData.getAlias());
                eliteNetServiceData.setNetServiceInstanceName(netServiceInstanceData.getName());
                eliteNetServiceData.setDescription(netServiceInstanceData.getDescription());
                eliteNetServiceData.setNetSubServiceList(new ArrayList());
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


                String exportName = netServiceInstanceData.getName()+"_"+netServiceTypeData.getName();
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition","attachment;filename=\""+exportName+".serviceconfig");
                ServletOutputStream out = response.getOutputStream();

                ObjectOutputStream objOutputStream = new ObjectOutputStream(out);
                objOutputStream.writeObject(eliteNetServiceData);
                objOutputStream.close();

                request.setAttribute("responseUrl","/viewNetServiceInstance.do?netserviceid="+serviceInstanceId);
                ActionMessage message = new ActionMessage("servermgr.service.export.success");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveMessages(request,messages);
                return mapping.findForward(SUCCESS_FORWARD);
            }else{                                 
                Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
                ActionMessage message = new ActionMessage("servermgr.service.export.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveErrors(request,messages);
                return mapping.findForward(FAILURE_FORWARD);
            }
        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch(Exception managerExp){
            Logger.logError(MODULE,"Error during data Manager operation,reason :"+managerExp.getMessage());
            Logger.logTrace(MODULE,managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
        }
        Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
        ActionMessage message = new ActionMessage("servermgr.service.export.failure");
        ActionMessages messages = new ActionMessages();
        messages.add("information",message);
        saveErrors(request,messages);
        return mapping.findForward(FAILURE_FORWARD);
    }
   
}

