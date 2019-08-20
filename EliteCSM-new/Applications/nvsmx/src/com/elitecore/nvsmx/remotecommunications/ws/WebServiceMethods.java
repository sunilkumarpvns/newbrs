package com.elitecore.nvsmx.remotecommunications.ws;

import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Maps;
import com.elitecore.nvsmx.remotecommunications.util.Transformers;

import java.util.Map;

/**
 * Created by aditya on 9/13/16.
 */
public enum WebServiceMethods {

    RE_AUTH_BY_SUBSCRIBER_IDENTITY(
            WebServiceMethods.SESSION_WS_PATH,
            "/reAuth/subscriberIdentity/") {
        @Override
        public  Function getTransformer() {
            return  Transformers.stringToString();
        }
    },

    RE_AUTH_BY_SESSION_IPV4(
            WebServiceMethods.SESSION_WS_PATH,
            "/reAuth/IPv4/") {
        @Override
        public Function getTransformer() {
            return  Transformers.stringToString();
        }

    },

    RE_AUTH_BY_SESSION_IPV6(
            WebServiceMethods.SESSION_WS_PATH,
            "/reAuth/IPv6/"){
        @Override
        public Function getTransformer() {
            return  Transformers.stringToString();
        }
    },

    RE_AUTH_BY_CORE_SESSION_ID(
            WebServiceMethods.SESSION_WS_PATH,
            "/reAuth/coreSessionId/") {
        @Override
        public Function getTransformer() {
            return  Transformers.stringToString();
        }

    },

    SESSIONS_BY_SUBSCRIBER_IDENTITY(
            WebServiceMethods.SESSION_WS_PATH,
            "/subscriberIdentity/"){



        @Override
        public Function getTransformer(){
            return Transformers.stringToSessionInformations();
        }
    },

    SESSIONS_BY_SUBSCRIBER_IDENTITY_FROM_CACHE(
            WebServiceMethods.SESSION_WS_PATH,
            "/subscriberIdentity/cache/"){

        @Override
        public Function getTransformer(){
            return Transformers.stringToSessionInformations();
        }
    },

    SESSIONS_BY_SESSION_IPV4_FROM_CACHE(
            WebServiceMethods.SESSION_WS_PATH,
            "/IPv4/cache/"){

        @Override
        public Function getTransformer(){
            return Transformers.stringToSessionInformations();
        }
    },

    SESSIONS_BY_SESSION_IPV6_FROM_CACHE(
            WebServiceMethods.SESSION_WS_PATH,
            "/IPv6/cache/"){

        @Override
        public Function getTransformer(){
            return Transformers.stringToSessionInformations();
        }
    }
    ,
    SESSIONS_BY_SESSION_IPV4(
            WebServiceMethods.SESSION_WS_PATH,
            "/IPv4/"){
        @Override
        public Function getTransformer(){
            return Transformers.stringToSessionInformations();
        }
    },

    SESSIONS_BY_SESSION_IPV6(
            WebServiceMethods.SESSION_WS_PATH,
            "/IPv6/"){

        @Override
        public Function getTransformer(){
            return Transformers.stringToSessionInformations();
        }
    },

    SERVER_STATUS(
            WebServiceMethods.SESSION_WS_PATH,
            "/serverStatus/"){
        @Override
        public Function getTransformer() {
            return  Transformers.stringToString();
        }

    },

    SESSIONS_BY_CORE_SESSION_ID(
            WebServiceMethods.SESSION_WS_PATH,
            "/coreSessionId/"){

        @Override
        public Function getTransformer(){
            return Transformers.stringTosessionInformation();
        }

    },

    SESSIONS_BY_CORE_SESSION_ID_FROM_CACHE(
            WebServiceMethods.SESSION_WS_PATH,
            "/coreSessionId/cache/"){


        @Override
        public Function getTransformer(){
            return Transformers.stringTosessionInformation();
        }

    },

    REMOVE_SESSION_BY_CORE_SESSION_ID(
            WebServiceMethods.SESSION_WS_PATH,
            "/remove/coreSessionId/"){
        @Override
        public Function getTransformer() {
            return  Transformers.stringToString();
        }

    },
    
    REMOVE_SESSION_BY_CORE_SESSION_ID_FROM_CACHE(
            WebServiceMethods.SESSION_WS_PATH,
            "/remove/cache/coreSessionId/"){
        @Override
        public Function getTransformer() {
            return  Transformers.stringToString();
        }

    },
    SESSIONS_DISCONNECT_BY_CORE_SESSION_ID(
            WebServiceMethods.SESSION_WS_PATH,
            "/disconnect/coreSessionId/"){
        @Override
        public Function getTransformer() {
            return  Transformers.stringToString();
        }

    },
    SESSIONS_DISCONNECT_BY_CORE_SUBSCRIBER_IDENTITY(
            WebServiceMethods.SESSION_WS_PATH,
            "/disconnect/subscriberIdentity/"){
        @Override
        public Function getTransformer() {
            return  Transformers.stringToString();
        }
    },
    NVSMX_SERVER_STATUS(
            WebServiceMethods.REST_RESTFUL_POLICY,
            "/serverStatus/"){
        @Override
        public Function getTransformer() {
            return  Transformers.stringToString();
        }

    },



