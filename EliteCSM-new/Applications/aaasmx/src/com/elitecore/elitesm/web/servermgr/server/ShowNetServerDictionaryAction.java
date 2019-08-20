package com.elitecore.elitesm.web.servermgr.server;

import java.io.File;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

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
import com.elitecore.elitesm.web.servermgr.server.forms.ShowNetServerDictionaryForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class ShowNetServerDictionaryAction extends BaseDictionaryAction {
    
    private static final String SUCCESS_FORWARD = "showNetServerDictionary";
    private static final String FAILURE_FORWARD = "failure";
    
    public ActionForward execute( ActionMapping mapping ,
                                  ActionForm form ,
                                  HttpServletRequest request ,
                                  HttpServletResponse response ) throws Exception {
        Logger.logInfo(MODULE, "Enter the execute method of :" + getClass().getName());
        IRemoteCommunicationManager remoteCommunicationManager = null;
        
        try {
            ShowNetServerDictionaryForm showNetServerDictionaryForm = (ShowNetServerDictionaryForm) form;
            // String fileName = request.getParameter("fileName");
            String fileName = showNetServerDictionaryForm.getFileName();
            File file = new File(fileName);
            String fileGroup = showNetServerDictionaryForm.getFileGroup();
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            
            String serverId = showNetServerDictionaryForm.getNetServerId();
            // String serverId = request.getParameter("netServerId");
            showNetServerDictionaryForm.setNetServerId(serverId);

            
            INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(serverId);
            
            if (serverId != null) {
                String ipAddress = netServerInstanceData.getAdminHost();
                int port = netServerInstanceData.getAdminPort();
                byte[] dataBytes = null;
                
                Object[] objArgValues = { fileName,fileGroup };
                String[] strArgTypes = { "java.lang.String", "java.lang.String"};
                String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);                
                remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
                remoteCommunicationManager.init(ipAddress, port, netServerCode,true);
                dataBytes = (byte[]) remoteCommunicationManager.execute("Elitecore:type=EliteAdmin", "readFileInByte", objArgValues, strArgTypes);
                
                // String dictionaryFile=null;
                /*
                 * FileOutputStream out = new FileOutputStream(file); out.write(dataBytes); out.close();
                 */// return mapping.f
                OutputStream out = response.getOutputStream();
                response.setContentType("text/xml");
                out.write(dataBytes);
                out.flush();
                // out.close();
                // ActionForward actionForward = new ActionForward();
                
                request.setAttribute("xmlByteData", dataBytes);
                return mapping.findForward(SUCCESS_FORWARD);
            } else {
                mapping.findForward(FAILURE_FORWARD);
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
        catch (CommunicationException seException) {
            Logger.logError(MODULE, "Error during operation,reason :" + seException.getMessage());
            Logger.logTrace(MODULE, seException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(seException);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.dictionary.jmx.server.error");
            ActionMessages messages = new ActionMessages();
            messages.add("warn", message);
            saveErrors(request, messages);
            
        }
        /*
         * catch(IOException connectionError){ Logger.logError(MODULE,"Error during operation,reason :"+connectionError.getMessage()); Logger.logTrace(MODULE,connectionError); ActionMessage message =
         * new ActionMessage("servermgr.server.dictionary.connection.failure"); ActionMessages messages = new ActionMessages(); messages.add("warn",message); saveErrors(request,messages); }
         */catch (Exception managerExp) {
            Logger.logError(MODULE, "Error during data Manager operation,reason :" + managerExp.getMessage());
            Logger.logTrace(MODULE, managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.dictionary.upload.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("warn", message);
            saveErrors(request, messages);
            
        }
        finally {
            try {
                if (remoteCommunicationManager != null)
                    remoteCommunicationManager.close();
            }
            catch (Throwable e) {
                remoteCommunicationManager = null;
            }
        }
        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
        return mapping.findForward(FAILURE_FORWARD);
    }
}
