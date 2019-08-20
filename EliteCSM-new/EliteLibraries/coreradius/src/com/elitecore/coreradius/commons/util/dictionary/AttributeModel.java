package com.elitecore.coreradius.commons.util.dictionary;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Equality;
import com.elitecore.coreradius.commons.config.BooleanAdapter;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;
import com.elitecore.coreradius.commons.util.constants.LengthFormat;
import com.elitecore.coreradius.commons.util.constants.PaddingType;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author narendra.pathai
 *
 */
public class AttributeModel{
	private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_NAME = "name";
	private static final String ATTRIBUTE_TYPE = "type";
	private static final String ATTRIBUTE_SUPPORTED_VALUES = "supported-values";
	private static final String ATTRIBUTE_SUPPORTED_VALUE = "value";

	private int id;
	private String name;
	private String type;
	private Set<AttributeSupportedValueModel> supportedValues;
	private Set<AttributeModel> subAttributes;
	private Boolean isTagged;
	private boolean ignoreCase;
	private boolean avPair;
	private int encryptStandard;
	private PaddingType paddingType;
	private LengthFormat lengthFormat;


	/* Transient fields */
	private Map<Integer, AttributeModel> attributeIdToAttributeModel;
	private Map<Long, AttributeSupportedValueModel> attributeIdToSupportedValue;

	/* Each AttributeModel needs to know about parent attribute Model */
	private AttributeModel parentAttributeModel;

	public AttributeModel() {
		supportedValues = new HashSet<AttributeSupportedValueModel>();
		attributeIdToAttributeModel = new HashMap<Integer, AttributeModel>();
		attributeIdToSupportedValue = new HashMap<>();
		subAttributes = new HashSet<AttributeModel>();
		paddingType = PaddingType.NONE;
		lengthFormat = LengthFormat.TLV;
		isTagged = false;
	}

