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

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.elitesm.blmanager.core.system.license.SMLicenseBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.ListNetServerInstanceForm;
//import com.sun.java_cup.internal.internal_error;
import com.elitecore.license.nfv.CentralizedLicenseCoordinator;


public class MiscServerAction extends BaseDictionaryAction{
    private static final String LIST_FORWARD = "netServerList";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE = "MISC SERVER ACTION";

    private static final String SUB_MODULE_ACTIONALIAS1 =ConfigConstant.CHANGE_SERVER_INSTANCE_ACTION;
    private static final String SUB_MODULE_ACTIONALIAS2 = ConfigConstant.DELETE_SERVER_INSTANCE_ACTION;

    @Override
    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logTrace(MODULE,"Entered execute method of "+getClass().getName());

        try {
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
    		
            ListNetServerInstanceForm lstNetServerInstanceForm = (ListNetServerInstanceForm)form;
            if(lstNetServerInstanceForm.getAction() != null){
                
                String[] strSelectedIds = request.getParameterValues("select");
                List<String> selectedIDList = new ArrayList<String>();
                
                if(strSelectedIds != null){
                    for (int i = 0; i < strSelectedIds.length; i++) {
                    	selectedIDList.add(strSelectedIds[i]);
					}
                    if(lstNetServerInstanceForm.getAction().equalsIgnoreCase("show")){
                        checkActionPermission(request, SUB_MODULE_ACTIONALIAS1);
                        netServerBLManager.updateStatus(selectedIDList,ServermgrConstant.SHOW_STATUS_ID);
                        doAuditing(staffData, SUB_MODULE_ACTIONALIAS1);
                    }else if(lstNetServerInstanceForm.getAction().equalsIgnoreCase("hide")){
                        checkActionPermission(request, SUB_MODULE_ACTIONALIAS1);
                        netServerBLManager.updateStatus(selectedIDList,ServermgrConstant.HIDE_STATUS_ID);
                        doAuditing(staffData, SUB_MODULE_ACTIONALIAS1);
                    }else if(lstNetServerInstanceForm.getAction().equalsIgnoreCase("delete")){
                    	checkActionPermission(request, SUB_MODULE_ACTIONALIAS2);
                    	try {
                    		CentralizedLicenseCoordinator coordinator = new CentralizedLicenseCoordinator(new SMLicenseBLManager());
                    		for ( String netServerInstanceId : selectedIDList ) {
								NetServerInstanceData serverInstanceData = null;
								serverInstanceData = (NetServerInstanceData) netServerBLManager.getNetServerInstance(netServerInstanceId);
								//deregistered the instance 
								coordinator.deregister(serverInstanceData.getName());
								
								String ip = serverInstanceData.getAdminHost();
								String port = String.valueOf(serverInstanceData .getAdminPort());
								Object[] objArgValues = {};
								String[] strArgTypes = {};
								String method = "deregisterLicense";
								try {
									//request to Eliteaaa for deregister the instance for license if it throws exception then continue to delete instance
									EliteUtility.doRemoteCommunication(MBeanConstants.LICENSE, method, ip, port, objArgValues, strArgTypes);
								} catch(Exception e) {
									Logger.getLogger().error(MODULE, "Server Communication failed. Connection can not established.");
								}
								//delete the staff user with instance in staff relation details
								netServerBLManager.deleteNetServerStaffRelDetailData(serverInstanceData.getName());
                    		}
                    		netServerBLManager.deleteServer(selectedIDList,staffData);
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
        return mapping.findForward(FAILURE_FORWARD);
    }
}
