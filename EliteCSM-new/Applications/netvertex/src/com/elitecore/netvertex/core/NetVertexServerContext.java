package com.elitecore.netvertex.core;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.voltdb.VoltDBClientManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.serverx.alert.listeners.SnmpAlertProcessor;
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
import com.elitecore.netvertex.core.conf.NetvertexServerConfiguration;
import com.elitecore.netvertex.core.locationmanagement.LocationRepository;
import com.elitecore.netvertex.core.lrn.data.LRNConfigurationRepository;
import com.elitecore.netvertex.service.pcrf.DeviceManager;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFResponseListner;
import com.elitecore.netvertex.service.pcrf.prefix.PrefixRepository;
import com.sun.management.snmp.agent.SnmpMib;

public interface NetVertexServerContext extends ServerContext {
	NetvertexServerConfiguration getServerConfiguration();
	PolicyRepository getPolicyRepository();
	VoltDBClientManager getVoltDBClientManager();
	LocationRepository getLocationRepository();
	DeviceManager getDeviceManager();
	
	boolean sendNotification(Template emailTemplate, Template smsTemplate, PCRFResponse response);
	boolean sendNotification(Template emailTemplate, Template smsTemplate, PCRFResponse response, Timestamp validityDate, NotificationRecipient recipient);
	boolean isPrimaryServer();
	SPRInfo getSPRInfo(String userIdentity);

	void sendSnmpTrap(SystemAlert peer, SnmpAlertProcessor snmpTrapProcessor);
	void registerSnmpMib(SnmpMib snmpMib);
	boolean sendSyRequest(PCRFResponse pcrfResponse, DiameterPeerGroupParameter diameterPeerGroupParameter, @Nullable String primaryGatewayName, PCRFResponseListner responseListner,CommandCode commandCode);

    PrefixRepository getPrefixRepository();
    LRNConfigurationRepository getLRNConfigurationRepository();

    boolean isLocationBasedServicesEnabled();
	DiameterPeerState registerPeerStatusListener(String gatewayId, DiameterPeerStatusListener diameterPeerStatusListener) throws StatusListenerRegistrationFailException;
	
	void registerCliCommand(List<ICommand> commandList);
	long getLicencedMessagePerMinute();
	DBConnectionManager getDBConnMgr(String name);
	@Nullable Map<String, String> getSyCounter(String sySessionID);
	@Nullable String getSyGatewayName(String sySessionId);
	void uploadLicense(int resultCode, String message, String licenseKey);
	void registerLicenseObserver(LicenseObserver licenseObserver);
	long getLicenseTPS();
}
