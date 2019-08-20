package com.elitecore.netvertex.service.pcrf.servicepolicy.conf.impl;

import com.elitecore.corenetvertex.constants.RequestAction;
import com.elitecore.corenetvertex.constants.ServicePolicyStatus;
import com.elitecore.corenetvertex.constants.SyMode;
import com.elitecore.corenetvertex.constants.UnknownUserAction;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.core.driver.spr.DriverConfiguration;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.PccServicePolicyConfiguration;

/**
 *
 * @author Manjil Purohit
 *
 */
public class PccServicePolicyConfigurationImpl implements PccServicePolicyConfiguration {
	private String servicePolicyId;

	private String name;
	private String description;
	private String ruleset;
	private RequestAction action;

	private String subscriberLookupOn;
	private String identityAttribute;
	private UnknownUserAction unknownUserAction;
	private String unknownUserPkgId;
	private String unknownUserPkgName;
	private String syGateway;
	private SyMode syMode = SyMode.PULL;
	private DriverConfiguration policyCdrDriver;
	private DriverConfiguration chargingCdrDriver;
	private Integer orderNumber;
	private ServicePolicyStatus status;

	public PccServicePolicyConfigurationImpl(String servicePolicyId,
											 String name,
											 String description,
											 String ruleset,
											 RequestAction action,
											 String subscriberLookupOn,
											 String identityAttribute,
											 UnknownUserAction unknownUserAction,
											 String unknownUserPkgId,
											 String unknownUserPkgName,
											 String syGateway,
											 SyMode syMode,
											 DriverConfiguration policyCdrDriver,
											 DriverConfiguration chargingCdrDriver,
											 Integer orderNumber,
											 ServicePolicyStatus status){
		this.servicePolicyId = servicePolicyId;
		this.name = name;
		this.description = description;
		this.ruleset = ruleset;
		this.action = action;
		this.subscriberLookupOn = subscriberLookupOn;
		this.identityAttribute = identityAttribute;
		this.unknownUserAction = unknownUserAction;
		this.unknownUserPkgId = unknownUserPkgId;
		this.unknownUserPkgName = unknownUserPkgName;
		this.syGateway = syGateway;
		this.syMode = syMode;
		this.policyCdrDriver = policyCdrDriver;
		this.chargingCdrDriver = chargingCdrDriver;
		this.orderNumber = orderNumber;
		this.status = status;
	}

	@Override
	public String getPcrfPolicyId() { return servicePolicyId; }

	@Override
	public String getName() { return name; }

	@Override
	public String getDescription() { return description; }

	@Override
	public String getRuleset() { return ruleset; }

	@Override
	public RequestAction getAction() { return action; }

	@Override
	public String getSubscriberLookupOn() { return subscriberLookupOn; }

	@Override
	public String getIdentityAttribute() { return identityAttribute; }

	@Override
	public UnknownUserAction getUnknownUserAction() { return unknownUserAction; }

	@Override
	public String getUnknownUserPkgId() { return unknownUserPkgId; }

	@Override
	public String getUnknownUserPkgName() { return unknownUserPkgName; }

	@Override
	public String getSyGateway() { return syGateway; }

	@Override
	public SyMode getSyMode() { return syMode; }

	@Override
	public DriverConfiguration getPolicyCdrDriver() { return policyCdrDriver; }

	@Override
	public DriverConfiguration getChargingCdrDriver() { return chargingCdrDriver; }

	@Override
	public Integer getOrderNumber() { return orderNumber; }

	@Override
	public ServicePolicyStatus getStatus() { return status; }

	@Override
	public String toString(){
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- PCRF Service Policy Configuration -- ");
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.incrementIndentation();
		builder.append("Id", servicePolicyId);
		builder.append("Name", name);
		builder.append("Description", description);
		builder.append("Ruleset", ruleset);
		builder.append("Action", action);
		builder.append("Order Number", orderNumber);
		builder.append("Status", status);
		builder.append("Subscriber Lookup On", subscriberLookupOn);
		builder.append("Identity Attribute", identityAttribute);
		builder.append("Unknown User Action", unknownUserAction);
		if(UnknownUserAction.ALLOW_UNKNOWN_USER == unknownUserAction) {
			builder.append("Unknown User Package", unknownUserPkgName);
		}
		builder.append("Sy Gateway", syGateway);
		builder.append("Sy Mode", syMode);
		builder.appendChildObject("Policy CDR Driver", policyCdrDriver);
		builder.appendChildObject("Charging CDR Driver", chargingCdrDriver);
		builder.decrementIndentation();
	}
}