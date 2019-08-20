package com.elitecore.corenetvertex.sm.gateway;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleType;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@Entity(name="com.elitecore.corenetvertex.sm.gateway.PCCRuleMappingData")
@Table(name = "TBLM_PCCRULE_MAPPING")
public class PCCRuleMappingData extends DefaultGroupResourceData implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String commProtocol;
	private AttributeMappingData staticAttributeMappings;
	private AttributeMappingData dynamicAttributeMappings;
	private transient List<RadiusGwProfilePCCRuleMappingData> radiusGatewayProfilePccRuleMappings;
	private transient List<DiameterGwProfilePCCRuleMappingData> diameterGatewayProfilePccMappings;
	private transient List<AttributeMappingData> attributeMappingDatas;

	public PCCRuleMappingData(){
		this.radiusGatewayProfilePccRuleMappings = Collectionz.newArrayList();
		this.diameterGatewayProfilePccMappings = Collectionz.newArrayList();
		this.attributeMappingDatas = Collectionz.newArrayList();
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

	@Transient
	public AttributeMappingData getStaticAttributeMappings() {
		if(Collectionz.isNullOrEmpty(getAttributeMappingDatas()) == false){
			getAttributeMappingDatas().forEach(staticMapping -> {
				if(PCCRuleType.STATIC.name().equalsIgnoreCase(staticMapping.getType())){
					staticAttributeMappings = staticMapping;
				}
			});
		}
		return staticAttributeMappings;
	}

	public void setStaticAttributeMappings(AttributeMappingData staticAttributeMappings) {
		this.staticAttributeMappings = staticAttributeMappings;
	}

	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}


	@Override
	@Transient
	public String getResourceName() {
		return getName();
	}

	@Override
	public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME, name);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
        jsonObject.addProperty(FieldValueConstants.COMMUNICATION_PROTOCOL, commProtocol );
        if(staticAttributeMappings != null){
            jsonObject.add("Static Attribute Mappings", staticAttributeMappings.toJson());

        }
        if(dynamicAttributeMappings != null){
            jsonObject.add("Dynamic Attribute Mappings", dynamicAttributeMappings.toJson());

        }
        return jsonObject;
	}

	@Column(name="comm_protocol")
	public String getCommProtocol() {
		return commProtocol;
	}

	public void setCommProtocol(String commProtocol) {
		this.commProtocol = commProtocol;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "pccRuleMappingData", orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	@JsonIgnore
	public List<RadiusGwProfilePCCRuleMappingData> getRadiusGatewayProfilePccRuleMappings() {
		return radiusGatewayProfilePccRuleMappings;
	}

	public void setRadiusGatewayProfilePccRuleMappings(List<RadiusGwProfilePCCRuleMappingData> radiusGatewayProfilePccRuleMappings) {
		this.radiusGatewayProfilePccRuleMappings = radiusGatewayProfilePccRuleMappings;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "pccRuleMappingData", orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	@JsonIgnore
	public List<DiameterGwProfilePCCRuleMappingData> getDiameterGatewayProfilePccMappings() {
		return diameterGatewayProfilePccMappings;
	}

	public void setDiameterGatewayProfilePccMappings(List<DiameterGwProfilePCCRuleMappingData> diameterGatewayProfilePccMappings) {
		this.diameterGatewayProfilePccMappings = diameterGatewayProfilePccMappings;
	}

	@Transient
	public AttributeMappingData getDynamicAttributeMappings() {
		if(Collectionz.isNullOrEmpty(getAttributeMappingDatas()) == false){
			getAttributeMappingDatas().forEach(staticMapping -> {
				if(PCCRuleType.DYNAMIC.name().equalsIgnoreCase(staticMapping.getType())){
					dynamicAttributeMappings = staticMapping;
				}
			});
		}
		return dynamicAttributeMappings;
	}

	public void setDynamicAttributeMappings(AttributeMappingData dynamicAttributeMappings) {
		this.dynamicAttributeMappings = dynamicAttributeMappings;
	}

	@OneToMany(cascade={CascadeType.ALL},fetch = FetchType.LAZY, mappedBy = "pccRuleMappingData",orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	@JsonIgnore
	public List<AttributeMappingData> getAttributeMappingDatas() {
		return attributeMappingDatas;
	}

	public void setAttributeMappingDatas(List<AttributeMappingData> attributeMappingDatas) {
		if(Collectionz.isNullOrEmpty(attributeMappingDatas) == false){
			attributeMappingDatas.forEach(mapping -> {
				if(PCCRuleType.DYNAMIC.name().equalsIgnoreCase(mapping.getType())){
					setDynamicAttributeMappings(mapping);
				}else if (PCCRuleType.STATIC.name().equalsIgnoreCase(mapping.getType())){
					setStaticAttributeMappings(mapping);
				}
			});
		}
		this.attributeMappingDatas = attributeMappingDatas;
	}
}
