package com.elitecore.elitesm.web.servermgr.server;

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
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerStaffRelDetailData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.CreateServerForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class CreateServerAction extends BaseDictionaryAction{
    private static final String SUCCESS_FORWARD = "netServerConformation";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE = "CREATE SERVER ";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.CREATE_SERVER_INSTANCE_ACTION;
    private static final String NEXT = "createServerNext";
    

    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
        Logger.logTrace(MODULE,"Entered execute method of "+getClass().getName());
        IRemoteCommunicationManager remoteCommunicationManager = null;

        try {
            checkAccess(request,SUB_MODULE_ACTIONALIAS);
        	IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
    		
            CreateServerForm createServerForm = (CreateServerForm)form;
            String action = createServerForm.getAction();
            String selectedServices[] = createServerForm.getSelectedServices();
          
            NetServerBLManager blManager = new NetServerBLManager();
            NetServiceBLManager serviceBLManager = new NetServiceBLManager();
            
            if(action.equalsIgnoreCase("next")){
            	String netServerTypeId = createServerForm.getNetServerType();
            	
            	List lstServiceType = serviceBLManager.getNetServiceTypeList(netServerTypeId);
            	createServerForm.setLstServiceType(lstServiceType);
            	request.setAttribute("createServerForm",createServerForm);
           
            	return mapping.findForward(NEXT);
            }else if(action.equalsIgnoreCase("create")){
	            remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);   
	            NetServerStaffRelDetailData data = new NetServerStaffRelDetailData();
	         
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
 	            	iNetServerInstanceData = blManager.createServerInstance(iNetServerInstanceData,serviceInstaceDataList, staffData);
	            }else{
	            	iNetServerInstanceData = blManager.createServerInstance(iNetServerInstanceData, staffData);
	            }
	            // get records from staff relation basis on instance name and get the staff user and send below on 146 line
	            
	            data.setName(createServerForm.getName());
                data.setStaffUser(createServerForm.getStaff());
                
                blManager.createInstanceStaffRelationData(data);
	
	            String netServerCode = PasswordEncryption.getInstance().crypt(iNetServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);			
	            String netServerName = PasswordEncryption.getInstance().crypt(iNetServerInstanceData.getName(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);

	            Object[] argValues = {netServerCode,netServerName};
	            String[] argTypes = {"java.lang.String","java.lang.String"};
	           remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"writeServerInstanceDetails",argValues,argTypes);			
	           	StaffBLManager staffBLManager = new StaffBLManager();
	           	String username = data.getStaffUser();
				StaffData staffDetailData = (StaffData) staffBLManager.getStaffDataByUserName(username); 
	            String password = PasswordEncryption.getInstance().decrypt(staffDetailData.getPassword(), PasswordEncryption.ELITE_PASSWORD_CRYPT);
	            
	            Object[] wsArgValues = {username, password, request.getLocalAddr(), request.getLocalPort(),request.getServletContext().getContextPath()};
	            String[] wsArgTypes = {"java.lang.String","java.lang.String","java.lang.String","java.lang.Integer","java.lang.String"};
	            remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"writeWebServiceDetail",wsArgValues,wsArgTypes);
	            remoteCommunicationManager.close();

	            List netServerTypeList = blManager.getNetServerTypeList();
	            request.setAttribute("netServerTypeList",netServerTypeList);
	            request.setAttribute("iNetServerInstanceData",iNetServerInstanceData);
	            return mapping.findForward(SUCCESS_FORWARD);
            }

        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
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
        return mapping.findForward(FAILURE_FORWARD);
    }

    private INetServiceInstanceData getServiceInstanceData(
		HttpServletRequest request, INetServiceTypeData serviceTypeData) {
    	INetServiceInstanceData netServiceInstanceData = new NetServiceInstanceData();
    	netServiceInstanceData.setCreatedByStaffId(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId());
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
        iNetServerInstanceData.setCreatedByStaffId(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId());
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
        iNetServerInstanceData.setStaff(createServerForm.getStaff());
        
        if(createServerForm.getStatus()!=null && createServerForm.getStatus().equalsIgnoreCase("1")){
            iNetServerInstanceData.setCommonStatusId(BaseConstant.SHOW_STATUS_ID);
        }else{
            iNetServerInstanceData.setCommonStatusId(BaseConstant.HIDE_STATUS_ID);
        }

        return iNetServerInstanceData;
    }
}
