package com.elitecore.nvsmx.system.filter;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.commons.base.Strings;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;

import sun.misc.BASE64Decoder;

public class IntegrationAuthenticationFilter implements Filter {
    public static final String AUTHENTICATION_HEADER = "Authorization";
    private static final String MODULE = "INTEGRATION-AUTH-FILTER";
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filter) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            boolean isAuthenticateUser = false;
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String authCredentials = httpServletRequest.getHeader(AUTHENTICATION_HEADER);
            try {
                isAuthenticateUser = authenticate((HttpServletRequest) request, authCredentials);
            } catch (NoSuchEncryptionException | EncryptionFailedException e) {
                getLogger().error(MODULE, "Unable to authenticate user.Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);

            }
            if (isAuthenticateUser) {
                filter.doFilter(request, response);
            } else {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "User not Authorized to access integration services");
                }
                if (response instanceof HttpServletResponse) {
                    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                    httpServletResponse
                            .setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not Authorized to access integration services");
                }
            }
        }
    }

    @Override
    public void destroy() {
        //No need to implement this
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        //No need to implement this
    }

    private boolean authenticate(HttpServletRequest request, String credential) throws NoSuchEncryptionException, EncryptionFailedException {
        if (credential == null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "User can't be authenticated for integration services. Reason: credentials not found");
            }
            return false;
        }
        final String encodedUserPassword = credential.replaceFirst("Basic" + " ", "");
        String usernameAndPassword = null;
        try {
            byte[] decodedBytes = new BASE64Decoder().decodeBuffer(encodedUserPassword);
            usernameAndPassword = new String(decodedBytes, "UTF-8");
        } catch (IOException e) {
            getLogger().error(MODULE, "Error while authenticating user for integration services.Reason: " + e.getMessage());
            getLogger().trace(e);
        }

        if(Strings.isNullOrBlank(usernameAndPassword)){
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "User can't be authenticated for integration services. Reason: credentials not found");
            }
            return false;
        }

        if((LicenseConstants.INTEGRATION_USER+":"+LicenseConstants.INTEGRATION_SECRET).equals(usernameAndPassword)){
            return true;
        }

        return false;
    }
}
