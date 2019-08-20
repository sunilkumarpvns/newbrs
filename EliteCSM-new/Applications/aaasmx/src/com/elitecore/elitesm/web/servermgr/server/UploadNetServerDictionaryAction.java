package com.elitecore.elitesm.web.servermgr.server;

import java.io.IOException;
import java.util.List;

/*
 * Comment By Kaushik - Old JMX Code Replaced By new
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServerErrorException;
import javax.management.remote.JMXServiceURL;*/
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.UploadNetServerDictionaryForm;
import com.elitecore.passwordutil.PasswordEncryption;


public class UploadNetServerDictionaryAction extends BaseDictionaryAction{

	private static final String SUCCESS_FORWARD = "success";
	private static final String UPLOAD_FORWARD = "uploadRadiusDictionary";
	private static final String FAILURE_FORWARD = "failure";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter the execute method of :"+getClass().getName());
		IRemoteCommunicationManager remoteCommunicationManager = null;

		try{
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			//NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
			//NetDriverBLManager netDriverBLManager = new NetDriverBLManager();
			UploadNetServerDictionaryForm netServerDictionaryForm = (UploadNetServerDictionaryForm)form;
			//String staffId = ((SystemLoginForm)request.getSession().getAttribute("netServerDictionaryForm")).getUserId();
			String strNetServerId = request.getParameter("netServerId");
			
			String fileGroup = netServerDictionaryForm.getFileGroup();
			List netServerTypeList = null;
			String netServerId=null;
			if(strNetServerId == null){
				netServerId = netServerDictionaryForm.getNetServerId();
			}
			netServerDictionaryForm.setNetServerId(netServerId);
			String action = netServerDictionaryForm.getAction();
			INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);

			if(netServerId != null ){
				FormFile formFile = netServerDictionaryForm.getConfigurationFile();
				if(action != null && action.equalsIgnoreCase("upload") ){
					if(formFile.getInputStream() != null){

						/*code added for Dictionary Upload*/						
						String ipAddress = netServerInstanceData.getAdminHost();
						int port = netServerInstanceData.getAdminPort();

						byte[] dataBytes = null;
						dataBytes = formFile.getFileData();
						//int flag = formFile.getInputStream().read(dataBytes);
						/*
						 * Comment By Kaushik - Old JMX Code Replaced By new	
					JMXServiceURL url = null;
				        JMXConnector jmxConnector = null;
				        MBeanServerConnection mbeanServerConnection = null;
				        ObjectName objName = null;
				        url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+ipAddress+":"+port+"/jmxrmi");
				        jmxConnector = JMXConnectorFactory.connect(url, null);
				        mbeanServerConnection = jmxConnector.getMBeanServerConnection();
				        objName =  new ObjectName ("Elitecore:type=EliteAdmin");
						 */

						Object[] objArgValues = {formFile.getFileName(),dataBytes,fileGroup};
						String[] strArgTypes = {"java.lang.String",byte[].class.getName(),"java.lang.String"};

						String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);				        
						remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
						remoteCommunicationManager.init(ipAddress,port,netServerCode,true);
						remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","saveFile",objArgValues,strArgTypes);

						/*Comment By Kaushik - Old JMX Code Replaced By new
						 *  mbeanServerConnection.invoke(objName,"saveDictionary",objArgValues,strArgTypes);
				           jmxConnector.close();*/

						//ObjectInputStream ois = new ObjectInputStream(formFile.getInputStream());
						//EliteNetServerData eliteNetServerData = (EliteNetServerData)ois.readObject();

//						if(eliteNetServerData.getVersion().equalsIgnoreCase(netServerInstanceData.getVersion())){
						//netServerBLManager.updateServerDetails(serverId,eliteNetServerData,staffId);
						request.setAttribute("responseUrl","/manageRadiusDictionary.do?netServerId="+netServerId);
						ActionMessage message = new ActionMessage("servermgr.server.dictionary.upload.success");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveMessages(request,messages);
						return mapping.findForward(SUCCESS_FORWARD);

						/*						}else{
							Logger.logTrace(MODULE,"Configuration Object version mismatch.");
							ActionMessage message = new ActionMessage("servermgr.server.import.failure");
							ActionMessage message1 = new ActionMessage("servermgr.server.import.version.mismatch");
							ActionMessages messages = new ActionMessages();
							messages.add("warn",message);
							messages.add("warn",message1);			
							saveErrors(request,messages);
						}
						 */	
					}else{
						Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
						ActionMessage message = new ActionMessage("servermgr.server.dictionary.upload.failure");
						ActionMessage message1 = new ActionMessage("servermgr.server.dictionary.invalidfileinput");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						messages.add("information",message1);
						saveErrors(request,messages);
						return mapping.findForward(FAILURE_FORWARD);
					}

				}else{
					System.out.println("TO FORWARD");
					netServerTypeList = netServerBLManager.getNetServerTypeList();
					request.setAttribute("netServerInstanceData",netServerInstanceData);
					request.setAttribute("netServerTypeList",netServerTypeList);
					request.setAttribute("netServerTypeList",netServerTypeList);
					
					
					Object[] objArgValues = {};
					String[] strArgTypes = {};
					String ipAddress = netServerInstanceData.getAdminHost();
					int port = netServerInstanceData.getAdminPort();
					String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);				        
					remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
					remoteCommunicationManager.init(ipAddress,port,netServerCode,true);
					String[] fileGroups= (String[])remoteCommunicationManager.getAttribute("Elitecore:type=EliteAdmin", "AllFileGroups");
					request.setAttribute("fileGroups",fileGroups);
					if(fileGroups!=null && fileGroups.length>0)
					{
						netServerDictionaryForm.setFileGroup(fileGroups[0]);
					}
					return mapping.findForward(UPLOAD_FORWARD);
				}
			}else{
				Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
				ActionMessage message = new ActionMessage("servermgr.server.dictionary.upload.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}

		} catch (UnidentifiedServerInstanceException commExp) {
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
			Logger.logError(MODULE,"Error during operation,reason :"+seException.getMessage());
			Logger.logTrace(MODULE,seException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(seException);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.server.dictionary.jmx.server.error");
			ActionMessages messages = new ActionMessages();
			messages.add("warn",message);
			saveErrors(request,messages);

		}
		catch(IOException connectionError){
			Logger.logError(MODULE,"Error during operation,reason :"+connectionError.getMessage());
			Logger.logTrace(MODULE,connectionError);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(connectionError);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.server.dictionary.connection.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("warn",message);
			saveErrors(request,messages);
		}
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
