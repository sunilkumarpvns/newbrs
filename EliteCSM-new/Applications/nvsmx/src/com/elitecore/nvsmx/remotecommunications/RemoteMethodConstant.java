package com.elitecore.nvsmx.remotecommunications;

/**
 * Created by aditya on 9/20/16.
 */
public class RemoteMethodConstant {
    //NetVertex REST WS Constant

    public static final String NETVERTEX_RELOAD_CONFIGURATION_BASE_PATH_URI = "/netvertex/server-instance";
    public static final String NETVERTEX_RELOAD_CONFIGURATION = "NETVERTEX_RELOAD_CONFIGURATION";
    public static final String NETVERTEX_GET_DATABASE_DATASOURCE = "NETVERTEX_GET_DATABASE_DATASOURCE";
    public static final String NETVERTEX_GET_SERVICES_INFO = "NETVERTEX_GET_SERVICES_INFO";
    public static final String NETVERTEX_SESSION_REST_BASE_URI_PATH = "/netvertex/session";
    public static final String NETVERTEX_LICENSE_REST_BASE_URI_PATH = "/netvertex/license";
    public static final String NETVERTEX_POLICY_REST_BASE_URI_PATH = "/netvertex/reload";
    public static final String RE_AUTH_BY_SUBSCRIBER_IDENTITY = "RE_AUTH_BY_SUBSCRIBER_IDENTITY";
    public static final String RE_AUTH_BY_SESSION_IPV4 = "RE_AUTH_BY_SESSION_IPV4";
    public static final String RE_AUTH_BY_SESSION_IPV6 = "RE_AUTH_BY_SESSION_IPV6";
    public static final String RE_AUTH_BY_CORE_SESSION_ID = "RE_AUTH_BY_CORE_SESSION_ID";
    public static final String SESSIONS_BY_SUBSCRIBER_IDENTITY = "SESSIONS_BY_SUBSCRIBER_IDENTITY";
    public static final String SESSIONS_BY_SUBSCRIBER_IDENTITY_FROM_CACHE = "SESSIONS_BY_SUBSCRIBER_IDENTITY_FROM_CACHE";
    public static final String SESSIONS_BY_SESSION_IPV6_FROM_CACHE = "SESSIONS_BY_SESSION_IPV6_FROM_CACHE";
    public static final String SESSIONS_BY_SESSION_IPV4_FROM_CACHE = "SESSIONS_BY_SESSION_IPV4_FROM_CACHE";
    public static final String SESSIONS_BY_SESSION_IPV4 = "SESSIONS_BY_SESSION_IPV4";
    public static final String SESSIONS_BY_SESSION_IPV6 = "SESSIONS_BY_SESSION_IPV6";
    public static final String SERVER_STATUS = "SERVER_STATUS";
    public static final String SESSIONS_BY_CORE_SESSION_ID = "SESSIONS_BY_CORE_SESSION_ID";
    public static final String SESSIONS_BY_CORE_SESSION_ID_FROM_CACHE = "SESSIONS_BY_CORE_SESSION_ID_FROM_CACHE";
    public static final String REMOVE_SESSION_BY_CORE_SESSION_ID = "REMOVE_SESSION_BY_CORE_SESSION_ID";
    public static final String REMOVE_SESSION_BY_CORE_SESSION_ID_FROM_CACHE = "REMOVE_SESSION_BY_CORE_SESSION_ID_FROM_CACHE";
    public static final String SESSIONS_DISCONNECT_BY_CORE_SESSION_ID = "SESSIONS_DISCONNECT_BY_CORE_SESSION_ID";
    public static final String SESSIONS_DISCONNECT_BY_CORE_SUBSCRIBER_IDENTITY = "SESSIONS_DISCONNECT_BY_CORE_SUBSCRIBER_IDENTITY";
    public static final String RELOAD_POLICIES = "RELOAD_POLICIES";
    public static final String RELOAD_POLICY = "RELOAD_POLICY";
    public static final String RELOAD_POLICIES_BY_GROUPS = "RELOAD_POLICIES_BY_GROUPS";
    public static final String RELOAD_DATA_SLICE_CONFIGURATION = "RELOAD_DATA_SLICE_CONFIGURATION";
    //same constant as enum name

    //PD REST WS Constant

    public static final String NVSMX_REST_BASE_URI_PATH = "/rest/restful/policy";
    public static final String NVSMX_PRIVATE_REST_BASE_URI_PATH = "/rest/restful/policy/internal";
    public static final String NVSMX_STATUS_REST_PATH = "/rest/restful/status";
    public static final String NVSMX_REMOTE_SHUT_DOWN = "NVSMX_REMOTE_SHUT_DOWN";
    public static final String NVSMX_REMOTE_WAKE_UP = "NVSMX_REMOTE_WAKE_UP";

    public static final String NVSMX_RELOAD_ALL_OWN_POLICY = "NVSMX_RELOAD_ALL_OWN_POLICY";
    public static final String NVSMX_RELOAD_OWN_POLICIES = "NVSMX_RELOAD_OWN_POLICIES";
    public static final String NVSMX_RELOAD_OWN_POLICIES_BY_GROUPS = "NVSMX_RELOAD_OWN_POLICIES_BY_GROUPS";
    public static final String NVSMX_RELOAD_OWN_DATA_SLICE_CONFIGURATION = "NVSMX_RELOAD_OWN_DATA_SLICE_CONFIGURATION";

    private RemoteMethodConstant(){}
}
