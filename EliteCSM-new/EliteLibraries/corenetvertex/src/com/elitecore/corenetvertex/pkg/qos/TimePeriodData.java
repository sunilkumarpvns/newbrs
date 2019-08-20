package com.elitecore.corenetvertex.pkg.qos;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;


@Entity
@Table(name="TBLM_TIME_PERIOD")
public class TimePeriodData implements Serializable,Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final ToStringStyle TIME_PERIOD_DATA_TO_STRING_STYLE = new TimePeriodDataToString();

	transient private String id;
	@SerializedName(FieldValueConstants.MOY)private String moy;
	@SerializedName(FieldValueConstants.DOM)private String dom;
	@SerializedName(FieldValueConstants.DOW)private String dow;
	@SerializedName(FieldValueConstants.TIMEPERIOD)private String timePeriod;
	transient private QosProfileData qosProfile;
	
	@Id
	@Column(name="ID")
	@GeneratedValue(generator="eliteSequenceGenerator")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Column(name="MOY")
	public String getMoy() {
		return moy;
	}
	public void setMoy(String moy) {
		this.moy = moy;
	}
	
	@Column(name="DOM")
	public String getDom() {
		return dom;
	}
	public void setDom(String dom) {
		this.dom = dom;
	}
	
	@Column(name="DOW") 
	public String getDow() {
		return dow;
	}
	public void setDow(String dow) {
		this.dow = dow;
	}
	
	@Column(name="TIME_PERIOD") 
	public String getTimePeriod() {
		return timePeriod;
	}
	public void setTimePeriod(String timePeriod) {
		this.timePeriod = timePeriod;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "QOS_PROFILE_ID")
	@XmlTransient
	public QosProfileData getQosProfile() {
		return qosProfile;
	}
	public void setQosProfile(QosProfileData qosProfile) {
		this.qosProfile = qosProfile;
	}
	
	@Override
	public String toString() {

		return toString(TIME_PERIOD_DATA_TO_STRING_STYLE);
	}
	
	public String toString(ToStringStyle toStringStyle) {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this, toStringStyle)
		.append("MoY", moy)
		.append("DoM", dom)
		.append("DoW", dow)
		.append("Timeperiod", timePeriod);
		

		return toStringBuilder.toString();

	}

	public TimePeriodData copyModel() {
		TimePeriodData newData = new TimePeriodData();
		newData.moy = this.moy;
		newData.dom = this.dom;
		newData.dow = this.dow;
		newData.timePeriod = this.timePeriod;
		return newData;
	}

	private static final class TimePeriodDataToString extends ToStringStyle.CustomToStringStyle {

		private static final long serialVersionUID = 1L;

		TimePeriodDataToString() {
			super();
			this.setContentStart(getSpaces(2) + getTabs(1));
			this.setFieldSeparatorAtStart(false);
			this.setFieldSeparator(getSpaces(3));
			this.setContentEnd("");
		}
	}
	
	public JsonObject toJson(){
		Gson gson =  new GsonBuilder().serializeNulls().create();
		return gson.toJsonTree(this).getAsJsonObject();
	}
	
	public TimePeriodData deepClone() throws CloneNotSupportedException {
		TimePeriodData newData = (TimePeriodData) this.clone();
		newData.qosProfile = qosProfile;
		return newData;
	}
}
