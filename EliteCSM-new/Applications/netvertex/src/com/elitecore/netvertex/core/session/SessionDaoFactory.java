package com.elitecore.netvertex.core.session;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.util.voltdb.VoltDBClientManager;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.SchemaMapping;
import com.elitecore.core.serverx.sessionx.SystemPropertiesProvider;
import com.elitecore.core.serverx.sessionx.conf.SessionConfiguration;
import com.elitecore.core.serverx.sessionx.conf.impl.SessionConfigurationImpl;
import com.elitecore.core.serverx.sessionx.impl.SchemaMappingImpl;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.session.conf.SessionManagerConfiguration;
import com.elitecore.netvertex.core.session.voltdb.VoltDBSessionDao;
import com.elitecore.netvertex.core.voltdb.VoltDBClientAdapter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SessionDaoFactory {

    private static final  String CORE_SESS_PK_FIELD = "CS_ID";
    private static final  String CORE_SESS_START_TIME_FIELD = "START_TIME";
    private static final  String CORE_SESS_LAST_UPDATE_TIME_FIELD = "LAST_UPDATE_TIME";
    private static final  String CORE_SESS_TABLE_NAME = "TBLT_SESSION";

    private static final  String SESSION_RULE_PK_FIELD = "SR_ID";
    private static final  String SESSION_RULE_START_TIME_FIELD = "START_TIME";
    private static final  String SESSION_RULE_LAST_UPDATE_TIME_FIELD = "LAST_UPDATE_TIME";
    private static final  String SESSION_RULE_TABLE_NAME = "TBLT_SUB_SESSION";


    public SessionDao create(NetVertexServerContext serverContext) throws InitializationFailedException {

        final SessionManagerConfiguration sessionMgrConf = serverContext.getServerConfiguration().getSessionManagerConfiguration();
        if(sessionMgrConf == null){
            throw new InitializationFailedException("Fail in initializing session manager. Reason: session configuration is null");
        }

        List<FieldMapping> coreSessionFieldMappings = sessionMgrConf.getCoreSessionFieldMappings();
        List<FieldMapping> sessionRulesFieldMappings = sessionMgrConf.getSessionRuleFieldMappings();

        SchemaMappingImpl coreSessionSchemaMapping = new SchemaMappingImpl(CORE_SESS_PK_FIELD,CORE_SESS_START_TIME_FIELD,CORE_SESS_LAST_UPDATE_TIME_FIELD);

        coreSessionSchemaMapping.setFieldMappings(new ArrayList<>(coreSessionFieldMappings));
        coreSessionSchemaMapping.setIdGenerator(SchemaMapping.UUID_GENERATOR);
        coreSessionSchemaMapping.setSchemaName(CORE_SESS_TABLE_NAME);
        coreSessionSchemaMapping.setTableName(CORE_SESS_TABLE_NAME);


        SchemaMappingImpl sessionRuleSchemaMapping = new SchemaMappingImpl(SESSION_RULE_PK_FIELD, SESSION_RULE_START_TIME_FIELD, SESSION_RULE_LAST_UPDATE_TIME_FIELD);
        sessionRuleSchemaMapping.setFieldMappings(new ArrayList<>(sessionRulesFieldMappings));
        sessionRuleSchemaMapping.setIdGenerator(SchemaMapping.UUID_GENERATOR);

        sessionRuleSchemaMapping.setSchemaName(SESSION_RULE_TABLE_NAME);
        sessionRuleSchemaMapping.setTableName(SESSION_RULE_TABLE_NAME);


        SessionConfigurationImpl.SQLDialectFactory dialectFactory = createDialectFactory();
        SessionConfigurationImpl sessionConfiguration;
        if(dialectFactory == null){
            sessionConfiguration = new SessionConfigurationImpl(serverContext);
        } else {
            sessionConfiguration = new SessionConfigurationImpl(serverContext,dialectFactory);
        }

        sessionConfiguration.setHighQueryResponseTime(AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS);
        if(sessionMgrConf.isBatchUpdateEnable()){
            sessionConfiguration.setSessionFactoryType(SessionConfiguration.DB_SESSION_WITH_BATCH_UPDATE);

            sessionConfiguration.setBatchUpdateInterval(
                    TimeUnit.SECONDS.toMillis(sessionMgrConf.getBatchUpdateIntervalInSec()));

            sessionConfiguration.setMaxBatchSize(sessionMgrConf.getBatchSize());
            sessionConfiguration.setDeleteBatched(sessionMgrConf.isDeleteInBatch());
            sessionConfiguration.setUpdateBatched(sessionMgrConf.isUpdateInBatch());
            sessionConfiguration.setSaveBatched(sessionMgrConf.isSaveInBatch());
        }else{
            sessionConfiguration.setSessionFactoryType(SessionConfiguration.DB_SESSION);
        }


        DBDataSource dbDataSource = serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getSessionDS();
        //Adding Primary and Secondary data source
        sessionConfiguration.addDataSource(dbDataSource);


        sessionConfiguration.addSchema(coreSessionSchemaMapping);
        sessionConfiguration.addSchema(sessionRuleSchemaMapping);

        sessionConfiguration.setSystemPropertiesProvider(new SystemPropertiesProvider() {

            @Override
            public boolean isNoWaitEnabled() {
                return serverContext.getServerConfiguration().getMiscellaneousParameterConfiguration().getSessionNoWait();
            }

            @Override
            public boolean isBatchEnabled() {
                return serverContext.getServerConfiguration().getMiscellaneousParameterConfiguration().getSessionBatch();
            }

            @Override
            public int getQueryTimeout() {
                return CommonConstants.QUERY_TIMEOUT_SEC;
            }

            @Override
            public int getBatchQueryTimeout() {
                return sessionMgrConf.getBatchQueryTimeout();
            }
        });

        if(dbDataSource.getConnectionURL().contains(com.elitecore.corenetvertex.constants.CommonConstants.VOLTDB)){
            return createVoltDBSessionDao(serverContext, sessionConfiguration);
        } else{
            return createRelationalDBSessionDao(serverContext, sessionConfiguration);
        }

    }

    private SessionDao createRelationalDBSessionDao(NetVertexServerContext serverContext, SessionConfigurationImpl sessionConfiguration)
            throws InitializationFailedException {
        return new RelationalDBSessionDao(serverContext, sessionConfiguration);
    }

    private SessionDao createVoltDBSessionDao(NetVertexServerContext serverContext, SessionConfigurationImpl sessionConfiguration)
            throws InitializationFailedException {
        List<DBDataSource> dbDataSources = sessionConfiguration.getDataSources();
        DBDataSource dbDataSource = dbDataSources.get(0);
        VoltDBClientManager voltDBClientManager = serverContext.getVoltDBClientManager();
        VoltDBClient voltDBClient = new VoltDBClientAdapter(voltDBClientManager.getOrCreateClient(dbDataSource, serverContext.getTaskScheduler()));
        return new VoltDBSessionDao(serverContext, sessionConfiguration, voltDBClient);
    }

    protected @Nullable SessionConfigurationImpl.SQLDialectFactory createDialectFactory() {
        return null;
    }

}
