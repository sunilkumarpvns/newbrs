package com.elitecore.elitesm.web.servermgr.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.server.forms.CreateServerForm;

public class InitCreateServerAction extends BaseWebAction {

    private static final String MODULE = "INIT CREATE SERVER ACTION";
    private static final String SUCCESS_FORWARD = "initCreateServer";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.CREATE_SERVER_INSTANCE_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logTrace(MODULE,"Entered execute method of "+getClass().getName());
        List lstNetServerType = null;
        try{
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            CreateServerForm createServerForm = (CreateServerForm)form;
            String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
            createServerForm.setDescription(getDefaultDescription(userName));
            createServerForm.setStatus("1");
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            lstNetServerType = netServerBLManager.getNetServerTypeList();
            
            StaffBLManager staffBlmanager = new StaffBLManager();

            List<StaffData> staffList = staffBlmanager.getList();
			createServerForm.setStaffDataList(staffList);
			
			request.setAttribute("staffList",staffList);
        	request.setAttribute("createServerForm",createServerForm);
        }
        catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
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
