package com.elitecore.nvsmx.system.spr;


import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.voltdb.VoltDBClient;
import com.elitecore.core.commons.util.voltdb.VoltDBClientManager;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.commons.utilx.db.TransactionFactoryGroupImpl;
import com.elitecore.core.commons.utilx.ldap.LDAPConnectionManager;
import com.elitecore.core.systemx.esix.LoadBalancerType;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.SpInterfaceType;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.db.DBDataSource;
import com.elitecore.corenetvertex.core.ldap.LDAPDataSource;
import com.elitecore.corenetvertex.spr.*;
import com.elitecore.corenetvertex.spr.data.SubscriberRepositoryConfiguration;
import com.elitecore.corenetvertex.spr.data.impl.DBSPInterfaceConfigurationImpl;
import com.elitecore.corenetvertex.spr.data.impl.LDAPSPInterfaceConfigurationImpl;
import com.elitecore.corenetvertex.spr.exceptions.InitializationFailedException;
import com.elitecore.corenetvertex.spr.voltdb.VoltDBSPInterface;
import com.elitecore.corenetvertex.spr.voltdb.VoltSubscriberRepositoryImpl;
import com.elitecore.corenetvertex.spr.voltdb.VoltUMOperation;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.alert.AlertListenerImpl;
import com.elitecore.nvsmx.system.db.DBDatasourceImpl;
import com.elitecore.nvsmx.system.db.NVSMXDBConnectionManager;
import com.elitecore.nvsmx.system.driver.cdr.BalanceEDRListener;
import com.elitecore.nvsmx.system.driver.cdr.SubscriberEDRListener;
import com.elitecore.nvsmx.system.driver.cdr.SubscriptionEDRListener;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.system.scheduler.EliteScheduler;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;
public class SPRProviderImpl implements SPRProvider {


	private static final String MODULE = "SPR-PROVIDER";
	private static final int DEFAULT_ABMF_BATCH_SIZE_NVSMX = 1;
	private static final String DEFAULT_ID = "0";
	private static final String DEFAULT_NAME = "defaultSPR";
	private AlertListener dummyAlertListener = new AlertListenerImpl();
	private TestSubscriberCache testSubscriberCache;
	private Map<String, SubscriberRepository> subscriberRepositoryById = new HashMap<>(10);

	public SPRProviderImpl(TestSubscriberCache testSubscriberCache) {
		this.testSubscriberCache = testSubscriberCache;
	}

	@Override
	public SubscriberRepository getDefaultRepository() {

		if (subscriberRepositoryById.containsKey(DEFAULT_ID)) {
			return subscriberRepositoryById.get(DEFAULT_ID);
		}

		UMOperation umOperation = new UMOperation(dummyAlertListener, DefaultNVSMXContext.getContext().getPolicyRepository());
        SubscriberRepository defaultSubscriberRepository = new SubscriberRepositoryImpl(
                DEFAULT_ID,
                DEFAULT_NAME,
                getTransactionFactoryAdapter(NVSMXDBConnectionManager.getInstance().getTransactionFactory()),
                dummyAlertListener,
                DefaultNVSMXContext.getContext().getPolicyRepository()
                , umOperation
                , new ABMFconfigurationImpl(DEFAULT_ABMF_BATCH_SIZE_NVSMX, CommonConstants.DEFAULT_BATCH_QUERY_TIMEOUT, CommonConstants.QUERY_TIMEOUT_DEFAULT)
                , testSubscriberCache
                , Collections.<String>emptyList()
                , null,
                createABMFCSVFailOperation(),
                createMonetaryABMFCSVFailOperation(),
				createRnCABMFCSVFailOperation(),
				new BalanceEDRListener(),
				new SubscriberEDRListener(),
				new SubscriptionEDRListener(), SystemParameterDAO.getCurrency());
		
		subscriberRepositoryById.put(DEFAULT_ID, defaultSubscriberRepository);
		
		return defaultSubscriberRepository;
	}

