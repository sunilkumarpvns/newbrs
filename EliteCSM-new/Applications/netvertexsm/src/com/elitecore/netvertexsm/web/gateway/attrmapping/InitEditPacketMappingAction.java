package com.elitecore.netvertexsm.web.gateway.attrmapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.gateway.MappingBLManager;
import com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.gateway.attrmapping.form.PacketMappingForm;

public class InitEditPacketMappingAction extends BaseWebAction {
	private static final String UPDATE_FORWARD = "initEditMapping";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_PACKET_MAPPING;
	private static final String MODULE = "PACKET-MAPPING";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				PacketMappingForm packetMappingForm = (PacketMappingForm) form;
	        	MappingBLManager mappingBLManager=new MappingBLManager();
	        	PacketMappingData packetMappingData = new PacketMappingData();
				
				String strMappingId = request.getParameter("mappingId");
				Long mappingId = Long.parseLong(strMappingId);

				if(mappingId != null ){
					packetMappingData.setPacketMapId(mappingId);
					packetMappingData = mappingBLManager.getPacketMappingData(packetMappingData);
					
					packetMappingForm.setPacketMapId(mappingId);
					packetMappingForm.setName(packetMappingData.getName());
					packetMappingForm.setDescription(packetMappingData.getDescription());
					packetMappingForm.setCommProtocol(packetMappingData.getCommProtocol());
					packetMappingForm.setType(packetMappingData.getType());
					packetMappingForm.setPacketType(packetMappingData.getPacketType());
					packetMappingForm.setAttributeMappings(packetMappingData.getAttributeMappings());
					packetMappingForm.setPacketMappingData(packetMappingData);
				}
				request.setAttribute("packetMappingForm", packetMappingForm);
				request.setAttribute("packetMappingData", packetMappingData);
				
				return mapping.findForward(UPDATE_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("update.init.failed"));
	            saveErrors(request, messages);
	            return mapping.findForward(FAILURE_FORWARD);
			} 
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
