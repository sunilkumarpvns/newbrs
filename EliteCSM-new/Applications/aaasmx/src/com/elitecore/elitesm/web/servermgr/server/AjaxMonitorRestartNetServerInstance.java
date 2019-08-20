/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   NetServerRestartAction.java                             
 * ModualName                                     
 * Created on Dec 11, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.web.servermgr.server;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.InitializationFailedException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerTypeData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remoteconnection.IRemoteServerOperartionManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;

/**
 * @author kaushikvira
 */
public class AjaxMonitorRestartNetServerInstance extends BaseDictionaryAction {
    
    private static final String MODULE = "RESTART NETSERVER INSTANCE";
    
    public ActionForward execute( ActionMapping mapping ,
                                  ActionForm form ,
                                  HttpServletRequest request ,
                                  HttpServletResponse response ) throws Exception {
        
        Logger.logInfo(MODULE, "Enter execute method of " + getClass().getName());
        response.setContentType("text/xml");
        response.setHeader("Cache-Control", "no-cache");
        INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
        INetServerTypeData   netServerTypeData = new NetServerTypeData();
        String strNetServerId = null;
        try {
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            strNetServerId = request.getParameter("netServerId");
            
            if (strNetServerId == null)
            	strNetServerId = (String) request.getAttribute("netServerId");
            
            if (strNetServerId == null)
                throw new InvalidValueException("netServerId Detail must be specified.");
            String netServerId = strNetServerId.trim();
            netServerInstanceData.setNetServerId(netServerId);
            netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
            netServerTypeData = netServerBLManager.getNetServerType(netServerInstanceData.getNetServerTypeId());
            IRemoteServerOperartionManager iRemoteServerOperarionManager = CommunicationManagerFactory.getRemoteServeraoperarionManager(CommunicationConstant.TELNET, netServerInstanceData,netServerTypeData);
            
            if (iRemoteServerOperarionManager.isProcessRunning()) {
                PrintWriter out = null;
                try {
                    out = new PrintWriter(response.getWriter());
                    Logger.logDebug(MODULE, "<monitorresponse>3</monitorresponse>");
                    out.write("<monitorresponse>3</monitorresponse>");
                    out.flush();
                    out.close();
                }
                catch (Exception e) {
                    Logger.logTrace(MODULE, e);
                    out = null;
                }
                return null;
            }
            
            int status = iRemoteServerOperarionManager.isSuccessfullRestart();
            
            if (status == -1) {
                PrintWriter out = null;
                try {
                    out = new PrintWriter(response.getWriter());
                    Logger.logDebug(MODULE, "<monitorresponse>3</monitorresponse>");
                    out.write("<monitorresponse>3</monitorresponse>");
                    out.flush();
                    out.close();
                }
                catch (Exception e) {
                    Logger.logTrace(MODULE, e);
                    out = null;
                }
                return null;
            } else if (status == 0) {
                PrintWriter out = null;
                try {
                    out = new PrintWriter(response.getWriter());
                    Logger.logDebug(MODULE, "<monitorresponse>0</monitorresponse>");
                    out.write("<monitorresponse>0</monitorresponse>");
                    out.flush();
                    out.close();
                }
                catch (Exception e) {
                    Logger.logTrace(MODULE, e);
                    out = null;
                }
                return null;
            } else if (status == 1) {
                PrintWriter out = null;
                try {
                    out = new PrintWriter(response.getWriter());
                    Logger.logDebug(MODULE, "<monitorresponse>1</monitorresponse>");
                    out.write("<monitorresponse>1</monitorresponse>");
                    out.flush();
                    out.close();
                }
                catch (Exception e) {
                    Logger.logTrace(MODULE, e);
                    out = null;
                }
                return null;
                
            } else if (status == -2) {
                PrintWriter out = null;
                try {
                    out = new PrintWriter(response.getWriter());
                    Logger.logDebug(MODULE, "<monitorresponse>-2</monitorresponse>");
                    out.write("<monitorresponse>-2</monitorresponse>");
                    out.flush();
                    out.close();
                }
                catch (Exception e) {
                    Logger.logTrace(MODULE, e);
                    out = null;
                }
                return null;
                
            }
            {
                PrintWriter out = null;
                try {
                    out = new PrintWriter(response.getWriter());
                    Logger.logDebug(MODULE, "<monitorresponse>-1</monitorresponse>");
                    out.write("<monitorresponse>-1</monitorresponse>");
                    out.flush();
                    out.close();
                }
                catch (Exception e) {
                    Logger.logTrace(MODULE, e);
                    out = null;
                }
                return null;
            }
            
        }
        catch (InitializationFailedException e) {
            Logger.logError(MODULE, "Error during Communication Manager Operation" + EliteExceptionUtils.getMessage(e));
            Logger.logTrace(MODULE, e);
            
        }
        catch (InvalidValueException e) {
            Logger.logError(MODULE, "Error during Communication Manager Operation" + EliteExceptionUtils.getMessage(e));
            Logger.logTrace(MODULE, e);
            
        }
        catch (DataValidationException e) {
            Logger.logError(MODULE, "Error during Communication Manager Operation" + EliteExceptionUtils.getMessage(e));
            Logger.logTrace(MODULE, e);
            
        }
        catch (DataManagerException exp) {
            Logger.logError(MODULE, "Error during Communication Manager Operation" + EliteExceptionUtils.getMessage(exp));
            Logger.logTrace(MODULE, exp);
            
        }
        catch (Exception e) {
            Logger.logError(MODULE, "Error during Communication Manager Operation" + EliteExceptionUtils.getMessage(e));
            Logger.logTrace(MODULE, e);
        }
        PrintWriter out = null;
        try {
            out = new PrintWriter(response.getWriter());
            Logger.logDebug(MODULE, "<monitorresponse>-1</monitorresponse>");
            out.write("<monitorresponse>-1</monitorresponse>");
            out.flush();
            out.close();
        }
        catch (Exception e) {
            Logger.logTrace(MODULE, e);
            out = null;
        }
        return null;
    }
    
}
