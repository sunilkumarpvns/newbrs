package com.elitecore.nvsmx.system.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jaidiptrivedi
 *
 * Contains results and committable, set committable true if you want to commit transaction for that result.
 */
public enum Results {

    LOGIN("login", true),
    DBSETUP("dbsetup", false),
    WELCOME("welcome", true),
    SERVER_MANAGER_WELCOME("serverManagerWelcome", true),
    SUCCESS("success", true),
    FAIL("success",true),
    SUBTABLE_SUCCESS("subtablesuccess", false),
    ERROR("error", false),
    FORGOT_PASSWORD("forgotPassword", false),
    RESET_PASSWORD("resetPassword", false),
    MENU_ITEMS("menuItems", false),
    LIST("list", false),
    VIEW("view", true),
    BRM("brm",true),
    DISPATCH_VIEW("dispatchView", true),
    DETAIL("detail", false),
    DELETE("delete", false),
    UPDATE("update", false),
    CREATE("create", false),
    PROFILE_PICTURE("profilePicture", false),
    SUBSCRIBER_SEARCH_SUCCESS("subscriberSearchSuccess", false),
    PKG_SUCCESS("pkgsuccess", false),
    ACTION_CHAIN_URL("actionChainUrl", true),
    VIEW_DELETE_ADDON("viewDeleteAddOn", false),
    REDIRECT_ACTION("redirectAction", true),
    CREATE_DETAIL("createdetail", false),
    UPDATE_DETAIL("updatedetail", false),
    SEARCH_ADDON_SUCCESS("searchAddOnSuccess", false),
    SEARCH_TEST_SUBCRIBER("searchTestSubscriber", false),
    SEARCH_DELETED_SUBCRIBER("searchDeletedSubscriber", false),
    AUDIT("audit", false),
    AUDIT_DATA_DETAIL("auditDataDetail", false),
    REDIRECT_ERROR("RedirectError", false),
    SEARCH_SUCCESS("SearchSuccess", false),
    IMPORT_PKG("importPackage", false),
    IMPORT_SERVICE("importServiceData", false),
    IMPORT_DATA_SERVICE_TYPE("data-service-type-import", false),
    IMPORT_RATING_GROUP("importRatingGroup", false),
    IMPORT_PCC_RULE("importPCCRule", false),
    IMPORT_IMS_PKG("importImsPackage", false),
    IMPORT_EMERGENCY_PKG("importEmergencyPackage", false),
    SUBTABLEURL("subtableurl", false),
    IMPORT_STATUS_REPORT("importStatusReport", false),
    RELOAD_POLICY("reloadPolicy", false),
    PROGRESS_BAR("progressbar", false),
    COMPULSARY_CHANGE("compulsaryChange", false),
    MANUAL_CHANGE("manualChange", false),
    MANAGE_ORDER("manageOrder", false),
    IMPORT_CHARGING_RULE_BASE_NAME("importChargingRuleBaseName", false),
    EXPORT_COMPLETED("EXPORT_COMPLETED", false),
    REDIRECT_TO_PARENT("redirectToParent", true),
    DISPATCH_TO_PARENT("dispatchToParent", false),
    UPLOAD_REPORT("upload-report", false),
    REDIRECT_UPLOAD_REPORT("redirectUploadReport", true),
    LIST_DELETED("listDeleted", false),
    DOWNLOAD("download", false),
    SSO_LOGIN("ssoLogin",false);


    private String value;
    private boolean committable;
    private static Map<String, Results> map = new HashMap<>();

    static {
        for (Results result : values()) {
            map.put(result.value, result);
        }
    }

    Results(String value, boolean committable) {
        this.value = value;
        this.committable = committable;
    }

    public String getValue() {
        return value;
    }

    public boolean isCommittable() {
        return committable;
    }

    public static Results fromValue(String val) {
        return map.get(val);
    }
}
