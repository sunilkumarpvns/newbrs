package com.elitecore.corenetvertex.pd.ratecardgroup;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pd.calender.CalenderData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardScope;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData")
@Table(name = "TBLM_RATE_CARD_GROUP")
public class RateCardGroupData extends ResourceData implements Serializable{

	private static final long serialVersionUID = -3350072391717720699L;
	private String name;
	private String description;
	private transient String advanceCondition;
	private Integer orderNo;
	private Integer fixedCharge1;
	private Integer fixedCharge2;

	private transient RateCardData specialDayRateCard;
	private RateCardData peakRateRateCard;
	private RateCardData offPeakRateRateCard;
	private transient RateCardData weekendRateRateCard;
	private transient List<TimeSlotRelationData> timeSlotRelationData;
	private transient RncPackageData rncPackageData;

	private transient CalenderData peakRateCalender;
	private transient CalenderData offpeakRateCalender;
	private transient CalenderData specialDayCalender;
	private String peakRateRateCardId;
	private String offPeakRateRateCardId;
	private String rncPkgId;

	public RateCardGroupData() {
		timeSlotRelationData = Collectionz.newArrayList();
	}


	@Column(name = "FIXED_CHRGE1")
	public Integer getFixedCharge1() {
		return fixedCharge1;
	}

	public void setFixedCharge1(Integer fixedCharge1) {
		this.fixedCharge1 = fixedCharge1;
	}

	@Column(name = "FIXED_CHRGE2")
	public Integer getFixedCharge2() {
		return fixedCharge2;
	}

	public void setFixedCharge2(Integer fixedCharge2) {
		this.fixedCharge2 = fixedCharge2;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RNC_PACKAGE_ID")
	@XmlTransient
	@JsonIgnore
	public RncPackageData getRncPackageData() {
		return rncPackageData;
	}

	public void setRncPackageData(RncPackageData rncPackageData) {
		if(rncPackageData != null) {
			setRncPkgId(rncPackageData.getId());
		}
		this.rncPackageData = rncPackageData;
	}

	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PEAK_RATE_CALENDER")
	public CalenderData getPeakRateCalender() {
		return peakRateCalender;
	}

	public void setPeakRateCalender(CalenderData peakRateCalender) {
		this.peakRateCalender = peakRateCalender;
	}


	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OFF_PEAK_RATE_CALENDER")
	public CalenderData getOffpeakRateCalender() {
		return offpeakRateCalender;
	}

	public void setOffpeakRateCalender(CalenderData offpeakRateCalender) {
		this.offpeakRateCalender = offpeakRateCalender;
	}

	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OFF_PEAK_RATE_RATECARD_ID")
	public RateCardData getOffPeakRateRateCard() {
		return offPeakRateRateCard;
	}

	public void setOffPeakRateRateCard(RateCardData offPeakRateRateCard) {
        if(offPeakRateRateCard != null){
            setOffPeakRateRateCardId(offPeakRateRateCard.getId());
        }
		this.offPeakRateRateCard = offPeakRateRateCard;
	}


	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SPECIAL_DAY_CALENDER")
	public CalenderData getSpecialDayCalender() {
		return specialDayCalender;
	}

	public void setSpecialDayCalender(CalenderData specialDayCalender) {
		this.specialDayCalender = specialDayCalender;
	}



	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PEAK_RATE_RATECARD_ID")
	public RateCardData getPeakRateRateCard() {
		return peakRateRateCard;
	}

	public void setPeakRateRateCard(RateCardData peakRateRateCard) {
		if(peakRateRateCard != null){
			setPeakRateRateCardId(peakRateRateCard.getId());
		}
		this.peakRateRateCard = peakRateRateCard;
	}


	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "WEEKEND_RATE_RATECARD_ID")
	public RateCardData getWeekendRateRateCard() {
		return weekendRateRateCard;
	}

