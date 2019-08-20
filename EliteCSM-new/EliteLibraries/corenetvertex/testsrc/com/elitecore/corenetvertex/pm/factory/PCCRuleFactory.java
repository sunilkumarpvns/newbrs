package com.elitecore.corenetvertex.pm.factory;

import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.PriorityLevel;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.corenetvertex.pm.constants.UsageMetering;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.PCCRuleImpl;
import com.elitecore.corenetvertex.pm.pkg.PCCRuleImpl.PCCRuleBuilder;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;

import java.util.Arrays;
import java.util.Random;

import static com.elitecore.commons.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PCCRuleFactory {
	private static final Random RANDOM = new Random();
	
	public static PCCRuleBuilder createPCCRuleWithRandomQoS() {

		RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);
		//String serviceDataFlow = new String("allow", "tcp", "any", "0", "any", "0");
		DataServiceType dataServiceType = new DataServiceType("1", "default", 1, Arrays.asList(""), Arrays.asList(ratingGroup));
		return new PCCRuleBuilder("1", "default")
				.withAppServiceProviderId("1")
				.withPriorityLevel(PriorityLevel.PRIORITY_LEVEL_10)
				.withChargingMode(com.elitecore.corenetvertex.core.constant.ChargingModes.BOTH)
				.withDynamic(true)
				.withFlowStatus(FlowStatus.ENABLED)
				.withGbrdl(randomQoS())
				.withGbrul(randomQoS())
				.withMbrdl(randomQoS())
				.withMbrul(randomQoS())
				.withMonitoringKey("default")
				.withPrecedence(1)
				.withQci(QCI.QCI_GBR_1)
				.withRatingGroup(dataServiceType.getRatingGroupList().get(0))
				.withServiceDataFlows(dataServiceType.getServiceDataFlowList())
				.withServiceType(dataServiceType)
				.withStatus(PolicyStatus.SUCCESS)
				.withUsageMetering(UsageMetering.TIME_VOLUME_QUOTA);
				
		
	}

	public static PCCRuleImpl createPCCRuleHasLowerQoSThan(PCCRuleImpl pccRule) {
		PCCRuleImpl newPCCRule = new PCCRuleBuilder(pccRule.getId() + 1, "new" + pccRule.getName())
				.withAppServiceProviderId(pccRule.getAppServiceProviderId())
				.withPriorityLevel(pccRule.getPriorityLevel())
				.withChargingMode(pccRule.getChargingMode())
				.withDynamic(pccRule.isDynamic())
				.withFlowStatus(pccRule.getFlowStatus())
				.withGbrdl(pccRule.getGBRDL() - 1)
				.withGbrul(pccRule.getGBRUL() - 1)
				.withMbrdl(pccRule.getMBRDL() - 1)
				.withMbrul(pccRule.getMBRUL() - 1)
				.withMonitoringKey(pccRule.getMonitoringKey())
				.withPeCapability(pccRule.getPeCapability())
				.withPeVulnerability(pccRule.getPeVulnerability())
				.withPrecedence(pccRule.getPrecedence())
				.withQci(pccRule.getQCI())
				.withChargingKey(pccRule.getChargingKey())
				.withServiceDataFlows(pccRule.getServiceDataFlows())
				.withServiceName(pccRule.getServiceName())
				.withServiceIdentifier(pccRule.getServiceIdentifier())
				.withStatus(pccRule.getStatus())
				.withUsageMetering(pccRule.getUsageMetering())
				.build();
		
		assertEquals(-1, newPCCRule.compareTo(pccRule));

		return checkNotNull(newPCCRule, "PCCRule should not be null");
	}

	public static PCCRuleImpl createPCCRuleHasHigherQoSThan(PCCRuleImpl pccRule) {
		PCCRuleImpl newPCCRule = new PCCRuleBuilder(pccRule.getId() + 1, "new" + pccRule.getName())
				.withAppServiceProviderId(pccRule.getAppServiceProviderId())
				.withPriorityLevel(pccRule.getPriorityLevel())
				.withChargingMode(pccRule.getChargingMode())
				.withDynamic(pccRule.isDynamic())
				.withFlowStatus(pccRule.getFlowStatus())
				.withGbrdl(pccRule.getGBRDL() + 1)
				.withGbrul(pccRule.getGBRUL() + 1)
				.withMbrdl(pccRule.getMBRDL() + 1)
				.withMbrul(pccRule.getMBRUL() + 1)
				.withMonitoringKey(pccRule.getMonitoringKey())
				.withPeCapability(pccRule.getPeCapability())
				.withPeVulnerability(pccRule.getPeVulnerability())
				.withPrecedence(pccRule.getPrecedence())
				.withQci(pccRule.getQCI())
				.withChargingKey(pccRule.getChargingKey())
				.withServiceDataFlows(pccRule.getServiceDataFlows())
				.withServiceName(pccRule.getServiceName())
				.withServiceIdentifier(pccRule.getServiceIdentifier())
				.withStatus(pccRule.getStatus())
				.withUsageMetering(pccRule.getUsageMetering())
				.build();
		
		assertEquals(1, newPCCRule.compareTo(pccRule));

		return checkNotNull(newPCCRule, "PCCRule should not be null");
	}

	public static PCCRuleImpl createPCCRuleHasEqualQoSTo(PCCRuleImpl pccRule) {
		PCCRuleImpl newPCCRule = new PCCRuleBuilder(pccRule.getId() + 1, "new" + pccRule.getName())
				.withAppServiceProviderId(pccRule.getAppServiceProviderId())
				.withPriorityLevel(pccRule.getPriorityLevel())
				.withChargingMode(pccRule.getChargingMode())
				.withDynamic(pccRule.isDynamic())
				.withFlowStatus(pccRule.getFlowStatus())
				.withGbrdl(pccRule.getGBRDL())
				.withGbrul(pccRule.getGBRUL())
				.withMbrdl(pccRule.getMBRDL())
				.withMbrul(pccRule.getMBRUL())
				.withMonitoringKey(pccRule.getMonitoringKey())
				.withPeCapability(pccRule.getPeCapability())
				.withPeVulnerability(pccRule.getPeVulnerability())
				.withPrecedence(pccRule.getPrecedence())
				.withQci(pccRule.getQCI())
				.withChargingKey(pccRule.getChargingKey())
				.withServiceDataFlows(pccRule.getServiceDataFlows())
				.withServiceName(pccRule.getServiceName())
				.withServiceIdentifier(pccRule.getServiceIdentifier())
				.withStatus(pccRule.getStatus())
				.withUsageMetering(pccRule.getUsageMetering())
				.build();
		
		assertEquals(0, pccRule.compareTo(newPCCRule));

		return checkNotNull(newPCCRule, "PCCRule should not be null");
	}
	
	private static int randomQoS() {
		return RANDOM.nextInt(Integer.MAX_VALUE);
	}

	public static PCCRuleImpl createPCCRuleHasLowerFUPLevelThan(PCCRuleImpl pccRule) {
		PCCRuleImpl newPCCRule = new PCCRuleBuilder(pccRule.getId() + 1, "new" + pccRule.getName())
				.withAppServiceProviderId(pccRule.getAppServiceProviderId())
				.withPriorityLevel(pccRule.getPriorityLevel())
				.withChargingMode(pccRule.getChargingMode())
				.withDynamic(pccRule.isDynamic())
				.withFlowStatus(pccRule.getFlowStatus())
				.withGbrdl(pccRule.getGBRDL())
				.withGbrul(pccRule.getGBRUL())
				.withMbrdl(pccRule.getMBRDL())
				.withMbrul(pccRule.getMBRUL())
				.withMonitoringKey(pccRule.getMonitoringKey())
				.withPeCapability(pccRule.getPeCapability())
				.withPeVulnerability(pccRule.getPeVulnerability())
				.withPrecedence(pccRule.getPrecedence())
				.withQci(pccRule.getQCI())
				.withChargingKey(pccRule.getChargingKey())
				.withServiceDataFlows(pccRule.getServiceDataFlows())
				.withServiceName(pccRule.getServiceName())
				.withServiceIdentifier(pccRule.getServiceIdentifier())
				.withStatus(pccRule.getStatus())
				.withUsageMetering(pccRule.getUsageMetering())
				.withFUPLevel(pccRule.getFupLevel()-1)
				.build();

		assertTrue(newPCCRule.getFupLevel() < pccRule.getFupLevel());

		return checkNotNull(newPCCRule, "PCCRule should not be null");
	}

	public static PCCRuleImpl createPCCRuleHasHigherFUPLevelThan(PCCRuleImpl pccRule) {
		PCCRuleImpl newPCCRule = new PCCRuleBuilder(pccRule.getId() + 1, "new" + pccRule.getName())
				.withAppServiceProviderId(pccRule.getAppServiceProviderId())
				.withPriorityLevel(pccRule.getPriorityLevel())
				.withChargingMode(pccRule.getChargingMode())
				.withDynamic(pccRule.isDynamic())
				.withFlowStatus(pccRule.getFlowStatus())
				.withGbrdl(pccRule.getGBRDL())
				.withGbrul(pccRule.getGBRUL())
				.withMbrdl(pccRule.getMBRDL())
				.withMbrul(pccRule.getMBRUL())
				.withMonitoringKey(pccRule.getMonitoringKey())
				.withPeCapability(pccRule.getPeCapability())
				.withPeVulnerability(pccRule.getPeVulnerability())
				.withPrecedence(pccRule.getPrecedence())
				.withQci(pccRule.getQCI())
				.withChargingKey(pccRule.getChargingKey())
				.withServiceDataFlows(pccRule.getServiceDataFlows())
				.withServiceName(pccRule.getServiceName())
				.withServiceIdentifier(pccRule.getServiceIdentifier())
				.withStatus(pccRule.getStatus())
				.withUsageMetering(pccRule.getUsageMetering())
				.withFUPLevel(pccRule.getFupLevel()+1)
				.build();

		assertTrue(newPCCRule.getFupLevel() > pccRule.getFupLevel());

		return checkNotNull(newPCCRule, "PCCRule should not be null");
	}
}
