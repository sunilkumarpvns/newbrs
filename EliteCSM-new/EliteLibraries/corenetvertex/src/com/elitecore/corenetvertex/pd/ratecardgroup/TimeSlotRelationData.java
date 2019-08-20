package com.elitecore.corenetvertex.pd.ratecardgroup;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.google.gson.JsonObject;

@Entity(name="com.elitecore.corenetvertex.pd.ratecardgroup.TimeSlotRelationData")
@Table(name = "TBLM_RCG_TIME_SLOT_REL_DATA")
public class TimeSlotRelationData implements Serializable{
	
	private static final long serialVersionUID = 3369608460484901596L;
	private String id;
    private String dayOfWeek;
    private String timePeriod;
    private Integer orderNo;
    private String type;
    private transient RateCardGroupData rateCardGroupData;
    
    public TimeSlotRelationData() {
	}


	public Object copyModel(){
		TimeSlotRelationData newtimeSlotRelationData=new TimeSlotRelationData();
		newtimeSlotRelationData.setId(null);
		newtimeSlotRelationData.setDayOfWeek(this.dayOfWeek);
		newtimeSlotRelationData.setTimePeriod(this.timePeriod);
		newtimeSlotRelationData.setOrderNo(this.orderNo);
		newtimeSlotRelationData.setType(this.type);
		newtimeSlotRelationData.setId(null);
		newtimeSlotRelationData.setRateCardGroupData(null);
		return newtimeSlotRelationData;

	}

	public TimeSlotRelationData(String dayOfWeek, String timePeriod, String type, Integer orderNumber) {
    	this.dayOfWeek = dayOfWeek;
    	this.timePeriod = timePeriod;
    	this.type = type;
    	this.orderNo = orderNumber;
    }
    
	@Column(name ="ORDER_NUMBER")
    public Integer getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}
	
    @Column(name =  "TYPE")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
    
	@Id
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @Column(name ="ID")
    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name =  "RATECARD_GROUP_ID")
    @JsonIgnore
    public RateCardGroupData getRateCardGroupData() {
		return rateCardGroupData;
	}
	public void setRateCardGroupData(RateCardGroupData rateCardGroupData) {
		this.rateCardGroupData = rateCardGroupData;
	}
	
	@Column(name="DAY_OF_WEEK")
	public String getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	@Column(name="TIME_PERIOD")
	public String getTimePeriod() {
		return timePeriod;
	}
	public void setTimePeriod(String timePeriod) {
		this.timePeriod = timePeriod;
	}
	
	public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.DAY_OF_WEEK, dayOfWeek);
        jsonObject.addProperty(FieldValueConstants.TIME_PERIOD, timePeriod);
        jsonObject.addProperty(FieldValueConstants.ORDER_NO, orderNo);
        jsonObject.addProperty(FieldValueConstants.TYPE, type);

        return jsonObject;
    }

	
}