	public void setWeekendRateRateCard(RateCardData weekendRateRateCard) {
		this.weekendRateRateCard = weekendRateRateCard;
	}


	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SPECIAL_DAY_RATECARD_ID")
	public RateCardData getSpecialDayRateCard() {
		return specialDayRateCard;
	}

	public void setSpecialDayRateCard(RateCardData specialDayRateCard) {
		this.specialDayRateCard = specialDayRateCard;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "rateCardGroupData", cascade = {
			CascadeType.ALL }, orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy("orderNo ASC")
	public List<TimeSlotRelationData> getTimeSlotRelationData() {
		return timeSlotRelationData;
	}

	public void setTimeSlotRelationData(List<TimeSlotRelationData> timeSlotRelationData) {
		this.timeSlotRelationData = timeSlotRelationData;
	}

	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "ADVANCE_CONDITION")
	public String getAdvanceCondition() {
		return advanceCondition;
	}

	public void setAdvanceCondition(String advanceCondition) {
		this.advanceCondition = advanceCondition;
	}

	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	@Override
	@Transient
	public String getGroups() {
		return rncPackageData.getGroups();
	}

	@Column(name = "ORDER_NO")
	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	@Transient
	public String getPeakRateRateCardId() {
		return peakRateRateCardId;
	}

	public void setPeakRateRateCardId(String peakRateRateCardId) {
		this.peakRateRateCardId = peakRateRateCardId;
	}

	@Transient
	public String getOffPeakRateRateCardId() {
		return offPeakRateRateCardId;
	}

	public void setOffPeakRateRateCardId(String offPeakRateRateCardId) {
		this.offPeakRateRateCardId = offPeakRateRateCardId;
	}

	@Transient
	@Override
	public String getResourceName() {
		return getName();
	}

	public RateCardGroupData copyModel(RncPackageData rncPackageData) {

        RateCardGroupData newrateCardGroupData = new RateCardGroupData();
        newrateCardGroupData.setId(null);
        newrateCardGroupData.setRncPkgId(null);
        newrateCardGroupData.setPeakRateRateCardId(null);
        newrateCardGroupData.setOffPeakRateRateCardId(null);
        newrateCardGroupData.setName(this.name);
        newrateCardGroupData.setAdvanceCondition(this.advanceCondition);
        newrateCardGroupData.setOrderNo(this.orderNo);
        newrateCardGroupData.setFixedCharge1(this.fixedCharge1);
        newrateCardGroupData.setFixedCharge2(this.fixedCharge2);
        newrateCardGroupData.setDescription(this.description);

        if (Objects.nonNull(this.getOffPeakRateRateCard())) {
        	if(this.getOffPeakRateRateCard().getScope().equalsIgnoreCase(RateCardScope.GLOBAL.name())) {
				newrateCardGroupData.setOffPeakRateRateCard(this.getOffPeakRateRateCard());
				newrateCardGroupData.setCreatedByStaff(rncPackageData.getCreatedByStaff());
			} else {
				for (RateCardData rateCard : rncPackageData.getRateCardData()) {
					if (rateCard.getName().equalsIgnoreCase(this.offPeakRateRateCard.getName())) {
						newrateCardGroupData.setOffPeakRateRateCard(rateCard);
						newrateCardGroupData.setCreatedByStaff(rncPackageData.getCreatedByStaff());
						break;
					}
				}
			}
        }

        if (Objects.nonNull(this.getPeakRateRateCard())) {
			if(this.getPeakRateRateCard().getScope().equalsIgnoreCase(RateCardScope.GLOBAL.name())) {
				newrateCardGroupData.setPeakRateRateCard(this.getPeakRateRateCard());
				newrateCardGroupData.setCreatedByStaff(rncPackageData.getCreatedByStaff());
			} else {
				for (RateCardData rateCard : rncPackageData.getRateCardData()) {
					if (rateCard.getName().equalsIgnoreCase(this.peakRateRateCard.getName())) {
						newrateCardGroupData.setPeakRateRateCard(rateCard);
						newrateCardGroupData.setCreatedByStaff(rncPackageData.getCreatedByStaff());
						break;
					}
				}
			}
        }


        if (Objects.nonNull(this.getSpecialDayRateCard())) {
            for (RateCardData rateCard : rncPackageData.getRateCardData()) {
                if (rateCard.getName().equalsIgnoreCase(this.specialDayRateCard.getName())) {
                    newrateCardGroupData.setSpecialDayRateCard(rateCard);
                    newrateCardGroupData.setCreatedByStaff(rncPackageData.getCreatedByStaff());
                    break;
                }
            }
        }

        if (Objects.nonNull(this.getWeekendRateRateCard())) {
            for (RateCardData rateCard : rncPackageData.getRateCardData()) {
                if (rateCard.getName().equalsIgnoreCase(this.weekendRateRateCard.getName())) {
                    newrateCardGroupData.setWeekendRateRateCard(rateCard);
                    newrateCardGroupData.setCreatedByStaff(rncPackageData.getCreatedByStaff());
                    break;
                }

            }
        }

        if (Objects.nonNull(this.getPeakRateCalender())) {
            newrateCardGroupData.setPeakRateCalender(this.getPeakRateCalender());
        }

        if (Objects.nonNull(this.getSpecialDayCalender())) {
            newrateCardGroupData.setSpecialDayCalender(this.getSpecialDayCalender());
        }

        if (Objects.nonNull(this.getOffpeakRateCalender())) {
            newrateCardGroupData.setOffpeakRateCalender(this.getOffpeakRateCalender());
        }


        List<TimeSlotRelationData> newTimeSlotRelationData = new ArrayList<>();
        for (TimeSlotRelationData timeSlotRelation : this.timeSlotRelationData) {
            TimeSlotRelationData timeSlotRelationDataCopy = (TimeSlotRelationData) timeSlotRelation.copyModel();
            timeSlotRelationDataCopy.setRateCardGroupData(newrateCardGroupData);
            newTimeSlotRelationData.add(timeSlotRelationDataCopy);
        }
        newrateCardGroupData.setTimeSlotRelationData(newTimeSlotRelationData);
        newrateCardGroupData.setCreatedByStaff(rncPackageData.getCreatedByStaff());
        return newrateCardGroupData;
    }

