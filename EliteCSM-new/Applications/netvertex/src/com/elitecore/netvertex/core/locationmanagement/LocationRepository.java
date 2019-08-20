package com.elitecore.netvertex.core.locationmanagement;

import com.elitecore.netvertex.core.locationmanagement.data.LocationInformationConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.MccConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.NetworkConfiguration;

public interface LocationRepository {
	MccConfiguration getMCCConfigurationByMCCCode(String mcc);
	NetworkConfiguration getNetworkInformationByMCCMNC(String mccmnc);
	NetworkConfiguration getNetworkInformationById(String id);

	LocationInformationConfiguration getLocationInformationByCGI(String mcc, String mnc, String lac, String ci);

	LocationInformationConfiguration getLocationInformationBySAI(String mcc, String mnc, String lac, String sac);

	LocationInformationConfiguration getLocationInformationByRAI(String mcc, String mnc, String lac, String rac);

	LocationInformationConfiguration getLocationInformationByAreaName(String areaName);
}