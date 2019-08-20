package com.elitecore.aaa.diameter.conf;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.conf.impl.ServerCertificateConfigurable;
import com.elitecore.aaa.diameter.conf.impl.PeerDetail;
import com.elitecore.aaa.diameter.conf.impl.PeerProfileConfigurable;
import com.elitecore.aaa.diameter.conf.impl.PeerProfileDetail;
import com.elitecore.aaa.diameter.conf.impl.PeerSecurityParameters;
import com.elitecore.aaa.diameter.conf.impl.PeersConfigurable;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.tls.EliteSSLParameter;
import com.elitecore.core.commons.tls.ServerCertificateProfile;
import com.elitecore.core.commons.tls.cipher.CipherSuites;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.RedirectHostAVPFormat;
import com.elitecore.license.base.commons.LicenseNameConstants;

public class DiameterCompositeConfigurablePostReadProcessor {
	private static final String MODULE = "DIA-COM-CONF-POST-PROC";
	private final ServerContext serverContext;
	private final PeersConfigurable peersConfigurable;
	private final PeerProfileConfigurable peerProfileConfigurable;
	private final ServerCertificateConfigurable serverCertificateConfigurable;
	private List<PeerData> peerDataList = new ArrayList<PeerData>();
	private List<PeerData> licenseExceededPeers = new ArrayList<PeerData>();
	
	public DiameterCompositeConfigurablePostReadProcessor(
			ServerContext serverContext,
			PeersConfigurable peersConfigurable,
			PeerProfileConfigurable peerProfileConfigurable,
			ServerCertificateConfigurable serverCertificateConfigurable) {
		this.serverContext = serverContext;
		this.peersConfigurable = peersConfigurable;
		this.peerProfileConfigurable = peerProfileConfigurable;
		this.serverCertificateConfigurable = serverCertificateConfigurable;
	}


	public List<PeerData> getLicenseExceededPeers() {
		return licenseExceededPeers;
	}

	public List<PeerData> getPeerDataList() {
		return peerDataList;
	}

	public void postRead() {
		postReadProcessingForPeers();
	}
	
	private void postReadProcessingForPeers() {
		peerDetailToPeerData(serverContext);

		filterLicenseExceededPeers(serverContext);
	}
	
