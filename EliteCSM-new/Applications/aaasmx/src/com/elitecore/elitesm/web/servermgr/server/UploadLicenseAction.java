/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   UploadLicenseAction.java                             
 * ModualName                                     
 * Created on Oct 9, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.web.servermgr.server;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.AuthorizationException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.FormatInvalidException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidFileExtensionException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.NullValueException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.ValueOutOfRangeException;
import com.elitecore.elitesm.datamanager.core.exceptions.environmentsupport.EnvironmentNotSupportedException;
import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.OperationFailedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.server.forms.UploadLicenseForm;
import com.elitecore.passwordutil.PasswordEncryption;

/**
 * @author kaushikvira
 */
public class UploadLicenseAction extends BaseWebAction {

    private static final String MODULE            = "UPLOAD LICENSE";
    private static final String FAILURE_FORWARD   = "failure";
    private static final String SUCCESS_FORWARD   = "success";
    private static final String UPLOADLICENSEFILE = "uploadLicenseFile";
    private static final String LICENSEFILEEXT    = "lic";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.UPLOAD_LICENSE_ACTION;

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Logger.logInfo(MODULE, "Enter the execute method of :" + getClass().getName());
        IRemoteCommunicationManager remoteCommunicationManager = null;
        IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
        
        try {
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            UploadLicenseForm uploadLicForm = (UploadLicenseForm) form;
            NetServerBLManager netServerBLManager = new NetServerBLManager();

            String strNetServerId = request.getParameter("netserverid");
            String netServerId=null;
			if(strNetServerId == null){
				netServerId = uploadLicForm.getNetServerId();
			}else{
				netServerId = strNetServerId;
			}
            if (uploadLicForm.getAction() == null || uploadLicForm.getAction().equalsIgnoreCase("")) {

                uploadLicForm.setNetServerId(netServerId);

                INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
                List netServerTypeList = netServerBLManager.getNetServerTypeList();
                if (netServerId != null) {
                    netServerInstanceData.setNetServerId(netServerId);
                    netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
                    request.setAttribute("netServerInstanceData", netServerInstanceData);
                    request.setAttribute("netServerTypeList", netServerTypeList);
                }
                return mapping.findForward(UPLOADLICENSEFILE);

            } else {
                Logger.logInfo(MODULE, "Upload License File called" + getClass().getName());

                String netserverId = uploadLicForm.getNetServerId();

                if (netserverId == null ) { throw new DataManagerException("netserverId is null"); }

                INetServerInstanceData serverInstanceData = netServerBLManager.getNetServerInstance(netServerId);

                FormFile file = uploadLicForm.getLicenseFile();
                String strFilename = file.getFileName();
                StringTokenizer strFileTocken = new StringTokenizer(strFilename, ".");
                String fileExt = null;
                while (strFileTocken.hasMoreTokens()) {
                    fileExt = strFileTocken.nextToken();
                }

                if (fileExt == null || !fileExt.equalsIgnoreCase(LICENSEFILEEXT)) { throw new InvalidFileExtensionException("File Ext is not match"); }

                byte[] buffer = file.getFileData();
                String fileData = new String(buffer);
                String ipAddress = serverInstanceData.getAdminHost();
                int port = serverInstanceData.getAdminPort();
                Object[] objArgValues = { fileData};
                String[] strArgTypes = { "java.lang.String"};

                remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
                String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
                remoteCommunicationManager.init(ipAddress,port,netServerCode,true);
                remoteCommunicationManager.execute(MBeanConstants.LICENSE, "saveLicense", objArgValues, strArgTypes);

                doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
                request.setAttribute("responseUrl", "/initNetServerLicense.do?netServerId="+netServerId);
                ActionMessage message = new ActionMessage("servermgr.server.uploadlicense.success");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveMessages(request, messages);
                Logger.logInfo(MODULE, "Forwarding to :" + mapping.findForward(SUCCESS_FORWARD).getPath());
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
        catch (IOException jmExp) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + jmExp.getMessage());
            Logger.logTrace(MODULE, jmExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(jmExp);
			request.setAttribute("errorDetails", errorElements);
            request.setAttribute("responseUrl", "/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.uploadlicense.failure");
            ActionMessage message1 = new ActionMessage("servermgr.server.connection.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            messages.add("information1", message1);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (AuthorizationException authExp) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());
            Logger.logTrace(MODULE, authExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);
            request.setAttribute("responseUrl", "/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.uploadlicense.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (EnvironmentNotSupportedException envException) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + envException.getMessage());
            Logger.logTrace(MODULE, envException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(envException);
			request.setAttribute("errorDetails", errorElements);
            request.setAttribute("responseUrl", "/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.uploadlicense.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (CommunicationException connException) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + connException.getMessage());
            Logger.logTrace(MODULE, connException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(connException);
			request.setAttribute("errorDetails", errorElements);
            request.setAttribute("responseUrl", "/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.uploadlicense.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (OperationFailedException opException) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + opException.getMessage());
            Logger.logTrace(MODULE, opException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(opException);
			request.setAttribute("errorDetails", errorElements);
            request.setAttribute("responseUrl", "/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.uploadlicense.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }

        catch (ConstraintViolationException conException) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + conException.getMessage());
            Logger.logTrace(MODULE, conException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(conException);
			request.setAttribute("errorDetails", errorElements);
            request.setAttribute("responseUrl", "/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.uploadlicense.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (FormatInvalidException forException) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + forException.getMessage());
            Logger.logTrace(MODULE, forException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(forException);
			request.setAttribute("errorDetails", errorElements);
            request.setAttribute("responseUrl", "/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.uploadlicense.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (NullValueException nullValueException) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason NullValueException:" + nullValueException.getMessage());
            Logger.logTrace(MODULE, nullValueException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(nullValueException);
			request.setAttribute("errorDetails", errorElements);
            request.setAttribute("responseUrl", "/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.uploadlicense.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (InvalidFileExtensionException invException) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + invException.getMessage());
            Logger.logTrace(MODULE, invException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(invException);
			request.setAttribute("errorDetails", errorElements);
            request.setAttribute("responseUrl", "/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.uploadlicense.failure");
            ActionMessage message1 = new ActionMessage("error.invalid.fileextension");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            messages.add("information1", message1);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (InvalidValueException invalidValueException) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + invalidValueException.getMessage());
            Logger.logTrace(MODULE, invalidValueException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(invalidValueException);
			request.setAttribute("errorDetails", errorElements);
            request.setAttribute("responseUrl", "/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.uploadlicense.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (ValueOutOfRangeException valueOutOfRangeException) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + valueOutOfRangeException.getMessage());
            Logger.logTrace(MODULE, valueOutOfRangeException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(valueOutOfRangeException);
			request.setAttribute("errorDetails", errorElements);
            request.setAttribute("responseUrl", "/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.uploadlicense.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (DataValidationException validationException) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + validationException.getMessage());
            Logger.logTrace(MODULE, validationException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(validationException);
			request.setAttribute("errorDetails", errorElements);
            request.setAttribute("responseUrl", "/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.uploadlicense.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (DataManagerException mgrException) {
            Logger.logError(MODULE, "Error during Data Manager (DataMgr) operation , reason :" + mgrException.getMessage());
            Logger.logTrace(MODULE, mgrException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(mgrException);
			request.setAttribute("errorDetails", errorElements);
            request.setAttribute("responseUrl", "/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.uploadlicense.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);

        }
        catch (Exception exp) {
            Logger.logError(MODULE, "Error during Data Manager (DataMgr) operation , reason :" + exp.getMessage());
            Logger.logTrace(MODULE, exp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
            request.setAttribute("responseUrl", "/listNetServerInstance");
            ActionMessage message = new ActionMessage("servermgr.server.uploadlicense.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
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
    }
}
