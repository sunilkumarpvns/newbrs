/**
 * EliteGenericRadiusServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.elitecore.elitesm.ws.ytl.cal.aaa.dm;

public class EliteGenericRadiusServiceLocator extends org.apache.axis.client.Service implements com.elitecore.elitesm.ws.ytl.cal.aaa.dm.EliteGenericRadiusService {

    public EliteGenericRadiusServiceLocator() {
    }


    public EliteGenericRadiusServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EliteGenericRadiusServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for EliteGenericRadiusWS
    private java.lang.String EliteGenericRadiusWS_address = "http://elitecore:8081/eliteradius/services/EliteGenericRadiusWS";

    public java.lang.String getEliteGenericRadiusWSAddress() {
        return EliteGenericRadiusWS_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EliteGenericRadiusWSWSDDServiceName = "EliteGenericRadiusWS";

    public java.lang.String getEliteGenericRadiusWSWSDDServiceName() {
        return EliteGenericRadiusWSWSDDServiceName;
    }

    public void setEliteGenericRadiusWSWSDDServiceName(java.lang.String name) {
        EliteGenericRadiusWSWSDDServiceName = name;
    }

    public com.elitecore.elitesm.ws.ytl.cal.aaa.dm.EliteGenericRadiusWS_PortType getEliteGenericRadiusWS() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EliteGenericRadiusWS_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEliteGenericRadiusWS(endpoint);
    }

    public com.elitecore.elitesm.ws.ytl.cal.aaa.dm.EliteGenericRadiusWS_PortType getEliteGenericRadiusWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.elitecore.elitesm.ws.ytl.cal.aaa.dm.EliteGenericRadiusWSSoapBindingStub _stub = new com.elitecore.elitesm.ws.ytl.cal.aaa.dm.EliteGenericRadiusWSSoapBindingStub(portAddress, this);
            _stub.setPortName(getEliteGenericRadiusWSWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEliteGenericRadiusWSEndpointAddress(java.lang.String address) {
        EliteGenericRadiusWS_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.elitecore.elitesm.ws.ytl.cal.aaa.dm.EliteGenericRadiusWS_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.elitecore.elitesm.ws.ytl.cal.aaa.dm.EliteGenericRadiusWSSoapBindingStub _stub = new com.elitecore.elitesm.ws.ytl.cal.aaa.dm.EliteGenericRadiusWSSoapBindingStub(new java.net.URL(EliteGenericRadiusWS_address), this);
                _stub.setPortName(getEliteGenericRadiusWSWSDDServiceName());
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
        if ("EliteGenericRadiusWS".equals(inputPortName)) {
            return getEliteGenericRadiusWS();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://eliteGenericRadiusWS.ws.service.radius.aaa.elitecore.com", "EliteGenericRadiusService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://eliteGenericRadiusWS.ws.service.radius.aaa.elitecore.com", "EliteGenericRadiusWS"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("EliteGenericRadiusWS".equals(portName)) {
            setEliteGenericRadiusWSEndpointAddress(address);
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
