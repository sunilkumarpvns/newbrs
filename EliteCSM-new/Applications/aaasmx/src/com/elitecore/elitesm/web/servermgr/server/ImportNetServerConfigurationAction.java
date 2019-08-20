package com.elitecore.elitesm.web.servermgr.server;

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

import com.elitecore.commons.base.Strings;
import com.elitecore.core.util.mbean.data.config.EliteNetServerData;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.ImportNetServerConfigurationForm;

public class ImportNetServerConfigurationAction extends BaseDictionaryAction {
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.IMPORT_SERVER_ACTION;

    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());

        try{
            checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            ImportNetServerConfigurationForm netServerConfigurationForm = (ImportNetServerConfigurationForm)form;
            String serverId = netServerConfigurationForm.getNetServerId();
            INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(serverId);


            if(Strings.isNullOrBlank(serverId) == false){
                FormFile formFile = netServerConfigurationForm.getConfigurationFile();

                if(formFile.getFileName().endsWith(".serverconfig") || formFile.getFileName().endsWith(".config")){


                    if(formFile.getInputStream() != null){

                        ObjectInputStream ois = new ObjectInputStream(formFile.getInputStream());
                        EliteNetServerData eliteNetServerData = (EliteNetServerData) ois.readObject();

                        if(eliteNetServerData.getVersion().equalsIgnoreCase(netServerInstanceData.getVersion())){
                            netServerBLManager.updateServerDetails(serverId,eliteNetServerData,getLoggedInUserId(request),BaseConstant.HIDE_STATUS_ID);
                            request.setAttribute("responseUrl","/importNetServerDetail.do?netserverid="+serverId);
                            ActionMessage message = new ActionMessage("servermgr.server.import.success");
                            ActionMessages messages = new ActionMessages();
                            messages.add("information",message);
                            saveMessages(request,messages);
                            return mapping.findForward(SUCCESS_FORWARD);
                        }else{
                            Logger.logTrace(MODULE,"Configuration Object version mismatch.");
                            ActionMessage message = new ActionMessage("servermgr.server.import.failure");
                            ActionMessage message1 = new ActionMessage("servermgr.server.import.version.mismatch");
                            ActionMessages messages = new ActionMessages();
                            messages.add("warn",message);
                            messages.add("warn",message1);			
                            saveErrors(request,messages);
                        }
                    }else{
                        Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
                        ActionMessage message = new ActionMessage("servermgr.server.import.failure");
                        ActionMessages messages = new ActionMessages();
                        messages.add("information",message);
                        saveErrors(request,messages);
                        return mapping.findForward(FAILURE_FORWARD);
                    }
                }
                else{
                    Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
                    ActionMessage message = new ActionMessage("servermgr.server.import.invalid.extension");
                    ActionMessages messages = new ActionMessages();
                    messages.add("information",message);
                    saveErrors(request,messages);
                    return mapping.findForward(FAILURE_FORWARD);
                }
            }else{
                Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
                ActionMessage message = new ActionMessage("servermgr.server.import.failure");
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
            Logger.logDebug(MODULE,"Error during data Manager operation,reason :"+cce.getMessage());
            Logger.logTrace(MODULE,cce);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(cce);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.import.failure");
            ActionMessage message1 = new ActionMessage("servermgr.server.import.invalid.object");
            ActionMessages messages = new ActionMessages();
            messages.add("warn",message);
            messages.add("warn",message1);			
            saveErrors(request,messages);
        }catch(InvalidClassException ice){
            Logger.logDebug(MODULE,"Error during data Manager operation,reason :"+ice.getMessage());
            Logger.logTrace(MODULE,ice);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(ice);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.import.failure");
            ActionMessage message1 = new ActionMessage("servermgr.server.import.invalid.object");
            ActionMessages messages = new ActionMessages();
            messages.add("warn",message);
            messages.add("warn",message1);			
            saveErrors(request,messages);
        }catch(Exception managerExp){
            Logger.logDebug(MODULE,"Error during data Manager operation,reason :"+managerExp.getMessage());
            Logger.logTrace(MODULE,managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.import.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("warn",message);
            saveErrors(request,messages);
        }
        Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
        return mapping.findForward(FAILURE_FORWARD);
    }
}

