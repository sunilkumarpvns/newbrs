package com.elitecore.nvsmx.system.interceptor;

import com.elitecore.commons.kpi.util.ReflectionUtil;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.nvsmx.policydesigner.controller.util.EliteGenericCTRL;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.constants.Results;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author aditya.shrivastava
 */
public class VerifyValidConfigurationInterceptor extends MethodFilterInterceptor {

    private static final long serialVersionUID = 1L;
    private static final String MODULE = "VALID-CONFIG-INTRCPTR";
    public static final String SYSTEM_PARAMETER = "system-parameter";

    @Override
    protected String doIntercept(ActionInvocation actionInvocation) throws Exception {

        if (SystemParameterDAO.isMandatorySystemParamaterConfigured()) {
            return actionInvocation.invoke();
        }

        if (LogManager.getLogger().isInfoLogLevel()) {
            LogManager.getLogger().info(MODULE, "Redirecting to system parameter configuration. Reason: Mandatory system parameter(country,operator & currency) are not configured");
        }
        Object object = actionInvocation.getAction();
        if (object instanceof EliteGenericCTRL) {
            List<Method> setActionChainUrlMethodList = ReflectionUtil.getDeclaredMethodsAnnotatedWith(object.getClass(), ActionChain.class);
            for (Method method : setActionChainUrlMethodList) {
                method.invoke(object, "/sm/systemparameter/system-parameter/*/edit");
                return Results.REDIRECT_ACTION.getValue();
            }
        } else if (object instanceof RestGenericCTRL) {
            String actionName = actionInvocation.getProxy().getActionName();
            if (actionName.equalsIgnoreCase(SYSTEM_PARAMETER)) {
                return actionInvocation.invoke();
            }
            ((RestGenericCTRL) object).setActionChainUrl("/sm/systemparameter/system-parameter/*/edit");
            return Results.REDIRECT_ACTION.getValue();
        }
        return actionInvocation.invoke();
    }


}
