package com.elitecore.corenetvertex.pkg.syquota;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.core.validator.SyQuotaProfileDetailValidator;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.SystemUtils;
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
 * Data class for the Sy Related Quota
 * @author Dhyani.Raval
 *
 */
@Entity
@Table(name = "TBLM_SY_QUOTA_PROFILE")
public class SyQuotaProfileData extends ResourceData implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private static final ToStringStyle QUOTA_PROFILE_DATA_TO_STRING_STYLE = new QuotaProfileDataToString();
	@SerializedName(FieldValueConstants.NAME)private String name;
	private String description;
	private PkgData pkgData;
	private List<SyQuotaProfileDetailData> syQuotaProfileDetailDatas;
	private List<QosProfileData> qosProfileDatas;
	
	public SyQuotaProfileData(){
		syQuotaProfileDetailDatas = Collectionz.newArrayList();
		qosProfileDatas = Collectionz.newArrayList();
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
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="PACKAGE_ID")
	@XmlTransient
	public PkgData getPkgData() {
		return pkgData;
	}
	public void setPkgData(PkgData pkgData) {
		this.pkgData = pkgData;
	}
	
	@OneToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY,mappedBy="syQuotaProfileData",orphanRemoval=true)
	@Fetch(FetchMode.SUBSELECT)
	@Import(required = true, validatorClass =SyQuotaProfileDetailValidator.class)
	public List<SyQuotaProfileDetailData> getSyQuotaProfileDetailDatas() {
		return syQuotaProfileDetailDatas;
	}
	public void setSyQuotaProfileDetailDatas(
			List<SyQuotaProfileDetailData> syQuotaProfileDetailDatas) {
		this.syQuotaProfileDetailDatas = syQuotaProfileDetailDatas;
	}

	@OneToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY,mappedBy="syQuotaProfileData",orphanRemoval=true)
	@Fetch(FetchMode.SUBSELECT)
	@XmlTransient
	public List<QosProfileData> getQosProfileDatas() {
		return qosProfileDatas;
	}

	public void setQosProfileDatas(List<QosProfileData> qosProfileDatas) {
		this.qosProfileDatas = qosProfileDatas;
	}
	
	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	public void setStatus(String status) {
		super.setStatus(status);
	}
	
	@Transient
	@Override
	public String getAuditableId() {
		if(pkgData != null){
			return pkgData.getId();
		}
		return getId();
	}
	
	@Transient
	@Override
	public ResourceData getAuditableResource() {
		return pkgData;
	}

	@Transient
	public String getHierarchy() {
		if(pkgData!=null){
			return pkgData.getHierarchy() +"<br>"+ getId() + "<br>"+ name;
		}
		return new String();
	}

	@Override
	public String toString() {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this, QUOTA_PROFILE_DATA_TO_STRING_STYLE).append("Name", name);

		toStringBuilder.append("");
		if (Collectionz.isNullOrEmpty(syQuotaProfileDetailDatas) == false) {

			for (SyQuotaProfileDetailData quotaProfileDetailData : syQuotaProfileDetailDatas) {
				toStringBuilder.append("Quota Profile Detail Data", quotaProfileDetailData);
			}
		} else {

			toStringBuilder.append("No quota profile detail(s) found");
		}

		return toStringBuilder.toString();
	}

	private static final class QuotaProfileDataToString extends ToStringStyle.CustomToStringStyle {

		private static final long serialVersionUID = 1L;

		QuotaProfileDataToString() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(4) + getTabs(1));
		}
	}
	
	public JsonObject toJson(){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.NAME, name);
		jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
		
		
		if(syQuotaProfileDetailDatas != null){
			JsonArray jsonArray = new JsonArray();
			for(SyQuotaProfileDetailData syQuotaProfileDetailData : syQuotaProfileDetailDatas){
				jsonArray.add(syQuotaProfileDetailData.toJson());
			}
			jsonObject.add(FieldValueConstants.SY_QUOTA_PROFILE_DETAIL, jsonArray);
		}
		return jsonObject;
	}
	
	public SyQuotaProfileData deepClone() throws CloneNotSupportedException {
		SyQuotaProfileData newData = (SyQuotaProfileData) this.clone();
		newData.syQuotaProfileDetailDatas = Collectionz.newArrayList();
		
		if (syQuotaProfileDetailDatas.isEmpty() == false) {
			for (SyQuotaProfileDetailData syBasedQuotaProfileData : syQuotaProfileDetailDatas) {
				SyQuotaProfileDetailData clonedSyQuotaProfileData = syBasedQuotaProfileData.deepClone();
				clonedSyQuotaProfileData.setSyQuotaProfileData(newData);
				newData.syQuotaProfileDetailDatas.add(clonedSyQuotaProfileData);
			}
		}
		
		return newData;
	}

	@Override
	@Transient
	public String getGroups() {
		return pkgData.getGroups();
	}

	@Override
	@Transient
	public String getResourceName() {
		return getName();
	}

	public SyQuotaProfileData copyModel(PkgData pkgData) {
		SyQuotaProfileData newData = new SyQuotaProfileData();

		newData.description = this.description;
		newData.name = this.name;

		List<SyQuotaProfileDetailData> syQuotaProfileDetailDatas = Collectionz.newArrayList();
		for (SyQuotaProfileDetailData syQuotaProfileDetailData : this.syQuotaProfileDetailDatas) {
			SyQuotaProfileDetailData syQuotaProfileDetailDataCopy = syQuotaProfileDetailData.copyModel();
			syQuotaProfileDetailDataCopy.setId(null);
			syQuotaProfileDetailDataCopy.setSyQuotaProfileData(newData);
			syQuotaProfileDetailDatas.add(syQuotaProfileDetailDataCopy);

		}
		newData.setSyQuotaProfileDetailDatas(syQuotaProfileDetailDatas);
		newData.setCreatedDateAndStaff(pkgData.getCreatedByStaff());

		return newData;
	}
}
