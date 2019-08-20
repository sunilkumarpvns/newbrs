package com.elitecore.corenetvertex.pm.rnc.ratecard;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.io.Serializable;
import java.util.List;

public class MonetaryRateCardVersion implements Serializable, ToStringable{

	private static final long serialVersionUID = 1L;

	private final String id;
	private final String name;
	private final String rncPackageId;
	private final String rncPackageName;
	private final String rateCardGroupId;
	private final String rateCardGroupName;
	private final String rateCardId;
	private final String rateCardName;
	private final List<MonetaryRateCardVeresionDetail> ratingBehaviors;

	public MonetaryRateCardVersion(String id, String name, List<MonetaryRateCardVeresionDetail> ratingBehaviors,
								   String rncPackageId, String rncPackageName, String rateCardGroupId, String rateCardGroupName,
								   String rateCardId,String rateCardName) {
		this.id = id;
		this.name = name;
		this.ratingBehaviors = ratingBehaviors;
		this.rncPackageId = rncPackageId;
		this.rncPackageName = rncPackageName;
		this.rateCardGroupId = rateCardGroupId;
		this.rateCardGroupName = rateCardGroupName;
		this.rateCardId = rateCardId;
		this.rateCardName = rateCardName;
	}

	public String getName() {
		return name;
	}

	public List<MonetaryRateCardVeresionDetail> getRatingBehaviors() {
		return ratingBehaviors;
	}

	public String getId() {
		return id;
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

	public String getRateCardId() {
		return rateCardId;
	}

	public String getRateCardName() {
		return rateCardName;
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.append("Name", name);
		builder.appendChildObject("Version Details",ratingBehaviors);
	}

	@Override
	public String toString(){
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		toString(builder);
		return builder.toString();
	}
}
