package com.elitecore.corenetvertex.pkg.ims;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.SystemUtils;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pkg.constants.PCCAttribute;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.google.gson.JsonObject;

/**
 * 
 * @author Dhyani.Raval
 *
 */
@Entity
@Table(name = "TBLM_IMS_PACKAGE_PCC_ATTRIBUTE")
public class IMSPkgPCCAttributeData implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final ToStringStyle IMS_PKG_PCC_ATTR_SERVICE_STYLE = new IMSPkgPCCAttributeDataToString();
	private String id;
	private PCCAttribute attribute;
	private String expression;
	private PCCRuleAttributeAction action;
	private String value;
	private IMSPkgServiceData imsPkgServiceData;
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(generator = "eliteSequenceGenerator")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name = "ATTRIBUTE")
	public PCCAttribute getAttribute() {
		return attribute;
	}
	public void setAttribute(PCCAttribute attribute) {
		this.attribute = attribute;
	}
	
	@Column(name = "EXPRESSION")
	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name = "ACTION")
	public PCCRuleAttributeAction getAction() {
		return action;
	}
	public void setAction(PCCRuleAttributeAction action) {
		this.action = action;
	}
	
	@Column(name = "VALUE")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="IMS_PACKAGE_SERVICE_ID")
	@XmlTransient
	public IMSPkgServiceData getImsPkgServiceData() {
		return imsPkgServiceData;
	}
	public void setImsPkgServiceData(IMSPkgServiceData imsPkgServiceData) {
		this.imsPkgServiceData = imsPkgServiceData;
	}
	
	public String toString(ToStringStyle toStringStyle) {
		ToStringBuilder toStringBuilder = new ToStringBuilder(this, toStringStyle)
		.append("Attribute", attribute)
		.append("Expression", expression)
		.append("Action", action.val)
		.append("Value", value);

		return toStringBuilder.toString();

	}
	
	public String toString() {
		return toString(IMS_PKG_PCC_ATTR_SERVICE_STYLE);
	}
	
	private static final class IMSPkgPCCAttributeDataToString extends ToStringStyle.CustomToStringStyle {

		private static final long serialVersionUID = 1L;

		IMSPkgPCCAttributeDataToString() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(4) + getTabs(2));
		}
	}
	
	public JsonObject toJson(){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.ATTRIBUTE, attribute.displayValue);
		jsonObject.addProperty(FieldValueConstants.EXPRESSION, expression);
		jsonObject.addProperty(FieldValueConstants.ACTION, action.val);
		jsonObject.addProperty(FieldValueConstants.VALUE, value);
		return jsonObject;
	}
	
	
}
