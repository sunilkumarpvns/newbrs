package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.TgppR9Builder;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.UMBuilder;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.AvpAccumalator;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterPacketMapping;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.ArrayList;
import java.util.List;

public class PCCToDiameterGxMapping implements PCRFToDiameterPacketMapping{

	private static final String MODULE = "PCC-TO-DIA-GX-MAPPING";
	private UMBuilder umBuilder;
	
	public PCCToDiameterGxMapping(UMBuilder umBuilder) {
		this.umBuilder = umBuilder;
	}
	
	public PCCToDiameterGxMapping() {
		this.umBuilder = new TgppR9Builder();
	}
	
	@Override
	public void apply(DiameterPacketMappingValueProvider valueProvider, AvpAccumalator accumalator) {

		PCRFResponse response = valueProvider.getPcrfResponse();

		handleUM(valueProvider, accumalator, response);

		
		handlePCCRuleRemove(accumalator, response);
		handleChargingRuleBaseNameInstall(accumalator, response);
		handleChargingRuleBaseNameRemove(accumalator, response);
	}

	private void handleUM(DiameterPacketMappingValueProvider valueProvider, AvpAccumalator accumalator, PCRFResponse pcrfResponse) {


		if(Collectionz.isNullOrEmpty(pcrfResponse.getUsageMonitoringInfoList())) {
			return;
		}

		DiameterGatewayConfiguration configuration = valueProvider.getDiameterGatewayConfiguration();
		if(configuration.getSupportedStandard() == SupportedStandard.CISCOSCE){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Unable to set Usage Information for PCRFResponse with CoreSessionId: "
						+ pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val)
						+ ". Reason: Supported Standard is CiscoSCE for Gateaw: " + configuration.getName());
			}

			return;
		}

		List<IDiameterAVP> usageMonitoringAVPs = umBuilder.buildUsageMonitoringAVP(pcrfResponse);

		if (Collectionz.isNullOrEmpty(usageMonitoringAVPs) == false) {
			accumalator.add(usageMonitoringAVPs);
		}
	}

	private void handlePCCRuleRemove(AvpAccumalator accumalator, PCRFResponse pcrfResponse) {

		if (Collectionz.isNullOrEmpty(pcrfResponse.getRemovablePCCRules())) {
			return;
		}

		ArrayList<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();

		for (String pccRuleName : pcrfResponse.getRemovablePCCRules()) {

			IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.TGPP_CHARGING_RULE_NAME);
			diameterAVP.setStringValue(pccRuleName);
			diameterAVPs.add(diameterAVP);
		}

		IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.TGPP_CHARGING_RULE_REMOVE);
		diameterAVP.setGroupedAvp(diameterAVPs);
		accumalator.add(diameterAVP);
	}

	private void handleChargingRuleBaseNameRemove(AvpAccumalator accumalator, PCRFResponse pcrfResponse) {

		if (Collectionz.isNullOrEmpty(pcrfResponse.getRemovableChargingRuleBaseNames())) {
			return;
		}

		ArrayList<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();

		for (String chargingRuleBaseName : pcrfResponse.getRemovableChargingRuleBaseNames()) {

			IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.TGPP_CHARGING_RULE_BASE_NAME);
			diameterAVP.setStringValue(chargingRuleBaseName);
			diameterAVPs.add(diameterAVP);
		}

		IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.TGPP_CHARGING_RULE_REMOVE);
		diameterAVP.setGroupedAvp(diameterAVPs);
		accumalator.add(diameterAVP);
	}

	private void handleChargingRuleBaseNameInstall(AvpAccumalator accumalator, PCRFResponse pcrfResponse) {

		if (Collectionz.isNullOrEmpty(pcrfResponse.getInstallableChargingRuleBaseNames())) {
			return;
		}

		ArrayList<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();

		for (ChargingRuleBaseName chargingRuleBaseName : pcrfResponse.getInstallableChargingRuleBaseNames()) {

			IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.TGPP_CHARGING_RULE_BASE_NAME);
			diameterAVP.setStringValue(chargingRuleBaseName.getName());
			diameterAVPs.add(diameterAVP);
		}

		IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.TGPP_CHARGING_RULE_INSTALL);
		diameterAVP.setGroupedAvp(diameterAVPs);
		accumalator.add(diameterAVP);
	}

}
