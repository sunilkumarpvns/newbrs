package com.elitecore.corenetvertex.pm.rnc.ratecard;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;

import java.util.List;

public class MonetaryRateCard implements RateCard{

	private static final long serialVersionUID = 1L;
	private static final RateCardType type = RateCardType.MONETARY;

	private String id;
	private String name;
	private String description;
	private String keyOne;
	private String keyTwo;
	private Uom rateUnit;
	private Uom pulseUnit;
	private List<MonetaryRateCardVersion> rateCardVersions;
	private String rncPackageId;
	private String rncPackageName;
	private String rateCardGroupId;
	private String rateCardGroupName;

	public MonetaryRateCard(String id, String name, String description, String keyOne, String keyTwo,
							List<MonetaryRateCardVersion> rateCardVersions, Uom rateUnit, Uom pulseUnit,
							String rncPackageId, String rncPackageName, String rateCardGroupId, String rateCardGroupName) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.keyOne = keyOne;
		this.keyTwo = keyTwo;
		this.rateCardVersions = rateCardVersions;
		this.rateUnit = rateUnit;
		this.pulseUnit = pulseUnit;
		this.rncPackageId = rncPackageId;
		this.rncPackageName = rncPackageName;
		this.rateCardGroupId = rateCardGroupId;
		this.rateCardGroupName = rateCardGroupName;

	}

	public String getName() {
		return name;
	}

	public String getKeyOne() {
		return keyOne;
	}

	public String getKeyTwo() {
		return keyTwo;
	}

	public List<MonetaryRateCardVersion> getRateCardVersions() {
		return rateCardVersions;
	}

	public Uom getRateUnit() {
		return rateUnit;
	}

	public Uom getPulseUnit() {
		return pulseUnit;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getRncPackageId() {
		return rncPackageId;
	}

	public String getRncPackageName() {
		return rncPackageName;
	}

	public String getRateCardGroupId() {
		return rateCardGroupId;
	}

	public String getRateCardGroupName() {
		return rateCardGroupName;
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {

		builder.append("Name", name);
		builder.append("Description", description);
		builder.append("Type", type.getValue());

		if(Strings.isNullOrBlank(keyOne)==false){
			builder.append("Key One", keyOne);
		}
		if(Strings.isNullOrBlank(keyTwo)==false) {
			builder.append("Key Two", keyTwo);
		}
		if(rateUnit!=null){
			builder.append("Rate Unit", rateUnit);
		}
		if(pulseUnit!=null){
			builder.append("Pulse Unit", pulseUnit);
		}

		builder.newline();
		builder.appendChildObject("Versions", rateCardVersions);
	}

	@Override
	public String toString(){
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public RateCardType getType() {
		return type;
	}
}
