package com.elitecore.elitesm.web.servermgr.server;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.elitecore.elitesm.blmanager.radius.clientprofile.ClientProfileBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.ClientDetailBean;
import com.elitecore.elitesm.web.servermgr.server.forms.ViewNetServerLiveClientsDetailsForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class ViewNetServerLiveClientsDetailsAction  extends BaseDictionaryAction{
	private static final String VIEW_FORWARD = "viewNetServerLiveClientsDetails";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "VIEW SERVER CLIENTS DETAIL ACTION";
	private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.CONFIGURED_CLIENTS;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		ViewNetServerLiveClientsDetailsForm viewNetServerLiveClientsDetailsForm = (ViewNetServerLiveClientsDetailsForm) form;
		IRemoteCommunicationManager remoteCommunicationManager = null;
		try {
			checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String host = "";
			int port = 0;
			String strNetServerId = request.getParameter("netServerId");
			String netServerId=null;
    		if(strNetServerId != null){
    			netServerId = strNetServerId;
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
				host = serverInstanceData.getAdminHost();
				port = serverInstanceData.getAdminPort(); 

				String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);				
				remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
				remoteCommunicationManager.init(host,port,netServerCode,true);
				Map<String,List> clientsDetailsMap = (Map<String, List>) remoteCommunicationManager.execute(MBeanConstants.CLIENT,"clientDetails",null,null);
				
				List activeClientList = null;
				List unsupportedVendorClientList=null;
				List licenseExceededClientList=null ;
				
				if(clientsDetailsMap!=null){
					activeClientList = clientsDetailsMap.get("ACTIVE");
					unsupportedVendorClientList = clientsDetailsMap.get("UNSUPPORTED_VENDORS");
					licenseExceededClientList = clientsDetailsMap.get("LICENSE_EXCEEDED");
				}
				
				ClientProfileBLManager clientProfileBLManager = new ClientProfileBLManager();
				
				List<RadiusClientProfileData> clientProfileList = (List<RadiusClientProfileData>)clientProfileBLManager.getRadiusClientProfileList();
				
				Map<String, RadiusClientProfileData> clientProfileMap = new HashMap<String,RadiusClientProfileData>();
				
				if(clientProfileList!=null && !clientProfileList.isEmpty()){
					for (Iterator<RadiusClientProfileData> iterator = clientProfileList.iterator(); iterator.hasNext();) {
						RadiusClientProfileData radiusClientProfileData = iterator.next();
						clientProfileMap.put(radiusClientProfileData.getProfileName(), radiusClientProfileData);
					}
				}
				
				List<ClientDetailBean> activeClientDetailList =getClientDetailList(activeClientList,clientProfileMap);
				List<ClientDetailBean> unsupportedVendorClientDetailList =getClientDetailList(unsupportedVendorClientList,clientProfileMap);
				List<ClientDetailBean> licenseExceededClientDetailList =getClientDetailList(licenseExceededClientList,clientProfileMap);
				
				request.setAttribute("activeClientList",activeClientDetailList);
				request.setAttribute("unsupportedVendorClientList",unsupportedVendorClientDetailList);
				request.setAttribute("licenseExceededClientList",licenseExceededClientDetailList);
				request.setAttribute("netServerInstanceData",netServerInstanceData);
				request.setAttribute("clientProfileMap",clientProfileMap);
				doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
				viewNetServerLiveClientsDetailsForm.setErrorCode("0");				
				return mapping.findForward(VIEW_FORWARD);
			}else{
				Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
				ActionMessage message = new ActionMessage("servermgr.server.livedetails");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}
		}catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
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
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(sue);
			request.setAttribute("errorDetails", errorElements);
			viewNetServerLiveClientsDetailsForm.setErrorCode("-1");
			EliteNetServerDetails eliteServerDetails = new EliteNetServerDetails();
			request.setAttribute("eliteLiveServerDetails",eliteServerDetails);
			return mapping.findForward(VIEW_FORWARD);			
		} catch (Exception e) {
			Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.server.livedetails");
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
	private List<ClientDetailBean>  getClientDetailList(List<Map<String,String>> clientMapList,Map<String,RadiusClientProfileData> clientProfileMap){
		List<ClientDetailBean> clientDetailList = new ArrayList<ClientDetailBean>();
		if(clientMapList!=null && !clientMapList.isEmpty()){
			for (Iterator<Map<String,String>> iterator = clientMapList.iterator(); iterator.hasNext();) {
				Map<String,String>  clientDetailMap = iterator.next();
				ClientDetailBean clientDetailBean = new ClientDetailBean();
				clientDetailBean.setClientIP(clientDetailMap.get("IP"));
				clientDetailBean.setRequestExpiryTime(clientDetailMap.get("EXPIRY_TIME"));
				clientDetailBean.setSharedSecret(clientDetailMap.get("SHARED_SECRET"));
				String profileName = clientDetailMap.get("PROFILE_NAME");
				clientDetailBean.setClientProfileData(clientProfileMap.get(profileName));
				clientDetailList.add(clientDetailBean);
				Logger.logDebug(MODULE, "clientDetailBean :"+clientDetailBean);
			}
		}
		return clientDetailList; 
	}
}
