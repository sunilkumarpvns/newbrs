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

public class FlatSlab implements RateSlab {

	private static final String MODULE = "FLAT-SLAB";
	private Double slabValue;
	private BigDecimal pulse;
	private BigDecimal rate;
	private OfflineRnCKeyConstants ratingKey;
	private Uom pulseUom;
	private BigDecimal ratePerPulse;
	private BigDecimalFormatter bigDecimalFormatter;
	
	public FlatSlab(Double slabValue, BigDecimal pulse, BigDecimal rate, OfflineRnCKeyConstants ratingKey, Uom pulseUom, BigDecimal ratePerPulse, BigDecimalFormatter bigDecimalFormatter) {
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
		ratingRequest.getRncRequest().getTraceWriter().println();
		ratingRequest.getRncRequest().getTraceWriter().print("[ " + MODULE + " ]");
		ratingRequest.getRncRequest().getTraceWriter().println();
		ratingRequest.getRncRequest().getTraceWriter().println();
		ratingRequest.getRncRequest().getTraceWriter().incrementIndentation();
		
		String usage = RnCPreConditions.checkKeyNotNull(ratingRequest.getRncRequest(), ratingKey);
		if (Integer.parseInt(ratingRequest.getRncRequest().getAttribute(ratingKey)) <= slabValue.intValue() ||
				slabValue.intValue() == -1) {
			BigDecimal consumedPulses = calculateConsumedPulse(new BigDecimal(usage), pulse);
			BigDecimal accountedCost = consumedPulses.multiply(ratePerPulse);
			
			ratingRequest.setSlabOnePulse(consumedPulses);
			ratingRequest.setSlabOneCost(accountedCost);
			ratingRequest.setSlabOneRatePerPulse(ratePerPulse);
			ratingRequest.setChargePerUom(rate);
			ratingRequest.setRatingCompleted(true);
			
			ratingRequest.getRncRequest().getTraceWriter().print("- Rate slab with slab value: " + slabValue.intValue() + " applied. Details :- ");
			ratingRequest.getRncRequest().getTraceWriter().println();
			ratingRequest.getRncRequest().getTraceWriter().print("- usage accounted by this slab : " + usage);
			ratingRequest.getRncRequest().getTraceWriter().println();
			ratingRequest.getRncRequest().getTraceWriter().print("- total usage accounted till now : " + usage);
			ratingRequest.getRncRequest().getTraceWriter().println();
			ratingRequest.getRncRequest().getTraceWriter().print("- number of pulse : " + consumedPulses);
			ratingRequest.getRncRequest().getTraceWriter().println(); 
			ratingRequest.getRncRequest().getTraceWriter().print("- charge per UOM: " + rate);
			ratingRequest.getRncRequest().getTraceWriter().println();
			ratingRequest.getRncRequest().getTraceWriter().print("- rate per pulse : " + bigDecimalFormatter.format(ratePerPulse));
			ratingRequest.getRncRequest().getTraceWriter().println();
			ratingRequest.getRncRequest().getTraceWriter().print("- accounted cost : " + bigDecimalFormatter.format(accountedCost));
			ratingRequest.getRncRequest().getTraceWriter().println();
			ratingRequest.getRncRequest().getTraceWriter().decrementIndentation();
			return true;
		} else {
			ratingRequest.getRncRequest().getTraceWriter().print("Skipping slab with slab value " + slabValue
					+ ", Reason: value of " + ratingKey.getName() 
					+ " exceeds configured slab value");
			ratingRequest.getRncRequest().getTraceWriter().decrementIndentation();
			return false;
		}
	}
	//FIXME-vicky- sessionTime, pulse and total pulse consumed can be made integer
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
