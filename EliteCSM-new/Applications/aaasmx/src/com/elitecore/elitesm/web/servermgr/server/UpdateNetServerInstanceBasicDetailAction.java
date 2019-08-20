package com.elitecore.elitesm.web.servermgr.server;

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
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerStaffRelDetailData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerTypeData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.UpdateNetServerInstanceBasicDetailForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class UpdateNetServerInstanceBasicDetailAction extends BaseDictionaryAction {
    private static final String FAILURE_FORWARD = "failure";
    private static final String SUCCESS_FORWARD = "success";
    private static final String UPDATE_FORWARD  = "updateNetSeverInstanceBasicDetail";
    private static final String MODULE = "UPDATE BASIC DETAIL ACTION";
    private static final String SUBMODULEACTIONALIAS=ConfigConstant.UPDATE_NET_SERVER_INSTANCE_BASIC_DETAIL_ACTION;
	private IRemoteCommunicationManager remoteCommunicationManager;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());

        NetServerBLManager netServerBLManager = new NetServerBLManager();
        String strNetServerId = request.getParameter("netserverid");
        IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		
        try {
            checkActionPermission(request, SUBMODULEACTIONALIAS);
    		String netServerId=null;
			if (strNetServerId != null) {
				netServerId = strNetServerId;
			}
            UpdateNetServerInstanceBasicDetailForm updateNetServerInstanceBasicDetailForm = (UpdateNetServerInstanceBasicDetailForm)form;
            List lstNetServerType = netServerBLManager.getNetServerTypeList();
           
            if(updateNetServerInstanceBasicDetailForm.getAction() == null){
                if(netServerId != null){
                    INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
                    netServerInstanceData.setNetServerId(netServerId);
                    netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
                    
                    boolean setAlive = true;
                    try {
                    	remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
                    	remoteCommunicationManager.init(netServerInstanceData.getAdminHost(),netServerInstanceData.getAdminPort(), null, false);
                    	
                    } catch (Exception e) {
                    	setAlive = false;
                    	Logger.logInfo(MODULE, "Server Communication failed. Connection can not established.");
                    }
                    
                    List<NetServerTypeData> netServerTypeList = netServerBLManager.getNetServerTypeList();
                    updateNetServerInstanceBasicDetailForm = convertBeanToForm(netServerInstanceData);
                    if(setAlive) {
                    	updateNetServerInstanceBasicDetailForm.setAlive(true);
                    }else {
                    	updateNetServerInstanceBasicDetailForm.setAlive(false);
                    }
                    
                    NetServerStaffRelDetailData data = netServerBLManager.getInstanceStaffRelationDataByName(netServerInstanceData.getName());
                    if(data != null) {
                    	netServerInstanceData.setStaff(data.getStaffUser());
                    	updateNetServerInstanceBasicDetailForm.setInstanceStaffRelId(data.getId());
                    	updateNetServerInstanceBasicDetailForm.setStaff(data.getStaffUser());
                    } 
	                
	                StaffBLManager staffBlmanger = new StaffBLManager();
	                List<StaffData> staffList = staffBlmanger.getList();
	                updateNetServerInstanceBasicDetailForm.setStaffDataList(staffList);
	                
                    request.setAttribute("netServerInstanceData",netServerInstanceData);
                    request.setAttribute("lstNetServerType",lstNetServerType);
                    request.setAttribute("updateNetServerInstanceBasicDetailForm",updateNetServerInstanceBasicDetailForm);
                    request.setAttribute("netServerTypeList",netServerTypeList);
                }
                return mapping.findForward(UPDATE_FORWARD);
            }else if(updateNetServerInstanceBasicDetailForm.getAction().equalsIgnoreCase("update")){
            	
            	INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(updateNetServerInstanceBasicDetailForm.getNetServerId());
            	//it will fetch the existed staff relation data on the existed name of instance.
            	NetServerStaffRelDetailData data = netServerBLManager.getInstanceStaffRelationDataByName(netServerInstanceData.getName());
                
            	convertFormToBean(updateNetServerInstanceBasicDetailForm,netServerInstanceData);
                
            	// set the existed staff data.
                netServerInstanceData.setStaff(data.getStaffUser());
                netServerBLManager.updateBasicDetail(netServerInstanceData);
                
                NetServerStaffRelDetailData netServerStaffReldata = new NetServerStaffRelDetailData();
                netServerStaffReldata.setId(updateNetServerInstanceBasicDetailForm.getInstanceStaffRelId());
                netServerStaffReldata.setName(updateNetServerInstanceBasicDetailForm.getName());
                netServerStaffReldata.setStaffUser(updateNetServerInstanceBasicDetailForm.getStaff());
                
                if(updateNetServerInstanceBasicDetailForm.getStaff() == null) {
                	netServerStaffReldata.setStaffUser(data.getStaffUser());
                }else {
                	StaffBLManager staffBLManager = new StaffBLManager();
                	String username = netServerStaffReldata.getStaffUser();
                	StaffData staffDetailData = (StaffData) staffBLManager.getStaffDataByUserName(username); 
                	String password = PasswordEncryption.getInstance().decrypt(staffDetailData.getPassword(), PasswordEncryption.ELITE_PASSWORD_CRYPT);
                	
                	Object[] wsArgValues = {username, password, request.getLocalAddr(), request.getLocalPort(),request.getServletContext().getContextPath()};
                	String[] wsArgTypes = {"java.lang.String","java.lang.String","java.lang.String","java.lang.Integer","java.lang.String"};
                	remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
                	NetServerInstanceData netServerInstance = netServerBLManager.getNetServerInstanceByName(netServerStaffReldata.getName());
                	remoteCommunicationManager.init(netServerInstance.getAdminHost(),netServerInstance.getAdminPort(), null, false);
                	remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"writeWebServiceDetail",wsArgValues,wsArgTypes);
                	remoteCommunicationManager.close();
                	
                }	
                netServerBLManager.updateInstanceStaffRelationData(netServerStaffReldata);
           
                doAuditing(staffData, SUBMODULEACTIONALIAS);
                request.setAttribute("lstNetServerType",lstNetServerType);

                request.setAttribute("responseUrl","/updateNetServerInstanceBasicDetail.do?netserverid="+netServerInstanceData.getNetServerId());
                ActionMessage message = new ActionMessage("servermgr.server.updatebasicdetail.success");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveMessages(request,messages);
                return mapping.findForward(SUCCESS_FORWARD);
            }
        } catch(CommunicationException ce){
            Logger.logTrace(MODULE,"Returning error forward from "+getClass().getName());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(ce);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.communication.failure");
            ActionMessage messageReason = new ActionMessage("servermgr.server.update.liveserver.failure.reason");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            messages.add("information",messageReason);
            saveErrors(request,messages);
            return mapping.findForward(FAILURE_FORWARD);
        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }
        catch (Exception exp) {
            Logger.logError(MODULE,"Returning error forward from "+ getClass().getName() + "Reason:-" +exp.getMessage());
            Logger.logTrace(MODULE,exp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
        }
        ActionMessages messages = new ActionMessages();
        messages.add("information",new ActionMessage("servermgr.update.failure"));
        saveErrors(request,messages);
        return mapping.findForward(FAILURE_FORWARD);
    }

    private UpdateNetServerInstanceBasicDetailForm convertBeanToForm(INetServerInstanceData netServerInstanceData){
        UpdateNetServerInstanceBasicDetailForm updateNetServerInstanceBasicDetailForm = null;
        if(netServerInstanceData != null){
            updateNetServerInstanceBasicDetailForm = new UpdateNetServerInstanceBasicDetailForm();
            updateNetServerInstanceBasicDetailForm.setNetServerId(netServerInstanceData.getNetServerId());
            updateNetServerInstanceBasicDetailForm.setName(netServerInstanceData.getName());
            updateNetServerInstanceBasicDetailForm.setDescription(netServerInstanceData.getDescription());
            updateNetServerInstanceBasicDetailForm.setNetServerType(netServerInstanceData.getNetServerTypeId());
            updateNetServerInstanceBasicDetailForm.setServerHome(netServerInstanceData.getServerHome());
            updateNetServerInstanceBasicDetailForm.setJavaHome(netServerInstanceData.getJavaHome());
        }
        return updateNetServerInstanceBasicDetailForm;
    }

    private void convertFormToBean(UpdateNetServerInstanceBasicDetailForm updateNetServerInstanceBasicDetailForm, INetServerInstanceData netServerInstanceData){
        if(updateNetServerInstanceBasicDetailForm != null){
            netServerInstanceData.setNetServerId(updateNetServerInstanceBasicDetailForm.getNetServerId());
            netServerInstanceData.setName(updateNetServerInstanceBasicDetailForm.getName());
            netServerInstanceData.setDescription(updateNetServerInstanceBasicDetailForm.getDescription());
            netServerInstanceData.setNetServerTypeId(updateNetServerInstanceBasicDetailForm.getNetServerType());
            netServerInstanceData.setJavaHome(updateNetServerInstanceBasicDetailForm.getJavaHome());
            String strServerHome = updateNetServerInstanceBasicDetailForm.getServerHome();
            if(updateNetServerInstanceBasicDetailForm.getStaff() != null) {
            	netServerInstanceData.setStaff(updateNetServerInstanceBasicDetailForm.getStaff());
            }
            strServerHome=strServerHome.replaceAll("!", "\\\\");
            netServerInstanceData.setServerHome(strServerHome);
        }
    }
}
