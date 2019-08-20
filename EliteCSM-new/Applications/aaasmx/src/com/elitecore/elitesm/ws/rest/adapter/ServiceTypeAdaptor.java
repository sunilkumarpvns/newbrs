package com.elitecore.elitesm.ws.rest.adapter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceData;

public class ServiceTypeAdaptor extends XmlAdapter<NetServiceData,NetServiceData>{

	@Override
	public NetServiceData unmarshal(NetServiceData servicesData) throws Exception {
		List<String> serverTypeIds = new ArrayList<String>();
		NetServiceData netServicesData= new NetServiceData();
		List<String> servicesNames = servicesData.getServicesNames();
		
		if (Collectionz.isNullOrEmpty(servicesNames) == false) {
			NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
			
			for(String serviceName:servicesNames){
				String serviceId = "";
				if(Strings.isNullOrBlank(serviceName) == false){
					try {
						serviceId = netServiceBLManager.getNetServiceTypeIdByName(serviceName);
					} catch (Exception e) {
						e.printStackTrace();
						serviceId = "Invalid";
					}
					
					if(Strings.isNullOrBlank(serviceId)){
						serviceId = "Invalid";
					}
					
					serverTypeIds.add(serviceId);
				}
			}
			
		}
		
		netServicesData.setServicesNames(serverTypeIds);
		return netServicesData;
	}

	@Override
	public NetServiceData marshal(NetServiceData servicesData) throws Exception {
		return servicesData;
	}
}
