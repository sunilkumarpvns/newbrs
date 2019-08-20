package com.elitecore.nvsmx.ws.interceptor;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author Prakashkumar Pala
 * @since 22-Oct-2018
 * This is base class which can be extended by all exposed Web Service classes.
 * This Singleton class can give common information like current IP address of method.
 */
public class WebServiceAuditInterceptor implements WebServiceInterceptor{

    private static final WebServiceAuditInterceptor webServiceAuditInterceptor = new WebServiceAuditInterceptor();

    private WebServiceAuditInterceptor(){
        //NOSONAR - private constructor for singleton
    }

    public static WebServiceAuditInterceptor getInstance(){
        return webServiceAuditInterceptor;
    }

    /**
     * @author Prakashkumar Pala
     * @since 22-Oct-2018
     * @return String ipAddress of current request
     * This method will return current IP Address of the request context.
     * It can be used to fetch caller client IP address while calling any web service.
     */
    private String fetchIpAddressOfRequest(){
        Message message = PhaseInterceptorChain.getCurrentMessage();
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        return (Objects.nonNull(request) && Objects.nonNull(request.getRemoteAddr()))
                ? request.getRemoteAddr()
                : null;
    }

    @Override
    public void requestReceived(WebServiceRequest request){
        request.setRequestIpAddress(fetchIpAddressOfRequest());
    }

    @Override
    public void responseReceived(WebServiceResponse request){
        //NOSONAR - No need to implement.
    }
}
