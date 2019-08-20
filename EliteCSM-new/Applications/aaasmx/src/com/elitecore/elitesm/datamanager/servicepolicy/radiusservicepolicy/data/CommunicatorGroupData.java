package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;

@XmlRootElement(name = "communicator-group")
public class CommunicatorGroupData implements Differentiable{

	private List<CommunicatorData> communicatorDataList = new ArrayList<CommunicatorData>();

	@XmlElement(name = "communicator")
	public List<CommunicatorData> getCommunicatorDataList() {
		return communicatorDataList;
	}
	
	@XmlTransient
	public int getGroupSize() {
		return communicatorDataList.size();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		if(Collectionz.isNullOrEmpty(communicatorDataList) == false){
			JSONArray array = new JSONArray();
			
			int communicatorDataListSize = communicatorDataList.size();
			for (int i = 0; i < communicatorDataListSize; i++) {
				array.add(communicatorDataList.get(i).toJson());
			}
			
			if(array.size() > 0){
				object.put("Server/Group Entry", array);
			}
			
		}
		return object;
	}
}
