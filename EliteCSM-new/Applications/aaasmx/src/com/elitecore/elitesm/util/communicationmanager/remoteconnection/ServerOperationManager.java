/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ServerOperationManager.java                             
 * ModualName                                     
 * Created on Dec 12, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.util.communicationmanager.remoteconnection;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
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
public class ServerOperationManager {
    
    private static ServerOperationManager              serverOperationManager = new ServerOperationManager();
    
    private static final String                        MODULE                 = "SERVER OP MANAGER";
    
    private static Map<Long, RestartOperationThread> operationThreadMap     = new HashMap<Long, RestartOperationThread>();
    
    private static Map<String, RestartOperationThread> resultThreadMap        = new HashMap<String, RestartOperationThread>();
    
    public static final int                            NOT_STARTED            = 0;
    public static final int                            STARTUP_IN_PROGRESS    = 1;
    public static final int                            RUNNING                = 2;
    public static final int                            SHUTDOWN_IN_PROGRESS   = 3;
    public static final int                            STOPPED                = 4;
    public static final int                            ERROR                  = -1;
    
    private ServerOperationManager() {}
    
    public static ServerOperationManager getInstance( ) {
        return serverOperationManager;
    }
    
    public RestartOperationThread getOperationThread( long netServerInstanceId ) {
        Logger.logDebug(MODULE, "Returning opThread" + netServerInstanceId);
        return operationThreadMap.get(netServerInstanceId);
    }
    
    public boolean isInProcess( String netServerId ) {
        if (operationThreadMap.containsKey(netServerId)){
        	return true;
        }else{
        	return false;
        }
    }
    
    /*
     * @author kaushik vira
     * @param 
     * @purpose -1 - Restart process process running.
     *           0 - Error in Restart process
     *           1 - Successfull Restart. 
     */

    public int isSuccessfullRestart( String netServerId ) {
        
        if (isInProcess(netServerId))
            return -1;
        if (resultThreadMap.containsKey(netServerId)) {
            if (resultThreadMap.get(netServerId).isException == true) 
                return 0;
            return 1;
        } 
        return -2;
    }
    
    public void setOperationThread( INetServerInstanceData netServerInstanceData, INetServerTypeData netServerTypeData ) throws InitializationFailedException {
        
        Logger.logDebug(MODULE, "Enter In setOperationThread");
        Logger.logDebug(MODULE, "RestartOperationThread Map size :" + operationThreadMap.size());
        
        if (!operationThreadMap.containsKey(netServerInstanceData.getNetServerId())) {
            Logger.logDebug(MODULE, "Adding Restart Server Process in operartion Thread Map");
            RestartOperationThread opThread = new RestartOperationThread(netServerInstanceData,netServerTypeData, CommunicationConstant.TELNET, CommunicationConstant.JMX_RMI_JINI);
            operationThreadMap.put(new Long(netServerInstanceData.getNetServerId()), opThread);
            resultThreadMap.remove(netServerInstanceData.getNetServerId());
            Logger.logDebug(MODULE, "RestartOperationThread Map size :" + operationThreadMap.size());
        } else {
            try {
                Logger.logDebug(MODULE, "Restaring process All ready exits Thread state :" + operationThreadMap.get(netServerInstanceData.getNetServerId()).thread.getState());
            }
            catch (Exception e) {}
        }
    }
    
    private void remvoeOperationThread( String netServerInstanceId ) {
        operationThreadMap.remove(netServerInstanceId);
    }
    
    
    public class RestartOperationThread implements Runnable {
        
        
        private int                         state;
        
        private boolean                     isException = false;
        
        private IRemoteOperartionManager    iRemoteOperationManager;
        
        private IRemoteCommunicationManager iRemoteCommunicationManager;
        
        private INetServerInstanceData      netServerInstanceData;
        
        private Thread                      thread;
        
