package com.elitecore.aaa.radius.service;

import com.elitecore.core.servicex.IPacketHash;
import com.elitecore.coreradius.commons.packet.RadiusPacketHash;

public class RadServiceRequestHash extends RadiusPacketHash implements IPacketHash{

	public RadServiceRequestHash(int clientPort, int identifier,
			byte[] applicationData) {
		super(clientPort, identifier, applicationData);
	}
}
