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

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.netvertexsm.blmanager.servergroup.ServerInstanceGroupBlManager;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.servergroup.data.ServerInstanceGroupRelationData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.ListNetServerInstanceForm;


public class MiscServerAction extends BaseWebAction{
    private static final String LIST_FORWARD = "netServerList";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE = "MISC SERVER ACTION";

    private static final String SUB_MODULE_ACTIONALIAS2 = ConfigConstant.DELETE_SERVER_INSTANCE_ACTION;

    @Override
    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logTrace(MODULE,"Entered execute method of "+getClass().getName());

        try {
            NetServerBLManager netServerBLManager = new NetServerBLManager();

            ListNetServerInstanceForm lstNetServerInstanceForm = (ListNetServerInstanceForm)form;
                String[] strSelectedIds = request.getParameterValues("select");
                String serverInstanceGroupId = request.getParameter("serverInstanceGroupId");
                List<Long> selectedIDList = new ArrayList<Long>();
                
                if(strSelectedIds != null){
                    for (int i = 0; i < strSelectedIds.length; i++) {
                    	selectedIDList.add(Long.parseLong(strSelectedIds[i]));
					}
                    checkActionPermission(request, SUB_MODULE_ACTIONALIAS2);
                    try {
                    	ServerInstanceGroupBlManager blManager = ServerInstanceGroupBlManager.getInstance();
                    	
                    	ServerInstanceGroupRelationData serverInstanceGroupRelationData = blManager.getServerInstanceRelationDatasBy(String.valueOf(selectedIDList.get(0)));
                    	
                    	if(CommonConstants.SECONDARY_INSATNCE != serverInstanceGroupRelationData.getServerWeightage()) { 
                    		blManager.swapInstances(serverInstanceGroupId);
                    	}
                    	netServerBLManager.deleteServer(selectedIDList);
                    }catch(DataManagerException hExp){
                    	Logger.logError(MODULE,"Error during Data Manager operation, reason: "+hExp.getMessage());
                    	Logger.logTrace(MODULE,hExp);
                    	Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
                    	request.setAttribute("errorDetails", errorElements);
                    	ActionMessage message = new ActionMessage("servermgr.delete.failure");
                    	ActionMessages messages = new ActionMessages();
                    	messages.add("information",message);
                    	saveErrors(request,messages);
                    	return mapping.findForward(FAILURE_FORWARD);
                    }
                }
            Logger.logTrace(MODULE, "Returning success forward from " + getClass().getName());            
            return mapping.findForward(LIST_FORWARD);
        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("servermgr.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);          

            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch (DataManagerException hExp) {
            Logger.logError(MODULE,"Error during Data Manager operation, reason: "+hExp.getMessage());
            Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
        }
        catch (Exception hExp) {
            Logger.logError(MODULE,"Error during Data Manager operation, reason: "+hExp.getMessage());
            Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
        }
        Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
        ActionMessage message = new ActionMessage("servermgr.update.failure");
        ActionMessages messages = new ActionMessages();
        messages.add("information",message);
        saveErrors(request,messages);
        
        ActionMessages errorHeadingMessage = new ActionMessages();
        message = new ActionMessage("servermgr.error.heading","deleting");
        errorHeadingMessage.add("errorHeading",message);
        saveMessages(request,errorHeadingMessage);          

        return mapping.findForward(FAILURE_FORWARD);
    }
}
