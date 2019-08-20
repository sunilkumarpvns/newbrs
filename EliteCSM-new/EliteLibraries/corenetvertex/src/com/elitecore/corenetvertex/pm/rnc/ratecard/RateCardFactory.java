package com.elitecore.corenetvertex.pm.rnc.ratecard;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardData;
import com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersionDetail;
import com.elitecore.corenetvertex.pd.ratecard.NonMonetaryRateCardData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardScope;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pm.rnc.RnCFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.elitecore.corenetvertex.constants.TierRateType.FLAT;
import static com.elitecore.corenetvertex.constants.TierRateType.INCREMENTAL;
import static com.elitecore.corenetvertex.constants.TierRateType.fromVal;

public class RateCardFactory {

	private RateCardVersionFactory rateCardVersionFactory;
	private RnCFactory rnCFactory;

	public RateCardFactory(RateCardVersionFactory rateCardVersionFactory, RnCFactory rnCFactory) {
		this.rateCardVersionFactory = rateCardVersionFactory;
		this.rnCFactory = rnCFactory;
	}

	private void createAndAddSlabs(List<RateSlab> rateSlabs, MonetaryRateCardVersionDetail rateCardVersionDetail, String pulseUom, String rateUom) {
		RateSlab rateSlabOne;

		if (rateCardVersionDetail.getSlab1() == null) {
			rateCardVersionDetail.setSlab1(CommonConstants.SLAB_UNLIMITED);
		}

		rateSlabOne = new RateSlab(rateCardVersionDetail.getSlab1()
				, rateCardVersionDetail.getPulse1()== null? CommonConstants.QUOTA_UNDEFINED:rateCardVersionDetail.getPulse1()
				, rateCardVersionDetail.getRate1()
				, Uom.valueOf(pulseUom)
				, Uom.valueOf(rateUom)
				, fromVal(rateCardVersionDetail.getRateType()), rateCardVersionDetail.getDiscount(), Objects.nonNull(rateCardVersionDetail.getRevenueDetail())?rateCardVersionDetail.getRevenueDetail().getName():null);
		rateSlabs.add(rateSlabOne);

	}

