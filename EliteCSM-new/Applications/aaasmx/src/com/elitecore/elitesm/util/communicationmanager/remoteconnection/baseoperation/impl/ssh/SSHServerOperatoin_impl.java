/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SSHServerOperatoin_impl.java                             
 * ModualName                                     
 * Created on Dec 12, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.util.communicationmanager.remoteconnection.baseoperation.impl.ssh;

import java.net.InetAddress;

import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.OperationFailedException;
import com.elitecore.elitesm.util.communicationmanager.remoteconnection.baseoperation.IRemoteOperartionManager;

/**
 * @author kaushikvira
 */
public class SSHServerOperatoin_impl implements IRemoteOperartionManager {
    
    public void cdServerSytemDir( ) throws CommunicationException {
    // TODO Auto-generated method stub
    
    }
    
    public void connect( ) throws CommunicationException {
    // TODO Auto-generated method stub
    
    }
    
    public void disconnect( ) {
    // TODO Auto-generated method stub
    
    }
    
    public InetAddress getLocalAddress( ) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public int getLocalPort( ) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public String getProtocol( ) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public InetAddress getRemoteAddress( ) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public int getRemotePort( ) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public String getServerState( ) throws CommunicationException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public boolean isAlive( long timeout ) throws OperationFailedException {
        // TODO Auto-generated method stub
        return false;
    }
    
    public boolean isConnected( ) {
        // TODO Auto-generated method stub
        return false;
    }
    
    public void startServer( ) throws CommunicationException {
    // TODO Auto-generated method stub
    
    }
    
    public boolean isConnectionPossible( ) {
        // TODO Auto-generated method stub
        return false;
    }
}