    RELOAD_POLICY(
            WebServiceMethods.REST_RESTFUL_POLICY,
            "/policy"){
        @Override
        public Function getTransformer() {
            return  Transformers.stringToPolicyCacheDetail();
        }
    },
    RELOAD_POLICIES_BY_GROUPS(
            WebServiceMethods.REST_RESTFUL_POLICY,
            "/policyByGroups"){
        @Override
        public Function getTransformer() {
            return  Transformers.stringToPolicyCacheDetail();
        }
    },
    RELOAD_POLICIES(
            WebServiceMethods.REST_RESTFUL_POLICY,
            "/policy"){
        @Override
        public Function getTransformer() {
            return  Transformers.stringToPolicyCacheDetail();
        }
    },
    NVSMX_RELOAD_ALL_OWN_POLICY(
            "rest/restful/policy/private/",
            "/reload/allPolicies"){
        @Override
        public Function getTransformer() {
            return  Transformers.stringToPolicyCacheDetail();
        }
    },
    NVSMX_RELOAD_OWN_POLICIES(
            WebServiceMethods.REST_RESTFUL_POLICY,
            "/reload/policies"){
        @Override
        public Function getTransformer() {
            return  Transformers.stringToPolicyCacheDetail();
        }
    },
    NVSMX_RELOAD_OWN_POLICIES_BY_GROUPS(
            WebServiceMethods.REST_RESTFUL_POLICY,
            "/reload/policiesByGroups"){
        @Override
        public Function getTransformer() {
            return  Transformers.stringToPolicyCacheDetail();
        }
    },
    NVSMX_REMOTE_SHUT_DOWN(
            WebServiceMethods.NVSMX_STATUS_WS_PATH,
            "/remoteShutDown/"){
        @Override
        public Function getTransformer() {
            return  Transformers.stringToString();

        }
    },
    NVSMX_REMOTE_WAKE_UP(
            WebServiceMethods.NVSMX_STATUS_WS_PATH,
                    "/remoteWakeUp/"){
        @Override
        public Function getTransformer() {
            return  Transformers.stringToString();

        }
    },

    NETVERTEX_RELOAD_CONFIGURATION(WebServiceMethods.NETVERTEX_INSTANCE_API_PATH, "/reload-configuration") {
        public Function getTransformer() {
            return Transformers.stringToString();
        }
    },

    NETVERTEX_GET_DATABASE_DATASOURCE(WebServiceMethods.NETVERTEX_INSTANCE_API_PATH, "/getDatabaseDataSource") {
        public Function getTransformer() {
            return Transformers.stringToString();
        }
    },

    NETVERTEX_GET_SERVICES_INFO(WebServiceMethods.NETVERTEX_INSTANCE_API_PATH, "/serviceInfo/") {
        public Function getTransformer() {
            return Transformers.stringToString();
        }
    },

    NETVERTEX_GET_GATEWAY_STATUS_INFO(WebServiceMethods.NETVERTEX_INSTANCE_API_PATH, "/gatewayStatusInfo/") {
        public Function getTransformer() {
            return Transformers.stringToString();
        }
    },

    NETVERTEX_GET_GLOBAL_LISTENERS_INFO(WebServiceMethods.NETVERTEX_INSTANCE_API_PATH, "/globalListenerInfo/") {
        public Function getTransformer() {
            return Transformers.stringToString();
        }
    },

    NETVERTEX_GET_DATASOURCE_INFO(WebServiceMethods.NETVERTEX_INSTANCE_API_PATH, "/dataSourceInfo/") {
        public Function getTransformer() {
            return Transformers.stringToString();
        }
    },

    NETVERTEX_GET_SERVER_INFO(WebServiceMethods.NETVERTEX_INSTANCE_API_PATH, "/serverInfo") {
        public Function getTransformer() {
            return Transformers.stringToString();
        }
    },

    NETVERTEX_GET_POLICY_STATUS_INFO(WebServiceMethods.NETVERTEX_INSTANCE_API_PATH, "/policyStatusInfo") {
        public Function getTransformer() {
            return Transformers.stringToString();
        }
    },

    NETVERTEX_REMOVE_ALTERNATE_ID_FROM_CACHE(WebServiceMethods.NETVERTEX_INSTANCE_API_PATH, "/removeAlternateIdFromCache/") {
        public Function getTransformer() {
            return Transformers.stringToString();
        }
    },

    NVSMX_RELOAD_OWN_DATA_SLICE_CONFIGURATION(WebServiceMethods.REST_RESTFUL_POLICY, "/reload/reloadOwnDataSliceConfiguration") {
        @Override
        public Function getTransformer () { return Transformers.stringToString(); }
    },

    RELOAD_DATA_SLICE_CONFIGURATION(WebServiceMethods.REST_RESTFUL_POLICY, "/reloadDataSliceConfiguration") {
        @Override
        public Function getTransformer() {
            return Transformers.stringToString();
        }
    }

    ;
    private static final String REST_RESTFUL_POLICY = "rest/restful/policy/";

    private static final String SESSION_WS_PATH ="/netvertex/session";
    private static final String NVSMX_STATUS_WS_PATH ="/rest/restful/status";
    private static final String NETVERTEX_INSTANCE_API_PATH= "/netvertex/server-instance";

    private String contextPath;
    private String methodName;

    private static Map<String,WebServiceMethods> nameToMethodMap = Maps.newHashMap();

    static {
        for(WebServiceMethods  method : WebServiceMethods.values() ){
            nameToMethodMap.put(method.name(),method);
        }
    }

    WebServiceMethods(String contextPath, String methodName) {
        this.contextPath = contextPath;
        this.methodName = methodName;
    }


    public static WebServiceMethods of(String name){
        return nameToMethodMap.get(name);
    }

    public String getMethodURL() {
        return methodName;
    }



    public abstract <V> Function<String,V> getTransformer();


}