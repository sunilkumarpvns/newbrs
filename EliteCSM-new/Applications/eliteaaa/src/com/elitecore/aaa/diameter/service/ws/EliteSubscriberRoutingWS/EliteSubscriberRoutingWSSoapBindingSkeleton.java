package com.elitecore.aaa.diameter.service.ws.EliteSubscriberRoutingWS;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class EliteSubscriberRoutingWSSoapBindingSkeleton implements EliteSubcriberRoutingWS {
	
	private EliteSubcriberRoutingWS impl;
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
        
        // Building IMSI Operation
        
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
        		new org.apache.axis.description.ParameterDesc(
        				new javax.xml.namespace.QName("", "imsi"), 
        				org.apache.axis.description.ParameterDesc.IN, 
        				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), 
        				java.lang.String.class, false, false),
        		new org.apache.axis.description.ParameterDesc(
        				new javax.xml.namespace.QName("", "imsiTableName"), 
        				org.apache.axis.description.ParameterDesc.IN, 
        				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), 
        				java.lang.String.class, false, false)
        	};
        _oper = new org.apache.axis.description.OperationDesc("getPeerByIMSI", _params, 
        		new javax.xml.namespace.QName("", "getPeerByIMSI"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://eliteSubscriberRoutingWS.ws.service.diameter.aaa.elitecore.com", "getPeerByIMSI"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getPeerByIMSI") == null) {
            _myOperations.put("getPeerByIMSI", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getPeerByIMSI")).add(_oper);
        
        // Building MSISDN Operation
        
        _params = new org.apache.axis.description.ParameterDesc [] {
        		new org.apache.axis.description.ParameterDesc(
        				new javax.xml.namespace.QName("", "msisdn"), 
        				org.apache.axis.description.ParameterDesc.IN, 
        				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), 
        				java.lang.String.class, false, false),
        		new org.apache.axis.description.ParameterDesc(
        				new javax.xml.namespace.QName("", "msisdnTableName"), 
        				org.apache.axis.description.ParameterDesc.IN, 
        				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), 
        				java.lang.String.class, false, false)
        	};
        
        _oper = new org.apache.axis.description.OperationDesc("getPeerByMSISDN", 
        		_params, new javax.xml.namespace.QName("", "getPeerByMSISDN"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://eliteDiameterWS.ws.service.diameter.aaa.elitecore.com", "getPeerByMSISDN"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getPeerByMSISDN") == null) {
            _myOperations.put("getPeerByMSISDN", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getPeerByMSISDN")).add(_oper);
    }

    public EliteSubscriberRoutingWSSoapBindingSkeleton() {
        this.impl = new EliteSubscriberRoutingWSSoapBindingImpl();
    }

	@Override
	public Map<String, String> getPeerByIMSI(String imsi, String imsiTableName)
			throws RemoteException {
		return impl.getPeerByIMSI(imsi, imsiTableName);
	}

	@Override
	public Map<String, String> getPeerByMSISDN(String msisdn,
			String msisdnTableName) throws RemoteException {
		return impl.getPeerByMSISDN(msisdn, msisdnTableName);
	}

}
