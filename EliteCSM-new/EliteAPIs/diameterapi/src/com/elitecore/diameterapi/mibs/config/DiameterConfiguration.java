package com.elitecore.diameterapi.mibs.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeer;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerVendorTable;
import com.elitecore.diameterapi.mibs.statistics.MIBIndexRecorder;

public class DiameterConfiguration extends Observable implements DiameterConfigProvider {
	
	private Map<String, DiameterPeerConfig> peerConfigMap;
	private MIBIndexRecorder mibIndexRecorder;

	public DiameterConfiguration(MIBIndexRecorder mibIndexRecorder) {
		this.mibIndexRecorder = mibIndexRecorder;
		this.peerConfigMap = Collections.synchronizedMap(new LinkedHashMap<String, DiameterPeerConfig>());
	}

	public void init(Collection<DiameterPeer> diameterPeerList) {
		ConcurrentHashMap<String, DiameterPeerConfig> tempPeerConfigMap = new ConcurrentHashMap<String, DiameterPeerConfig>();
		if(diameterPeerList != null){
			for(DiameterPeer peer : diameterPeerList){
				if(peer == null || Strings.isNullOrBlank(peer.getHostIdentity())){
					continue;
				}
				mibIndexRecorder.recordIndexFor(peer.getPeerData());
				tempPeerConfigMap.put(peer.getHostIdentity(), peer.getPeerConfig());
			}
		}
		peerConfigMap = tempPeerConfigMap;
	}

	@Override
	public String getAllPeerConfigSummary() {
		StringBuilder summary = new StringBuilder();
		for (Entry<String, DiameterPeerConfig> entry : peerConfigMap.entrySet()){
			 summary.append("\n");
			 summary.append(getPeerConfigSummary(entry.getKey(), entry.getValue()));
		}
		return summary.toString();
	}
	
	@Override
	public String getPeerConfigSummary(String hostIdentity) {
		return getPeerConfigSummary(hostIdentity, peerConfigMap.get(hostIdentity));
	}
	
	private String getPeerConfigSummary(String hostIdentity, DiameterPeerConfig peerConfig) {
		
		if(peerConfig == null){
			return "Peer: "+hostIdentity+" is not registered.";
		}
		TableFormatter formatter = new TableFormatter(new String[]{"PEER: " +hostIdentity}, 
				new int[]{60}, TableFormatter.ONLY_HEADER_LINE);

		formatter.add("Peer ID            : " + peerConfig.getPeerId(), TableFormatter.LEFT);
		formatter.add("Peer Index         : " + peerConfig.getDbpPeerIndex(), TableFormatter.LEFT);
		formatter.add("Local Port         : " + peerConfig.getDbpPeerPortConnect(), TableFormatter.LEFT);
		formatter.add("IP Addresses       : " + peerConfig.getPeerIpAddresses(), TableFormatter.LEFT);
		formatter.add("Transport Protocol : " + peerConfig.getDbpPeerTransportProtocol().protocolTypeStr, TableFormatter.LEFT);
		formatter.add("Security           : " + peerConfig.getDbpPeerSecurity().protocolName, TableFormatter.LEFT);
		formatter.add("Firmware Revision  : " + getPeerFirmwareRevision(peerConfig), TableFormatter.LEFT);
		formatter.add("Supported Vendors  : " + getPeerSupportedVendorSummary(peerConfig.getDbpPeerVendorTable()), TableFormatter.LEFT);
		
		Set<ApplicationEnum> appTable = peerConfig.getDbpAppAdvFromPeer();
		if(appTable != null && appTable.size() > 0){
			formatter.addNewLine();
			formatter.add("--Supported Applications--", TableFormatter.CENTER);
			formatter.add(getPeerAppAdvSumary(appTable));
		}else{
			formatter.add("--No Applications registered--", TableFormatter.CENTER);
		}
		return formatter.getFormattedValues();
	}

	/* if Firmware not available it returns NONE for display purpose*/
	private String getPeerFirmwareRevision(DiameterPeerConfig peerConfig) {
		return peerConfig.getPeerFirmwareRevison() == 0? "NONE" : String.valueOf(peerConfig.getPeerFirmwareRevison());
	}
	
