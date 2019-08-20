package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AdditionalDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.PrimaryDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.SecondaryAndCacheDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.SubscriberProfileRepositoryDetails;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.UpdateIdentityParamsDetail;

@XmlRootElement(name = "user-profile-repository")
@XmlType(propOrder = {"updateIdentity", "driverDetails", "anonymousProfileIdentity"})
public class DiameterSubscriberProfileRepositoryDetails extends DiameterApplicationHandlerDataSupport 
implements SubscriberProfileRepositoryDetails{

	@Valid
	private UpdateIdentityParamsDetail updateIdentity;
	
	@Valid
	private DiameterProfileDriverDetails driverDetails;
	private String anonymousProfileIdentity;
	private String userName;
	private String userNameResponseAttribute;
	
	public DiameterSubscriberProfileRepositoryDetails() {
		updateIdentity = new UpdateIdentityParamsDetail();
		driverDetails = new DiameterProfileDriverDetails();
	}
	
	@Override
	@XmlElement(name = "update-identity")
	public UpdateIdentityParamsDetail getUpdateIdentity() {
		return updateIdentity;
	}
	
	
	public void setUpdateIdentity(UpdateIdentityParamsDetail updateIdentityParameters) {
		this.updateIdentity = updateIdentityParameters;
	}


	@Override
	@XmlElement(name = "profile-drivers")
	public DiameterProfileDriverDetails getDriverDetails() {
		return driverDetails;
	}
	
	public void setDriverDetails(DiameterProfileDriverDetails profileDriverDetails) {
		this.driverDetails = profileDriverDetails;
	}
	
	@Override
	@XmlElement(name = "annonymous-profile-identity")
	public String getAnonymousProfileIdentity() {
		return anonymousProfileIdentity;
	}
	
	public void setAnonymousProfileIdentity(String anonymousProfileIdentity) {
		this.anonymousProfileIdentity = anonymousProfileIdentity;
	}
	
//	@XmlElement(name = "user-name")
	@XmlTransient
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
//	@XmlElement(name = "user-name-response-attribute")
	@XmlTransient
	public String getUserNameResponseAttribute() {
		return userNameResponseAttribute;
	}
	
	public void setUserNameResponseAttribute(String userNameResponseAttribute) {
		this.userNameResponseAttribute = userNameResponseAttribute;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Profile Lookup Handler | Enabled: %s", 10, ' '), getEnabled()));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Anonymous Identity",
				getAnonymousProfileIdentity() != null ? getAnonymousProfileIdentity() : ""));
		out.println(getUpdateIdentity());
		out.println(getDriverDetails());
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Enabled", getEnabled());
		object.put("Update User Identity", updateIdentity.toJson());
		object.put("Anonymous Profile Identity", anonymousProfileIdentity);
		
		List<PrimaryDriverDetail> primaryDrivers = driverDetails.getPrimaryDriverGroup();
		List<String> primaryDriversList = new ArrayList<String>();
		if(Collectionz.isNullOrEmpty(primaryDrivers) == false){
			int primaryDriverSize = primaryDrivers.size();
			
			for (int i = 0; i < primaryDriverSize; i++) {
				PrimaryDriverDetail primaryDriverDetail = primaryDrivers.get(i);
				primaryDriversList.add(primaryDriverDetail.getDriverInstanceId() + "|" + primaryDriverDetail.getWeightage());
			}
			
			if(Collectionz.isNullOrEmpty(primaryDriversList) == false){
				String primaryDriverString = primaryDriversList.toString().replaceAll("[\\s\\[\\]]", "");
				object.put("Primary Group", primaryDriverString);
			}
		}
		
		List<SecondaryAndCacheDriverDetail> secondaryDrivers = driverDetails.getSecondaryDriverGroup();
		List<String> secondaryDriversList = new ArrayList<String>();
		if(Collectionz.isNullOrEmpty(secondaryDrivers) == false){
			int secondaryDriverSize = secondaryDrivers.size();
			
			for (int i = 0; i < secondaryDriverSize; i++) {
				SecondaryAndCacheDriverDetail secondaryDriverDetail = secondaryDrivers.get(i);
				secondaryDriversList.add(secondaryDriverDetail.getSecondaryDriverId());
			}
			
			if(Collectionz.isNullOrEmpty(secondaryDriversList) == false){
				String secondaryDriverString = secondaryDriversList.toString().replaceAll("[\\s\\[\\]]", "");
				object.put("Secondary Group", secondaryDriverString);
			}
		}
		
		List<AdditionalDriverDetail> additionalDrivers = driverDetails.getAdditionalDriverList();
		List<String> additionalDriversList = new ArrayList<String>();
		if(Collectionz.isNullOrEmpty(additionalDrivers) == false){
			int additionalDriversSize = additionalDrivers.size();
			
			for (int i = 0; i < additionalDriversSize; i++) {
				AdditionalDriverDetail additionalDriverDetail = additionalDrivers.get(i);
				additionalDriversList.add(additionalDriverDetail.getDriverId());
			}
			
			if(Collectionz.isNullOrEmpty(additionalDriversList) == false){
				String additionalDriverString = additionalDriversList.toString().replaceAll("[\\s\\[\\]]", "");
				object.put("Additional Group", additionalDriverString);
			}
		}
		
		object.put("Driver Script", driverDetails.getDriverScript().trim());
		return object;
	}
}