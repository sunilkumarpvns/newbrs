package com.elitecore.netvertex.service.offlinernc.rncpackage;

import java.util.List;

import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorCodes;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorMessages;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.rcgroup.RateCardGroup;

public class RnCPackage {

	private String name;
	private List<RateCardGroup> rateCardGroups;
	
	public RnCPackage(String name, List<RateCardGroup> rateCardGroups) {
		this.name = name;
		this.rateCardGroups = rateCardGroups;
	}

	public String getName() {
		return name;
	}

	public void apply(RnCRequest request, RnCResponse response) throws OfflineRnCException {
		
		
		for (RateCardGroup rateCardGroup : rateCardGroups) {
			if (rateCardGroup.apply(request, response)) {
				response.setAttribute(OfflineRnCKeyConstants.RNC_PACKAGE, getName());
				return;
			}
		}
		
		throw new OfflineRnCException(OfflineRnCErrorCodes.RATE_CARD_GROUP_NOT_FOUND, OfflineRnCErrorMessages.RATE_CARD_GROUP_NOT_FOUND);
	}
}
