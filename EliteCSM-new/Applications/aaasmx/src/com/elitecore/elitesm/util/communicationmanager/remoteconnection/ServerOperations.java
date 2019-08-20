/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ServerOperations.java                             
 * ModualName                                     
 * Created on Dec 13, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.util.communicationmanager.remoteconnection;

import java.net.ConnectException;

import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.InitializationFailedException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.communicationmanager.remoteconnection.baseoperation.IRemoteOperartionManager;
import com.elitecore.elitesm.util.communicationmanager.remoteconnection.baseoperation.RemoteOperationManagerFactory;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

/**
 * @author kaushikvira
 */
public class ServerOperations implements IRemoteServerOperartionManager {
    
    IRemoteOperartionManager iRemoteOperationManager;
    
    INetServerInstanceData   iNetServerInstanceData;
    
    INetServerTypeData       iNetServerTypeData;
    
    private static String    MODULE = "SERVER OPERATIONS";
    
    private String           type   = null;
   
    public ServerOperations( String type , INetServerInstanceData netServerInstanceData, INetServerTypeData netServerTypeData ) throws InitializationFailedException {
        this.type = type;
        this.iNetServerInstanceData = netServerInstanceData;
        this.iNetServerTypeData=netServerTypeData;
        iRemoteOperationManager = RemoteOperationManagerFactory.getRemoteOperationManager(this.type, netServerInstanceData,netServerTypeData);
    }
    
    public boolean checkRemoteServerConnectionPossible() throws ConnectException {
        Logger.logDebug(MODULE, "Checking Telnet Connection Between SM and " + iNetServerInstanceData.getNetServerId());
        
        Logger.logDebug(MODULE, "Checking JMX Connection");
        IRemoteCommunicationManager iRemoteCommunicaionManager = null;
        try {
            iRemoteCommunicaionManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
        }
        catch (InitializationFailedException e) {
            Logger.logDebug(MODULE, "Unable to Init " + CommunicationConstant.JMX_RMI_JINI + " Impl");
            return false;
        }
        String netServerCode = null;
        
        try {
            netServerCode = PasswordEncryption.getInstance().crypt(iNetServerInstanceData.getNetServerCode(), ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
        }
        catch (NoSuchEncryptionException e) {
            Logger.logError(MODULE, "Unable to crypt password.");
            Logger.logTrace(MODULE, e);
            netServerCode = iNetServerInstanceData.getNetServerCode();
		} catch (EncryptionFailedException e) {
            Logger.logError(MODULE, "Unable to crypt password.");
            Logger.logTrace(MODULE, e);
            netServerCode = iNetServerInstanceData.getNetServerCode();
        }
        try {
            iRemoteCommunicaionManager.init(iNetServerInstanceData.getAdminHost(), iNetServerInstanceData.getAdminPort(), netServerCode, false);
        }
        catch (UnidentifiedServerInstanceException e) {
            Logger.logDebug(MODULE, "Unindentified Server instance found. No Action taken. ");
        }
        catch (CommunicationException e) {
            Logger.logDebug(MODULE, CommunicationConstant.JMX_RMI_JINI + " Connection Checked Failed.");
            return false;
        }
        finally {
            iRemoteCommunicaionManager.close();
        }
        Logger.logDebug(MODULE, "Check JMX Connection Checked.");
        
        Logger.logDebug(MODULE, "Checking " + this.type + " Connection");
        
        if (!iRemoteOperationManager.isConnectionPossible()) {
            Logger.logDebug(MODULE, this.type + " Connection Checked Failed.");
            return false;
        }
        
        Logger.logDebug(MODULE, this.type + " Connection Checked Successfully.");
        return true;
    }
    
    public boolean initRestartServer( ) throws InitializationFailedException, CommunicationException {
        
        Logger.logDebug(MODULE, "Calling Restart Server Operation using " + type);
        
        if (ServerOperationManager.getInstance().isInProcess(iNetServerInstanceData.getNetServerId())) 
            return false;
        
        ServerOperationManager.getInstance().setOperationThread(iNetServerInstanceData,iNetServerTypeData);
        return true;
        
    }
    
    public void startServer( ) throws CommunicationException {
        try {
            Logger.logDebug(MODULE, "Calling start Server Operation using " + type);
            iRemoteOperationManager.connect();
            iRemoteOperationManager.startServer();
        }
        catch (CommunicationException e) {
            throw e;
        }
        catch (Exception e) {
            throw new CommunicationException(e);
        }
        finally {
            iRemoteOperationManager.disconnect();
        }
    }
    
    public boolean isProcessRunning( ) {
        Logger.logDebug(MODULE, "Checking Server restart process Running for " + iNetServerInstanceData.getNetServerId());
        return ServerOperationManager.getInstance().isInProcess(iNetServerInstanceData.getNetServerId());
    }
    
    /*
     * -1 - Restart process process running. 
     * 0 - Error in Restart process 
     * 1 - Successfull Restart. 
     */
    public int isSuccessfullRestart( ) {
        Logger.logDebug(MODULE, "Checking Server restart process Running for " + iNetServerInstanceData.getNetServerId());
        return ServerOperationManager.getInstance().isSuccessfullRestart(iNetServerInstanceData.getNetServerId());
    }
    
}
