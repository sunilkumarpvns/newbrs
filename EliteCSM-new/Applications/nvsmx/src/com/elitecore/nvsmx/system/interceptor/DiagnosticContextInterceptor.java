package com.elitecore.nvsmx.system.interceptor;

import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import org.apache.logging.log4j.ThreadContext;
import org.apache.struts2.StrutsStatics;

import javax.servlet.http.HttpServletRequest;

import static com.elitecore.commons.base.Strings.isNullOrBlank;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.apache.logging.log4j.ThreadContext.put;

/**
 * @author Prakash Pala
 * @since 27-Dec-2018
 * This interceptor is used for adding basic MDC (Mapped Diagnostic Context) information
 * for GUI and Sturts 2 call flow.
 */
public class DiagnosticContextInterceptor extends BaseInterceptor {
    private static final long serialVersionUID = 1L;
    private static final String MODULE = "GUI-MDC-INTRCPTR";
    private static final String USERNAME = "Username";
    private static final String IP_ADDRESS = "IpAddress";
    private static final String MODULE_NAME = "ModuleName";
    private static final String OPERATION = "Operation";

    @Override
    public String doIntercept(ActionInvocation actionInvocation) throws Exception {
        String action = Action.LOGIN;
        try {
            final ActionContext context = actionInvocation.getInvocationContext();
            HttpServletRequest request = (HttpServletRequest) context.get(StrutsStatics.HTTP_REQUEST);
            RequestURIHelper requestURIHelper = new RequestURIHelper(request);

            String username = (String) request.getSession().getAttribute(Attributes.STAFF_USERNAME);
            if(isNullOrBlank(username) == false){
                put(USERNAME, username);
            }

            String ipAddress = request.getRemoteAddr();
            if(isNullOrBlank(ipAddress) == false){
                put(IP_ADDRESS, ipAddress);
            }

            String moduleName = requestURIHelper.getModuleName();
            if(isNullOrBlank(moduleName) == false){
                put(MODULE_NAME, moduleName);
            }

            String operation = requestURIHelper.getMethodName();
            if(isNullOrBlank(operation) == false){
                put(OPERATION, operation);
            }

            action = actionInvocation.invoke();

        } catch(Exception exp){
            getLogger().warn(MODULE, exp.getMessage());
            getLogger().trace(MODULE, exp);
        } finally {
            if(ThreadContext.isEmpty() == false){
                ThreadContext.clearAll();
            }
        }
        return action;
    }

    private static class RequestURIHelper extends RequestURI{
        RequestURIHelper(HttpServletRequest request){
            super(request);
        }
        @Override
        protected boolean isAuthorizedAction(ACLModules module) {
            return false;
        }
    }
}