	@Override
	public JsonObject toJson() {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.NAME, name);
		jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
		jsonObject.addProperty(FieldValueConstants.ADVANCE_CONDITION, advanceCondition);
		jsonObject.addProperty(FieldValueConstants.ORDER_NO, orderNo);
		if(peakRateRateCard != null) {
			jsonObject.addProperty(FieldValueConstants.PEAK_RATE_RATE_CARD, peakRateRateCard.getName());
			jsonObject.addProperty(FieldValueConstants.PEAK_RATE_RATE_CARD_ID, peakRateRateCard.getId());
		}
		if(offPeakRateRateCard != null) {
			jsonObject.addProperty(FieldValueConstants.OFF_PEAK_RATE_RATE_CARD, offPeakRateRateCard.getName());
			jsonObject.addProperty(FieldValueConstants.OFF_PEAK_RATE_RATE_CARD_ID, offPeakRateRateCard.getId());
		}
		if (timeSlotRelationData != null) {
			JsonArray jsonArray = new JsonArray();
			for (TimeSlotRelationData timeSlotRelation : timeSlotRelationData) {
				jsonArray.add(timeSlotRelation.toJson());
			}
			jsonObject.add(FieldValueConstants.TIME_SLOT, jsonArray);
		}
		return jsonObject;

	}

	@Transient
	@Override
	public String getAuditableId() {
		return rncPackageData.getId();
	}

	@Transient
	@Override
	public ResourceData getAuditableResource() {
		return rncPackageData;
	}

	@Override
	@Transient
	public String getHierarchy() {
		return rncPackageData.getHierarchy() +"<br>"+ getId() + "<br>"+ name;
	}

	@Transient
	public String getRncPkgId() {
		return rncPkgId;
	}

	public void setRncPkgId(String rncPkgId) {
		this.rncPkgId = rncPkgId;
	}

}
