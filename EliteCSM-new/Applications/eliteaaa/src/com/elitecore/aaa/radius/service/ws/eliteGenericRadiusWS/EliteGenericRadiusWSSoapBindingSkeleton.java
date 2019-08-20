package com.elitecore.aaa.radius.service.ws.eliteGenericRadiusWS;

import java.rmi.RemoteException;
import java.util.Map;

public class EliteGenericRadiusWSSoapBindingSkeleton implements EliteGenericRadiusWS{

	private com.elitecore.aaa.radius.service.ws.eliteGenericRadiusWS.EliteGenericRadiusWS impl;
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
				new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "packetType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"), java.lang.Integer.class, false, false),
				new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "identifier"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"), java.lang.Integer.class, false, false),
				new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "attrMap"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false), 
		};
		_oper = new org.apache.axis.description.OperationDesc("requestGenericRadiusWS", _params, new javax.xml.namespace.QName("", "requestGenericRadiusWS"));
		_oper.setReturnType(new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"));
		_oper.setElementQName(new javax.xml.namespace.QName("http://EliteGenericRadiusWS.ws.service.radius.aaa.elitecore.com", "requestGenericRadiusWS"));
		_oper.setSoapAction("");
		_myOperationsList.add(_oper);
		if (_myOperations.get("requestGenericRadiusWS") == null) {
			_myOperations.put("requestGenericRadiusWS", new java.util.ArrayList());
		}
		((java.util.List)_myOperations.get("requestGenericRadiusWS")).add(_oper);
	}

	public EliteGenericRadiusWSSoapBindingSkeleton() {
		this.impl = new EliteGenericRadiusWSSoapBindingImpl();
	}

	public EliteGenericRadiusWSSoapBindingSkeleton(EliteGenericRadiusWS impl) {
		this.impl = impl;
	}

	@Override
	public Map<String, String[]> requestGenericRadiusWS(java.lang.Integer packetType,java.lang.Integer identifier, Map attrMap)
			throws RemoteException {
		return impl.requestGenericRadiusWS(packetType,identifier, attrMap);
	}

}
