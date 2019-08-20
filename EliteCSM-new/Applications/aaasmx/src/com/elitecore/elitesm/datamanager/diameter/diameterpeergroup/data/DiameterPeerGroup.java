package com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpeergroup.DiameterPeerGroupBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.DiameterPeerGroupAdapter;
import com.elitecore.elitesm.ws.rest.adapter.NumericAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
   
/**
 * @author nayana.rathod
 *
 */

@XmlRootElement(name="diameter-peer-group")
@XmlType(propOrder = { "peerGroupName", "description", "peerList","stateful","transactionTimeout", "geoRedunduntGroup"})
@ValidObject
public class DiameterPeerGroup extends BaseData implements Differentiable,Validator{
	     
    	private String peerGroupId;
    	
    	@NotEmpty(message="Name must be specified")
    	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
    	@Expose
  	    @SerializedName("Peer Group Name")
    	private String peerGroupName;
    	
    	@Expose
    	@SerializedName("Description")
    	private String description;
    	
    	private String auditUId;
    	
    	@NotNull(message="Peer Group must be configured")
    	private List<DiameterPeerRelationWithPeerGroup> peerList;

    	@Expose
  	    @SerializedName("Stateful")
    	private String stateful;
    	
    	@Expose
  	    @SerializedName("Transaction Timeout")
    	private Long transactionTimeout;
    	
    	@Expose
  	    @SerializedName("Geo Redundant Group")
    	private String geoRedunduntGroup;
    	
    	public DiameterPeerGroup() {
    		description = RestUtitlity.getDefaultDescription();
    	}

		@XmlElement(name="stateful")
		@NotEmpty(message="Stateful must be specified")
		@Pattern(regexp = RestValidationMessages.REGEX_TRUE_FALSE, message = "Invalid Value of Stateful( allow only true or false)")
		public String getStateful() {
			return stateful;
		}

		public void setStateful(String statefull) {
			this.stateful = statefull.toLowerCase();
		}

		@XmlElement(name="transaction-timeout")
		@NotNull(message="Transaction Timeout must be specified")
		@Min(value=0,message="Transaction Timeout must be numeric")
		@XmlJavaTypeAdapter(value=NumericAdapter.class)
		public Long getTransactionTimeout() {
			return transactionTimeout;
		}

		public void setTransactionTimeout(Long transactionTimeout) {
			this.transactionTimeout = transactionTimeout;
		}
		
		
    	@XmlElementWrapper(name="peer-group")
    	@XmlElement(name="peer-entry")
    	@Size(min=1,message="At least one peer with weightage must be specified in Peer Group")
    	@Valid
    	public List<DiameterPeerRelationWithPeerGroup> getPeerList() {
			return peerList;
		}

		public void setPeerList(List<DiameterPeerRelationWithPeerGroup> peerList) {
			this.peerList = peerList;
		}

    	@XmlTransient
		public String getPeerGroupId() {
			return peerGroupId;
		}

		public void setPeerGroupId(String peerGroupId) {
			this.peerGroupId = peerGroupId;
		}

		@XmlElement(name = "name",type = String.class)
		public String getPeerGroupName() {
			return peerGroupName;
		}

		public void setPeerGroupName(String peerGroupName) {
			this.peerGroupName = peerGroupName;
		}

		@XmlTransient
		public String getAuditUId() {
			return auditUId;
		}

		public void setAuditUId(String auditUId) {
			this.auditUId = auditUId;
		}
		
		@XmlElement(name = "description",type = String.class)
		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		@XmlElement(name = "geo-redundunt-group",type = String.class)
		@XmlJavaTypeAdapter(value=DiameterPeerGroupAdapter.class)
		public String getGeoRedunduntGroup() {
			return geoRedunduntGroup;
		}

		public void setGeoRedunduntGroup(String geoRedunduntGroup) {
			this.geoRedunduntGroup = geoRedunduntGroup;
		}
		
		@Override
		public JSONObject toJson() {
			JSONObject object = new JSONObject();
			object.put("Peer Group Name", peerGroupName);
			object.put("Description",description);
			
			if(Collectionz.isNullOrEmpty(peerList) == false){
				JSONArray peers = new JSONArray();
				for (DiameterPeerRelationWithPeerGroup peerDetail : peerList) {
					if(peerDetail != null && Strings.isNullOrBlank(peerDetail.getPeerName()) == false && peerDetail.getWeightage() != null){
						peers.add(peerDetail.getPeerName() + "-W-" + peerDetail.getWeightage());
					}	
				}
				object.put("Peer Group",peers);
			}
			
			object.put("Stateful", this.stateful);
			object.put("Transaction Timeout(ms)", this.transactionTimeout);
			
			String geoRedunduntGroupName = EliteSMReferencialDAO.fetchDiameterPeerGroupData(this.geoRedunduntGroup);
			if( "-".equals(geoRedunduntGroupName)){
				object.put("Geo Redundunt Group", "NONE");
			}else{
				object.put("Geo Redundunt Group", geoRedunduntGroupName);
			}
			return object;
		}

		@Override
		public boolean validate(ConstraintValidatorContext context) {
			boolean isValid = true;
			List<DiameterPeerRelationWithPeerGroup> peers = getPeerList();
			Set<String> peerNames = new HashSet<String>();
			
			if(Collectionz.isNullOrEmpty(peers) == false){
				for(DiameterPeerRelationWithPeerGroup peerDetail : peers){
					String peerName = peerDetail.getPeerName();
					if(peerNames.add(peerName.trim()) == false){
						RestUtitlity.setValidationMessage(context,"Duplicate Peer found in Peer List: "+peerName);
						isValid = false;
						break;
					}
				}
			}
			
			if (Strings.isNullOrBlank(this.geoRedunduntGroup) == false){
				if (RestValidationMessages.INVALID.equals(this.geoRedunduntGroup)) {
					RestUtitlity.setValidationMessage(context,"Invalid Geo Redundunt Group");
					isValid = false;
				} else {
					
					DiameterPeerGroupBLManager diameterPeerGroupBLManager = new DiameterPeerGroupBLManager();
					
					try {
						DiameterPeerGroup diameterPeerGroup = diameterPeerGroupBLManager.getDiameterPeerGroupById(this.geoRedunduntGroup);
						if(diameterPeerGroup.getPeerGroupName().equals(peerGroupName)){
							RestUtitlity.setValidationMessage(context, "Geo Redundunt Group can not be same as Group Name");
							isValid = false;
						}
					} catch (DataManagerException e) {
						e.printStackTrace();
					}
				}
			}
			
			return isValid;
		}
}
