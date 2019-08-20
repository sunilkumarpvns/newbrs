package com.elitecore.netvertex.core.ddf;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.voltdb.VoltDBClient;
import com.elitecore.core.commons.util.voltdb.VoltDBClientManager;
import com.elitecore.core.commons.utilx.db.TransactionFactoryGroupImpl;
import com.elitecore.core.commons.utilx.ldap.LDAPConnectionManager;
import com.elitecore.core.systemx.esix.LoadBalancerType;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.SpInterfaceType;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.db.DBDataSource;
import com.elitecore.corenetvertex.core.ldap.LDAPDataSource;
import com.elitecore.corenetvertex.spr.ABMFBatchOperation;
import com.elitecore.corenetvertex.spr.ABMFconfigurationImpl;
import com.elitecore.corenetvertex.spr.ExternalDBSPInterface;
import com.elitecore.corenetvertex.spr.ExternalLDAPSPInterface;
import com.elitecore.corenetvertex.spr.LDAPConnectionProvider;
import com.elitecore.corenetvertex.spr.MonetaryABMFOperationImpl;
import com.elitecore.corenetvertex.spr.RecordProcessor;
import com.elitecore.corenetvertex.spr.RnCABMFBatchOperation;
import com.elitecore.corenetvertex.spr.SPRProvider;
import com.elitecore.corenetvertex.spr.SubscriberRepository;
import com.elitecore.corenetvertex.spr.SubscriberRepositoryImpl;
import com.elitecore.corenetvertex.spr.TestSubscriberAwareSPInterface;
import com.elitecore.corenetvertex.spr.TestSubscriberDisabledSPInterface;
import com.elitecore.corenetvertex.spr.UMBatchOperation.BatchOperationData;
import com.elitecore.corenetvertex.spr.UMOperation;
import com.elitecore.corenetvertex.spr.UMOperationBuilder;
import com.elitecore.corenetvertex.spr.UMconfigurationImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberRepositoryConfiguration;
import com.elitecore.corenetvertex.spr.data.impl.DBSPInterfaceConfigurationImpl;
import com.elitecore.corenetvertex.spr.data.impl.LDAPSPInterfaceConfigurationImpl;
import com.elitecore.corenetvertex.spr.exceptions.InitializationFailedException;
import com.elitecore.corenetvertex.spr.voltdb.VoltDBSPInterface;
import com.elitecore.corenetvertex.spr.voltdb.VoltSubscriberRepositoryImpl;
import com.elitecore.corenetvertex.spr.voltdb.VoltUMOperation;
import com.elitecore.netvertex.core.NetVertexDBConnectionManager;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.conf.impl.DBDataSourceImpl;
import com.elitecore.netvertex.core.driver.cdr.ABMFCSVDataBuilder;
import com.elitecore.netvertex.core.driver.cdr.BalanceEDRListener;
import com.elitecore.netvertex.core.driver.cdr.CSVFailOverDriver;
import com.elitecore.netvertex.core.driver.cdr.MonetaryABMFCSVDataBuilder;
import com.elitecore.netvertex.core.driver.cdr.RnCABMFCSVDataBuilder;
import com.elitecore.netvertex.core.driver.cdr.SubscriberEDRListener;
import com.elitecore.netvertex.core.driver.cdr.SubscriptionEDRListener;
import com.elitecore.netvertex.core.driver.cdr.SubscriberEDRListener;
import com.elitecore.netvertex.core.driver.cdr.BalanceEDRListener;
import com.elitecore.netvertex.core.driver.cdr.UMCSVDataBuilder;
import com.elitecore.netvertex.core.driver.cdr.VoltUMCSVDataBuilder;
import com.elitecore.netvertex.core.transaction.TransactionFactoryAdapter;
import com.elitecore.netvertex.core.voltdb.VoltDBClientAdapter;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * SPRProvider gives SubscriberRepository by provided sprId
 * 
 * @author Chetan.Sankhala
 */
class SPRProviderImpl implements SPRProvider {

