package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard;


import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;

import java.math.BigDecimal;

public class FlatSlab implements RateSlab {

	private long slabValue;
	private long pulse;
	private BigDecimal rate;
	private Uom pulseUom;
	private Uom rateUom;
	private long rateUomInBytesOrSeconds;
	private long pulseInBytesOrSeconds;

	public FlatSlab(long slabValue, long pulse, BigDecimal rate, Uom pulseUom, Uom rateUom) {
		this.slabValue = slabValue;
		this.pulse = pulse;
		this.rate = rate;
		this.pulseUom = pulseUom;
		this.rateUom = rateUom;
		setPulseInBytesInSeconds(pulse, pulseUom);
		setRateUomInBytesInSeconds(rateUom);
	}

	private void setPulseInBytesInSeconds(long pulse, Uom pulseUom) {
		switch (pulseUom) {
			case SECOND:
				pulseInBytesOrSeconds = pulse;
				break;
			case MINUTE:
				pulseInBytesOrSeconds = TimeUnit.MINUTE.toSeconds(pulse);
				break;
			case HOUR:
				pulseInBytesOrSeconds = TimeUnit.HOUR.toSeconds(pulse);
				break;
			case MB:
				pulseInBytesOrSeconds = DataUnit.MB.toBytes(pulse);
				break;
			case KB:
				pulseInBytesOrSeconds = DataUnit.KB.toBytes(pulse);
				break;
			case GB:
				pulseInBytesOrSeconds = DataUnit.GB.toBytes(pulse);
				break;
			case BYTE:
				pulseInBytesOrSeconds = pulse;
				break;
		}
	}

	private void setRateUomInBytesInSeconds(Uom rateUom) {
		switch (rateUom) {
			case SECOND:
			case BYTE:
				rateUomInBytesOrSeconds = 1;
				break;
			case MINUTE:
				rateUomInBytesOrSeconds = TimeUnit.MINUTE.toSeconds(1);
				break;
			case HOUR:
				rateUomInBytesOrSeconds = TimeUnit.HOUR.toSeconds(1);
				break;
			case MB:
				rateUomInBytesOrSeconds = DataUnit.MB.toBytes(1);
				break;
			case KB:
				rateUomInBytesOrSeconds = DataUnit.KB.toBytes(1);
				break;
			case GB:
				rateUomInBytesOrSeconds = DataUnit.GB.toBytes(1);
				break;
			case PERPULSE:
				rateUomInBytesOrSeconds = pulseInBytesOrSeconds;
				break;
		}
	}

	@Override
    public long getSlabValue() {
		return slabValue;
	}

	@Override
    public long getPulse() {
		return pulse;
	}

	@Override
	public BigDecimal getRate() {
		return rate;
	}

	@Override
    public Uom getPulseUom() {
		return pulseUom;
	}

	@Override
    public Uom getRateUom() {
		return rateUom;
	}

	@Override
	public long getPulseInBytesOrSeconds() {
		return pulseInBytesOrSeconds;
	}

	@Override
	public boolean isVolumeBasedRateDefined() {
		if(rateUom == Uom.PERPULSE ) {
			return "VOLUME".equals(pulseUom.getUnitType());
		} else {
			return "VOLUME".equals(rateUom.getUnitType());
		}

	}

	@Override
	public long getRateUomInBytesOrSeconds() {
		return rateUomInBytesOrSeconds;
	}

	@Override
	public String toString() {

		IndentingToStringBuilder toStringBuilder = new IndentingToStringBuilder();

		toString(toStringBuilder);

		return toStringBuilder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.newline()
				.append("Slab Value", slabValue)
				.append("Pulse", pulse)
				.append("Rate", rate)
				.append("Pulse UoM", pulseUom)
				.append("Rate UoM", rateUom);
	}
}
