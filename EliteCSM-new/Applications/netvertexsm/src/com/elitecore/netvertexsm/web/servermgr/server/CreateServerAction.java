package com.elitecore.netvertexsm.web.servermgr.server;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.server.form.CreateServerForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class CreateServerAction extends BaseWebAction{
    private static final String SUCCESS_FORWARD = "netServerConformation";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE = "CREATE SERVER ";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.CREATE_SERVER_INSTANCE_ACTION;
    private static final String NEXT = "createServerNext";
    

    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
        Logger.logTrace(MODULE,"Entered execute method of "+getClass().getName());
        String serverGroupId;
        IRemoteCommunicationManager remoteCommunicationManager = null;
        try {

            checkAccess(request,SUB_MODULE_ACTIONALIAS);
            serverGroupId = request.getParameter("groupId");
            CreateServerForm createServerForm = (CreateServerForm)form;
            String action = createServerForm.getAction();
            String selectedServices[] = createServerForm.getSelectedServices();
            //TODO code provided for default selection of PCRF service
            if(selectedServices == null){
            	selectedServices = new String[1];
            	selectedServices[0] =  "SCR0001";
            }else{
            	String[] tempService = new String[selectedServices.length+1];
            	tempService[0] =  "SCR0001";
            	System.arraycopy(selectedServices, 0, tempService, 1, selectedServices.length );
                selectedServices = tempService;
            }
      
            NetServerBLManager blManager = new NetServerBLManager();
            NetServiceBLManager serviceBLManager = new NetServiceBLManager();
            if(action.equalsIgnoreCase("next")){
            	String netServerTypeId = createServerForm.getNetServerType();
            	List lstServiceType = serviceBLManager.getNetServiceTypeList(netServerTypeId);
            	createServerForm.setLstServiceType(lstServiceType);
            	request.setAttribute("createServerForm",createServerForm);
            	request.setAttribute("groupId", serverGroupId);
            	return mapping.findForward(NEXT);
            }else if(action.equalsIgnoreCase("create")){
            	
	            remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);   
	         
	            try{
	                Object[] objArgValues = {};
	                String[] strArgTypes = {};
	                remoteCommunicationManager.init(createServerForm.getAdminInterfaceIP(),createServerForm.getAdminInterfacePort(), null, false);		        
	                String serverHome = (String) remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"serverHome",objArgValues,strArgTypes);
	                String javaHome = (String) remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"javaHome",objArgValues,strArgTypes);
	                createServerForm.setServerHome(serverHome);
	                createServerForm.setJavaHome(javaHome);
	                
	            }catch(CommunicationException ce){
	                Logger.logTrace(MODULE,"Returning error forward from "+getClass().getName());
	    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(ce);
	    			request.setAttribute("errorDetails", errorElements);
	                ActionMessage message = new ActionMessage("servermgr.server.communication.failure");
	                ActionMessage messageReason = new ActionMessage("servermgr.server.create.liveserver.failure.reason");
	                ActionMessages messages = new ActionMessages();
	                messages.add("information",message);
	                messages.add("information",messageReason);
	                saveErrors(request,messages);
	                
		            ActionMessages errorHeadingMessage = new ActionMessages();
		            message = new ActionMessage("error.heading.create.serverinstance.failure");
		            errorHeadingMessage.add("errorHeading",message);
		            saveMessages(request,errorHeadingMessage);      
		            
	                return mapping.findForward(FAILURE_FORWARD);
	            }catch(Exception e){
	                Logger.logTrace(MODULE,"Returning error forward from "+getClass().getName());
	    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
	    			request.setAttribute("errorDetails", errorElements);
	                ActionMessage message = new ActionMessage("servermgr.create.failure");
	                ActionMessage messageReason = new ActionMessage("servermgr.server.create.liveserver.failure.reason");
	                ActionMessages messages = new ActionMessages();
	                messages.add("information",message);
	                messages.add("information",messageReason);
	                saveErrors(request,messages);
	                
		            ActionMessages errorHeadingMessage = new ActionMessages();
		            message = new ActionMessage("error.heading.create.serverinstance.failure");
		            errorHeadingMessage.add("errorHeading",message);
		            saveMessages(request,errorHeadingMessage);
		            
	                return mapping.findForward(FAILURE_FORWARD);
	            }
	            //List<INetServiceInstanceData> lstServices = new ArrayList<INetServiceInstanceData>();
	         
	            
	            INetServerInstanceData iNetServerInstanceData = formToBean(request, createServerForm);
	            Logger.logInfo(MODULE,"Create Server Form "+createServerForm);
	            if(selectedServices!=null){
	            	List<INetServiceInstanceData> serviceInstaceDataList = new ArrayList<INetServiceInstanceData>();
 	            	for (int i = 0; i < selectedServices.length; i++) {
	            		INetServiceTypeData serviceTypeData = serviceBLManager.getNetServiceType(selectedServices[i]);
	            		INetServiceInstanceData instanceData = getServiceInstanceData(request,serviceTypeData);
	            		serviceInstaceDataList.add(instanceData);
	            	}
 	            	iNetServerInstanceData = blManager.createServerInstance(iNetServerInstanceData,serviceInstaceDataList, serverGroupId);
	            }else{
	            	iNetServerInstanceData = blManager.createServerInstance(iNetServerInstanceData,serverGroupId);
	            }
	            
	            
	
	            String netServerCode = PasswordEncryption.getInstance().crypt(iNetServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);			
	            String netServerName = PasswordEncryption.getInstance().crypt(iNetServerInstanceData.getName(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
	            
	            Object[] argValues = {netServerCode,netServerName};
	            String[] argTypes = {"java.lang.String","java.lang.String"};
	            remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"writeServerInstanceDetails",argValues,argTypes);			
	            remoteCommunicationManager.close();
	
	            List netServerTypeList = blManager.getNetServerTypeList();
	            request.setAttribute("netServerTypeList",netServerTypeList);
	            request.setAttribute("selectedServices",selectedServices);	            
	            request.setAttribute("iNetServerInstanceData",iNetServerInstanceData);
	            return mapping.findForward(SUCCESS_FORWARD);
            }

        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
                        
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("servermgr.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);             
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch (UnidentifiedServerInstanceException commExp) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
            Logger.logTrace(MODULE,commExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
            ActionMessage messageReason = new ActionMessage("servermgr.server.invalididentifier");			
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            messages.add("information",messageReason);
            saveErrors(request,messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("error.heading.create.serverinstance.failure");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
            
        } catch (CommunicationException commExp) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
            Logger.logTrace(MODULE,commExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.create.liveserver.failure");
            ActionMessage messageReason = new ActionMessage("servermgr.server.create.liveserver.failure.reason");			
            ActionMessage messageSolution = new ActionMessage("servermgr.server.create.liveserver.failure.solution");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            messages.add("information",messageReason);
            messages.add("information",messageSolution);			
            saveErrors(request,messages);
            
        } catch (Exception managerExp) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
            Logger.logTrace(MODULE,managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.create.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
	
        }
        ActionMessages errorHeadingMessage = new ActionMessages();
        ActionMessage message = new ActionMessage("servermgr.error.heading","creating");
        errorHeadingMessage.add("errorHeading",message);
        saveMessages(request,errorHeadingMessage); 
        return mapping.findForward(FAILURE_FORWARD);
    }

    private INetServiceInstanceData getServiceInstanceData(
		HttpServletRequest request, INetServiceTypeData serviceTypeData) {
    	INetServiceInstanceData netServiceInstanceData = new NetServiceInstanceData();
    	netServiceInstanceData.setCreatedByStaffId(Long.parseLong(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId()));
    	netServiceInstanceData.setCreateDate(getCurrentTimeStemp());
        netServiceInstanceData.setNetServiceTypeId(serviceTypeData.getNetServiceTypeId());
        netServiceInstanceData.setName(serviceTypeData.getName());
        netServiceInstanceData.setDisplayName(serviceTypeData.getName());
        netServiceInstanceData.setDescription(serviceTypeData.getDescription());
        netServiceInstanceData.setCommonStatusId(BaseConstant.SHOW_STATUS_ID);
        netServiceInstanceData.setInstanceId("000");
        netServiceInstanceData.setIsInSync(BaseConstant.HIDE_STATUS_ID);
        netServiceInstanceData.setSystemGenerated(BaseConstant.NOT_A_SYSTEMRECORD);
        netServiceInstanceData.setLastModifiedDate(null);
        netServiceInstanceData.setLastModifiedByStaffId(null);
        netServiceInstanceData.setStatusChangeDate(null);
        netServiceInstanceData.setLastSyncDate(null);
        netServiceInstanceData.setLastSuccessSynDate(null);
        netServiceInstanceData.setLastSyncStatus(null);

    	return netServiceInstanceData;
	}

	private INetServerInstanceData formToBean(HttpServletRequest request, CreateServerForm createServerForm) throws DataManagerException {


        NetServerBLManager blManager = new NetServerBLManager();
        INetServerTypeData serverTypeData = blManager.getNetServerType(createServerForm.getNetServerType());

        INetServerInstanceData  iNetServerInstanceData = new NetServerInstanceData();
        iNetServerInstanceData.setNetServerCode("SVI0000");
        iNetServerInstanceData.setName(createServerForm.getName());
        iNetServerInstanceData.setDescription(createServerForm.getDescription());
        iNetServerInstanceData.setNetServerTypeId(createServerForm.getNetServerType());
        iNetServerInstanceData.setVersion(serverTypeData.getVersion());
        iNetServerInstanceData.setAdminHost(createServerForm.getAdminInterfaceIP());
        iNetServerInstanceData.setAdminPort(createServerForm.getAdminInterfacePort());
        iNetServerInstanceData.setCreateDate(new Timestamp(new Date().getTime()));
        iNetServerInstanceData.setCreatedByStaffId(Long.parseLong(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId()));
        iNetServerInstanceData.setLastModifiedDate(null);
        iNetServerInstanceData.setLastModifiedByStaffId(null);
        iNetServerInstanceData.setJavaHome(createServerForm.getJavaHome());
        iNetServerInstanceData.setServerHome(createServerForm.getServerHome());

        iNetServerInstanceData.setIsInSync(createServerForm.getIsInSync());
        iNetServerInstanceData.setSystemGenerated(BaseConstant.NOT_A_SYSTEMRECORD);

        iNetServerInstanceData.setStatusChangeDate(null);
        iNetServerInstanceData.setLastSyncDate(null);
        iNetServerInstanceData.setLastSuccessSynDate(null);
        iNetServerInstanceData.setLastSyncStatus(null);

        if(createServerForm.getStatus()!=null && createServerForm.getStatus().equalsIgnoreCase("1")){
            iNetServerInstanceData.setCommonStatusId(BaseConstant.SHOW_STATUS_ID);
        }else{
            iNetServerInstanceData.setCommonStatusId(BaseConstant.HIDE_STATUS_ID);
        }

        return iNetServerInstanceData;
    }
}
