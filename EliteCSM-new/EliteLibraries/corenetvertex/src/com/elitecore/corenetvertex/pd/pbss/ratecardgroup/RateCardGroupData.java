package com.elitecore.corenetvertex.pd.pbss.ratecardgroup;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pd.calender.CalenderData;
import com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.pbss.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Entity(name = "com.elitecore.corenetvertex.pd.pbss.ratecardgroup.RateCardGroupData")
@Table(name = "TBLM_RATECARD_GROUP")
public class RateCardGroupData extends ResourceData implements Serializable {

	private static final long serialVersionUID = -3350072391717720699L;
	private String name;
	private String description;
	private String accountEffect;
	private String labels;
	private transient String advanceCondition;
	private Integer orderNo;
	
	private RateCardData specialDayRateCard;
	private RateCardData peakRateRateCard;
	private RateCardData offpeakRateRateCard;
	private RateCardData weekendRateRateCard;
	private RateCardData rate2RateCard;
	private RateCardData rate3RateCard;
	private List<TimeSlotRelationData> timeSlotRelationData;

	private CalenderData peakRateCalender;
	private CalenderData offpeakRateCalender;
	private CalenderData specialDayCalender;
	
	private String specialRateCalenderId;
	
	private String specialDayRateCardId;
	private String peakRateRateCardId;
	private String offpeakRateRateCardId;
	private String weekendRateRateCardId;
	private String rate2RateCardId;
	private String rate3RateCardId;
	
	private transient RncPackageData rncPackageData;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RNC_PACKAGE_ID")
	@Fetch(FetchMode.JOIN)
	@XmlTransient
	@JsonIgnore
	public RncPackageData getRncPackageData() {
		return rncPackageData;
	}

	public void setRncPackageData(RncPackageData rncPackageData) {
		this.rncPackageData = rncPackageData;
	}

