package com.elitecore.netvertexsm.web.servermgr.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

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
import com.elitecore.netvertexsm.web.servermgr.server.form.LiveDictionaryData;
import com.elitecore.netvertexsm.web.servermgr.server.form.ManageNetServerDictionaryForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class ManageNetServerDictionaryAction extends BaseWebAction{

	private static final String UPLOAD_FORWARD = "manageRadiusDictionary";
	private static final String FAILURE_FORWARD = "failure";
	private static final String SUCCESS_FORWARD = "success";
	private static final String VIEW_FORWARD = "manageRadiusDictionary";
	private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.MANAGE_LIVE_SERVER_DICTIONARIES_ACTION;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		IRemoteCommunicationManager remoteCommunicationManager = null;
		Map<String, List<String>> mapDictionary = null;
		ManageNetServerDictionaryForm manageNetServerDictionaryForm = (ManageNetServerDictionaryForm)form;
		try{
			checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			String strNetServerId = request.getParameter("netServerId");
			Long netServerId=null;
			if(strNetServerId != null){
				netServerId = Long.parseLong(strNetServerId);
			}
			manageNetServerDictionaryForm.setNetServerId(netServerId);

			List netServerTypeList = null;
			if(netServerId == null)
				netServerId = manageNetServerDictionaryForm.getNetServerId();
			String action = manageNetServerDictionaryForm.getAction();

			INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);
			netServerTypeList = netServerBLManager.getNetServerTypeList();
			request.setAttribute("netServerInstanceData",netServerInstanceData);
			request.setAttribute("netServerTypeList",netServerTypeList);
			request.setAttribute("netServerId", netServerId);

			if(netServerId != null ){
				if(action != null && action.equalsIgnoreCase("delete") ){

					String ipAddress = netServerInstanceData.getAdminHost();
					int port = netServerInstanceData.getAdminPort();
					
					String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
					remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
					remoteCommunicationManager.init(ipAddress,port,netServerCode,true);
					//EliteNetServerDetails eliteServerDetails = (EliteNetServerDetails) remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","readServerDetails",null,null);		
					String fileGroups[] = (String[])remoteCommunicationManager.getAttribute("Elitecore:type=EliteAdmin", "AllFileGroups");
					if(fileGroups!=null){
						for(int i=0;i<fileGroups.length;i++){
							String[] s = request.getParameterValues(fileGroups[i]);
							if(s!=null)
							{
								Object[] objArgValues={s,fileGroups[i]};
								String[] strArgTypes = {String[].class.getName(),"java.lang.String"};
								remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","deleteFile",objArgValues,strArgTypes);
							}
						}
					}


					request.setAttribute("responseUrl","/manageRadiusDictionary.do?netServerId="+netServerId);
					ActionMessage message = new ActionMessage("servermgr.server.dictionary.delete.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request,messages);
					return mapping.findForward(SUCCESS_FORWARD);

				}
				else{
					String ipAddress = netServerInstanceData.getAdminHost();
					int port = netServerInstanceData.getAdminPort();

					Object[] objArgValues = new Object[0];
					String[] strArgTypes = new String[0];
					String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
					remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
					remoteCommunicationManager.init(ipAddress,port,netServerCode,true);
					mapDictionary=(Map<String, List<String>>)remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","readFilesFromAllFileGroups",objArgValues,strArgTypes);
					Iterator<String> it = mapDictionary.keySet().iterator();
					Map<String,List<LiveDictionaryData>> mapDictionaries = new LinkedHashMap<String, List<LiveDictionaryData>>();
					while (it.hasNext()) {
						String key = it.next();
						Logger.logInfo(MODULE, "Dictionaries in group : "+key);
						List<String> listDictionaries = mapDictionary.get(key);
						List<LiveDictionaryData> liveDictionaryList = new ArrayList<LiveDictionaryData>();
						for(int i=0;i<listDictionaries.size();i++){
							LiveDictionaryData liveDictionaryData = new LiveDictionaryData();
							String dictionaryName = listDictionaries.get(i);
							liveDictionaryData.setName(dictionaryName);
							Logger.logInfo(MODULE, "-->Dictionary : "+dictionaryName);
							liveDictionaryData.setFileGroup(key);
							liveDictionaryList.add(liveDictionaryData);
						}
						mapDictionaries.put(key, liveDictionaryList);
					}

					manageNetServerDictionaryForm.setMapDictionary(mapDictionaries);
					manageNetServerDictionaryForm.setErrorCode("0");
					request.setAttribute("manageNetServerDictionaryForm", manageNetServerDictionaryForm);
					return mapping.findForward(UPLOAD_FORWARD);

				}
			}
			else{
				Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
				ActionMessage message = new ActionMessage("servermgr.server.dictionary.operation.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}

		} catch(ActionNotPermitedException e){
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
		}
		catch(CommunicationException seException){
			Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
			Logger.logTrace(MODULE,seException);
			manageNetServerDictionaryForm.setErrorCode("-1");
			EliteNetServerDetails eliteServerDetails = new EliteNetServerDetails();	
			request.setAttribute("eliteLiveServerDetails",eliteServerDetails);
			return mapping.findForward(VIEW_FORWARD);	

		}
		catch(Exception managerExp){
			Logger.logError(MODULE,"Error during data Manager operation,reason :"+managerExp.getMessage());
			Logger.logTrace(MODULE,managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.server.dictionary.operation.failure");
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
