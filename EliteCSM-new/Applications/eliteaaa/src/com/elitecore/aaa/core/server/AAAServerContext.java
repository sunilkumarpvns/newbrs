/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */


package com.elitecore.aaa.core.server;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.conf.context.AAAConfigurationState;
import com.elitecore.aaa.core.eap.session.EAPSessionManager;
import com.elitecore.aaa.core.plugins.conf.PluginDetail;
import com.elitecore.aaa.core.wimax.WimaxSessionManager;
import com.elitecore.aaa.core.wimax.keys.KeyManager;
import com.elitecore.aaa.diameter.plugins.core.DiameterPluginManager;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPCommunicatorManager;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusESIGroupFactory;
import com.elitecore.commons.base.Optional;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.utilx.mbean.BaseMBeanController;
import com.elitecore.core.imdg.HazelcastImdgInstance;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.diameterapi.core.common.transport.VirtualInputStream;
import com.elitecore.diameterapi.core.common.transport.VirtualOutputStream;
import com.elitecore.diameterapi.core.stack.ElementRegistrationFailedException;
import com.elitecore.diameterapi.diameter.common.data.RoutingEntryData;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.sun.management.snmp.agent.SnmpMib;


/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public interface AAAServerContext extends ServerContext {
	
	public EAPSessionManager getEapSessionManager(); 		
	public AAAServerConfiguration getServerConfiguration();
	public WimaxSessionManager getWimaxSessionManager();
	public KeyManager getKeyManager();
	public RadUDPCommunicatorManager getRadUDPCommunicatorManager();
	public Optional<ConcurrencySessionManager> getLocalSessionManager(String sessionManagerId);
	public long getAAAServerUPTime();
	public Map<String, ConcurrencySessionManager> getLocalSessionManagerMap();
	public void registerMBean(BaseMBeanController baseMBeanImpl);
	public AAAConfigurationState getConfigurationState();
	public ESCommunicator getDiameterDriver(String driverInstanceId);
	public void registerSnmpMib(SnmpMib snmpMib);
	public VirtualInputStream registerVirtualPeer(PeerDataImpl peerData, VirtualOutputStream virtualOutputStream) throws ElementRegistrationFailedException;
	public void registerPriorityRoutingEntry(RoutingEntryData entryData)throws ElementRegistrationFailedException;
	public PluginDetail getPluginDetail();
	public void registerPlugins(List<PluginEntryDetail> names);
	public DiameterPluginManager getDiameterPluginManager();
	HazelcastImdgInstance getHazelcastImdgInstance();
	
	boolean hasRadiusSession(String sessionId);
	ISession getOrCreateRadiusSession(String sessionId);
	Set<String> search(String index, String value);
	RadiusESIGroupFactory getRadiusESIGroupFactory();
	
}
