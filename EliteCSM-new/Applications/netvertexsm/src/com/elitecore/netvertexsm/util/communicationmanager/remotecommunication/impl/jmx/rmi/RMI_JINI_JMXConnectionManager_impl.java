/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   RMI_JINI_JMXConnectionManager_impl.java                             
 * ModualName                                     
 * Created on Nov 24, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.impl.jmx.rmi;

import java.net.Inet6Address;
import java.net.InetAddress;

import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.impl.BaseConnectionManager_impl;


/**
 * @author kaushikvira
 */
public class RMI_JINI_JMXConnectionManager_impl extends BaseConnectionManager_impl implements IRemoteCommunicationManager {

    private JMXServiceURL         url;
    private JMXConnector          jmxConnector;
    private MBeanServerConnection mbeanServerConnection;
    private boolean               isInitialized;
    private boolean               isCommunicationProblem;

    public void init( String hostIp ,long port, String encServerIdentifier, boolean serverIdVerifyFlag ) throws CommunicationException, UnidentifiedServerInstanceException {

        // Convert long to String
        String strPort = "" + port;
        // Call init init( String hostIp , String port );
        // Please Be carefull.. other wise it can create infinite Loop...
        if (strPort instanceof String)
            init(hostIp, strPort, encServerIdentifier, serverIdVerifyFlag);
    }

    public void init( String hostIp ,int port, String encServerIdentifier, boolean serverIdVerifyFlag ) throws CommunicationException, UnidentifiedServerInstanceException {

        // Convert long to String
        String strPort = "" + port;
        // Call init init( String hostIp , String port );
        // Please Be carefull.. other wise it can create infinite Loop...
        if (strPort instanceof String)
            init(hostIp, strPort, encServerIdentifier, serverIdVerifyFlag);
    }

    public void init( String hostIp ,String port, String encServerIdentifier, boolean serverIdVerifyFlag ) throws CommunicationException, UnidentifiedServerInstanceException {

        try {
            validateRemoteIp(hostIp);
            validateRemotePort(port);
        }
        catch (DataValidationException e) {
            throw new CommunicationException("Invalid RemoteIp or port, Reason :" + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new CommunicationException("Invalid RemoteIp or port, Reason :" + e.getMessage(), e);
        }

        try {
        	 
            isInitialized = true;
            isCommunicationProblem = false;
            InetAddress inetAddress=InetAddress.getByName(hostIp.trim());
            if(inetAddress instanceof Inet6Address){
            	url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://[" + inetAddress.getHostAddress() + "]:" + port + "/jmxrmi");
            }else {
               url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + inetAddress.getHostAddress() + ":" + port + "/jmxrmi");
            }
            
                     
            
            try {
                jmxConnector = JMXConnectorFactory.connect(url, null);
                mbeanServerConnection = jmxConnector.getMBeanServerConnection();

            }
            catch (Exception e) {
                try{
                    Thread.sleep(1000);
                }catch(Throwable e1){
                }
                jmxConnector = JMXConnectorFactory.connect(url, null);
                mbeanServerConnection = jmxConnector.getMBeanServerConnection();
            }
         
            if(serverIdVerifyFlag){
                Object[] objArg = {};
                String[] strArg = {};
                String serverInstanceId = (String)this.execute(MBeanConstants.CONFIGURATION,"readServerInstanceId", objArg, strArg);
                
                if(serverInstanceId != null){
                    if(!serverInstanceId.equals(encServerIdentifier)){
                        throw new UnidentifiedServerInstanceException("Server Identifier invalid.");
                    }
                }else{
                    throw new UnidentifiedServerInstanceException("Server Identifier invalid.");            		
                }
            }


        }catch (UnidentifiedServerInstanceException e) {
            isCommunicationProblem = true;
            throw e; 
        }
        catch (Exception e) {
            isCommunicationProblem = true;
            throw new CommunicationException("Problem initializing Remote interface, reason : " + e.getMessage(), e);
        }

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

    private void reInitialize( ) throws CommunicationException {
        try {
            jmxConnector = JMXConnectorFactory.connect(url, null);
            mbeanServerConnection = jmxConnector.getMBeanServerConnection();
            isCommunicationProblem = false;
        }
        catch (Exception e) {
            throw new CommunicationException("Problem initializing Remote interface, Reason : " + e.getMessage(), e);
        }
    }

    public Object getAttribute(String name,String attribute) throws CommunicationException
    {

        if (!isInitialized)
            throw new CommunicationException("Not initialized, explicit initialization required.");

        if (isCommunicationProblem)
            reInitialize();

        Object resultObject = null;
        try {
            resultObject = mbeanServerConnection.getAttribute(new ObjectName(name),attribute);
        }
        catch (JMException exp) {
            throw new CommunicationException("function Call failed.   Reason : " + exp.getMessage(), exp);
        }
        catch (Exception exp) {
            throw new CommunicationException("function Call failed.  Reason : " + exp.getMessage(), exp);
        }

        return resultObject;
    }

    public Object execute( String objectName ,String method , Object[] params ,String[] signs ) throws CommunicationException {


        if (!isInitialized)
            throw new CommunicationException("Not initialized, explicit initialization required.");

        if (isCommunicationProblem)
            reInitialize();

        Object resultObject = null;
        try {

            resultObject = mbeanServerConnection.invoke(new ObjectName(objectName), method, params, signs);

        }
        catch (JMException exp) {

            throw new CommunicationException("function Call failed.   Reason : " + exp.getMessage(), exp);
        }
        catch (Exception exp) {

            throw new CommunicationException("function Call failed.  Reason : " + exp.getMessage(), exp);
        }

        return resultObject;
    }

    public void close( ) {

        if (isInitialized == true) {

            if (jmxConnector != null) {
                try {
                    jmxConnector.close();
                }
                catch (Exception e) {
                    jmxConnector = null;
                }
            }
            mbeanServerConnection = null;
            url = null;
            isInitialized = false;
        }
    }
}
