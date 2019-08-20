package com.elitecore.netvertexsm.web.servermgr.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.ViewCDRRecordsForm;
			 
public class InitViewCDRRecordsAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "initViewCDRRecords";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "Init CDRRecords Action";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());

		try{
			ViewCDRRecordsForm viewCDRRecordsForm = (ViewCDRRecordsForm)form;
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
			List netServerTypeList = netServerBLManager.getNetServerTypeList();
			String strNetServerId = request.getParameter("netserverid");
			Long netServerId=null;
			if(strNetServerId == null){
				netServerId = viewCDRRecordsForm.getNetServerId();
			}else{
				netServerId = Long.parseLong(strNetServerId);
			}
			
			if(netServerId != null){
				netServerInstanceData.setNetServerId(netServerId);
				netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
				request.setAttribute("netServerInstanceData",netServerInstanceData);
					
				viewCDRRecordsForm.setNetServerId(netServerId);
				request.setAttribute("netServerTypeList",netServerTypeList);
				return  mapping.findForward(SUCCESS_FORWARD);
			}else{
				Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
				ActionMessage message = new ActionMessage("servermgr.viewserver.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}
		}catch(Exception exp){
			Logger.logTrace(MODULE,exp.getMessage());
		}
			Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
			ActionMessage message = new ActionMessage("servermgr.viewserver.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
}
