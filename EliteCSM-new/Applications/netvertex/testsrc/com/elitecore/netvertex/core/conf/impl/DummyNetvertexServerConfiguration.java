package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.corenetvertex.spr.data.DDFConfiguration;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfiguration;
import com.elitecore.netvertex.core.alerts.conf.AlertsConfiguration;
import com.elitecore.netvertex.core.conf.*;
import com.elitecore.netvertex.core.driver.spr.DriverConfiguration;
import com.elitecore.netvertex.core.roaming.DummyMCCMNCRoutingConfigurationImpl;
import com.elitecore.netvertex.core.roaming.conf.MCCMNCRoutingConfiguration;
import com.elitecore.netvertex.core.session.conf.SessionManagerConfiguration;
import com.elitecore.netvertex.escommunication.data.PDInstanceConfiguration;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfigurable;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterStackConfiguration;
import com.elitecore.netvertex.gateway.diameter.mapping.ApplicationPacketType;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterMapping;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.conf.RadiusListenerConfiguration;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMapping;
import com.elitecore.netvertex.gateway.radius.mapping.RadApplicationPacketType;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCMapping;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;
import com.elitecore.netvertex.service.offlinernc.guiding.conf.GuidingConfiguration;
import com.elitecore.netvertex.service.offlinernc.prefix.conf.PrefixConfiguration;
import com.elitecore.netvertex.service.pcrf.conf.PCRFServiceConfiguration;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.PccServicePolicyConfiguration;
import org.mockito.Mockito;

import javax.annotation.Nullable;
import java.util.*;

import static org.mockito.Mockito.mock;


/**
 * Created by harsh on 6/23/17.
 */
public class DummyNetvertexServerConfiguration implements NetvertexServerConfiguration {


    private MiscellaneousConfiguration miscellaneousConfiguration;
    private MCCMNCRoutingConfiguration mccmncConfiguration;
    private SessionManagerConfiguration sessionManagerConfiguration;
    private Map<String,DiameterGatewayConfiguration> diameterGatewayConfigurations;
    private Map<String, RadiusGatewayConfiguration> radiusGatewayConfById;
    private Map<String,DiameterGatewayConfiguration> diameterGatewayConfByName;
    private Map<String,DiameterGatewayConfiguration> diameterGatewayConfByHostIdentity;
    private Map<String,RadiusGatewayConfiguration> radiusGatewayConfByName;
    private Map<String, RadiusGatewayConfiguration> radiusGatewayConfbyIpAddress;
    private NetvertexServerGroupConfiguration netvertexServerGroupConfiguration;
    private Map<Integer,PccServicePolicyConfiguration> pcrfServicePolicyConfigurations;
    private Map<String,PccServicePolicyConfiguration> pcrfServicePolicyConfByName;
    private Map<ApplicationPacketType, PCCToDiameterMapping> pccToDiameterMappings;
    private Map<ApplicationPacketType, DiameterToPCCMapping> diameterToPCCMappings;
    private Map<RadApplicationPacketType, PCCToRadiusMapping> pccToRadiusMappings;
    private Map<RadApplicationPacketType, RadiusToPCCMapping> radiusToPCCMappings;
    private Map<String, DriverConfiguration> nameToDriverConfiguration;
    private Map<String, DriverConfiguration> idToDriverConfiguration;

    private NotificationServiceConfigurationImpl notificationServiceConfiguration;
    private AlertListenerConfiguration alertListenersConfiguration;
    private AlertsConfiguration alertConfiguration;
    private DDFConfiguration ddfConfiguration;
    private SystemParameterConfiguration systemParameterConfiguration;
    private DatabaseDSConfiguration databaseDSConfiguration;

    public static DummyNetvertexServerConfiguration spy() {
        return Mockito.spy(new DummyNetvertexServerConfiguration());
    }

    public DummyNetvertexServerConfiguration() {
        diameterGatewayConfigurations = new HashMap<>();
        diameterGatewayConfByHostIdentity = new HashMap<>();
        diameterGatewayConfByName = new HashMap<>();
        radiusGatewayConfByName = new HashMap<>();
        radiusGatewayConfById = new HashMap<>();
        radiusGatewayConfbyIpAddress = new HashMap<>();
        mccmncConfiguration = DummyMCCMNCRoutingConfigurationImpl.spy();
        pcrfServicePolicyConfigurations = new HashMap<>();
        pcrfServicePolicyConfByName = new HashMap<>();
        pccToDiameterMappings = new HashMap<>();
        diameterToPCCMappings = new HashMap<>();
        pccToRadiusMappings = new HashMap<>();
        radiusToPCCMappings = new HashMap<>();
        nameToDriverConfiguration = new HashMap<>();
        idToDriverConfiguration = new HashMap<>();

    }

