package com.elitecore.netvertexsm.web.servermgr.server;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.data.PolicyCacheDetail;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.util.remotecommunications.RemoteMethodInvocator;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.ViewNetServerLiveDetailsForm;

public class SignalServerReloadPolicyAction  extends BaseWebAction{
    private static final String SUCCESS_FORWARD = "success";	
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE = SignalServerReloadPolicyAction.class.getSimpleName();
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.SIGNAL_SERVER_RELOAD_CACHE_ACTION;

    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
    	Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
    	NetServerBLManager netServerBLManager = new NetServerBLManager();
    	ViewNetServerLiveDetailsForm netServerLiveDetailsForm = (ViewNetServerLiveDetailsForm)form;
    	ActionMessages messages = new ActionMessages();
    	try {
    		checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
    		String strNetServerId = request.getParameter("netServerId");
    		Long netServerId=null;
    		if(strNetServerId != null){
    			netServerId = Long.parseLong(strNetServerId);
    		}
    			netServerLiveDetailsForm.setNetServerId(netServerId);
    			INetServerInstanceData serverInstanceData = netServerBLManager.getNetServerInstance(netServerId);
    			PolicyCacheDetail policyCacheDetail=(PolicyCacheDetail)RemoteMethodInvocator.invokeRemoteMethod(serverInstanceData, MBeanConstants.POLICYCONFIGURATION,"reloadPolicy",null,null);
    			Logger.logInfo(MODULE,"Total policies successfully cached are : "+policyCacheDetail.getSuccessCounter());
    			request.setAttribute("responseUrl","/viewNetServerLiveDetails.do?netServerId="+netServerId);
    			
    			if(policyCacheDetail.getStatus()==PolicyStatus.SUCCESS){
    				ActionMessage message = new ActionMessage("servermgr.server.reload.policy.success");
    				messages.add("information",message);
    			}else if(policyCacheDetail.getStatus()==PolicyStatus.FAILURE){
    				ActionMessage message=new ActionMessage("servermgr.server.reload.policy.failure");
    				ActionMessage messageRemark = new ActionMessage("servermgr.server.reload.policy.failure.suggestion",policyCacheDetail.getRemark());
    				messages.add("information",message);
    				messages.add("information",messageRemark);
    			}else{
    				ActionMessage status = new ActionMessage("servermgr.server.reload.policy.status");
    				messages.add("information",status);
    				if(policyCacheDetail.getSuccessCounter()>0){
    					ActionMessage successpolicycount = new ActionMessage("servermgr.server.reload.policy.successpolicycount",policyCacheDetail.getSuccessCounter());
    					messages.add("information",successpolicycount);
    				}if(policyCacheDetail.getPartialSuccessCounter()>0){
    					ActionMessage partialSuccessPolicyCount = new ActionMessage("servermgr.server.reload.policy.partialsuccesspolicycount",policyCacheDetail.getPartialSuccessCounter());
    					messages.add("information",partialSuccessPolicyCount);
    				}if(policyCacheDetail.getFailureCounter()>0){
    					ActionMessage failurePolicyCount = new ActionMessage("servermgr.server.reload.policy.failurepolicycount",policyCacheDetail.getFailureCounter());
    					messages.add("information",failurePolicyCount);
    				}
    			}
    			saveMessages(request,messages);
    			request.setAttribute("showButton",true);
    			request.setAttribute("netServerId",netServerId);
    			return mapping.findForward(SUCCESS_FORWARD);
    	}catch(ActionNotPermitedException e){
    		Logger.logError(MODULE,"Error in Reload Policy Operation.Reason: " + e.getMessage());
    		return actionNotPermittedFailure(mapping,request);
    	}catch (Exception e) {
    		Logger.logTrace(MODULE,"Error in Reload Policy Operation.Reason: "+ getClass().getName());
    		Logger.logTrace(MODULE,e);
    		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    		request.setAttribute("errorDetails", errorElements);
    		ActionMessage message = new ActionMessage("servermgr.server.reload.policy.failure");
    		messages.add("information",message);
    		if(e instanceof UnidentifiedServerInstanceException ){
    			messages.add("information",new ActionMessage("servermgr.server.invalididentifier"));
    		}else if(e instanceof CommunicationException){
    			messages.add("information",new ActionMessage("servermgr.server.communication.failure"));
    		}
    		saveErrors(request,messages);
    	}
    	return mapping.findForward(FAILURE_FORWARD);
    }
 }
