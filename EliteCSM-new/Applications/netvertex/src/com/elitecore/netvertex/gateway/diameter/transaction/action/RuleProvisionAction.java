package com.elitecore.netvertex.gateway.diameter.transaction.action;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.conf.impl.DiameterGatewayConfigurationImpl;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

public class RuleProvisionAction extends ActionHandler {
	private static final String NAME = "RULE-INSTALL";
	private static final String MODULE = "RLE-INSTL";

	public RuleProvisionAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public TransactionState handle() {
		List<PCCRule> pccRulesForCCA = new ArrayList<PCCRule>();
		List<PCCRule> pccRulesForRAR = new ArrayList<PCCRule>();
		// Send ACK of CCR
		DiameterRequest diameterRequest = getDiameterRequest();
		DiameterAnswer ccaPacket = new DiameterAnswer(diameterRequest);
		PCRFResponse pcrfResponse = getPCRFResponse();
		PCRFRequest pcrfRequest = getPCRFRequest();
		
		int ruleInstallMode = DiameterGatewayConfigurationImpl.ALL_ON_NETWORK_ENTRY;
		
		DiameterGatewayConfiguration configuration = getTransactionContext().getControllerContext().getGatewayConfigurationByName(pcrfResponse.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val));
		if(configuration == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Considering Rule Install Mode as All On Network Entry.Reason: gateway configuration not found");
			}
		}
		
		boolean isRAREligible = false;
		
		if(ruleInstallMode == DiameterGatewayConfigurationImpl.ALL_ON_NETWORK_ENTRY){
			if(pcrfResponse.getInstallablePCCRules() != null) {
				pccRulesForCCA.addAll(pcrfResponse.getInstallablePCCRules());
			}
			isRAREligible = false;
		}else if(ruleInstallMode == DiameterGatewayConfigurationImpl.FIRST_ON_NETWORK_ENTRY){
			pccRulesForRAR = pcrfResponse.getInstallablePCCRules();
			
			if(pccRulesForRAR == null) { // No PCC Rule Available
				isRAREligible = false;
			} else if (pccRulesForRAR.size() == 1) { // Only One PCC Rule Available
				isRAREligible = false;
				pccRulesForCCA.add(pccRulesForRAR.remove(0));
			} else { // More than one PCC Rule available
				pccRulesForCCA.add(pccRulesForRAR.get(0));
				pccRulesForRAR.remove(0);
				isRAREligible = true;
			}
		}else{ // DiameterGatewayConfiguration.NONE_ON_NETWORK_ENTRY
			pccRulesForRAR = pcrfResponse.getInstallablePCCRules();
			if(Collectionz.isNullOrEmpty(pccRulesForRAR)){
				isRAREligible = false;
			}else{
				isRAREligible = true;
			}
			pcrfResponse.setInstallablePCCRules(new ArrayList<PCCRule>());
		}		
		
		TransactionState state = TransactionState.COMPLETE;
		String ccaResultcode = pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.val);
		
		DiameterRequest rarRequest = null;
		if (isRAREligible) {
			// Need to send RAR For installing Rules
			pcrfResponse.setInstallablePCCRules(pccRulesForRAR);
			rarRequest = getTransactionContext().getControllerContext().buildRAR(pcrfResponse);
			if (rarRequest == null) {
				isRAREligible = false;
				ccaResultcode = PCRFKeyValueConstants.RESULT_CODE_INTERNAL_ERROR.val;
				getLogger().error(MODULE, "Skipping RAR sending. Reason: RAR generation failed");
			}
		}
		
		pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.val, ccaResultcode);
		pcrfResponse.setInstallablePCCRules(pccRulesForCCA);
		getTransactionContext().getControllerContext().buildCCA(pcrfResponse, ccaPacket);
		// Sending CCA 
		boolean isCCASent = getTransactionContext().sendAnswer(ccaPacket, diameterRequest);

		if(isRAREligible){
			if (getTransactionContext().sendRequest(rarRequest, pcrfResponse.getAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.val), true)) {
				state = TransactionState.WAIT_RULE_INSTALL_ACK;
			}
		}
		
		if (TransactionState.COMPLETE == state) {
			if (PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equals(ccaResultcode) && isCCASent) {
				handleSession(pcrfRequest, pcrfResponse);
			}
		}
		
		return state;
	}
	
}
