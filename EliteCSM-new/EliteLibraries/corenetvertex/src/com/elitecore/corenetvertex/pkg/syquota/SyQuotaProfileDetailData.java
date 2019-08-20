package com.elitecore.corenetvertex.pkg.syquota;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.core.validator.DataServiceTypeValidator;
import org.apache.commons.lang.SystemUtils;

import com.elitecore.corenetvertex.constants.CounterPresence;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.google.gson.JsonObject;

/**
 * Detail data of the sy quota based information
 * @author Dhyani.Raval
 *
 */
@Entity
@Table(name = "TBLM_SY_QUOTA_PROFILE_DETAIL")
public class SyQuotaProfileDetailData implements Serializable, Cloneable {

	private static final ToStringStyle QUOTA_PROFILE_DETAIL_TO_STRING_STYLE = new QuotaProfileDetailDataToString();
	private static final long serialVersionUID = 1L;
	private String id;
	private SyQuotaProfileData syQuotaProfileData;
	private DataServiceTypeData dataServiceTypeData;
	private String counterName;
	private String hsqValue;
	private String fup1Value;
	private String fup2Value;
	private Integer counterPresent = CounterPresence.OPTIONAL_FUP1.getVal();
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(generator = "eliteSequenceGenerator")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "SY_QUOTA_PROFILE_ID")
	@XmlTransient
	public SyQuotaProfileData getSyQuotaProfileData() {
		return syQuotaProfileData;
	}
	public void setSyQuotaProfileData(SyQuotaProfileData syQuotaProfileData) {
		this.syQuotaProfileData = syQuotaProfileData;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="DATA_SERVICE_TYPE_ID")
	@Import(required = true, validatorClass = DataServiceTypeValidator.class)
	@XmlElement(name = "dataServiceType")
	public DataServiceTypeData getDataServiceTypeData() {
		return dataServiceTypeData;
	}
	public void setDataServiceTypeData(DataServiceTypeData dataServiceTypeData) {
		this.dataServiceTypeData = dataServiceTypeData;
	}
	
	@Column(name = "COUNTER_NAME")
	public String getCounterName() {
		return counterName;
	}
	public void setCounterName(String counterName) {
		this.counterName = counterName;
	}
	
	@Column(name ="HSQ_VALUE")
	public String getHsqValue() {
		return hsqValue;
	}
	public void setHsqValue(String hsqValue) {
		this.hsqValue = hsqValue;
	}
	
	@Column(name = "FUP1_VALUE")
	public String getFup1Value() {
		return fup1Value;
	}
	public void setFup1Value(String fup1Value) {
		this.fup1Value = fup1Value;
	}
	
	@Column(name = "FUP2_VALUE")
	public String getFup2Value() {
		return fup2Value;
	}
	public void setFup2Value(String fup2Value) {
		this.fup2Value = fup2Value;
	}
	
	@Column(name = "COUNTER_PRESENT")
	public Integer getCounterPresent() {
		return counterPresent;
	}
	public void setCounterPresent(Integer counterPresent) {
		if(counterPresent != null && CounterPresence.fromValue(counterPresent) != null){
			this.counterPresent = counterPresent;
		}
	}
	
	public String toString() {
		
		return new ToStringBuilder(this, QUOTA_PROFILE_DETAIL_TO_STRING_STYLE)
			.append("Service Name", dataServiceTypeData.getName())
			.append("Counter Name", counterName)
			.append("Hsq Value", hsqValue)
			.append("FUP1 Value", fup1Value)
			.append("FUP2 Value", fup2Value)
			.append("Counter presence",CounterPresence.fromValue(counterPresent).getDisplayVal())
			.toString();
	}

    public SyQuotaProfileDetailData copyModel() {
		SyQuotaProfileDetailData newData = new SyQuotaProfileDetailData();
		newData.counterName = this.counterName;
		newData.counterPresent = this.counterPresent;
		newData.dataServiceTypeData = this.dataServiceTypeData;
		newData.fup1Value = this.fup1Value;
		newData.fup2Value = this.fup2Value;
		newData.hsqValue = this.hsqValue;
		return  newData;
    }

    private static final class QuotaProfileDetailDataToString extends ToStringStyle.CustomToStringStyle {
		
		private static final long serialVersionUID = 1L;

		QuotaProfileDetailDataToString() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(6) + getTabs(2));
		}
	}
	
	public JsonObject toJson(){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.COUNTER_NAME, counterName);
		jsonObject.addProperty(FieldValueConstants.HSQ_VALUE, hsqValue);
		jsonObject.addProperty(FieldValueConstants.FUP1_VALUE, fup1Value);
		jsonObject.addProperty(FieldValueConstants.FUP2_VALUE, fup2Value);
		jsonObject.addProperty(FieldValueConstants.COUNTER_PRESENCE, CounterPresence.fromValue(counterPresent).getDisplayVal());
		JsonObject serviceTypeJson = new JsonObject();
		serviceTypeJson.add(dataServiceTypeData.getName(), jsonObject);
		return serviceTypeJson;
	}

	public SyQuotaProfileDetailData deepClone() throws CloneNotSupportedException {
		SyQuotaProfileDetailData newData = (SyQuotaProfileDetailData) this.clone();
		newData.dataServiceTypeData = dataServiceTypeData == null ? null : dataServiceTypeData.deepClone();
		return newData;
	}
}
