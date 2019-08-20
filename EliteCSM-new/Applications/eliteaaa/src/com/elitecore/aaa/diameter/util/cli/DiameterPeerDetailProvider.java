package com.elitecore.aaa.diameter.util.cli;

import java.util.HashMap;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;

public class DiameterPeerDetailProvider extends DetailProvider {

	private static final String PEER = "peer";
	private static final String HOST_IDENTIY = "<Host Identity>";
	private DiameterConfigProvider configProvider;
	private HashMap<String, DetailProvider> detailProviderMap;

	public DiameterPeerDetailProvider(DiameterConfigProvider provider) {
		detailProviderMap = new HashMap<String, DetailProvider>();
		configProvider = provider;
	}

	@Override
	public String execute(String[] parameters) {
		
		if (parameters != null && parameters.length >= 1 && Strings.isNullOrBlank(parameters[0]) == false) {
			
			String peerCmdArg = parameters[0];
			
			if (EliteBaseCommand.isHelpParameter(peerCmdArg)) {
				return getHelpMsg();
			}

			return configProvider.getPeerConfigSummary(peerCmdArg);
		}

		return configProvider.getAllPeerConfigSummary();
	}

	@Override
	public String getHelpMsg() {
		StringBuilder responseBuilder = new StringBuilder();
		responseBuilder.append("\nUsage 	 : show diameter config " + PEER + " [" + HOST_IDENTIY + "]");
		responseBuilder.append("\nDescription: Displays Configuration Details of All Peers.\n(If provided with Host Identity, displays details of that Peer.)");
		return responseBuilder.toString();
	}

	@Override
	public String getKey() {
		return PEER;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}

	@Override
	public String getDescription() {
		return "Display Configuration details of Diameter Peer";
	}

}
