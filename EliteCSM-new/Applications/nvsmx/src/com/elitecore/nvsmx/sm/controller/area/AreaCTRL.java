package com.elitecore.nvsmx.sm.controller.area;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.location.area.AreaData;
import com.elitecore.corenetvertex.sm.location.area.LacInformationData;
import com.elitecore.corenetvertex.sm.location.city.CityData;
import com.elitecore.corenetvertex.sm.location.region.RegionData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.sm.routing.network.NetworkData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.util.List;

import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * It will manage all the operation for Area
 * @author dhyani.raval
 */

@ParentPackage(value = "sm")
@Namespace("/sm/area")
@Results({
        @Result(name= SUCCESS, type=RestGenericCTRL.REDIRECT_ACTION,params = {NVSMXCommonConstants.ACTION_NAME,"area"}),

})
public class AreaCTRL extends RestGenericCTRL<AreaData> {

    private List<CountryData> countryDataList = Collectionz.newArrayList();
    private List<RegionData> regionDataList = Collectionz.newArrayList();
    private List<CityData> cityDataList = Collectionz.newArrayList();
    private List<NetworkData> networkDataList = Collectionz.newArrayList();

    @Override
    public ACLModules getModule() {
        return ACLModules.AREA;
    }

    @Override
    public AreaData createModel() {
        return new AreaData();
    }

    public List<CountryData> getCountryDataList() {
        return countryDataList;
    }

    public void setCountryDataList(List<CountryData> countryDataList) {
        this.countryDataList = countryDataList;
    }

    public String getRegionDataList() {
        Gson gson = GsonFactory.defaultInstance();
        JsonArray modelJson = gson.toJsonTree(regionDataList,new TypeToken<List<RegionData>>() {}.getType()).getAsJsonArray();
        return modelJson.toString();
    }

    public void setRegionDataList(List<RegionData> regionDataList) {
        this.regionDataList = regionDataList;
    }

    public String getCityDataList() {
        Gson gson = GsonFactory.defaultInstance();
        JsonArray modelJson = gson.toJsonTree(cityDataList,new TypeToken<List<RegionData>>() {}.getType()).getAsJsonArray();
        return modelJson.toString();
    }

    public void setCityDataList(List<CityData> cityDataList) {
        this.cityDataList = cityDataList;
    }

    public String getNetworkDataList() {
        Gson gson = GsonFactory.defaultInstance();
        JsonArray modelJson = gson.toJsonTree(networkDataList,new TypeToken<List<NetworkData>>() {}.getType()).getAsJsonArray();
        return modelJson.toString();
    }

    public void setNetworkDataList(List<NetworkData> networkDataList) {
        this.networkDataList = networkDataList;
    }

    @SkipValidation
    @Override
    public void prepareValuesForSubClass() throws Exception {
        setCountryDataList(CRUDOperationUtil.findAll(CountryData.class));
        setRegionDataList(CRUDOperationUtil.findAll(RegionData.class));
        setCityDataList(CRUDOperationUtil.findAll(CityData.class));
        setNetworkDataList(CRUDOperationUtil.findAll(NetworkData.class));
    }

    private void filterEmptyLacInformationDatas(List<LacInformationData> lacInformationDataList) {
        Collectionz.filter(lacInformationDataList, (LacInformationData lacInformationData) -> {
            if(lacInformationData == null) {
                return false;
            }

           if(lacInformationData.getLac() == null) {
                addFieldError("LAC",getText("error.required.field", new String[]{"LAC"}));
           }
           if(lacInformationData.getLac() > 999999) {
               addFieldError("LAC", "Maximum Length of LAC can be 6 digits");
           }
           return true;
        });
    }

    public String getLacInformationDataAsJson(){
        Gson gson = GsonFactory.defaultInstance();
        AreaData areaData = (AreaData) getModel();
        return gson.toJsonTree(areaData.getLacInformationDataList(),new TypeToken<List<LacInformationData>>() {}.getType()).getAsJsonArray().toString();
    }

    @Override
    public void validate() {

        AreaData areaData = (AreaData) getModel();
        areaData.setCityData(CRUDOperationUtil.get(CityData.class,areaData.getCityData().getId()));

        if (areaData.getCityData() == null) {
            addFieldError("cityId","Invalid City ID");
        }

        if(areaData.getCityData().getRegionData().getId().equals(areaData.getRegionId()) == false) {
            addFieldError("regionId","Invalid Region ID");
        }

        if(areaData.getCityData().getRegionData().getCountryData().getId().equals(areaData.getCountryId()) == false) {
            addFieldError("countryId","Invalid Country ID");
        }


        if(areaData.getNetworkData() != null && Strings.isNullOrBlank(areaData.getNetworkData().getId()) == false) {
            areaData.setNetworkData(CRUDOperationUtil.get(NetworkData.class,areaData.getNetworkData().getId()));
        }

        filterEmptyLacInformationDatas(areaData.getLacInformationDataList());
        if(Collectionz.isNullOrEmpty(areaData.getLacInformationDataList()) == false && areaData.getNetworkData() == null) {
            addActionError("Network is Required");
        }

        super.validate();
    }
}
