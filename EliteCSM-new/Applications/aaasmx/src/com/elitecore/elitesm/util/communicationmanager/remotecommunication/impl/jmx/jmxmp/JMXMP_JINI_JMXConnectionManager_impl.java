/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   JMXMP_JINI_JMXConnectionManager_impl.java.java                             
 * ModualName                                     
 * Created on Nov 24, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.util.communicationmanager.remotecommunication.impl.jmx.jmxmp;

import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.impl.BaseConnectionManager_impl;

/**
 * @author kaushikvira
 */
public class JMXMP_JINI_JMXConnectionManager_impl extends BaseConnectionManager_impl implements IRemoteCommunicationManager {
    
    public void init( String hostIp ,String port , String encServerIdentifier, boolean serverIdVerifyFlag) throws CommunicationException, UnidentifiedServerInstanceException {

    }
    
    public void init( String hostIp ,long port, String encServerIdentifier, boolean serverIdVerifyFlag ) throws CommunicationException , UnidentifiedServerInstanceException{
    // TODO Auto-generated method stub
    
    }
    
    public void init( String hostIp ,int port, String encServerIdentifier, boolean serverIdVerifyFlag ) throws CommunicationException, UnidentifiedServerInstanceException {
    // TODO Auto-generated method stub
    
    }
    
    
    public void init(String hostIp, int port, String encServerIdentifier) throws CommunicationException, UnidentifiedServerInstanceException {
        init(hostIp, port, encServerIdentifier,true);
    }

    public void init(String hostIp, long port, String encServerIdentifier) throws CommunicationException, UnidentifiedServerInstanceException {
        init(hostIp, port, encServerIdentifier,true);

    }

    public void init(String hostIp, String port, String encServerIdentifier) throws CommunicationException, UnidentifiedServerInstanceException {
        init(hostIp, port, encServerIdentifier,true);
    }

    
   public Object execute( String objectName ,
                        String method ,
                        Object[] params ,
                        String[] signs ) throws CommunicationException {
    // TODO Auto-generated method stub
    return null;
}
    
   public Object getAttribute( String name ,
                            String attribute ) throws CommunicationException {
    // TODO Auto-generated method stub
    return null;
}
  
    public void close( ) {
    // TODO Auto-generated method stub
    
    }
    
}
