/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   IRemoteCommunicationManager.java                             
 * ModualName                                     
 * Created on Oct 19, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.util.communicationmanager.remoteconnection.baseoperation;

import java.net.InetAddress;

import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.OperationFailedException;

/**
 * @author kaushikvira
 */
public interface IRemoteOperartionManager {
    
    public void connect( ) throws CommunicationException;
    
    public boolean isConnected( );
    
    public boolean isAlive( long timeout ) throws OperationFailedException;
    
    public String getProtocol( );
    
    public void disconnect( );
    
    public void startServer( ) throws CommunicationException;
    
    public InetAddress getRemoteAddress( );
    
    public int getRemotePort( );
    
    public InetAddress getLocalAddress( );
    
    public int getLocalPort( );
    
    public boolean isConnectionPossible( );
    
}