	public RateCardGroupData() {
		timeSlotRelationData = Collectionz.newArrayList();
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
	@JoinColumn(name = "OFF_PEAK_RATE_RATECARD")
	public RateCardData getOffpeakRateRateCard() {
		return offpeakRateRateCard;
	}

	public void setOffpeakRateRateCard(RateCardData offpeakRateRateCard) {
		this.offpeakRateRateCard = offpeakRateRateCard;
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



	@Transient
	public String getSpecialRateCalenderId() {
		if (Strings.isNullOrBlank(specialRateCalenderId)) {
			return specialRateCalenderId;
		}
		return specialRateCalenderId;
	}

	public void setSpecialRateCalenderId(String specialRateCalendarId) {
		if (Strings.isNullOrBlank(specialRateCalendarId) == false) {
			CalenderData specialRateCalendar = new CalenderData();
			specialRateCalendar.setId(specialRateCalendarId);
			this.specialDayCalender = specialRateCalendar;
			this.specialRateCalenderId = specialRateCalendarId;
		}
	}

	
	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PEAK_RATE_RATECARD")
	public RateCardData getPeakRateRateCard() {
		return peakRateRateCard;
	}

	public void setPeakRateRateCard(RateCardData peakRateRateCard) {
		this.peakRateRateCard = peakRateRateCard;
	}

	
	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "WEEKEND_RATE_RATECARD")
	public RateCardData getWeekendRateRateCard() {
		return weekendRateRateCard;
	}

	public void setWeekendRateRateCard(RateCardData weekendRateRateCard) {
		this.weekendRateRateCard = weekendRateRateCard;
	}
	
	
	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SPECIAL_DAY_RATECARD")
	public RateCardData getSpecialDayRateCard() {
		return specialDayRateCard;
	}

	public void setSpecialDayRateCard(RateCardData specialDayRateCard) {
		this.specialDayRateCard = specialDayRateCard;
	}
	
	@Transient
	public String getSpecialDayRateCardId() {
		if (Strings.isNullOrBlank(specialDayRateCardId)) {
			this.specialDayRateCardId = this.specialDayRateCard.getId();
		}
		return specialDayRateCardId;
	}

	public void setSpecialDayRateCardId(String specialRateCard) {
		if (Strings.isNullOrBlank(specialRateCard) == false) {
			RateCardData specialRateCardData = new RateCardData();
			specialRateCardData.setId(specialRateCard);
			this.specialDayRateCard = specialRateCardData;
			this.specialDayRateCardId = specialRateCard;
		}
	}
	
	@Transient
	public String getPeakRateRateCardId() {
		if (Strings.isNullOrBlank(peakRateRateCardId) ) {
			this.peakRateRateCardId = this.peakRateRateCard.getId();
		}
		return peakRateRateCardId;
	}

	public void setPeakRateRateCardId(String peakRateCard) {
		if (Strings.isNullOrBlank(peakRateCard) == false) {
			RateCardData peakRateCardData = new RateCardData();
			peakRateCardData.setId(peakRateCard);
			this.peakRateRateCard = peakRateCardData;
			this.peakRateRateCardId = peakRateCard;
		}
	}
	
	@Transient
	public String getOffpeakRateRateCardId() {
		if (Strings.isNullOrBlank(offpeakRateRateCardId)) {
			this.offpeakRateRateCardId = this.offpeakRateRateCard.getId();
		}
		return offpeakRateRateCardId;
	}

	public void setOffpeakRateRateCardId(String peakRateCard) {
		if (Strings.isNullOrBlank(peakRateCard) == false) {
			RateCardData offpeakRateCardData = new RateCardData();
			offpeakRateCardData.setId(peakRateCard);
			this.offpeakRateRateCard = offpeakRateCardData;
			this.offpeakRateRateCardId = peakRateCard;
			
		}
	}
	
	@Transient
	public String getWeekendRateRateCardId() {
		if (Strings.isNullOrBlank(weekendRateRateCardId)) {
			this.weekendRateRateCardId = this.weekendRateRateCard.getId();
		}
		return weekendRateRateCardId;
	}

	public void setWeekendRateRateCardId(String weekRateCard) {
		if (Strings.isNullOrBlank(weekRateCard) == false) {
			RateCardData weekRateCardData = new RateCardData();
			weekRateCardData.setId(weekRateCard);
			this.weekendRateRateCard = weekRateCardData;
			this.weekendRateRateCardId = weekRateCard;
		}
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rateCardGroupData", cascade = {
			CascadeType.ALL }, orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	public List<TimeSlotRelationData> getTimeSlotRelationData() {
		return timeSlotRelationData;
	}

	public void setTimeSlotRelationData(List<TimeSlotRelationData> timeSlotRelationData) {
		Collectionz.filter(timeSlotRelationData, timeSlotRelationDatas -> {
			boolean isValidTimeSlot = true;
			if (timeSlotRelationDatas == null) {
				return false;
			} else {
				if (Strings.isNullOrBlank(timeSlotRelationDatas.getType())) {
					isValidTimeSlot = false;
				}
			}
			return isValidTimeSlot;
		});
		
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
	
	@Column(name = "ACCOUNT_EFFECT")
	public String getAccountEffect() {
		return accountEffect;
	}

	public void setAccountEffect(String accountEffect) {
		this.accountEffect = accountEffect;
	}


	@Column(name = "LABELS")
	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
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
	@Column(name = "GROUPS")
	public String getGroups() {
		return super.getGroups();
	}
	
	@Column(name = "ORDER_NO")
	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	@Transient
	@XmlTransient
	@Override
	public String getHierarchy() {
		return getId() + "<br>" + name;
	}

	@Transient
	@Override
	public String getResourceName() {
		return getName();
	}
	
	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RATE_TWO_RATECARD")
	public RateCardData getRate2RateCard() {
		return rate2RateCard;
	}

	public void setRate2RateCard(RateCardData rate2RateCard) {
		this.rate2RateCard = rate2RateCard;
	}
	
	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RATE_THREE_RATECARD")
	public RateCardData getRate3RateCard() {
		return rate3RateCard;
	}

	public void setRate3RateCard(RateCardData rate3RateCard) {
		this.rate3RateCard = rate3RateCard;
	}
	
	
	@Transient
	public String getRate2RateCardId() {
		if (Strings.isNullOrBlank(rate2RateCardId)) {
			this.rate2RateCardId = this.rate2RateCard.getId();
		}
		return rate2RateCardId;
	}
	
	public void setRate2RateCardId(String rate2RateCardId) {
		if (Strings.isNullOrBlank(rate2RateCardId) == false) {
			RateCardData rate2RateCardData = new RateCardData();
			rate2RateCardData.setId(rate2RateCardId);
			this.rate2RateCard = rate2RateCardData;
			this.rate2RateCardId = rate2RateCardId;
		}
	}
	
	@Transient
	public String getRate3RateCardId() {
		if (Strings.isNullOrBlank(rate3RateCardId)) {
			this.rate3RateCardId = this.rate3RateCard.getId();
		}
		return rate3RateCardId;
	}
	
	public void setRate3RateCardId(String rate3RateCardId) {
		if (Strings.isNullOrBlank(rate3RateCardId) == false) {
			RateCardData rate3RateCardData = new RateCardData();
			rate3RateCardData.setId(rate3RateCardId);
			this.rate3RateCard = rate3RateCardData;
			this.rate3RateCardId = rate3RateCardId;
		}
	}
	
	
	
	@Override
	public JsonObject toJson() {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.NAME, name);
		jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
		jsonObject.addProperty(FieldValueConstants.LABELS, labels);
		jsonObject.addProperty(FieldValueConstants.ADVANCE_CONDITION, advanceCondition);
		jsonObject.addProperty(FieldValueConstants.ORDER_NO, orderNo);
		jsonObject.addProperty(FieldValueConstants.PEAK_RATE_RATE_CARD, peakRateRateCard.getName());
		if (timeSlotRelationData != null) {
			JsonArray jsonArray = new JsonArray();
			for (TimeSlotRelationData timeSlotRelation : timeSlotRelationData) {
				jsonArray.add(timeSlotRelation.toJson());
			}
			jsonObject.add("Time Slot", jsonArray);
		}
		return jsonObject;

	}

}
