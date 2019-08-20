package com.elitecore.corenetvertex.pd.bod;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.google.gson.JsonObject;
import org.apache.commons.lang.SystemUtils;
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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;

/**
 * Used to configure IPCAN QoS for BoD package
 * @author Ishani.Dave
 *
 */
@Entity(name = "com.elitecore.corenetvertex.pd.bod.BoDQosMultiplierData")
@Table(name = "TBLM_BOD_QOS_MULTIPLIER")
public class BoDQosMultiplierData extends ResourceData implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer fupLevel;
	private Double multiplier = 1.00;
	@JsonIgnore
	private BoDData bodData;
	private List<BoDServiceMultiplierData> bodServiceMultiplierDatas;
	private String bodPackageId;
	private static final ToStringStyle QOS_MULTIPLIER_DATA_TO_STRING_STYLE = new QosMultiplierToString();

	
	public BoDQosMultiplierData(){
		bodServiceMultiplierDatas = Collectionz.newArrayList();
	}
	
	@Column(name = "FUP_LEVEL")
	public Integer getFupLevel() {
		return fupLevel;
	}
	public void setFupLevel(Integer fupLevel) {
		this.fupLevel = fupLevel;
	}
	
	@Column(name = "MULTIPLIER")
	public Double getMultiplier() {
		return multiplier;
	}
	public void setMultiplier(Double multiplier) {
		this.multiplier = multiplier;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BOD_PACKAGE_ID")
	@JsonIgnore
	@XmlTransient
	public BoDData getBodData() {
		return bodData;
	}
	public void setBodData(BoDData bodData) {
		if (bodData != null) {
			setBodPackageId(bodData.getId());
		}
		this.bodData = bodData;
	}

	@Override
	@Column(name = "STATUS")
	@JsonIgnore
	public String getStatus() {
		return super.getStatus();
	}

	@Override
	@Transient
	@JsonIgnore
	public String getGroups() {
		return bodData.getGroups();
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "bodQosMultiplierData", orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	public List<BoDServiceMultiplierData> getBodServiceMultiplierDatas() {
		bodServiceMultiplierDatas.sort((mul1,mul2)->{
			if(mul1==null || mul2==null){
				return 0;
			}

			DataServiceTypeData mul1ServiceData =mul1.getServiceTypeData();
			if(mul1ServiceData==null || mul1ServiceData.getName()==null){
				return 0;
			}

			DataServiceTypeData mul2ServiceData =mul2.getServiceTypeData();
			if(mul2ServiceData==null || mul2ServiceData.getName()==null){
				return 0;
			}

			return mul1ServiceData.getName().compareTo(mul2ServiceData.getName());
		});
		return bodServiceMultiplierDatas;
	}
	public void setBodServiceMultiplierDatas(
			List<BoDServiceMultiplierData> bodServiceMultiplierDatas) {
		this.bodServiceMultiplierDatas = bodServiceMultiplierDatas;
	}

	@Transient
	public String getBodPackageId() {
		return bodPackageId;
	}

	public void setBodPackageId(String bodPackageId) {
		this.bodPackageId = bodPackageId;
	}

	private static final class QosMultiplierToString extends ToStringStyle.CustomToStringStyle{

		private static final long serialVersionUID = 1L;
		
		QosMultiplierToString(){
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(4) + getTabs(1));
		}
	}
	
	public String toString(ToStringStyle toStringStyle){
		
		ToStringBuilder toStringBuilder = new ToStringBuilder(this, toStringStyle)
		.append(FieldValueConstants.FUP_LEVEL,fupLevel)
		.append(FieldValueConstants.QOS_MULTIPLIER,multiplier);
		return toStringBuilder.toString();
	}
	
	@Override
	public String toString() {
		return toString(QOS_MULTIPLIER_DATA_TO_STRING_STYLE);
	}
	
	@Override
	public JsonObject toJson(){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.QOS_MULTIPLIER, multiplier);
		if(bodServiceMultiplierDatas != null){
			JsonObject serviceMultiplierJson = new JsonObject();
			for(BoDServiceMultiplierData boDServiceMultiplierData : this.bodServiceMultiplierDatas){
					serviceMultiplierJson.addProperty(boDServiceMultiplierData.getServiceTypeData().getName(),boDServiceMultiplierData.getMultiplier());
			}
			jsonObject.add(FieldValueConstants.SERVICE_MULTIPLIER_DATA, serviceMultiplierJson);
		}
		JsonObject fupObject = new JsonObject();
		fupObject.add((fupLevel > 0 ? ("FUP Level-" + fupLevel) : "HSQ") + " IPCAN Multiplier", jsonObject);
		return fupObject;
	}

	@Override
	@Transient
	public String getResourceName() {
		return "BoD QoS Multiplier";
	}
}
