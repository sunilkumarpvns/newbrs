package com.elitecore.nvsmx.staff;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.spr.LDAPConnectionProvider;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.LDAPUtility;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPSearchResults;
import netscape.ldap.LDAPv2;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Communicator to query in LDAP
 * @author dhyani.raval
 */
public class LDAPConnector extends ESCommunicatorImpl {

    private static final String MODULE = "LDAP-CONNECTOR";
    private final LDAPConnectionProvider connectionProvider;
    private List<String> attributes = Collectionz.newArrayList();
    private String userPrefix = null;
    private List<String> searchBaseDNs = Collectionz.newArrayList();
    private String name;
    private int statusCheckDuration = 10;//TODO Need to verify default value
    private LDAPConnection ldapConnection = null;

    public LDAPConnector(@Nullable TaskScheduler taskScheduler, String name, int statusCheckDuration,LDAPConnectionProvider connectionProvider, String userPrefix, List<String> searchBaseDNs, List<String> attributes) {
        super(taskScheduler);
        this.connectionProvider = connectionProvider;
        this.userPrefix = userPrefix;
        this.searchBaseDNs = searchBaseDNs;
        this.attributes = attributes;
        this.statusCheckDuration = statusCheckDuration;
        this.name = name;
    }


    /**
     * This method returns the filter according to RFC2254 for LDAP
     * @param strSearchString
     * @return String
     */
    private static String getRFC2254LDAPFilter(String strSearchString) {

        StringBuilder  buffer = new StringBuilder(strSearchString.length() + 16);
        for (int cnt = 0; cnt < strSearchString.length(); cnt++) {
            char ch = strSearchString.charAt(cnt);
            if (LDAPUtility.isRFC2254LDAPFilter(ch) || (ch >= CommonConstants.SPACE && ch <= CommonConstants.TILDE) == false) {
                buffer.append(CommonConstants.BACKSLASH);
                buffer.append(Integer.toHexString(ch));
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }



    public Map<String, String[]> execute(String userIdentity) throws OperationFailedException, LDAPException {
        if (getLogger().isLogLevel(LogLevel.INFO)) {
            getLogger().info(MODULE, "Trying to get data for user identity: " + userIdentity);
        }

        LDAPSearchResults ldapSearchResults;

        try {
            ldapConnection = connectionProvider.getConnection();

            if (ldapConnection == null) {

                throw new OperationFailedException("LDAP connection is not available", ResultCode.SERVICE_UNAVAILABLE);
            }

            String strSearchString = "(" + userPrefix + getRFC2254LDAPFilter(userIdentity) + ")";
            List<String> ldapEntryAttrs = attributes;
            String[] strLdapEntryAttributes = ldapEntryAttrs.toArray(new String[ldapEntryAttrs.size()]);

            for (String searchBaseDN : searchBaseDNs) {
                long queryExecutionTime = System.currentTimeMillis();
                ldapSearchResults = ldapConnection.search(searchBaseDN, LDAPv2.SCOPE_SUB, strSearchString, strLdapEntryAttributes, false);
                queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;

                if (queryExecutionTime > AlertConstants.LDAP_HIGH_QUERY_RESPONSE_TIME_MS) {
                    if (getLogger().isLogLevel(LogLevel.WARN)) {
                        getLogger().warn(MODULE, "LDAP query execution time getting high, - Last query execution time = " + queryExecutionTime + " ms.");
                    }
                }
                if (ldapSearchResults != null && ldapSearchResults.hasMoreElements()) {
                    return LDAPQueryResult.createAttributes(ldapSearchResults);
                }

            }

        } finally {
            connectionProvider.close(ldapConnection);
        }
        return null;
    }


    @Override
    protected int getStatusCheckDuration() {
        return statusCheckDuration;
    }

    @Override
    public void scan() {

        if (ldapConnection == null) {
            try {
                ldapConnection = connectionProvider.getConnection();
            } catch (LDAPException e) {
                getLogger().error(MODULE,"Error while connection to LDAP. Reason : " +e.getMessage());
                getLogger().error(MODULE,"Error while connection to LDAP. Reason : " +e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTypeName() {
        return "LDAP CONNECTOR";
    }

    public List<String> getAttributes() {
        return attributes;
    }

}
