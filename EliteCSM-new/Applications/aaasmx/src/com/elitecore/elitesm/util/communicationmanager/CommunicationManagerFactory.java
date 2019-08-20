/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   CommunicationManagerFactory.java                             
 * ModualName                                     
 * Created on Dec 13, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.util.communicationmanager;

import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.InitializationFailedException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.RemoteCommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remoteconnection.IRemoteServerOperartionManager;
import com.elitecore.elitesm.util.communicationmanager.remoteconnection.ServerOperations;

/**
 * @author kaushikvira
 */
public class CommunicationManagerFactory {
    
    public CommunicationManagerFactory() {}
    
    public static IRemoteCommunicationManager getRemoteCommunicationManager( String type ) throws InitializationFailedException {
        
        return RemoteCommunicationManagerFactory.getRemoteCommunicationManager(type);
    }
    
    public static IRemoteServerOperartionManager getRemoteServeraoperarionManager( String type ,
                                                                                   INetServerInstanceData iNetServerInstanceData, INetServerTypeData iNetServerTypeData ) throws InitializationFailedException {
        return new ServerOperations(type, iNetServerInstanceData,iNetServerTypeData);
    }
    
}