    @Override
    public RadiusGatewayConfiguration getRadiusGatewayConfiguration(int gatewayId) {
        return radiusGatewayConfById.get(gatewayId);
    }

    @Override
    public RadiusGatewayConfiguration getRadiusGatewayConfiguration(String gatewayAddress) {
        return radiusGatewayConfbyIpAddress.get(gatewayAddress);
    }

    @Override
    public RadiusGatewayConfiguration getRadiusGatewayConfigurationByName(String name) {
        return radiusGatewayConfByName.get(name);
    }

    @Override
    public Collection<RadiusGatewayConfiguration> getRadiusGatewayConfigurations() {
        return null;
    }

    @Override
    public DiameterGatewayConfiguration getDiameterGatewayConfiguration(int gatewayId) {
        return diameterGatewayConfigurations.get(String.valueOf(gatewayId));
    }

    @Override
    public DiameterGatewayConfiguration getDiameterGatewayConfByHostIdentity(String hostIdentity) {
        return diameterGatewayConfByHostIdentity.get(hostIdentity);
    }

    @Override
    public DiameterGatewayConfiguration getDiameterGatewayConfByName(String gatewayName) {
        return diameterGatewayConfByName.get(gatewayName);
    }

    @Override
    public DiameterGatewayConfiguration getDiameterGatewayConfByConnUrl(String connUrl) {
        return null;
    }

    @Override
    public PccServicePolicyConfiguration getPCRFServicePolicyConfiguration(int pcrfServicePolicyId) { return pcrfServicePolicyConfigurations.get(pcrfServicePolicyId); }

    @Override
    public PccServicePolicyConfiguration getPCRFServicePolicyConfByName(String pcrfServicePolicyName) { return pcrfServicePolicyConfByName.get(pcrfServicePolicyName); }

    @Override
    public LDAPDSConfiguration getLDAPDSConfiguration() {
        return null;
    }

    @Override
    public DatabaseDSConfiguration getDatabaseDSConfiguration() {
        return databaseDSConfiguration;
    }

    @Override
    public PCRFServiceConfiguration getPCRFServiceConfiguration() { return null; }

    @Override
    public RadiusListenerConfiguration getRadiusGatewayEventListenerConfiguration() {
        return null;
    }

    @Override
    public NetvertexServerInstanceConfigurationImpl getNetvertexServerInstanceConfiguration() {
        return null;
    }

    @Override
    public DiameterStackConfiguration getDiameterStackConfiguration() {
        return null;
    }

    @Override
    public Collection<DiameterGatewayConfiguration> getDiameterGatewayConfigurations() { return null; }

    @Override
    public AlertListenerConfiguration getAlertListenersConfiguration() {
        return alertListenersConfiguration;
    }

    @Override
    public SessionManagerConfiguration getSessionManagerConfiguration() {
        return sessionManagerConfiguration;
    }


    @Override
    public NotificationServiceConfigurationImpl getNotificationServiceConfiguration() {
        return notificationServiceConfiguration;
    }

    @Override
    public Set<String> getRadiusGatewayNames() {
        return radiusGatewayConfByName.keySet();
    }

    @Override
    public Set<String> getDiameterGatewayNames() {
        return diameterGatewayConfByName.keySet();
    }

    @Override
    public DiameterGatewayConfigurable getDiameterGatewayConfigurable() {
        return null;
    }

    @Override
    public Set<String> getPCRFServicePolicyNames() {
        return pcrfServicePolicyConfByName.keySet();
    }

    @Override
    public MiscellaneousConfiguration getMiscellaneousParameterConfiguration() {
        return miscellaneousConfiguration;
    }

    @Override
    public MCCMNCRoutingConfiguration getMCCMNCRoutingConfiguration() {
        return mccmncConfiguration;
    }


    @Override
    public DDFConfiguration getDDFTableData() {
        return ddfConfiguration;
    }

    @Nullable
    @Override
    public NetvertexServerGroupConfiguration getNetvertexServerGroupConfiguration() {
        return netvertexServerGroupConfiguration;
    }

    public MiscellaneousConfiguration spyMiscConf() {
    	MiscellaneousConfiguration miscellaneousConfiguration = mock(MiscellaneousConfiguration.class);
        this.miscellaneousConfiguration = miscellaneousConfiguration;
        return miscellaneousConfiguration;
    }
    
    public SystemParameterConfiguration spySystemParameterConf() {
        SystemParameterConfiguration systemParameterConfiguration = mock(SystemParameterConfiguration.class);
        this.systemParameterConfiguration = systemParameterConfiguration;
        return systemParameterConfiguration;
    }

