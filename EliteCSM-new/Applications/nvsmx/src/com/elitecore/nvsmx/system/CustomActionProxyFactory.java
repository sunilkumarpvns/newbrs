package com.elitecore.nvsmx.system;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.DefaultActionProxyFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.apache.struts2.StrutsConstants.PREFIX_BASED_MAPPER_CONFIGURATION;

public class CustomActionProxyFactory extends DefaultActionProxyFactory {

    private static final String MODULE = "CUST-ACT-PROXY-FCTRY";
    private Map<String, ActionProxyFactory> actionProxyFactories = new HashMap<>();


    @Override
    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }


    @Inject(PREFIX_BASED_MAPPER_CONFIGURATION)
    public void setPrefixBasedActionProxyFactories(String list) {
        if (list != null) {
            String[] factories = CommonConstants.COMMA_SPLITTER.splitToArray(list);
            for (String factory : factories) {
                String[] thisFactory = factory.split(":");
                if ((thisFactory != null) && (thisFactory.length == 2)) {
                    String factoryPrefix = thisFactory[0].trim();
                    String factoryName = thisFactory[1].trim();
                    ActionProxyFactory obj = container.getInstance(ActionProxyFactory.class, factoryName);
                    if (obj != null) {
                        actionProxyFactories.put(factoryPrefix, obj);
                    } else if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Invalid PrefixBasedActionProxyFactory config entry: " + factory);
                    }
                }
            }
        }

    }

    @Override
    public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {
        String uri = namespace + (namespace.endsWith("/") ? actionName : "/" + actionName);

        for (int lastIndex = uri.lastIndexOf('/'); lastIndex > (-1); lastIndex = uri.lastIndexOf('/', lastIndex - 1)) {
            String key = uri.substring(0, lastIndex);
            ActionProxyFactory actionProxyFactory = actionProxyFactories.get(key);
            if (actionProxyFactory != null) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, " Using ActionProxyFactory "+ actionProxyFactory +" for Prefix: " + key);
                }
                return actionProxyFactory.createActionProxy(namespace, actionName, methodName, extraContext, executeResult, cleanupContext);
            }
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "No ActionProxyFactory defined for: " + key);
            }

        }
        if (getLogger().isWarnLogLevel()) {
            getLogger().warn(MODULE, "Cannot find any matching ActionProxyFactory, falling back to: " + super.getClass().getName());
        }
        return super.createActionProxy(namespace, actionName, methodName, extraContext, executeResult, cleanupContext);
    }
}