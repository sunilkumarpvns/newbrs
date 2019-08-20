package com.elitecore.netvertex.core.mapping.gx;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.TgppR9Builder;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.UMBuilder;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.PCCToDiameterGxMapping;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.utility.AttributeFactories;
import com.elitecore.netvertex.gateway.diameter.utility.AttributeFactory;
import com.elitecore.netvertex.gateway.diameter.utility.AvpAccumalatorTestSupport;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.usagemetering.ServiceUnit;
import com.elitecore.netvertex.usagemetering.UMLevel;
import com.elitecore.netvertex.usagemetering.UsageMonitoringInfo;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class PCCToDiameterGxMappingTest {

	private PCCToDiameterGxMapping pccToDiameterGxMapping;
	private DiameterPacketMappingValueProvider valueProvider;
	private AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();
	private PCRFResponse pcrfResponse;
	private DiameterPacket diameterPacket;
	private AttributeFactory attributeFactory = AttributeFactories.fromDummyDictionary();

	@Before
	public void before() {
		pccToDiameterGxMapping = new PCCToDiameterGxMapping();
		pcrfResponse = new PCRFResponseImpl();
		diameterPacket = new DiameterAnswer();
		valueProvider = new DiameterPacketMappingValueProvider(pcrfResponse,
				diameterPacket,
				mock(DiameterGatewayConfiguration.class),
				new DummyDiameterGatewayControllerContext());

		DummyDiameterDictionary.getInstance();
	}

	public class PCCRuleRemove {
		
		private List<String> createRemovablePCCRules() {
			return Arrays.asList("removable_pcc1", "removable_pcc2", "removable_pcc3");
		}
		
		@Test
		public void added_InAccumulator_WhenFoundInPCRFResponse() {
			
			pcrfResponse.setRemovablePCCRules(createRemovablePCCRules());
			
			pccToDiameterGxMapping.apply(valueProvider, accumalator);
			
			ReflectionAssert.assertLenientEquals(createExpectedRemovablePCCAVPs(), accumalator.getAll());
		}
		
		@Test
		public void notAdded_InAccumulator_WhenNotFoundInPCRFResponse() {
			
			pccToDiameterGxMapping.apply(valueProvider, accumalator);
			
			ReflectionAssert.assertLenientEquals(Collectionz.newArrayList(), accumalator.getAll());
		}
		
		
		private List<IDiameterAVP> createExpectedRemovablePCCAVPs() {
			
			ArrayList<IDiameterAVP> avps = new ArrayList<IDiameterAVP>();
			
			for (String pcc : createRemovablePCCRules()) {
				avps.add(attributeFactory.create(DiameterAVPConstants.TGPP_CHARGING_RULE_NAME, pcc));
			}
			
			IDiameterAVP groupAVP = attributeFactory.create(DiameterAVPConstants.TGPP_CHARGING_RULE_REMOVE);
			groupAVP.setGroupedAvp(avps);
			
			return Arrays.asList(groupAVP);
		}
	}
	
	public class CRBNRemove {
		
		private List<String> createRemovableCRBN() {
			return Arrays.asList("removable_crbn1", "removable_crbn2", "removable_crbn3");
		}
		
		@Test
		public void added_InAccumulator_WhenFoundInPCRFResponse() {
			
			pcrfResponse.setRemovableChargingRuleBaseNames(createRemovableCRBN());
			
			pccToDiameterGxMapping.apply(valueProvider, accumalator);
			
			ReflectionAssert.assertLenientEquals(createExpectedRemovableCRBNAVPs(), accumalator.getAll());
		}
		
		@Test
		public void notAdded_InAccumulator_WhenNotFoundInPCRFResponse() {
			
			pccToDiameterGxMapping.apply(valueProvider, accumalator);
			
			ReflectionAssert.assertLenientEquals(Collectionz.newArrayList(), accumalator.getAll());
		}
		
		private List<IDiameterAVP> createExpectedRemovableCRBNAVPs() {
			
			ArrayList<IDiameterAVP> avps = new ArrayList<IDiameterAVP>();
			
			for (String crbn : createRemovableCRBN()) {
				avps.add(attributeFactory.create(DiameterAVPConstants.TGPP_CHARGING_RULE_BASE_NAME, crbn));
			}
			
			IDiameterAVP groupAVP = attributeFactory.create(DiameterAVPConstants.TGPP_CHARGING_RULE_REMOVE);
			groupAVP.setGroupedAvp(avps);
			
			return Arrays.asList(groupAVP);
		}
	}

	public class CRBNInstall {

		
		private List<ChargingRuleBaseName> createInstallableCRBN() {
			
			return Arrays.asList(new ChargingRuleBaseName(null, "isntallable_crbn1", null, 0, null)
				, new ChargingRuleBaseName(null, "isntallable_crbn2", null, 0, null)
				, new ChargingRuleBaseName(null, "isntallable_crbn3", null, 0, null));
		}
		
		@Test
		public void added_InAccumulator() {
			
			pcrfResponse.setInstallableChargingRuleBaseNames(createInstallableCRBN());
			
			pccToDiameterGxMapping.apply(valueProvider, accumalator);
			
			ReflectionAssert.assertLenientEquals(createExpectedInstallableCRBNAVPs(), accumalator.getAll());
		}
		
		@Test
		public void notAdded_InAccumulator() {
			
			pccToDiameterGxMapping.apply(valueProvider, accumalator);
			
			ReflectionAssert.assertLenientEquals(Collectionz.newArrayList(), accumalator.getAll());
		}
		
		private List<IDiameterAVP> createExpectedInstallableCRBNAVPs() {
			
			ArrayList<IDiameterAVP> avps = new ArrayList<IDiameterAVP>();
			
			for (ChargingRuleBaseName crbn : createInstallableCRBN()) {
				avps.add(attributeFactory.create(DiameterAVPConstants.TGPP_CHARGING_RULE_BASE_NAME, crbn.getName()));
			}
			
			IDiameterAVP groupAVP = attributeFactory.create(DiameterAVPConstants.TGPP_CHARGING_RULE_INSTALL);
			groupAVP.setGroupedAvp(avps);
			
			return Arrays.asList(groupAVP);
		}
	
	} 
	
	public class UMBuilderAVPs {
		
		@Before
		public void setUp() {
			
			UMBuilder umBuilder = new TgppR9Builder();

			UsageMonitoringInfo monitoringInfo = createUM();
			ArrayList<UsageMonitoringInfo> monitoringInfos = new ArrayList<>(2);
			monitoringInfos.add(monitoringInfo);
			pcrfResponse.setUsageMonitoringInfoList(monitoringInfos);
			pccToDiameterGxMapping = new PCCToDiameterGxMapping(umBuilder);
		}

		private UsageMonitoringInfo createUM() {
			UsageMonitoringInfo usageInfo = new UsageMonitoringInfo();
			usageInfo.setMonitoringKey("mk1");
			usageInfo.setUsageMonitoringLevel(UMLevel.PCC_RULE_LEVEL);
			usageInfo.setGrantedServiceUnit(new ServiceUnit.ServiceUnitBuilder().withTotalOctets(100).build());
			return usageInfo;
		}

		@Test
		public void notAddedIn_AvpAccumulator_supportedStandard_Is_CiscoSCE() {

			DiameterGatewayConfiguration configuration = valueProvider.getDiameterGatewayConfiguration();

			when(configuration.getSupportedStandard()).thenReturn(SupportedStandard.CISCOSCE);

			pccToDiameterGxMapping.apply(valueProvider, accumalator);

			ReflectionAssert.assertLenientEquals(Collections.emptyList(), accumalator.getAll());
		}

		@Test
		public void notAddedIn_AvpAccumulator_usageNotFoundInPCRFResponse() {

			pcrfResponse.setUsageMonitoringInfoList(null);
			pccToDiameterGxMapping.apply(valueProvider, accumalator);

			ReflectionAssert.assertLenientEquals(Collections.emptyList(), accumalator.getAll());
		}


		@Test
		public void added_InAccumulator_WhenGrantedUsageFoundInPCRFResponse() {
			
			pccToDiameterGxMapping.apply(valueProvider, accumalator);
			
			ReflectionAssert.assertLenientEquals(createExpectedAvp(), accumalator.getAll());
		}

		private List<IDiameterAVP> createExpectedAvp() {
			AvpGrouped umInfo = (AvpGrouped) attributeFactory.create(DiameterAVPConstants.TGPP_USAGE_MONITORING_INFORMATION);

			umInfo.addSubAvp(DiameterAVPConstants.TGPP_USAGE_MONITORING_KEY, "mk1");
			umInfo.addSubAvp(DiameterAVPConstants.TGPP_USAGE_MONITORING_LEVEL, UMLevel.PCC_RULE_LEVEL.val);
			AvpGrouped serviceUnit = (AvpGrouped) attributeFactory.create(DiameterAVPConstants.GRANTED_SERVICE_UNIT);
			serviceUnit.addSubAvp(DiameterAVPConstants.CC_TOTAL_OCTETS, 100);

			umInfo.addSubAvp(serviceUnit);

			return Arrays.asList(umInfo);
		}

		
	}
	
}
