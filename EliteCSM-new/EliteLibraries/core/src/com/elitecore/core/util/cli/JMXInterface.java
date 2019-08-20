package com.elitecore.core.util.cli;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public final class JMXInterface {
	
	private JMXServiceURL url;
	private JMXConnector jmxConnector;
	private MBeanServerConnection mbeanServerConnection;

	private boolean isInitialized = false;
	
	private static JMXInterface instance;
	
	private JMXInterface(){
		
	}
	
	static {
		instance = new JMXInterface();
	}
	
	public static JMXInterface getInstance(){
		return instance;
	}
	
	public void init(String host, int port) throws JMXException {

		//TODO Pending - proper status
		if(isInitialized)
			return;
		try {
			
			url =  new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+host+":"+port+"/jmxrmi");
			jmxConnector = JMXConnectorFactory.connect(url, null);
			mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			isInitialized = true;
		}catch (Exception e){
			mbeanServerConnection = null;
			if(jmxConnector != null){
				try {
					jmxConnector.close();
				} catch (IOException e1) {
				}
			}
			
			throw new JMXException("Problem initializing JMX interface, reason : " + e.getMessage(), e);	
		}
	}

	private void reInitialize() throws JMXException {
		isInitialized = false;
		mbeanServerConnection = null;
		try {
			if(jmxConnector != null){
				try {
					jmxConnector.close();
				} catch (IOException e1) {
				}
			}
			jmxConnector = JMXConnectorFactory.connect(url, null);
			mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			isInitialized = true;
		}catch (Exception e){
			throw new JMXException("Problem initializing JMX interface, reason : " + e.getMessage(), e);	
		}
	}

	public Object makeCall(String objectName, String method, Object[] params, String[]signs) throws JMXException {
		
		if (isInitialized == false || mbeanServerConnection == null)
			reInitialize();
		
		Object resultObject = null;
		
		try {
			if(mbeanServerConnection == null){
				throw new JMXException("Problem initializing JMX interface, Reason: Unknown");
			}
			resultObject = mbeanServerConnection.invoke(new ObjectName(objectName), method, params, signs);
			
		}catch (IOException e) {
			isInitialized = false;
			//e.printStackTrace();
			throw new JMXException(" 0 Communication problem.",e);
		}catch (Exception exp) {
			isInitialized = false;
			//exp.printStackTrace();
			throw new JMXException(" 1 Communication problem.",exp);
		}
		
		return resultObject;
	}
	
	/**
	 * This method is used to get value of a specific attribute of a named MBean.
	 * Doesn't requires parameters as only attribute is being accessed
	 * 
	 * @return The value of {@code attribute}.
	 * @param objectName
	 * @param attribute
	 * @throws JMXException 
	 *  */
	
	public Object makeCall(String objectName, String attribute) throws JMXException {
		if (isInitialized == false || mbeanServerConnection == null) {
			reInitialize();
		}

		Object resultObject = null;

		try {
			if(mbeanServerConnection == null){
				throw new JMXException("Problem initializing JMX interface, Reason: Unknown");
			}
			resultObject = mbeanServerConnection.getAttribute(new ObjectName(objectName), attribute);
		} catch (IOException e) {
			isInitialized = false;
			throw new JMXException(" 0 Communication problem.",e);
		} catch (Exception exp) {
			isInitialized = false;
			throw new JMXException(" 1 Communication problem.",exp);
		}

		return resultObject;
	}
}