	private TransactionFactoryAdapter getTransactionFactoryAdapter(TransactionFactory transactionFactory) {
		
		TransactionFactoryGroupImpl transactionFactoryGroup = new TransactionFactoryGroupImpl(LoadBalancerType.SWITCH_OVER);
		transactionFactoryGroup.addCommunicator(transactionFactory);
		
		return new TransactionFactoryAdapter(transactionFactoryGroup);
	}

	@Override
	public Collection<SubscriberRepository> getAllSubscriberRepository() {
		return subscriberRepositoryById.values();
	}

	private RecordProcessor<ABMFBatchOperation.BatchOperationData> createABMFCSVFailOperation() {
		return new RecordProcessor.EmptyRecordProcessor<>();
	}

	private RecordProcessor<RnCABMFBatchOperation.BatchOperationData> createRnCABMFCSVFailOperation() {
		return new RecordProcessor.EmptyRecordProcessor<>();
	}

	private RecordProcessor<MonetaryABMFOperationImpl.MonetaryOperationData> createMonetaryABMFCSVFailOperation() {
		return new RecordProcessor.EmptyRecordProcessor<>();
	}

	private RecordProcessor<VoltUMOperation.VoltUMOperationData> createVoltUMCSVFailOperation() {
		return new RecordProcessor.EmptyRecordProcessor<>();
	}

