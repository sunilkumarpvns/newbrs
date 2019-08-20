package com.elitecore.netvertexsm.web.servermgr.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.LiveSNMPRFCData;
import com.elitecore.netvertexsm.web.servermgr.server.form.ViewSupportedRFCForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class ViewSupportedRFCAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";
	private static final String UPLOAD_FORWARD = "viewSupportedRFC";
	private static final String FAILURE_FORWARD = "failure";
	private static final String VIEW_FORWARD = "viewSupportedRFC";
	private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.VIEW_SUPPORTED_RFC_ACTION;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter the execute method of :"+getClass().getName());
		IRemoteCommunicationManager remoteCommunicationManager = null;
		ViewSupportedRFCForm viewSupportedRFCForm = (ViewSupportedRFCForm)form;
		try{
			checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
			NetServerBLManager netServerBLManager = new NetServerBLManager();


			String strNetServerId = request.getParameter("netServerId");
			Long netServerId=null;
			if(strNetServerId != null){
				netServerId = Long.parseLong(strNetServerId);
			}
			
			viewSupportedRFCForm.setNetServerId(netServerId);

			List netServerTypeList = null;
			if(netServerId == null)
				netServerId = viewSupportedRFCForm.getNetServerId();
			INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);
			netServerTypeList = netServerBLManager.getNetServerTypeList();
			request.setAttribute("netServerInstanceData",netServerInstanceData);
			request.setAttribute("netServerTypeList",netServerTypeList);
			request.setAttribute("netServerId", netServerId);

			if(netServerId != null ){

				String ipAddress = netServerInstanceData.getAdminHost();
				int port = netServerInstanceData.getAdminPort();

				Object[] objArgValues = {};
				String[] strArgTypes = {};
				//List supportedRFC = new ArrayList();
				HashMap supportedRFC = new HashMap();

				String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);				        
				remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
				remoteCommunicationManager.init(ipAddress,port,netServerCode,true);

				supportedRFC = (HashMap)remoteCommunicationManager.execute(MBeanConstants.SUPPORTED_RFC,"readSupportedRFCs",objArgValues,strArgTypes);

				List liveRFCDataList = new ArrayList();
				HashMap supportedRFCs = new HashMap();
				Iterator itr = supportedRFC.keySet().iterator();

				while(itr.hasNext()){

					LiveSNMPRFCData liveSNMPRFCData = new LiveSNMPRFCData();
					String key = (String)itr.next(); 
					liveSNMPRFCData.setName(key);
					liveSNMPRFCData.setDescription((String)supportedRFC.get(key));
					liveRFCDataList.add(liveSNMPRFCData);

				}

				viewSupportedRFCForm.setSupportedRFC(liveRFCDataList);
				viewSupportedRFCForm.setErrorCode("0");
				return mapping.findForward(UPLOAD_FORWARD);
			}

		}
		catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		catch (UnidentifiedServerInstanceException commExp) {
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
		}
		catch(CommunicationException seException){
			Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
			Logger.logTrace(MODULE,seException);
			viewSupportedRFCForm.setErrorCode("-1");
			EliteNetServerDetails eliteServerDetails = new EliteNetServerDetails();	
			request.setAttribute("eliteLiveServerDetails",eliteServerDetails);
			return mapping.findForward(VIEW_FORWARD);

		}
		/*catch(IOException connectionError){
			Logger.logError(MODULE,"Error during operation,reason :"+connectionError.getMessage());
                        Logger.logTrace(MODULE,connectionError);
			ActionMessage message = new ActionMessage("servermgr.server.dictionary.connection.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("warn",message);
			saveErrors(request,messages);
		}*/
		catch(Exception managerExp){
			Logger.logError(MODULE,"Error during data Manager operation,reason :"+managerExp.getMessage());
			Logger.logTrace(MODULE,managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.server.dictionary.upload.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("warn",message);
			saveErrors(request,messages);

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


		Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
		return mapping.findForward(FAILURE_FORWARD);
	}
}
