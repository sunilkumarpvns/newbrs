package com.elitecore.elitesm.datamanager.servermgr.drivers.detailacctlocal.data;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class DetailLocalAttrRelationData extends BaseData implements Differentiable{
	
	private String attrRelId;
	private String attrIds;
	private String defaultValue;
	private String useDictionaryValue;
	private String detailLocalId;
	
	
		public String getAttrIds() {
			return attrIds;
		}
		public void setAttrIds(String attrIds) {
			this.attrIds = attrIds;
		}
		public String getDefaultValue() {
			return defaultValue;
		}
		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}
		public String getUseDictionaryValue() {
			return useDictionaryValue;
		}
		public void setUseDictionaryValue(String useDictionaryValue) {
			this.useDictionaryValue = useDictionaryValue;
		}
		public String getDetailLocalId() {
			return detailLocalId;
		}
		public void setDetailLocalId(String detailLocalId) {
			this.detailLocalId = detailLocalId;
		}
		public String getAttrRelId() {
			return attrRelId;
		}
		public void setAttrRelId(String attrRelId) {
			this.attrRelId = attrRelId;
		}
		@Override
		public JSONObject toJson() {
			JSONObject object = new JSONObject();
			if(attrIds!=null){
				object.put(attrIds,
						new JSONObject().accumulate("DefaultValue", defaultValue)
						.accumulate("Use Dictionary Value", useDictionaryValue)
						);
			}
			return object;
		}
		
}