	private static final String DEFAULT_NAME = "defaultSPR";
	private static final String DEFAULT_ID = "0";
	private static final String MODULE = "SPR-PROVIDER";

	private final NetVertexServerContext serverContext;
	private final AlertListener alertListener;
	private Map<String, SubscriberRepository> subscriberRepositoryById;

	public SPRProviderImpl(NetVertexServerContext netVertexServerContext) {
		this.serverContext = netVertexServerContext;
		this.alertListener = new AlertListenerImpl();
		this.subscriberRepositoryById = new HashMap<>(10);
	}

	/**
	 * 
	 * This is 
	 * 
	 * @author Chetan.Sankhala
	 */
	private class AlertListenerImpl implements com.elitecore.corenetvertex.core.alerts.AlertListener {

		@Override
		public void generateSystemAlert(String severity, com.elitecore.corenetvertex.core.alerts.Alerts alertEnum, String alertGeneratorIdentity,
				String alertMessage) {
			serverContext
					.generateSystemAlert(severity, Alerts.fromCoreNetvertexAlert(alertEnum), alertGeneratorIdentity, alertMessage);
		}

		@Override
		public void generateSystemAlert(String severity, com.elitecore.corenetvertex.core.alerts.Alerts alertEnum, String alertGeneratorIdentity,
				String alertMessage, int alertIntValue, String alertStringValue) {
			serverContext
					.generateSystemAlert(severity, Alerts.fromCoreNetvertexAlert(alertEnum), alertGeneratorIdentity, alertMessage, alertIntValue, alertStringValue);
		}
	}

