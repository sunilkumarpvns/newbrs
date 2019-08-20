package com.elitecore.corenetvertex.sm.location.geography;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.google.gson.JsonObject;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@Entity(name="com.elitecore.corenetvertex.sm.location.geography.GeographyData")
@Table(name = "TBLM_GEOGRAPHY")
public class GeographyData extends DefaultGroupResourceData implements Serializable {

    private String name;
    private String country;
    private transient List<CountryData> geographyCountryRelDatas;

    public GeographyData(){
        geographyCountryRelDatas = Collectionz.newArrayList();
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    @Transient
    public String getResourceName() {
        return getName();
    }

    @Override
    @Column(name = "STATUS")
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME, name);
        if(Collectionz.isNullOrEmpty(geographyCountryRelDatas) == false){
            JsonObject countryJson = new JsonObject();
            for(CountryData country : geographyCountryRelDatas) {
                countryJson.addProperty(country.getName(),country.getCode());
            }
            jsonObject.add(FieldValueConstants.COUNTRY_DATA,countryJson);
        }
        return jsonObject;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name="TBLM_GEOGRAPHY_COUNTRY_REL",
            joinColumns={@JoinColumn(name="GEOGRAPHY_ID", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="COUNTRY_CODE", referencedColumnName="code")})
    public List<CountryData> getGeographyCountryRelDatas() {
        return geographyCountryRelDatas;
    }

    public void setGeographyCountryRelDatas(List<CountryData> geographyCountryRelDatas) {
        this.geographyCountryRelDatas = geographyCountryRelDatas;
    }

    @Transient
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


}
