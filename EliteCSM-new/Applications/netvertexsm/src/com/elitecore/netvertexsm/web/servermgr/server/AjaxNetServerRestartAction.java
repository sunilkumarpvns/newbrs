/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   NetServerRestartAction.java                             
 * ModualName                                     
 * Created on Dec 11, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.web.servermgr.server;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.opererationfailed.InitializationFailedException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerTypeData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remoteconnection.IRemoteServerOperartionManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;

/**
 * @author kaushikvira
 */
public class AjaxNetServerRestartAction extends BaseWebAction {
    
    private static final String MODULE = "RESTART NETSERVER INSTANCE";
    
    public ActionForward execute( ActionMapping mapping ,
                                  ActionForm form ,
                                  HttpServletRequest request ,
                                  HttpServletResponse response ) throws Exception {
        
        Logger.logInfo(MODULE, "Enter execute method of " + getClass().getName());
        response.setContentType("text/xml");
        response.setHeader("Cache-Control", "no-cache");
        INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
        INetServerTypeData netServerTypeData = new NetServerTypeData();        
        String strNetServerId = null;
        try {
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            strNetServerId = request.getParameter("netServerId");
            
            if (strNetServerId == null)
            	strNetServerId = (String) request.getAttribute("netServerId");
            
            if (strNetServerId == null)
                throw new InvalidValueException("netServerId Detail must be specified.");
            long netServerId = Long.parseLong(strNetServerId.trim());
            netServerInstanceData.setNetServerId(netServerId);
            netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
            netServerTypeData = netServerBLManager.getNetServerType(netServerInstanceData.getNetServerTypeId());
            IRemoteServerOperartionManager iRemoteServerOperarionManager = CommunicationManagerFactory.getRemoteServeraoperarionManager(CommunicationConstant.TELNET, netServerInstanceData, netServerTypeData);
            
            if (!iRemoteServerOperarionManager.checkRemoteServerConnectionPossible()) {
                PrintWriter out = null;
                try {
                    out = new PrintWriter(response.getWriter());
                    Logger.logDebug(MODULE, "<response>-1</response>");
                    out.write("<response>-1</response>");
                    out.flush();
                    out.close();
                }
                catch (Exception e) {
                    Logger.logTrace(MODULE, e);
                    out = null;
                }
                return null;
            }
            
            if (!iRemoteServerOperarionManager.initRestartServer()) {
                PrintWriter out = null;
                try {
                    out = new PrintWriter(response.getWriter());
                    Logger.logDebug(MODULE, "<response>-2</response>");
                    out.write("<response>-2</response>");
                    out.flush();
                    out.close();
                }
                catch (Exception e) {
                    Logger.logTrace(MODULE, e);
                    out = null;
                }
                return null;
            } else {
                PrintWriter out = null;
                try {
                    out = new PrintWriter(response.getWriter());
                    Logger.logDebug(MODULE, "<response>3</response>");
                    out.write("<response>3</response>");
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
            Logger.logDebug(MODULE, "<response>-3</response>");
            out.write("<response>-3</response>");
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
