/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DownloadLicensePublickeyAction.java                             
 * ModualName                                     
 * Created on Oct 8, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.web.servermgr.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.passwordutil.PasswordEncryption;


public class DownloadLicenseAction extends BaseWebAction {

    private static final String MODULE          = "DOWNLOAD LICENSE";
    private static final String FAILURE_FORWARD = "failure";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.DOWNLOAD_LICENSE_ACTION;


    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger.logInfo(MODULE, "Enter the execute method of :" + getClass().getName());
        IRemoteCommunicationManager remoteCommunicationManager = null;
        ServletOutputStream out = null;
        PrintWriter writer = null;
        ActionErrors errors = new ActionErrors();
        try {
            //checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            
            String strNetServerId = request.getParameter("netserverid");
            String netServerId= null;
            if(strNetServerId!=null){
            	netServerId = strNetServerId.trim();
            }
            if ( Strings.isNullOrBlank(netServerId) == false ) {

                NetServerBLManager netServerBLManager = new NetServerBLManager();
                INetServerInstanceData serverInstanceData = netServerBLManager.getNetServerInstance(netServerId);

                String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
                remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
                remoteCommunicationManager.init(serverInstanceData.getAdminHost(),serverInstanceData.getAdminPort(),netServerCode,true );
                String license = (String)  remoteCommunicationManager.execute(MBeanConstants.LICENSE,"readLicense",null,null);
            	IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
    			
                if(license!=null){
                	response.setHeader("Content-Disposition", "attachment;filename=\"local_node.lic");
                	response.setContentType("application/octet-stream");
                	out = response.getOutputStream();
                	writer = new PrintWriter(out);
                	writer.print(license);
                	/* Closeing all the streams */
                	writer.close();
                	out.close();
                	doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
                }else{
        	        Logger.logError(MODULE, "Error during getting license string : "+license);
        	        ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
                    ActionMessage messageReason = new ActionMessage("servermgr.license.nolicense");			
                    ActionMessages messages = new ActionMessages();
                    messages.add("information", message);
                    messages.add("information", messageReason);
        	        saveErrors(request, messages);
                	request.setAttribute("responseUrl", request.getContextPath()+"/listNetServerInstance.do");
                    return mapping.findForward(FAILURE_FORWARD);
                }
            }
            return null;
        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch (UnidentifiedServerInstanceException commExp) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
			request.setAttribute("responseUrl", request.getContextPath()+"/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
            ActionMessage messageReason = new ActionMessage("servermgr.server.invalididentifier");			
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            messages.add("information",messageReason);
            saveErrors(request,messages);
            return mapping.findForward(FAILURE_FORWARD);
        }catch (CommunicationException e) {
            Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            request.setAttribute("responseUrl", request.getContextPath()+"/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.downlodpublickey.failure");
            ActionMessage message1 = new ActionMessage("servermgr.server.connection.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            messages.add("information1", message1);
            saveErrors(request, messages);
            Logger.logInfo(MODULE, "Forwarding to :" + mapping.findForward(FAILURE_FORWARD).getPath());
            return mapping.findForward(FAILURE_FORWARD);
        }catch (IOException jmExp) {
            Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE, jmExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(jmExp);
			request.setAttribute("errorDetails", errorElements);
            request.setAttribute("responseUrl", request.getContextPath()+"/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.downlodpublickey.failure");
            ActionMessage message1 = new ActionMessage("servermgr.server.connection.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            messages.add("information1", message1);
            saveErrors(request, messages);
            Logger.logInfo(MODULE, "Forwarding to :" + mapping.findForward(FAILURE_FORWARD).getPath());
            return mapping.findForward(FAILURE_FORWARD);
        }catch (Exception managerExp) {
            Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE, "Returning error forward from " + managerExp);
            request.setAttribute("responseUrl", request.getContextPath()+"/listNetServerInstance");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.downlodpublickey.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            Logger.logInfo(MODULE, "Forwarding to :" + mapping.findForward(FAILURE_FORWARD).getPath());
            return mapping.findForward(FAILURE_FORWARD);
        }
        finally {            
            try{
                if (writer != null)
                    writer.close();

                if (out != null)
                    out.close();

                if(remoteCommunicationManager != null)
                    remoteCommunicationManager.close();  

            }
            catch (Throwable e) {
                remoteCommunicationManager = null;
                writer=null;
                out=null;
            }


        }
    }

}
