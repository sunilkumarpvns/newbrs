package com.elitecore.aaa.diameter.subscriber;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import com.elitecore.aaa.core.conf.impl.UpdateIdentityParamsDetail;
import com.elitecore.aaa.core.subscriber.conf.SubscriberProfileRepositoryDetails;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterSubscriberProfileRepositoryHandler;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterApplicationHandlerDataSupport;
import com.elitecore.aaa.radius.conf.impl.AdditionalDriverDetail;
import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;
import com.elitecore.aaa.radius.conf.impl.SecondaryAndCacheDriverDetail;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;

@XmlRootElement(name = "user-profile-repository")
public class DiameterSubscriberProfileRepositoryDetails extends DiameterApplicationHandlerDataSupport 
implements SubscriberProfileRepositoryDetails{

	private UpdateIdentityParamsDetail updateIdentityParameters;
	private DiameterProfileDriverDetails profileDriverDetails;
	private List<String> userIdentities;
	private String anonymousProfileIdentity;
	private String userName;
	private String userNameResponseAttribute;
	private static final char COMMA = ',';
	private List<String> userNameResponseAttributeList;
	
	public DiameterSubscriberProfileRepositoryDetails() {
		updateIdentityParameters = new UpdateIdentityParamsDetail();
		profileDriverDetails = new DiameterProfileDriverDetails();
		userIdentities = new ArrayList<String>();
		userNameResponseAttributeList = new ArrayList<String>(0);
	}
	
	@Override
	@XmlElement(name = "update-identity")
	public UpdateIdentityParamsDetail getUpdateIdentity() {
		return updateIdentityParameters;
	}
	
	
	public void setUpdateIdentity(UpdateIdentityParamsDetail updateIdentityParameters) {
		this.updateIdentityParameters = updateIdentityParameters;
	}


	@Override
	@XmlElement(name = "profile-drivers")
	public DiameterProfileDriverDetails getDriverDetails() {
		return profileDriverDetails;
	}
	
	public void setDriverDetails(DiameterProfileDriverDetails profileDriverDetails) {
		this.profileDriverDetails = profileDriverDetails;
	}
	
	@XmlElementWrapper(name = "user-identities")
	@XmlElement(name = "user-identity")
	public List<String> getUserIdentities() {
		return userIdentities;
	}

	public void setUserIdentities(List<String> userIdentities) {
		this.userIdentities = userIdentities;
	}
 
	public void addPrimaryDriverDetail(String driverId, Integer weightage) {
		profileDriverDetails.getPrimaryDriverGroup().add(new PrimaryDriverDetail(driverId, weightage));
	}

	public void addAdditionalDriverDetail(String additionalDriverId) {
		profileDriverDetails.getAdditionalDriverList().add(new AdditionalDriverDetail(additionalDriverId));
	}
	
	public void addSecondaryDriverDetail(String secondaryDriverId, String cacheDriverId) {
		profileDriverDetails.getSecondaryDriverGroup().add(new SecondaryAndCacheDriverDetail(secondaryDriverId, cacheDriverId));
	}

	@Override
	public String getAnonymousProfileIdentity() {
		return anonymousProfileIdentity;
	}
	
	public void setAnonymousProfileIdentity(String anonymousProfileIdentity) {
		this.anonymousProfileIdentity = anonymousProfileIdentity;
	}
	
	@XmlElement(name = "user-name")
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@XmlElement(name = "user-name-response-attribute")
	public String getUserNameResponseAttribute() {
		return userNameResponseAttribute;
	}
	
	public void setUserNameResponseAttribute(String userNameResponseAttribute) {
		this.userNameResponseAttribute = userNameResponseAttribute;
	}
	
	@Override
	public DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> createHandler(
			DiameterServiceContext context) {
		return new DiameterSubscriberProfileRepositoryHandler();
	}
	
	@XmlTransient
	public List<String> getUserNameResponseAttributeList() {
		return userNameResponseAttributeList;
	}

	@Override
	public void postRead() {
		super.postRead();
		registerPrimaryDrivers();
		registerSecondaryDrivers();
		registerAdditionalDrivers();
	}
	
	public void postResponseProcessingForUserNameResponeAttribute(){
		if(Strings.isNullOrEmpty(userNameResponseAttribute) == false){
			userNameResponseAttributeList = Strings.splitter(COMMA).trimTokens().split(userNameResponseAttribute);
		}
	}
	
	private void registerAdditionalDrivers() {
		for (AdditionalDriverDetail driverDetail : profileDriverDetails.getAdditionalDriverList()) {
			getPolicyData().registerRequiredDriverId(driverDetail.getDriverId());
		}
	}

	private void registerSecondaryDrivers() {
		for (SecondaryAndCacheDriverDetail driverDetail : profileDriverDetails.getSecondaryDriverGroup()) {
			getPolicyData().registerRequiredDriverId(driverDetail.getSecondaryDriverId());
		}
	}

	private void registerPrimaryDrivers() {
		for (PrimaryDriverDetail driverDetail : profileDriverDetails.getPrimaryDriverGroup()) {
			getPolicyData().registerRequiredDriverId(driverDetail.getDriverInstanceId());
		}
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Profile Lookup Handler | Enabled: %s", 10, ' '), isEnabled()));
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
		object.put("Enabled", isEnabled());
		object.put("Update User Identity", updateIdentityParameters.toJson());
		object.put("Anonymous Profile Identity", anonymousProfileIdentity);
		
		List<PrimaryDriverDetail> primaryDrivers = profileDriverDetails.getPrimaryDriverGroup();
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
		
		List<SecondaryAndCacheDriverDetail> secondaryDrivers = profileDriverDetails.getSecondaryDriverGroup();
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
		
		List<AdditionalDriverDetail> additionalDrivers = profileDriverDetails.getAdditionalDriverList();
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
		
		object.put("Driver Script", profileDriverDetails.getDriverScript().trim());
		return object;
	}
}