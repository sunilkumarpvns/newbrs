package com.elitecore.netvertex.service.offlinernc.ratecard;

import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;

public interface RateSlab {
	boolean apply(RatingRequest ratingRequest, RnCResponse response) throws OfflineRnCException;
}
