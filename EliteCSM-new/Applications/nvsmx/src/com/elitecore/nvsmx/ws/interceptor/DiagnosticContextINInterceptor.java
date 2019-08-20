package com.elitecore.nvsmx.ws.interceptor;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static com.elitecore.commons.base.Strings.isNullOrBlank;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.apache.logging.log4j.ThreadContext.put;

/**
 * @author Prakash Pala
 * @since 27-Dec-2018
 * This interceptor is used for cxf REST web services for adding basic MDC (Mapped Diagnostic Context) information.
 */
public class DiagnosticContextINInterceptor extends AbstractPhaseInterceptor{
    private static final String MODULE = "MDC-INFO-INTERCEPTOR-IN";
    private static final String IP_ADDRESS = "IpAddress";
    private static final String OPERATION = "Operation";

    public DiagnosticContextINInterceptor() {
        super(Phase.USER_LOGICAL);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        try{
            String ipAddress = fetchRemoteIpAddress(message);
            if(isNullOrBlank(ipAddress) == false){
                put(IP_ADDRESS, ipAddress);
            }

            String operationName = (String) message.get("path_to_match_slash");
            if(isNullOrBlank(operationName) == false){
                put(OPERATION, operationName.replace("/", ""));
            }

        } catch (Exception exp){
            getLogger().warn(MODULE, exp.getMessage());
            getLogger().trace(MODULE, exp);
        }
    }


    private String fetchRemoteIpAddress(Message message){
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        return (Objects.nonNull(request) && Objects.nonNull(request.getRemoteAddr()))
                ? request.getRemoteAddr()
                : null;
    }
}
