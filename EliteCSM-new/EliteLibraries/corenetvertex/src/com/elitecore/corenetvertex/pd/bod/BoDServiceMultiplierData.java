package com.elitecore.corenetvertex.pd.bod;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.google.gson.JsonObject;
import org.apache.commons.lang.SystemUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * Used to configure service wise multiplier for Top-Up Package
 * @author ishani.dave
 *
 */
@Entity(name = "com.elitecore.corenetvertex.pd.bod.BoDServiceMultiplierData")
@Table(name = "TBLM_BOD_SERVICE_MULTIPLIER")
public class BoDServiceMultiplierData implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private DataServiceTypeData serviceTypeData;
	private Double multiplier;
	@JsonIgnore
	private BoDQosMultiplierData bodQosMultiplierData;
	private static final ToStringStyle SERVICE_MULTIPLIER_DATA_TO_STRING_STYLE = new ServiceMultiplierToString();

	@Id
	@Column(name = "ID")
	@GeneratedValue(generator = "eliteSequenceGenerator")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@ManyToOne(fetch= FetchType.EAGER)
	@JoinColumn(name = "SERVICE_ID")
	@JsonIgnore
	public DataServiceTypeData getServiceTypeData() {
		return serviceTypeData;
	}

	public void setServiceTypeData(DataServiceTypeData serviceTypeData) {
		this.serviceTypeData = serviceTypeData;
	}

	@Transient
	public String getServiceTypeDataId(){
		if(serviceTypeData==null){
			return null;
		}
		return serviceTypeData.getId();
	}

	public void setServiceTypeDataId(String serviceTypeDataId){
		if(serviceTypeData==null){
			serviceTypeData = new DataServiceTypeData();
		}
		serviceTypeData.setId(serviceTypeDataId);
	}

	@Column(name = "MULTIPLER")
	public Double getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(Double multiplier) {
		this.multiplier = multiplier;
	}

	@ManyToOne(fetch= FetchType.EAGER)
	@JoinColumn(name = "BOD_QOS_MULTIPLIER_ID")
	@XmlTransient
	@JsonIgnore
	public BoDQosMultiplierData getBodQosMultiplierData() {
		return bodQosMultiplierData;
	}

	public void setBodQosMultiplierData(BoDQosMultiplierData bodQosMultiplierData) {
		this.bodQosMultiplierData = bodQosMultiplierData;
	}
	
	
	private static final class ServiceMultiplierToString extends ToStringStyle.CustomToStringStyle{
		private static final long serialVersionUID = 1L;
		ServiceMultiplierToString(){
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(4) + getTabs(1));
		}
	}
	
	
	public String toString(ToStringStyle toStringStyle){
		
		ToStringBuilder toStringBuilder = new ToStringBuilder(this, toStringStyle)
		.append(FieldValueConstants.SERVICE_TYPE,serviceTypeData)
		.append(FieldValueConstants.MULTIPLIER,multiplier);
		return toStringBuilder.toString();
	}
	
	@Override
	public String toString() {
		return toString(SERVICE_MULTIPLIER_DATA_TO_STRING_STYLE);
	}

	public JsonObject toJson(){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.SERVICE_TYPE, serviceTypeData.getName());
		jsonObject.addProperty(FieldValueConstants.MULTIPLIER, multiplier);
		return jsonObject;
	}
	
}
