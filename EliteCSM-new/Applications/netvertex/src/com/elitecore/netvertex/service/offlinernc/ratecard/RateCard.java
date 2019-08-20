package com.elitecore.netvertex.service.offlinernc.ratecard;

import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;

public interface RateCard {
	String getName();
	boolean apply(RnCRequest request, RnCResponse response) throws OfflineRnCException;
}