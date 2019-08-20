package com.elitecore.aaa.radius.subscriber.conf;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONObject;

import com.elitecore.aaa.core.conf.impl.UpdateIdentityParamsDetail;
import com.elitecore.aaa.core.subscriber.conf.SubscriberProfileRepositoryDetails;
import com.elitecore.aaa.radius.conf.impl.AdditionalDriverDetail;
import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;
import com.elitecore.aaa.radius.conf.impl.RadiusProfileDriversDetails;
import com.elitecore.aaa.radius.conf.impl.SecondaryAndCacheDriverDetail;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.AcctSubscriberProfileRepositoryHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.conf.AcctServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.AuthSubscriberProfileRepositoryHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ServicePolicyHandlerDataSupport;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;

/**
 * 
 * @author narendra.pathai
 *
 */
@XmlRootElement(name = "user-profile-repository")
public class RadiusSubscriberProfileRepositoryDetails extends ServicePolicyHandlerDataSupport implements SubscriberProfileRepositoryDetails, AuthServicePolicyHandlerData, AcctServicePolicyHandlerData {
	private String anonymousProfileIdentity;
	private RadiusProfileDriversDetails driverDetails;
	private UpdateIdentityParamsDetail updateIdentity;
	
	public RadiusSubscriberProfileRepositoryDetails() {
		updateIdentity = new UpdateIdentityParamsDetail();
		driverDetails = new RadiusProfileDriversDetails();
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.aaa.radius.policies.servicepolicy.restructured.UserProfileRepositoryDetails#getAnnonymousProfileIdentity()
	 */
	@Override
	@XmlElement(name = "anonymous-identity")
	public String getAnonymousProfileIdentity() {
		return this.anonymousProfileIdentity;
	}

	public void setAnonymousProfileIdentity(String anonymousProfileIdentity) {
		this.anonymousProfileIdentity = anonymousProfileIdentity;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.aaa.radius.policies.servicepolicy.restructured.UserProfileRepositoryDetails#getUpdateIdentity()
	 */
	@Override
	@XmlElement(name = "update-identity")
	public UpdateIdentityParamsDetail getUpdateIdentity() {
		return updateIdentity;
	}

	public void setUpdateIdentity(UpdateIdentityParamsDetail updateIdentity) {
		this.updateIdentity = updateIdentity;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.aaa.radius.policies.servicepolicy.restructured.UserProfileRepositoryDetails#getDriverDetails()
	 */
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
		super.postRead();
		registerPrimaryDrivers();
		registerSecondaryDrivers();
		registerAdditionalDrivers();
	}

	private void registerAdditionalDrivers() {
		for (AdditionalDriverDetail driverDetail : driverDetails.getAdditionalDriverList()) {
			getRadiusServicePolicyData().registerRequiredDriver(driverDetail.getDriverId());
		}
	}

	private void registerSecondaryDrivers() {
		for (SecondaryAndCacheDriverDetail driverDetail : driverDetails.getSecondaryDriverGroup()) {
			getRadiusServicePolicyData().registerRequiredDriver(driverDetail.getSecondaryDriverId());
			if (driverDetail.getCacheDriverId() != null) {
				getRadiusServicePolicyData().registerRequiredDriver(driverDetail.getCacheDriverId());
			}
		}
	}

	private void registerPrimaryDrivers() {
		for (PrimaryDriverDetail driverDetail : driverDetails.getPrimaryDriverGroup()) {
			getRadiusServicePolicyData().registerRequiredDriver(driverDetail.getDriverInstanceId());
		}
	}
	
	@Override
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
		return new AcctSubscriberProfileRepositoryHandler(serviceContext, this);
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return new AuthSubscriberProfileRepositoryHandler();
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
