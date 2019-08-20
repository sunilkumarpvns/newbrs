package com.elitecore.nvsmx.system.constants;
/*
 * 
 * This class will contain constant for attributes only.
 * 
 */
public class Attributes {


    /*
             * Application Scope attribute
             */
	//FIXME private variable at AuditSearch Page
	public static int count;
	public static final String ACTION = "action";
	public static final String SYSTEM_INFORMATION = "systemInformation";
	public static final String BLOCKED_IP_RELEASE_TIME_MAP = "blockedIPreleaseTimeMap";
	
	public static final String ROW_DATA ="rowData";
	public static final String TABLE_ID ="tableId";
	
	public static final String NOTE = "note";

	public static final String CAPTCHA_FAILURE_COUNT = "captchaFailureCount";
	public static final String UNSUBSCRIBE_FAILURE_COUNT = "unsubscribeFailureCount";

	public static final String STAFF_USERNAME = "staffUsername";
	public static final String STAFF_NAME = "staffName";
	public static final String STAFF_DATA = "staffData";
	public static final String STAFF_ID = "staffId";
	public static final String STAFF_PASSWORD = "staffPassword";

	public static final String SUBSCRIBER = "subscriber";
	public static final String SUBSCRIBER_IDENTITY = "subscriberIdentity";
	public static final String ALTERNATE_ID_FIELD = "alternateIdField";
	public static final String CRITERIA = "criteriaJson";
	public static final String CRITERIA_VAL = "criteriaVal";
	public static final String ADDON_ID = "addOnId";
	public static final String ADDON_SUBSCRIPTION_ID = "addonSubscriptionId";
	public static final String TEST_SUBSCRIBERS = "testSubscribers";

	public static final String PKG_ID = "pkgId";
	public static final String PKG_MODE = "pkgMode";
	public static final String PKG_TYPE = "pkgType";

	public static final String NOTIFICATION_TEMPLATE_ID = "notificationTemplateId";
	public static final String QOS_PROFILE_ID ="qosProfileId";
	public static final String QUOTA_PROFILE_ID="quotaProfileId";
	public static final String QUOTA_PROFILE_DETAIL_ID="quotaProfileDetailId";
	public static final String QOS_PROFILE_DETAIL_ID= "qosProfileDetailId";
	public static final String PCC_RULE_ID = "pccRuleId";
	
	public static final String ADVANCE_CONDITIONS = "advanceConditions";
	public static final String TIME_PERIOD = "timePeriod";
	public static final String PCCRULE_CHARGING_KEY = "chargingKey";
	
	public static final String USAGE_NOTIFICATION_DATA = "usageNotificationData";
	public static final String QUOTA_NOTIFICATION_DATA = "quotaNotificationData";
	public static final String USAGE_NOTIFICATION_ID = "usageNotificationId";
	public static final String QUOTA_NOTIFICATION_ID = "quotaNotificationId";
	
	public static final String VIEW_HISTORY = "viewHistory";
	public static final String STAFF_BELONGING_GROUP_IDS = "staffBelongingGroupIds";
	public static final String CURRENCY = "currency";
	public static final String STAFF_BELONGING_GROUP = "staffBelongingGroups";
	public static final String STAFF_GROUP_BELONGING_ROLES_MAP = "staffGroupsBelongingRoles";	
	public static final String STAFF_ROLES_SET = "staffRoles";
	public static final String MESSAGE = "message";
	public static final String DATA_PACKAGE_GROUP_IDS = "dataPackageGroupsIds";
	public static final String DATA_PACKAGE_GROUP_NAMES = "dataPackageGroupsNames";
	public static final String CHARGING_RULE_GROUP_FIELD_NAME = "chargingRuleBaseNameData.groups";
	
	public static final String IMS_PACKAGE_GROUP_NAMES = "imsPackageGroupsNames";
	public static final String IMS_PACKAGE_GROUP_IDS = "imsPackageGroupsIds";
	public static final String IMS_PKG_SERVICE_ID = "imsPkgServiceId";


