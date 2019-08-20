package com.elitecore.netvertex.core.session.voltdb;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.conf.impl.SessionConfigurationImpl;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.session.SessionDao;

import java.util.List;

import static com.elitecore.commons.base.Strings.isNullOrBlank;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.netvertex.core.session.NetvertexSessionManager.CORE_SESS_TABLE_NAME;
import static com.elitecore.netvertex.core.session.voltdb.VoltCriteriaDataFactory.create;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.DELETE_CORE_SESSIONS_BY_SINGLE_KEY_VALUE;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.DELETE_SESSION_RULES_BY_SINGLE_KEY_VALUE;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.SAVE_CORE_SESSION;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.SAVE_SESSION_RULE;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.SELECT_SESSION_RULES_BY_SINGLE_KEY_VALUE;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.UPDATE_CORE_SESSION;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.UPDATE_SESSION_RULE;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public class VoltDBSessionDao implements SessionDao {

    private static final String MODULE = "VOLT-SESSION-DAO-IMPL";

    private final NetVertexServerContext serverContext;
    private SessionConfigurationImpl sessionConfiguration;
    private VoltDBClient voltDBClient;

    private VoltSynchronousSave saveOperation;
    private VoltSynchronousUpdate updateOperation;
    private VoltSynchronousDelete deleteOperation;
    private VoltSynchronousSelect selectOperation;


    public VoltDBSessionDao(final NetVertexServerContext serverContext,
                            SessionConfigurationImpl sessionConfiguration,
                            VoltDBClient voltDBClient)
            throws InitializationFailedException {

        this.serverContext = serverContext;
        this.sessionConfiguration = sessionConfiguration;
        this.voltDBClient = voltDBClient;
        createDBOperation();
        if(getLogger().isInfoLogLevel()){
            getLogger().info(MODULE, "VoltDB SessionDao created successfully.");
        }
    }

    private void createDBOperation() {
        saveOperation = new VoltSynchronousSave(voltDBClient);
        updateOperation = new VoltSynchronousUpdate(voltDBClient);
        deleteOperation = new VoltSynchronousDelete(voltDBClient);
        selectOperation = new VoltSynchronousSelect(voltDBClient);
    }


    @Override
    public void reloadSessionManagerConfiguration(){
        sessionConfiguration.setMaxBatchSize(serverContext.getServerConfiguration().getSessionManagerConfiguration().getBatchSize());
    }

    @Override
    public boolean isAlive(){
        return voltDBClient.isAlive();
    }

    @Override
    public int saveCoreSession(SessionData sessionData){
        return saveOperation.execute(sessionData, sessionConfiguration.getSchemaList().get(0), SAVE_CORE_SESSION);
    }

    @Override
    public int updateCoreSession(SessionData sessionData){
        return updateOperation.execute(sessionData, sessionConfiguration.getSchemaList().get(0), UPDATE_CORE_SESSION);
    }

    @Override
    public Criteria getCriteriaByTableName(String tableName){
        if(CORE_SESS_TABLE_NAME.equals(tableName)){
            return new VoltDBCoreSessionCriteria(tableName);
        }
        return new VoltDBSimpleCriteria(tableName);
    }

    @Override
    public int saveSessionRule(SessionData sessionData){
        return saveOperation.execute(sessionData, sessionConfiguration.getSchemaList().get(1), SAVE_SESSION_RULE);
    }

    @Override
    public int updateSessionRule(SessionData sessionData){
        return updateOperation.execute(sessionData, sessionConfiguration.getSchemaList().get(1), UPDATE_SESSION_RULE);
    }

    @Override
    public void deleteSessionRule(Criteria criteria){

        VoltDBSimpleCriteria voltDBCriteria = (VoltDBSimpleCriteria)criteria;

        String key = voltDBCriteria.getSimpleKey();
        String value = voltDBCriteria.getSimpleValue();

        if (PCRFKeyConstants.CS_CORESESSION_ID.val.equals(key)) {
            deleteSessionRuleByCoreSessionId(value);
        } else if(PCRFKeyConstants.CS_AF_SESSION_ID.val.equals(key)){
            deleteOperation.delete(create(DELETE_SESSION_RULES_BY_SINGLE_KEY_VALUE, null
                    , PCRFKeyConstants.CS_AF_SESSION_ID.val, value));
        } else if(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val.equals(key)){
            deleteOperation.delete(create(DELETE_SESSION_RULES_BY_SINGLE_KEY_VALUE, null
                    , PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, value));
        } else {
            throw new UnsupportedOperationException("Complex Criteria is not supported.");
        }
    }

    @Override
    public int deleteSessionRule(SessionData sessionData){
        String coreSessionId = sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.val);
        String afSessionId = sessionData.getValue(PCRFKeyConstants.CS_AF_SESSION_ID.val);
        String gateWayAddress = sessionData.getValue(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val);

        if(isNullOrBlank(coreSessionId) == false){
            return deleteOperation.delete(create(DELETE_SESSION_RULES_BY_SINGLE_KEY_VALUE, null
                    , PCRFKeyConstants.CS_CORESESSION_ID.val, coreSessionId));
        } else if(isNullOrBlank(afSessionId) == false){
            return deleteOperation.delete(create(DELETE_SESSION_RULES_BY_SINGLE_KEY_VALUE, null
                    , PCRFKeyConstants.CS_AF_SESSION_ID.val, afSessionId));
        } else if(isNullOrBlank(gateWayAddress) == false){
            return deleteOperation.delete(create(DELETE_SESSION_RULES_BY_SINGLE_KEY_VALUE, null
                    , PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, gateWayAddress));
        } else {
            throw new UnsupportedOperationException("Complex Criteria is not supported.");
        }
    }

    @Override
    public int deleteCoreSession(Criteria criteria){
        VoltDBCoreSessionCriteria voltDBCriteria = (VoltDBCoreSessionCriteria)criteria;

        List<String> coreSessions = voltDBCriteria.getCoreSessionIdValues();
        List<String> gatewayAddresses = voltDBCriteria.getGatewayAddressValues();

        if (isNotEmpty(coreSessions)) {
            return deleteCoreSessionByCoreSessionId(coreSessions.get(0));
        } else if(isNotEmpty(gatewayAddresses)){
            return deleteCoreSessionByGatewayAddress(gatewayAddresses.get(0));
        } else {
            throw new UnsupportedOperationException("Complex Criteria is not supported.");
        }
    }

    @Override
    public int deleteCoreSessionByCoreSessionId(String coreSessionId){
        return deleteOperation.delete(create(DELETE_CORE_SESSIONS_BY_SINGLE_KEY_VALUE, null,
                PCRFKeyConstants.CS_CORESESSION_ID.val, coreSessionId));
    }

    public int deleteCoreSessionByGatewayAddress(String gatewayAddress){
        return deleteOperation.delete(create(DELETE_CORE_SESSIONS_BY_SINGLE_KEY_VALUE, null,
                PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, gatewayAddress));
    }

    @Override
    public void deleteSessionRuleByCoreSessionId(String coreSessionId){
        deleteOperation.delete(create(DELETE_SESSION_RULES_BY_SINGLE_KEY_VALUE, null
                , PCRFKeyConstants.CS_CORESESSION_ID.val, coreSessionId));
    }

    @Override
    public List<SessionData> getCoreSessionByCoreSessionID(String coreSessionId){
        return selectOperation.list(create(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE, sessionConfiguration.getSchemaList().get(0)
                , PCRFKeyConstants.CS_CORESESSION_ID.val, coreSessionId));
    }

    @Override
    public List<SessionData> getCoreSessionBySubscriberIdentity(String subscriberIdentity){
        return selectOperation.list(create(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE, sessionConfiguration.getSchemaList().get(0)
                , PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberIdentity));
    }

    @Override
    public List<SessionData> getCoreSessionBySessionIPv4(String sessionIPv4){
        return selectOperation.list(create(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE, sessionConfiguration.getSchemaList().get(0)
                , PCRFKeyConstants.CS_SESSION_IPV4.val, sessionIPv4));
    }

    @Override
    public List<SessionData> getCoreSessionBySessionIPv6(String sessionIPv6){
        return selectOperation.list(create(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE, sessionConfiguration.getSchemaList().get(0)
                , PCRFKeyConstants.CS_SESSION_IPV6.val, sessionIPv6));
    }

    @Override
    public List<SessionData> getCoreSessionByGatewayAddress(String gatewayAddress){
        return selectOperation.list(create(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE, sessionConfiguration.getSchemaList().get(0)
                , PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, gatewayAddress));
    }

    @Override
    public List<SessionData> getCoreSessionList(Criteria criteria){
        return selectOperation.list(create(sessionConfiguration.getSchemaList().get(0), criteria));
    }

    @Override
    public List<SessionData> getSessionRules(Criteria criteria){
        VoltDBSimpleCriteria voltDBCriteria = (VoltDBSimpleCriteria)criteria;

        String key = voltDBCriteria.getSimpleKey();
        String value = voltDBCriteria.getSimpleValue();

        if (PCRFKeyConstants.CS_CORESESSION_ID.val.equals(key)) {
            return getSessionRuleByCoreSessionID(value);
        } else if(PCRFKeyConstants.CS_AF_SESSION_ID.val.equals(key)){
            return getSessionRuleByAfSessionID(value);
        } else {
            throw new UnsupportedOperationException("Complex Criteria is not supported.");
        }
    }

    @Override
    public List<SessionData> getSessionRuleByCoreSessionID(String coreSessionID){
        return selectOperation.list(create(SELECT_SESSION_RULES_BY_SINGLE_KEY_VALUE, sessionConfiguration.getSchemaList().get(1)
                , PCRFKeyConstants.CS_CORESESSION_ID.val, coreSessionID));
    }

    public List<SessionData> getSessionRuleByAfSessionID(String afSessionID){
        return selectOperation.list(create(SELECT_SESSION_RULES_BY_SINGLE_KEY_VALUE, sessionConfiguration.getSchemaList().get(1)
                , PCRFKeyConstants.CS_AF_SESSION_ID.val, afSessionID));
    }
}
