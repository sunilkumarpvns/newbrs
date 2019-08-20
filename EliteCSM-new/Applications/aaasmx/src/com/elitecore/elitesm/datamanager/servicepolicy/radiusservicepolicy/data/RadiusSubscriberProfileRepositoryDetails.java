package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

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
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;

@XmlRootElement(name = "user-profile-repository")
@XmlType(propOrder = {"updateIdentity", "driverDetails", "anonymousProfileIdentity"})
public class RadiusSubscriberProfileRepositoryDetails extends ServicePolicyHandlerDataSupport implements SubscriberProfileRepositoryDetails, AuthServicePolicyHandlerData, AcctServicePolicyHandlerData {
	private String anonymousProfileIdentity;
	
	@Valid
	private RadiusProfileDriversDetails driverDetails;
	
	@Valid
	private UpdateIdentityParamsDetail updateIdentity;
	
	public RadiusSubscriberProfileRepositoryDetails() {
		updateIdentity = new UpdateIdentityParamsDetail();
		driverDetails = new RadiusProfileDriversDetails();
	}
	
	@Override
	@XmlElement(name = "anonymous-identity")
	public String getAnonymousProfileIdentity() {
		return this.anonymousProfileIdentity;
	}

	public void setAnonymousProfileIdentity(String anonymousProfileIdentity) {
		this.anonymousProfileIdentity = anonymousProfileIdentity;
	}

	@Override
	@XmlElement(name = "update-identity")
	public UpdateIdentityParamsDetail getUpdateIdentity() {
		return updateIdentity;
	}

	public void setUpdateIdentity(UpdateIdentityParamsDetail updateIdentity) {
		this.updateIdentity = updateIdentity;
	}

	@Override
	@XmlElement(name = "profile-drivers")
	public RadiusProfileDriversDetails getDriverDetails() {
		return driverDetails;
	}

	public void setDriverDetails(RadiusProfileDriversDetails driverDetails) {
		this.driverDetails = driverDetails;
	}
	
	@Override
	public void postRead() {
	}

	@Override
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
		return null;
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return null;
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
				secondaryDriversList.add(secondaryDriverDetail.getSecondaryDriverId() + "|" + Strings.valueOf(secondaryDriverDetail.getCacheDriverId()));
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