	@Override
	public SubscriberRepository getRepository(SubscriberRepositoryConfiguration sprData) {

		if (subscriberRepositoryById.containsKey(sprData.getSprId())) {
			return subscriberRepositoryById.get(sprData.getSprId());
		}

		SubscriberRepositoryConfiguration subscriberRepositoryData = sprData;

		DBDatasourceImpl databaseDSData = (DBDatasourceImpl) subscriberRepositoryData.getDbDataSource();

		if (databaseDSData == null) {
		    if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "DataSource not found for spr: " + subscriberRepositoryData.getSprName());
            }
			return null;
		}

		SubscriberRepository subscriberRepository;

		if (databaseDSData.getConnectionURL().contains(CommonConstants.VOLTDB)) {
			subscriberRepository = createVoltDBSubscriberRepository(subscriberRepositoryData, databaseDSData);
		} else {
			subscriberRepository = createSubscriberRepository(sprData, subscriberRepositoryData, databaseDSData);
		}

        if (subscriberRepository == null) {
            return null;
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Subscriber Repository: " + subscriberRepositoryData.getSprName()  + " initialized successfully");
        }

		subscriberRepositoryById.put(sprData.getSprId(), subscriberRepository);

		return subscriberRepository;
	}

	private SubscriberRepository createVoltDBSubscriberRepository(SubscriberRepositoryConfiguration subscriberRepoConf, DBDataSource dbDataSource) {

		VoltDBClientManager voltDBClientManager = DefaultNVSMXContext.getContext().getVoltDBClientManager();
		VoltDBClient voltDBClient = null;
		try {
			voltDBClient = voltDBClientManager.getOrCreateClient((com.elitecore.core.commons.util.db.datasource.DBDataSource) dbDataSource, DefaultNVSMXContext.getContext().getTaskScheduler());
		} catch (com.elitecore.core.commons.InitializationFailedException e) {
			getLogger().error(MODULE, "Error while creating VoltDB Client. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
		com.elitecore.core.commons.util.db.datasource.DBDataSource nvsmxDBDataSource = NVSMXDBConnectionManager.getInstance().getDataSource();

		TransactionFactoryGroupImpl configurationDBTransactionFactoryGroup = createTransactionFactoryGroup(nvsmxDBDataSource);
		if (configurationDBTransactionFactoryGroup == null){
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Unable to create Subscriber Repository. Reason: Transaction factory not found for  data source "+ nvsmxDBDataSource.getDataSourceName());
			}
			return null;
		}

		TestSubscriberAwareSPInterface spInterface;

		if (testSubscriberCache != null) {
			spInterface = new TestSubscriberEnabledSPInterface(new VoltDBSPInterface(dummyAlertListener,new VoltDBClientAdapter(voltDBClient),TimeSource.systemTimeSource()), DefaultNVSMXContext.getContext().getPolicyRepository(),
					testSubscriberCache);
		} else {
			spInterface = new TestSubscriberDisabledSPInterface(new VoltDBSPInterface(dummyAlertListener,new VoltDBClientAdapter(voltDBClient),TimeSource.systemTimeSource()));
		}

		return new VoltSubscriberRepositoryImpl(spInterface,
				subscriberRepoConf.getSprId(),
				subscriberRepoConf.getSprName(),
				new VoltDBClientAdapter(voltDBClient),
				dummyAlertListener,
				DefaultNVSMXContext.getContext().getPolicyRepository(),
				subscriberRepoConf.getGroupIds(),
				subscriberRepoConf.getAlternateIdField(), null,
				VoltUMOperation.create(dummyAlertListener, DefaultNVSMXContext.getContext().getPolicyRepository(), TimeSource.systemTimeSource(),
						createVoltUMCSVFailOperation()),
				TimeSource.systemTimeSource(), SystemParameterDAO.getCurrency(),new SubscriptionEDRListener(), new BalanceEDRListener(), new SubscriberEDRListener());
	}

	private TransactionFactoryGroupImpl createTransactionFactoryGroup(com.elitecore.core.commons.util.db.datasource.DBDataSource dbDataSource) {
		DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance(dbDataSource.getDataSourceName());

		if (dbConnectionManager.isInitilized() == false) {

			try {
				dbConnectionManager.init(dbDataSource, EliteScheduler.getInstance().getTaskSchedular());
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

	private SubscriberRepository createSubscriberRepository(SubscriberRepositoryConfiguration sprData, SubscriberRepositoryConfiguration subscriberRepositoryData,
															DBDatasourceImpl databaseDSData) {

		TransactionFactoryGroupImpl transactionFactoryGroup = createTransactionFactoryGroup(databaseDSData);
		if(transactionFactoryGroup == null && getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Unable to create Subscriber Repository. Reason: Transaction factory not found for  data source "+databaseDSData.getDataSourceName());

		}


		UMOperation umOperation = new UMOperation(dummyAlertListener, DefaultNVSMXContext.getContext().getPolicyRepository());
		SubscriberRepository subscriberRepository;
		// isLocal SPR configured
		if (subscriberRepositoryData.getSpInterfaceConfiguration() == null) {

			subscriberRepository = new SubscriberRepositoryImpl(subscriberRepositoryData.getSprId(),
					subscriberRepositoryData.getSprName(),
					new TransactionFactoryAdapter(transactionFactoryGroup),
					dummyAlertListener,
					DefaultNVSMXContext.getContext().getPolicyRepository(),
					umOperation,
					subscriberRepositoryData.getAbmFconfiguration(),
					testSubscriberCache,
					sprData.getGroupIds(),
					subscriberRepositoryData.getAlternateIdField(),
					createABMFCSVFailOperation(),
					createMonetaryABMFCSVFailOperation(),
					createRnCABMFCSVFailOperation(),
					new BalanceEDRListener(),
					new SubscriberEDRListener(),
					new SubscriptionEDRListener(), SystemParameterDAO.getCurrency());
		}  else {


			if (SpInterfaceType.DB_SP_INTERFACE == subscriberRepositoryData.getSpInterfaceConfiguration().getType()) {

				subscriberRepository = createSubscriberRepositoryForDBSPInterface(sprData, subscriberRepositoryData, transactionFactoryGroup, umOperation);

			} else {
				subscriberRepository = createSubscriberRepositoryForLDAPSPInterface(subscriberRepositoryData, transactionFactoryGroup, umOperation);
			}

			if (subscriberRepository == null){
				return null;
			}
		}

		return subscriberRepository;
	}

	private SubscriberRepository createSubscriberRepositoryForLDAPSPInterface(SubscriberRepositoryConfiguration subscriberRepositoryData, TransactionFactoryGroupImpl transactionFactoryGroup,
																			  UMOperation umOperation) {
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

		ExternalLDAPSPInterface externalLDAPSPInterface = new ExternalLDAPSPInterface(ldapSPInterfaceConfiguration, dummyAlertListener, new LDAPConnectionProvider() {
            @Override
            public LDAPConnection getConnection() throws LDAPException {
                return LDAPConnectionManager.getInstance(ldapDataSource.getDataSourceName()).getConnection();
            }

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

		return new SubscriberRepositoryImpl(subscriberRepositoryData.getSprId(),
                subscriberRepositoryData.getSprName(),
                new TransactionFactoryAdapter(transactionFactoryGroup),
                dummyAlertListener,
                externalLDAPSPInterface,
                DefaultNVSMXContext.getContext().getPolicyRepository(),
                umOperation,
                subscriberRepositoryData.getAbmFconfiguration(),
                testSubscriberCache, subscriberRepositoryData.getGroupIds(),
                subscriberRepositoryData.getAlternateIdField(),
                createABMFCSVFailOperation(),
                createMonetaryABMFCSVFailOperation(),
                createRnCABMFCSVFailOperation(),
				new BalanceEDRListener(),
				new SubscriberEDRListener(),
				new SubscriptionEDRListener(), SystemParameterDAO.getCurrency());

	}

	private SubscriberRepository createSubscriberRepositoryForDBSPInterface(SubscriberRepositoryConfiguration sprData, SubscriberRepositoryConfiguration subscriberRepositoryData, TransactionFactoryGroupImpl transactionFactoryGroup, UMOperation umOperation) {
		DBSPInterfaceConfigurationImpl dbSPInterfaceConfiguration = (DBSPInterfaceConfigurationImpl) subscriberRepositoryData.getSpInterfaceConfiguration();
		DBDataSource dbDataSource = dbSPInterfaceConfiguration.getDbDataSource();
		DBConnectionManager spInterfaceDBConnectionManager = DBConnectionManager.getInstance(dbDataSource.getDataSourceName());

		if (spInterfaceDBConnectionManager.isInitilized() == false) {
            try {
                spInterfaceDBConnectionManager.init((com.elitecore.core.commons.util.db.datasource.DBDataSource) dbDataSource, DefaultNVSMXContext.getContext().getTaskScheduler());
            } catch (DatabaseInitializationException ex) {
                getLogger().error(MODULE, "Error while initializing DB data source: " + dbDataSource.getDataSourceName()
                        + ". Reason:" + ex.getMessage());
                getLogger().trace(MODULE, ex);
            } catch (DatabaseTypeNotSupportedException ex) {
                getLogger().error(MODULE, "Error while initializing DB data source: " + dbDataSource.getDataSourceName() + ". Reason: "
                        + ex.getMessage());
                getLogger().trace(ex);
				return null;
            }
        }

		TransactionFactoryGroupImpl spInterfaceTransactionFactoryGroup = new TransactionFactoryGroupImpl(LoadBalancerType.SWITCH_OVER);
		spInterfaceTransactionFactoryGroup.addCommunicator(spInterfaceDBConnectionManager.getTransactionFactory());

		ExternalDBSPInterface externalDBSPInterface = new ExternalDBSPInterface(dbSPInterfaceConfiguration,
                dummyAlertListener, new TransactionFactoryAdapter(spInterfaceTransactionFactoryGroup));

		try {
            externalDBSPInterface.init();
        } catch (InitializationFailedException e) {
            getLogger().error(MODULE, "Error while initializing external DB SPR. Reason: " + e.getMessage());
            getLogger().trace(e);
			return null;
        }

		return new SubscriberRepositoryImpl(subscriberRepositoryData.getSprId(),
                subscriberRepositoryData.getSprName(),
                new TransactionFactoryAdapter(transactionFactoryGroup),
                dummyAlertListener,
                externalDBSPInterface,
                DefaultNVSMXContext.getContext().getPolicyRepository(),
                umOperation,
                subscriberRepositoryData.getAbmFconfiguration(),
                testSubscriberCache,
                sprData.getGroupIds(), subscriberRepositoryData.getAlternateIdField(),
                createABMFCSVFailOperation(),
                createMonetaryABMFCSVFailOperation(),
                createRnCABMFCSVFailOperation(),
				new BalanceEDRListener(),
				new SubscriberEDRListener(),
				new SubscriptionEDRListener(), SystemParameterDAO.getCurrency());
	}

}