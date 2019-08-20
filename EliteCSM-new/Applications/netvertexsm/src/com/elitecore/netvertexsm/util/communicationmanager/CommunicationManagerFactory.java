/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   CommunicationManagerFactory.java                             
 * ModualName                                     
 * Created on Dec 13, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.util.communicationmanager;

import com.elitecore.netvertexsm.datamanager.core.exceptions.opererationfailed.InitializationFailedException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.RemoteCommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remoteconnection.IRemoteServerOperartionManager;
import com.elitecore.netvertexsm.util.communicationmanager.remoteconnection.ServerOperations;

/**
 * @author kaushikvira
 */
public class CommunicationManagerFactory {
    
    public CommunicationManagerFactory() {}
    
    public static IRemoteCommunicationManager getRemoteCommunicationManager( String type ) throws InitializationFailedException {
        
        return RemoteCommunicationManagerFactory.getRemoteCommunicationManager(type);
    }
    
    public static IRemoteServerOperartionManager getRemoteServeraoperarionManager( String type ,
                                                                                   INetServerInstanceData iNetServerInstanceData, 
                                                                                   INetServerTypeData iNetServerTypeData ) throws InitializationFailedException {
        return new ServerOperations(type, iNetServerInstanceData, iNetServerTypeData);
    }
    
}
