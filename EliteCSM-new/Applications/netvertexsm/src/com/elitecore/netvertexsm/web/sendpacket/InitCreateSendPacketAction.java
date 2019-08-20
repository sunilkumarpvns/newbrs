package com.elitecore.netvertexsm.web.sendpacket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.sendpacket.form.SendPacketForm;

public class InitCreateSendPacketAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "initCreateSendPacket";
	private static final String MODULE = InitCreateSendPacketAction.class.getSimpleName();
	private String jsonData = "{\"App-Id\" : 16777238 , \"cc\": 272, \"isReq\": true, \"attr\": [] }";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		SendPacketForm sendPacketForm = (SendPacketForm) form;
		String action = request.getParameter("action");
		if(action.equalsIgnoreCase("create")){
			String netServerId = (String) request.getSession().getAttribute("netserverid");
			NetServerBLManager blManager = new NetServerBLManager();
			final INetServerInstanceData netServerInstance = blManager.getNetServerInstance(Long.parseLong(netServerId));
			sendPacketForm.setIpAddress(netServerInstance.getAdminHost());
			sendPacketForm.setPort((long) netServerInstance.getAdminPort());
			sendPacketForm.setPacketData(jsonData);
			sendPacketForm.setTimeOut(3L);
		}
		request.setAttribute("sendPacketForm", sendPacketForm);

		return mapping.findForward(SUCCESS_FORWARD);
	}

}
