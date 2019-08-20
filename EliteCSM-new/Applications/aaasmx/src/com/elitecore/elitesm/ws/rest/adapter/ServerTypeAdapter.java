package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

public class ServerTypeAdapter extends XmlAdapter<String, String>{

	@Override
	public String unmarshal(String serverName) throws Exception {
		String serverId = null;
		if(Strings.isNullOrEmpty(serverName) ==false){
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			try {
				serverId = netServerBLManager.getNetServerTypeIdByName(serverName);
			}
			catch(Exception e){
				e.printStackTrace();
				serverId = RestValidationMessages.INVALID;
			}
		} else {
			serverId = RestValidationMessages.INVALID;
		}
		return serverId;
	}

	@Override
	public String marshal(String serverId) throws Exception {
		String serverType = null;
		try {
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			serverType = netServerBLManager.getNetServerTypeNameById(serverId);
		} catch(Exception e){
			serverType = "";
		}
		return serverType;
	}
	
}
