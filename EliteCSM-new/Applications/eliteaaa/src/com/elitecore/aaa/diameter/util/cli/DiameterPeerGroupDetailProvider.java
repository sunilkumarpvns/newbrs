package com.elitecore.aaa.diameter.util.cli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.conf.impl.DiameterPeerGroupConfigurable;
import com.elitecore.aaa.diameter.conf.impl.PeerGroupData;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;

public class DiameterPeerGroupDetailProvider extends DetailProvider {

	private static final String PEER_GROUP = "peergroup";
	private static final String PEER_GROUP_NAME = "<Peer Group Name>";
	
	private AAAServerContext serverContext;
	private HashMap<String, DetailProvider> detailProviderMap;
	
	private Map<String, PeerGroupData> groupIdToGroup = new HashMap<String, PeerGroupData>();
	private Map<String, PeerGroupData> groupNameToGroup = new HashMap<String, PeerGroupData>();

	public DiameterPeerGroupDetailProvider(AAAServerContext serverContext) {
		
		this.serverContext = serverContext;
		detailProviderMap = new HashMap<String, DetailProvider>();
		DiameterPeerGroupConfigurable peerGroupConfiguration = serverContext.getServerConfiguration().getDiameterPeerGroupConfigurable();
		this.groupIdToGroup = peerGroupConfiguration.getGroupIdMap();
		this.groupNameToGroup = peerGroupConfiguration.getGroupNameToGroup();
	}

	@Override
	public String execute(String[] parameters) {
		
		List<PeerData> peerDataList = serverContext.getServerConfiguration().getDiameterPeerConfiguration().getPeerDataList();
		Map<String, PeerData> peerDetailsByName = new HashMap<String, PeerData>(peerDataList.size());
		for (PeerData data : peerDataList) {
			peerDetailsByName.put(data.getPeerName(), data);
		}

		if (parameters != null && parameters.length >= 1 && Strings.isNullOrBlank(parameters[0]) == false) {
			String peerGroupArg = parameters[0];
			if (EliteBaseCommand.isHelpParameter(peerGroupArg)) {
				return getHelpMsg();
			}
			return getPeerGroupConfigSummary(peerGroupArg, peerDetailsByName);
		}

		return getAllPeerGroupConfigSummary(peerDetailsByName);
	}

	@Override
	public String getHelpMsg() {
		StringBuilder responseBuilder = new StringBuilder();
		responseBuilder.append("\nUsage 	 : show diameter config " + PEER_GROUP + " [" + PEER_GROUP_NAME + "]");
		responseBuilder.append("\nDescription: Displays Configuration Details of All Peer Groups.\n(If provided with Peer Group Name, displays details of that Peer Group.)");
		return responseBuilder.toString();
	}

	@Override
	public String getKey() {
		return PEER_GROUP;
	}

	private String getAllPeerGroupConfigSummary(Map<String,PeerData> peerDetailsByName) {
		
		StringBuilder summary = new StringBuilder();
		
		for (Entry<String, PeerGroupData> entry : groupNameToGroup.entrySet()) {
			summary.append("\n");
			summary.append(getPeerGroupConfigSummary(entry.getKey(), entry.getValue(), peerDetailsByName));
		}
		return summary.toString();
	}

	private String getPeerGroupConfigSummary(String peerGroupName, PeerGroupData peerGroupData, Map<String,PeerData> nameToPeerDataMap) {

		if (Strings.isNullOrBlank(peerGroupName) || peerGroupData == null) {
			return "Peer Group: " + peerGroupName + " Does not exists.";
		}
		TableFormatter formatter = new TableFormatter(new String[] { "PEER GROUP : " + peerGroupName },
				new int[] { 60 }, TableFormatter.ONLY_HEADER_LINE);
		formatter.add("Peer Group Name                :  " + peerGroupData.getName(), TableFormatter.LEFT);
		formatter.add("Stateful                       :  " + peerGroupData.isStateFull(), TableFormatter.LEFT);
		formatter.add("Transaction Timeout(in ms)     :  " + peerGroupData.getTransactionTimeoutInMs(), TableFormatter.LEFT);
		formatter.add("Geo Redundant Group            :  " + (Strings.isNullOrBlank(peerGroupData.getGeoRedunduntGroupId())
				? "NONE" : this.groupIdToGroup.get(peerGroupData.getGeoRedunduntGroupId()).getName()), TableFormatter.LEFT);
		formatter.add("Peers                          :  ", TableFormatter.LEFT);

		for (PeerInfoImpl peerInfo : peerGroupData.getPeers()) {
			
			formatter.add("--------------------------------", TableFormatter.CENTER);

			PeerData peerConfiguration = nameToPeerDataMap.get(peerInfo.getPeerName());

			formatter.add("\tPEER Name                  :  " + peerInfo.getPeerName(), TableFormatter.LEFT);
			
			if (peerConfiguration != null) {
				formatter.add("\tHost Identity              :  " + peerConfiguration.getHostIdentity(), TableFormatter.LEFT);
			}
			formatter.add("\tLoad Factor                :  " + peerInfo.getLoadFactor(), TableFormatter.LEFT);

		}

		return formatter.getFormattedValues();

	}

	private String getPeerGroupConfigSummary(String peerGroupName, Map<String,PeerData> peerDetailsByName) {
		return getPeerGroupConfigSummary(peerGroupName, groupNameToGroup.get(peerGroupName), peerDetailsByName);
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}

	@Override
	public String getDescription() {
		return "Display Configuration details of Diameter Peer Group";
	}

}