	private String getPeerSupportedVendorSummary(DiameterBasePeerVendorTable[] vendorTable) {
		
		String vendors = "None";
		if(vendorTable.length > 0){
			vendors = vendorTable[0].getDbpPeerVendorId();
		}
		for (int i = 1; i < vendorTable.length; i++) {
				vendors = vendors + ", " + vendorTable[i].getDbpPeerVendorId();
		}
		return vendors;
	}

	private String getPeerAppAdvSumary(Set<ApplicationEnum> appTable) {
		TableFormatter innerTable = new TableFormatter(
				new String[]{"Vendor-Id","Application-Id", "Service-Type"},
				new int[]{18, 18, 18}, TableFormatter.OUTER_BORDER);
		Iterator<ApplicationEnum> it = appTable.iterator();
		while(it.hasNext()){
			ApplicationEnum applicationEnum = it.next();
			innerTable.addRecord(new String[]{
					String.valueOf(applicationEnum.getVendorId()),
					String.valueOf(applicationEnum.getApplicationId()),
					applicationEnum.getApplicationType().serviceTypeStr });
		}
		return innerTable.getFormattedValues();
	}

	@Override
	public String getDCCPeerConfig(String hostIdentity, DiameterPeerConfig peerConfig) {
		
		if(peerConfig == null){
			return "Peer: "+hostIdentity+" not registered.";
		}
		TableFormatter formatter = new TableFormatter(new String[]{hostIdentity}, 
				new int[]{60}, TableFormatter.ONLY_HEADER_LINE);
		
		formatter.add("Peer Id            : " + peerConfig.getPeerId(), TableFormatter.LEFT);
		formatter.add("Peer Index         : " + peerConfig.getDbpPeerIndex(), TableFormatter.LEFT);
		formatter.add("Firmware Revision  : " + peerConfig.getPeerFirmwareRevison(), TableFormatter.LEFT);
		
		DiameterBasePeerVendorTable[] vendorTable = peerConfig.getDbpPeerVendorTable();
		if(vendorTable != null && vendorTable.length > 0){
			formatter.addNewLine();
			formatter.add("--Supported Vendors--", TableFormatter.CENTER);
			formatter.add(getPeerSupportedVendorSummary(vendorTable));
		}else{
			formatter.add("--No Vendors registered--", TableFormatter.CENTER);
		}
		return formatter.getFormattedValues();
	}
	
	@Override
	public String getAllDCCPeerConfigSummary() {
		StringBuilder summary = new StringBuilder();
		for (Entry<String, DiameterPeerConfig> entry : peerConfigMap.entrySet()){
			 summary.append(getDCCPeerConfig(entry.getKey(), entry.getValue()));
		}
		return summary.toString();
	}

	@Override
	public String getDCCPeerConfig(String hostIdentity) {
		return getDCCPeerConfig(hostIdentity, peerConfigMap.get(hostIdentity));
	}

	public void addDiameterPeer(DiameterPeer peer) {
		if (peer != null && peer.getHostIdentity() != null) {
			mibIndexRecorder.recordIndexFor(peer.getPeerData());
			peerConfigMap.put(peer.getHostIdentity(), peer.getPeerConfig());
			setChanged();
			notifyObservers(peer.getHostIdentity());
		}
	}

	@Override
	public Map<String, DiameterPeerConfig> getPeerConfigMap() {
		return peerConfigMap;
	}

	public void reload(Collection<DiameterPeer> peerList) {
		if(peerList != null){
			for(DiameterPeer peer : peerList){
				
				if(peer == null || Strings.isNullOrBlank(peer.getHostIdentity())){
					continue;
				}
					
				if (peerConfigMap.containsKey(peer.getHostIdentity()) == false) {
					addDiameterPeer(peer);
				}
			}
		}
	}

	@Override
	public DiameterPeerConfig getPeerConfig(String hostIdentity) {
		return peerConfigMap.get(hostIdentity);
	}

	@Override
	public DiameterPeerState getPeerState(String hostIdentity) {
		return DiameterPeerState.fromStateOrdinal(peerConfigMap.get(hostIdentity).getPeerState());
	}

}
