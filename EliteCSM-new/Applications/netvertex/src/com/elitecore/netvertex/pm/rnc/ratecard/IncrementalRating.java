package com.elitecore.netvertex.pm.rnc.ratecard;

import com.elitecore.corenetvertex.constants.TierRateType;
import com.elitecore.corenetvertex.pm.rnc.ratecard.MonetaryRateCardVeresionDetail;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateSlab;

import java.sql.Timestamp;
import java.util.List;

public class IncrementalRating extends MonetaryRateCardVeresionDetail {

	public IncrementalRating(String id, String label1, String label2, Timestamp fromDate, List<RateSlab> slabs,
							 String rncPackageId, String rncPackageName, String rateCardGroupId, String rateCardGroupName,
							 String rateCardId, String rateCardName, String rateCardVersionId, String rateCardVersionName) {
		super(id, label1, label2,fromDate, TierRateType.FLAT,slabs,
				rncPackageId, rncPackageName, rateCardGroupId, rateCardGroupName, rateCardId, rateCardName,
				rateCardVersionId, rateCardVersionName);
	}
}