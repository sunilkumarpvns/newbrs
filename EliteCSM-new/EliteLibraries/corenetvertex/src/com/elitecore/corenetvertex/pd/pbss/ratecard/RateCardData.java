package com.elitecore.corenetvertex.pd.pbss.ratecard;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pd.pbss.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonObject;
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
 * manage RateCard related information.
 * Created by Saket on 17/12/17.
 */

@Entity(name = "com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardData")
@Table(name = "TBLM_RATECARD")
public class RateCardData extends ResourceData implements Serializable {

	private static final long serialVersionUID = -9173903847327724345L;
	private String name;
	private String description;
	/*private RateFileFormatData rateFileFormatData;*/
	private String rateUom;
	private String pulseUom;
	private String labelKey1;
	private String labelKey2;
	private List<RateCardVersionRelation> rateCardVersionRelation;
	
	private transient RncPackageData rncPackageData;
	
	
/*	@JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RATE_FILE_FORMAT_ID")
	@Fetch(FetchMode.JOIN)
	public RateFileFormatData getRateFileFormatData() {
		return rateFileFormatData;
	}

	public void setRateFileFormatData(RateFileFormatData rateFileFormatData) {
		this.rateFileFormatData = rateFileFormatData;
	}*/

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RNC_PACKAGE_ID")
	@Fetch(FetchMode.JOIN)
	@XmlTransient
	@JsonIgnore
	public RncPackageData getRncPackageData() {
		return rncPackageData;
	}

	public void setRncPackageData(RncPackageData rncPackageData) {
		this.rncPackageData = rncPackageData;
	}
	
	/*@Transient
    public String getRateFileFormatDataId() {
        if(this.getRateFileFormatData()!=null){
            return getRateFileFormatData().getId();
        }
        return null;
    }

    public void setRateFileFormatDataId(String rateFileFormatDataId) {
        if(Strings.isNullOrBlank(rateFileFormatDataId) == false){
        	RateFileFormatData rateFileFormateDataObj =new RateFileFormatData();
        	rateFileFormateDataObj.setId(rateFileFormatDataId);
        	this.rateFileFormatData = rateFileFormateDataObj;
        }
    }*/
	
	public RateCardData() {
		rateCardVersionRelation = Collectionz.newArrayList();
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

	


	@Column(name = "RATE_UOM")
	public String getRateUom() {
		return rateUom;
	}

	public void setRateUom(String rateUom) {
		this.rateUom = rateUom;
	}
	
	@Column(name = "PULSE_UOM")
	public String getPulseUom() {
		return pulseUom;
	}

	public void setPulseUom(String pulseUom) {
		this.pulseUom = pulseUom;
	}
	
	@Column(name =  "LABEL_KEY_ONE")
	public String getLabelKey1() {
		return labelKey1;
	}

	public void setLabelKey1(String labelKey1) {
		this.labelKey1 = labelKey1;
	}

	@Column(name =  "LABEL_KEY_TWO")
	public String getLabelKey2() {
		return labelKey2;
	}

	public void setLabelKey2(String labelKey2) {
		this.labelKey2 = labelKey2;
	}

	@OneToMany(fetch = FetchType.EAGER,mappedBy = "rateCardData" ,cascade = {CascadeType.ALL},orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
	public List<RateCardVersionRelation> getRateCardVersionRelation() {
		return rateCardVersionRelation;
	}

	public void setRateCardVersionRelation(List<RateCardVersionRelation> rateCardVersionRelation) {
		this.rateCardVersionRelation = rateCardVersionRelation;
	}
	
	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}
	
	@Transient
	@Override
	@JsonIgnore
	public String getResourceName() {
		return getName();
	}
	
	@Override
	public JsonObject toJson() {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.NAME, name);
		jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
		/*jsonObject.addProperty(FieldValueConstants.RATEFILEFORMAT, rateFileFormatData.getName());*/
		jsonObject.addProperty(FieldValueConstants.RATEUOM, rateUom);
		jsonObject.addProperty(FieldValueConstants.PULSEUOM, pulseUom);
		jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
		jsonObject.addProperty(FieldValueConstants.GROUPS, getGroups());
		jsonObject.addProperty(FieldValueConstants.LABEL_KEY_ONE, labelKey1);
        jsonObject.addProperty(FieldValueConstants.LABEL_KEY_TWO, labelKey2);
		return jsonObject;
	}
}
