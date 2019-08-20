package com.elitecore.netvertex.gateway.diameter.mapping;

import com.elitecore.corenetvertex.constants.ConversionType;
import com.elitecore.corenetvertex.constants.PacketType;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayProfileData;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.CCADefaultMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMapping.DiameterToPCCMappingBuilder;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterMapping.PCCToDiameterMappingBuilder;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.ChargingRuleDefinitionPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.RARDefaultMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.UMBuilder;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterMappingFactory;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterPacketMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Jay Trivedi
 *
 */
public enum ApplicationPacketType {


	GX_CCR("GX", PacketType.CREDIT_CONTROL_REQUEST, ConversionType.GATEWAY_TO_PCC) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			return new DiameterToPCCMappingBuilder().withGxCCRMapping(umBuilder)
					.withConfiguredMapping(mappings)
					.build();
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("PCC to Diameter mapping is not supported for " + this.name());
		}
	},

	GX_CCA("GX", PacketType.CREDIT_CONTROL_RESPONSE, ConversionType.PCC_TO_GATEWAY) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("Diameter to PCC mapping is not supported for " + this.name());
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings,
															   PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory,
															   DiameterGatewayProfileData gatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			return new PCCToDiameterMappingBuilder()
					.withGxCCAMapping(umBuilder).withConfiguredMapping(mappings)
					.withDefaultMappings(CCADefaultMapping.create(pcrfToDiameterMappingFactory, gatewayProfileData, chargingRuleDefinitionPacketMapping)).build();
		}
	},

	GX_RAR("GX", PacketType.RE_AUTH_REQUEST, ConversionType.PCC_TO_GATEWAY) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("Diameter to PCC mapping is not supported for " + this.name());
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings,
															   PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory,
															   DiameterGatewayProfileData diameterGatewayProfileData,
															   ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			return new PCCToDiameterMapping.PCCToDiameterMappingBuilder()
					.withGxRARMapping(umBuilder).withConfiguredMapping(mappings)
					.withDefaultMappings(RARDefaultMapping.create(pcrfToDiameterMappingFactory, diameterGatewayProfileData, chargingRuleDefinitionPacketMapping)).build();
		}
	},

	GX_RAA("GX", PacketType.RE_AUTH_RESPONSE, ConversionType.GATEWAY_TO_PCC) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			return new DiameterToPCCMappingBuilder().withGxRAAMapping()
					.withConfiguredMapping(mappings).build();
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("PCC to Diameter mapping is not supported for " + this.name());
		}
	},

	GY_CCR("GY", PacketType.CREDIT_CONTROL_REQUEST, ConversionType.GATEWAY_TO_PCC) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			return new DiameterToPCCMappingBuilder().withGyCCRMapping()
					.withConfiguredMapping(mappings).build();
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("PCC to Diameter mapping is not supported for " + this.name());
		}
	},

	GY_CCA("GY", PacketType.CREDIT_CONTROL_RESPONSE, ConversionType.PCC_TO_GATEWAY) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("PCC to Diameter mapping is not supported for " + this.name());
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			return new PCCToDiameterMapping.PCCToDiameterMappingBuilder()
					.withGyCCAMapping().withConfiguredMapping(mappings).build();
		}
	},

	GY_RAR("GY", PacketType.RE_AUTH_REQUEST, ConversionType.PCC_TO_GATEWAY) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("Diameter to PCC mapping is not supported for " + this.name());
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			return new PCCToDiameterMapping.PCCToDiameterMappingBuilder()
					.withGyRARMapping().withConfiguredMapping(mappings).build();
		}
	},

	GY_RAA("GY", PacketType.RE_AUTH_RESPONSE, ConversionType.GATEWAY_TO_PCC) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			return new DiameterToPCCMappingBuilder().withGyRAAMapping()
					.withConfiguredMapping(mappings).build();
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("PCC to Diameter mapping is not supported for " + this.name());
		}
	},

	ASR("RX", PacketType.ABORT_SESSION_REQUEST, ConversionType.PCC_TO_GATEWAY) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("Diameter to PCC mapping is not supported for " + this.name());
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			return new PCCToDiameterMappingBuilder()
					.withASRMapping().withConfiguredMapping(mappings).build();
		}
	},

	ASA("RX", PacketType.ABORT_SESSION_RESPONSE, ConversionType.GATEWAY_TO_PCC) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			return new DiameterToPCCMappingBuilder().withASAMapping()
					.withConfiguredMapping(mappings).build();
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("PCC to Diameter mapping is not supported for " + this.name());
		}
	},

	SLR("SY", PacketType.SPENDING_LIMIT_REQUEST, ConversionType.PCC_TO_GATEWAY) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("Diameter to PCC mapping is not supported for " + this.name());
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			return new PCCToDiameterMapping.PCCToDiameterMappingBuilder()
					.withSLRMapping().withConfiguredMapping(mappings).build();
		}
	},

	SLA("SY", PacketType.SPENDING_LIMIT_RESPONSE, ConversionType.GATEWAY_TO_PCC) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			return new DiameterToPCCMappingBuilder().withSLAMapping()
					.withConfiguredMapping(mappings).build();
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("PCC to Diameter mapping is not supported for " + this.name());
		}
	},

	SNR("SY", PacketType.SPENDING_STATUS_NOTIFICATION_REQUEST, ConversionType.GATEWAY_TO_PCC) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			return new DiameterToPCCMappingBuilder().withSNRMapping()
					.withConfiguredMapping(mappings).build();
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("PCC to Diameter mapping is not supported for " + this.name());
		}
	},

	SNA("SY", PacketType.SPENDING_STATUS_NOTIFICATION_RESPONSE, ConversionType.PCC_TO_GATEWAY) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("Diameter to PCC mapping is not supported for " + this.name());
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			return new PCCToDiameterMapping.PCCToDiameterMappingBuilder()
					.withSNAMapping().withConfiguredMapping(mappings).build();
		}
	},

	AAR("RX", PacketType.AUTHENTICATE_AUTHORIZE_REQUEST, ConversionType.GATEWAY_TO_PCC) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			return new DiameterToPCCMappingBuilder().withAARMapping()
					.withConfiguredMapping(mappings).build();
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("PCC to Diameter mapping is not supported for " + this.name());
		}
	},

	AAA("RX", PacketType.AUTHENTICATE_AUTHORIZE_RESPONSE, ConversionType.PCC_TO_GATEWAY) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("Diameter to PCC mapping is not supported for " + this.name());
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			return new PCCToDiameterMapping.PCCToDiameterMappingBuilder()
					.withAAAMapping().withConfiguredMapping(mappings).build();
		}
	},

	RX_STR("GX", PacketType.SESSION_TERMINATION_REQUEST, ConversionType.GATEWAY_TO_PCC) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			return new DiameterToPCCMappingBuilder().withRxSTRMapping()
					.withConfiguredMapping(mappings).build();
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("PCC to Diameter mapping is not supported for " + this.name());
		}
	},

	STA("GX", PacketType.SESSION_TERMINATION_RESPONSE, ConversionType.PCC_TO_GATEWAY) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("Diameter to PCC mapping is not supported for " + this.name());
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			return new PCCToDiameterMapping.PCCToDiameterMappingBuilder()
					.withRxSTAMapping().withConfiguredMapping(mappings).build();
		}
	},

	SY_STR("GX", PacketType.SESSION_TERMINATION_REQUEST, ConversionType.PCC_TO_GATEWAY) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("Diameter to PCC mapping is not supported for " + this.name());
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			return new PCCToDiameterMapping.PCCToDiameterMappingBuilder()
					.withSySTRMapping().withConfiguredMapping(mappings).build();
		}
	},

	SY_STA("SY", PacketType.SESSION_TERMINATION_RESPONSE, ConversionType.GATEWAY_TO_PCC) {
		@Override
		public DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
															   DiameterGatewayProfileData diameterGatewayProfileData, UMBuilder umBuilder) {
			return new DiameterToPCCMappingBuilder().withSySTAMappings()
					.withConfiguredMapping(mappings).build();
		}

		@Override
		public PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings, PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {
			throw new UnsupportedOperationException("PCC to Diameter mapping is not supported for " + this.name());
		}
	};

	private final PacketType packetType;
	private ConversionType conversionType;
	private final String applicationInterface;
	private static Map<String, ApplicationPacketType> applicationPacketTypeByName;

	static {

		applicationPacketTypeByName = new HashMap<>();
		for (ApplicationPacketType applicationPacketType : ApplicationPacketType.values()) {
			applicationPacketTypeByName.put(applicationPacketType.applicationInterface + "_" + applicationPacketType.packetType.getPacketType()
					, applicationPacketType);

		}


	}

	ApplicationPacketType(String applicationInterface, PacketType packetType, ConversionType conversionType) {
		this.applicationInterface = applicationInterface;
		this.packetType = packetType;

		this.conversionType = conversionType;
	}


	public static ApplicationPacketType fromString(String applicationType, String packetType) {
		return applicationPacketTypeByName.get(applicationType + "_" + packetType);
	}



	private boolean isPccToGateway() {
		return this.conversionType == ConversionType.PCC_TO_GATEWAY;
	}

	private boolean isGatewayToPcc() {
		return this.conversionType == ConversionType.GATEWAY_TO_PCC;
	}

	public static List<ApplicationPacketType> getGatwayToPccAppPacketType() {
		return Arrays.stream(ApplicationPacketType.values()).
				filter(ApplicationPacketType::isGatewayToPcc).
				collect(Collectors.toList());
	}

	public static List<ApplicationPacketType> getPccToGatetypeAppPacketType() {
		return Arrays.stream(ApplicationPacketType.values()).
				filter(ApplicationPacketType::isPccToGateway).
				collect(Collectors.toList());
	}

	public ConversionType getConversionType() {
		return conversionType;
	}

	public abstract DiameterToPCCMapping createDiameterToPCCMapping(LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings,
																	DiameterGatewayProfileData diameterGatewayProfileData,
																	UMBuilder umBuilder);

	public abstract PCCToDiameterMapping createPCCToDiameterMapping(LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings,
																	PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory,
																	DiameterGatewayProfileData diameterGatewayProfileData, ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder);

	public static boolean contains(String type) {
		return applicationPacketTypeByName.containsKey(type);
	}

	public static Set<String> getDiaApplicationPacketTypes() {
		return applicationPacketTypeByName.keySet();
	}
}
