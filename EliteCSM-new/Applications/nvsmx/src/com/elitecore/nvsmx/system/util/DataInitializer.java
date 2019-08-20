package com.elitecore.nvsmx.system.util;

import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.util.voltdb.VoltDBClientManager;
import com.elitecore.core.commons.utilx.ldap.LDAPConnectionManager;
import com.elitecore.core.serverx.snmp.EliteSnmpAgent;
import com.elitecore.core.servicex.EliteAdminService;
import com.elitecore.core.util.cli.cmd.ClearCommand;
import com.elitecore.core.util.cli.cmd.ICommand;
import com.elitecore.core.util.cli.cmd.ShowCommand;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.core.db.DBDataSource;
import com.elitecore.corenetvertex.core.ldap.LDAPDataSource;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.ldap.LdapData;
import com.elitecore.corenetvertex.pm.HibernateConfigurationUtil;
import com.elitecore.corenetvertex.pm.HibernateDataReader;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.rnc.RnCFactory;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationSchemeFactory;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackageFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardVersionFactory;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroupFactory;
import com.elitecore.corenetvertex.sm.acl.LdapAuthConfigurationData;
import com.elitecore.corenetvertex.sm.systemparameter.SystemParameter;
import com.elitecore.corenetvertex.spr.LDAPConnectionProvider;
import com.elitecore.corenetvertex.util.DataSourceProvider;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.nvsmx.Version;
import com.elitecore.nvsmx.policydesigner.model.subscriber.SubscriberDAO;
import com.elitecore.nvsmx.remotecommunications.EndPointFactory;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.NetvertexInstanceGroupConfiguration;
import com.elitecore.nvsmx.remotecommunications.NvsmxInstanceConfiguration;
import com.elitecore.nvsmx.remotecommunications.RMIGroupFactory;
import com.elitecore.nvsmx.remotecommunications.RMIGroupManager;
import com.elitecore.nvsmx.remotecommunications.UpdatePDStatusTask;
import com.elitecore.nvsmx.remotecommunications.data.EndPointStatus;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.NETVERTEX_PCRF_MIBImpl;
import com.elitecore.nvsmx.staff.StaffLDAPConfiguration;
import com.elitecore.nvsmx.staff.StaffLDAPInterface;
import com.elitecore.nvsmx.system.ConfigurationProvider;
import com.elitecore.nvsmx.system.cli.ESICommand;
import com.elitecore.nvsmx.system.cli.ResetStatisticsProvider;
import com.elitecore.nvsmx.system.cli.StatisticsDetailProvider;
import com.elitecore.nvsmx.system.cli.SubscriberImportCommand;
import com.elitecore.nvsmx.system.cli.WebServiceStatisticsDetailProvider;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.db.DBDatasourceImpl;
import com.elitecore.nvsmx.system.db.NVSMXDBConnectionManager;
import com.elitecore.nvsmx.system.driver.cdr.BalanceEDRLineBuilder;
import com.elitecore.nvsmx.system.driver.cdr.EDRDriver;
import com.elitecore.nvsmx.system.driver.cdr.EDRDriverManager;
import com.elitecore.nvsmx.system.driver.cdr.ExternalAlternateIdEDRLineBuilder;
import com.elitecore.nvsmx.system.driver.cdr.SubscriberEDRLineBuilder;
import com.elitecore.nvsmx.system.driver.cdr.SubscriptionEDRLineBuilder;
import com.elitecore.nvsmx.system.groovy.GroovyManager;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.jmx.SubscriberImportController;
import com.elitecore.nvsmx.system.ldap.LDAPDataSourceImpl;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.system.listeners.PDContextReader;
import com.elitecore.nvsmx.system.scheduler.EliteScheduler;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatisticsManager;
import com.elitecore.nvsmx.ws.internal.blmanager.ServerStatusWSBLManager;
import com.elitecore.nvsmx.ws.util.AlternateIdOperationUtils;
import com.sun.management.snmp.SnmpOid;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import org.hibernate.cfg.Configuration;

import javax.annotation.Nullable;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.Executors;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Used to initialize data which are required on startup
 * @author dhyani.raval
 */
public class DataInitializer {

    private static final String MODULE = "DATA-INITIALIZER";
    private static DataInitializer dataInitializer;

    private static final String SYSTEM_PATH = "system";
    private static final String SYS_INIT_FILE = "_sys.init";
    public static final String NAME = "Policy Designer";


