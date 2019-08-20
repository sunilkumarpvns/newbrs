package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;

import java.io.Serializable;
import java.util.List;

public class FlatRating implements VersionDetail, Serializable {

	private List<RateSlab> slabs;
	private String keyOneValue;
	private String keyTwoValue;
	private String revenueDetail;

	public FlatRating(String keyOneValue, String keyTwoValue, List<RateSlab> slabs, String revenueDetail) {
		this.keyOneValue = keyOneValue;
		this.keyTwoValue = keyTwoValue;
		this.slabs = slabs;
		this.revenueDetail = revenueDetail;
	}

	@Override
	public String getKeyOneValue() {
		return keyOneValue;
	}

	@Override
	public String getKeyTwoValue() {
		return keyTwoValue;
	}

	public List<RateSlab> getSlabs() {
		return slabs;
	}

	@Override
	public String revenueDetail() {
		return revenueDetail;
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
				.append("Key One", keyOneValue)
				.append("Key Two", keyTwoValue)
				.appendChildObject("Flat Slabs", slabs)
		        .append("Revenue Detail", revenueDetail);
	}
}


