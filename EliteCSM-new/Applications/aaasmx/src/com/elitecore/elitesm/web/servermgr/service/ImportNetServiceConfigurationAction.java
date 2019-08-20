package com.elitecore.elitesm.web.servermgr.service;

import java.io.InvalidClassException;
import java.io.ObjectInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import com.elitecore.core.util.mbean.data.config.EliteNetServiceData;
import com.elitecore.elitesm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.service.forms.ImportNetServiceConfigurationForm;

public class ImportNetServiceConfigurationAction extends BaseWebAction {
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.IMPORT_SERVICE_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());

        try{
            checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
            NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
            ImportNetServiceConfigurationForm netServiceConfigurationForm = (ImportNetServiceConfigurationForm)form;
            
            String netServiceId = netServiceConfigurationForm.getNetServiceId();

            if(netServiceId != null){
                INetServiceInstanceData netServiceInstanceData = netServiceBLManager.getNetServiceInstance(netServiceId);				
                INetServiceTypeData netServiceTypeData = netServiceBLManager.getNetServiceType(netServiceInstanceData.getNetServiceTypeId());				
                FormFile formFile = netServiceConfigurationForm.getConfigurationFile();
                String filename = formFile.getFileName();

                if(filename.endsWith(".serviceconfig") || filename.endsWith(".config")){

                    if(formFile != null){
                        filename = formFile.getFileName();
                        ObjectInputStream ois = new ObjectInputStream(formFile.getInputStream());
                        EliteNetServiceData eliteNetServiceData = (EliteNetServiceData)ois.readObject();

                        if(netServiceTypeData.getAlias().equalsIgnoreCase(eliteNetServiceData.getNetServiceName())){
                            netServiceBLManager.updateServiceDetails(netServiceId,eliteNetServiceData,getLoggedInUserId(request),BaseConstant.HIDE_STATUS_ID);					
                            request.setAttribute("responseUrl","/importNetServiceDetail.do?netserviceid="+netServiceId);
                            ActionMessage message = new ActionMessage("servermgr.service.import.success");
                            ActionMessages messages = new ActionMessages();
                            messages.add("information",message);
                            saveMessages(request,messages);
                            return mapping.findForward(SUCCESS_FORWARD);
                        }else{
                            Logger.logTrace(MODULE,"Error during data Manager operation,reason : ");
                            ActionMessage message = new ActionMessage("servermgr.service.import.failure");
                            ActionMessage message1 = new ActionMessage("servermgr.service.import.invalid.service");
                            ActionMessages messages = new ActionMessages();
                            messages.add("warn",message);
                            messages.add("warn",message1);			
                            saveErrors(request,messages);
                            return mapping.findForward(FAILURE_FORWARD);						
                        }
                    }else{
                        Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
                        ActionMessage message = new ActionMessage("servermgr.service.import.failure");
                        ActionMessage message1 = new ActionMessage("servermgr.service.import.invalid.object");
                        ActionMessages messages = new ActionMessages();
                        messages.add("information",message);
                        messages.add("information",message1);					
                        saveErrors(request,messages);
                        return mapping.findForward(FAILURE_FORWARD);
                    }
                }
                else{
                    Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
                    ActionMessage message = new ActionMessage("servermgr.service.import.invalid.extension");
                    ActionMessages messages = new ActionMessages();
                    messages.add("information",message);
                    saveErrors(request,messages);
                    return mapping.findForward(FAILURE_FORWARD);
                }
            }else{
                Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
                ActionMessage message = new ActionMessage("servermgr.service.import.failure");
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
        }catch(ClassCastException cce){
            cce.printStackTrace();
            Logger.logTrace(MODULE,"Error during data Manager operation,reason :"+cce.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(cce);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.service.import.failure");
            ActionMessage message1 = new ActionMessage("servermgr.service.import.invalid.object");
            ActionMessages messages = new ActionMessages();
            messages.add("warn",message);
            messages.add("warn",message1);			
            saveErrors(request,messages);
        }catch(InvalidClassException ice){
            ice.printStackTrace();
            Logger.logTrace(MODULE,"Error during data Manager operation,reason :"+ice.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(ice);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.service.import.failure");
            ActionMessage message1 = new ActionMessage("servermgr.service.import.invalid.object");
            ActionMessages messages = new ActionMessages();
            messages.add("warn",message);
            messages.add("warn",message1);			
            saveErrors(request,messages);
        }catch(Exception managerExp){
            managerExp.printStackTrace();
            Logger.logTrace(MODULE,"Error during data Manager operation,reason :"+managerExp.getMessage());
            Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.service.import.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);

        }
        return mapping.findForward(FAILURE_FORWARD);
    }
}

