package com.elitecore.corenetvertex.pm.rnc.ratecard;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.TierRateType;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.io.Serializable;
import java.math.BigDecimal;

public class RateSlab implements Serializable,ToStringable {

	private static final long serialVersionUID = 1L;

	private long slabValue;
	private long pulse;
	private BigDecimal rate;
	private Integer discount;
	private String revenueDetail;
	private Uom pulseUnit;
	private Uom rateUnit;
	private TierRateType ratingType;
	private long rateMinorUnit;
	private long pulseMinorUnit;

	public RateSlab(long slabValue, long pulse, BigDecimal rate, Uom pulseUom, Uom rateUom, TierRateType ratingType, Integer discount, String revenueDetail) {
		this.slabValue = slabValue;
		this.pulse = pulse;
		this.rate = rate;
		this.pulseUnit = pulseUom;
		this.rateUnit = rateUom;
		this.ratingType = ratingType;
		this.discount = discount;
		this.revenueDetail = revenueDetail;

		switch(pulseUom) {		
			case SECOND : 
				pulseMinorUnit = pulse;
				break;
			case MINUTE :
				pulseMinorUnit = TimeUnit.MINUTE.toSeconds(pulse);
				break;
			case HOUR :
				pulseMinorUnit = TimeUnit.HOUR.toSeconds(pulse);
				break;
			default :
				pulseMinorUnit = pulse;			 
		}
		
		switch (rateUom) {
			case PERPULSE:
				rateMinorUnit = pulseMinorUnit;
				break;
			case SECOND : 
				rateMinorUnit = TimeUnit.SECOND.toSeconds(1l);
				break;
			case MINUTE :
				rateMinorUnit = TimeUnit.MINUTE.toSeconds(1l);
				break;
			case HOUR :
				rateMinorUnit = TimeUnit.HOUR.toSeconds(1l);
				break;
			default :
				rateMinorUnit = pulseMinorUnit;	
		}
		
	}

	public long getSlabValue() {
		return slabValue;
	}

	public long getPulse() {
		return pulse;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public Uom getPulseUnit() {
		return pulseUnit;
	}

	public Uom getRateUnit() {
		return rateUnit;
	}

	public TierRateType getRatingType() {
		return ratingType;
	}
	
	public long getRateMinorUnit() {
		return rateMinorUnit;
	}
	
	public long getPulseMinorUnit() {
		return pulseMinorUnit;
	}

	public Integer getDiscount() { return discount; }

	public String getRevenueDetail() {
		return revenueDetail;
	}

	@Override
	public String toString(){
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.append("Slab", slabValue);
		builder.append("Pulse", pulse!= CommonConstants.QUOTA_UNDEFINED?pulse:CommonConstants.UNDEFINED_STRING);
		builder.append("Rate", rate);
		builder.append("Discount(%)", discount);
		builder.append("Revenue Detail", revenueDetail);
	}
}
