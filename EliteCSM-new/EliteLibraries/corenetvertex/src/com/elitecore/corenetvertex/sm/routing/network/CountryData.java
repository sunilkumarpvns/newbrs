package com.elitecore.corenetvertex.sm.routing.network;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import com.google.gson.JsonObject;

@Entity(name="com.elitecore.corenetvertex.sm.routing.network.CountryData")
@Table(name = "TBLS_COUNTRY")
public class CountryData implements Serializable{

	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String code;
	private String geography;
	

	@Id
    @Column(name = "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    @Transient
    @XmlTransient
    public String getAuditableId() {
        return id;
    }
    
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	@Column(name = "CODE")
	public String getCode() {
		return code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Transient
	public String getResourceName() {
		return name;
	}
	
	public JsonObject toJson() {
		return new JsonObject();
	}

	@Transient
	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}
}


