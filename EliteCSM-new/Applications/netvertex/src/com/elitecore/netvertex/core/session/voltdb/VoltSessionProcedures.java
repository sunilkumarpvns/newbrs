package com.elitecore.netvertex.core.session.voltdb;

public class VoltSessionProcedures {
    public static final String SAVE_CORE_SESSION = "SaveCoreSessionStoredProcedure";
    public static final String SAVE_SESSION_RULE = "SaveSubSessionStoredProcedure";

    public static final String UPDATE_CORE_SESSION = "UpdateCoreSessionStoredProcedure";
    public static final String UPDATE_SESSION_RULE = "UpdateSubSessionStoredProcedure";

    public static final String DELETE_CORE_SESSIONS_BY_SINGLE_KEY_VALUE = "DeleteCoreSessionsBySingleKeyValueStoredProcedure";
    public static final String DELETE_SESSION_RULES_BY_SINGLE_KEY_VALUE = "DeleteSessionRulesBySingleKeyValueStoredProcedure";

    public static final String SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE = "SelectCoreSessionsBySingleKeyValueStoredProcedure";
    public static final String SELECT_CORE_SESSIONS_BY_CRITERIA = "SelectCoreSessionsByCriteriaStoredProcedure";
    public static final String SELECT_SESSION_RULES_BY_SINGLE_KEY_VALUE = "SelectSessionRulesBySingleKeyValueStoredProcedure";
}
