package com.elitecore.netvertexsm.web.servermgr.server;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.core.system.systemparameter.SystemParameterBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.ISystemParameterData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.server.form.ListNetServerInstanceForm;


public class ListNetServerInstanceAction extends BaseWebAction{
    private static final String SUCCESS_FORWARD ="listNetServerInstance";
    private static final String FAILURE_FORWARD ="failure";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.LIST_NET_SERVER_INSTANCE_ACTION;
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_SYSTEM_PARAMETERS_ACTION;
    
    private static final String MODULE = "LIST SERVER ACTION";

    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logTrace(MODULE,"Enter execute method of  "+getClass().getName());
       try {
           
           checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
           
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            ListNetServerInstanceForm lstNetServerInstanceForm = (ListNetServerInstanceForm)form;
            List serverList = netServerBLManager.getNetServerInstanceList();
            List netServerTypeList = netServerBLManager.getNetServerTypeList();
            lstNetServerInstanceForm.setListServer(serverList);
            request.setAttribute("netServerTypeList",netServerTypeList);
            
            /*start: code to retrieve customized menu name*/
			SystemParameterBLManager blManager = new SystemParameterBLManager();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String actionAlias = ACTION_ALIAS;
			List lstParameterValue = blManager.getList(staffData,actionAlias);
			Iterator listIterator = lstParameterValue.iterator();
			int index=0;
			String  customizedMenuName="Customized Menu";
			while(listIterator.hasNext()){
				ISystemParameterData dData = (ISystemParameterData)lstParameterValue.get(index++);
				if(dData.getParameterId()==29){
					customizedMenuName  = dData.getValue();
					Logger.logInfo(MODULE, " customizedMenuName  :>> "+customizedMenuName);
					break;
				}
			}					
			request.getSession().setAttribute("customizedMenuName", customizedMenuName);
			/*end: code to retrieve customized menu name*/
			
            return mapping.findForward(SUCCESS_FORWARD);
        }
        catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("servermgr.error.heading","listing");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	            
                        
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch (DataManagerException hExp) {
            Logger.logError(MODULE,"Error during data Manager operation, reason : "+hExp.getMessage());
            Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
        }catch (Exception hExp) {
            Logger.logError(MODULE,"Error during data Manager operation, reason : "+hExp.getMessage());
            Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
        }
        Logger.logTrace(MODULE,"Returning error forward from "+getClass().getName());
        ActionMessage message = new ActionMessage("servermgr.list.failure");
        ActionMessages messages = new ActionMessages();
        messages.add("information",message);
        saveErrors(request,messages);
        
        ActionMessages errorHeadingMessage = new ActionMessages();
        message = new ActionMessage("servermgr.error.heading","listing");
        errorHeadingMessage.add("errorHeading",message);
        saveMessages(request,errorHeadingMessage); 	            
        
        return mapping.findForward(FAILURE_FORWARD);
    }
}
