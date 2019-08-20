package com.elitecore.corenetvertex.pm.rnc.pkg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pm.rnc.RnCFactory;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationScheme;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationSchemeFactory;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroup;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroupFactory;


import static java.util.Comparator.comparing;

public class RnCPackageFactory {

	private RateCardGroupFactory rateCardGroupFactory;
	private RnCFactory rnCFactory;
	private ThresholdNotificationSchemeFactory thresholdNotificationSchemeFactory;
	
	public RnCPackageFactory(RateCardGroupFactory rateCardGroupFactory, RnCFactory rnCFactory, ThresholdNotificationSchemeFactory thresholdNotificationSchemeFactory) {
		this.rateCardGroupFactory = rateCardGroupFactory;
		this.rnCFactory = rnCFactory;
		this.thresholdNotificationSchemeFactory = thresholdNotificationSchemeFactory;
	}

	public RnCPackage create(RncPackageData rncPackageData){
		
		List<RateCardGroup> rateCardGroups = new ArrayList<>();
		List<String> groupList = new ArrayList<>();
		
		rncPackageData.getRateCardGroupData().sort(comparing(RateCardGroupData::getOrderNo));

		List<String> failReasons = new ArrayList<>();
		List<String> partialFailReasons = new ArrayList<>();

		List<String> rcgFailReasons = new ArrayList<>();
		List<String> rcgFPartialFailReasons = new ArrayList<>();

		PolicyStatus policyStatus = PolicyStatus.SUCCESS;

		if(rncPackageData.getChargingType() == null){
			failReasons.add("Charging Type is not configured");
		}

		ChargingType chargingType = ChargingType.fromName(rncPackageData.getChargingType());

		if (chargingType == null) {
			failReasons.add("Invalid charging type: " + rncPackageData.getChargingType() + " configured. Valid values: [SESSION, EVENT]");
		}

		if(rncPackageData.getRateCardGroupData().isEmpty()){
			failReasons.add("Rate Card Group is not configured");
		}

		ThresholdNotificationScheme thresholdNotificationScheme = thresholdNotificationSchemeFactory.createThresholdNotificationScheme(rncPackageData.getRncNotifications(), chargingType,
				partialFailReasons);

		for (RateCardGroupData rateCardGroupData : rncPackageData.getRateCardGroupData()) {
            RateCardGroup rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, rncPackageData.getId(), rncPackageData.getName(), chargingType, rcgFailReasons);

            if(null!=rateCardGroup){
                rateCardGroups.add(rateCardGroup);
            }
		}
		if(rcgFailReasons.isEmpty()==false) {
			failReasons.add("Rate Card Group parsing failed. Reason: "+rcgFailReasons.toString());
		}

		if(rcgFPartialFailReasons.isEmpty()==false){
			partialFailReasons.add("Rate Card Group parsing partially failed. Reason: "+rcgFPartialFailReasons.toString());
		}


		if(failReasons.isEmpty()==false){
			policyStatus = PolicyStatus.FAILURE;
		} else if(partialFailReasons.isEmpty()==false){
			policyStatus = PolicyStatus.PARTIAL_SUCCESS;
		}

		String failReason = null;
		if(failReasons.isEmpty()==false){
			failReason = "RnC package parsing failed. Reason: "+failReasons.toString();
		}

		String partialFailReason = null;
		if(partialFailReasons.isEmpty()==false){
			partialFailReason = "RnC package parsing partially failed. Reason: "+partialFailReasons.toString();
		}

		if(Strings.isNullOrBlank(rncPackageData.getGroups())==false){
		   groupList = CommonConstants.COMMA_SPLITTER.split(rncPackageData.getGroups());
		}


		rateCardGroups.sort(Comparator.comparingInt(RateCardGroup::getOrder));
		return rnCFactory.createRncPackage(rncPackageData.getId(),
				rncPackageData.getName(),
				rncPackageData.getDescription(),
				groupList, rateCardGroups, thresholdNotificationScheme,
				rncPackageData.getTag(),
				RnCPkgType.fromName(rncPackageData.getType()),
				PkgMode.getMode(rncPackageData.getMode()),
				PkgStatus.fromVal(rncPackageData.getStatus()),
				policyStatus,
				failReason,
				partialFailReason,
				chargingType,rncPackageData.getCurrency());
	}
}
