package com.elitecore.netvertexsm.web.servermgr.server;


import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.RemoteCommunicationManagerFactory;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.ViewCDRRecordsForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class ViewCDRRecordsAction extends BaseWebAction{
	private static final String VIEW_FORWARD = "viewCDRRecords";
	private static final String EDIT_CDRRECORDS = "editCDRRecords";
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "VIEW CDR RECORDS";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter the execute method of :"+getClass().getName());
		ActionErrors errors = new ActionErrors();
		ViewCDRRecordsForm viewCDRRecordsForm = (ViewCDRRecordsForm)form;
                IRemoteCommunicationManager remoteConnectionManager = null;
		
		try {
			String host = "";
			int port = 0;
			Long netServerId = viewCDRRecordsForm.getNetServerId();
			NetServerBLManager netServerBLManager = new NetServerBLManager();
		
			if(netServerId != null){
				INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
				netServerInstanceData.setNetServerId(netServerId);
				netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
				List netServerTypeList = netServerBLManager.getNetServerTypeList();
			    
				request.setAttribute("netServerInstanceData",netServerInstanceData);
				request.setAttribute("netServerTypeList",netServerTypeList);
				
				if(request.getParameter("prmSessionId") != null){
					viewCDRRecordsForm.setSessionId(request.getParameter("prmSessionId"));
					viewCDRRecordsForm.setStrCallStartDate(request.getParameter("prmCallStartDate"));
					viewCDRRecordsForm.setStrCallEndDate(request.getParameter("prmCallEndDate"));
				}
				
				if(viewCDRRecordsForm.getAction() != null){
				    HashMap hashMap = new HashMap();
				    hashMap.put("SESSION_ID",viewCDRRecordsForm.getSessionId());
				    hashMap.put("CALL_START_DATE",viewCDRRecordsForm.getCallStartDate());
				    hashMap.put("CALL_END_DATE",viewCDRRecordsForm.getCallEndDate());

				    host = netServerInstanceData.getAdminHost();
					port = netServerInstanceData.getAdminPort();
			   /*Comment By Kaushik - Old JMX Code Replaced By new
			    JMXServiceURL url = null;
			    JMXConnector jmxConnector = null;
			    MBeanServerConnection mbeanServerConnection = null;
			    ObjectName objName = null;
			    url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+host+":"+port+"/jmxrmi"); 
		            jmxConnector = JMXConnectorFactory.connect(url, null); 
		            mbeanServerConnection = jmxConnector.getMBeanServerConnection();
		            objName =  new ObjectName ("Elitecore:type=EliteAdmin");
		            String strVersion = (String)mbeanServerConnection.getAttribute(objName, "VersionInformation");*/
					String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);                                        
					remoteConnectionManager = RemoteCommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
					remoteConnectionManager.init(host,port,netServerCode,true);
					String strVersion = (String) remoteConnectionManager.getAttribute("Elitecore:type=EliteAdmin","VersionInformation");
                                                
				
		            if(strVersion.equalsIgnoreCase(netServerInstanceData.getVersion())){
		            	Object[] objArgValues = {hashMap};
		            	String[] strArgTypes = {"java.util.Map"};
		            	/*objName =  new ObjectName ("Elitecore:type=EliteAdmin");
		            	List lstCDRRecordsList = (List)mbeanServerConnection.invoke(objName,"readEntities",objArgValues,strArgTypes);*/
                                List lstCDRRecordsList = (List) remoteConnectionManager.execute("Elitecore:type=EliteAdmin","readEntities",objArgValues,strArgTypes);
                            	List lstCDRColumns = (List)lstCDRRecordsList.get(0);
		            	viewCDRRecordsForm.setCDRRecordsList(lstCDRRecordsList);
		  		viewCDRRecordsForm.setErrorCode("0");
		  		request.setAttribute("lstCDRRecordsList",lstCDRRecordsList);
		  				
		            	if(viewCDRRecordsForm.getAction().equalsIgnoreCase("View Details")){
		            		request.setAttribute("lstCDRColumns",lstCDRColumns);
		            		return mapping.findForward(EDIT_CDRRECORDS);
		            	}else{
		            		return mapping.findForward(VIEW_FORWARD);
		            	}
		            }else{
		            	                Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
						ActionMessage message = new ActionMessage("servermgr.server.livedetails");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveErrors(request,messages);
						return mapping.findForward(FAILURE_FORWARD);
		            }
	            
				}else{
					viewCDRRecordsForm.setErrorCode("0");
					return mapping.findForward(VIEW_FORWARD);
				}
			}else{
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				ActionMessage message = new ActionMessage("servermgr.service.synchronize.configuration.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("fatal",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}
		} catch (UnidentifiedServerInstanceException commExp) {
			commExp.printStackTrace();
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
			ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
			ActionMessage messageReason = new ActionMessage("servermgr.server.invalididentifier");			
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			messages.add("information",messageReason);
			saveErrors(request,messages);
            return mapping.findForward(FAILURE_FORWARD);			
		}
                catch(CommunicationException e)
                {
                    viewCDRRecordsForm.setErrorCode("-1");
                    Logger.logError(MODULE,"Error during data Manager operation,reason : "+e.getMessage());
                    Logger.logTrace(MODULE,e);
                    return mapping.findForward(VIEW_FORWARD);
                 }
               catch (Exception exp) {
                        viewCDRRecordsForm.setErrorCode("-1");
			Logger.logError(MODULE,"Error during data Manager operation,reason : "+exp.getMessage());
		}
                 finally{
                            try{
                                if(remoteConnectionManager != null)
                                    remoteConnectionManager.close();  
                                }
                                catch (Throwable e) {
                                    remoteConnectionManager = null;
                              }
                 }
		 Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
		 ActionMessage message = new ActionMessage("servermgr.server.livedetails");
		 ActionMessages messages = new ActionMessages();
		 messages.add("information",message);
		 saveErrors(request,messages);
		 return mapping.findForward(FAILURE_FORWARD);
	}
}
