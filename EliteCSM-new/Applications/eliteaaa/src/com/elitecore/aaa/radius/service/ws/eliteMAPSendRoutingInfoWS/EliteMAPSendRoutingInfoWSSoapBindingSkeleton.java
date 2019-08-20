/**
 * EliteMAPSendRoutingInfoWSSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS;

public class EliteMAPSendRoutingInfoWSSoapBindingSkeleton implements com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS.EliteMAPSendRoutingInfoWS, org.apache.axis.wsdl.Skeleton {
    private com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS.EliteMAPSendRoutingInfoWS impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "msisdn"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("wsMAPSendRoutingInfo", _params, new javax.xml.namespace.QName("", "wsMAPSendRoutingInfoReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://eliteMAPSendRoutingInfoWS.ws.service.radius.aaa.elitecore.com", "wsMAPSendRoutingInfo"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("wsMAPSendRoutingInfo") == null) {
            _myOperations.put("wsMAPSendRoutingInfo", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("wsMAPSendRoutingInfo")).add(_oper);
    }

    public EliteMAPSendRoutingInfoWSSoapBindingSkeleton() {
        this.impl = new com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS.EliteMAPSendRoutingInfoWSSoapBindingImpl();
    }

    public EliteMAPSendRoutingInfoWSSoapBindingSkeleton(com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS.EliteMAPSendRoutingInfoWS impl) {
        this.impl = impl;
    }
    public java.lang.String wsMAPSendRoutingInfo(java.lang.String msisdn) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.wsMAPSendRoutingInfo(msisdn);
        return ret;
    }

}
