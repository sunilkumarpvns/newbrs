package com.elitecore.elitesm.web.servermgr.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
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
import com.elitecore.elitesm.web.servermgr.server.forms.ListUserfileAccountInformationForm;
import com.elitecore.elitesm.web.servermgr.server.forms.MiscUserfileDatasourceForm;
import com.elitecore.passwordutil.PasswordEncryption;


public class MiscUserfileDatasourceAction extends BaseDictionaryAction{

	private static final String LIST_FORWARD = "listUserfileAccountInformation";
	private static final String FAILURE_FORWARD = "failure";

	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response){
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());

		int port=0;
		String host=null;
		List netServerTypeList=null;
		/*
		 * Comment By Kaushik - Old JMX Code Replaced By new
		 * 
                JMXServiceURL url=null;
		JMXConnector jmxConnector=null;*/

		INetServerInstanceData netServerInstanceData=null;
		int status=0;
		String netServerId=null;
		String fileName="";
		int currentPageNumber = 0;
		String SUCCESS_FORWARD ="success"; 
		IRemoteCommunicationManager remoteCommunicationManager = null;
		try{

			MiscUserfileDatasourceForm miscUserfileDatasourceForm = (MiscUserfileDatasourceForm)actionForm;

			if(miscUserfileDatasourceForm.getAction() != null){

				String[] strSelectedIds = request.getParameterValues("select");
			    List<Long> selectedIDList = new ArrayList<Long>();
			    if(strSelectedIds!=null){
			    	for (int i = 0; i < strSelectedIds.length; i++) {
                	selectedIDList.add(Long.parseLong(strSelectedIds[i]));
			    	}
                }
				
				fileName=miscUserfileDatasourceForm.getSelectedFileName();
				netServerId=miscUserfileDatasourceForm.getNetserverid();
				
				if(miscUserfileDatasourceForm.getAction().equalsIgnoreCase("paging")){
							
					if(strSelectedIds!=null && strSelectedIds.length>0 ) {

						NetServerBLManager netServerBLManager = new NetServerBLManager();

						netServerInstanceData=netServerBLManager.getNetServerInstance(netServerId);
						port=netServerInstanceData.getAdminPort();
						host=netServerInstanceData.getAdminHost();

						/*
						 * Comment By Kaushik - Old JMX Code Replaced By new
                                                        url =  new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+host+":"+port+"/jmxrmi");
							jmxConnector = JMXConnectorFactory.connect(url, null);
							MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
							ObjectName objName = new ObjectName("Elitecore:type=EliteAdmin");*/

						String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);							
						remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
						remoteCommunicationManager.init(host,port,netServerCode,true);
						String[] argName={"java.lang.String","java.lang.String"};
						
						for(int i=0;i<strSelectedIds.length;i++){
							Object[] argValue={fileName,strSelectedIds[i]};
							/*
							 * Comment By Kaushik - Old JMX Code Replaced By new
							 * status = (Integer)mbeanServerConnection.invoke(objName,"deleteUserAccount",argValue,argName);
							 * */
							
							status = (Integer) remoteCommunicationManager.execute(MBeanConstants.RADIUS_USERFILE,"deleteUserAccount",argValue,argName);
							
							if(status <=0){
								Logger.logTrace(MODULE, "Returning Error Forward From :" + getClass().getName());
								ActionMessage message = new ActionMessage("userfiledatasource.deleteuseraccount.failed");
								ActionMessage message1 = new ActionMessage("userfiledatasource.updateuseraccount.readonlyfile");
								ActionMessages messages = new ActionMessages();
								messages.add("information",message);
								messages.add("information",message1);
								saveErrors(request,messages);
								return mapping.findForward(FAILURE_FORWARD);
							}
							
							
						}
						currentPageNumber = (int)getCurrentPageNumberAfterDel(strSelectedIds.length, miscUserfileDatasourceForm.getPageNumber(), miscUserfileDatasourceForm.getTotalPages()-1, miscUserfileDatasourceForm.getTotalRecords());
						
						request.setAttribute("responseUrl","/listUserfileAccountInformation.do?action=paging&pageNo="+currentPageNumber+"&selectedFileName="+miscUserfileDatasourceForm.getSelectedFileName()+"&netserverid="+miscUserfileDatasourceForm.getNetserverid());
			            ActionMessage message = new ActionMessage("userfiledatasource.deleteuseraccount.success");
						ActionMessages messages1 = new ActionMessages();
						messages1.add("information",message);
						saveMessages(request,messages1);
						return mapping.findForward(SUCCESS_FORWARD); 	
						
					}
				}
			}
              
			ListUserfileAccountInformationForm listUserfileAccountInformationForm=new ListUserfileAccountInformationForm();
			listUserfileAccountInformationForm.setAction("paging");
			listUserfileAccountInformationForm.setPageNo(currentPageNumber);
			listUserfileAccountInformationForm.setSelectedFileName(miscUserfileDatasourceForm.getSelectedFileName());
			listUserfileAccountInformationForm.setNetserverid(miscUserfileDatasourceForm.getNetserverid());

			return mapping.findForward(LIST_FORWARD);

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
			return mapping.findForward(FAILURE_FORWARD);			
		}
		catch(CommunicationException comm)
		{
			Logger.logError(MODULE,"Error in Operation.  Reason :" + comm.getMessage());
			Logger.logTrace(MODULE,comm);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(comm);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("userfiledatasource.deleteuseraccount.failed");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD); 
		}
		catch(Exception managerExp){
			Logger.logError(MODULE,"Error in Operation.  Reason :" + managerExp.getMessage());
			Logger.logTrace(MODULE,managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("userfiledatasource.deleteuseraccount.failed");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD); 
		}
		finally
		{
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

