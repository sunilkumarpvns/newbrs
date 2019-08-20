package com.elitecore.elitesm.ws.exception;


public class SessionMgmtWebServiceException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	
	public SessionMgmtWebServiceException(String message){
		super(message);
	}
	
//	public SessionMgmtWebServiceException(String faultString,String detail){
//		this(getFaultCodeObject(), faultString, "NO ACTOR", getDetail(detail));
//	}
//	
//	public SessionMgmtWebServiceException(String detail,Throwable throwable){
//		this(getFaultCodeObject(throwable), detail, "NO ACTOR", getDetail(detail));
//	}
//	
//	public SessionMgmtWebServiceException(String faultString,String detail, Throwable  throwable){
//		this(getFaultCodeObject(throwable), faultString, "NO ACTOR", getDetail(detail));
//	}
//	
//	public SessionMgmtWebServiceException(QName faultcode, String faultstring,String faultactor, Detail detail) {
//		super(faultcode, faultstring, faultactor, detail);
//	}
//
//	
//	public static QName getFaultCodeObject(Throwable e){
//		return new javax.xml.namespace.QName("env.Server");
//	}
//
//	public static QName getFaultCodeObject(){
//		return new javax.xml.namespace.QName("env.Server");
//	}
//
//	public static Detail getDetail(String detailString){
//      Detail detail = null;
//      try{
//          detail = SOAPFactory.newInstance().createDetail();
//          //add error message here
//          detail.addTextNode(detailString);
//       }catch(SOAPException ex){
//      	 ex.printStackTrace();
//       }
//       return detail;
//	}
}
