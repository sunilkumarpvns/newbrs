package com.elitecore.nvsmx.staff;

import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.spr.LDAPConnectionProvider;
import com.elitecore.corenetvertex.spr.exceptions.InitializationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * An interface which will communicate with LDAP to fetch the profile anf build the Staff Profile of LDAP
 * @author dhyani.raval
 */
public class StaffLDAPInterface {

    private static final String MODULE = "LDAP-STAFF-INTERFACE";
    private static final int STATUS_CHECK_DURATION_DEAULT_VALUE = 10;
    private StaffLDAPConfiguration staffLDAPConfiguration;
    private LDAPConnectionProvider connectionProvider;
    private LDAPConnector ldapConnector;

    public StaffLDAPInterface(StaffLDAPConfiguration staffLDAPConfiguration, LDAPConnectionProvider connectionProvider) {

        this.connectionProvider = connectionProvider;
        this.staffLDAPConfiguration = staffLDAPConfiguration;
    }


    public void init(TaskScheduler taskScheduler, String name) throws InitializationFailedException {

        ldapConnector = new LDAPConnector(taskScheduler,name, STATUS_CHECK_DURATION_DEAULT_VALUE, connectionProvider, staffLDAPConfiguration.getUserPrefix(), staffLDAPConfiguration.getSearchBaseDnList(), staffLDAPConfiguration.getStaffLDAPProfileFieldMapping().getStaffAttributeList());
        List<String> staffAttributeList = staffLDAPConfiguration.getStaffLDAPProfileFieldMapping().getStaffAttributeList();
        if (staffAttributeList.isEmpty()) {
            throw new InitializationFailedException("Missing staff field mapping in LDAP Authentication");
        }
    }

    public StaffLDAPProfile getProfile(String userIdentity) throws OperationFailedException {
        if (getLogger().isLogLevel(LogLevel.INFO)) {
            getLogger().info(MODULE, "Trying to get account data for user identity: " + userIdentity);
        }
        long totalQueryExecutionTime = System.currentTimeMillis();
        try {

            Map<String, String[]> ldapResults = ldapConnector.execute(userIdentity);
            totalQueryExecutionTime = System.currentTimeMillis() - totalQueryExecutionTime;
            getLogger().info(MODULE, "Staff load time is: " + totalQueryExecutionTime);
            return createProfileData(ldapResults);

        } catch (Exception e) {
            getLogger().error(MODULE,"Error while fetching profile from LDAP. Reason: "+e.getMessage());
            getLogger().trace(MODULE,e);
        }

        return null;
    }

    private StaffLDAPProfile createProfileData(Map<String, String[]> ldapResults) throws Exception {

        StaffLDAPProfileFieldMapping staffLDAPProfileFieldMapping = staffLDAPConfiguration.getStaffLDAPProfileFieldMapping();
        StaffLDAPProfile staffLDAPProfile = new StaffLDAPProfile();

        for (StaffFields staffFields : StaffFields.values()) {
            String[] values = ldapResults.get(staffLDAPProfileFieldMapping.getFieldMappingForKey(staffFields));

            if (values == null) {
                continue;
            }

            if (Arrayz.isNullOrEmpty(values)) {
                return null;
            }

            try {
                staffFields.setStringValue(staffLDAPProfile, values[0]);
            } catch (Exception e) {
                getLogger().error(MODULE, "Invalid value: " + values[0] + " for field: " + staffFields.getColumnName());
                getLogger().trace(MODULE, e);
            }
        }
        return staffLDAPProfile;
    }

}