	private RecordProcessor<BatchOperationData> createCSVRecordProcessor(String sprName) {
		CSVFailOverDriver<BatchOperationData> driver = new CSVFailOverDriver<>(serverContext.getServerHome(), sprName + "-UM", new UMCSVDataBuilder(),
				serverContext.getTaskScheduler());
		try {
			driver.init();
		} catch (DriverInitializationFailedException e) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "CSV FailOver Driver initialization failed for SPR : " + sprName + ". Reason : " + e.getMessage());
			}
			getLogger().trace(MODULE, e);
			return new RecordProcessor.EmptyRecordProcessor<>();
		}
		return driver;
	}

	private RecordProcessor<VoltUMOperation.VoltUMOperationData> createVoltUMCSVRecordProcessor(String sprName) {
		CSVFailOverDriver<VoltUMOperation.VoltUMOperationData> driver = new CSVFailOverDriver<>(serverContext.getServerHome(), sprName + "-VOLT-UM",
				new VoltUMCSVDataBuilder(), serverContext.getTaskScheduler());

		try {
			driver.init();
		} catch (DriverInitializationFailedException e) {
			generateLog(sprName, e);
			return new RecordProcessor.EmptyRecordProcessor<>();
		}

		return driver;
	}

	private void generateLog(String sprName, DriverInitializationFailedException e) {
		if (getLogger().isWarnLogLevel()) {
			getLogger().warn(MODULE, "CSV FailOver Driver initialization failed for SPR : " + sprName + ". Reason : " + e.getMessage());
		}
		getLogger().trace(MODULE, e);
	}

	private RecordProcessor<ABMFBatchOperation.BatchOperationData> createABMFCSVRecordProcessor(String sprName) {
		CSVFailOverDriver<ABMFBatchOperation.BatchOperationData> driver = new CSVFailOverDriver<>(serverContext.getServerHome(), sprName + "-ABMF-NON-MONITORY",
				new ABMFCSVDataBuilder(), serverContext.getTaskScheduler());

		try {
			driver.init();
		} catch (DriverInitializationFailedException e) {
			generateLog(sprName, e);
			return new RecordProcessor.EmptyRecordProcessor<>();
		}

		return driver;
	}

	private RecordProcessor<RnCABMFBatchOperation.BatchOperationData> createRnCABMFCSVRecordProcessor(String sprName) {
		CSVFailOverDriver<RnCABMFBatchOperation.BatchOperationData> driver = new CSVFailOverDriver<>(serverContext.getServerHome(), sprName + "-RNC-ABMF-NON-MONITORY",
				new RnCABMFCSVDataBuilder(), serverContext.getTaskScheduler());

		try {
			driver.init();
		} catch (DriverInitializationFailedException e) {
			generateLog(sprName, e);
			return new RecordProcessor.EmptyRecordProcessor<>();
		}

		return driver;
	}

	private RecordProcessor<MonetaryABMFOperationImpl.MonetaryOperationData> createMonetaryABMFCSVRecordProcessor(String sprName) {
		CSVFailOverDriver<MonetaryABMFOperationImpl.MonetaryOperationData> driver = new CSVFailOverDriver<>(serverContext.getServerHome(), sprName + "-ABMF-MONETARY",
				new MonetaryABMFCSVDataBuilder(), serverContext.getTaskScheduler());

		try {
			driver.init();
		} catch (DriverInitializationFailedException e) {
			generateLog(sprName, e);
			return new RecordProcessor.EmptyRecordProcessor<>();
		}

		return driver;
	}

	@Override
	public SubscriberRepository getRepository(SubscriberRepositoryConfiguration sprData) {

		if (subscriberRepositoryById.containsKey(sprData.getSprId())) {
			return subscriberRepositoryById.get(sprData.getSprId());
		}

		SubscriberRepositoryConfiguration subscriberRepositoryData = sprData;

		DBDataSourceImpl databaseDSData = (DBDataSourceImpl) subscriberRepositoryData.getDbDataSource();

		if (databaseDSData == null) {
			getLogger().debug(MODULE, "DataSource not found for spr: " + subscriberRepositoryData.getSprName());
			return null;
		}

		SubscriberRepository subscriberRepository;

		if (databaseDSData.getConnectionURL().contains(CommonConstants.VOLTDB)) {

			subscriberRepository = createVoltDBSubscriberRepository(subscriberRepositoryData, databaseDSData);

		} else {

			TransactionFactoryGroupImpl transactionFactoryGroup = createTransactionFactoryGroup(databaseDSData);

			TransactionFactoryAdapter transactionFactoryAdapter = new TransactionFactoryAdapter(transactionFactoryGroup);

			UMOperation umOperation = new UMOperationBuilder(alertListener, serverContext.getPolicyRepository(), sprData.getSprName())
					.withTransactionFactory(transactionFactoryAdapter)
					.withUMConf(subscriberRepositoryData.getUmConfiguration())
					.withRecordProcessor(createCSVRecordProcessor(subscriberRepositoryData.getSprName()))
					.build();

			subscriberRepository = createSubscriberRepository(sprData, subscriberRepositoryData, transactionFactoryGroup, transactionFactoryAdapter, umOperation);

			if (subscriberRepository == null) {
				umOperation.stop();
				return null;
			}
		}



        this.subscriberRepositoryById.put(sprData.getSprId(), subscriberRepository);
        return subscriberRepository;
	}

    /**
     * Default SusbcriberRepository will be created using NetVertex default datasource.
     */
    @Override
    public SubscriberRepository getDefaultRepository() {

        if (subscriberRepositoryById.containsKey(DEFAULT_ID)) {
            return subscriberRepositoryById.get(DEFAULT_ID);
        }

        TransactionFactoryGroupImpl transactionFactoryGroup = new TransactionFactoryGroupImpl(LoadBalancerType.SWITCH_OVER);
        transactionFactoryGroup.addCommunicator(NetVertexDBConnectionManager.getInstance().getTransactionFactory());

        UMOperation umOperation = new UMOperationBuilder(alertListener, serverContext.getPolicyRepository(), DEFAULT_NAME)
                .withTransactionFactory(new TransactionFactoryAdapter(transactionFactoryGroup))
                .withUMConf(new UMconfigurationImpl(CommonConstants.DEFAULT_BATCH_SIZE, CommonConstants.DEFAULT_BATCH_QUERY_TIMEOUT))
                .build();


        SubscriberRepository defaultSubscriberRepository = new SubscriberRepositoryImpl(DEFAULT_ID, DEFAULT_NAME,
                new TransactionFactoryAdapter(transactionFactoryGroup),
                alertListener, serverContext.getPolicyRepository(),
                umOperation,
                new ABMFconfigurationImpl(CommonConstants.DEFAULT_BATCH_SIZE,
                CommonConstants.DEFAULT_BATCH_QUERY_TIMEOUT,
                        CommonConstants.QUERY_TIMEOUT_DEFAULT),
                null, Collections.emptyList(),
                null,
                createABMFCSVRecordProcessor(DEFAULT_NAME),
                createMonetaryABMFCSVRecordProcessor(DEFAULT_NAME),
				createRnCABMFCSVRecordProcessor(DEFAULT_NAME),
				new BalanceEDRListener(),
				new SubscriberEDRListener(),
				new SubscriptionEDRListener(), serverContext.getServerConfiguration().getSystemParameterConfiguration().getSystemCurrency());


        subscriberRepositoryById.put(DEFAULT_ID, defaultSubscriberRepository);

        return defaultSubscriberRepository;
    }

	@Override
	public Collection<SubscriberRepository> getAllSubscriberRepository() {
		return subscriberRepositoryById.values();
	}

	private SubscriberRepository createVoltDBSubscriberRepository(SubscriberRepositoryConfiguration subscriberRepositoryData,
																  DBDataSource dbDataSource) {

		VoltDBClientManager voltDBClientManager = serverContext.getVoltDBClientManager();
		VoltDBClient voltDBClient = null;
		try {
			voltDBClient = voltDBClientManager.getOrCreateClient((com.elitecore.core.commons.util.db.datasource.DBDataSource) dbDataSource, serverContext.getTaskScheduler());
		} catch (com.elitecore.core.commons.InitializationFailedException e) {
			getLogger().error(MODULE, "Error while creating VoltDB Client. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}

		TestSubscriberAwareSPInterface spInterface = new TestSubscriberDisabledSPInterface(new VoltDBSPInterface(alertListener,new VoltDBClientAdapter(voltDBClient),
				TimeSource.systemTimeSource()));


		VoltUMOperation voltUMOperation = VoltUMOperation.create(alertListener, serverContext.getPolicyRepository(), TimeSource.systemTimeSource(),
				createVoltUMCSVRecordProcessor(subscriberRepositoryData.getSprName()));

		voltUMOperation.init();

		return new VoltSubscriberRepositoryImpl(spInterface, subscriberRepositoryData.getSprId(),
                subscriberRepositoryData.getSprName(),
                new VoltDBClientAdapter(voltDBClient),
                alertListener,
                serverContext.getPolicyRepository(),
                subscriberRepositoryData.getGroupIds(),
                subscriberRepositoryData.getAlternateIdField(),
				null,
				voltUMOperation,
				TimeSource.systemTimeSource(), serverContext.getServerConfiguration().getSystemParameterConfiguration().getSystemCurrency(),new SubscriptionEDRListener(), new BalanceEDRListener(), new SubscriberEDRListener());
	}

	private SubscriberRepository createSubscriberRepository(SubscriberRepositoryConfiguration sprData, SubscriberRepositoryConfiguration subscriberRepositoryData,
															TransactionFactoryGroupImpl transactionFactoryGroup, TransactionFactoryAdapter transactionFactoryAdapter, UMOperation umOperation) {

		SubscriberRepository subscriberRepository;
		// isLocal SPR configured
		if (subscriberRepositoryData.getSpInterfaceConfiguration() == null) {

			subscriberRepository = new SubscriberRepositoryImpl(subscriberRepositoryData.getSprId(),
					subscriberRepositoryData.getSprName(),
					transactionFactoryAdapter,
					alertListener,
					serverContext.getPolicyRepository(),
					umOperation,
					subscriberRepositoryData.getAbmFconfiguration(),
					null,
					sprData.getGroupIds(),
					subscriberRepositoryData.getAlternateIdField(),
					createABMFCSVRecordProcessor(subscriberRepositoryData.getSprName()),
					createMonetaryABMFCSVRecordProcessor(subscriberRepositoryData.getSprName()),
					createRnCABMFCSVRecordProcessor(subscriberRepositoryData.getSprName()),
					new BalanceEDRListener(),
					new SubscriberEDRListener(),
					new SubscriptionEDRListener(), serverContext.getServerConfiguration().getSystemParameterConfiguration().getSystemCurrency());
		}  else {


			if (SpInterfaceType.DB_SP_INTERFACE == subscriberRepositoryData.getSpInterfaceConfiguration().getType()) {

				subscriberRepository = createSubscriberRepositoryForDBSPInterface(sprData, subscriberRepositoryData, transactionFactoryGroup, umOperation);

			} else {

				subscriberRepository = createSubscriberRepositoryForLDAPSPInterface(subscriberRepositoryData, transactionFactoryAdapter, umOperation);
			}

			if (subscriberRepository == null){
				return null;
			}

		}

		subscriberRepositoryById.put(sprData.getSprId(), subscriberRepository);

		return subscriberRepository;

	}

	private SubscriberRepository createSubscriberRepositoryForLDAPSPInterface(SubscriberRepositoryConfiguration subscriberRepositoryData, TransactionFactoryAdapter transactionFactoryAdapter,
																			  UMOperation umOperation) {

    	SubscriberRepository subscriberRepository;

    	LDAPSPInterfaceConfigurationImpl ldapSPInterfaceConfiguration = (LDAPSPInterfaceConfigurationImpl) subscriberRepositoryData.getSpInterfaceConfiguration();

		LDAPDataSource ldapDataSource = ldapSPInterfaceConfiguration.getLdapDataSource();

		LDAPConnectionManager ldapConnectionManager = LDAPConnectionManager.getInstance(ldapDataSource.getDataSourceName());

		if (ldapConnectionManager.isInitialize() == false) {

            try {
                ldapConnectionManager.createLDAPConnectionPool((com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource) ldapDataSource, true);
            } catch (DriverInitializationFailedException e) {
                getLogger().error(MODULE, "Error while initializing DB data source: " + ldapDataSource.getDataSourceName() + ". Reason:"
                        + e.getMessage());
                getLogger().trace(MODULE, e);
            } catch (LDAPException e) {
                getLogger().error(MODULE, "Error while initializing DB data source: " + ldapDataSource.getDataSourceName() + ". Reason:"
                        + e.getMessage());
                getLogger().trace(MODULE, e);
				return null;
            }
        }

		ExternalLDAPSPInterface externalLDAPSPInterface = new ExternalLDAPSPInterface(ldapSPInterfaceConfiguration, alertListener, new LDAPConnectionProvider() {
            @Override
            public LDAPConnection getConnection() throws LDAPException {
                return LDAPConnectionManager.getInstance(ldapDataSource.getDataSourceName()).getConnection();
            }

            @Override
            public void close(LDAPConnection connection) {
                LDAPConnectionManager.getInstance(ldapDataSource.getDataSourceName()).closeConnection(connection);
            }
        });

		try {
            externalLDAPSPInterface.init();
        } catch (InitializationFailedException e) {
            getLogger().error(MODULE, "Error while initializing external LDAP SPR. Reason:"
                    + e.getMessage());
            getLogger().trace(MODULE, e);
			return null;
        }

        subscriberRepository = new SubscriberRepositoryImpl(subscriberRepositoryData.getSprId(),
                subscriberRepositoryData.getSprName(),
                transactionFactoryAdapter,
                alertListener,
                externalLDAPSPInterface,
                serverContext.getPolicyRepository(),
                umOperation,
                subscriberRepositoryData.getAbmFconfiguration(),
                null, subscriberRepositoryData.getGroupIds(),
                subscriberRepositoryData.getAlternateIdField(),
                createABMFCSVRecordProcessor(subscriberRepositoryData.getSprName()),
                createMonetaryABMFCSVRecordProcessor(subscriberRepositoryData.getSprName()),
                createRnCABMFCSVRecordProcessor(subscriberRepositoryData.getSprName()),
				new BalanceEDRListener(),
				new SubscriberEDRListener(),
				new SubscriptionEDRListener(), serverContext.getServerConfiguration().getSystemParameterConfiguration().getSystemCurrency());
		return subscriberRepository;
	}

	private SubscriberRepository createSubscriberRepositoryForDBSPInterface(SubscriberRepositoryConfiguration sprData, SubscriberRepositoryConfiguration subscriberRepositoryData,
																			TransactionFactoryGroupImpl transactionFactoryGroup, UMOperation umOperation) {
		SubscriberRepository subscriberRepository;

		DBSPInterfaceConfigurationImpl dbSPInterfaceConfiguration = (DBSPInterfaceConfigurationImpl) subscriberRepositoryData.getSpInterfaceConfiguration();
		DBDataSource dbDataSource = dbSPInterfaceConfiguration.getDbDataSource();

		TransactionFactoryGroupImpl spInterfaceTransactionFactoryGroup = createTransactionFactoryGroup((DBDataSourceImpl) dbDataSource);

		ExternalDBSPInterface externalDBSPInterface = new ExternalDBSPInterface(dbSPInterfaceConfiguration,
                alertListener, new TransactionFactoryAdapter(spInterfaceTransactionFactoryGroup));

		try {
            externalDBSPInterface.init();
        } catch (InitializationFailedException e) {
            getLogger().error(MODULE, "Error while initializing external DB SPR. Reason: " + e.getMessage());
            getLogger().trace(e);
			return null;
        }

		subscriberRepository = new SubscriberRepositoryImpl(subscriberRepositoryData.getSprId(),
                subscriberRepositoryData.getSprName(),
                new TransactionFactoryAdapter(transactionFactoryGroup),
                alertListener,
                externalDBSPInterface,
                serverContext.getPolicyRepository(),
                umOperation,
                subscriberRepositoryData.getAbmFconfiguration(),
                null,
                sprData.getGroupIds(),
                subscriberRepositoryData.getAlternateIdField(),
                createABMFCSVRecordProcessor(subscriberRepositoryData.getSprName()),
                createMonetaryABMFCSVRecordProcessor(subscriberRepositoryData.getSprName()),
                createRnCABMFCSVRecordProcessor(subscriberRepositoryData.getSprName()),
				new BalanceEDRListener(),
				new SubscriberEDRListener(),
				new SubscriptionEDRListener(), serverContext.getServerConfiguration().getSystemParameterConfiguration().getSystemCurrency());
		return subscriberRepository;
	}

	private TransactionFactoryGroupImpl createTransactionFactoryGroup(DBDataSourceImpl dbDataSource) {
		DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance(dbDataSource.getDataSourceName());

		if (dbConnectionManager.isInitilized() == false) {

			try {
				dbConnectionManager.init(dbDataSource, serverContext.getTaskScheduler());
			} catch (DatabaseInitializationException ex) {
				getLogger().error(MODULE, "Error while initializing DB data source: " + dbDataSource.getDataSourceName() + ". Reason: "
						+ ex.getMessage());
				getLogger().trace(MODULE, ex);
			} catch (DatabaseTypeNotSupportedException ex) {
				getLogger().error(MODULE, "Error while initializing DB data source: " + dbDataSource.getDataSourceName() + ". Reason: "
						+ ex.getMessage());
				getLogger().trace(ex);
				return null;
			}
		}

		TransactionFactoryGroupImpl transactionFactoryGroup = new TransactionFactoryGroupImpl(LoadBalancerType.SWITCH_OVER);
		transactionFactoryGroup.addCommunicator(dbConnectionManager.getTransactionFactory());
		return transactionFactoryGroup;
	}
}