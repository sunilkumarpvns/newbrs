package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

public class ServerAdapter extends XmlAdapter<String, String>{

	@Override
	public String unmarshal(String serverName) throws Exception {
		String serverId = null;
		if(Strings.isNullOrEmpty(serverName) ==false){
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			try {
				if(Strings.isNullOrBlank(serverName) == false){
					serverId = netServerBLManager.getNetServerIdByName(serverName);
				} else {
					serverId = RestValidationMessages.INVALID;
				}
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
	public String marshal(String v) throws Exception {
		
		String serverName = null;
		try {
			
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			
			INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(v);
			serverName = netServerInstanceData.getName();
		
		} catch(Exception e){
			e.printStackTrace();
			serverName = "";
		}
		return serverName;
	}

}