	public MonetaryRateCard createMonetaryRateCard(MonetaryRateCardData data, String rateCardGroupId, String rateCardGroupName, ChargingType chargingType, List<String> failReasons) {
		if (data == null) {
			failReasons.add("Rate card details are not configured for a rate card in rate card group: "+ rateCardGroupName );
			return null;
		}

		RateCardData rateCardData = data.getRateCardData();

		List<com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion> rateCardVersionRelation = data.getMonetaryRateCardVersions();

		List<MonetaryRateCardVersion> rateCardVersions = new ArrayList<>();

		Uom rateUnit = Uom.fromVaue(data.getRateUnit());
		Uom pulseUnit = Uom.fromVaue(data.getPulseUnit());

		String rncPackageDataName = null;

		if(rateUnit == null){
			failReasons.add("Rate Unit is not set for Monetary Rate Card: "+rateCardData.getName());
			return null;
		}

		if(ChargingType.SESSION.equals(chargingType) == true) {
			if (pulseUnit == null) {
				failReasons.add("Pulse Unit is not set for Monetary Rate Card: " + rateCardData.getName());
				return null;
			}

			if (pulseUnit.equals(Uom.SECOND) == false && pulseUnit.equals(Uom.MINUTE) == false && pulseUnit.equals(Uom.HOUR) == false) {
				failReasons.add("Invalid Pulse Unit: " + pulseUnit + " for Rate Card: " + rateCardData.getName());
				return null;
			}
		}else{
			pulseUnit = Uom.EVENT;
		}

		if(ChargingType.EVENT.equals(chargingType) == true && Uom.EVENT.name().equals(rateUnit.getUnitType()) == false ){
			failReasons.add("Charging Type and Rate Units are incompatible with each other for Monetary Rate Card: "+rateCardData.getName());
			return null;
		}

		if(rateUnit!= Uom.PERPULSE && pulseUnit.getUnitType().equals(rateUnit.getUnitType())== false ){
			failReasons.add("Pulse Unit and Rate Units are incompatible with each other for Monetary Rate Card: "+rateCardData.getName());
			return null;
		}

		if(rateCardVersionRelation.isEmpty()){
			failReasons.add("Rate card details not configured for rate card: "+rateCardData.getName());
			return null;
		}

		if(rateCardData.getScope().equalsIgnoreCase(RateCardScope.LOCAL.name())) {
			rncPackageDataName = rateCardData.getRncPackageData().getName();
		}

		for (com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion versionRelation: rateCardVersionRelation) {
			MonetaryRateCardVeresionDetail monetaryRateCardVeresionDetail;
			List<MonetaryRateCardVeresionDetail> monetaryRateCardVersionDetails = new ArrayList<>();

			List<MonetaryRateCardVersionDetail> rateCardVersionDetails = versionRelation.getMonetaryRateCardVersionDetail();

			if(rateCardVersionDetails.isEmpty()){
				failReasons.add("Rate card version details not configured for rate card: "+rateCardData.getName());
				continue;
			}

			MonetaryRateCardVersion monetaryRateCardVersion = rateCardVersionFactory.create(versionRelation.getId(),
					versionRelation.getName(), monetaryRateCardVersionDetails,rateCardData.getRncPkgId(),
					rncPackageDataName, rateCardGroupId, rateCardGroupName,
					rateCardData.getId(),rateCardData.getName());

			for (MonetaryRateCardVersionDetail rateCardVersionDetail : rateCardVersionDetails) {

				if(FLAT.equals(fromVal(rateCardVersionDetail.getRateType()))==false && INCREMENTAL.equals(fromVal(rateCardVersionDetail.getRateType()))==false){
					failReasons.add("Invalid Rate type: " + rateCardVersionDetail.getRateType() + " in RC: "+rateCardData.getName());
					continue;
				}

				List<RateSlab> rateSlabs = new ArrayList<>();
				createAndAddSlabs(rateSlabs,
						rateCardVersionDetail,
						data.getPulseUnit(),
						data.getRateUnit());

				String keyValueOne = rateCardVersionDetail.getLabel1();
				String keyValueTwo = rateCardVersionDetail.getLabel2();
				keyValueOne = (Strings.isNullOrBlank(keyValueOne) ? "" : keyValueOne);
				keyValueTwo = (Strings.isNullOrBlank(keyValueTwo) ? "" : keyValueTwo);

				monetaryRateCardVeresionDetail = new MonetaryRateCardVeresionDetail(rateCardVersionDetail.getId(),
						keyValueOne,  keyValueTwo,
						versionRelation.getEffectiveFromDate(),
						fromVal(rateCardVersionDetail.getRateType()),
						rateSlabs, monetaryRateCardVersion.getRncPackageId(),
						monetaryRateCardVersion.getRncPackageName(),
						monetaryRateCardVersion.getRateCardGroupId(),
						monetaryRateCardVersion.getRateCardGroupName(),
						monetaryRateCardVersion.getRateCardId(),
						monetaryRateCardVersion.getRateCardName(),
						monetaryRateCardVersion.getId(),
						monetaryRateCardVersion.getName());

				monetaryRateCardVersionDetails.add(monetaryRateCardVeresionDetail);
			}

			rateCardVersions.add(monetaryRateCardVersion);
		}

		return rnCFactory.createMonetaryRateCard(rateCardData.getId(),rateCardData.getName(), rateCardData.getDescription(),
				data.getLabelKey1(),
				data.getLabelKey2(),
				rateCardVersions,
				rateUnit,
				pulseUnit,
				rateCardData.getRncPkgId(), rncPackageDataName, rateCardGroupId, rateCardGroupName);
	}

