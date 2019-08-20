package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.conf.context.AAAConfigurationState;
import com.elitecore.aaa.core.conf.impl.DummyAAAServerConfigurationImpl;
import com.elitecore.aaa.core.eap.session.EAPSessionManager;
import com.elitecore.aaa.core.plugins.conf.PluginDetail;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.wimax.WimaxSessionManager;
import com.elitecore.aaa.core.wimax.keys.KeyManager;
import com.elitecore.aaa.diameter.plugins.core.DiameterPluginManager;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPCommunicatorManager;
import com.elitecore.commons.base.Optional;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.utilx.mbean.BaseMBeanController;
import com.elitecore.core.imdg.HazelcastImdgInstance;
import com.elitecore.core.serverx.Stopable;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.alert.Alerts;
import com.elitecore.core.serverx.alert.IAlertEnum;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.serverx.alert.listeners.SnmpAlertProcessor;
import com.elitecore.core.serverx.manager.cache.Cacheable;
import com.elitecore.core.serverx.manager.scripts.ExternalScriptsManager;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.serverx.snmp.EliteSnmpAgent;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.diameterapi.core.common.transport.VirtualInputStream;
import com.elitecore.diameterapi.core.common.transport.VirtualOutputStream;
import com.elitecore.diameterapi.core.stack.ElementRegistrationFailedException;
import com.elitecore.diameterapi.diameter.common.data.RoutingEntryData;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.sun.management.snmp.agent.SnmpMib;

public class DummyAAAServerContext implements AAAServerContext {

	private RadUDPCommunicatorManagerImpl radUDPCommunicatorManagerImpl;
	private DummyAAAServerConfigurationImpl aaaServerConfiguration;
	private RadiusESIGroupFactory radiusEsiGroupFactory;

	@Override
	public String getServerHome() {
		return null;
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
	public void generateSystemAlert(AlertSeverity severity, IAlertEnum alertEnum, String alertGeneratorIdentity,
			String alertMessage) {

	}

	@Override
	public void generateSystemAlert(AlertSeverity severity, IAlertEnum alertEnum, String alertGeneratorIdentity,
			String alertMessage, Map<Alerts, Object> alertData) {

	}

	@Override
	public void generateSystemAlert(String severity, IAlertEnum alertEnum, String alertGeneratorIdentity,
			String alertMessage) {

	}

	@Override
	public void generateSystemAlert(AlertSeverity severity, IAlertEnum alertEnum, String alertGeneratorIdentity,
			String alertMessage, int alertIntValue, String alertStringValue) {

	}

	@Override
	public void generateSystemAlert(String severity, IAlertEnum alertEnum, String alertGeneratorIdentity,
			String alertMessage, int alertIntValue, String alertStringValue) {

	}

	@Override
	public boolean isLicenseValid(String key, String value) {
		return false;
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
		return null;
	}

	@Override
	public String getServerInstanceId() {
		return null;
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
	public void registerStopable(Stopable stopable) {

	}

	@Override
	public void sendSnmpTrap(SystemAlert alert, SnmpAlertProcessor alertProcessor) {

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
	public EliteSnmpAgent getSNMPAgent() {
		return null;
	}

	@Override
	public EAPSessionManager getEapSessionManager() {
		return null;
	}

	@Override
	public AAAServerConfiguration getServerConfiguration() {
		return aaaServerConfiguration;
	}

	@Override
	public WimaxSessionManager getWimaxSessionManager() {
		return null;
	}

	@Override
	public KeyManager getKeyManager() {
		return null;
	}

	@Override
	public RadUDPCommunicatorManager getRadUDPCommunicatorManager() {
		return radUDPCommunicatorManagerImpl;
	}
	
	@Override
	public Optional<ConcurrencySessionManager> getLocalSessionManager(String sessionManagerId) {
		return null;
	}

	@Override
	public long getAAAServerUPTime() {
		return 0;
	}

	@Override
	public Map<String, ConcurrencySessionManager> getLocalSessionManagerMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerMBean(BaseMBeanController baseMBeanImpl) {
		// TODO Auto-generated method stub

	}

	@Override
	public AAAConfigurationState getConfigurationState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ESCommunicator getDiameterDriver(String driverInstanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerSnmpMib(SnmpMib snmpMib) {
		// TODO Auto-generated method stub

	}

	@Override
	public VirtualInputStream registerVirtualPeer(PeerDataImpl peerData, VirtualOutputStream virtualOutputStream)
			throws ElementRegistrationFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerPriorityRoutingEntry(RoutingEntryData entryData) throws ElementRegistrationFailedException {
		// TODO Auto-generated method stub

	}

	@Override
	public PluginDetail getPluginDetail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerPlugins(List<PluginEntryDetail> names) {
		// TODO Auto-generated method stub

	}

	@Override
	public DiameterPluginManager getDiameterPluginManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HazelcastImdgInstance getHazelcastImdgInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasRadiusSession(String sessionId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ISession getOrCreateRadiusSession(String sessionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> search(String index, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RadiusESIGroupFactory getRadiusESIGroupFactory() {
		return radiusEsiGroupFactory;
	}

	public void setRadUdpCommunicatorManager(RadUDPCommunicatorManagerImpl radUDPCommunicatorManagerImpl) {
		this.radUDPCommunicatorManagerImpl = radUDPCommunicatorManagerImpl;
		
	}

	public void setServerConfiguration(DummyAAAServerConfigurationImpl dummyAAAServerConfigurationImpl) {
		this.aaaServerConfiguration = dummyAAAServerConfigurationImpl;
		
	}
	
	public void setRadiusESIGroupFactory(RadiusESIGroupFactory radiusEsiGroupFactory) {
		this.radiusEsiGroupFactory = radiusEsiGroupFactory;

	}
	
}
