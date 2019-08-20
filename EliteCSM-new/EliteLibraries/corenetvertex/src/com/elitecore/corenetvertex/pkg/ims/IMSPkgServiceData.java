package com.elitecore.corenetvertex.pkg.ims;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;


import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.core.validator.IMSPkgPCCAttributeValidator;
import com.elitecore.corenetvertex.core.validator.MediaTypeValidator;
import com.elitecore.commons.base.Strings;

import org.apache.commons.lang.SystemUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.IMSServiceAction;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * 
 * @author Dhyani.Raval
 *
 */
@Entity
@Table(name = "TBLM_IMS_PACKAGE_SERVICE")
public class IMSPkgServiceData extends ResourceData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final ToStringStyle IMS_PKG_SERVICE_STYLE = new IMSPkgServiceDataToString();
	private String name;
	private MediaTypeData mediaTypeData;
	private String mediaTypeId;
	private String afApplicationId;
	private String expression;
	private IMSServiceAction action;
	private IMSPkgData imsPkgData;
	private List<IMSPkgPCCAttributeData> imsPkgPCCAttributeDatas;
	
	public IMSPkgServiceData(){
		imsPkgPCCAttributeDatas = Collectionz.newArrayList();
	}
	

	@Column(name = "NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="MEDIA_TYPE_ID")
	@Import(required = true, validatorClass = MediaTypeValidator.class)
	public MediaTypeData getMediaTypeData() {
		return mediaTypeData;
	}

	public void setMediaTypeData(MediaTypeData mediaTypeData) {
		this.mediaTypeData = mediaTypeData;
	}
	
	
	@Column(name = "APP_FUNCTION_ID")
	public String getAfApplicationId() {
		return afApplicationId;
	}


	public void setAfApplicationId(String afApplicationId) {
		this.afApplicationId = afApplicationId;
	}
	
	@Column(name = "EXPRESSION")
	public String getExpression() {
		return expression;
	}
	


	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Column(name = "ACTION")
	public IMSServiceAction getAction() {
		return action;
	}
	public void setAction(IMSServiceAction action) {
		this.action = action;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="IMS_PACKAGE_ID")
	@XmlTransient
	public IMSPkgData getImsPkgData() {
		return imsPkgData;
	}
	public void setImsPkgData(IMSPkgData imsPkgData) {
		this.imsPkgData = imsPkgData;
	}
	
	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	public void setStatus(String status) {
		super.setStatus(status);
	}

	@OneToMany(cascade={CascadeType.ALL},fetch = FetchType.LAZY, mappedBy = "imsPkgServiceData",orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	@XmlElementWrapper(name="imsPkgPCCAttributes")
	@XmlElement(name="imsPkgPCCAttribute")
	@Import(required = true, validatorClass = IMSPkgPCCAttributeValidator.class)
	public List<IMSPkgPCCAttributeData> getImsPkgPCCAttributeDatas() {
		return imsPkgPCCAttributeDatas;
	}

	public void setImsPkgPCCAttributeDatas(
			List<IMSPkgPCCAttributeData> imsPkgPCCAttributeDatas) {
		this.imsPkgPCCAttributeDatas = imsPkgPCCAttributeDatas;
	}

	@Transient
	@XmlTransient
	public String getMediaTypeId() {
		return mediaTypeId;
	}

	public void setMediaTypeId(String mediaTypeId) {
		this.mediaTypeId = mediaTypeId;
	}
	
	public String toString(ToStringStyle toStringStyle) {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this, toStringStyle)
		.append("Name", name);
		if(this.mediaTypeData != null) {
			toStringBuilder.append("Media", this.mediaTypeData.getName())
							.append("Media identifier", this.mediaTypeData.getMediaIdentifier());
		}
		toStringBuilder.append("AF app id", afApplicationId);
		if(Strings.isNullOrBlank(expression) == false) {
			toStringBuilder.append("Additional condition", expression);
		}
		toStringBuilder.append("Action", action);

		toStringBuilder.append("\t");
		if (Collectionz.isNullOrEmpty(imsPkgPCCAttributeDatas) == false) {
			for (IMSPkgPCCAttributeData imsPkgPCCAttributeData : imsPkgPCCAttributeDatas) {
				toStringBuilder.append("PCC attributes", imsPkgPCCAttributeData.toString());
			}
		} else {

			toStringBuilder.append("No pcc attribute found");
		}

		return toStringBuilder.toString();

	}
	
	@Override
	public String toString() {
		return toString(IMS_PKG_SERVICE_STYLE);
	}
	
	private static final class IMSPkgServiceDataToString extends ToStringStyle.CustomToStringStyle {

		private static final long serialVersionUID = 1L;

		IMSPkgServiceDataToString() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(4) + getTabs(1));
		}
	}
	@Override
	@Transient
	public String getAuditableId() {
		if(imsPkgData != null){
			return imsPkgData.getId();
		}
		return getId();
	}

	@Transient
	public String getHierarchy() {
		if(imsPkgData != null){
			return imsPkgData.getHierarchy() +"<br>"+ getId() + "<br>"+ name;
		}
		return new String();
	}
	
	@Transient
	@Override
	public ResourceData getAuditableResource() {
		return imsPkgData;
	}
	
	public JsonObject toJson(){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.NAME, name);
		jsonObject.addProperty(FieldValueConstants.MEDIA_TYPE, mediaTypeData.getName());
		jsonObject.addProperty(FieldValueConstants.EXPRESSION, expression);
		jsonObject.addProperty(FieldValueConstants.AF_APPLICATION_ID, afApplicationId);
		jsonObject.addProperty(FieldValueConstants.ACTION, action.getName());

		if(imsPkgPCCAttributeDatas != null){
			JsonArray jsonArray = new JsonArray();
			for(IMSPkgPCCAttributeData imsPkgPCCAttributeData : imsPkgPCCAttributeDatas){
				jsonArray.add(imsPkgPCCAttributeData.toJson());
			}
			jsonObject.add(FieldValueConstants.IMS_PKG_PCC_ATTR, jsonArray);
		}
		return jsonObject;
	}
	@Override
	@Transient
	public String getResourceName() {
		return getName();
	}
}
