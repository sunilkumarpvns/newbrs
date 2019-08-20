/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   IRemoteCommunicationManager.java                             
 * ModualName                                     
 * Created on Nov 21, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.util.communicationmanager.remotecommunication;

import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;

/**
 * @author kaushikvira
 */
public interface IRemoteCommunicationManager {
    
    public void init( String hostIp ,String port , String encServerIdentifier, boolean serverIdVerifyFlag) throws CommunicationException, UnidentifiedServerInstanceException;

    public void init( String hostIp ,long port , String encServerIdentifier, boolean serverIdVerifyFlag) throws CommunicationException, UnidentifiedServerInstanceException;

    public void init( String hostIp ,int port , String encServerIdentifier, boolean serverIdVerifyFlag) throws CommunicationException, UnidentifiedServerInstanceException;
    
    public void init( String hostIp ,String port , String encServerIdentifier) throws CommunicationException, UnidentifiedServerInstanceException;

    public void init( String hostIp ,long port , String encServerIdentifier) throws CommunicationException, UnidentifiedServerInstanceException;

    public void init( String hostIp ,int port , String encServerIdentifier) throws CommunicationException, UnidentifiedServerInstanceException;

    

    public Object execute( String objectName ,String method ,Object[] params ,String[] signs ) throws CommunicationException;

    Object getAttribute( String name ,String attribute ) throws CommunicationException;
    
    public void close( );
    
}