	private void peerDetailToPeerData(ServerContext serverContext) {
		List<PeerData> tempPeerDataList = new ArrayList<PeerData>();
		for (PeerDetail peerDetail : peersConfigurable.getPeerList()) {
			String profileName = peerDetail.getProfileName();
			String peerName = peerDetail.getPeerName(); 

			if(peerName == null || peerName.trim().isEmpty()) {
				LogManager.getLogger().error(MODULE, "Invalid peer configured, Reason: Peer-Name not found");
				continue;
			}

			if(profileName == null || profileName.trim().isEmpty()) {
				LogManager.getLogger().error(MODULE, "Invaid peer " + peerName + " configured, Reason: Peer-Profile-Name not found");
				continue;
			}

			PeerDataImpl peerData = new PeerDataImpl();
			PeerProfileDetail peerProfileDetail = getPeerProfile(profileName, this.peerProfileConfigurable);

			if(peerProfileDetail == null) {
				LogManager.getLogger().error(MODULE, "Invalid peer " + peerName + " configured, Reason: peer-profile not found for profile-name: " + profileName);
				continue;
			}
			peerData.setSendDPRonCloseEvent(peerProfileDetail.getConnectionParameters().getIsSendDPREvent());
			peerData.setSocketSendBufferSize(peerProfileDetail.getConnectionParameters().getSocketSendBuffetSize());
			peerData.setSocketReceiveBufferSize(peerProfileDetail.getConnectionParameters().getSocketReceiveBuffetSize());
			peerData.setTCPNagleAlgo(peerProfileDetail.getConnectionParameters().getIsTCPNagleAlgoEnabled());
			peerData.setTransportProtocol(peerProfileDetail.getConnectionParameters().getTransportProtocol());

			peerData.setSessionCleanUpOnCER(peerProfileDetail.getIsSessionCleanupOnCER());
			peerData.setSessionCleanUpOnDPR(peerProfileDetail.getIsSessionCleanupOnDPR());
			peerData.setHotlinePolicy(peerProfileDetail.getHotlinePolicy());

			peerData.setPeerIndex(peerDetail.getPeerId());
			peerData.setPeerName(peerName);
			peerData.setURI(peerDetail.getURI());
			peerData.setHostIdentity(peerDetail.getHostIdentity());
			peerData.setSecondaryPeerName(peerDetail.getSecondaryPeerName());
			peerData.setInitiateConnectionDuration(peerProfileDetail.getConnectionParameters().getInitConnectionDuration() * 1000);
			peerData.setDuplicateConnectionPolicyType(peerProfileDetail.getDuplicateConnectionPolicyType());
			
			peerData.setRetransmissionCount(peerDetail.getRetransmissionCount());


			peerData.setRequestTimeout(peerDetail.getRequestTimeout());

			RedirectHostAVPFormat redirectHostAVPFormat = RedirectHostAVPFormat.fromRedirectHostAVPFormat(peerProfileDetail.getRedirectHostAVPFormat());
			if(redirectHostAVPFormat != null){
				peerData.setRedirectHostAVPFormat(redirectHostAVPFormat);
			}else{
				peerData.setRedirectHostAVPFormat(RedirectHostAVPFormat.DIAMETERURI);
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Invalid Redirection Host AVP Format for Peer " + peerDetail.getPeerName() + " found. Using RedirectionHostAVPFormat as : " + RedirectHostAVPFormat.DIAMETERURI);
			}
			peerData.setFollowRedirection(peerProfileDetail.getFollowRedirection());

			setRemoteAddress(peerDetail.getRemoteAddress(),peerData,peerDetail.getRealm(),profileName,serverContext);

			int dwrDuration = peerProfileDetail.getConnectionParameters().getDwrDuration();

			if(dwrDuration > 5 || dwrDuration == 0){
				peerData.setWatchdogInterval(dwrDuration * 1000);
			}else {
				int dwrDefaultTimer = 6;
				LogManager.getLogger().debug(MODULE, "Setting Default value "+dwrDefaultTimer+" to Device watchDogTimer for Profile,"+profileName+",while fetching DiameterPeerProfile Details.");
				peerData.setWatchdogInterval(dwrDefaultTimer * 1000);
			}

			setlocalAddress(peerDetail.getLocalAddress(),peerData,peerDetail.getRealm(),profileName,serverContext);

			peerData.setCERAVPString(peerProfileDetail.getStrCERAvps());
			peerData.setDPRAVPString(peerProfileDetail.getStrDPRAvps());
			peerData.setDWRAVPString(peerProfileDetail.getStrDWRAvps());

			peerData.setExclusiveAuthAppIDs(peerProfileDetail.getStrExclusiveAuthAppIds());
			peerData.setExclusiveAcctAppIDs(peerProfileDetail.getStrExclusiveAcctAppIds());					


			String realmName = peerDetail.getRealm();
			if(realmName != null && realmName.trim().length() > 0 ){
				peerData.setRealmName(realmName);
			}else{
				LogManager.getLogger().debug(MODULE, "Realm Name is  not Defined,while fetching DiameterPeerProfile Details.");
			}

			// Building URI of Peer as per URI Format defined in Peer configuration
			peerData.setURI(buildURI(peerData));


			peerData.setSecurityStandard(peerProfileDetail.getConnectionParameters().getSecurityStandard());

			peerData.setSSLParameter(createSecutiryParameter(peerProfileDetail.getSecurityParameters()));

			peerData.setHaIpAddress(peerProfileDetail.getHaIpAddress());
			peerData.setDhcpIpAddress(peerProfileDetail.getDhcpIpAddress());

			tempPeerDataList.add(peerData);
		}
		this.peerDataList = tempPeerDataList;
	}
	