	public AttributeModel(int id, String name, String type, Set<AttributeSupportedValueModel> supportedValues, Set<AttributeModel> subAttributes) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.supportedValues = supportedValues;
		this.subAttributes = subAttributes;
		paddingType = PaddingType.NONE;
		lengthFormat = LengthFormat.TLV;
		isTagged = false;
	}

	public AttributeModel(int id, String name, String type, Set<AttributeSupportedValueModel> supportedValues) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.supportedValues = supportedValues;
		paddingType = PaddingType.NONE;
		lengthFormat = LengthFormat.TLV;
		isTagged = false;
	}

	@XmlElement(name = "attribute")
	public Set<AttributeModel> getSubAttributes() {
		return subAttributes;
	}

	public void setSubAttributes(Set<AttributeModel> subAttributes){ this.subAttributes = subAttributes; }

	@XmlAttribute(name = ATTRIBUTE_NAME)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name = ATTRIBUTE_ID)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@XmlAttribute(name = ATTRIBUTE_TYPE)
	public String getType() {
		return type;
	}

	public void setType(String type){
		this.type = type;
	}

	@XmlElementWrapper(name = ATTRIBUTE_SUPPORTED_VALUES)
	@XmlElement(name = ATTRIBUTE_SUPPORTED_VALUE)
	public Set<AttributeSupportedValueModel> getSupportedValues() {
		return supportedValues;
	}

	@XmlAttribute(name = "has-tag")
	@XmlJavaTypeAdapter(type = Boolean.class, value = BooleanAdapter.class)
	public Boolean isTagged() {
		return isTagged;
	}

	public void setTagged(Boolean isTagged) {
		this.isTagged = isTagged;
	}

	@XmlAttribute(name = "ignore-case")
	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	@XmlAttribute(name = "avpair")
	public boolean isAvPair() {
		return avPair;
	}

	public void setAvPair(boolean avPair) {
		this.avPair = avPair;
	}

	@XmlAttribute(name = "encrypt-standard")
	public int getEncryptStandard() {
		return encryptStandard;
	}

	public void setEncryptStandard(int encryptStandard) {
		this.encryptStandard = encryptStandard;
	}

	@XmlAttribute(name = "padding-type")
	public PaddingType getPaddingType() {
		return paddingType;
	}

	public void setPaddingType(PaddingType paddingType) {
		//can be null in situation when padding type is improper in dictionary
		if(paddingType != null){
			this.paddingType = paddingType;
		}
	}

	@XmlAttribute(name = "length-format")
	public LengthFormat getLengthFormat() {
		return lengthFormat;
	}

	public void setLengthFormat(LengthFormat lengthFormat) {
		//can be null in situation when length format is improper in dictionary
		if(lengthFormat != null){
			this.lengthFormat = lengthFormat;
		}
	}

	public void postRead(){
		postReadForSubAttributes();
		postReadForSupportedValues();
	}

	private void postReadForSupportedValues() {
		for(AttributeSupportedValueModel supportedValue : getSupportedValues()){
			postReadForSupportedValue(supportedValue);
		}
	}

	private void postReadForSupportedValue(AttributeSupportedValueModel supportedValue) {
		supportedValue.setParent(this);
		attributeIdToSupportedValue.put(supportedValue.getId(), supportedValue);
	}

	private void setParentAttributeModelInSubAttribute(AttributeModel attributeModel) {
		attributeModel.setParent(this);
	}

	private void postReadForSubAttributes() {
		for(AttributeModel attributeModel : getSubAttributes()){
			postReadForSubAttribute(attributeModel);
			storeInDataStructure(attributeModel);
		}
	}

	private void storeInDataStructure(AttributeModel attributeModel) {
		attributeIdToAttributeModel.put(attributeModel.getId(), attributeModel);
	}

	private void postReadForSubAttribute(AttributeModel attributeModel) {
		setParentAttributeModelInSubAttribute(attributeModel);
		attributeModel.postRead();
	}

	public AttributeModel getSubAttributeModel(int attributeId){
		return attributeIdToAttributeModel.get(attributeId);
	}

	public AttributeSupportedValueModel getSupportedValueModel(long attributeId){
		return attributeIdToSupportedValue.get(attributeId);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;

		if(!(obj instanceof AttributeModel))
			return false;

		AttributeModel that = (AttributeModel) obj;
		return Equality.areEqual(getId(), that.getId())
				&& Equality.areEqual(getName(), that.getName());
	}

	@Override
	public int hashCode() {
		return (name + type).hashCode();
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		TabbedPrintWriter tabbedPrintWriter = new TabbedPrintWriter(out);
		appendTo(tabbedPrintWriter);
		out.close();
		return stringBuffer.toString();
	}

	protected void appendTo(TabbedPrintWriter out){
		out.println("Attribute Id: " + id + ", Name: " + name);
		out.println("Attribute Name: " + name);
		out.println("Attribute Type: " + type);
		out.println("Has Tag:" + isTagged());
		out.println("AV Pair:" + isAvPair());
		out.println("Ignore Case:" + isIgnoreCase());
		out.println("Length Format:" + getLengthFormat());
		out.println("Padding Type:" + getPaddingType());
		out.println("Encrypt Standard:" + getEncryptStandard());

		if(Collectionz.isNullOrEmpty(subAttributes) == false) {
			out.println("Sub Attributes: ");

			out.incrementIndentation();
			for (AttributeModel attributeModel : subAttributes) {
				attributeModel.appendTo(out);
			}
			out.decrementIndentation();
		}

		if(Collectionz.isNullOrEmpty(supportedValues) == false){
			out.println("Supported Values:");

			out.incrementIndentation();
			for (AttributeSupportedValueModel supportedValue : supportedValues) {
				supportedValue.appendTo(out);
			}
			out.decrementIndentation();
		}

		out.println();
	}

	@XmlTransient
	public AttributeModel getParent() {
		return parentAttributeModel;
	}

	private void setParent(AttributeModel parentAttributeModel){
		this.parentAttributeModel = parentAttributeModel;
	}

	public AttributeIdFormatter newIdFormatter(){
		return new AttributeIdFormatter(this);
	}
}
