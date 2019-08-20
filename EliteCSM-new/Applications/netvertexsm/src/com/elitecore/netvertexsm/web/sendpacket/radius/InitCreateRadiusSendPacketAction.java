package com.elitecore.netvertexsm.web.sendpacket.radius;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.sendpacket.form.SendRadiusPacketForm;

public class InitCreateRadiusSendPacketAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "initCreateRadiusSendPacket";
	private static final String MODULE = InitCreateRadiusSendPacketAction.class.getSimpleName();
	private String jsonData = "{\"code\": 4, \"attr\": [] }";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		SendRadiusPacketForm sendRadiusPacketForm = (SendRadiusPacketForm) form;
		String action = request.getParameter("action");
		if(action.equalsIgnoreCase("create")){
			String netServerId = (String) request.getSession().getAttribute("netserverid");
			NetServerBLManager blManager = new NetServerBLManager();
			final INetServerInstanceData netServerInstance = blManager.getNetServerInstance(Long.parseLong(netServerId));
			sendRadiusPacketForm.setIpAddress(netServerInstance.getAdminHost());
			sendRadiusPacketForm.setPort((long) netServerInstance.getAdminPort());
			sendRadiusPacketForm.setPacketData(jsonData);
			sendRadiusPacketForm.setTimeOut(3L);
		}
		request.setAttribute("sendRadiusPacketForm", sendRadiusPacketForm);
		return mapping.findForward(SUCCESS_FORWARD);
		
	}
		
}
