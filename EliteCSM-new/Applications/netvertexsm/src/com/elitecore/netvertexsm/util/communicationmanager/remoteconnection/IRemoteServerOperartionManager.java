/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   IRemoteCommunicationManager.java                             
 * ModualName                                     
 * Created on Oct 19, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.util.communicationmanager.remoteconnection;

import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.opererationfailed.InitializationFailedException;

/**
 * @author kaushikvira
 */
public interface IRemoteServerOperartionManager {
    
    public void startServer( ) throws CommunicationException;
    
    public boolean initRestartServer( ) throws InitializationFailedException,
                                       CommunicationException;
    
    public boolean checkRemoteServerConnectionPossible( );
    
    public boolean isProcessRunning( );
    
    public int isSuccessfullRestart( );
    
}
