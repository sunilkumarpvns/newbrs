package com.elitecore.corenetvertex.pm.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class RatingUtility {


	public static DecimalFormat createDecimalFormat() {
		DecimalFormat decimalFormatter = new DecimalFormat();
		decimalFormatter.setRoundingMode(RoundingMode.HALF_EVEN);
		decimalFormatter.setGroupingUsed(false);
		decimalFormatter.setMaximumFractionDigits(6);
		decimalFormatter.setMinimumFractionDigits(6);
		return decimalFormatter;
	}
}
