package com.elitecore.nvsmx.system.filter;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Decoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.StringTokenizer;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * A filter that will filter the requests from web service and perform username/password based authentication on the it.
 * Requests from the user interface will be bypassed.
 */
public class RestAuthenticationFilter implements Filter {
    private static final String AUTHENTICATION_HEADER = "Authorization";
    private static final String MODULE = "REST-AUTH-FILTER";
    private static final String USER_NAME = "username";
    private RestStaffDAO restStaffDAO = new RestStaffDAO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter) throws IOException, ServletException {
        if (request instanceof HttpServletRequest == false) {
            return;
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (httpServletRequest.getSession().getAttribute(Attributes.STAFF_USERNAME) != null) {
            filter.doFilter(request, response);
        } else {
            String authCredentials = httpServletRequest.getHeader(AUTHENTICATION_HEADER);
            // You can implement dependency injection here
            boolean isAuthenticateUser = false;
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
                    getLogger().info(MODULE, "User not Authorized");
                }
                if (response instanceof HttpServletResponse) {
                    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User Not Authorized");
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
        if(SystemParameterDAO.isSSOEnable()){
            return ssoSessionExists(request);
        }

        if (credential == null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "User can't be authenticated. Reason: credentials not found");
            }
            return false;
        }
        // header value format will be "Basic encoded string" for Basic
        // authentication. Example "Basic YWRtaW46YWRtaW4="
        final String encodedUserPassword = credential.replaceFirst("Basic" + " ", "");
        String usernameAndPassword = null;
        try {
            byte[] decodedBytes = new BASE64Decoder().decodeBuffer(encodedUserPassword);
            usernameAndPassword = new String(decodedBytes, "UTF-8");
        } catch (IOException e) {
            getLogger().error(MODULE, "Error while authenticating user.Reason: " + e.getMessage());
            getLogger().trace(e);
        }

        if (Strings.isNullOrBlank(usernameAndPassword)) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "User can't be authenticated. Reason: credentials not found");
            }
            return false;
        }
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        final String userName = tokenizer.nextToken();
        final String password = tokenizer.nextToken();


        StaffData staffData = restStaffDAO.getStaffData(userName);
        if (staffData == null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "User can't be authenticated. Reason: user doesn't exist with user name :"+userName);
            }
            return false;
        }
        if (staffData.getPassword().equals(PasswordUtility.getEncryptedPassword(password)) == false) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "User can't be authenticated. Reason: invalid password provided");
            }
            return false;
        }
        if (userName.equals(NVSMXCommonConstants.ADMIN)) {
            restStaffDAO.setGroupsForSuperAdmin(request);
        } else {
            restStaffDAO.setGroupsForOtherUser(request, staffData);
        }
        setStaffInfoInSession(request, staffData);
        return true;
    }

    private boolean ssoSessionExists(HttpServletRequest request) {

        SSOAuthenticator ssoAuthenticator = new SSOAuthenticator(request);
        if (ssoAuthenticator.authenticate() == false) {
            return false;
        }
        String userName = request.getHeader(USER_NAME);
        if (StringUtils.isBlank(userName)) {
            userName = ssoAuthenticator.getSsoSession().getToken().getPreferredUsername();
        }
        if (StringUtils.isBlank(userName)) {
            getLogger().error(MODULE, "User can't be authenticated. Reason: user name not found");
        }
        StaffData staffData = restStaffDAO.getStaffData(userName);
        if (Objects.isNull(staffData)) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Staff doesn't exist in with username: "+userName+" .So creating new staff");
            }
            //Prepare & save staff
            staffData = restStaffDAO.prepareAndSaveStaffData(ssoAuthenticator.getSsoSession(), userName);
            if(Objects.isNull(staffData)){
                getLogger().debug(MODULE, "User can't be authenticated");
                return false;
            }
           restStaffDAO.setGroupsForOtherUser(request, staffData);
        } else {
            if (userName.equals(NVSMXCommonConstants.ADMIN)) {
                restStaffDAO.setGroupsForSuperAdmin(request);
            } else {
                restStaffDAO.setGroupsForOtherUser(request, staffData);
            }
        }
        setStaffInfoInSession(request, staffData);
        return true;
    }





    private void setStaffInfoInSession(HttpServletRequest request,StaffData staffData) {
        request.getSession().setAttribute(Attributes.STAFF_USERNAME, staffData.getUserName());
        request.getSession().setAttribute(Attributes.STAFF_NAME, staffData.getName());
        request.getSession().setAttribute(Attributes.STAFF_DATA, staffData);
        request.getSession().setAttribute(Attributes.STAFF_ID, staffData.getId());
    }





    public String findExtension(String url) {
        int dotPos = url.lastIndexOf('.');
        int slashPos = url.lastIndexOf('/');
        if (dotPos > slashPos && dotPos > -1) {
            return url.substring(dotPos + 1);
        }
        return "";
    }

}
