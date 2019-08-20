package com.elitecore.elitesm.datamanager.rm.ippool.data;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.util.constants.BaseConstant;

public class IPPoolStatus implements Differentiable{

	public String status;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		
		if( status != null ){
			jsonObject.put("Status", (status.equals(BaseConstant.SHOW_STATUS_ID))?"Active":"Inactive");
		}
		return jsonObject;
	}

}
