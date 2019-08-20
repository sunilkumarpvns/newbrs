package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.systemx.esix.LoadBalancerType;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.constants.PolicyEnforcementMethod;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayData;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayProfileConfiguration;
import com.elitecore.netvertex.gateway.diameter.conf.impl.DiameterGatewayConfigurationImpl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class DiameterGatewayFactory{

	private static final String MODULE = "DIA-GW-FCTRY";
	private static final int NO_TRANSACTION_TIMEOUT = -1;

	private DiameterGatewayProfileFactory diameterGatewayProfileFactory;

	public DiameterGatewayFactory(DiameterGatewayProfileFactory diameterGatewayProfileFactory) {
		this.diameterGatewayProfileFactory = diameterGatewayProfileFactory;
	}
	
	public DiameterGatewayConfigurationImpl create(DiameterGatewayData diameterGatewayData) throws LoadConfigurationException {

		if(diameterGatewayData.getDescription() == null){
			diameterGatewayData.setDescription("");
		}
		
		if(diameterGatewayData.getRealm() == null){
			if(getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Diameter Gateway Realm is not provided.");
			}
			diameterGatewayData.setRealm("");
		}
		
		DiameterGatewayProfileConfiguration diameterGatewayProfileConf = diameterGatewayProfileFactory.create(diameterGatewayData.getDiameterGatewayProfileData());
		
		if(diameterGatewayProfileConf == null){
			throw new LoadConfigurationException("Error while reading diameter gateway configuration. Reason : gateway profile not configured");
		}

		ConnectionURL connectionURL = ConnectionURL.create(diameterGatewayData.getConnectionURL(), diameterGatewayData.getHostIdentity());
		LocalAddress localAddress = LocalAddress.create(diameterGatewayData.getLocalAddress(), diameterGatewayProfileConf.getInitConnectionDuration());
		
		if(diameterGatewayProfileConf.getSupportedStandard() == SupportedStandard.CISCOSCE){
			diameterGatewayData.setPolicyEnforcementMethod(PolicyEnforcementMethod.ASR.name());
		}
		
		PolicyEnforcementMethod enforcementMethod = PolicyEnforcementMethod.valueOf(diameterGatewayData.getPolicyEnforcementMethod());

		if(enforcementMethod == null || 
				(enforcementMethod.isDiameterMethod() || enforcementMethod.isDiameterSCEMethod()) == false){
			
			LogManager.getLogger().warn(MODULE, "Considering "+ PolicyEnforcementMethod.STANDARD +" as policy enforcement method"+
			" for Diameter Gateway: " + diameterGatewayData.getConnectionURL() + ". Reason: Configured invalid policy enforcement method: " 
					+ enforcementMethod);
			enforcementMethod = PolicyEnforcementMethod.STANDARD;
		}
		
		DiameterGatewayData alternateHost = diameterGatewayData.getAlternateHost();
		DiameterPeerGroupParameter diameterPeerGroupParameter;
		Map<String, Integer> peers = new HashMap<String, Integer>(2,1);

		String name = null;
		if (alternateHost == null) {
			
			peers.put(diameterGatewayData.getName(), 1);
			diameterPeerGroupParameter = new DiameterPeerGroupParameter(diameterGatewayData.getName(), peers, LoadBalancerType.ROUND_ROBIN,true, 2, NO_TRANSACTION_TIMEOUT);
		} else {

			name = alternateHost.getName();
			peers.put(diameterGatewayData.getName(), 1);
			peers.put(name, 1);
			diameterPeerGroupParameter = new DiameterPeerGroupParameter(diameterGatewayData.getName() + "-"
					+ name, peers, LoadBalancerType.ROUND_ROBIN, true, 2, NO_TRANSACTION_TIMEOUT);
			
		}
		
		return new DiameterGatewayConfigurationImpl(diameterGatewayData.getName(), diameterGatewayData.getId(), diameterGatewayProfileConf.getProfileId()
				, name, diameterPeerGroupParameter, CommunicationProtocol.DIAMETER, diameterGatewayData.getDescription()
				, connectionURL, diameterGatewayData.getRealm(), localAddress , diameterGatewayData.getHostIdentity(), enforcementMethod, diameterGatewayData.getRetransmissionCount(), diameterGatewayData.getRequestTimeout()
				, diameterGatewayProfileConf);
	}


	public static class LocalAddress {
		private String localIpAddress;
		private int localPort;

		public LocalAddress(String localIpAddress, int localPort) {
			this.localIpAddress = localIpAddress;
			this.localPort = localPort;
		}

		public String getLocalIpAddress() {
			return localIpAddress;
		}

		public int getLocalPort() {
			return localPort;
		}

		/**
		 * Possible cases
		 * setLocalAddress("harsh.elitecore.com");
		 * setLocalAddress("10.106.1.30:3030");
		 * setLocalAddress("10.106.1.30");
		 * setLocalAddress(":3555");
		 *
		 * @param localAddress
		 * @param initConnectionDuration
		 * @return
		 */
		public static LocalAddress create(String localAddress, long initConnectionDuration) {


				String address = localAddress;
				if(address == null || address.trim().length() == 0){
					if (initConnectionDuration > 0) {
						getLogger().warn(MODULE, "Local address for gateway not specified. Netvertex will select local address from network interfaces");
					}
					return new LocalAddress(address, com.elitecore.core.commons.util.constants.CommonConstants.ANY_PORT) ;
				}


				String localIpAdd = null;
				int localPort =  com.elitecore.core.commons.util.constants.CommonConstants.ANY_PORT;
				try {
					URLData urlData = URLParser.parse(address);
					localIpAdd = urlData.getHost();
					localPort = urlData.getPort();
				}catch(InvalidURLException invalidURLEx){
					LogManager.getLogger().warn(MODULE, "Invalid Local Address : "+localAddress);
					LogManager.getLogger().trace(MODULE, invalidURLEx);
				}

				return new LocalAddress(localIpAdd, localPort);
		}
	}



	public static class ConnectionURL {

		private int remotePort;
		private InetAddress remoteInetAddress;
		private boolean ipRange;
		private String configuredURL;

		public ConnectionURL() {

		}

		public ConnectionURL(InetAddress remoteInetAddress, int remotePort) {
			this.remotePort = remotePort;
			this.remoteInetAddress = remoteInetAddress;
			this.ipRange = false;
		}

		public ConnectionURL(String configuredURL) {
			this.configuredURL = configuredURL;
			this.ipRange = true;
		}

		public boolean isConfigured() {
			if(isIpRange()) {
				return Strings.isNullOrBlank(configuredURL) == false;
			} else {
				return Objects.isNull(remoteInetAddress) == false;
			}
		}

		public InetAddress getRemoteInetAddress() {
			return remoteInetAddress;
		}

		public int getPort() {
			return remotePort;
		}

		public boolean isIpRange() {
			return ipRange;
		}

		public String getIPAddress() {
			if(remoteInetAddress == null) {
				return null;
			}
			return remoteInetAddress.getHostAddress();
		}

		public String getConfiguredURL() {
			return configuredURL;
		}

		public static ConnectionURL create(String connectionURL, String hostIdentity){

			if(Strings.isNullOrBlank(connectionURL)) {
				return  new ConnectionURL();
			}

			InetAddress hostInetAddress = null;
			String ipAddress = com.elitecore.core.commons.util.constants.CommonConstants.UNIVERSAL_IP;

			if(DiameterUtility.isIPRange(connectionURL)) {
				return new ConnectionURL(connectionURL);
			}

			int remotePort = DiameterConstants.DIAMETER_SERVICE_PORT;
			if(connectionURL != null) {
				try{
					URLData remoteUrlData = URLParser.parse(connectionURL);
					remotePort = remoteUrlData.getPort() == URLParser.UNKNOWN_PORT ? DiameterConstants.DIAMETER_SERVICE_PORT :remoteUrlData.getPort();
					if(remoteUrlData.getHost() != null && remoteUrlData.getHost().trim().length() > 0 ){
						ipAddress = remoteUrlData.getHost();
					}
				}catch(InvalidURLException iEx){
					LogManager.getLogger().warn(MODULE, "Error while parsing URL: " + connectionURL + ". Reason: Invalid connection URL. Set Gateway IPAddress based on host-identity");
					LogManager.ignoreTrace(iEx);
				}
			} else {
				LogManager.getLogger().info(MODULE, "connection URL not configured. set Gateway IPAddress based on host-identity");
			}

			if(com.elitecore.core.commons.util.constants.CommonConstants.UNIVERSAL_IP.equalsIgnoreCase(ipAddress) == false){
				hostInetAddress = getInetAddress(ipAddress);
				if(hostInetAddress == null) {
					hostInetAddress = getInetAddress(hostIdentity);
				}
			}



			if(hostInetAddress != null){
				return new ConnectionURL(hostInetAddress, remotePort);
			}


			return new ConnectionURL(null, remotePort);

		}

		private static InetAddress getInetAddress(String address){
			InetAddress inetAddress = null;
			try{
				inetAddress = InetAddress.getByName(address);
			} catch (UnknownHostException e) {
				LogManager.getLogger().error(MODULE, "Error While fetching Dynamic IP Address. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}

			return inetAddress;
		}



	}
}
