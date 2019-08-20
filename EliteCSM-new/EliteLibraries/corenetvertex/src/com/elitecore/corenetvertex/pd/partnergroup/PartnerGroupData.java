package com.elitecore.corenetvertex.pd.partnergroup;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pd.lob.LobData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonObject;

/**
 * Used to manage Patner Group related information with DB 
 * Created by saket on 28/12/17.
 */
 
@Entity(name="com.elitecore.corenetvertex.pd.partnergroup.PartnerGroupData")
@Table(name = "TBLM_PARTNER_GROUP")
public class PartnerGroupData extends ResourceData implements Serializable {

	private static final long serialVersionUID = -9009640772210254187L;
	
	private String name;
	private String description;
	private LobData lobData;
	
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LOB_ID")
	public LobData getLobData() {
		return lobData;
	}

	public void setLobData(LobData lobData) {
		this.lobData = lobData;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Transient
    public String getLobId() {
        if(this.getLobData()!=null){
            return getLobData().getId();
        }
        return null;
    }

	public void setLobId(String lobId) {
		if (Strings.isNullOrBlank(lobId) == false) {
			LobData lobDataObj = new LobData();
			lobDataObj.setId(lobId);
			this.lobData = lobDataObj;
		}
	}

	@Override
	@Column(name = "GROUPS")
	public String getGroups() {
		return super.getGroups();
	}

	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}
	
	@Override
	@Transient
    @XmlTransient
    public String getHierarchy() {
        return getId() + "<br>" + name;
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
		if(getLobData() != null) {
            jsonObject.addProperty(FieldValueConstants.LOB,lobData.getName());
        }
		jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
		jsonObject.addProperty(FieldValueConstants.GROUPS, getGroups());
		return jsonObject;
	}
}
