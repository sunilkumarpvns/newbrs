package com.elitecore.aaa.radius.service.ws.eliteGenericRadiusWS;


public interface EliteGenericRadiusService extends javax.xml.rpc.Service {
	
    public String getEliteGenericRadiusWSAddress();

    public EliteGenericRadiusWS getEliteGenericRadiusWS() throws javax.xml.rpc.ServiceException;

    public EliteGenericRadiusWS getEliteGenericRadiusWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
