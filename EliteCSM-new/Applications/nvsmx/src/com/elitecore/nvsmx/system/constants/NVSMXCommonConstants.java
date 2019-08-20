package com.elitecore.nvsmx.system.constants;

import com.elitecore.corenetvertex.pkg.PkgType;

/**
 * Created by Ishani on 28/6/16.
 */
public class NVSMXCommonConstants {



    public static final String IS_IMPORT_EXPORT_PROCESSING = "isImportExportProcessing";
    public static final String PARAM1 = "PARAM_1";
    public static final String PARAM2 = "PARAM_2";
    public static final String WELCOME_URL = "commons/login/Login/welcome";
    public static final String MANUAL_PASSWORD_PAGE_URL = "commons/login/ChangePassword/redirectToChangePasswordPage";
    public static final String RESET_PASSWORD_PAGE_URL = "commons/login/ChangePassword/initResetPassword";
    public static final String ADMIN = "admin";
    public static final String DATATABLE_PARAMETERS = "beanType,draw,recordsTotal,recordsFiltered";
    public static final String HASH = "#";
    public static final String COMMA = ",";

    public static final int ASYNC_WAIT_TIME_OUT = 3;

    public static final String ENTITY_IMPORT_FAIL = "FAIL";
    public static final String ENTITY_IMPORT_SUCCESS = "SUCCESS";
    public static final String REDIRECT_URL = "redirectURL";
    public static final String GENERIC_PARTNER_RNC_SEARCH = "genericPartnerSearch";
    public static final String PROMOTIONAL_PKG_INIT_METHOD = "promotional/policydesigner/pkg/Pkg/PromotionalPkg/init?type=PROMOTIONAL";
    public static final String TOTAL = "<span style=\\\"width:10px;padding-right:10px\\\"></span><br/>";
    public static final String UPLOAD_STRING = "<span class=\\\"glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow\\\" style=\\\"float:right\\\"></span><br/>";
    public static final String DOWNLOAD_STRING = "<span class=\\\"glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow\\\" style=\\\"float:right\\\"></span><br/>";
    public static final String HTML_LINE_BREAK = "<br/>";

    public static final String ACTION_NAME = "actionName";
    public static final String ACTIVE = "ACTIVE";
    public static final String REST_PARENT_PKG_SM = "sm";
    public static final String REST_PARENT_PKG_PD = "pd";

    public static final String IMPORT_ENTITY_CREATED = "CREATED";
    public static final String IMPORT_ENTITY_UPDATED = "UPDATED";
    public static final String TYPE = "type";
    public static final String ADMIN_STAFF_ID = "STAFF_1";
    public static final String UNLIMITED_QUOTA_STRING = "UNLIMITED";

    public static final String PARTNER_ID = "partnerId";
    public static final String ACCOUNT_ID = "accountId";
    public static final String CHECKMASTER = "checkMaster";
    public static final String MASTER_PREFIX = "masterPrefix";
    public static final String PREFIXES = "Prefixes";

    public static final String DATABASE_CONFIG_FILE_LOCATION="/WEB-INF/database.properties";


    public static final String SPECIAL_DAY_RATE = "SPECIAL_RATE";
    public static final String PEAK_DAY_RATE = "PEAK_RATE";
    public static final String OFF_PEAK_DAY_RATE = "OFF_PEAK_RATE";
    public static final String WEEKEND_DAY_RATE = "WEEKEND_RATE";

    public static final String IMPORT_STATUS_REPORT = "common/import-status-report";

    public static final String RNC_PACKAGE_ID = "rncPackageId";
    public static final String FILE_MAPPING_TYPE = "FILEWRITING";
    public static final String RATECARD_GROUP_ID = "rateCardGroupIds";
    public static final String MANAGE_ORDER = "manageorder";
    public static final String RNC_PACKAGE_URL = "pd/rncpackage/rnc-package/";
    public static final String RATE_CARD_GROUP_ID = "rateCardGroupId";
    public static final String SUBTABLEURL = "subtableurl";


    public static final String GLOBAL_SYSTEM_PARAMETR = "globalSystemParameter";
    public static final String OFFLINE_RNC_SYSTEM_PARAMETER = "offlineRnCSystemParameter";

    public static final String SCHEMA_FILE_NAME = "netvertex-schema.sql";
    public static final String NETVERTEX_SQL_FILE_NAME = "netvertex.sql";
    public static final String NETWORK_INFORMATION_SQL_FILE_NAME = "networkInformation.sql";
    public static final String PGAGENT_SQL_FILE_NAME = "pgagent.sql";

    public static final int NOTIFICATION_AGENT_PASSWORD_MIN_LENGTH =1;
    public static final int NOTIFICATION_AGENT_PASSWORD_MAX_LENGTH =20;
    public static final String ACTION_PKG_SEARCH = "policydesigner/pkg/Pkg/search";
    public static final String ACTION_PKG_VIEW ="policydesigner/pkg/Pkg/view";
    public static final String ACTION_QUOTA_PROFILE_VIEW = "policydesigner/quota/QuotaProfile/view";
    public static final String ACTION_QOS_PROFILE_VIEW = "policydesigner/qos/QosProfile/view";
    public static final String ACTION_IMS_PKG_VIEW ="policydesigner/ims/IMSPkg/view";
    public static final String ACTION_EMERGENCY_PKG_SEARCH = "policydesigner/emergency/EmergencyPkg/search";
    public static final String ACTION_EMERGENCY_PKG_VIEW ="policydesigner/emergency/EmergencyPkg/view";
    public static final String ACTION_EMERGENCY_QOS_PROFILE_VIEW = "policydesigner/emergencypkgqos/EmergencyPkgQos/view";
    public static final String ACTION_PROMOTIONAL_PKG_SEARCH = "promotional/policydesigner/pkg/Pkg/PromotionalPkg/search?pkgType="+ PkgType.PROMOTIONAL.name();
    public static final Long LONG_MAX_VALUE = 999999999999999999L;

    public static final String START_BOLD_TEXT_TAG="<b>";
    public static final String CLOSE_BOLD_TEXT_TAG="</b>";
    public static final String START_ITALIC_TEXT_TAG="<i>";
    public static final String CLOSE_ITALIC_TEXT_TAG="</i>";
    public static final String DEFAULT_ROLE_ID = "ROLE_1";
    public static final String DEFAULT_PASSWORD_POLICY_ID = "PASSWORD_POLICY_1";
    public static final int RENEWAL_INTERVAL_MAX_VAL = 999;
    public static final String BREAK_LINE = "</br>";

    //EDR DRIVER COMMON CONSTANTS
    public static  final String EXTERNAL_ALTERNATE_ID_EDR_DRIVER = "externalAlternateIdEDRDriver";
    public static final String SUBSCRIPTION_EDR_DRIVER = "subscriptionEDRDriver";
    public static final String BALANCE_EDR_DRIVER = "balanceEDRDriver";
    public static final String SUBSCRIBER_EDR_DRIVER = "subscriberEDRDriver";
    public static final String BOD_PACKAGE_ID = "bodPackageId";
    public static final String READ_ONLY_ROLE_ID = "ROLE_4";

    //PREFIX HEADER
    public static final String PREFIX_HEADER = "Prefix,Country,Operator,Network";
    public static final String PREFIX_HEADER_WITH_REMARKS = "Prefix,Country,Operator,Network,Remarks";
    public static final String EXPORT_PREFIX= "PCC-PREFIX-";
    public static final String IMPORT_PREFIX_ERROR= "PCC-PREFIX-ERROR-";
    public static final String CSV = ".csv";

    private NVSMXCommonConstants(){}


}


