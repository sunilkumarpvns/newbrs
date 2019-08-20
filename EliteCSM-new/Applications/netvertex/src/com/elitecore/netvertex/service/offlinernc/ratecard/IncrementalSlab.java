package com.elitecore.netvertex.service.offlinernc.ratecard;

import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.util.BigDecimalFormatter;
import com.elitecore.netvertex.service.offlinernc.util.RnCPreConditions;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class IncrementalSlab implements RateSlab {

	private static final String MODULE = "INCR-RATE-SLAB";
	private Double slabValue;
	private BigDecimal pulse;
	private BigDecimal rate;
	private OfflineRnCKeyConstants ratingKey;
	private Uom pulseUom;
	private BigDecimal ratePerPulse;
	private BigDecimalFormatter bigDecimalFormatter;
	
	public IncrementalSlab(Double slabValue, BigDecimal pulse, BigDecimal rate, OfflineRnCKeyConstants ratingKey, Uom pulseUom, BigDecimal ratePerPulse, BigDecimalFormatter bigDecimalFormatter) {
		this.slabValue = slabValue;
		this.pulse = pulse;
		this.rate = rate;
		this.ratingKey = ratingKey;
		this.pulseUom = pulseUom;
		this.ratePerPulse = ratePerPulse;
		this.bigDecimalFormatter = bigDecimalFormatter;
	}

	@Override
	
	public boolean apply(RatingRequest ratingRequest, RnCResponse response) throws OfflineRnCException {
		ratingRequest.getRncRequest().getTraceWriter().println();
		ratingRequest.getRncRequest().getTraceWriter().print("[ " + MODULE + " ]");
		ratingRequest.getRncRequest().getTraceWriter().println();
		ratingRequest.getRncRequest().getTraceWriter().println();
		ratingRequest.getRncRequest().getTraceWriter().incrementIndentation();
		
		int totalUsage = Integer.parseInt(RnCPreConditions.checkKeyNotNull(ratingRequest.getRncRequest(), ratingKey));
		int previousAccountedUsage = ratingRequest.getPreviousAccountedUsage();
		int unAccountedUsage = totalUsage - previousAccountedUsage;
		int usageToBeAccountedInThisSlab;

		if (slabValue.intValue() == -1) {
			usageToBeAccountedInThisSlab = unAccountedUsage;
		} else if (unAccountedUsage < slabValue.intValue()) {
			usageToBeAccountedInThisSlab = unAccountedUsage;
		} else {
			usageToBeAccountedInThisSlab = slabValue.intValue(); 
		}

		BigDecimal consumedPulses = calculateConsumedPulse(new BigDecimal(usageToBeAccountedInThisSlab), pulse);
		BigDecimal accountedCost = consumedPulses.multiply(ratePerPulse);

		previousAccountedUsage +=  usageToBeAccountedInThisSlab;
		ratingRequest.setPreviousAccountedUsage(previousAccountedUsage);
		if (totalUsage == ratingRequest.getPreviousAccountedUsage()) {
			ratingRequest.setRatingCompleted(true);
		}
		ratingRequest.setSlabOnePulse(consumedPulses);
		ratingRequest.setSlabOneCost(accountedCost);
		ratingRequest.setSlabOneRatePerPulse(ratePerPulse);
		ratingRequest.setChargePerUom(rate);
		
		ratingRequest.getRncRequest().getTraceWriter().println(); 
		ratingRequest.getRncRequest().getTraceWriter().print(" - Rate slab with slab value: " + slabValue.intValue() + " applied. Details :- ");
		ratingRequest.getRncRequest().getTraceWriter().println(); 
		ratingRequest.getRncRequest().getTraceWriter().print(" - usage accounted by this slab: " + bigDecimalFormatter.format(new BigDecimal(usageToBeAccountedInThisSlab)));
		ratingRequest.getRncRequest().getTraceWriter().println(); 
		ratingRequest.getRncRequest().getTraceWriter().print(" - total usage accounted till now: " + bigDecimalFormatter.format(new BigDecimal(previousAccountedUsage)));
		ratingRequest.getRncRequest().getTraceWriter().println(); 
		ratingRequest.getRncRequest().getTraceWriter().print(" - number of pulse: " + consumedPulses);
		ratingRequest.getRncRequest().getTraceWriter().println(); 
		ratingRequest.getRncRequest().getTraceWriter().print(" - charge per UOM: " + rate);
		ratingRequest.getRncRequest().getTraceWriter().println();
		ratingRequest.getRncRequest().getTraceWriter().print(" - rate per pulse: " + bigDecimalFormatter.format(ratePerPulse));
		ratingRequest.getRncRequest().getTraceWriter().println(); 
		ratingRequest.getRncRequest().getTraceWriter().print(" - accounted cost: " + bigDecimalFormatter.format(accountedCost));
		ratingRequest.getRncRequest().getTraceWriter().println(); 
		ratingRequest.getRncRequest().getTraceWriter().decrementIndentation();
		return true;
	}
	
	private BigDecimal calculateConsumedPulse(BigDecimal sessionTime, BigDecimal pulse) {
		if (ratingKey == OfflineRnCKeyConstants.SESSION_TIME) {
			return calculateConsumedPulseVoice(sessionTime, pulse); 
		} else {
			return calculateConsumedPulseData(sessionTime, pulse);
		}
	}

	private BigDecimal calculateConsumedPulseVoice(BigDecimal sessionTime, BigDecimal pulse) {
		if (pulseUom == Uom.SECOND) {
			return sessionTime.divide(pulse , RoundingMode.CEILING);
		} else if (pulseUom == Uom.MINUTE) {
			return sessionTime.divide(pulse.multiply(new BigDecimal(60)), RoundingMode.CEILING);
		} else {
			return sessionTime.divide(pulse.multiply(new BigDecimal(3600)), RoundingMode.CEILING);
		}
	}
	
	private BigDecimal calculateConsumedPulseData(BigDecimal sessionTotalDataTransfer, BigDecimal pulse) {
		if (pulseUom == Uom.GB) {
			return sessionTotalDataTransfer.divide(pulse.multiply(new BigDecimal(DataUnit.GB.toBytes(1))), RoundingMode.CEILING);
		} else if (pulseUom == Uom.MB) {
			return sessionTotalDataTransfer.divide(pulse.multiply(new BigDecimal(DataUnit.MB.toBytes(1))), RoundingMode.CEILING);
		} else if (pulseUom == Uom.KB) {
			return sessionTotalDataTransfer.divide(pulse.multiply(new BigDecimal(DataUnit.KB.toBytes(1))), RoundingMode.CEILING);
		} else {
			return sessionTotalDataTransfer.divide(pulse, RoundingMode.CEILING);
		}
	}
}
