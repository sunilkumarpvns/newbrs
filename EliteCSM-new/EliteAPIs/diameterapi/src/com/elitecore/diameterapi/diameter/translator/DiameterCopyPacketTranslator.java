package com.elitecore.diameterapi.diameter.translator;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.manager.scripts.ScriptsExecutor;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.translator.data.CopyPacketTranslatorPolicyData;

public class DiameterCopyPacketTranslator extends BaseCopyPacketTranslator<DiameterPacket, IDiameterAVP, AvpGrouped> {

	private static final String MODULE = "DIA-CPY-PKT-TRNSLTR";

	public DiameterCopyPacketTranslator(CopyPacketTranslatorPolicyData policyData, 
			ScriptsExecutor executor, Class<?> scriptType){
		super(policyData, executor, scriptType);
	}
	
	@Override
	protected DiameterCopyPacketTranslatorPolicy createCopyPacketTranslationPolicy() {
		return new DiameterCopyPacketTranslatorPolicy();
	}

	@Override
	protected void copyRequest(TranslatorParams params) throws CloneNotSupportedException {
		DiameterPacket originalFromPacket = (DiameterPacket) params.getParam(TranslatorConstants.FROM_PACKET);
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Copying Diameter Request with HbH-ID=" + 
					originalFromPacket.getHop_by_hopIdentifier() + 
					", EtE-ID=" + originalFromPacket.getEnd_to_endIdentifier());
		}
		DiameterPacket toPacket = (DiameterPacket) originalFromPacket.clone(); 
		params.setParam(TranslatorConstants.TO_PACKET, toPacket);

	}
	
	@Override
	protected void copyResponse(TranslatorParams params) throws CloneNotSupportedException {
		DiameterPacket originalFromPacket = (DiameterPacket) params.getParam(TranslatorConstants.FROM_PACKET);
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Copying Diameter Answer with HbH-ID=" + 
					originalFromPacket.getHop_by_hopIdentifier() + 
					", EtE-ID=" + originalFromPacket.getEnd_to_endIdentifier());
		}
		DiameterPacket toPacket = (DiameterPacket) originalFromPacket.clone(); 
		params.setParam(TranslatorConstants.TO_PACKET, toPacket);

	}

	@Override
	protected void applyDummyResponse(TranslatorParams params) {
		// Nothing To Do handled in Router via Virtual Routing Peer
	}
}
