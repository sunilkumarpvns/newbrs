package com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data;

import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
   
public class MSISDNBasedRoutingTableData extends BaseData implements Differentiable{
	     
	    private String routingTableId;
	    
	    @Expose
	    @SerializedName("MSISDN Based Routing Table Name")
	    private String routingTableName;
	    
	    @Expose
	    @SerializedName("MSISDN Identity Attribute")
	    private String msisdnIdentityAttributes;
	    
	    @Expose
	    @SerializedName("MSISDN Length")
	    private Long msisdnLength;
	    
	    @Expose
	    @SerializedName("MCC")
	    private String mcc;
	    
	    private String auditUId;
		private Set<MSISDNFieldMappingData> msisdnFieldMappingDataSet;
		
		public String getRoutingTableId() {
			return routingTableId;
		}

		public void setRoutingTableId(String routingTableId) {
			this.routingTableId = routingTableId;
		}

		public String getRoutingTableName() {
			return routingTableName;
		}

		public void setRoutingTableName(String routingTableName) {
			this.routingTableName = routingTableName;
		}

		public String getMsisdnIdentityAttributes() {
			return msisdnIdentityAttributes;
		}

		public void setMsisdnIdentityAttributes(String msisdnIdentityAttributes) {
			this.msisdnIdentityAttributes = msisdnIdentityAttributes;
		}

		public String getAuditUId() {
			return auditUId;
		}

		public void setAuditUId(String auditUId) {
			this.auditUId = auditUId;
		}

		public Long getMsisdnLength() {
			return msisdnLength;
		}

		public void setMsisdnLength(Long msisdnLength) {
			this.msisdnLength = msisdnLength;
		}

		public String getMcc() {
			return mcc;
		}

		public void setMcc(String mcc) {
			this.mcc = mcc;
		}

		public Set<MSISDNFieldMappingData> getMsisdnFieldMappingDataSet() {
			return msisdnFieldMappingDataSet;
		}

		public void setMsisdnFieldMappingDataSet(
				Set<MSISDNFieldMappingData> msisdnFieldMappingDataSet) {
			this.msisdnFieldMappingDataSet = msisdnFieldMappingDataSet;
		}

		@Override
		public JSONObject toJson() {
			JSONObject object = new JSONObject();
			object.put("Routing Table Name", routingTableName);
			object.put("MSISDN Identity Attribute ", msisdnIdentityAttributes);
			object.put("MSISDN Length ", msisdnLength);
			object.put("MCC ", mcc);
			
			if(msisdnFieldMappingDataSet != null){
				JSONArray array = new JSONArray();
				for(MSISDNFieldMappingData msisdnFieldMappingData :msisdnFieldMappingDataSet){
					array.add(msisdnFieldMappingData.toJson());
				}
				object.put("MSISDN Based Routing Entries ", array);
			}
			return object;
		}
}
