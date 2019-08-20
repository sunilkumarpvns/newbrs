package com.elitecore.elitesm.web.servermgr.server;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;
import com.elitecore.core.util.mbean.data.config.EliteNetPluginData;
import com.elitecore.core.util.mbean.data.config.EliteNetServerData;
import com.elitecore.core.util.mbean.data.config.EliteNetServiceData;
import com.elitecore.core.util.mbean.data.dictionary.EliteDictionaryData;
import com.elitecore.elitesm.blmanager.diameter.dictionary.DiameterDictionaryBLManager;
import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.BaseConfigurationAction;
import com.elitecore.passwordutil.PasswordEncryption;

public class SynchronizeNetDictionaryAction extends BaseConfigurationAction {
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE = "DICTIONARY SYNCHRONIZE";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.SYNCHRONIZE_NET_DICTIONARY_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
        MessageResources messageResources = getResources(request,"resultMessageResources");
        IRemoteCommunicationManager remoteCommunicationManager = null;        
        try{
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            //NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
            IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
            
            
            /*
             * get Radius and Diameter Dictionary List
             */
            
            
            //List<DictionaryData> radiusDictionaryList=(List<DictionaryData>)request.getSession().getAttribute("radiusDictionaryList");
            List<DiameterdicData> diameterDictionaryList=(List<DiameterdicData>)request.getSession().getAttribute("diameterDictionaryList");
            RadiusDictionaryBLManager radiusDictionaryBLManager  = new RadiusDictionaryBLManager();
        	List<DictionaryData> radiusDictionaryList=radiusDictionaryBLManager.getDictionaryDataList();
            String strNetServerId = request.getParameter("netserverid");
            String netServerId=null;
			if(strNetServerId != null){
				netServerId = strNetServerId;
			}
            if(netServerId != null ){
                INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);
                List<EliteDictionaryData> eliteDictionaryList=getEliteDictionaryList(radiusDictionaryList,diameterDictionaryList,netServerInstanceData.getVersion());
                
                //EliteNetServerData eliteNetServerData = getServerLevelConfiguration(netServerId);

                String ipAddress = netServerInstanceData.getAdminHost();
                int port = netServerInstanceData.getAdminPort();	        


                String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
                Logger.logDebug(MODULE, "ENCRYPTED SERVER CODE:"+netServerCode);
                
                remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
                remoteCommunicationManager.init(ipAddress,port,netServerCode,true);
                String strVersion = (String) remoteCommunicationManager.getAttribute(MBeanConstants.CONFIGURATION,"VersionInformation");
                
                
                if(strVersion.equalsIgnoreCase(netServerInstanceData.getVersion())){
                	
                	Object[] objArgValues = {eliteDictionaryList};
                	String[] strArgTypes = {"java.util.List"};
                    List<String> list=(List<String>) remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"updateDictionary",objArgValues,strArgTypes);
                    
                    request.setAttribute("responseUrl","/updateNetDictionarySynchronize.do?netServerId="+netServerId); 
        			ActionMessage message = new ActionMessage("servermgr.update.dictionary.success");        			
        			
        			ActionMessages messages = new ActionMessages();
        			messages.add("information",message);
        			if(list != null && !list.isEmpty()){
        				ActionMessage message1 = new ActionMessage("servermgr.update.dictionary.listmessageheader");
        				messages.add("information",message1);
        				for(int i=0;i<list.size();i++){
        					int index=i+1;
        					String strMsg=""+index+ ".  " +list.get(i);
        					ActionMessage msg = new ActionMessage("servermgr.update.dictionary.listmessage",strMsg);
        					messages.add("information",msg);	
        				}
        			}
        			
        			saveMessages(request,messages);
        			doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
        			return mapping.findForward(SUCCESS_FORWARD);
                    
                    
                }else{
                    Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
                    ActionMessage message = new ActionMessage("error.two.failed",messageResources.getMessage("error.label.server"),messageResources.getMessage("error.label.synchronization"));					
                    ActionMessage message1 = new ActionMessage("servermgr.server.version.mismatch");
                    ActionMessages messages = new ActionMessages();
                    messages.add("information",message);
                    messages.add("information",message1);
                    saveErrors(request,messages);
                    return mapping.findForward(FAILURE_FORWARD);
                }
            }else{
                Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
                ActionMessage message = new ActionMessage("error.two.failed",messageResources.getMessage("error.label.server"),messageResources.getMessage("error.label.synchronization"));				
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
        }catch(CommunicationException c){
            Logger.logError(MODULE,"Error during data Manager operation,reason :"+c.getMessage());
            Logger.logTrace(MODULE,c);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(c);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.update.dictionary.failed");
            ActionMessage messageReason = new ActionMessage("servermgr.update.dictionary.communication.failed.reason");	
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);		
            messages.add("information",messageReason);
            saveErrors(request,messages);		

        }catch(Exception managerExp){
            Logger.logError(MODULE,"Error during data Manager operation,reason :"+managerExp.getMessage());
            Logger.logTrace(MODULE,managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.operation.failure");			
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);		
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
        return mapping.findForward(FAILURE_FORWARD);
    }
    
  
    private List<EliteDictionaryData> getEliteDictionaryList(List<DictionaryData> radiusDictionaryList,List<DiameterdicData> diameterDictionaryList,String version) throws DataManagerException {

    	List<EliteDictionaryData> eliteDictionaryList = new ArrayList<EliteDictionaryData>();
    	RadiusDictionaryBLManager radiusDictionaryBLManager = new RadiusDictionaryBLManager();
    	DiameterDictionaryBLManager diameterDictionaryBLManager = new DiameterDictionaryBLManager();

    	/*
    	 * Radius Dictionary
    	 */

    	for (Iterator iterator = radiusDictionaryList.iterator(); iterator.hasNext();) {

    		EliteDictionaryData eliteDictionaryData=new EliteDictionaryData();
    		DictionaryData dictionaryData = (DictionaryData) iterator.next();
    		eliteDictionaryData.setType("Radius");
    		eliteDictionaryData.setVendorId(dictionaryData.getVendorId());
    		eliteDictionaryData.setVendorName(dictionaryData.getName());
    		eliteDictionaryData.setVersion(version);

    		String xmlString=radiusDictionaryBLManager.convertAsXml(dictionaryData);
    		byte[] bDictionaryData=xmlString.getBytes();
    		eliteDictionaryData.setDictionaryData(bDictionaryData);
    		eliteDictionaryList.add(eliteDictionaryData);

    	}

    	/*
    	 * Diameter Dictionary
    	 */


    	for (Iterator iterator = diameterDictionaryList.iterator(); iterator.hasNext();) {

    		EliteDictionaryData eliteDictionaryData=new EliteDictionaryData();
    		DiameterdicData diameterdicData = (DiameterdicData) iterator.next();
    		eliteDictionaryData.setType("Diameter");
    		eliteDictionaryData.setApplicationId(diameterdicData.getApplicationId());
    		eliteDictionaryData.setVendorId(diameterdicData.getVendorId());
    		eliteDictionaryData.setVendorName(diameterdicData.getVendorName());
    		eliteDictionaryData.setVersion(version);

    		String xmlString=diameterDictionaryBLManager.convertAsXml(diameterdicData);
    		byte[] bDictionaryData=xmlString.getBytes();
    		eliteDictionaryData.setDictionaryData(bDictionaryData);
    		eliteDictionaryList.add(eliteDictionaryData);

    	}





    	return eliteDictionaryList;
    }


	private void printServices(EliteNetServerData serverData)
    {
    	Logger.logInfo(MODULE," Sending Server Object... ");
    	Logger.logInfo(MODULE," Server Name :  "+ serverData.getNetServerName());
    	Logger.logInfo(MODULE," Version Name :  "+ serverData.getVersion());

    	List serviceList = serverData.getNetServiceList();
    	printConfiguration(serverData.getNetConfigurationList());
    	List pluginList = serverData.getPluginList();

    	if(pluginList!=null){
    		for (Iterator iterator = pluginList.iterator(); iterator.hasNext();) {
    			EliteNetPluginData eliteNetPluginData = (EliteNetPluginData) iterator.next();
    			Logger.logInfo(MODULE," Plugin Name :  "+eliteNetPluginData.getPluginName());
    			printConfiguration(eliteNetPluginData.getNetConfigurationDataList());
    		}
    	}
    	if(serviceList!=null){
    		for (Iterator iterator = serviceList.iterator(); iterator.hasNext();) {
    			EliteNetServiceData eliteNetServiceData = (EliteNetServiceData) iterator.next();
    			Logger.logInfo(MODULE," Service Name :  "+ eliteNetServiceData.getNetServiceName());
    			printConfiguration(eliteNetServiceData.getNetConfigurationList());

    		}
    	}
    }
    private void printConfiguration(List configurationList){
    	if(configurationList!=null){
    		for (Iterator iterator = configurationList.iterator(); iterator.hasNext();) {
    			EliteNetConfigurationData configData = (EliteNetConfigurationData) iterator.next();
    			Logger.logInfo(MODULE, "\t Configuration Key --> " + configData.getNetConfigurationKey());
    		}
    	}
    }
}

