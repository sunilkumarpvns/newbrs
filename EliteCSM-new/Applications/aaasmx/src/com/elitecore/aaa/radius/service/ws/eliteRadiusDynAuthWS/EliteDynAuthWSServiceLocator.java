/**
 * EliteDynAuthWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS;

public class EliteDynAuthWSServiceLocator extends org.apache.axis.client.Service implements com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS.EliteDynAuthWSService {

 
    
    public EliteDynAuthWSServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EliteDynAuthWSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for eliteRadiusDynAuthWS
    private java.lang.String eliteRadiusDynAuthWS_address = "http://brijesh-desktop:8081/eliteradius/services/eliteRadiusDynAuthWS";

    public EliteDynAuthWSServiceLocator() {
    }

    public EliteDynAuthWSServiceLocator(String webserviceURL) {
    	eliteRadiusDynAuthWS_address=webserviceURL;
    }
    
    public java.lang.String geteliteRadiusDynAuthWSAddress() {
        return eliteRadiusDynAuthWS_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String eliteRadiusDynAuthWSWSDDServiceName = "eliteRadiusDynAuthWS";

    public java.lang.String geteliteRadiusDynAuthWSWSDDServiceName() {
        return eliteRadiusDynAuthWSWSDDServiceName;
    }

    public void seteliteRadiusDynAuthWSWSDDServiceName(java.lang.String name) {
        eliteRadiusDynAuthWSWSDDServiceName = name;
    }

    public com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS.EliteDynAuthWS geteliteRadiusDynAuthWS() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(eliteRadiusDynAuthWS_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return geteliteRadiusDynAuthWS(endpoint);
    }

    public com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS.EliteDynAuthWS geteliteRadiusDynAuthWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS.EliteRadiusDynAuthWSSoapBindingStub _stub = new com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS.EliteRadiusDynAuthWSSoapBindingStub(portAddress, this);
            _stub.setPortName(geteliteRadiusDynAuthWSWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void seteliteRadiusDynAuthWSEndpointAddress(java.lang.String address) {
        eliteRadiusDynAuthWS_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS.EliteDynAuthWS.class.isAssignableFrom(serviceEndpointInterface)) {
                com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS.EliteRadiusDynAuthWSSoapBindingStub _stub = new com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS.EliteRadiusDynAuthWSSoapBindingStub(new java.net.URL(eliteRadiusDynAuthWS_address), this);
                _stub.setPortName(geteliteRadiusDynAuthWSWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("eliteRadiusDynAuthWS".equals(inputPortName)) {
            return geteliteRadiusDynAuthWS();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://eliteRadiusDynAuthWS.ws.service.radius.aaa.elitecore.com", "EliteDynAuthWSService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://eliteRadiusDynAuthWS.ws.service.radius.aaa.elitecore.com", "eliteRadiusDynAuthWS"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("eliteRadiusDynAuthWS".equals(portName)) {
            seteliteRadiusDynAuthWSEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