        public RestartOperationThread( INetServerInstanceData netServerInstanceData , INetServerTypeData netServerTypeData, String remoteProtocol , String jmxType ) throws InitializationFailedException {
            try {
                iRemoteOperationManager = RemoteOperationManagerFactory.getRemoteOperationManager(remoteProtocol, netServerInstanceData,netServerTypeData);
                iRemoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(jmxType);
                this.netServerInstanceData = netServerInstanceData;
            }
            catch (InitializationFailedException e) {
                throw e;
            }
            Logger.logDebug(MODULE, "Member varible are initiated in RestartOperationThread");
            thread = new Thread(this);
            thread.start();
            thread.setName("Server Instance Thread:"+netServerInstanceData.getNetServerId());
        }
        
        private void stopServer( ) throws CommunicationException, ConnectException {
            try {
                Logger.logDebug(Thread.currentThread().getName(), "Execute stopServer() for serverId");
                String netServerCode = null;
                try {
                    netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(), ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
                }
                catch (NoSuchEncryptionException e) {
                    Logger.logError(MODULE, "Unable to crypt password.");
                    Logger.logTrace(MODULE, e);
                    netServerCode = netServerInstanceData.getNetServerCode();
				} catch (EncryptionFailedException e) {
                    Logger.logError(MODULE, "Unable to crypt password.");
                    Logger.logTrace(MODULE, e);
                    netServerCode = netServerInstanceData.getNetServerCode();
                }
                try {
                    iRemoteCommunicationManager.init(netServerInstanceData.getAdminHost(), netServerInstanceData.getAdminPort(), netServerCode, false);
                }
                catch (UnidentifiedServerInstanceException e) {
                    Logger.logDebug(MODULE, "Unindentified Server instance found. No Action taken. ");
                }
                iRemoteCommunicationManager.execute(MBeanConstants.CONFIGURATION, "shutdownServer", null, null);
                
            }
            catch (CommunicationException e) {
                throw e;
            }
            finally {
                iRemoteCommunicationManager.close();
            }
        }
        
        public void disconnect( ) throws CommunicationException {
            iRemoteOperationManager.disconnect();
        }
        
        public int getState( ) {
            return state;
        }
        
        public  void run( ) {
            
            try {
                Logger.logDebug(MODULE, "Execute run() for restart server serverId" + netServerInstanceData.getNetServerId());
                try {
                    Logger.logDebug(Thread.currentThread().getName(), "Sending Stop signal to Server");
                    stopServer();
                }
                catch (Exception e) {
                    Logger.logDebug(Thread.currentThread().getName(), "Error in Stopping Server.");
                    throw e;
                }
                Logger.logDebug(Thread.currentThread().getName(), "Waitting For Shutingdown Server SuccessFully");
                disconnect();
                Logger.logDebug(Thread.currentThread().getName(), "Server Stopped Successfully.");
                try {
                    Logger.logDebug(Thread.currentThread().getName(), "Starting Server");
                    iRemoteOperationManager.startServer();
                    Logger.logDebug(Thread.currentThread().getName(), "Server Restarted Successfully,Please Verify Server Logs.");
                    
                }
                catch (Exception e) {
                    Logger.logDebug(Thread.currentThread().getName(), "Error in Restarting Server");
                    Logger.logTrace(Thread.currentThread().getName(), e);
                    throw e;
                }
                finally {
                    disconnect();
                }
                
            }
            catch (Exception e) {
                Logger.logDebug(Thread.currentThread().getName(), "Error in  Restart Process");
                Logger.logTrace(Thread.currentThread().getName(), e);
                isException = true;
            }
            finally {
                ServerOperationManager.resultThreadMap.put(netServerInstanceData.getNetServerId(), ServerOperationManager.operationThreadMap.get(netServerInstanceData.getNetServerId()));
                ServerOperationManager.this.remvoeOperationThread(netServerInstanceData.getNetServerId());
                Logger.logDebug(Thread.currentThread().getName(), "Operation Thread Moved from Operation Map to Result Map");
            }
        }
    }
    
}
