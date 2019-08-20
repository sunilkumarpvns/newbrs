package com.elitecore.aaa.diameter.conf.impl;

import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;



@ReadOrder(order = { "diameterServiceConfigurable", "diameterWebServiceConfigurable",
		 "nasAppConfigurable", "nasPolicyConfigurable",
		 "eapAppConfigurable", "eapPolicyConfigurable",
		 "ccAppConfigurable", "ccPolicyConfigurable",	 
		 "diameterUserFileAuthDriverConfigurable", "diameterDbDriverConfigurable",
		 "diameterLDAPAuthDriverConfigurable","diameterHttpAuthDriverConfigurable",
		 "diameterMAPGWAuthDriverConfigurable","diameterClassicCSVAcctDriverConfigurable",
		 "diameterDbAcctDriverConfigurable","diameterDetailLocalAcctConfigurable",
		 "diameterCrestelDriverConfigurable","diameterCrestelDriverOCSv2DriverConfigurable",
		 "diameterPeerConfiguration","diameterRoutingTableConfigurable"})


public class DiameterServiceConfigurationImpl extends CompositeConfigurable {

	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