    private String deploymentPath = null;
    private EliteAdminService adminService;
    private static final String NETVERTEX_OID = "1.3.6.1.4.1.21067.4";
    private EliteSnmpAgent snmpAgent;
    private static final String  CONFIG_FILE_LOCATION;
    private EndPointFactory endPointFactory;
    private PDContextReader pdContextReader;
    private ServerStatusWSBLManager serverStatusWSBLManager;
    private StaffLDAPInterface staffLDAPInterface;

    static {
        dataInitializer = new DataInitializer();
        CONFIG_FILE_LOCATION="hibernate.cfg.xml";
    }

    public static DataInitializer getInstance() {
        return dataInitializer;
    }

    public void init() throws Exception {

        deploymentPath = DefaultNVSMXContext.getContext().getServerHome();

        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Method called init()");
        }

        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Data Initialization started");
        }

        initializeNVSMXConnectionManager(DefaultNVSMXContext.getContext().getServerHome());

        initializeHibernateSessionFactory();

        if (HibernateSessionFactory.isInitialized() == false) {

            if(getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping Data initialization. Reason: Hibernate session factory is not initialized");
            }
            throw new Exception("Hibernate session factory is not initialized");
        }

        initializeSystemParameter();

        initializeRMIInterface(DefaultNVSMXContext.getContext().getContextPath());

        initializePolicyManager();

        initializeGroovyManager();

        DefaultNVSMXContext.getContext().setVoltDBClientManager(new VoltDBClientManager());
        initializeSubscriberDAO();

        initializeAdminService();

        initializeSubscriberImportController();

        initializeWebServiceStaticsManager();

        initializeESICommand();

        initializeSNMPAgent();

        initializeLDAPForStaff();

        registerNetvertexMIBWithSNMPAgent();

        initializeEDRDriver();
        initializeAlternateIdUtility();

        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Initialized Startup Data Successfully");
        }

    }

    private void initializeAlternateIdUtility() {
        try {
            DefaultNVSMXContext.getContext().setAlternateIdOperationUtils(AlternateIdOperationUtils.create(EndPointManager.getInstance()));
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while initializing alternate id operation utility");
            getLogger().trace(MODULE, e);
        }
    }

    private void initializeLDAPForStaff() throws Exception {

        LdapAuthConfigurationData ldapAuthConfigurationData = (LdapAuthConfigurationData) CRUDOperationUtil.getDataFromDB(LdapAuthConfigurationData.class,"LDAP_AUTH_CONF_1");
        if(ldapAuthConfigurationData == null){
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Ldap Authentication can't be initialize for staff.Reason Ldap configuration not found.");
            }
            return;
        }
        StaffLDAPConfiguration staffLDAPConfiguration = StaffLDAPConfiguration.create(ldapAuthConfigurationData, new FailReason(""), new DataSourceProvider() {
            @Override
            public DBDataSource getDBDataSource(DatabaseData databaseData, FailReason failReason) {
                return DBDatasourceImpl.create(databaseData, failReason);
            }

            @Override
            public LDAPDataSource getLDAPDataSource(LdapData ldapData, FailReason failReason) {
                return LDAPDataSourceImpl.create(ldapData, failReason);
            }
        });

        if(staffLDAPConfiguration == null){
            return;
        }

        LDAPDataSource ldapDataSource = staffLDAPConfiguration.getLdapDataSource();

        LDAPConnectionManager ldapConnectionManager = LDAPConnectionManager.getInstance(ldapDataSource.getDataSourceName());

        if (ldapConnectionManager.isInitialize() == false) {

            try {
                ldapConnectionManager.createLDAPConnectionPool((com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource) ldapDataSource, true);
            } catch (DriverInitializationFailedException | LDAPException e) {
                getLogger().error(MODULE, "Error while initializing LDAP Data Source: " + ldapDataSource.getDataSourceName() + ". Reason:" + e.getMessage());
                getLogger().trace(MODULE, e);
            }
        }

        staffLDAPInterface = new StaffLDAPInterface(staffLDAPConfiguration,new LDAPConnectionProvider() {
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
            DefaultNVSMXContext context = DefaultNVSMXContext.getContext();
            staffLDAPInterface.init(context.getTaskScheduler(),context.getServerName());
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while initializing external Staff. Reason:" + e.getMessage());
            getLogger().trace(MODULE, e);
        }


    }

    public void destroy() {

        // use individual try-catch block to shut down resources

        if(pdContextReader != null){
            pdContextReader.updateStatus(EndPointStatus.SHUT_DOWN);
        }


        try {
            if (serverStatusWSBLManager != null && pdContextReader != null) {
                serverStatusWSBLManager.announcingShutDown(pdContextReader.getLocalPDContextInformation().getId());
            }
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while broadcasting shutdown status. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }


        try{
            HibernateSessionFactory.shutdown();
            adminService.stop();
        }catch(Exception e){
            getLogger().error(MODULE, "Error while closing session factory. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }

        try {
            EliteScheduler.getInstance().shutdown();
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while closing Scheduler. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }

        try {
            EndPointManager.getInstance().shutdown();
            if (endPointFactory != null) {
                endPointFactory.shutDown();
            }
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Shutting down this PD Context");
            }


        } catch (Exception e) {
            getLogger().error(MODULE, "Error while closing Remote message communicator. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }

        EDRDriverManager.getInstance().stop();

    }

    private void initializeNVSMXConnectionManager(String deploymentPath) {
        try {
            NVSMXDBConnectionManager.getInstance().init(deploymentPath);
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while initializing NVSMX DB Connection Manager. Reason: " + e.getMessage());
            getLogger().trace(e);
        }
    }

    private void initializeHibernateSessionFactory() {
        try {
            Configuration cfg = new Configuration();
            cfg.configure(CONFIG_FILE_LOCATION);
            HibernateConfigurationUtil.setConfigurationClasses(cfg);
            if(getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE,"Reading hibernate configuration properties from location "+CONFIG_FILE_LOCATION);
            }
            HibernateSessionFactory.buildSessionFactory(cfg);

        }catch(InitializationFailedException e){
            getLogger().error(MODULE, "Error while initializing Hibernate Session Factory. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    private void initializeRMIInterface(String contextPath) {
        try {

            SessionProvider sessionProvider = HibernateSessionFactory::getSession;
            //Reading Tomcat information
            pdContextReader = new PDContextReader(deploymentPath, contextPath, sessionProvider);
            pdContextReader.read();
            pdContextReader.updateStatus(EndPointStatus.STARTED);



            NetvertexInstanceGroupConfiguration netvertexInstanceGroupConfiguration = new NetvertexInstanceGroupConfiguration(sessionProvider,DefaultNVSMXContext.getContext());
            NvsmxInstanceConfiguration nvsmxInstanceConfiguration = new NvsmxInstanceConfiguration(sessionProvider,DefaultNVSMXContext.getContext());
            endPointFactory = new EndPointFactory(EliteScheduler.getInstance().TASK_SCHEDULER, Executors.newFixedThreadPool(getRMIThreads(), new EliteThreadFactory(MODULE, MODULE, Thread.NORM_PRIORITY)));
            EndPointManager endPointManager = EndPointManager.getInstance();
            endPointManager.init(endPointFactory, pdContextReader.getLocalPDContextInformation(), netvertexInstanceGroupConfiguration,nvsmxInstanceConfiguration,EliteScheduler.getInstance().TASK_SCHEDULER);


            RMIGroupFactory rmiGroupFactory = new RMIGroupFactory(endPointManager);
            RMIGroupManager rmiGroupManager = RMIGroupManager.getInstance();
            rmiGroupManager.init(rmiGroupFactory, netvertexInstanceGroupConfiguration);

            serverStatusWSBLManager = new ServerStatusWSBLManager(endPointManager);
            serverStatusWSBLManager.announcingWakeUp(pdContextReader.getLocalPDContextInformation().getId());

            //initializing update PD status task

            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Initializing scheduler for updating PD status");
            }

            EliteScheduler.getInstance().scheduleIntervalBasedTask(new UpdatePDStatusTask(sessionProvider,pdContextReader.getLocalPDContextInformation()));


            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Reading of PD context Information completed successfully");
            }
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while initializing rest Interface. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    private int getRMIThreads() {
        final String RMI_THREADS = "rmi.threads";
        final int RMI_THREADS_DEFAULT = 20;
        String rmiThreadsStr = System.getProperty(RMI_THREADS);
        int rmiThreads;
        if (Strings.isNullOrBlank(rmiThreadsStr) == false) {
            rmiThreads = Numbers.parseInt(rmiThreadsStr.trim(), RMI_THREADS_DEFAULT);
            if (rmiThreads < 10) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Considering default RMI Threads: " + RMI_THREADS_DEFAULT + "Invalid threads configured: " + rmiThreads + " configured in system parameter: " + RMI_THREADS + ". Minimum threads should be 10");
                }

                rmiThreads = RMI_THREADS_DEFAULT;
            } else {

                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Considering " + rmiThreads + " from system parameter: " + RMI_THREADS);
                }
            }

        } else {
            rmiThreads = RMI_THREADS_DEFAULT;

            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Considering default RMI Threads: " + rmiThreads + ". Reason: System parameter: " + RMI_THREADS + " is not configured");
            }
        }

        return rmiThreads;
    }

    private void initializePolicyManager() {
        try {
            PolicyManager policyManager = PolicyManager.getInstance();

            RnCFactory rnCFactory = new RnCFactory();
            RateCardVersionFactory rateCardVersionFactory = new RateCardVersionFactory(rnCFactory);
            RateCardFactory rateCardFactory = new RateCardFactory(rateCardVersionFactory, rnCFactory);
            RateCardGroupFactory rateCardGroupFactory = new RateCardGroupFactory(rateCardFactory, rnCFactory);
            ThresholdNotificationSchemeFactory thresholdNotificationSchemeFactory = new ThresholdNotificationSchemeFactory(rnCFactory);

			policyManager.init(deploymentPath, HibernateSessionFactory.getSessionFactory(), new HibernateDataReader(), new PackageFactory(),
					new RnCPackageFactory(rateCardGroupFactory, rnCFactory,thresholdNotificationSchemeFactory),
					DeploymentMode.fromName(SystemParameterDAO.get(SystemParameter.DEPLOYMENT_MODE.name())));

            DefaultNVSMXContext.getContext().setPolicyRepository(policyManager);

            if(getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Policy Manager successfully initialized");
            }
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while initializing policy manager. Reason: " +e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    private void initializeGroovyManager() {
        try {
            GroovyManager.getInstance().initScripts();
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while initializing Groovy Manager. Reason: " + e.getMessage());
            getLogger().trace(e);
        }
    }

    private void initializeSubscriberDAO() {
        try {
            SubscriberDAO.getInstance().init();
        } catch (Exception e) {
            getLogger().error(MODULE, "Could not read SPR Configuiration");
            getLogger().trace(MODULE, e);
        }
    }

    private void initializeAdminService() {
        try {
            adminService = new EliteAdminService(DefaultNVSMXContext.getContext());
            adminService.init();
            if (adminService.start()) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Admin service started success");
                }
            } else {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Admin service not started");
                }
            }

            writeSysInitDetails();

        } catch (Exception e) {
            getLogger().error(MODULE, "Error while starting admin service. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    private final void writeSysInitDetails() {
        String serverHome = deploymentPath;
        File systemFolder = new File(serverHome + File.separator + SYSTEM_PATH);
        try(FileWriter fileWriter = new FileWriter(new File(systemFolder,SYS_INIT_FILE));
            PrintWriter out = new PrintWriter(fileWriter)) {

            List<String> list = ManagementFactory.getRuntimeMXBean().getInputArguments();
            String port = null;
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    String arg = list.get(i);
                    if (arg.contains("-Dcom.sun.management.jmxremote.port")) {
                        port = arg.substring(arg.lastIndexOf('=') + 1);
                        break;
                    }
                }
            }
            if (port != null) {
                out.println(port.trim());
            } else {
                if(getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Could not find JMX service port.");
                }
            }
        } catch (Exception e) {
            getLogger().error(MODULE,"Problem writing server init information, reason : "+ e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    private void initializeSubscriberImportController() {
        try {

            SubscriberImportController subscriberImportController = new SubscriberImportController(deploymentPath);
            registerMBean(subscriberImportController);
            addCLICommand(new SubscriberImportCommand(subscriberImportController));

        } catch (Exception e) {
            getLogger().error(MODULE, "Subscriber import mbean registration failed. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    private void registerMBean(SubscriberImportController subscriberImportController) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {
        adminService.registerMbean(subscriberImportController);
    }

    private void addCLICommand(ICommand subscriberImportCommand) {
        adminService.addCliCommand(subscriberImportCommand);
    }

    private void initializeESICommand() {

        addCLICommand(new ESICommand(EndPointManager.getInstance()));

        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "ESI Command command registered successfully");
        }
    }

    private void initializeWebServiceStaticsManager() {
        try {

            ShowCommand showCommand = new ShowCommand();
            StatisticsDetailProvider statisticsDetailProvider = StatisticsDetailProvider.getInstance();
            WebServiceStatisticsDetailProvider webServiceStatisticsDetailProvider = new WebServiceStatisticsDetailProvider();
            statisticsDetailProvider.registerDetailProvider(webServiceStatisticsDetailProvider);
            ShowCommand.registerDetailProvider(statisticsDetailProvider);
            addCLICommand(showCommand);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "show statistics CLI command registered successfully");
            }

        } catch (Exception e) {
            getLogger().error(MODULE, "Web-Service Statistics command registration failed. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }

        try {

            ClearCommand clearCommand = new ClearCommand();
            ResetStatisticsProvider resetProvider = ResetStatisticsProvider.getInstance();
            ClearCommand.registerDetailProvider(resetProvider);
            addCLICommand(clearCommand);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Clear Web-Service statistics CLI command registered successfully");
            }

        } catch (Exception e) {
            getLogger().error(MODULE, "Clear Web-Service Statistics command registration failed. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    private void initializeSNMPAgent() {
        try {

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Initializing SNMP-Agent");
            }

            boolean skipAgentFlag = false;
            if(ConfigurationProvider.getInstance().getSnmpAddress()==null || ConfigurationProvider.getInstance().getSnmpAddress().trim().length()==0){
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "SNMP IP Address is Invalid. Skipping SNMP Agent creation.");
                }
                skipAgentFlag = true;
            }

            if(skipAgentFlag == false && ConfigurationProvider.getInstance().getSnmpPort()==0){
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "SNMP port is Invalid. Skipping SNMP Agent creation.");
                }
                skipAgentFlag = true;
            }

            if(skipAgentFlag == false){

                snmpAgent = new EliteSnmpAgent(DefaultNVSMXContext.getContext(),new SnmpOid(NETVERTEX_OID) , ConfigurationProvider.getInstance().getSnmpAddress() ,
                        ConfigurationProvider.getInstance().getSnmpPort());

                snmpAgent.init();

                RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
                long nvsmxUpTime = runtimeMXBean.getStartTime();

                snmpAgent.loadRFC1213Mib("Elite NetVertex Server , Version: "+ Version.getVersion(), NETVERTEX_OID,
                        nvsmxUpTime, "netvertex@elitecore.com", NAME, getServerIp());
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "SNMP-Agent initialized successfully");
                }
            }

        } catch (Exception e) {
            getLogger().error(MODULE, "SNMP-Agent initializing Failed. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    private String getServerIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            getLogger().error(MODULE,"Local IP address not found. Reason: " + e.getMessage());
            getLogger().trace(MODULE , e);
        }
        return NAME;
    }

    private void registerNetvertexMIBWithSNMPAgent() {
        try {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Registering Netvertex-MIB with SNMP-Agent");
            }
            if(snmpAgent != null){
                NETVERTEX_PCRF_MIBImpl netvertexPCRFMib = WebServiceStatisticsManager.getInstance().getNetvertex_PCRF_MIBImpl();

                if(netvertexPCRFMib != null){
                    netvertexPCRFMib.init();
                    snmpAgent.registerMib(netvertexPCRFMib);
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Netvertex-MIB successfully registered with SNMP-Agent");
                    }
                }else{
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Netvertex-MIB is Null. So, Netvertex-MIB registration with SNMP-Agent skipped.");
                    }
                }
            }else{
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "SNMP-Agent is Null. So, Netvertex-MIB registration skipped.");
                }
            }

        } catch (Exception e) {
            getLogger().error(MODULE, "Netvertex-MIB registration Failed. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    private void initializeSystemParameter() {
        try {
            SystemParameterDAO.init();
            ConfigurationProvider.getInstance().readPageSize();
        }catch(Exception e){
            getLogger().error(MODULE, "Could not initialize SystemParameter. Reason: " +e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    public @Nullable StaffLDAPInterface getStaffLDAPInterface() {
        return staffLDAPInterface;
    }

    private void initializeEDRDriver() {

        String balanceCSVHeader = "BALANCE_ID,SUBSCRIBER_ID,PACKAGE_ID,SERVICE_INSTANCE_ID,IP_ADDRESS,SERVICE_ID,RATING_GROUP_ID," +
                "QUOTA_PROFILE_ID,ADDON_PACKAGE_ID,PREVIOUS_BALANCE,CURRENT_BALANCE,AMOUNT,TRANSACTION_TYPE,BILLING_CYCLE_VOLUME," +
                "BILLING_CYCLE_TIME,CREDIT_LIMIT,NEXT_BILLING_CYCLE_CREDIT_LIMIT,VALID_FROM_DATE,VALID_TO_DATE,OPERATION,ACTION," +
                "REMARKS,MONETARY_RECHARGE_PLAN_NAME,PRICE,CURRENCY,TRANSACTIONID,UPDATE_TIME";
        EDRDriver balanceEDRDriver = new EDRDriver(
                DefaultNVSMXContext.getContext().getServerHome(),
                NVSMXCommonConstants.BALANCE_EDR_DRIVER,
                balanceCSVHeader,
                new BalanceEDRLineBuilder(Character.toString(CommonConstants.COMMA), new SimpleDateFormat(CommonConstants.DEFAULT_TIMESTAMP_FORMAT), TimeSource.systemTimeSource()),
                DefaultNVSMXContext.getContext().getTaskScheduler());

        String subscriberCSVHeader  ="SUBSCRIBER_ID,USERNAME,SERVICE_INSTANCE_ID,IP_ADDRESS,CUSTOMER_TYPE,STATUS,DATA_PACKAGE,EXPIRY_DATE,BILLLING_DATE,"
                + "IMSI,MSISDN,IMEI,NEXT_BILL_DATE,BILL_CHANGE_DATE,CREATED_DATE,MODIFIED_DATE,OPERATION,ACTION,CURRENCY,UPDATE_TIME";
        EDRDriver subscriberEDRDriver = new EDRDriver(
                DefaultNVSMXContext.getContext().getServerHome(),
                NVSMXCommonConstants.SUBSCRIBER_EDR_DRIVER,
                subscriberCSVHeader,
                new SubscriberEDRLineBuilder(Character.toString(CommonConstants.COMMA), new SimpleDateFormat(CommonConstants.DEFAULT_TIMESTAMP_FORMAT), TimeSource.systemTimeSource()),
                DefaultNVSMXContext.getContext().getTaskScheduler());

        String subscriptionCSVHeader  ="SUBSCRIBER_ID,PACKAGE_ID,SERVICE_INSTANCE_ID,IP_ADDRESS,PACKAGE_NAME,START_TIME,END_TIME,STATUS,TYPE,OPERATION,CURRENCY,PRIORITY,FNF_MEMBERS,UPDATE_TIME";
        EDRDriver subscriptionEDRDriver = new EDRDriver(
                DefaultNVSMXContext.getContext().getServerHome(),
                NVSMXCommonConstants.SUBSCRIPTION_EDR_DRIVER,
                subscriptionCSVHeader,
                new SubscriptionEDRLineBuilder(Character.toString(CommonConstants.COMMA), new SimpleDateFormat(CommonConstants.DEFAULT_TIMESTAMP_FORMAT), TimeSource.systemTimeSource()),
                DefaultNVSMXContext.getContext().getTaskScheduler());

        String externalAlternateIdCSVHeader = "SUBSCRIBER_ID, ALTERNATE_ID, UPDATED_ALTERNATE_ID, STATUS, UPDATED_STATUS, OPERATION, UPDATE_TIME";
        EDRDriver externalAlternateIdEDRDriver = new EDRDriver(
                DefaultNVSMXContext.getContext().getServerHome(),
                NVSMXCommonConstants.EXTERNAL_ALTERNATE_ID_EDR_DRIVER,
                externalAlternateIdCSVHeader,
                new ExternalAlternateIdEDRLineBuilder(Character.toString(CommonConstants.COMMA), new SimpleDateFormat(CommonConstants.DEFAULT_TIMESTAMP_FORMAT), TimeSource.systemTimeSource()),
                DefaultNVSMXContext.getContext().getTaskScheduler());

        try {
            balanceEDRDriver.init();
            EDRDriverManager.getInstance().registerDriver(balanceEDRDriver);

            subscriberEDRDriver.init();
            EDRDriverManager.getInstance().registerDriver(subscriberEDRDriver);

            subscriptionEDRDriver.init();
            EDRDriverManager.getInstance().registerDriver(subscriptionEDRDriver);

            externalAlternateIdEDRDriver.init();
            EDRDriverManager.getInstance().registerDriver(externalAlternateIdEDRDriver);

        } catch (DriverInitializationFailedException e){
            getLogger().error(MODULE, "Error while executing init() of EDRDriver. Reason: " + e.getMessage());

            getLogger().trace(MODULE, e);
        }
    }
}