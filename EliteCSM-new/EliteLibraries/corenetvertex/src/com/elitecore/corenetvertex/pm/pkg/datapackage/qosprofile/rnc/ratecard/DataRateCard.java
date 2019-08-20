package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard;

import java.io.Serializable;
import java.util.List;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class DataRateCard implements RateCard, Serializable, ToStringable{

    private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String keyOne;
	private String keyTwo;
	private Uom pulseUom;
	private Uom rateUom;

	private List<RateCardVersion> dataRateCardVersions;
	
	public DataRateCard(String id, String name, String keyOne,
			String keyTwo, List<RateCardVersion> dataRateCardVersions,
						Uom pulseUom, Uom rateUom) {
		this.id = id;
		this.name = name;
		this.keyOne = keyOne;
		this.keyTwo = keyTwo;
		this.dataRateCardVersions = dataRateCardVersions;
		this.pulseUom = pulseUom;
		this.rateUom = rateUom;
	}

	@Override
	public String getName() {
		return name;
	}

    @Override
	public String getKeyOne() {
		return keyOne;
	}

    @Override
	public String getKeyTwo() {
		return keyTwo;
	}

    @Override
	public List<RateCardVersion> getRateCardVersions() {
		return dataRateCardVersions;
	}

    @Override
	public String getId() {
		return id;
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
				.newline()
				.incrementIndentation()
				.incrementIndentation()
				.append("Id", id)
				.append("Name", name)
				.append("Key One", keyOne)
				.append("Key Two", keyTwo)
				.append("Pulse UoM", pulseUom)
				.append("Rate UoM", rateUom)
				.appendChildObject("Rate Card Versions", dataRateCardVersions);

	}

    @Override
	public Uom getPulseUom() {
		return pulseUom;
	}

    @Override
	public Uom getRateUom() {
		return rateUom;
	}
}
