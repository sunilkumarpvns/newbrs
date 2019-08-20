package com.elitecore.aaa.diameter.service.ws.EliteDiameterWS;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class EliteDiameterWSSoapBindingSkeleton implements EliteDiameterWS {
	
	private EliteDiameterWS impl;
    private static Map _myOperations = new Hashtable();
    private static Collection _myOperationsList = new ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static List getOperationDescByName(String methodName) {
        return (List)_myOperations.get(methodName);
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("requestDiameterReAuth", _params, new javax.xml.namespace.QName("", "requestDiameterReAuthReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://eliteDiameterWS.ws.service.diameter.aaa.elitecore.com", "requestDiameterReAuth"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("requestDiameterReAuth") == null) {
            _myOperations.put("requestDiameterReAuth", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("requestDiameterReAuth")).add(_oper);
        
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false), 
         };
        
        _oper = new org.apache.axis.description.OperationDesc("requestDiameterAbortSession", _params, new javax.xml.namespace.QName("", "requestDiameterAbortSession"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://eliteDiameterWS.ws.service.diameter.aaa.elitecore.com", "requestDiameterAbortSession"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("requestDiameterAbortSession") == null) {
            _myOperations.put("requestDiameterAbortSession", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("requestDiameterReAuth")).add(_oper);
    }

    public EliteDiameterWSSoapBindingSkeleton() {
        this.impl = new EliteDiameterWSSoapBindingImpl();
    }

    public EliteDiameterWSSoapBindingSkeleton(EliteDiameterWS impl) {
        this.impl = impl;
    }

	@Override
	public int wsDiameterReAuthRequest(Map in1) throws RemoteException {
	    return impl.wsDiameterReAuthRequest(in1);
	}

	@Override
	public int wsDiameterAbortSessionRequest(Map in1) throws RemoteException {
		return impl.wsDiameterAbortSessionRequest(in1);
	}

}
