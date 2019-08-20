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
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.AttributeMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.attrmapping.form.PacketMappingForm;

public class CreatePacketMappingAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_PACKET_MAPPING;
	private static final String MODULE = "PACKET-MAPPING"; 
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		if((checkAccess(request, ACTION_ALIAS))){
			try{
				PacketMappingForm packetMapForm = (PacketMappingForm) form;
				MappingBLManager mappingBLManager = new MappingBLManager();
				PacketMappingData packetMapData = new PacketMappingData();
				packetMapData.setName(packetMapForm.getName());
				packetMapData.setDescription(packetMapForm.getDescription());
				packetMapData.setCommProtocol(packetMapForm.getCommProtocol());
				packetMapData.setPacketType(packetMapForm.getPacketType());
				packetMapData.setType(packetMapForm.getType());
								
				String[] attribute = request.getParameterValues("attribute");
				String[] policyKey = request.getParameterValues("policyKey");
				String[] defaultValue = request.getParameterValues("defaultValue");
				String[] valueMapping = request.getParameterValues("valueMapping");
				
				List<AttributeMappingData> attributeMappings = new ArrayList<AttributeMappingData>();
				for(int i=0; i<attribute.length; i++) {
					AttributeMappingData attributeMappingData = new AttributeMappingData();
					attributeMappingData.setOrderNumber(i+1);
					attributeMappingData.setAttribute(attribute[i]);
					attributeMappingData.setPolicyKey(policyKey[i]);
					attributeMappingData.setDefaultValue(defaultValue[i]);
					attributeMappingData.setValueMapping(valueMapping[i]);
					attributeMappings.add(attributeMappingData);
				}
				packetMapData.setAttributeMappings(attributeMappings);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				mappingBLManager.create(packetMapData,staffData, ACTION_ALIAS);
				
				ActionMessage message = new ActionMessage("mapping.create.success", packetMapForm.getName());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
				request.setAttribute("responseUrl","/searchMapping.do");
				return mapping.findForward(SUCCESS_FORWARD);
			}catch (Exception e) {
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("mapping.create.failure"));
	            saveErrors(request, messages);
	            	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("mapping.error.heading","creating");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage); 	 
	            return mapping.findForward(FAILURE_FORWARD);
			}    
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("mapping.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	             
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}