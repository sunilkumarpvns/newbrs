package com.elitecore.nvsmx.system.listeners;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.voltdb.VoltDBClientManager;
import com.elitecore.core.serverx.Stopable;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.alert.Alerts;
import com.elitecore.core.serverx.alert.IAlertEnum;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.serverx.alert.listeners.SnmpAlertProcessor;
import com.elitecore.core.serverx.manager.cache.Cacheable;
import com.elitecore.core.serverx.manager.scripts.ExternalScriptsManager;
import com.elitecore.core.serverx.snmp.EliteSnmpAgent;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.systemx.esix.configuration.EndPointConfigurationImpl;
import com.elitecore.core.systemx.esix.http.EndPointManager;
import com.elitecore.core.systemx.esix.http.HTTPClientFactory;
import com.elitecore.core.systemx.esix.http.HTTPConnector;
import com.elitecore.core.systemx.esix.http.HTTPMethodType;
import com.elitecore.core.systemx.esix.http.RemoteMethod;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.exception.InvalidPublickeyException;
import com.elitecore.license.crypt.DefaultEncryptor;
import com.elitecore.nvsmx.Version;
import com.elitecore.nvsmx.remotecommunications.RemoteMethodConstant;
import com.elitecore.nvsmx.system.NVSMXContext;
import com.elitecore.nvsmx.system.db.NVSMXDBConnectionManager;
import com.elitecore.nvsmx.system.scheduler.EliteScheduler;
import com.elitecore.nvsmx.ws.util.AlternateIdOperationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class DefaultNVSMXContext implements NVSMXContext{

	private static final String MODULE = "DEFAULT-NVSMX-CONTEXT";
	private PolicyRepository policyRepository;
	private VoltDBClientManager voltDBClientManager;

	private static final DefaultNVSMXContext context;
	private String contextPath;
	private String serverHome;

	private static final String SERVER_INSTANCE_NAME = "Policy Designer";

	private String localHostName;
	private static final int MAX_POOL=100;
	private static final int CONNECTION_TIME_OUT=3000;
	private static final int READ_TIME_OUT=3000;
	private HttpClient poolableHTTPClient = new HTTPClientFactory().getPoolableHttpClient(MAX_POOL, CONNECTION_TIME_OUT, READ_TIME_OUT);
	private AlternateIdOperationUtils alternateIdOperationUtils;

	static {
		context = new DefaultNVSMXContext();
	}

	private DefaultNVSMXContext(){
		try {
			localHostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			LogManager.getLogger().debug(MODULE,
					"Could not get host name. Reason: "+e.getMessage());
			LogManager.getLogger().trace(e);
		}
	}

	public static DefaultNVSMXContext getContext() {
		return context;
	}

	@Override
	public PolicyRepository getPolicyRepository() {
		return policyRepository;
	}

	public void setPolicyRepository(PolicyRepository policyRepository) {
		this.policyRepository = policyRepository;
	}


	@Override
	public VoltDBClientManager getVoltDBClientManager() {
		return voltDBClientManager;
	}

	public void setVoltDBClientManager(VoltDBClientManager voltDBClientManager) {
		this.voltDBClientManager = voltDBClientManager;
	}

	@Override
	public void sendSnmpTrap(SystemAlert alert,
							 SnmpAlertProcessor alertProcessor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerStopable(Stopable stopable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerCacheable(Cacheable cacheable) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isServerStartedWithLastConf() {
		return false;
	}

	@Override
	public boolean isLicenseValid(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void incrementTPSCounter() {
		// TODO Auto-generated method stub

	}

	@Override
	public TaskScheduler getTaskScheduler() {
		return EliteScheduler.getInstance().getTaskSchedular();
	}

	@Override
	public long getTPSCounter() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getServerVersion() {
		return Version.getMajorVersion();
	}

	@Override
	public long getServerStartUpTime() {
		return 0;
	}

	@Override
	public String getServerName() {
		return "NVSMX Server";
	}

	@Override
	public String getServerMajorVersion() {
		return Version.getMajorVersion();
	}

	@Override
	public String getServerInstanceName() {
		return SERVER_INSTANCE_NAME;
	}

	@Override
	public String getServerInstanceId() {
		return SERVER_INSTANCE_NAME;
	}

	@Override
	public String getServerHome() {
		return serverHome;
	}

	public void setServerHome(String serverHome){
		this.serverHome = serverHome;
	}

	@Override
	public String getServerDescription() {
		return SERVER_INSTANCE_NAME;
	}

	@Override
	public String getSVNRevision() {
		return Version.getSVNRevision();
	}

	@Override
	public String getReleaseDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalHostName() {
		return localHostName;
	}


	@Override
	public ExternalScriptsManager getExternalScriptsManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	@Override
	public String getContact() {
		return "support@netvertex.com";
	}

	@Override
	public void generateSystemAlert(String severity, IAlertEnum alertEnum,
									String alertGeneratorIdentity, String alertMessage,
									int alertIntValue, String alertStringValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateSystemAlert(AlertSeverity severity,
									IAlertEnum alertEnum, String alertGeneratorIdentity,
									String alertMessage, int alertIntValue, String alertStringValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateSystemAlert(String severity, IAlertEnum alertEnum,
									String alertGeneratorIdentity, String alertMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateSystemAlert(AlertSeverity severity,
									IAlertEnum alertEnum, String alertGeneratorIdentity,
									String alertMessage) {
		// TODO Auto-generated method stub

	}

    @Override
    public void generateSystemAlert(AlertSeverity severity, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage, Map<Alerts, Object> alertData) {
		//SONAR ignore
    }

    @Override
	public long addTotalResponseTime(long responseTimeInNano) {
		return 0;
	}

	@Override
	public int addAndGetAverageRequestCount(int delta) {
		return 0;
	}

	@Override
	public String getPublicKey() throws InvalidPublickeyException{
		String publicKey;

		try (Connection connection = NVSMXDBConnectionManager.getInstance().getConnection()) {
			DatabaseMetaData databaseMetaData =connection.getMetaData();
			String databaseURL = databaseMetaData.getURL();

			String dbIdQuery=null;
			String userIdQuery=null;

			if (databaseURL.toLowerCase().contains("oracle")) {
				dbIdQuery = "select DBID from v$database";
				userIdQuery = "select sys_context('USERENV','CURRENT_USERID') USERID from dual";
			} else if (databaseURL.toLowerCase().contains("postgresql")) {
				dbIdQuery = "select oid DBID from pg_tablespace where spcname = '"+databaseMetaData.getUserName()+"'";
				userIdQuery = "select oid USERID from pg_authid where rolname = '"+databaseMetaData.getUserName()+"'";
			}

			if(dbIdQuery!=null && userIdQuery!=null){

				try(PreparedStatement statementDBID = connection.prepareStatement(dbIdQuery);
					ResultSet resultSetDBID =statementDBID.executeQuery();
					PreparedStatement statementUserID = connection.prepareStatement(userIdQuery);
					ResultSet resultSetUserId =statementUserID.executeQuery()){
					if (resultSetDBID.next()) {
						publicKey = resultSetDBID.getString("DBID");
					} else {
						throw new InvalidPublickeyException("Mismatch in DB or tablespace configuration. Please contact support.");
					}

					if (resultSetUserId.next()) {
						if(publicKey!=null){
							publicKey += resultSetUserId.getString("USERID");
						} else {
							throw new InvalidPublickeyException("Mismatch in DB or tablespace configuration. Please contact support.");
						}
					} else {
						throw new InvalidPublickeyException("Mismatch in DB or tablespace configuration. Please contact support.");
					}
				}

				if(publicKey!=null){
					//Minimum public key length, before encryption, must be 10
					if(publicKey.length()<10){
						publicKey = StringUtils.rightPad(publicKey, 10, publicKey);
					}
					publicKey = new DefaultEncryptor().encryptPublicKey(publicKey);
				} else {
					throw new InvalidPublickeyException("Mismatch in DB or tablespace configuration. Please contact support.");
				}
			} else {
				throw new InvalidPublickeyException("Unknown Database configured." +
						" Please contact support.");
			}

		} catch (DataSourceException e){
			LogManager.getLogger().debug(MODULE,
					"Could not generate public key. Reason: "+e.getMessage());
			LogManager.getLogger().trace(e);
			throw new InvalidPublickeyException("Could not connect to Database. Please contact support.");

		} catch (SQLException e){
			LogManager.getLogger().debug(MODULE,
					"Could not generate public key. Reason: "+e.getMessage());
			LogManager.getLogger().trace(e);
			throw new InvalidPublickeyException("SQL Error . Please contact support.");
		}

		return publicKey;
	}
    @Override
    public EliteSnmpAgent getSNMPAgent() {
        return null;
    }

	@Override
	public void deregisterNetvertexServerLicense(ServerInstanceData netServerInstanceData){
		HTTPConnector httpConnector = getNVConnector(netServerInstanceData);
		if(httpConnector!=null){
			try {
				httpConnector.submit(new RemoteMethod(RemoteMethodConstant.NETVERTEX_LICENSE_REST_BASE_URI_PATH,
						"/deregister", HTTPMethodType.POST));
			} catch (CommunicationException e){
				getLogger().error(MODULE,"Could not deregister server with name: "+netServerInstanceData.getName()+
						". Reason: "+e.getMessage());
				getLogger().trace(e);
			}
		}
	}

	@Override
	public AlternateIdOperationUtils getAlternateIdOperationUtils() {
		return alternateIdOperationUtils;
	}

	public void setAlternateIdOperationUtils(AlternateIdOperationUtils alternateIdOperationUtils) {
		this.alternateIdOperationUtils = alternateIdOperationUtils;
	}




	private HTTPConnector getNVConnector(ServerInstanceData netServerInstanceData){

		HTTPConnector httpConnector = EndPointManager.getInstance().getEndPoint(netServerInstanceData.getId());

		if(httpConnector!=null){
			return httpConnector;
		}

		String restApiUrl = netServerInstanceData.getRestApiUrl();
		String[] urlData = restApiUrl.split(":");

		if(urlData.length==2){
			EndPointConfigurationImpl endPointConfiguration = new EndPointConfigurationImpl(
					netServerInstanceData.getId(),
					urlData[0],
					urlData[1],
					"",
					netServerInstanceData.getName(),
					LicenseConstants.INTEGRATION_USER,
					LicenseConstants.INTEGRATION_SECRET
			);
			EndPointManager.getInstance().addEndPoint(endPointConfiguration, poolableHTTPClient, EliteScheduler.getInstance().TASK_SCHEDULER);

			return httpConnector;

		} else {
			return null;
		}
	}
}

