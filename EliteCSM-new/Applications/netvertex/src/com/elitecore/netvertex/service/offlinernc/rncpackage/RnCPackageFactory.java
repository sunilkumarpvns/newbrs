package com.elitecore.netvertex.service.offlinernc.rncpackage;
import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.pd.pbss.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.pbss.rncpackage.RncPackageData;
import com.elitecore.netvertex.service.offlinernc.rcgroup.RateCardGroup;
import com.elitecore.netvertex.service.offlinernc.rcgroup.RateCardGroupFactory;

public class RnCPackageFactory {

	private RateCardGroupFactory rateCardGroupFactory;
	
	public RnCPackageFactory(RateCardGroupFactory rateCardGroupFactory) {
		this.rateCardGroupFactory = rateCardGroupFactory;
	}

	public RnCPackage create(RncPackageData rncPackageData, String currency) throws InitializationFailedException {
		
		List<RateCardGroup> rateCardGroups = new ArrayList<>();
		
		rncPackageData.getRateCardGroupData().sort(comparing(RateCardGroupData::getOrderNo));
		
		for (RateCardGroupData rateCardGroupData : rncPackageData.getRateCardGroupData()) {
			rateCardGroups.add(rateCardGroupFactory.create(rateCardGroupData, currency, rateCardGroupData.getAccountEffect()));
		}
		
		return new RnCPackage(rncPackageData.getName(), rateCardGroups);
	}

}
