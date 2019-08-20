package com.elitecore.netvertex.service.offlinernc.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalFormatter {

	private final int ratingDecimalPoint;
	private final RoundingModeTypes roundingMode;

	public BigDecimalFormatter(int ratingDecimalPoint, RoundingModeTypes roundingMode) {
		this.ratingDecimalPoint = ratingDecimalPoint;
		this.roundingMode = roundingMode;
	}

	public BigDecimal scale(BigDecimal input) {
		if (RoundingModeTypes.UPPER == roundingMode) {
			return input.setScale(ratingDecimalPoint, RoundingMode.HALF_EVEN);
		} else {
			return input.setScale(ratingDecimalPoint, RoundingMode.DOWN);
		}
	}
	
	public String format(BigDecimal input) {
		return scale(input).toString();
	}
}
