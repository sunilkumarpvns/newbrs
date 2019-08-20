/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   RemoteCommunicationManagerFactory.java                             
 * ModualName                                     
 * Created on Nov 24, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.util.communicationmanager.remotecommunication;

import com.elitecore.netvertexsm.datamanager.core.exceptions.opererationfailed.InitializationFailedException;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;

/**
 * @author kaushikvira
 */
public class RemoteCommunicationManagerFactory {
    
    final private static String jmx_rmi_jini_impl   = "com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.impl.jmx.rmi.RMI_JINI_JMXConnectionManager_impl";
    final private static String jmx_jmxmp_jini_impl = "com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.impl.jmx.jmxmp.JMXMP_JINI_JMXConnectionManager_impl";
    
    public static IRemoteCommunicationManager getRemoteCommunicationManager(String cName) throws InitializationFailedException {
        
        IRemoteCommunicationManager remoteCommManagerInterface = null;
        try {
            
            if (cName.equalsIgnoreCase(CommunicationConstant.JMX_RMI_JINI)) {
                remoteCommManagerInterface = (IRemoteCommunicationManager) Class.forName(jmx_rmi_jini_impl).newInstance();
            } else if (cName.equalsIgnoreCase(CommunicationConstant.JMX_JMXMP_JINI)) {
                remoteCommManagerInterface = (IRemoteCommunicationManager) Class.forName(jmx_jmxmp_jini_impl).newInstance();
            } else throw new InitializationFailedException("Operatation Not Supported");
            
        }
        catch (Throwable e) {
            throw new InitializationFailedException("Unable to Create Instance Remote Communication Class , Please Check impl class qulified name", e);
        }
        return remoteCommManagerInterface;
    }
    
}
