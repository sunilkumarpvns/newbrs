
package com.elitecore.corenetvertex.pm.rnc.ratecard;

import com.elitecore.corenetvertex.constants.TierRateType;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class MonetaryRateCardVeresionDetail implements Serializable, ToStringable {

	private static final long serialVersionUID = 1L;

	private String id;
	private final String label1;
	private final String label2;
	private final List<RateSlab> slabs;
	private final Timestamp fromDate;
	private final TierRateType ratingType;
	private final String rncPackageId;
	private final String rncPackageName;
	private final String rateCardGroupId;
	private final String rateCardGroupName;
	private final String rateCardId;
	private final String rateCardName;
	private final String rateCardVersionId;
	private final String rateCardVersionName;

	public MonetaryRateCardVeresionDetail(String id, String label1, String label2, Timestamp fromDate, TierRateType ratingType, List<RateSlab> slabs,
											String rncPackageId, String rncPackageName, String rateCardGroupId, String rateCardGroupName,
											String rateCardId, String rateCardName, String rateCardVersionId, String rateCardVersionName) {
		this.id = id;
		this.label1 = label1;
		this.label2 = label2;
		this.fromDate = fromDate;
		this.slabs = slabs;
		this.ratingType = ratingType;
		this.rncPackageId = rncPackageId;
		this.rncPackageName = rncPackageName;
		this.rateCardGroupId = rateCardGroupId;
		this.rateCardGroupName = rateCardGroupName;
		this.rateCardId = rateCardId;
		this.rateCardName = rateCardName;
		this.rateCardVersionId = rateCardVersionId;
		this.rateCardVersionName = rateCardVersionName;
	}

	public String getId() {
		return id;
	}

	public List<RateSlab> getSlabs() {
		return slabs;
	}

	public Timestamp getFromDate() {
		return fromDate;
	}

	public TierRateType getRatingType() {
		return ratingType;
	}

	public String getLabel1() {
		return label1;
	}

	public String getLabel2() {
		return label2;
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

	public String getRateCardVersionId() {
		return rateCardVersionId;
	}

	public String getRateCardVersionName() {
		return rateCardVersionName;
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.newline()
		.append("Key One", label1)
		.append("Key Two", label2);
		if(fromDate!=null){
			builder.append("From Date", fromDate);
		}
		builder.append("Rating Type", ratingType.name());

		builder.appendChildObject("Flat Slabs", slabs);

	}

	@Override
	public String toString(){
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		toString(builder);
		return builder.toString();
	}
}
