package com.elitecore.netvertex.ws.subscriberprovisioningws;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProvisioningWS;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.7.1
 * 2014-01-09T15:00:11.049+05:30
 * Generated source version: 2.7.1
 * 
 */
@WebServiceClient(name = "SubscriberProvisioningService", 
                  wsdlLocation = "http://localhost:8080/netvertexsm/services/SubscriberProvisioningService?wsdl",
                  targetNamespace = "http://subscriberProvisioningWS.ws.netvertex.elitecore.com/") 
public class SubscriberProvisioningService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://subscriberProvisioningWS.ws.netvertex.elitecore.com/", "SubscriberProvisioningService");
    public final static QName SubscriberProvisioningWS = new QName("http://subscriberProvisioningWS.ws.netvertex.elitecore.com/", "SubscriberProvisioningWS");
    static {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/netvertexsm/services/SubscriberProvisioningService?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(SubscriberProvisioningService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://localhost:8080/netvertexsm/services/SubscriberProvisioningService?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public SubscriberProvisioningService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public SubscriberProvisioningService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SubscriberProvisioningService() {
        super(WSDL_LOCATION, SERVICE);
    }
    

    /**
     *
     * @return
     *     returns SubscriberProvisioningWS
     */
    @WebEndpoint(name = "SubscriberProvisioningWS")
    public SubscriberProvisioningWS getSubscriberProvisioningWS() {
        return super.getPort(SubscriberProvisioningWS, SubscriberProvisioningWS.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SubscriberProvisioningWS
     */
    @WebEndpoint(name = "SubscriberProvisioningWS")
    public SubscriberProvisioningWS getSubscriberProvisioningWS(WebServiceFeature... features) {
        return super.getPort(SubscriberProvisioningWS, SubscriberProvisioningWS.class, features);
    }

}