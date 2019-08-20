/**
 * EliteMAPSendRoutingInfoWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS;

public class EliteMAPSendRoutingInfoWSServiceLocator extends org.apache.axis.client.Service implements com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS.EliteMAPSendRoutingInfoWSService {

    public EliteMAPSendRoutingInfoWSServiceLocator() {
    }


    public EliteMAPSendRoutingInfoWSServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EliteMAPSendRoutingInfoWSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for EliteMAPSendRoutingInfoWS
    private java.lang.String EliteMAPSendRoutingInfoWS_address = "http://dhavalj:58080/eliteradius/services/EliteMAPSendRoutingInfoWS";

    public java.lang.String getEliteMAPSendRoutingInfoWSAddress() {
        return EliteMAPSendRoutingInfoWS_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EliteMAPSendRoutingInfoWSWSDDServiceName = "EliteMAPSendRoutingInfoWS";

    public java.lang.String getEliteMAPSendRoutingInfoWSWSDDServiceName() {
        return EliteMAPSendRoutingInfoWSWSDDServiceName;
    }

    public void setEliteMAPSendRoutingInfoWSWSDDServiceName(java.lang.String name) {
        EliteMAPSendRoutingInfoWSWSDDServiceName = name;
    }

    public com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS.EliteMAPSendRoutingInfoWS getEliteMAPSendRoutingInfoWS() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EliteMAPSendRoutingInfoWS_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEliteMAPSendRoutingInfoWS(endpoint);
    }

    public com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS.EliteMAPSendRoutingInfoWS getEliteMAPSendRoutingInfoWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS.EliteMAPSendRoutingInfoWSSoapBindingStub _stub = new com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS.EliteMAPSendRoutingInfoWSSoapBindingStub(portAddress, this);
            _stub.setPortName(getEliteMAPSendRoutingInfoWSWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEliteMAPSendRoutingInfoWSEndpointAddress(java.lang.String address) {
        EliteMAPSendRoutingInfoWS_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS.EliteMAPSendRoutingInfoWS.class.isAssignableFrom(serviceEndpointInterface)) {
                com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS.EliteMAPSendRoutingInfoWSSoapBindingStub _stub = new com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS.EliteMAPSendRoutingInfoWSSoapBindingStub(new java.net.URL(EliteMAPSendRoutingInfoWS_address), this);
                _stub.setPortName(getEliteMAPSendRoutingInfoWSWSDDServiceName());
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
        if ("EliteMAPSendRoutingInfoWS".equals(inputPortName)) {
            return getEliteMAPSendRoutingInfoWS();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://eliteMAPSendRoutingInfoWS.ws.service.radius.aaa.elitecore.com", "EliteMAPSendRoutingInfoWSService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://eliteMAPSendRoutingInfoWS.ws.service.radius.aaa.elitecore.com", "EliteMAPSendRoutingInfoWS"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("EliteMAPSendRoutingInfoWS".equals(portName)) {
            setEliteMAPSendRoutingInfoWSEndpointAddress(address);
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
