package com.elitecore.test.diameter;

import com.elitecore.test.dependecy.diameter.packet.avps.IDiameterAVP;

public interface DiameterAttributeProvider {
	
	IDiameterAVP getKnowAtrribute(String id);

}
