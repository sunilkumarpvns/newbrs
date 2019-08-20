package com.elitecore.netvertex.gateway.diameter.plugin;


import com.elitecore.core.commons.plugins.PluginConfiguration;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.voltdb.VoltDBClientManager;
import com.elitecore.core.serverx.ServerContext;
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
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.util.cli.cmd.ICommand;
import com.elitecore.corenetvertex.constants.NotificationRecipient;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.diameterapi.core.common.peer.exception.StatusListenerRegistrationFailException;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerStatusListener;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.license.base.LicenseObserver;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.conf.NetvertexServerConfiguration;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.locationmanagement.LocationRepository;
import com.elitecore.netvertex.core.lrn.data.LRNConfigurationRepository;
import com.elitecore.netvertex.core.roaming.MCCMNCEntry;
import com.elitecore.netvertex.core.roaming.MCCMNCRoutingEntry;
import com.elitecore.netvertex.core.roaming.RoutingEntry;
import com.elitecore.netvertex.core.roaming.conf.MCCMNCRoutingConfiguration;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.gateway.diameter.plugin.conf.impl.MCCMNCDiameterPluginConfigurationImpl;
import com.elitecore.netvertex.service.pcrf.DeviceManager;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFResponseListner;
import com.elitecore.netvertex.service.pcrf.prefix.conf.PrefixConfigurable;
import com.elitecore.netvertex.service.pcrf.prefix.conf.PrefixConfiguration;
import com.sun.management.snmp.agent.SnmpMib;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MCCMNCDiameterPluginTest {
	
	private MCCMNCDiameterPlugin mccmncDiameterPlugin;
	private Map<String, MCCMNCEntry> mccmncEntries = new HashMap<String, MCCMNCEntry>();
	
	@Before
	public void beforeSetup(){
		
		final ServiceContext serviceContext = new ServiceContext() {
			NetVertexServerContext netVertexServerContext = new NetvertexServerContextImpl();
			
			@Override
			public ServerContext getServerContext() {
				return netVertexServerContext;
			}
		};
		
		PluginContext pluginContext = new PluginContext() {
			
			
			
			@Override
			public ServerContext getServerContext() {
				return serviceContext.getServerContext();
			}
			
			@Override
			public PluginConfiguration getPluginConfiguration(String pluginName) {
				return new MCCMNCDiameterPluginConfigurationImpl(serviceContext.getServerContext(), "mccmncplugin");
			}
		};
		
		PluginInfo pluginInfo = new PluginInfo();
		pluginInfo.setDescription("");
		pluginInfo.setPluginClass("MCCMNCDiameterPlugin");
		pluginInfo.setPluginConfClass("MCCMNCDiameterPluginConfigurationImpl");
		mccmncDiameterPlugin = new MCCMNCDiameterPlugin(pluginContext, pluginInfo);
		
	}
	
	
	@Test
	public void testMCCMNCInfoAttributeAddedInRequest(){
		
		MCCMNCEntry mccmncEntry = new MCCMNCEntry("1", "123", "456", "elitecore", "elitecore", "india", "elitecore", "GSM");
		mccmncEntries.put(mccmncEntry.getMCCMNC(), mccmncEntry);
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.CREDIT_CONTROL.code);
		
		IDiameterAVP subscriberId = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID);
		
		
		IDiameterAVP subscriberIdType = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
		subscriberIdType.setInteger(DiameterAttributeValueConstants.DIAMETER_END_USER_IMSI);
		IDiameterAVP subscriberIdData = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
		subscriberIdData.setStringValue("123456789012345");
		
		ArrayList<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();
		diameterAVPs.add(subscriberIdType);
		diameterAVPs.add(subscriberIdData);
		subscriberId.setGroupedAvp(diameterAVPs);
		
		diameterRequest.addAvp(subscriberId);
		
		mccmncDiameterPlugin.handleInMessage(diameterRequest, null, ISession.NO_SESSION, null, null);
		
		IDiameterAVP diameterAVP = diameterRequest.getInfoAVP(DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC);
		
		assertEquals("123456",diameterAVP.getStringValue());
	}
	
	
	@Test
	public void testMultipleMCCMNCInfoAttributesAddedInRequest(){
		
		MCCMNCEntry mccmncEntry = new MCCMNCEntry("1", "123", "456", "elitecore", "elitecore", "india", "elitecore", "GSM");
		MCCMNCEntry mccmncEntry2 = new MCCMNCEntry("2", "123", "45", "elitecore", "elitecore", "india", "elitecore", "GSM");
		mccmncEntries.put(mccmncEntry.getMCCMNC(), mccmncEntry);
		mccmncEntries.put(mccmncEntry2.getMCCMNC(), mccmncEntry2);
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.CREDIT_CONTROL.code);
		
		IDiameterAVP subscriberId = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID);
		
		
		IDiameterAVP subscriberIdType = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
		subscriberIdType.setInteger(DiameterAttributeValueConstants.DIAMETER_END_USER_IMSI);
		IDiameterAVP subscriberIdData = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
		subscriberIdData.setStringValue("123456");
		
		ArrayList<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();
		diameterAVPs.add(subscriberIdType);
		diameterAVPs.add(subscriberIdData);
		subscriberId.setGroupedAvp(diameterAVPs);
		
		diameterRequest.addAvp(subscriberId);
		
		mccmncDiameterPlugin.handleInMessage(diameterRequest, null, ISession.NO_SESSION, null, null);
		
		List<IDiameterAVP> diameterAVP = diameterRequest.getInfoAVPList(DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC);
		
		assertEquals(2,diameterAVP.size());
		assertEquals("123456",diameterAVP.get(0).getStringValue());
		assertEquals("12345",diameterAVP.get(1).getStringValue());
	}
	
	
	@Test
	public void testIMSIsizeIs5(){
		
		MCCMNCEntry mccmncEntry = new MCCMNCEntry("1", "123", "456", "elitecore", "elitecore", "india", "elitecore", "GSM");
		MCCMNCEntry mccmncEntry2 = new MCCMNCEntry("2", "123", "45", "elitecore", "elitecore", "india", "elitecore", "GSM");
		mccmncEntries.put(mccmncEntry.getMCCMNC(), mccmncEntry);
		mccmncEntries.put(mccmncEntry2.getMCCMNC(), mccmncEntry2);
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.CREDIT_CONTROL.code);
		
		IDiameterAVP subscriberId = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID);
		
		
		IDiameterAVP subscriberIdType = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
		subscriberIdType.setInteger(DiameterAttributeValueConstants.DIAMETER_END_USER_IMSI);
		IDiameterAVP subscriberIdData = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
		subscriberIdData.setStringValue("12345");
		
		ArrayList<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();
		diameterAVPs.add(subscriberIdType);
		diameterAVPs.add(subscriberIdData);
		subscriberId.setGroupedAvp(diameterAVPs);
		
		diameterRequest.addAvp(subscriberId);
		
		mccmncDiameterPlugin.handleInMessage(diameterRequest, null, ISession.NO_SESSION, null, null);
		
		List<IDiameterAVP> diameterAVP = diameterRequest.getInfoAVPList(DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC);
		
		assertEquals(1,diameterAVP.size());
		assertEquals("12345",diameterAVP.get(0).getStringValue());
	}
	
	@Test
	public void testMCCMNCEntryNotfoundForIMSI(){
		
		MCCMNCEntry mccmncEntry = new MCCMNCEntry("1", "122", "456", "elitecore", "elitecore", "india", "elitecore", "GSM");
		MCCMNCEntry mccmncEntry2 = new MCCMNCEntry("2", "122", "45", "elitecore", "elitecore", "india", "elitecore", "GSM");
		mccmncEntries.put(mccmncEntry.getMCCMNC(), mccmncEntry);
		mccmncEntries.put(mccmncEntry2.getMCCMNC(), mccmncEntry2);
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.CREDIT_CONTROL.code);
		
		IDiameterAVP subscriberId = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID);
		
		
		IDiameterAVP subscriberIdType = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
		subscriberIdType.setInteger(DiameterAttributeValueConstants.DIAMETER_END_USER_IMSI);
		IDiameterAVP subscriberIdData = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
		subscriberIdData.setStringValue("123456789012345");
		
		ArrayList<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();
		diameterAVPs.add(subscriberIdType);
		diameterAVPs.add(subscriberIdData);
		subscriberId.setGroupedAvp(diameterAVPs);
		
		diameterRequest.addAvp(subscriberId);
		
		mccmncDiameterPlugin.handleInMessage(diameterRequest, null, ISession.NO_SESSION, null, null);
		
		List<IDiameterAVP> diameterAVP = diameterRequest.getInfoAVPList(DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC);
		
		assertNull(diameterAVP);
	}
	
	@Test
	public void testMCCMNCPluingWithDiameterAnswer(){
		
		MCCMNCEntry mccmncEntry = new MCCMNCEntry("1", "123", "456", "elitecore", "elitecore", "india", "elitecore", "GSM");
		MCCMNCEntry mccmncEntry2 = new MCCMNCEntry("2", "123", "45", "elitecore", "elitecore", "india", "elitecore", "GSM");
		mccmncEntries.put(mccmncEntry.getMCCMNC(), mccmncEntry);
		mccmncEntries.put(mccmncEntry2.getMCCMNC(), mccmncEntry2);
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.CREDIT_CONTROL.code);
		
		IDiameterAVP subscriberId = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID);
		
		
		IDiameterAVP subscriberIdType = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
		subscriberIdType.setInteger(DiameterAttributeValueConstants.DIAMETER_END_USER_IMSI);
		IDiameterAVP subscriberIdData = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
		subscriberIdData.setStringValue("123456789012345");
		
		ArrayList<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();
		diameterAVPs.add(subscriberIdType);
		diameterAVPs.add(subscriberIdData);
		subscriberId.setGroupedAvp(diameterAVPs);
		
		
		
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);
		diameterAnswer.addAvp(subscriberId);
		
		mccmncDiameterPlugin.handleInMessage(null, diameterAnswer, ISession.NO_SESSION, null, null);
		
		List<IDiameterAVP> diameterAVP = diameterRequest.getInfoAVPList(DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC);
		
		assertNull(diameterAVP);
	}
	
	@Test
	public void testSubscriberIdAvpNotFound(){
		
		MCCMNCEntry mccmncEntry = new MCCMNCEntry("1", "123", "456", "elitecore", "elitecore", "india", "elitecore", "GSM");
		mccmncEntries.put(mccmncEntry.getMCCMNC(), mccmncEntry);
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.CREDIT_CONTROL.code);
		
		mccmncDiameterPlugin.handleInMessage(diameterRequest, null, ISession.NO_SESSION, null, null);
		
		IDiameterAVP diameterAVP = diameterRequest.getInfoAVP(DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC);
		
		assertNull(diameterAVP);
	}
	
	@Test
	public void testSubscriberIdTypeIsOtherThanIMSI(){
		
		MCCMNCEntry mccmncEntry = new MCCMNCEntry("1", "123", "456", "elitecore", "elitecore", "india", "elitecore", "GSM");
		mccmncEntries.put(mccmncEntry.getMCCMNC(), mccmncEntry);
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.CREDIT_CONTROL.code);
		
		IDiameterAVP subscriberId = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID);
		
		
		IDiameterAVP subscriberIdType = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
		subscriberIdType.setInteger(DiameterAttributeValueConstants.DIAMETER_END_USER_NAI);
		IDiameterAVP subscriberIdData = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
		subscriberIdData.setStringValue("123456789012345");
		
		ArrayList<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();
		diameterAVPs.add(subscriberIdType);
		diameterAVPs.add(subscriberIdData);
		subscriberId.setGroupedAvp(diameterAVPs);
		
		diameterRequest.addAvp(subscriberId);
		
		mccmncDiameterPlugin.handleInMessage(diameterRequest, null, ISession.NO_SESSION, null, null);
		
		IDiameterAVP diameterAVP = diameterRequest.getInfoAVP(DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC);
		
		assertNull(diameterAVP);
	}
	
	@Test
	public void testIMSIsizeIsLessThan5(){
		
		MCCMNCEntry mccmncEntry = new MCCMNCEntry("1", "123", "456", "elitecore", "elitecore", "india", "elitecore", "GSM");
		mccmncEntries.put(mccmncEntry.getMCCMNC(), mccmncEntry);
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.CREDIT_CONTROL.code);
		
		IDiameterAVP subscriberId = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID);
		
		
		IDiameterAVP subscriberIdType = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
		subscriberIdType.setInteger(DiameterAttributeValueConstants.DIAMETER_END_USER_NAI);
		IDiameterAVP subscriberIdData = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
		subscriberIdData.setStringValue("1234");
		
		ArrayList<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();
		diameterAVPs.add(subscriberIdType);
		diameterAVPs.add(subscriberIdData);
		subscriberId.setGroupedAvp(diameterAVPs);
		
		diameterRequest.addAvp(subscriberId);
		
		mccmncDiameterPlugin.handleInMessage(diameterRequest, null, ISession.NO_SESSION, null, null);
		
		IDiameterAVP diameterAVP = diameterRequest.getInfoAVP(DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC);
		
		assertNull(diameterAVP);
	}
	
	
	
	
	
	@Test
	public void testDiameterBaseMessage(){
		
		MCCMNCEntry mccmncEntry = new MCCMNCEntry("1", "123", "456", "elitecore", "elitecore", "india", "elitecore", "GSM");
		MCCMNCEntry mccmncEntry2 = new MCCMNCEntry("2", "123", "45", "elitecore", "elitecore", "india", "elitecore", "GSM");
		mccmncEntries.put(mccmncEntry.getMCCMNC(), mccmncEntry);
		mccmncEntries.put(mccmncEntry2.getMCCMNC(), mccmncEntry2);
		
		//CER
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.CAPABILITIES_EXCHANGE.code);
		
		mccmncDiameterPlugin.handleInMessage(diameterRequest, null, ISession.NO_SESSION, null, null);
		
		List<IDiameterAVP> diameterAVP = diameterRequest.getInfoAVPList(DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC);
		
		assertNull(diameterAVP);
		
		
		//DWR
		diameterRequest.setCommandCode(CommandCode.DEVICE_WATCHDOG.code);
		
		mccmncDiameterPlugin.handleInMessage(diameterRequest, null, ISession.NO_SESSION, null, null);
		
		diameterAVP = diameterRequest.getInfoAVPList(DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC);
		
		assertNull(diameterAVP);
		
		
		//DPR
		diameterRequest.setCommandCode(CommandCode.DISCONNECT_PEER.code);
		
		mccmncDiameterPlugin.handleInMessage(diameterRequest, null, ISession.NO_SESSION, null, null);
		
		diameterAVP = diameterRequest.getInfoAVPList(DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC);
		
		assertNull(diameterAVP);
	}
	
	private  class NetvertexServerContextImpl implements NetVertexServerContext{
		
		DummyNetvertexServerConfiguration netvertexServerConfiguration = new DummyNetvertexServerConfiguration();

		public NetvertexServerContextImpl() {
			netvertexServerConfiguration.setMccmncConfiguration(new MCCMNCRoutingConfigurationImpl());
		}
		
		@Override
		public String getServerHome() {
			return "";
		}

		@Override
		public String getServerName() {
			return "";
		}

		@Override
		public String getServerVersion() {
			return "6.4";
		}

		@Override
		public String getServerMajorVersion() {
			return "6.4";
		}

		@Override
		public String getServerDescription() {
			return "";
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
		public String getServerInstanceId() {
			return "6.4";
		}

		@Override
		public String getServerInstanceName() {
			return "NetVertexTest";
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
		public PolicyRepository getPolicyRepository() {
			return null;
		}

		@Override
		public VoltDBClientManager getVoltDBClientManager() {
			return null;
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
		public NetvertexServerConfiguration getServerConfiguration() {
			return netvertexServerConfiguration;
		}

		@Override
		public LocationRepository getLocationRepository() {
			return null;
		}

		@Override
		public DeviceManager getDeviceManager() {
			return null;
		}

		@Override
		public boolean isLocationBasedServicesEnabled() {
			return false;
		}

		

		@Override
		public void registerCliCommand(List<ICommand> commandList) {
			// TODO Auto-generated method stub
		
	}

		@Override
		public long getLicencedMessagePerMinute() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public DBConnectionManager getDBConnMgr(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void generateSystemAlert(AlertSeverity severity,
				IAlertEnum alertEnum, String alertGeneratorIdentity,
				String alertMessage, int alertIntValue, String alertStringValue) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void generateSystemAlert(String severity, IAlertEnum alertEnum,
				String alertGeneratorIdentity, String alertMessage,
				int alertIntValue, String alertStringValue) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sendSnmpTrap(SystemAlert peer,
				SnmpAlertProcessor snmpTrapProcessor) {
			// TODO Auto-generated method stub
			
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TaskScheduler getTaskScheduler() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean sendSyRequest(PCRFResponse pcrfResponse, DiameterPeerGroupParameter diameterPeerGroupParameter, String primaryGatewayName,
				PCRFResponseListner responseListner, CommandCode commandCode) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public PrefixConfigurable getPrefixRepository() {
			return null;
		}

		@Override
		public LRNConfigurationRepository getLRNConfigurationRepository() {
			return null;
		}

		@Override
		public DiameterPeerState registerPeerStatusListener(String gatewayId, DiameterPeerStatusListener diameterPeerStatusListener)
				throws StatusListenerRegistrationFailException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getSyGatewayName(String sySessionId) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean sendNotification(com.elitecore.corenetvertex.service.notification.Template emailTemplate,
				com.elitecore.corenetvertex.service.notification.Template smsTemplate, PCRFResponse response) {
			// TODO Auto-generated method stub
			return false;
	}

		@Override
		public boolean sendNotification(com.elitecore.corenetvertex.service.notification.Template emailTemplate,
				com.elitecore.corenetvertex.service.notification.Template smsTemplate, PCRFResponse response, Timestamp validityDate,
				NotificationRecipient recipient) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void registerStopable(Stopable stopable) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void uploadLicense(int resultCode, String message, String licenseKey){

		}

		@Override
		public void registerLicenseObserver(LicenseObserver licenseObserver){

		}
		@Override
		public long getLicenseTPS(){
			return -1;
		}
	}

	private class MCCMNCRoutingConfigurationImpl implements MCCMNCRoutingConfiguration{
	
		@Override
		public List<RoutingEntry> getRoutingEntries() {
			return null;
		}

		@Override
		public MCCMNCEntry getMCCMNCEntry(String mccmnc) {
			return mccmncEntries.get(mccmnc);
		}

		@Override
		public Collection<String> getMccMncs() {
			return getMccMncs();
		}

		@Override
		public Collection<String> getRoutingEntryNames() {
			return getRoutingEntryNames();
		}

		@Override
		public MCCMNCRoutingEntry getRoutingEntryByMCCMNC(String mccmnc) {
			return null;
		}

		@Override
		public RoutingEntry getRoutingEntryByName(String name) {
			return null;
		}
		
	}
	
}



