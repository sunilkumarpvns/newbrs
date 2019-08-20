package com.elitecore.corenetvertex.sm.location.region;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Used to manage data of region with database as well as controller
 * @author dhyani.raval
 */
@Entity(name = "com.elitecore.corenetvertex.sm.location.region.RegionData")
@Table(name = "TBLM_REGION")
public class RegionData extends DefaultGroupResourceData implements Serializable {

    private String name;
    private CountryData countryData;

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "COUNTRY_ID")
    @JsonIgnore
    public CountryData getCountryData() {
        return countryData;
    }

    public void setCountryData(CountryData countryData) {
        this.countryData = countryData;
    }

    @Column(name = "STATUS")
    @Override
    public String getStatus() {
        return super.getStatus();
    }

    @Transient
    @Override
    public String getResourceName() {
        return getName();
    }

    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME,name);
        jsonObject.addProperty("Country",countryData.getName());
        return jsonObject;
    }

    @Transient
    public String getCountryId() {
        if(this.getCountryData()!=null){
            return getCountryData().getId();
        }
        return null;
    }

    public void setCountryId(String databaseId) {
        if(Strings.isNullOrBlank(databaseId) == false){
            CountryData countryDataTemp = new CountryData();
            countryDataTemp.setId(databaseId);
            this.countryData = countryDataTemp;
        }
    }
}
