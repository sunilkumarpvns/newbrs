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
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.exception.AddNewServerConfigOpFailedException;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.ListNetServerConfigurationForm;

public class ListNetServerConfigurationAction extends BaseWebAction {

    private static final String SUCCESS_FORWARD = "listNetServerConfiguration";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE          = "LIST NET SERVER CONFIGURATION";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.LIST_NET_SERVER_CONFIGURATION_ACTION;
    
    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse reponse ) throws Exception {
        Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
        String strNetServerId = request.getParameter("netserverid");

        try {
			Long netServerId=0L;
			if(strNetServerId != null){
				netServerId = Long.parseLong(strNetServerId);
			}
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            
            NetServerBLManager netServerBLManager = new NetServerBLManager();
//            PluginBLManager pluginBLManager = new PluginBLManager();
            
            ListNetServerConfigurationForm listNetServerConfigurationForm = (ListNetServerConfigurationForm) form;
            INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
            netServerInstanceData.setNetServerId(netServerId);
            netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());

            Logger.logInfo(MODULE, "--START-- Checking New Configuration(ServerType) is/are added for "+netServerId);
            netServerBLManager.addNewServerConfiguration(netServerId);
            Logger.logInfo(MODULE, "--END-- Checking New Configuration(ServerType) is/are added for "+netServerId);

            List netServerconfigInstanceMapList = netServerBLManager.getNetServerConfigInstanceList(netServerId);
            
/*            List<PluginDetailBean> pluginConfigInstanceList = new ArrayList<PluginDetailBean>();
            List<PluginTypeData> pluginTypeList = pluginBLManager.getPluginTypeList(netServerInstanceData.getNetServerTypeId());

            List<PluginInstanceData> pluginInstanceList = pluginBLManager.getPluginInstanceList(netServerInstanceData.getNetServerId());
            for(int i=0;i<pluginInstanceList.size();i++){
            	PluginInstanceData pluginInstanceData = (PluginInstanceData)pluginInstanceList.get(i);
                List tempConfigInstanceList = pluginBLManager.getPluginConfigInstanceList(pluginInstanceData.getPluginInstanceId());
                PluginTypeData pluginTypeData = pluginBLManager.getPluginType(pluginInstanceData.getPluginTypeId()); 
                for(int j=0;j<tempConfigInstanceList.size();j++){
                    INetConfigurationInstanceData pluginConfigInstanceData = (INetConfigurationInstanceData)tempConfigInstanceList.get(j);
                    INetConfigurationData netConfigurationData = pluginBLManager.getRootParameterConfigurationData(pluginConfigInstanceData.getConfigId());
                    PluginDetailBean pluginDetailBean  = new PluginDetailBean();
                    pluginDetailBean.setName(pluginInstanceData.getName());
                    pluginDetailBean.setDisplayName(netConfigurationData.getAlias());
                    pluginDetailBean.setPluginTypeId(pluginInstanceData.getPluginTypeId());
                    pluginDetailBean.setDescription(pluginInstanceData.getDescription());
                    pluginDetailBean.setPluginInstanceId(pluginInstanceData.getPluginInstanceId());
                    pluginDetailBean.setNetConfigInstanceId(pluginConfigInstanceData.getConfigInstanceId());
                    pluginDetailBean.setNetServerId(netServerInstanceData.getNetServerId());
                    
                    pluginConfigInstanceList.add(pluginDetailBean);
                }

            }*/
            List netServerTypeList = netServerBLManager.getNetServerTypeList();
            listNetServerConfigurationForm.setConfigInstanceList(netServerconfigInstanceMapList);
            listNetServerConfigurationForm.setNetServerId(netServerId);
            
         /*   listNetServerConfigurationForm.setPluginConfigInstanceList(pluginConfigInstanceList);
            request.setAttribute("pluginTypeList", pluginTypeList);
            request.setAttribute("pluginConfigInstanceList",pluginConfigInstanceList);
         */
            request.setAttribute("netServerInstanceData", netServerInstanceData);
            request.setAttribute("netServerTypeList", netServerTypeList);

            Logger.logTrace(MODULE, "Returning success forward from " + getClass().getName());
            return mapping.findForward(SUCCESS_FORWARD);        
        }catch(ActionNotPermitedException ae){
            Logger.logError(MODULE,"Error :-" + ae.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("servermgr.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch(AddNewServerConfigOpFailedException hExp) {
            Logger.logError(MODULE, "Error during Data Manager Operation, reason :" + hExp.getMessage());
            Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.update.server.configuration.failure");
            ActionMessage messageReason = new ActionMessage("servermgr.server.addnewconfig.failed","Server");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            messages.add("information1", messageReason);
            saveErrors(request, messages);
        }catch(DataManagerException hExp) {
            Logger.logError(MODULE, "Error during Data Manager Operation, reason :" + hExp.getMessage());
            Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.update.server.configuration.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
        }catch(Exception e) {
            Logger.logError(MODULE, "Error when listing the server level configuration, reason :" + e.getMessage());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.update.server.configuration.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
        }
        
        ActionMessages errorHeadingMessage = new ActionMessages();
        ActionMessage message = new ActionMessage("servermgr.error.heading","updating");
        errorHeadingMessage.add("errorHeading",message);
        saveMessages(request,errorHeadingMessage);
        return mapping.findForward(FAILURE_FORWARD);
    }
}