	public static final String LAST_URI="lastUri";
	
	public static final String AUDIT_PAGE_HEADING_NAME = "auditPageHeadingName";
	public static final String AUDITABLE_ID = "auditableId";
	public static final String ACTUAL_ID = "actualId";
	public static final String AUDIT_DATA = "auditData";

	public static final String SSO_USERNAME = "ssoUsername";
	public static final String SSO_PASSWORD = "ssoPassword";
	public static final String DATA_PACKAGES = "dataPackages";
	public static final String RNC_PACKAGES = "rncPackages";
	public static final String LIVE_RNC_PACKAGES = "liveRncPackages";
	public static final String IMS_PACKAGES = "imsPackages";
	public static final String IMS_PACKAGES_LIVE ="imsLivePackages";
	public static final String DATA_PACKAGES_LIVE ="livePackages";
	
	public static final String RATING_GROUP_IDS ="ratingGroupIds";
	public static final String RATING_GROUP_ID ="ratingGroupId";
	public static final String DATA_SERVICE_TYPE_ID ="serviceTypeId";
	public static final String IDS = "ids";
	public static final String FROM_VIEW_PAGE = "fromViewPage";
	
	public static final String UNAUTHORIZED_USER = "unauthorizedUser";
	public static final String FUP_LEVEL = "fupLevel";
	
	public static final String INSTANCE_ID_CODE = "insatanceIDCode";
	public static final String CORE_SESSION_ID = "coreSessionID";
	public static final String REDIRECT_URL = "redirectUrl";
	public static final String SCOPE = "scope";
	public static final String IS_MANUAL_PASSWORD_CHANGE= "isManualPasswordChange";

	public static final String CHARGING_RULE_BASE_NAME_ID = "chargingRuleBaseNameId";

	public static final String UPDATE_ACTION = "updateAction";

	public static final String GROUPIDS = "groupIds";

	public static final String SELECTED_INDEXES = "selectedIndexes"; //This will be used to identify selected entity while importing.
	public static final String USER_ACTION = "userAction"; //This will be used to identify which action user has perform while importing. possible values Replace or Fail
	public static final String SERVICE_TYPES = "serviceTypes" ; //This will be used when need to fetch service types from request.
	public static final String PCCRULES = "pccRules"; //Used when we need to fetch PCCRules from request.
	public static final String IMS_PKG_DATAS = "imsPkgDatas"; //This will be used when need to fetch ims packages from request.
	public static final String RATING_GROUPS= "ratingGroups";//This will be used when need to fetch rating groups from request
	public static final String CHARGING_RULE_BASE_NAME = "chargingRuleBaseNames"; //This will be used when need to fetch charging rule base names from request

	public static final String EMERGENCY_PKGS = "emergencyPkgDatas";
	public static final String PKG_DATAS = "pkgDatas";


	public static final String RNC_PROFILE_DATA = "rncProfileData";
	public static final String RNC_PROFILE_DETAIL_DATA = "rncProfileDetailData";
	public static final String ENTITY_OLD_GROUPS = "entityOldGroups";
	public static final String SHOW_WARNING = "showWarning";
	public static final String PROFILE_PICTURE_FIELD = "profilePicture";
	public static final String LOGGED_IN_STAFF_PROFILE_PICTURE_ID = "loggedInStaffProfilePictureId" ;
	
	public static final String MENU_TYPE = "menuType";
	
	
	//Server Group
	public static final String SERVER_GROUP_TAB_TYPE = "tabType";
	public static final String SERVER_GROUP_TYPE = "serverGroupType";

    // Server Profile
	public static final String SERVER_PROFILE_TAB_TYPE = "tabType";
		
	public static final String PARTNER_MENU = "partnerMenu";

	public static final String PREFIX_TYPES ="prefixTypes";
	
}

