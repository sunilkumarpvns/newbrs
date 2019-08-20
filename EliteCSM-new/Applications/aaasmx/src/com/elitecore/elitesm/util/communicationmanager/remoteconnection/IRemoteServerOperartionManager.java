/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   IRemoteCommunicationManager.java                             
 * ModualName                                     
 * Created on Oct 19, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.util.communicationmanager.remoteconnection;

import java.net.ConnectException;

import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.InitializationFailedException;

/**
 * @author kaushikvira
 */
public interface IRemoteServerOperartionManager {
    
    public void startServer( ) throws CommunicationException;
    
    public boolean initRestartServer( ) throws InitializationFailedException,
                                       CommunicationException;
    
    public boolean checkRemoteServerConnectionPossible() throws ConnectException;
    
    public boolean isProcessRunning( );
    
    public int isSuccessfullRestart( );
    
}