	private void filterLicenseExceededPeers(ServerContext serverContext) {

		int configuredPeerSize = peerDataList.size();
		boolean isLicenseValid = serverContext.isLicenseValid(LicenseNameConstants.SYSTEM_DIAMETER_PEER, 
				String.valueOf(configuredPeerSize + 1));
		
		if (isLicenseValid == false) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Number of peers: " + configuredPeerSize + " exceeds license limit," +
						" will ignore unlicensed peers.");
			}
		}
		
		for (int i = 0; i < configuredPeerSize ; i++) {
			
			PeerData peerData = peerDataList.get(i);
			isLicenseValid = serverContext.isLicenseValid(LicenseNameConstants.SYSTEM_DIAMETER_PEER, String.valueOf(i + 1));
			if (isLicenseValid == false) {
				this.licenseExceededPeers.add(peerData);
				if(LogManager.getLogger().isWarnLogLevel())
					LogManager.getLogger().warn(MODULE, "Ignoring peer: " + peerData.getPeerName() + " as license limit exceeded.");
			}
			
		}
		peerDataList.removeAll(licenseExceededPeers);
	}

	private EliteSSLParameter createSecutiryParameter(PeerSecurityParameters securityParameters) {
		ServerCertificateProfile serverCertificateProfile = serverCertificateConfigurable.getServerCertificateProfileById(securityParameters.getServerCertificateId());
		EliteSSLParameter eliteSSLParameter = new EliteSSLParameter(serverCertificateProfile, securityParameters.getMinTlsVersion(), securityParameters.getMaxTlsVersion());
		eliteSSLParameter.setClientCertificateRequestRequired(securityParameters.getClientCertificateRequest());
		eliteSSLParameter.setValidateCertificateExpiry(securityParameters.getValidateCertificateExpiry());
		eliteSSLParameter.setValidateCertificateRevocation(securityParameters.getValidateCertificateRevocation());
		eliteSSLParameter.setValidateCertificateCA(securityParameters.getValidateCertificateCA());
		eliteSSLParameter.setValidateSubjectCN(securityParameters.getValidateHost());
		eliteSSLParameter.setHandshakeTimeout(securityParameters.getHandShakeTimeout());
		String cipherSuitesStr= securityParameters.getEnabledCiphersuites();
	
		if(cipherSuitesStr != null){
			ArrayList<CipherSuites> cipherSuites = new ArrayList<CipherSuites>();
			
			for(String cipherSuite : cipherSuitesStr.split(",")){
				try{
					CipherSuites cipherConstants = CipherSuites.fromCipherCode(Integer.parseInt(cipherSuite));
					if(cipherConstants == null){
						LogManager.getLogger().debug(MODULE, "Invalid value: " + cipherSuite + " for cipher suites. Value is not in the supported cipher suite list");
						continue;
					}
					
					cipherSuites.add(cipherConstants);
				}catch(NumberFormatException ex){
					LogManager.getLogger().debug(MODULE, "Invalid value: " + cipherSuite + " for cipher suites. Value should be in numeric");
				}
			}
			
			eliteSSLParameter.addEnabledCiphersuites(cipherSuites);
		}
		return eliteSSLParameter;
	}

	private String buildURI(PeerData peerData) {
		String uri = peerData.getURI();

		if (!uri.contains("${")){
			return uri;
		}
		//TODO When TLS configuration are introduced set aaas for peer when TLS is enabled

		//${aaa} = aaa:// or aaas:// if TLS is enabled
		uri = uri.replace("${aaa}", "aaa://");

		//${FQDN} = <Peer's_Host_name>
		uri = uri.replace("${FQDN}", peerData.getHostIdentity());

		//${port} = :<Peer's_Remote_Port>
		uri = uri.replace("${port}", ":" + peerData.getRemotePort());

		//${transport} = ;transport=
		uri = uri.replace("${transport}", ";transport=");

		//${tansport-protocol} = ;transport=<tcp_or_sctp>
		uri = uri.replace("${tansport-protocol}", ";transport="+peerData.getTransportProtocol());

		//${protocol} = ;protocol=
		uri = uri.replace("${protocol}", ";protocol=");

		//${aaa-protocol} = ;protocol=diameter
		uri = uri.replace("${aaa-protocol}", ";protocol=diameter");

		return uri;
	}
	
	private PeerProfileDetail getPeerProfile(String profileName, PeerProfileConfigurable peerProfileConfigurable) {
		
		List<PeerProfileDetail> peerProfileList = peerProfileConfigurable.getProfiles();
		for(int i=0;i<peerProfileList.size();i++){
			if(profileName.equals(peerProfileList.get(i).getName())){
				return peerProfileList.get(i);
			}
		}
		return null;
	}
	
	private void setRemoteAddress(String remoteAddress,PeerDataImpl peerData ,String realmName ,String peerProfileName,ServerContext serverContext){
		InetAddress hostInetAddress = null;
		URLData remoteUrlData = null;
		
		/*
		 * If net-mask/IP-range configured as remote address, we will set only remote address to peerData as string  
		 * The Diameter API will handle this IP-Range  
		 */
		if(DiameterUtility.isIPRange(remoteAddress)) {
			peerData.setRemoteIPAddress(remoteAddress);
			return;
		}
		
		try {
			remoteUrlData = URLParser.parse(remoteAddress);
			int remotePort = remoteUrlData.getPort() == URLParser.UNKNOWN_PORT ?DiameterConstants.DIAMETER_SERVICE_PORT:remoteUrlData.getPort();
			peerData.setRemotePort(remotePort);
			if(remoteUrlData.getHost() != null && remoteUrlData.getHost().trim().length() > 0 ){
				hostInetAddress = getInetAddress(remoteUrlData.getHost());
			}
		} catch (InvalidURLException e1) {
			LogManager.getLogger().warn(MODULE, "Invalid Remote Address : "+remoteAddress+" is configured For Peer "+peerData.getPeerName()+", Trying to resolve Host Identity");
		}catch (NumberFormatException e2) {
			LogManager.getLogger().warn(MODULE, "Invalid Remote Address :"+remoteAddress+" is configured for Peer"+peerData.getPeerName()+", Trying to resolve Host Identity");
		}
		if(hostInetAddress == null && peerData.getHostIdentity() != null){
			hostInetAddress = getInetAddress(peerData.getHostIdentity());
		}
		if(hostInetAddress != null){
			peerData.setRemoteIPAddress(hostInetAddress.getHostAddress());
			peerData.setRemoteInetAddress(hostInetAddress);
		}else{
			
			if (Strings.isNullOrBlank(remoteAddress)) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Blank remote address is configured for peer:" + peerData.getPeerName() + ", so initiate connection with peer is disabled.");
				}
			} else {
				serverContext.generateSystemAlert(AlertSeverity.ERROR, Alerts.OTHER_GENERIC, MODULE,
						"Unknown Peer Configuration For" + peerData.getPeerName(), 0,
						"Unknown Peer Configuration For" + peerData.getPeerName());
				
				LogManager.getLogger().error(MODULE, "Unknown Peer Configuration For :" + peerData.getPeerName());
			}
			
			peerData.setInitiateConnectionDuration(0);
		}
	}
	
	private InetAddress getInetAddress(String address){
		InetAddress inetAddress = null;
		try{
			if(!address.equals("0.0.0.0"))
				inetAddress = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			LogManager.getLogger().warn(MODULE, "Error While fetching Dynamic Ip Address ,Reason: Unknown host "+e.getMessage());
		}

		return inetAddress;
	}
	
	private void setlocalAddress(String localAddress,PeerDataImpl peerData,String realmName,String peerProfileName,ServerContext serverContext){
		if (localAddress == null || localAddress.trim().length() == 0){
			localAddress = "0.0.0.0";
		}
		try {
			URLData urlData = null;
			urlData = URLParser.parse(localAddress);
			String localIpAdd = urlData.getHost();
			int localPort = urlData.getPort();
			
			
			if(localIpAdd != null && localIpAdd.trim().length() > 0){
			
				if (localIpAdd.equals("0.0.0.0")){
					try {
						peerData.setLocalInetAddress(InetAddress.getLocalHost());
						peerData.setLocalIPAddress(InetAddress.getLocalHost().getHostAddress());
					} catch (UnknownHostException e) {
						serverContext.generateSystemAlert(AlertSeverity.ERROR, Alerts.OTHER_GENERIC, MODULE,
								"Ip Address 0.0.0.0 is not Found for Peer ,so using dynamic Ip Address.", 0,
								"Ip Address 0.0.0.0 is not Found for Peer ,so using dynamic Ip Address.");
						LogManager.getLogger().error(MODULE, "Error While fetching Dynamic Ip Address ,Reason : "+e.getMessage());
					}
				}else {
					try {
						peerData.setLocalIPAddress(localIpAdd);
						peerData.setLocalInetAddress(InetAddress.getByName(localIpAdd));
					} catch (UnknownHostException e) {
						serverContext.generateSystemAlert(AlertSeverity.ERROR, Alerts.OTHER_GENERIC, MODULE,
								"Configured Ip Address can not be for Peer ,so getting Ip Address From "+localIpAdd+" as Configured for peer", 0,
								"Configured Ip Address can not be for Peer ,so getting Ip Address From "+localIpAdd+" as Configured for peer");
						
						LogManager.getLogger().error(MODULE, "Error While fetching Local Inet Address for "+localIpAdd+" ,Reason : "+e.getMessage());
					}
				}

			}
			peerData.setLocalPort(localPort);
		
		} catch (InvalidURLException e1) {
			LogManager.getLogger().warn(MODULE, "Invalid Local Address : "+localAddress+" is configured for Peer "+peerData.getPeerName()+",So Peer will not added");
		}catch (NumberFormatException e2) {
			LogManager.getLogger().warn(MODULE, "Invalid Local Address : "+localAddress+" is configured for Peer"+peerData.getPeerName()+",So Peer will not added");
		}
	}

}
