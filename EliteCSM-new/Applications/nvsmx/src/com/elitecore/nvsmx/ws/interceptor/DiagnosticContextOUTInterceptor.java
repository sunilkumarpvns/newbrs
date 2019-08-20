package com.elitecore.nvsmx.ws.interceptor;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.logging.log4j.ThreadContext;

/**
 * @author Prakash Pala
 * @since 27-Dec-2018
 * This interceptor is used for cxf REST web services for clearing basic MDC (Mapped Diagnostic Context) information.
 */
public class DiagnosticContextOUTInterceptor extends AbstractPhaseInterceptor {
    public DiagnosticContextOUTInterceptor() {
        super(Phase.USER_LOGICAL);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        if(ThreadContext.isEmpty() == false){
            ThreadContext.clearAll();
        }
    }
}

