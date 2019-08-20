package com.elitecore.corenetvertex.pkg.ims;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.core.validator.IMSPkgServiceDataValidator;
import com.elitecore.corenetvertex.core.validator.IMSPkgValidator;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.pkg.importimspkg.IMSPkgImportOperation;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.DoubleToStringAdapter;
import com.google.gson.annotations.SerializedName;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.google.gson.JsonObject;

/**
 * Represents multi-media package entity
 * 
 * @author Dhayni.Raval
 *
 */

@Entity
@Table(name="TBLM_IMS_PACKAGE")
@XmlRootElement(name="imsPkgData")
@Import(validatorClass = IMSPkgValidator.class, importClass = IMSPkgImportOperation.class,required = true)
public class IMSPkgData extends ResourceData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SerializedName(FieldValueConstants.NAME)private String name;
	@SerializedName(FieldValueConstants.DESCRIPTION)private String description;
	@SerializedName(FieldValueConstants.TYPE)private String type = PkgType.BASE.name();
	@SerializedName(FieldValueConstants.MODE)private String packageMode;
	@XmlJavaTypeAdapter(value = DoubleToStringAdapter.class) private Double price;
	private transient List<IMSPkgServiceData> imsPkgServiceDatas;
	private PolicyStatus policyStatus = PolicyStatus.SUCCESS;
	private String failReason;
	private String partialFailReason;
	
	public IMSPkgData(){
		this.imsPkgServiceDatas = Collectionz.newArrayList();
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
	
	@Override
	@Column(name = "STATUS")
	@XmlElement
	public String getStatus() {
		return super.getStatus();
	}

	@Override
	public void setStatus(String status) {
		super.setStatus(status);
	}

	@Column(name = "TYPE")
	@XmlTransient
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name = "PACKAGE_MODE")
	public String getPackageMode() {
		return packageMode;
	}

	public void setPackageMode(String packageMode) {
		this.packageMode = packageMode;
	}


	@Column(name = "PRICE")
	@XmlTransient
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Override
	@Column(name="GROUPS")
	public String getGroups() {
		return super.getGroups();
	}

	@Override
	public void setGroups(String groups) {
		super.setGroups(groups);
	}

	@Transient
	@XmlTransient
	public String getPartialFailReason() {
		return partialFailReason;
	}

	public void setPartialFailReason(String partialFailReason) {
		this.partialFailReason = partialFailReason;
	}

	@Transient
	@XmlTransient
	public String getFailReason() {
		return failReason;
	}

	@Transient
	@XmlTransient
	public PolicyStatus getPolicyStatus() {
		return policyStatus;
	}

	public void setPolicyStatus(PolicyStatus policyStatus) {
		this.policyStatus = policyStatus;
	}

	@Transient
	public void copy(IMSPkgData previousPkgData) {
		this.name = previousPkgData.name;
		this.description = previousPkgData.description;
		this.type = previousPkgData.type;
		this.packageMode = previousPkgData.packageMode;
		this.price = previousPkgData.price;
		this.imsPkgServiceDatas = previousPkgData.imsPkgServiceDatas;
		this.policyStatus = previousPkgData.policyStatus;
	}

	public void setFailReasons(String failReasons) {
		this.failReason = failReasons;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "imsPkgData", orphanRemoval = true)	
	@Fetch(FetchMode.SUBSELECT)
	@Where(clause="STATUS != 'DELETED'")
	@OrderBy(value="CREATED_DATE")
	@XmlElementWrapper(name="imsPkgServiceDatas")
	@XmlElement(name="imsPkgServiceData")
	@Import(required = true, validatorClass = IMSPkgServiceDataValidator.class)
	public List<IMSPkgServiceData> getImsPkgServiceDatas() {
		return imsPkgServiceDatas;
	}

	public void setImsPkgServiceDatas(List<IMSPkgServiceData> imsPkgServiceDatas) {
		this.imsPkgServiceDatas = imsPkgServiceDatas;
	}
	
	@Override
	public String toString() {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this,
				ToStringStyle.CUSTOM_TO_STRING_STYLE).append("Name", name)
				.append("Type", type)
				.append("Package Mode", packageMode)
				.append("Price", price);


		toStringBuilder.append("\t");
		if (Collectionz.isNullOrEmpty(imsPkgServiceDatas) == false) {
			for (IMSPkgServiceData imsPkgServiceData : this.imsPkgServiceDatas) {
				toStringBuilder.append("Service", imsPkgServiceData.toString());
			}
		}

		toStringBuilder.append("Policy Status", policyStatus);
		if (failReason != null) {
			toStringBuilder.append("Fail Reasons", failReason);
		}
		if (partialFailReason != null) {
			toStringBuilder.append("Partial Fail Reasons", partialFailReason);
		}

		return toStringBuilder.toString();

	}
	
	@Transient
	@Override
	public String getHierarchy() {
		return getId() + "<br>" + name;
	}

	@Override
	public JsonObject toJson(){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.NAME, name);
		jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
		jsonObject.addProperty(FieldValueConstants.TYPE, type);
		jsonObject.addProperty(FieldValueConstants.MODE, packageMode);
		jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
		jsonObject.addProperty(FieldValueConstants.PRICE, price);
		return jsonObject;
		
	}

	public void setDeletedStatus(){
		setStatus(CommonConstants.STATUS_DELETED);
		for(IMSPkgServiceData imsPkgServiceData:imsPkgServiceDatas){
			imsPkgServiceData.setStatus(CommonConstants.STATUS_DELETED);
		}
	}

	@Override
	@Transient
	public String getResourceName() {
		return getName();
	}
}
