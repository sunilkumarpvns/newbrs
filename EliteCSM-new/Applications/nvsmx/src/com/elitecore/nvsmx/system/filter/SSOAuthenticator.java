package com.elitecore.nvsmx.system.filter;

import org.keycloak.KeycloakSecurityContext;

import javax.servlet.ServletRequest;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class SSOAuthenticator {

    private static final String MODULE = "SSO-ATH-CTR";
    private ServletRequest request;
    KeycloakSecurityContext ssoSession;

    public SSOAuthenticator(ServletRequest request) {
        this.request = request;
        this.ssoSession = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
    }

    public boolean authenticate() {
        if (ssoSession == null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Authentication failed. Reason: sso session not found from request");
            }
            return false;

        }
        if (ssoSession.getToken() == null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Authentication failed. Reason: token not found from sso session");
            }
            return false;
        }
        if (ssoSession.getToken().isExpired()) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Authentication failed. Reason: token has expired");
            }
            return false;
        }
        return true;

    }

    public KeycloakSecurityContext getSsoSession() {
        return ssoSession;
    }
}
