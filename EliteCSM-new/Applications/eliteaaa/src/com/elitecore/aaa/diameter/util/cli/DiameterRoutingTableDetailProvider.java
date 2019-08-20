package com.elitecore.aaa.diameter.util.cli;

import java.util.HashMap;
import java.util.List;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.conf.DiameterRoutingTableConfiguration;
import com.elitecore.aaa.diameter.conf.impl.RoutingEntryDataImpl;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.diameter.common.data.RoutingEntryData;

public class DiameterRoutingTableDetailProvider extends DetailProvider {

	private final static String ROUTETABLE    = "routingtable";
	private final static String ALLCONF     = "all";
	private final static String ROUTELIST     = "list";
	private final static String ROUTENAME   = "name";

	private HashMap<String ,DetailProvider> detailProviderMap;
	private AAAServerContext serverContext;
	public DiameterRoutingTableDetailProvider(AAAServerContext serverContext){
		this.serverContext = serverContext;
		detailProviderMap = new HashMap<String ,DetailProvider>();
	}
	@Override
	public String execute(String[] parameters) {
		String routingTableName = serverContext.getServerConfiguration().getDiameterStackConfiguration().getRoutingTableName();
		DiameterRoutingTableConfiguration routingTableConfiguration = serverContext.getServerConfiguration().getDiameterRoutingConfiguration().getDiameterRoutingTableConfiguration(routingTableName);
		if(routingTableConfiguration == null) {
			return "Routing-Table Configuration not found for: " + routingTableName;
		}
		StringBuilder responseBuilder=new StringBuilder();
		if (parameters != null && parameters.length > 0){
			if(ROUTENAME.equalsIgnoreCase(parameters[0]) && parameters.length > 1){
				List<RoutingEntryDataImpl> routingEntryDatas= routingTableConfiguration.getRoutingEntryDataList();
				for(RoutingEntryData routingEntryData : routingEntryDatas){
					if(parameters[1].equalsIgnoreCase(routingEntryData.getRoutingName())){
						responseBuilder.append(routingEntryData+"\n");
						break;
					}
				}
				return responseBuilder.toString();
			}else if (ROUTELIST.equalsIgnoreCase(parameters[0])){
				List<RoutingEntryDataImpl> routingEntryDatas= routingTableConfiguration.getRoutingEntryDataList();
				for(RoutingEntryData routingEntryData : routingEntryDatas){
					responseBuilder.append(routingEntryData.getRoutingName()+"\n");
				}
				return responseBuilder.toString();
			} else if(ALLCONF.equalsIgnoreCase(parameters[0])){
				List<RoutingEntryDataImpl> routingEntryDatas= routingTableConfiguration.getRoutingEntryDataList();
				for(RoutingEntryData routingEntryData : routingEntryDatas){
					responseBuilder.append(routingEntryData+"\n");
				}
				return responseBuilder.toString();
			} 
		} 
		responseBuilder.append("Provide valid arguments\n");
		return responseBuilder.append(getHelpMsg()).toString();
	}

	@Override
	public String getHelpMsg() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage : show diameter routingtable [<options>]\n");
		responseBuilder.append("--------------------------------Possible Options:--------------------------------\n");
		responseBuilder.append(ALLCONF+"						 	  : Display details of all Routing configuration\n");
		responseBuilder.append(ROUTELIST+"							  : Display the list of Routing Table entry names \n");
		responseBuilder.append(ROUTENAME+" <route-config-name>		  : Display route configuration for given routing entry\n");
		return responseBuilder.toString();
	}

	@Override
	public String getKey() {
		return ROUTETABLE;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}

	@Override
	public String getHotkeyHelp() {
		return "'"+ROUTETABLE+"':{'"+ALLCONF+"':{},'"+ROUTENAME+"':{},'"+ROUTELIST+"':{}}";
	}
}
