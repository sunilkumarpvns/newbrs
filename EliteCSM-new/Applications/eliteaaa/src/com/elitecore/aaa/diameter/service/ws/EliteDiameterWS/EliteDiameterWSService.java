package com.elitecore.aaa.diameter.service.ws.EliteDiameterWS;


public interface EliteDiameterWSService extends javax.xml.rpc.Service{
	public String geteliteDiameterWSAddress();

    public EliteDiameterWS geteliteDiameterWS() throws javax.xml.rpc.ServiceException;

    public EliteDiameterWS geteliteDiameterWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;

}
