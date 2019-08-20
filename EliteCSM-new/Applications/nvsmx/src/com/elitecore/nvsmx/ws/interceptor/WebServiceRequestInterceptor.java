package com.elitecore.nvsmx.ws.interceptor;

import static com.elitecore.commons.logging.LogManager.getLogger;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
 
public class WebServiceRequestInterceptor extends AbstractSoapInterceptor {
 
    private static final String MODULE = "WS-REQUEST-INTERCEPTOR";

	public WebServiceRequestInterceptor() {
        super(Phase.USER_LOGICAL);
    }
 
    public void handleMessage(SoapMessage message) throws Fault {
    	if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "called handleMessage()");
		}
    	 
	   	javax.xml.namespace.QName qName = (QName)message.get("javax.xml.ws.wsdl.port");
	   	
	   	if(qName != null){
	   		
		   	String webServiveName = qName.getLocalPart();
		   	if(webServiveName!=null) {
		   		
		   		WebServiceModule module = WebServiceModule.fromName(webServiveName);
		   		if(module != null ){
		   			
				   	boolean hasReachedFlag =  module.hasReachedTPSLimit();
					if ( hasReachedFlag == true){
						if (getLogger().isDebugLogLevel()) {
							getLogger().debug(MODULE, "TPS limit Reached. Call not Allowed for '"+webServiveName+"'");
						}
						throw new Fault(new Exception("TPS limit Reached. Call not Allowed for '"+webServiveName+"'"));
					} 
					
		   		}else{
		   			if (getLogger().isInfoLogLevel()) {
						getLogger().debug(MODULE, "No Web-Service module matched");
					}
		   		}
		   	}else{
		   		if (getLogger().isInfoLogLevel()) {
					getLogger().debug(MODULE, "No Local Part found for given call");
				}	
		   	}
	   	}else{
	   		if (getLogger().isInfoLogLevel()) {
				getLogger().debug(MODULE, "No Qualified Name found for given Call");
			}	
	   	}
    }
 
}