	public NonMonetaryRateCard createNonMonetaryRateCard(NonMonetaryRateCardData data, String rateCardGroupId, String rateCardGroupName, ChargingType chargingType, List<String> failReasons) {
		if (data == null) {
			failReasons.add("Rate card details are not configured for a rate card in rate card group: "+ rateCardGroupName );
			return null;
		}

		RateCardData rateCardData = data.getRateCardData();

		Uom timeUnit = Uom.fromVaue(data.getTimeUom());
		Uom pulseUnit = Uom.fromVaue(data.getPulseUom());
		long time = data.getTime()!=null?data.getTime():CommonConstants.QUOTA_UNLIMITED;
		long event = data.getEvent()!=null?data.getEvent():CommonConstants.QUOTA_UNLIMITED;
		long pulse = data.getPulse()!=null?data.getPulse():CommonConstants.QUOTA_UNDEFINED;
		long timeMinorUnit = CommonConstants.QUOTA_UNDEFINED;
		long pulseMinorUnit = CommonConstants.QUOTA_UNDEFINED;

		RenewalIntervalUnit renewalIntervalUnit = RenewalIntervalUnit.fromRenewalIntervalUnit(data.getRenewalIntervalUnit());

		if(renewalIntervalUnit == null){
			failReasons.add("Invalid Renewal Interval Unit is configured with Non Monetary Rate Card: " + rateCardData.getName());
			return null;
		}


		if(ChargingType.SESSION.equals(chargingType) == true && pulseUnit == null) {
			failReasons.add("Pulse Unit is not set for Non Monetary Rate Card: " + rateCardData.getName());
			return null;
		}

		if(ChargingType.EVENT.equals(chargingType) == true && Uom.EVENT.name().equals(pulseUnit.getUnitType()) == false ){
			failReasons.add("Charging Type and Event Units are incompatible with each other for Non Monetary Rate Card: "+rateCardData.getName());
			return null;
		}

		if(Uom.EVENT.getUnitType().equals(pulseUnit.getUnitType())){
			time = CommonConstants.QUOTA_UNDEFINED;
			pulseUnit = Uom.EVENT;
			pulse = 1;
			pulseMinorUnit = 1;
		} else if(Uom.SECOND.getUnitType().equals(pulseUnit.getUnitType())){
			event = CommonConstants.QUOTA_UNDEFINED;
		}

		if( time > 0 ){
			if(timeUnit == null) {
				failReasons.add("Time Unit is not set for Non Monetary Rate Card: " + rateCardData.getName());
				return null;
			}

			if(pulse <= 0) {
				failReasons.add("Invalid pulse value is set for Non Monetary Rate Card: " + rateCardData.getName());
				return null;
			}

            if(timeUnit!= Uom.PERPULSE && pulseUnit.getUnitType().equals(timeUnit.getUnitType())== false ){
                failReasons.add("Pulse Unit and Rate Unit are incompatible with each other for Monetary Rate Card: "+rateCardData.getName());
                return null;
            }

            if(pulseUnit.equals(Uom.SECOND) == false && pulseUnit.equals(Uom.MINUTE) == false && pulseUnit.equals(Uom.HOUR) == false ){
                failReasons.add("Invalid time unit: "+timeUnit+" for Rate Card: "+rateCardData.getName());
                return null;
            }
			timeMinorUnit = timeUnit.getMinorUnits(time);
			pulseMinorUnit= pulseUnit.getMinorUnits(pulse);
		}

		return rnCFactory.createNonMonetaryRateCard(rateCardData.getId(),rateCardData.getName(), rateCardData.getDescription(),
                timeUnit,
                time,
				timeMinorUnit,
                event,
                pulseUnit,
                pulse,
                pulseMinorUnit,
                rateCardData.getRncPkgId(), rateCardData.getRncPackageData().getName(), rateCardGroupId, rateCardGroupName,data.getRenewalInterval() == null ? 0: data.getRenewalInterval(),renewalIntervalUnit,data.getProration());
	}
}
