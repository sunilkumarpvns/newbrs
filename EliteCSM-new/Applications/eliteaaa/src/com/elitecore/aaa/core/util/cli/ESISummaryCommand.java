package com.elitecore.aaa.core.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.mibs.radius.accounting.client.RadiusAcctClientMIB;
import com.elitecore.aaa.mibs.radius.authentication.client.RadiusAuthClientMIB;
import com.elitecore.aaa.mibs.radius.dynauth.client.RadiusDynAuthClientMIB;
import com.elitecore.aaa.mibs.rm.charging.client.RMChargingClientMIB;
import com.elitecore.aaa.mibs.rm.ippool.client.RMIPPoolClientMIB;
import com.elitecore.aaa.mibs.sm.client.RemoteSessionManagerClientMIB;
import com.elitecore.aaa.radius.conf.RadESConfiguration;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;

public class ESISummaryCommand extends EliteBaseCommand {
	
	private AAAServerContext serverContext;
	
	@Override
	public String execute(String parameter) {
		if(parameter==null || parameter.trim().length()==0){
			return getConfiguredESIList();
		}else {
			RadESConfiguration radESConfiguration = serverContext.getServerConfiguration().getRadESConfiguration();
			DefaultExternalSystemData externalSystemData=null;
			Map<String, DefaultExternalSystemData> esiMap=null;
			if (radESConfiguration != null && (esiMap = radESConfiguration.getAllESI()) != null && esiMap.size() > 0) {
				for (Entry<String, DefaultExternalSystemData> entry : esiMap.entrySet()) {
					if(entry.getValue().getName().equals(parameter)){
						externalSystemData = entry.getValue();
						break;
					}
				}
				if (externalSystemData != null) {
					if(externalSystemData.getEsiType()==RadESConfiguration.RadESTypeConstants.RAD_AUTH_PROXY.type){
						return RadiusAuthClientMIB.getSummary(parameter);
					} else if (externalSystemData.getEsiType()==RadESConfiguration.RadESTypeConstants.RAD_ACCT_PROXY.type) {
						 return RadiusAcctClientMIB.getSummary(parameter);
					} else if (externalSystemData.getEsiType()==RadESConfiguration.RadESTypeConstants.NAS.type) {
						 return RadiusDynAuthClientMIB.getSummary(parameter);
					} else if (externalSystemData.getEsiType()==RadESConfiguration.RadESTypeConstants.CHARGING_GATEWAY.type) {
						 return RMChargingClientMIB.getSummary(parameter);
					} else if (externalSystemData.getEsiType()==RadESConfiguration.RadESTypeConstants.IP_POOL_SERVER.type) {
						 return RMIPPoolClientMIB.getSummary(parameter);
					} else if (externalSystemData.getEsiType()==RadESConfiguration.RadESTypeConstants.PREPAID_SERVER.type) {
						 return "";
					} else if (externalSystemData.getEsiType()==RadESConfiguration.RadESTypeConstants.SESSION_MANAGER.type) {
						 return RemoteSessionManagerClientMIB.getSummary(parameter);
					} else {
						return "Invalid Name ESI Name Given";
					}
				} else {
					return "Invalid Name ESI Name Given";
				}
				
			}else {
				return "No ESI configured";
			}
		}
		
	}
	
	private String getConfiguredESIList() {
		RadESConfiguration radESConfiguration = serverContext.getServerConfiguration().getRadESConfiguration();
		Map<String, DefaultExternalSystemData> esiMap=null;
		if(radESConfiguration!=null && (esiMap =radESConfiguration.getAllESI())!=null && esiMap.size()>0){
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.println("-------- Configured ESI ----------");
			int i=0;
			DefaultExternalSystemData externalSystemData;
			for (Entry<String, DefaultExternalSystemData> entry : esiMap.entrySet()) {
				
				externalSystemData = entry.getValue();
				if (externalSystemData != null && (externalSystemData.getEsiType() == RadESConfiguration.RadESTypeConstants.RAD_AUTH_PROXY.type || externalSystemData.getEsiType() == RadESConfiguration.RadESTypeConstants.RAD_ACCT_PROXY.type || externalSystemData.getEsiType() == RadESConfiguration.RadESTypeConstants.NAS.type 
						|| externalSystemData.getEsiType() == RadESConfiguration.RadESTypeConstants.IP_POOL_SERVER.type ||
							externalSystemData.getEsiType() == RadESConfiguration.RadESTypeConstants.SESSION_MANAGER.type ||
							externalSystemData.getEsiType() == RadESConfiguration.RadESTypeConstants.CHARGING_GATEWAY.type)) {
					++i;
					out.println("("+i+") "+entry.getValue().getName());
				}
			
					
			}
			 out.close();
			 return stringWriter.toString();
		}else {
			return "No ESI Configured";
		}
	}

	public ESISummaryCommand(AAAServerContext serverContext) {
		this.serverContext = serverContext;
	}

	@Override
	public String getCommandName() {
		return "esisummary";
	}

	@Override
	public String getDescription() {
		return "Displays Request Summary Of Particular ESI";
	}

	@Override
	public String getHotkeyHelp() {
		return "{'esisummary':{'esiname':{}}}";
	}
}
