package com.elitecore.netvertex.ws.parentalws;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import com.elitecore.netvertexsm.ws.cxfws.ssp.parental.ParentalWS;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.7.1
 * 2014-01-09T22:40:24.614+05:30
 * Generated source version: 2.7.1
 * 
 */
@WebServiceClient(name = "ParentalService", 
                  wsdlLocation = "http://localhost:8081/netvertexsm/services/ParentalService?wsdl",
                  targetNamespace = "http://parentalWS.ws.netvertex.elitecore.com/") 
public class ParentalService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://parentalWS.ws.netvertex.elitecore.com/", "ParentalService");
    public final static QName ParentalWS = new QName("http://parentalWS.ws.netvertex.elitecore.com/", "ParentalWS");
    static {
        URL url = null;
        try {
            url = new URL("http://localhost:8081/netvertexsm/services/ParentalService?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(ParentalService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://localhost:8081/netvertexsm/services/ParentalService?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public ParentalService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public ParentalService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ParentalService() {
        super(WSDL_LOCATION, SERVICE);
    }
    

    /**
     *
     * @return
     *     returns ParentalWS
     */
    @WebEndpoint(name = "ParentalWS")
    public ParentalWS getParentalWS() {
        return super.getPort(ParentalWS, ParentalWS.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ParentalWS
     */
    @WebEndpoint(name = "ParentalWS")
    public ParentalWS getParentalWS(WebServiceFeature... features) {
        return super.getPort(ParentalWS, ParentalWS.class, features);
    }

}
