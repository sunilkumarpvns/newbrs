package com.elitecore.test.diameter.factory;

import com.elitecore.test.dependecy.diameter.packet.avps.IDiameterAVP;

public interface AttributeProvider {
	
	IDiameterAVP getKnowAtrribute(String id);

}