	public void setSystemParameterConf(SystemParameterConfiguration configuration) {
		this.systemParameterConfiguration = configuration;
	}

    public DiameterGatewayConfiguration spyDiameterGatewayConfFor(int gatewayId) {
        DiameterGatewayConfiguration diameterGatewayConfiguration1 = mock(DiameterGatewayConfiguration.class);
        this.diameterGatewayConfigurations.put(String.valueOf(gatewayId), diameterGatewayConfiguration1);
        return diameterGatewayConfiguration1;
    }

    public DiameterGatewayConfiguration spyDiameterGatewayConfFor(String gatewayName) {
        DiameterGatewayConfiguration diameterGatewayConfiguration1 = mock(DiameterGatewayConfiguration.class);
        this.diameterGatewayConfByName.put(gatewayName, diameterGatewayConfiguration1);
        return diameterGatewayConfiguration1;
    }

    public PCCToDiameterMapping spyPCCToDiameterMappingConfFor(String type) {
        PCCToDiameterMapping pccToDiameterMapping = mock(PCCToDiameterMapping.class);
        this.pccToDiameterMappings.put(ApplicationPacketType.valueOf(type), pccToDiameterMapping);
        return pccToDiameterMapping;
    }

    public PCCToRadiusMapping spyPCCToRadiusMappingConfFor(String type) {
        PCCToRadiusMapping pccToRadiusMapping = mock(PCCToRadiusMapping.class);
        this.pccToRadiusMappings.put(RadApplicationPacketType.valueOf(type), pccToRadiusMapping);
        return pccToRadiusMapping;
    }

    public DiameterToPCCMapping spyDiameterToPCCMappingConfFor(String type) {
        DiameterToPCCMapping diameterToPCCMapping = mock(DiameterToPCCMapping.class);
        this.diameterToPCCMappings.put(ApplicationPacketType.valueOf(type), diameterToPCCMapping);
        return diameterToPCCMapping;
    }

    public RadiusToPCCMapping spyRadiusToPCCMappingConfFor(String type) {
        RadiusToPCCMapping radiusToPCCMapping = mock(RadiusToPCCMapping.class);
        this.radiusToPCCMappings.put(RadApplicationPacketType.valueOf(type), radiusToPCCMapping);
        return radiusToPCCMapping;
    }

    public PccServicePolicyConfiguration spyPcrfServicePolicyConfFor(int pcrfServicePolicyId) {
        PccServicePolicyConfiguration pcrfServicePolicyConfiguration = mock(PccServicePolicyConfiguration.class);
        this.pcrfServicePolicyConfigurations.put(pcrfServicePolicyId, pcrfServicePolicyConfiguration);
        return pcrfServicePolicyConfiguration;
    }

    public PccServicePolicyConfiguration spyPcrfServicePolicyConfFor(String pcrfServicePolicyName) {
        PccServicePolicyConfiguration pcrfServicePolicyConfiguration = mock(PccServicePolicyConfiguration.class);
        this.pcrfServicePolicyConfByName.put(pcrfServicePolicyName, pcrfServicePolicyConfiguration);
        return pcrfServicePolicyConfiguration;
    }

    public RadiusGatewayConfiguration spyRadiusGatewayConf() {
        RadiusGatewayConfiguration radiusGatewayConfiguration = mock(RadiusGatewayConfiguration.class);
        return radiusGatewayConfiguration;
    }

    public RadiusGatewayConfiguration spyRadiusGatewayConfFor(String gatewayName) {
        RadiusGatewayConfiguration radiusGatewayConfiguration1 = mock(RadiusGatewayConfiguration.class);
        this.radiusGatewayConfByName.put(gatewayName, radiusGatewayConfiguration1);
        return radiusGatewayConfiguration1;
    }


    public RadiusGatewayConfiguration spyRadiusGatewayConfByName(String gatewayName) {
        RadiusGatewayConfiguration radiusGatewayConfiguration = mock(RadiusGatewayConfiguration.class);
        this.radiusGatewayConfByName.put(gatewayName, radiusGatewayConfiguration);
        return radiusGatewayConfiguration;
    }

    public void setMiscConf(MiscellaneousConfiguration miscellaneousConfiguration) {
        this.miscellaneousConfiguration = miscellaneousConfiguration;
    }

    public void setMccmncConfiguration(MCCMNCRoutingConfiguration mccmncConfiguration) {
        this.mccmncConfiguration = mccmncConfiguration;
    }

    public void setSessionManagerConfiguration(SessionManagerConfiguration sessionManagerConfiguration) {
        this.sessionManagerConfiguration = sessionManagerConfiguration;
    }

