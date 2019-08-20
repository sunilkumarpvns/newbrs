package com.elitecore.aaa.diameter.service.ws.EliteGenericDiameterWS;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class EliteGenericDiameterWSSoapBindingSkeleton implements EliteGenericDiameterWS{
	
	

	
	private EliteGenericDiameterWS impl;
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in3"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in4"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in5"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("requestGenericDiameterRequest", _params, new javax.xml.namespace.QName("", "requestGenericDiameterRequest"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://eliteGenericDiameterWS.ws.service.diameter.aaa.elitecore.com", "requestGenericDiameterRequest"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("requestGenericDiameterRequest") == null) {
            _myOperations.put("requestGenericDiameterRequest", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("requestGenericDiameterRequest")).add(_oper);
    }

	@Override
	public Map<String, String> wsDiameterGenericRequest(int commandCode, int applicationId, int hopByHopId, int endToEndId, byte flagByte ,Map in1) throws RemoteException {
		return impl.wsDiameterGenericRequest(commandCode,  applicationId, hopByHopId, endToEndId, flagByte ,in1);
	}
	
    public EliteGenericDiameterWSSoapBindingSkeleton() {
        this.impl = new EliteGenericDiameterWSSoapBindingImpl();
    }

    public EliteGenericDiameterWSSoapBindingSkeleton(EliteGenericDiameterWS impl) {
        this.impl = impl;
    }

}
