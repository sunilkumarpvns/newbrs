package com.elitecore.netvertexsm.web.gateway.attrmapping;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.gateway.MappingBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;

public class MiscPacketMappingAction extends BaseWebAction {
	private static final String SUCCESS = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.DELETE_PACKET_MAPPING;
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
            
		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				MappingBLManager mappingBLManager =new MappingBLManager();
				List<Long> packetMapIdList = new ArrayList<Long>();
				String[] strPacketMapIds = request.getParameterValues("select"); 
				if(mappingBLManager.isPacketMappingConfiguredWithGatewayProfile(strPacketMapIds)){
					throw new DataManagerException("PacketMapping Configured with Gateway Profile.");
				}			
				for(int i=0; i<strPacketMapIds.length; i++) {
					packetMapIdList.add(Long.parseLong(strPacketMapIds[i]));
				}
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				mappingBLManager.delete(packetMapIdList,staffData,ACTION_ALIAS);
				
				ActionMessage message = new ActionMessage("packetmapping.delete.success");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
				request.setAttribute("responseUrl","/searchMapping.do");
				
				return mapping.findForward(SUCCESS);
				
			}catch(DataManagerException dataManagerExcp){
				Logger.logError(MODULE,"Error during delete operation, reason : "+ dataManagerExcp.getMessage());
                Logger.logTrace(MODULE,dataManagerExcp);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dataManagerExcp);
      			request.setAttribute("errorDetails", errorElements);
      			ActionMessages messages = new ActionMessages();
      			ActionMessage message = new ActionMessage("packetmapping.delete.failure");
      			messages.add("information", message);     
                message = new ActionMessage("packetmapping.configured.in.gatewayprofile");
                messages.add("information", message);     
                saveErrors(request, messages);                
			}catch(Exception managerExp){
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
                Logger.logTrace(MODULE,managerExp);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
      			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("packetmapping.delete.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveErrors(request, messages);
			}
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("mapping.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	 
		    return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logWarn(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("mapping.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	 
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	public boolean isPacketMapConfiguredWithGatewayProfile(){
		return true;
}
}
