package com.elitecore.corenetvertex.pm.rnc.rcgroup;

import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.corenetvertex.pd.ratecardgroup.TimeSlotRelationData;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCard;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

import java.io.Serializable;
import java.util.List;

public class RateCardGroup implements Serializable, ToStringable {

	private static final long serialVersionUID = 1L;

	private static final String MODULE = "RCG";

	private String id;
	private String name;
	private String description;
	private LogicalExpression advancedCondition;
	private RateCard peakRateCard;
	private RateCard offPeakRateCard;
	private String rncPackageId;
	private String rncPackageName;
	private int order;
	private AccessTimePolicy accessTimePolicy;
	private List<TimeSlotRelationData> timeSlotRelationDatas;

	public RateCardGroup(String id, String name, String description, LogicalExpression advancedCondition, RateCard peakDaysRateCard, RateCard offPeakRateCard, String rncPackageId, String rncPackageName, int order, AccessTimePolicy accessTimePolicy, List<TimeSlotRelationData> timeSlotRelationDatas) {

		this.id = id;
		this.name = name;
		this.description = description;
		this.advancedCondition = advancedCondition;
		this.peakRateCard = peakDaysRateCard;
		this.offPeakRateCard = offPeakRateCard;
		this.rncPackageId = rncPackageId;
		this.rncPackageName = rncPackageName;
		this.order = order;
		this.accessTimePolicy = accessTimePolicy;
		this.timeSlotRelationDatas = timeSlotRelationDatas;
	}

	public static String getMODULE() {
		return MODULE;
	}

	public String getName() {
		return name;
	}

	public LogicalExpression getAdvancedCondition() {
		return advancedCondition;
	}

	public RateCard getPeakRateCard() {
		return peakRateCard;
	}

	public RateCard getOffPeakRateCard() {
		return offPeakRateCard;
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

	public int getOrder() {
		return order;
	}

	public AccessTimePolicy getAccessTimePolicy() {
		return accessTimePolicy;
	}

	@Override
	public void toString(IndentingToStringBuilder builder){
		builder.append("Name", name);
		builder.append("Description", description);
		if(advancedCondition!=null){
			builder.append("Advanced Condition", advancedCondition);
		}
		builder.appendChildObject("Peak Rate Card", peakRateCard);
		builder.appendChildObject("Off-Peak Rate Card", offPeakRateCard);
		if(accessTimePolicy != null){
			builder.append("Time Restrictions", accessTimePolicy);
		}
	}

	@Override
	public String toString(){
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		toString(builder);
		return builder.toString();
	}

	public List<TimeSlotRelationData> getTimeSlotRelationDatas() {
		return timeSlotRelationDatas;
	}

	public void setTimeSlotRelationDatas(List<TimeSlotRelationData> timeSlotRelationDatas) {
		this.timeSlotRelationDatas = timeSlotRelationDatas;
	}
}
