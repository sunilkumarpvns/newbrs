package com.elitecore.core.util.mbean.data.live;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EliteNetSystemStatusData implements Serializable {
	
	private static final long serialVersionUID = 1L;

	//private String moduleName;
	private Map<String, List<EliteNetRemoteSystemData>> remoteSystemGroupMap;
        
        public EliteNetSystemStatusData(){
            remoteSystemGroupMap = new HashMap<String, List<EliteNetRemoteSystemData>>();
        }

	public Map<String, List<EliteNetRemoteSystemData>> getRemoteSystemGroupMap() {
		return remoteSystemGroupMap;
	}

	public void setRemoteSystemGroupMap(Map<String, List<EliteNetRemoteSystemData>> remoteSystemGroupMap) {
		this.remoteSystemGroupMap = remoteSystemGroupMap;
	}
	
        public void addRemoteSystemGroup(String groupName,EliteNetRemoteSystemData eliteNetRemoteSystemData){
            List<EliteNetRemoteSystemData> remoteSystemList = remoteSystemGroupMap.get(groupName);
            
            if (remoteSystemList == null){
                remoteSystemList = new ArrayList<EliteNetRemoteSystemData>();
                remoteSystemGroupMap.put(groupName, remoteSystemList);
            }
            
            remoteSystemList.add(eliteNetRemoteSystemData);
        }
}
