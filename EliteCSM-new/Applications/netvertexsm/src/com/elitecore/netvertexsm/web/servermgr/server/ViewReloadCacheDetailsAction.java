package com.elitecore.netvertexsm.web.servermgr.server;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.util.mbean.data.live.EliteNetServerDetails;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.CacheDetailBean;
import com.elitecore.netvertexsm.web.servermgr.server.form.ViewReloadCacheDetailsForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class ViewReloadCacheDetailsAction  extends BaseWebAction{
    private static final String VIEW_FORWARD = "viewReloadCacheDetail";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE = "VIEW RELOAD CACHE DETAILS ACTION";
    //private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.VIEW_RELOAD_CACHE_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
        NetServerBLManager netServerBLManager = new NetServerBLManager();
        ViewReloadCacheDetailsForm viewReloadCacheDetailsForm = (ViewReloadCacheDetailsForm) form;
        IRemoteCommunicationManager remoteCommunicationManager = null;
        try {
           // checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            String strNetServerId = request.getParameter("netServerId");
			Long netServerId=null;
			if(strNetServerId != null){
				netServerId = Long.parseLong(strNetServerId);
			}
            if(netServerId != null){
                INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
                netServerInstanceData.setNetServerId(netServerId);
                netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
                List netServerTypeList = netServerBLManager.getNetServerTypeList();
                List netServerInstanceList = netServerBLManager.getNetServerInstanceList();

                request.setAttribute("netServerInstanceData",netServerInstanceData);
                request.setAttribute("netServerInstanceList",netServerInstanceList);			
                request.setAttribute("netServerTypeList",netServerTypeList);

                INetServerInstanceData serverInstanceData = netServerBLManager.getNetServerInstance(netServerId);
                String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);				
                remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
                remoteCommunicationManager.init(serverInstanceData.getAdminHost(),serverInstanceData.getAdminPort(),netServerCode,true);
                List<Map> cacheDetailsList=(List<Map>) remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION_DETAIL,"retriveReloadedCacheDetails",null,null);
                List<CacheDetailBean> cacheDetails = new ArrayList<CacheDetailBean>();
                if(cacheDetailsList!=null){
				    int cacheDetailSize = cacheDetailsList.size();
				    
				    for(int i=0;i<cacheDetailSize; i++){
							Map cacheMap = cacheDetailsList.get(i);												    
			  	            String name=null;
				            String source = null;
				            String lastUpdatedTime = null;
				            String lastReloadAttemptTime = null;
				            if(cacheMap!=null){
								name= (String)cacheMap.get("NAME");
								source= (String)cacheMap.get("SOURCE");
								lastUpdatedTime= (String)cacheMap.get("UPDATEDTIME");
								lastReloadAttemptTime= (String)cacheMap.get("LASTRELOADATTEMPTTIME");
								
							}
				            if(name==null) name="-";
				            if(source==null) source="-";
				            if(lastUpdatedTime==null) lastUpdatedTime="-";
				            if(lastReloadAttemptTime==null) lastReloadAttemptTime="-";
				            
				            CacheDetailBean cacheDetailBean = new CacheDetailBean();
				            cacheDetailBean.setName(name);
				            cacheDetailBean.setSource(source);
				            cacheDetailBean.setLastUpdatedTime(lastUpdatedTime);
				            cacheDetailBean.setLastReloadAttemptTime(lastReloadAttemptTime);
				            cacheDetails.add(cacheDetailBean);
				    }
                }
                
                
                viewReloadCacheDetailsForm.setCacheDetails(cacheDetails);
                List lstCacheDetails = viewReloadCacheDetailsForm.getCacheDetails();
                request.setAttribute("lstCacheDetails",lstCacheDetails);
                viewReloadCacheDetailsForm.setErrorCode("0");
                request.setAttribute("viewReloadCacheDetailsForm",viewReloadCacheDetailsForm);
                return mapping.findForward(VIEW_FORWARD);
            }else{
                Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
                ActionMessage message = new ActionMessage("servermgr.server.livedetails");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveErrors(request,messages);
                return mapping.findForward(FAILURE_FORWARD);
            }
        }catch (UnidentifiedServerInstanceException commExp) {
            commExp.printStackTrace();
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
            ActionMessage messageReason = new ActionMessage("servermgr.server.invalididentifier");			
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            messages.add("information",messageReason);
            saveErrors(request,messages);
            return mapping.findForward(FAILURE_FORWARD);			
        }catch(CommunicationException sue){
            Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
            Logger.logTrace(MODULE,sue);
            viewReloadCacheDetailsForm.setErrorCode("-1");
            request.setAttribute("viewReloadCacheDetailsForm",viewReloadCacheDetailsForm);
            EliteNetServerDetails eliteServerDetails = new EliteNetServerDetails();
            request.setAttribute("eliteLiveServerDetails",eliteServerDetails);
            return mapping.findForward(VIEW_FORWARD);			
        } catch (Exception e) {
            Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.communication.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        finally{
            try{
                if(remoteCommunicationManager != null)
                    remoteCommunicationManager.close();  
            }
            catch (Throwable e) {
                remoteCommunicationManager = null;
            }
        }
    }
}
