package com.elitecore.corenetvertex.sm.location.area;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.elitecore.corenetvertex.sm.location.city.CityData;
import com.elitecore.corenetvertex.sm.routing.network.NetworkData;
import com.google.gson.JsonArray;
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
import java.io.Serializable;
import java.util.List;


/**
 * Used to manage data of area with database as well as controller
 * @author dhyani.raval
 */
@Entity(name = "com.elitecore.corenetvertex.sm.location.area.AreaData")
@Table(name = "TBLM_AREA")
public class AreaData extends DefaultGroupResourceData implements Serializable {

    private String name;
    private CityData cityData;
    private NetworkData networkData;
    private String param1;
    private String param2;
    private String param3;
    private Integer congestionStatus;
    private String calledStations;
    private String ssids;
    private List<LacInformationData> lacInformationDataList;
    private transient String regionId;
    private transient String countryId;

    public AreaData() {
        this.lacInformationDataList = Collectionz.newArrayList();
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "CITY_ID")
    @JsonIgnore
    public CityData getCityData() {
        return cityData;
    }

    public void setCityData(CityData cityData) {
        this.cityData = cityData;
    }

    @ManyToOne
    @JoinColumn(name = "NETWORK_ID")
    @JsonIgnore
    public NetworkData getNetworkData() {
        return networkData;
    }

    public void setNetworkData(NetworkData networkData) {
        this.networkData = networkData;
    }

    @Column(name = "PARAM1")
    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    @Column(name = "PARAM2")
    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    @Column(name = "PARAM3")
    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    @Column(name = "CONGESTION_STATUS")
    public Integer getCongestionStatus() {
        return congestionStatus;
    }

    public void setCongestionStatus(Integer congestionStatus) {
        this.congestionStatus = congestionStatus;
    }

    @Column(name = "CALLED_STATIONS")
    public String getCalledStations() {
        return calledStations;
    }

    public void setCalledStations(String calledStations) {
        this.calledStations = calledStations;
    }

    @Column(name = "SSIDS")
    public String getSsids() {
        return ssids;
    }

    public void setSsids(String ssids) {
        this.ssids = ssids;
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


    @OneToMany(fetch = FetchType.LAZY,cascade = {CascadeType.ALL},orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "AREA_ID")
    public List<LacInformationData> getLacInformationDataList() {
        return lacInformationDataList;
    }

    public void setLacInformationDataList(List<LacInformationData> lacInformationDataList) {
        this.lacInformationDataList = lacInformationDataList;
    }
    @Transient
    public String getCityId() {
        if(this.getCityData()!=null){
            return getCityData().getId();
        }
        return null;
    }

    public void setCityId(String cityId) {
        if(Strings.isNullOrBlank(cityId) == false){
            CityData cityDataTemp = new CityData();
            cityDataTemp.setId(cityId);
            this.cityData = cityDataTemp;
        }
    }

    @Transient
    public String getNetworkId() {
        if(this.getNetworkData()!=null){
            return getNetworkData().getId();
        }
        return null;
    }

    public void setNetworkId(String networkId) {
        if(Strings.isNullOrBlank(networkId) == false){
            NetworkData networkDataTemp = new NetworkData();
            networkDataTemp.setId(networkId);
            this.networkData = networkDataTemp;
        }
    }

    @Transient
    public String getRegionId() {
        if (Strings.isNullOrBlank(regionId)) {
           regionId = getCityData().getRegionData().getId();
        }
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    @Transient
    public String getCountryId() {
        if (Strings.isNullOrBlank(countryId)) {
            countryId = getCityData().getRegionData().getCountryData().getId();
        }
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME,name);
        jsonObject.addProperty("Country", cityData.getRegionData().getCountryData().getName());
        jsonObject.addProperty("Region",cityData.getRegionData().getName());
        jsonObject.addProperty("City",cityData.getName());
        if(networkData != null) {
            jsonObject.addProperty("Network",networkData.getName());
        }
        jsonObject.addProperty("Param1",getParam1());
        jsonObject.addProperty("Param2",getParam2());
        jsonObject.addProperty("Param3",getParam3());
        jsonObject.addProperty("Called Stations",getCalledStations());
        jsonObject.addProperty("SSIDs",getSsids());

        if(lacInformationDataList != null) {
            JsonArray jsonArrayLacInformation = new JsonArray();
            JsonObject jsonObjectLacInformation = new JsonObject();
            for(LacInformationData lacInformationData : lacInformationDataList) {
                if(lacInformationData != null) {
                    jsonArrayLacInformation.add(lacInformationData.toJson());
                    jsonObjectLacInformation.add(lacInformationData.getLac() + "", jsonArrayLacInformation);
                }
            }
            jsonObject.add("LAC Information", jsonObjectLacInformation);
        }

        return jsonObject;
    }
}
