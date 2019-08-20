package com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data;

import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
   
public class IMSIBasedRoutingTableData extends BaseData implements Differentiable{
	     
	    private String routingTableId;
	    
	    @Expose
	    @SerializedName("IMSI Based Routing Table Name")
	    private String routingTableName;
	    
	    @Expose
	    @SerializedName("IMSI Identity Attribute")
	    private String imsiIdentityAttributes;
	    
	    private String auditUId;
		private Set<IMSIFieldMappingData> imsiFieldMappingDataSet;
	    
		public String getRoutingTableId() {
			return routingTableId;
		}
		public void setRoutingTableId(String routingTableId) {
			this.routingTableId = routingTableId;
		}
		public String getImsiIdentityAttributes() {
			return imsiIdentityAttributes;
		}
		public void setImsiIdentityAttributes(String imsiIdentityAttributes) {
			this.imsiIdentityAttributes = imsiIdentityAttributes;
		}
		public String getAuditUId() {
			return auditUId;
		}
		public void setAuditUId(String auditUId) {
			this.auditUId = auditUId;
		}
		public String getRoutingTableName() {
			return routingTableName;
		}
		public void setRoutingTableName(String routingTableName) {
			this.routingTableName = routingTableName;
		}
		
		@Override
		public String toString() {
			return "IMSIBasedRoutingTableData [routingTableId="
					+ routingTableId + ", routingTableName=" + routingTableName
					+ ", imsiIdentityAttributes=" + imsiIdentityAttributes
					+ ", auditUId=" + auditUId + "]";
		}
		
		public Set<IMSIFieldMappingData> getImsiFieldMappingDataSet() {
			return imsiFieldMappingDataSet;
		}
		public void setImsiFieldMappingDataSet(
				Set<IMSIFieldMappingData> imsiFieldMappingDataSet) {
			this.imsiFieldMappingDataSet = imsiFieldMappingDataSet;
		}
		
		@Override
		public JSONObject toJson() {
			JSONObject object = new JSONObject();
			object.put("Routing Table Name", routingTableName);
			object.put("IMSI Identity Attribute ", imsiIdentityAttributes);
			
			if(imsiFieldMappingDataSet != null){
				JSONArray array = new JSONArray();
				for(IMSIFieldMappingData imsiFieldMappingData :imsiFieldMappingDataSet){
					array.add(imsiFieldMappingData.toJson());
				}
				object.put("IMSI Based Routing Entries ", array);
			}
			return object;
		}
}
