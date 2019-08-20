package com.elitecore.aaa.radius.service.auth.policy.handlers.conf;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.conf.impl.AdditionalDriverDetail;
import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;
import com.elitecore.aaa.radius.conf.impl.RadiusProfileDriversDetails;
import com.elitecore.aaa.radius.conf.impl.SecondaryAndCacheDriverDetail;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.AcctCdrHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.conf.AcctServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.AuthCdrHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ServicePolicyHandlerDataSupport;
import com.elitecore.commons.base.Collectionz;

/**
 * 
 * @author narendra.pathai
 *
 */
@XmlRootElement(name = "cdr-handler-entry")
public class CdrHandlerEntryData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData, AcctServicePolicyHandlerData {

	private String ruleset;
	private RadiusProfileDriversDetails driverDetails;
	private boolean wait;
	
	@XmlAttribute(name = "wait")
	public boolean isWait() {
		return wait;
	}

	public void setWait(boolean wait) {
		this.wait = wait;
	}

	@XmlElement(name = "ruleset")
	public String getRuleset() {
		return ruleset;
	}
	
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}

	@XmlElement(name = "cdr-drivers")
	public RadiusProfileDriversDetails getDriverDetails() {
		return driverDetails;
	}

	public void setDriverDetails(RadiusProfileDriversDetails driverDetails) {
		this.driverDetails = driverDetails;
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return new AuthCdrHandler(serviceContext, this);
	}

	@Override
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
		return new AcctCdrHandler(serviceContext, this);
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
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(format("%-30s: %s", "Ruleset", getRuleset() != null ? getRuleset() : ""));
		out.print(getDriverDetails());
		out.println(format("%-30s: %s", "Wait", isWait()));
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Rule Set", ruleset);
		
		List<PrimaryDriverDetail> primaryDrivers = driverDetails.getPrimaryDriverGroup();
		List<String> primaryDriversList = new ArrayList<String>();
		if(Collectionz.isNullOrEmpty(primaryDrivers) == false){
			int primaryDriverSize = primaryDrivers.size();
			
			for (int i = 0; i < primaryDriverSize; i++) {
				PrimaryDriverDetail primaryDriverDetail = primaryDrivers.get(i);
				primaryDriversList.add(primaryDriverDetail.getDriverInstanceId());
			}
			
			if( Collectionz.isNullOrEmpty(primaryDriversList) == false ){
				String primaryDriverString = primaryDriversList.toString().replaceAll("[\\s\\[\\]]", "");
				object.put("Primary Driver", primaryDriverString);
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
			
			if( Collectionz.isNullOrEmpty(secondaryDriversList) == false ){
				String secondaryDriverString = secondaryDriversList.toString().replaceAll("[\\s\\[\\]]", "");
				object.put("Secondary Driver", secondaryDriverString);
			}
		}
		
		object.put("Wait For CDR To Dump", wait);
		object.put("Driver Script", driverDetails.getDriverScript());
		return object;
	}
}
