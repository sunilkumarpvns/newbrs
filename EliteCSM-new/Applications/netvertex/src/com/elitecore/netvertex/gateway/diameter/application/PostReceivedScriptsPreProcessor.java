package com.elitecore.netvertex.gateway.diameter.application;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.scripts.DiameterGroovyScript;

public class PostReceivedScriptsPreProcessor implements RequestPreprocessor {

	private final static String MODULE = "APPLY-SCRIPT-RCVD-PCKT-PRE-PROC";

	private DiameterGatewayControllerContext context;

	public PostReceivedScriptsPreProcessor(DiameterGatewayControllerContext context) {
		this.context = context;
	}

	@Override
	public void process(@Nonnull DiameterRequest request, @Nonnull DiameterGatewayConfiguration gatewayConf) {

		String gatewayName = gatewayConf.getName();

		List<DiameterGroovyScript> scripts = context.getDiameterGroovyScripts(gatewayName);

		if (Collectionz.isNullOrEmpty(scripts)) {
			return;
		}

		for (DiameterGroovyScript script : scripts) {
			try {
				script.postReceived(request);
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error in executing script: " + script.getName() + " for Diameter-Packet with Session-ID= "
						+ request.getAVPValue(DiameterAVPConstants.SESSION_ID) + " for gateway = " + gatewayName + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}

		}
	}
}
