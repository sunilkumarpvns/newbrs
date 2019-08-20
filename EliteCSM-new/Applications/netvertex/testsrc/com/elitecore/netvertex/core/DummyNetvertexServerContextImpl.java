package com.elitecore.netvertex.core;

import com.elitecore.core.commons.util.db.DBConnectionManager;
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
import com.elitecore.core.systemx.esix.FakeTaskScheduler;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.util.cli.cmd.ICommand;
import com.elitecore.corenetvertex.constants.NotificationRecipient;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.diameterapi.core.common.peer.exception.StatusListenerRegistrationFailException;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerStatusListener;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.license.base.LicenseObserver;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.devicemanagement.DummyDeviceManager;
import com.elitecore.netvertex.core.prefix.DummyLRNrepository;
import com.elitecore.netvertex.core.locationmanagement.DummyLocationRepository;
import com.elitecore.netvertex.core.prefix.DummyPrefixRepository;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFResponseListner;
import com.sun.management.snmp.agent.SnmpMib;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DummyNetvertexServerContextImpl implements NetVertexServerContext {
	
	private PolicyRepository policyRepository = new DummyPolicyRepository();
	private Map<String, DBConnectionManager> connMgrMap = new HashMap<String, DBConnectionManager>();
	private DummyNetvertexServerConfiguration netvertexServerConfiguration;
	private FakeTaskScheduler fakeTaskScheduler = Mockito.spy(new FakeTaskScheduler());
	private DummyLocationRepository locationRepository = DummyLocationRepository.spy();
	private DummyDeviceManager deviceManager = DummyDeviceManager.spy();
	private DummyPrefixRepository prefixRepository= DummyPrefixRepository.spy();
	private DummyLRNrepository lrNrepository = DummyLRNrepository.spy();
	private VoltDBClientManager voltDBClientManager;

	private String serverInstanceId = UUID.randomUUID().toString();
	private String serverHome;

	public DummyNetvertexServerContextImpl() {
		this.netvertexServerConfiguration = DummyNetvertexServerConfiguration.spy();
		this.locationRepository = DummyLocationRepository.spy();
        this.prefixRepository = DummyPrefixRepository.spy();
        this.lrNrepository = DummyLRNrepository.spy();
	}
	
	@Override
	public String getServerHome() {
		return serverHome;
	}

	@Override
	public String getServerName() {
		return null;
	}

	@Override
	public String getServerVersion() {
		return null;
	}

	@Override
	public String getServerMajorVersion() {
		return null;
	}

	@Override
	public String getServerDescription() {
		return null;
	}

	@Override
	public void generateSystemAlert(AlertSeverity severity,
			IAlertEnum alertEnum, String alertGeneratorIdentity,
			String alertMessage) {
	}

    @Override
    public void generateSystemAlert(AlertSeverity severity, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage, Map<Alerts, Object> alertData) {

    }

    @Override
	public void generateSystemAlert(String severity, IAlertEnum alertEnum,
			String alertGeneratorIdentity, String alertMessage) {

	}

	@Override
	public boolean isLicenseValid(String key, String value) {
        return true;
	}

	@Override
	public long getTPSCounter() {
		return 0;
	}

	@Override
	public void incrementTPSCounter() {

	}
	
	@Override
	public TaskScheduler getTaskScheduler() {
		return fakeTaskScheduler;
	}
	
	@Override
	public String getServerInstanceId() {
		return serverInstanceId;
	}

	@Override
	public String getServerInstanceName() {
		return null;
	}

	@Override
	public boolean isServerStartedWithLastConf() {
		return false;
	}

	@Override
	public ExternalScriptsManager getExternalScriptsManager() {
		return null;
	}

	@Override
	public String getSVNRevision() {
		return null;
	}

	@Override
	public String getReleaseDate() {
		return null;
	}

	@Override
	public long getServerStartUpTime() {
		return 0;
	}

	@Override
	public String getLocalHostName() {
		return null;
	}

	@Override
	public String getContact() {
		return null;
	}

	@Override
	public void registerCacheable(Cacheable cacheable) {

	}

	@Override
	public DummyNetvertexServerConfiguration getServerConfiguration() {
		return netvertexServerConfiguration;
	}

	@Override
	public DummyLocationRepository getLocationRepository() {
		return locationRepository;
	}

	@Override
	public DummyDeviceManager getDeviceManager() {
		return deviceManager;
	}

	@Override
	public PolicyRepository getPolicyRepository() {
		return policyRepository;
	}

	@Override
	public VoltDBClientManager getVoltDBClientManager() {
		return voltDBClientManager;
	}

	@Override
	public boolean isPrimaryServer() {
		return false;
	}

	@Override
	public SPRInfo getSPRInfo(String userIdentity) {
		return null;
	}

	@Override
	public void registerSnmpMib(SnmpMib snmpMib) {

	}

	@Override
	public boolean isLocationBasedServicesEnabled() {
		return false;
	}

	@Override
	public long getLicencedMessagePerMinute() {
		return 0;
	}
	
	public void setPolicyRepository(PolicyRepository policyRepository) {
		this.policyRepository = policyRepository;
	}

	
	@Override
	public DBConnectionManager getDBConnMgr(String name) {
		
		return connMgrMap.get(name);
	}
	
	public void addDBConnMgr(DBConnectionManager dbConnectionManager) {
		connMgrMap.put(dbConnectionManager.getCacheName(),dbConnectionManager);
}

	@Override
	public void generateSystemAlert(AlertSeverity severity,
			IAlertEnum alertEnum, String alertGeneratorIdentity,
			String alertMessage, int alertIntValue, String alertStringValue) {

	}



	@Override
	public void generateSystemAlert(String severity, IAlertEnum alertEnum,
			String alertGeneratorIdentity, String alertMessage,
			int alertIntValue, String alertStringValue) {

	}

	@Override
	public void sendSnmpTrap(SystemAlert peer,
			SnmpAlertProcessor snmpTrapProcessor) {

	}

	public void setNetvertexServerConfiguration(DummyNetvertexServerConfiguration netvertexServerConfiguration) {
		this.netvertexServerConfiguration = netvertexServerConfiguration;
		
	}

	public void setLocationRepository (DummyLocationRepository locationRepository) {
		this.locationRepository = locationRepository;
	}

	public void setDeviceManager(DummyDeviceManager deviceMAnager) {
		this.deviceManager = deviceMAnager;
	}

	public void setVoltDBClientManager(VoltDBClientManager voltDBClientManager){
		this.voltDBClientManager = voltDBClientManager;
	}

	@Override
	public long addTotalResponseTime(long responseTime) {
		return 0;
	}

	@Override
	public int addAndGetAverageRequestCount(int delta) {
		return 0;
	}

    @Override
    public EliteSnmpAgent getSNMPAgent() {
        return null;
    }

    @Override
	public Map<String, String> getSyCounter(String sySessionID) {
		return null;
	}

	@Override
	public void registerCliCommand(List<ICommand> commandList) {

	}

	@Override
	public boolean sendSyRequest(PCRFResponse pcrfResponse, DiameterPeerGroupParameter diameterPeerGroupParameter, String primaryGatewayName,
			PCRFResponseListner responseListner, CommandCode commandCode) {
		return false;
	}

	@Override
	public DummyPrefixRepository getPrefixRepository() {
		return prefixRepository;
	}

	@Override
	public DummyLRNrepository getLRNConfigurationRepository() {
		return lrNrepository;
	}

	@Override
	public DiameterPeerState registerPeerStatusListener(String gatewayId, DiameterPeerStatusListener diameterPeerStatusListener)
			throws StatusListenerRegistrationFailException {
		return null;
	}

	@Override
	public String getSyGatewayName(String sySessionId) {
		return null;
	}

	@Override
	public boolean sendNotification(Template emailTemplate,
			Template smsTemplate, PCRFResponse response, Timestamp validityDate,
			NotificationRecipient recipient) {
		return false;
	}

	@Override
	public boolean sendNotification(Template emailTemplate,
			Template smsTemplate, PCRFResponse response) {
		return false;
	}

	@Override
	public void registerStopable(Stopable stopable) {
		this.registerStopable(stopable);
	}


	public void setServerInstanceId(String serverInstanceId) {
		this.serverInstanceId = serverInstanceId;
	}


	public static DummyNetvertexServerContextImpl spy() {
		DummyNetvertexServerContextImpl dummyNetvertexServerContext = new DummyNetvertexServerContextImpl();
		dummyNetvertexServerContext.setNetvertexServerConfiguration(DummyNetvertexServerConfiguration.spy());
		dummyNetvertexServerContext.setLocationRepository(DummyLocationRepository.spy());

		return Mockito.spy(dummyNetvertexServerContext);
	}

	public void setServerHome(String serverHome) {
		this.serverHome = serverHome;
	}

	@Override
	public void uploadLicense(int resultCode, String message, String licenseKey) throws UnsupportedOperationException{

	}

	@Override
	public void registerLicenseObserver(LicenseObserver licenseObserver) throws UnsupportedOperationException{

	}
	public void setDeviceMAnager(DummyDeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}

	@Override
	public long getLicenseTPS(){
		return -1;
	}

}
