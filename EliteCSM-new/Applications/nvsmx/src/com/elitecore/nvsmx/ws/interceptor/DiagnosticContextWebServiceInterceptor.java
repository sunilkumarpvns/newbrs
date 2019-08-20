package com.elitecore.nvsmx.ws.interceptor;

/**
 * @author Prakash Pala
 * @since 28-Dec-2018
 * DiagnosticContextWebServiceInterceptor is used to resolve to specific WebServiceRequst Type and initiate MDC Information.
 */
public class DiagnosticContextWebServiceInterceptor implements WebServiceInterceptor{

    private static final DiagnosticContextWebServiceInterceptor diagnosticContextWebServiceInterceptor = new DiagnosticContextWebServiceInterceptor();
    private DiagnosticContextWebServiceInterceptor(){
        //NOSONAR - private constructor for singleton
    }
    private DiagnosticContextInjector diagnosticContextInjector = new DiagnosticContextInjector();

    public static DiagnosticContextWebServiceInterceptor getInstance(){ return diagnosticContextWebServiceInterceptor; }

    @Override
    public void requestReceived(WebServiceRequest request) {
        request.visit(diagnosticContextInjector);
    }

    @Override
    public void responseReceived(WebServiceResponse request) {
        //NOSONAR - No need to implement.
    }
}
