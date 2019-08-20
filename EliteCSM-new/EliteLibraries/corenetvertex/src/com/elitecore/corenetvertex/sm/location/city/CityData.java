package com.elitecore.corenetvertex.sm.location.city;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.elitecore.corenetvertex.sm.location.region.RegionData;
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
 * Used to manage data of city with database as well as controller
 * @author dhyani.raval
 */
@Entity(name = "com.elitecore.corenetvertex.sm.location.city.CityData")
@Table(name = "TBLM_CITY")
public class CityData extends DefaultGroupResourceData implements Serializable{

    private String name;
    private RegionData regionData;
    private String countryId;

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "REGION_ID")
    @JsonIgnore
    public RegionData getRegionData() {
        return regionData;
    }

    public void setRegionData(RegionData regionData) {
        this.regionData = regionData;
    }

    @Override
    @Transient
    public String getResourceName() {
        return getName();
    }

    @Column(name = "STATUS")
    @Override
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME,name);
        jsonObject.addProperty("Country",regionData.getCountryData().getName());
        jsonObject.addProperty("Region",regionData.getName());
        return jsonObject;
    }

    @Transient
    public String getRegionId() {
        if(this.getRegionData()!=null){
            return getRegionData().getId();
        }
        return null;
    }

    public void setRegionId(String regionId) {
        if(Strings.isNullOrBlank(regionId) == false){
            RegionData regionDataTemp = new RegionData();
            regionDataTemp.setId(regionId);
            this.regionData = regionDataTemp;
        }
    }

    @Transient
    public String getCountryId() {
        if(Strings.isNullOrBlank(countryId)) {
            countryId = getRegionData().getCountryData().getId();
        }
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }
}
