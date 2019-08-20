package com.elitecore.corenetvertex.pm.rnc.rcgroup;

import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.acesstime.TimeSlot;
import com.elitecore.acesstime.exception.InvalidTimeSlotException;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.ratecardgroup.TimeSlotRelationData;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pm.rnc.RnCFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCard;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardFactory;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

public class RateCardGroupFactory {

	private  static final String MODULE = "RATE-CARE-GROUP-FACTORY";
	private RateCardFactory rateCardFactory;
	private RnCFactory rnCFactory;

	public RateCardGroupFactory(RateCardFactory rateCardFactory, RnCFactory rnCFactory) {
		this.rateCardFactory = rateCardFactory;
		this.rnCFactory = rnCFactory;
	}

	public RateCardGroup create(RateCardGroupData rateCardGroupData, String rnCPackageId, String rnCPackageName, ChargingType chargingType, List<String> failReasons) {
		LogicalExpression advancedCondition = null;

		try {
			advancedCondition = createAdvancedCondition(rateCardGroupData.getAdvanceCondition());

		} catch (Exception e){
			getLogger().error(MODULE, "Advance Condition parsing failed for Rate Card Group: "+rateCardGroupData.getName());
			getLogger().trace(e);

			failReasons.add("Advance Condition parsing failed for Rate Card Group: "+rateCardGroupData.getName());
		}

        RateCard peakRateCard=null;

 		List<String> rcFailReasons = new ArrayList<>();

		if (rateCardGroupData.getPeakRateRateCard() != null) {
			if(RateCardType.MONETARY.name().equals(rateCardGroupData.getPeakRateRateCard().getType())){
				peakRateCard = rateCardFactory.createMonetaryRateCard(rateCardGroupData.getPeakRateRateCard().getMonetaryRateCardData(), rateCardGroupData.getId(), rateCardGroupData.getName(), chargingType, rcFailReasons);
			} else if(RateCardType.NON_MONETARY.name().equals(rateCardGroupData.getPeakRateRateCard().getType())){
				peakRateCard = rateCardFactory.createNonMonetaryRateCard(rateCardGroupData.getPeakRateRateCard().getNonMonetaryRateCardData(), rateCardGroupData.getId(), rateCardGroupData.getName(), chargingType, rcFailReasons);
			} else {
				failReasons.add("Unknown rate card type "+rateCardGroupData.getPeakRateRateCard().getType()+" is set for rate card "+rateCardGroupData.getPeakRateRateCard().getName()+" in Rate Card Group: "+rateCardGroupData.getName());
			}
		} else {
			failReasons.add("No peak rate card is configured for Rate Card Group: "+rateCardGroupData.getName());
		}

		AccessTimePolicy accessTimePolicy = null;
		RateCard offPeakRateCard = null;
		if (rateCardGroupData.getOffPeakRateRateCard() != null) {
			if (RateCardType.MONETARY.name().equals(rateCardGroupData.getOffPeakRateRateCard().getType())) {
				offPeakRateCard = rateCardFactory.createMonetaryRateCard(rateCardGroupData.getOffPeakRateRateCard().getMonetaryRateCardData(), rateCardGroupData.getId(), rateCardGroupData.getName(), chargingType, rcFailReasons);
			} else if (RateCardType.NON_MONETARY.name().equals(rateCardGroupData.getOffPeakRateRateCard().getType())) {
				offPeakRateCard = rateCardFactory.createNonMonetaryRateCard(rateCardGroupData.getOffPeakRateRateCard().getNonMonetaryRateCardData(), rateCardGroupData.getId(), rateCardGroupData.getName(), chargingType, rcFailReasons);
			} else {
				failReasons.add("Unknown rate card type " + rateCardGroupData.getOffPeakRateRateCard().getType() + " is set for rate card " + rateCardGroupData.getOffPeakRateRateCard().getName() + " in Rate Card Group: " + rateCardGroupData.getName());
			}

			if(Collectionz.isNullOrEmpty(rateCardGroupData.getTimeSlotRelationData())){
				failReasons.add("At-least one time slot is required to be configured with Off-Peak rate card associated with Rate Card Group: " + rateCardGroupData.getName());
			}else {
				List<TimeSlot> timeSlots = createTimeSlots(rateCardGroupData.getTimeSlotRelationData(),failReasons);
				if (Collectionz.isNullOrEmpty(timeSlots) == false) {
					accessTimePolicy = new AccessTimePolicy();
					for (TimeSlot timeSlot : timeSlots) {
						accessTimePolicy.addTimeSlot(timeSlot);
					}
				}
			}
		}

		if(rcFailReasons.isEmpty() == false){
			failReasons.add("Rate card parsing failed. Reason: "+rcFailReasons);
		}
		
		return rnCFactory.createRateCardGroup(rateCardGroupData.getId(),rateCardGroupData.getName(), rateCardGroupData.getDescription(), advancedCondition, peakRateCard,offPeakRateCard,rnCPackageId, rnCPackageName, rateCardGroupData.getOrderNo()==null?1:rateCardGroupData.getOrderNo(),accessTimePolicy,rateCardGroupData.getTimeSlotRelationData());

	}

	private LogicalExpression createAdvancedCondition(String advanceCondition) throws InvalidExpressionException {
		if (Strings.isNullOrBlank(advanceCondition) == false) {
			return parseExpression(advanceCondition);
		} else {
			return null;
		}
	}

	private LogicalExpression parseExpression(String advanceCondition) throws InvalidExpressionException {
			return (LogicalExpression) Compiler.getDefaultCompiler().parseExpression(advanceCondition);
	}

	private static List<TimeSlot> createTimeSlots(List<TimeSlotRelationData> timePeriods,List<String> failReasons) {
		List<TimeSlot> timeSlots = new ArrayList<>();
		for (TimeSlotRelationData timePeriod : timePeriods) {
			try {
				timeSlots.add(TimeSlot.getTimeSlot(null, null, timePeriod.getDayOfWeek(), timePeriod.getTimePeriod()));
			} catch (InvalidTimeSlotException e) {
				failReasons.add("Invalid TimeSlot Configuration. Reason: " + e.getMessage());
				ignoreTrace(e);
			}

		}
		return timeSlots;
	}
}
