package com.elitecore.corenetvertex.pd.calender;

import java.io.Serializable;
import java.sql.Timestamp;

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
import com.google.gson.annotations.SerializedName;

@Entity(name="com.elitecore.corenetvertex.pd.calender.CalenderDetails")
@Table(name = "TBLM_CALENDER_DETAILS")

public class CalenderDetails implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    private String id;
    @SerializedName(FieldValueConstants.CALENDER_NAME)private String calenderName;
    @SerializedName(FieldValueConstants.FROM_DATE)private Timestamp fromDate;
    @SerializedName(FieldValueConstants.TO_DATE)private Timestamp toDate;
    
    private transient  CalenderData calenderData;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name =  "CALENDER_ID")
    @JsonIgnore
    public CalenderData getCalenderData() {
		return calenderData;
	}

	public void setCalenderData(CalenderData calenderData) {
		this.calenderData = calenderData;
	}

	@Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	@Column(name="CALENDER_NAME")
	public String getCalenderName() {
		return calenderName;
	}

	public void setCalenderName(String calenderName) {
		this.calenderName = calenderName;
	}

	@Column(name="FROM_DATE")
	public Timestamp getFromDate() {
		return fromDate;
	}

	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}
	@Column(name="TO_DATE")
	public Timestamp getToDate() {
		return toDate;
	}

	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}

	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.CALENDER_NAME, calenderName);
        jsonObject.addProperty(FieldValueConstants.FROM_DATE, fromDate.toString());
        jsonObject.addProperty(FieldValueConstants.TO_DATE, toDate.toString());

        return jsonObject;
	}

	public Object copyModel(){
    	CalenderDetails calenderDetailsCopy = new CalenderDetails();
    	calenderDetailsCopy.setId(null);
    	calenderDetailsCopy.setCalenderName(this.calenderName);
    	calenderDetailsCopy.setFromDate(this.fromDate);
    	calenderDetailsCopy.setToDate(this.toDate);
    	calenderDetailsCopy.setCalenderData(null);
    	return calenderDetailsCopy;
	}


}
