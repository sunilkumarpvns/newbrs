package com.elitecore.netvertex.service.offlinernc.ratecard;

import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;

public interface RatingBehavior {
	boolean apply(RatingRequest request, RnCResponse reponse) throws OfflineRnCException;

	boolean isApplicable(RnCRequest request, RnCResponse response, String keyOne, String keyTwo) throws OfflineRnCException;
	
	String getKey();
}
