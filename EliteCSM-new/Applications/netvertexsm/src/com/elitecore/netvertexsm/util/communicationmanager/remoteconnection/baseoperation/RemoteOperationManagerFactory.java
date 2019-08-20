/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   RemoteOperationManagerFactory.java                             
 * ModualName                                     
 * Created on Oct 19, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.util.communicationmanager.remoteconnection.baseoperation;

import com.elitecore.netvertexsm.datamanager.core.exceptions.opererationfailed.InitializationFailedException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.netvertexsm.util.communicationmanager.remoteconnection.baseoperation.impl.telnet.TelnetServerOperation_impl;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;

/**
 * @author kaushikvira
 */
public class RemoteOperationManagerFactory {
    
    public static IRemoteOperartionManager getRemoteOperationManager( String communicationType ,
                                                                      INetServerInstanceData iNetServerInstanceData, INetServerTypeData iNetServerTypeData ) throws InitializationFailedException {
        
        IRemoteOperartionManager commManagerInterface = null;
        try {
            if (communicationType.equalsIgnoreCase(CommunicationConstant.TELNET))
                commManagerInterface = (IRemoteOperartionManager) new TelnetServerOperation_impl(iNetServerInstanceData, iNetServerTypeData);
            else throw new InitializationFailedException("Operatation Not Supported");
        }
        catch (Exception e) {
            throw new InitializationFailedException("Unable to Create Instance for Util Class", e);
        }
        return commManagerInterface;
    }
}
