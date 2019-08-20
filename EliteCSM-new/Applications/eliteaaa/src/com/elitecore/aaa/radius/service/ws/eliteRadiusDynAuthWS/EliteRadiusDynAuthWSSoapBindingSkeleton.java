package com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS;

import java.util.Map;

public class EliteRadiusDynAuthWSSoapBindingSkeleton implements EliteDynAuthWS{

	private com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS.EliteDynAuthWS impl;
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
				new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
				new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false), 
		};
		_oper = new org.apache.axis.description.OperationDesc("requestCOA", _params, new javax.xml.namespace.QName("", "requestCOAReturn"));
		_oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		_oper.setElementQName(new javax.xml.namespace.QName("http://eliteRadiusDynAuthWS.ws.service.radius.aaa.elitecore.com", "requestCOA"));
		_oper.setSoapAction("");
		_myOperationsList.add(_oper);
		if (_myOperations.get("requestCOA") == null) {
			_myOperations.put("requestCOA", new java.util.ArrayList());
		}
		((java.util.List)_myOperations.get("requestCOA")).add(_oper);

		_params = new org.apache.axis.description.ParameterDesc [] {
				new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "strUserName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
				new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "attrMap"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false), 
		};
		_oper = new org.apache.axis.description.OperationDesc("requestCOAExt", _params, new javax.xml.namespace.QName("", "requestCOAExtReturn"));
		_oper.setReturnType(new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"));
		_oper.setElementQName(new javax.xml.namespace.QName("http://eliteRadiusDynAuthWS.ws.service.radius.aaa.elitecore.com", "requestCOAExt"));
		_oper.setSoapAction("");
		_myOperationsList.add(_oper);
		if (_myOperations.get("requestCOAExt") == null) {
			_myOperations.put("requestCOAExt", new java.util.ArrayList());
		}
		((java.util.List)_myOperations.get("requestCOAExt")).add(_oper);
		
		_params = new org.apache.axis.description.ParameterDesc [] {
				new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
				new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false), 
		};
		_oper = new org.apache.axis.description.OperationDesc("requestDisconnect", _params, new javax.xml.namespace.QName("", "requestDisconnectReturn"));
		_oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		_oper.setElementQName(new javax.xml.namespace.QName("http://eliteRadiusDynAuthWS.ws.service.radius.aaa.elitecore.com", "requestDisconnect"));
		_oper.setSoapAction("");
		_myOperationsList.add(_oper);
		if (_myOperations.get("requestDisconnect") == null) {
			_myOperations.put("requestDisconnect", new java.util.ArrayList());
		}
		((java.util.List)_myOperations.get("requestDisconnect")).add(_oper);

		_params = new org.apache.axis.description.ParameterDesc [] {
				new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "strUserName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
				new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "attrMap"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false), 
		};
		_oper = new org.apache.axis.description.OperationDesc("requestDisconnectExt", _params, new javax.xml.namespace.QName("", "requestDisconnectExtReturn"));
		_oper.setReturnType(new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"));
		_oper.setElementQName(new javax.xml.namespace.QName("http://eliteRadiusDynAuthWS.ws.service.radius.aaa.elitecore.com", "requestDisconnectExt"));
		_oper.setSoapAction("");
		_myOperationsList.add(_oper);
		if (_myOperations.get("requestDisconnectExt") == null) {
			_myOperations.put("requestDisconnectExt", new java.util.ArrayList());
		}
		((java.util.List)_myOperations.get("requestDisconnectExt")).add(_oper);
		        		
		_params = new org.apache.axis.description.ParameterDesc [] {
				new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
				new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false), 
		};
		_oper = new org.apache.axis.description.OperationDesc("requestHotline", _params, new javax.xml.namespace.QName("", "requestHotlineReturn"));
		_oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		_oper.setElementQName(new javax.xml.namespace.QName("http://eliteRadiusDynAuthWS.ws.service.radius.aaa.elitecore.com", "requestHotline"));
		_oper.setSoapAction("");
		_myOperationsList.add(_oper);
		if (_myOperations.get("requestHotline") == null) {
			_myOperations.put("requestHotline", new java.util.ArrayList());
		}
		((java.util.List)_myOperations.get("requestHotline")).add(_oper);

		_params = new org.apache.axis.description.ParameterDesc [] {
				new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
				new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
				new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false), 
		};
		_oper = new org.apache.axis.description.OperationDesc("wimaxDynAuthRequest", _params, new javax.xml.namespace.QName("", "wimaxDynAuthRequestReturn"));
		_oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		_oper.setElementQName(new javax.xml.namespace.QName("http://eliteRadiusDynAuthWS.ws.service.radius.aaa.elitecore.com", "wimaxDynAuthRequest"));
		_oper.setSoapAction("");
		_myOperationsList.add(_oper);
		if (_myOperations.get("wimaxDynAuthRequest") == null) {
			_myOperations.put("wimaxDynAuthRequest", new java.util.ArrayList());
		}
		((java.util.List)_myOperations.get("wimaxDynAuthRequest")).add(_oper);
	}

	public EliteRadiusDynAuthWSSoapBindingSkeleton() {
		this.impl = new EliteRadiusDynAuthWSSoapBindingImpl();
	}

	public EliteRadiusDynAuthWSSoapBindingSkeleton(EliteDynAuthWS impl) {
		this.impl = impl;
	}
	public int requestCOA(java.lang.String in0, java.util.Map in1) throws java.rmi.RemoteException
	{
		return impl.requestCOA(in0, in1);
	}

	public int requestDisconnect(java.lang.String in0, Map in1) throws java.rmi.RemoteException
	{
		return impl.requestDisconnect(in0, in1);
	}
	public Map<String, String[]> requestCOAExt(String strUserName, Map<String, String[]> attrMap)  throws java.rmi.RemoteException{
		return impl.requestCOAExt(strUserName,attrMap);
	}

	public Map<String,String[]> requestDisconnectExt(String strUserName, Map<String, String[]> attrMap) throws java.rmi.RemoteException{
		return impl.requestDisconnectExt(strUserName,attrMap);
	}
	/*
	@Override
	public int requestHotline(String in0, Map in1)
			throws RemoteException {
		return impl.requestHotline(in0, in1);
	}

	@Override
	public int wimaxDynAuthRequest(int in0, String in1, Map in2)
			throws RemoteException {
		return impl.wimaxDynAuthRequest(in0, in1, in2);
	}
	 */

}
