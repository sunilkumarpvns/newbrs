package com.elitecore.netvertexsm.web.servermgr.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.util.remotecommunications.RemoteMethodInvocator;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;

public class ViewPolicyStatusAction extends BaseWebAction{
    private static final String FAILURE_FORWARD = "failure";
    private static final String VIEW_FORWARD = "netServerPolicyStatus";
    private static final String MODULE =ViewPolicyStatusAction.class.getSimpleName();
    
    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
    	Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
    	List netServerTypeList = null;
    	NetServerBLManager netServerBLManager = new  NetServerBLManager();
    	
    	INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
		netServerTypeList = netServerBLManager.getNetServerTypeList();
		
    	ActionMessages messages = new ActionMessages();
    	
		try {
    		String strNetServerId = request.getParameter("netServerId");
        	Long netServerId=null;
    		if(strNetServerId != null){
    			netServerId = Long.parseLong(strNetServerId);
    		}
    		netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);
    		List<PolicyDetail> policyDetail=(List<PolicyDetail>)RemoteMethodInvocator.invokeRemoteMethod(netServerInstanceData, MBeanConstants.POLICYCONFIGURATION,"retrievePolicyDetail",null,null);
			request.setAttribute("netServerInstanceData",netServerInstanceData);
			request.setAttribute("netServerTypeList",netServerTypeList);
			request.setAttribute("netServerId",netServerId);
			request.setAttribute("policyDetails",policyDetail);
    		
    		Logger.logInfo(MODULE,"Total policies successfully cached are :"+policyDetail.size());
    		return mapping.findForward(VIEW_FORWARD);
    
    	}catch(ActionNotPermitedException e){
    		Logger.logError(MODULE,"Error in Policy status Operation. Reason: " + e.getMessage());
    		return actionNotPermittedFailure(mapping,request);
    	}catch (Exception e) {
    		Logger.logTrace(MODULE,"Error in Policy status Operation. Reason: "+ getClass().getName());
    		Logger.logTrace(MODULE,e);
    		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    		request.setAttribute("errorDetails", errorElements);
    		ActionMessage message = new ActionMessage("servermgr.server.policy.status.failure");
    		
    		if(e instanceof UnidentifiedServerInstanceException ){
    			messages.add("information",new ActionMessage("servermgr.server.invalididentifier"));
    			saveErrors(request,messages);
    			return mapping.findForward(FAILURE_FORWARD);
    		}else if(e instanceof CommunicationException){
    			messages.add("information",new ActionMessage("servermgr.server.communicate.fail",netServerInstanceData.getAdminHost()));
    			messages.add("information",new ActionMessage("servermgr.server.down"));
    			saveErrors(request,messages);
    			return mapping.findForward(FAILURE_FORWARD);
    		}
    		messages.add("information",message);
    		saveErrors(request,messages);
    	}
		return mapping.findForward(FAILURE_FORWARD);
    	
    }

}
