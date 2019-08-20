package com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS;

public interface EliteDynAuthWSService extends javax.xml.rpc.Service {
    public String geteliteRadiusDynAuthWSAddress();

    public EliteDynAuthWS geteliteRadiusDynAuthWS() throws javax.xml.rpc.ServiceException;

    public EliteDynAuthWS geteliteRadiusDynAuthWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
