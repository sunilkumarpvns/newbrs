package com.elitecore.elitesm.util.constants;

import com.elitecore.passwordutil.PasswordEncryption;

public class ServermgrConstant extends BaseConstant {

    public static final String MODULE_NAME               = "Servermgr";

    public static final int    SERVER_ID_ENCRYPTION_MODE = PasswordEncryption.ELITECRYPT;
    // public static final String TELNET = "Telnet";

    /* Start Log Report Constant */
    public final static String FILE_PATTERN              = "file_pattern";
    public final static String FILE_LOCATION             = "file_location";
    public final static String RECORD_DATE               = "record_date";
    public final static String FILE_NAME                 = "file_name";
    public final static String TRIBANDNUMBER             = "triBandNumber";
    /* End Log Report Constant */


    public static final String DEFAULT_STAFF = "STF0000";
    public static final String FALLBACK_DRIVERNAME = "fallback-driver-name";
    public static final String ADDITIONAL_DRIVER_NAME = "add-driver-name";
    public static final String ADD_PROXY_DRIVER_NAME = "add-proxy-driver-name";
    public static final String SESSION_PLUGIN_NAME = "session-plugin-name";
    public static final String NO_PLUGIN = "NONE";
    public static final String DRIVER_NAME = "driver-name";
    public static final String NO_DRIVER = "NONE";
    
    public static final String DRIVER_INSTANCE_MODE = "N";
    public static final String FALLBACK_INSTANCE_MODE = "F";
    public static final String ADDITIONAL_INSTANCE_MODE = "A";
    public static final String FORMER_INSTANCE_MODE = "O";
    public static final String NORMAL_INSTANCE_MODE = "N";
    
    public static final String DRIVER_INSTANCE_MODE_DISPLAY = "Default";
    public static final String FALLBACK_INSTANCE_MODE_DISPLAY = "Fallback";
    public static final String ADDITIONAL_INSTANCE_MODE_DISPLAY = "Additional";
    public static final String FORMER_INSTANCE_MODE_DISPLAY = "Unidentified";

	public static final String RAD_AUTH ="RAD_AUTH";
	public static final String RAD_ACCT ="RAD_ACCT";
	


}
