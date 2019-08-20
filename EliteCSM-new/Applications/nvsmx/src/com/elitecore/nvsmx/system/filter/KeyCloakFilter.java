package com.elitecore.nvsmx.system.filter;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.servlet.KeycloakOIDCFilter;

import javax.servlet.*;
import java.io.IOException;


public class KeyCloakFilter extends KeycloakOIDCFilter {

    private static final String MODULE = "KEY-CLOAL-FLTR";
    FilterConfig filterConfig = null;

    public KeyCloakFilter() {
        super();
    }

    public KeyCloakFilter(KeycloakConfigResolver definedconfigResolver) {
        super(definedconfigResolver);
        this.filterConfig = filterConfig;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.filterConfig = filterConfig;
    }
    @Override
    public void destroy() {
        this.filterConfig = null;
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (SystemParameterDAO.isSSOEnable()) {
            super.doFilter(request, response, chain);
            return;
        }
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(MODULE, "SSO is disabled. So skipping key cloak filter");
        }
        chain.doFilter(request, response);
    }


}