    public NetvertexServerGroupConfiguration spyGroupConfiguration() {
        return  this.netvertexServerGroupConfiguration = mock(NetvertexServerGroupConfiguration.class);
    }

    public void setNotificationServiceConfiguration(NotificationServiceConfigurationImpl notificationServiceConfiguration) {
        this.notificationServiceConfiguration = notificationServiceConfiguration;
    }

    public NotificationServiceConfigurationImpl spyNotificationConfiguration() {
        return  this.notificationServiceConfiguration = mock(NotificationServiceConfigurationImpl.class);
    }

    public SessionManagerConfiguration spySessionManagerConf() {
        SessionManagerConfiguration sessionManagerConfiguration = mock(SessionManagerConfiguration.class);
        this.sessionManagerConfiguration = sessionManagerConfiguration;
        return sessionManagerConfiguration;
    }
    public List<PDInstanceConfiguration> getPDInstanceConfiguration(){
        return null;
    }

    @Override
    public PCCToDiameterMapping getPCCToDiameterMappingsByPacketType(String gatewayName, String packetType) {
        return this.pccToDiameterMappings.get(ApplicationPacketType.valueOf(packetType));
    }

    @Override
    public DiameterToPCCMapping getDiameterToPCCMappingsByPacketType(String gatewayName, String packetType) {
        return this.diameterToPCCMappings.get(ApplicationPacketType.valueOf(packetType));
    }

    public void setAlertListenerConfiguration(AlertListenerConfiguration alertListenerConfiguration) {
        this.alertListenersConfiguration = alertListenerConfiguration;
    }

    public AlertListenerConfiguration spyAlertListenerConfiguration() {
        AlertListenerConfiguration sessionManagerConfiguration = mock(AlertListenerConfiguration.class);
        this.alertListenersConfiguration = sessionManagerConfiguration;
        return alertListenersConfiguration;
    }

    public DDFConfiguration spyDDFConfiguration() {
        DDFConfiguration ddfConfiguration = mock(DDFConfiguration.class);
        this.ddfConfiguration = ddfConfiguration;
        return ddfConfiguration;
    }

    public DummyMCCMNCRoutingConfigurationImpl spyMCCMNCRoutingConfigurationImpl() {
        DummyMCCMNCRoutingConfigurationImpl dummyMCCMNCRoutingConfiguration = DummyMCCMNCRoutingConfigurationImpl.spy();
        setMccmncConfiguration(dummyMCCMNCRoutingConfiguration);
        return dummyMCCMNCRoutingConfiguration;
    }

	@Override
	public List<GuidingConfiguration> getOfflineRnCServiceGuidingConfiguration() {
		return null;
	}


	@Override
	public List<PrefixConfiguration> getOfflineRnCServicePrefixConfiguration() {
		return null;
	}

    @Override
    public PCCToRadiusMapping getPCCToRadiusMappingsByPacketType(String gatewayName, String packetType) {
        return pccToRadiusMappings.get(RadApplicationPacketType.valueOf(packetType));
    }

    @Override
    public RadiusToPCCMapping getRadiusToPCCMappingsByPacketType(String gatewayName, String packetType) {
        return radiusToPCCMappings.get(RadApplicationPacketType.valueOf(packetType));
    }

    public void setNetvertexServerGroupConfiguration(NetvertexServerGroupConfiguration netvertexServerGroupConfiguration) {
        this.netvertexServerGroupConfiguration = netvertexServerGroupConfiguration;
    }

	@Override
	public SystemParameterConfiguration getSystemParameterConfiguration() {
		return this.systemParameterConfiguration;
	}

    @Override
    public DriverConfiguration getDriverConfigurationByName(String name) {
        return nameToDriverConfiguration.get(name);
    }

    @Override
    public DriverConfiguration getDriverConfigurationById(String id) {
        return idToDriverConfiguration.get(id);
    }

    public void setSystemParameterConfiguration(SystemParameterConfiguration systemParameterConfiguration) {
        this.systemParameterConfiguration = systemParameterConfiguration;
    }

    public void setDatabaseDSConfiguration(DatabaseDSConfiguration databaseDSConfiguration) {

        this.databaseDSConfiguration = databaseDSConfiguration;
    }

    public void addDiameterGatewayConfiguration(DiameterGatewayConfiguration diameterGatewayConfiguration) {
        diameterGatewayConfByName.put(diameterGatewayConfiguration.getName(), diameterGatewayConfiguration);
        diameterGatewayConfByHostIdentity.put(diameterGatewayConfiguration.getHostIdentity(), diameterGatewayConfiguration);
        diameterGatewayConfigurations.put(diameterGatewayConfiguration.getGatewayId(), diameterGatewayConfiguration);
    }
}
