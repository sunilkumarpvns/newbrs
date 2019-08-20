package com.elitecore.netvertexsm.web.servermgr.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.CreateServerForm;

public class InitCreateServerAction extends BaseWebAction {

    private static final String MODULE = "INIT CREATE SERVER ACTION";
    private static final String SUCCESS_FORWARD = "initCreateServer";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.CREATE_SERVER_INSTANCE_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logTrace(MODULE,"Entered execute method of "+getClass().getName());
        List lstNetServerType = null;
        try{
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            String serverGroupId = request.getParameter("groupId");
            CreateServerForm createServerForm = (CreateServerForm)form;
            createServerForm.setDescription(getDefaultDescription(request));
            createServerForm.setStatus("1");
            request.setAttribute("groupId", serverGroupId);
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            lstNetServerType = netServerBLManager.getNetServerTypeList();
        }
        catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("servermgr.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	            
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }
        catch(Exception exp){
            Logger.logError(MODULE,"Error during data Manager operation,reason :"+getClass().getName());
            Logger.logTrace(MODULE,exp);
            lstNetServerType = new ArrayList();
        }
        request.setAttribute("lstNetServerType",lstNetServerType);
        return mapping.findForward(SUCCESS_FORWARD);
    }

}
