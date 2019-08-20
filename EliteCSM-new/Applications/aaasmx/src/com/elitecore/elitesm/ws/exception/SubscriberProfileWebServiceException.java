package com.elitecore.elitesm.ws.exception;

import javax.xml.namespace.QName;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

public class SubscriberProfileWebServiceException extends SOAPFaultException {
	
	private static final long serialVersionUID = 1L;
	
	
	public SubscriberProfileWebServiceException(String detail){
		this(getFaultCodeObject(), detail, "NO ACTOR", getDetail(detail));
	}
	
	public SubscriberProfileWebServiceException(String faultString,String detail){
		this(getFaultCodeObject(), faultString, "NO ACTOR", getDetail(detail));
	}
	
	public SubscriberProfileWebServiceException(String detail,Throwable throwable){
		this(getFaultCodeObject(throwable), "Web Service Error", "NO ACTOR", getDetail(detail));
	}
	
	public SubscriberProfileWebServiceException(String faultString,String detail, Throwable  throwable){
		this(getFaultCodeObject(throwable), faultString, "NO ACTOR", getDetail(detail));
	}
	
	public SubscriberProfileWebServiceException(QName faultcode, String faultstring,String faultactor, Detail detail) {
		super(faultcode, faultstring, faultactor, detail);
	}

	
	public static QName getFaultCodeObject(Throwable e){
		return new javax.xml.namespace.QName("env.Server");
	}

	public static QName getFaultCodeObject(){
		return new javax.xml.namespace.QName("env.Server");
	}

	public static Detail getDetail(String detailString){
      Detail detail = null;
      try{
          detail = SOAPFactory.newInstance().createDetail();
          //add error message here
          detail.addTextNode(detailString);
       }catch(SOAPException ex){
      	 ex.printStackTrace();
       }
       return detail;
	}
}
