package com.elitecore.nvsmx.sm.controller.city;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.location.area.AreaData;
import com.elitecore.corenetvertex.sm.location.city.CityData;
import com.elitecore.corenetvertex.sm.location.region.RegionData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * It will manage all the operation for City
 * @author dhyani.raval
 */

@ParentPackage(value = "sm")
@Namespace("/sm/city")
@Results({
        @Result(name= SUCCESS, type=RestGenericCTRL.REDIRECT_ACTION,params = {NVSMXCommonConstants.ACTION_NAME,"city"}),

})
public class CityCTRL extends RestGenericCTRL<CityData> {

    private List<CountryData> countryDataList = Collectionz.newArrayList();
    private List<RegionData> regionDataList = Collectionz.newArrayList();
    private List<String> cityNameList = Collectionz.newArrayList();

    @Override
    public ACLModules getModule() {
        return ACLModules.CITY;
    }

    @Override
    public CityData createModel() {
        return new CityData();
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

    @Override
    public HttpHeaders create() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called create()");
        }
        try {
            String result = authorize();
            if(result.equals(SUCCESS) == false){
                setActionChainUrl(getRedirectURL(METHOD_EDITNEW));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            StaffData staffData = getStaffData();
            if(Collectionz.isNullOrEmpty(getCityNameList()) == false) {
                for (String name : getCityNameList()) {

                    CityData cityData = (CityData) getModel();

                    CityData cityDataToSave = new CityData();
                    cityDataToSave.setName(name);

                    setRegionCountryValue(cityDataToSave,cityData.getRegionData());

                    cityDataToSave.setCreatedDateAndStaff(staffData);
                    CRUDOperationUtil.save(cityDataToSave);

                    String message = getModule().getDisplayLabel() + " <b><i>" + cityDataToSave.getResourceName() + "</i></b> " + "Created";
                    CRUDOperationUtil.audit(cityDataToSave, cityDataToSave.getResourceName(), AuditActions.CREATE, getStaffData(), getRequest().getRemoteAddr(), cityDataToSave.getHierarchy(), message);
                }
                addActionMessage(getModule().getDisplayLabel() + " Data created successfully");
                return new DefaultHttpHeaders(SUCCESS);
            } else {
                return super.create();
            }

        }catch (Exception e){
            getLogger().error(getLogModule(),"Error while creating "+ getModule().getDisplayLabel() +" information. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError(getModule().getDisplayLabel() + " "+ ActionMessageKeys.CREATE_FAILURE.key);
            return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }

    }

    private void setRegionCountryValue(CityData cityData, RegionData regionData)  {
        cityData.setRegionData(CRUDOperationUtil.get(RegionData.class,regionData.getId()));
        if(cityData.getRegionData() == null) {
            addFieldError("regionId" ,"Invalid Region ID");
        }

        if (cityData.getRegionData().getCountryData().getId().equals(cityData.getCountryId()) == false) {
            addFieldError("countryId" ,"Invalid Country ID");
        }
    }

    @Override
    public HttpHeaders index() {
        setRegionDataList(CRUDOperationUtil.findAll(RegionData.class));
        return super.index();
    }


    @SkipValidation
    @Override
    public void prepareValuesForSubClass() throws Exception {
        setCountryDataList(CRUDOperationUtil.findAll(CountryData.class));
        setRegionDataList(CRUDOperationUtil.findAll(RegionData.class));
    }

    @Override
    protected boolean prepareAndValidateDestroy(CityData cityData) {
        List associatedAreaList = findAssociatedAreas(cityData);
        if (Collectionz.isNullOrEmpty(associatedAreaList) == false) {
            addActionError("City "+cityData.getName()+" is associated with areas");
            String associatedAreas = Strings.join(",", associatedAreaList);
            getLogger().error(getLogModule(), "Error while deleting city " + cityData.getName() + ".Reason: city is associated with " + associatedAreas);
            return false;
        }
        return true;
    }

    /**
     * Required to fetch Region List in View page
     * @throws Exception
     */
    public void prepareShow() throws Exception {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called prepareShow()");
        }
        prepareValuesForSubClass();
    }

    public String getObjectAsJson(){
        Gson gson = GsonFactory.defaultInstance();
        List<CityData> cityDataListTemp = Collectionz.newArrayList();
        cityDataListTemp.add(((CityData)getModel()));
        JsonArray modelJson = gson.toJsonTree(cityDataListTemp,new TypeToken<List<RegionData>>() {}.getType()).getAsJsonArray();
        return modelJson.toString();
    }

    public List<String> getCityNameList() {
        return cityNameList;
    }

    public void setCityNameList(List<String> cityNameList) {
        this.cityNameList = cityNameList;
    }

    @Override
    public void validate() {
        CityData cityData = (CityData) getModel();
        if(Collectionz.isNullOrEmpty(getCityNameList()) == false) {
            for(String name : getCityNameList()) {
                boolean isAlreadyExist = CRUDOperationUtil.isDuplicateNameWithInParent(CityData.class,getMethodName(),cityData.getId(),name,cityData.getRegionId(),"regionData");
                if(isAlreadyExist){
                    addFieldError("name","Name already exists");
                }
            }
        } else {
            setRegionCountryValue(cityData,cityData.getRegionData());
            super.validate();
        }
    }

    public List findAssociatedAreas(CityData cityData) {
        try {
            DetachedCriteria areaData = DetachedCriteria.forClass(AreaData.class);
            areaData.add(Restrictions.eq("cityData", cityData));
            areaData.setProjection(Projections.property("name"));
            return CRUDOperationUtil.findAllByDetachedCriteria(areaData);

        } catch (Exception e) {
            LogManager.getLogger().error(getLogModule(), "Error while fetching associated areas.Reason: " + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
        }
        return null;

    }
}
