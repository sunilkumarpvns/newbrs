package com.elitecore.nvsmx.sm.controller.region;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.acl.StaffData;
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
 * It will manage all the operation for Region
 * @author dhyani.raval
 */

@ParentPackage(value = "sm")
@Namespace("/sm/region")
@Results({
        @Result(name= SUCCESS, type=RestGenericCTRL.REDIRECT_ACTION ,params = {NVSMXCommonConstants.ACTION_NAME,"region"}),

})
public class RegionCTRL extends RestGenericCTRL<RegionData> {

    private List<CountryData> countryDataList = Collectionz.newArrayList();
    private List<String> regionNameList = Collectionz.newArrayList();

    @Override
    public ACLModules getModule() {
        return ACLModules.REGION;
    }

    @Override
    public RegionData createModel() {
        return new RegionData();
    }

    public List<CountryData> getCountryDataList() {
        return countryDataList;
    }

    public void setCountryDataList(List<CountryData> countryDataList) {
        this.countryDataList = countryDataList;
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
            if(Collectionz.isNullOrEmpty(getRegionNameList()) == false) {
                for (String name : getRegionNameList()) {

                    RegionData regionDataToSave = new RegionData();

                    regionDataToSave.setName(name);

                    regionDataToSave.setCountryData(CRUDOperationUtil.get(CountryData.class, ((RegionData) getModel()).getCountryData().getId()));
                    if(regionDataToSave.getCountryData() == null) {
                        addFieldError("countryId","Invalid Country ID");
                    }
                    regionDataToSave.setCreatedDateAndStaff(staffData);
                    CRUDOperationUtil.save(regionDataToSave);

                    String message = getModule().getDisplayLabel() + " <b><i>" + regionDataToSave.getResourceName() + "</i></b> " + "Created";
                    CRUDOperationUtil.audit(regionDataToSave, regionDataToSave.getResourceName(), AuditActions.CREATE, getStaffData(), null, regionDataToSave.getHierarchy(), message);
                }

                addActionMessage(getModule().getDisplayLabel() + " Data created successfully");
                return new DefaultHttpHeaders(SUCCESS);
            } else {
                return super.create();
            }
        }catch (Exception e){
            getLogger().error(getLogModule(),"Error while creating "+ getModule().getDisplayLabel() +" information.Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError(getModule().getDisplayLabel() + " "+ ActionMessageKeys.CREATE_FAILURE.key);
            return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }
    }

    public String getObjectAsJson(){
        Gson gson = GsonFactory.defaultInstance();
        List<RegionData> regionDataListTemp = Collectionz.newArrayList();
        regionDataListTemp.add(((RegionData)getModel()));
        JsonArray modelJson = gson.toJsonTree(regionDataListTemp,new TypeToken<List<RegionData>>() {}.getType()).getAsJsonArray();
        return modelJson.toString();
    }

    @Override
    protected boolean prepareAndValidateDestroy(RegionData regionData) {
        List associatedCityList = findAssociatedCities(regionData);
        if (Collectionz.isNullOrEmpty(associatedCityList) == false) {
            addActionError("Region "+regionData.getName()+" is associated with cities");
            String associatedCities = Strings.join(",", associatedCityList);
            getLogger().error(getLogModule(), "Error while deleting region " + regionData.getName() + ".Reason: region is associated with " + associatedCities);
            return false;
        }
        return true;
    }

    @SkipValidation
    @Override
    public void prepareValuesForSubClass() throws Exception {
        setCountryDataList(CRUDOperationUtil.findAll(CountryData.class));
    }

    public List<String> getRegionNameList() {
        return regionNameList;
    }

    public void setRegionNameList(List<String> regionNameList) {
        this.regionNameList = regionNameList;
    }

    @Override
    public void validate() {
        RegionData regionData = (RegionData) getModel();
        if(Collectionz.isNullOrEmpty(getRegionNameList()) == false) {
            for(String name : getRegionNameList()) {
                boolean isAlreadyExist = CRUDOperationUtil.isDuplicateNameWithInParent(RegionData.class,getMethodName(),regionData.getId(),name,regionData.getCountryId(),"countryData");
                if(isAlreadyExist){
                    addFieldError("name","Name already exists");
                }
            }
        } else {
            regionData.setCountryData(CRUDOperationUtil.get(CountryData.class,regionData.getCountryData().getId()));
            if(regionData.getCountryData() == null) {
                addFieldError("countryId","Invalid Country ID");
            }
            super.validate();
        }
    }

    public List findAssociatedCities(RegionData regionData){
        try {
            DetachedCriteria cityCriteria = DetachedCriteria.forClass(CityData.class);
            cityCriteria.add(Restrictions.eq("regionData",regionData));
            cityCriteria.setProjection(Projections.property("name"));
             return CRUDOperationUtil.findAllByDetachedCriteria(cityCriteria);
        }catch (Exception e){
            LogManager.getLogger().error(getLogModule(), "Error while fetching associated cities.Reason: "+e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
}
        return null;
    }

}
