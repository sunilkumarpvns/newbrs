package com.elitecore.aaa.diameter.translators;

import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;

public class DiameterPacketValueProvider implements ValueProvider {

	private DiameterPacket diameterPacket;

	public DiameterPacketValueProvider(DiameterPacket request) {
		this.diameterPacket = request;
	}

	@Override
	public String getStringValue(String identifier) {

		if (identifier == null) {
			return null;
		} else {
			return diameterPacket.getAVPValue(identifier, true);

		}
	}

}
