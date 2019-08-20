package com.elitecore.nvsmx.sm.controller.geography;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.location.geography.GeographyData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(value = "sm")
@Namespace("/sm/geography")
@Results({
        @Result(name= SUCCESS, type= RestGenericCTRL.REDIRECT_ACTION ,params = {NVSMXCommonConstants.ACTION_NAME,"geography"}),

})
public class GeographyCTRL extends RestGenericCTRL<GeographyData> {

    private List<CountryData> countryList = Collectionz.newArrayList();


    @Override
    public ACLModules getModule() {
        return ACLModules.GEOGRAPHY;
    }

    @Override
    public GeographyData createModel() {
        return new GeographyData();
    }

    public List<CountryData> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<CountryData> countryList) {
        this.countryList = countryList;
    }


    @SkipValidation
    @Override
    public void prepareValuesForSubClass() throws Exception {
        setCountryList(CRUDOperationUtil.findAll(CountryData.class));
    }


    @Override
    public void validate() {
        super.validate();
        GeographyData geographyData = (GeographyData) getModel();
        if(Strings.isNullOrBlank(geographyData.getCountry())){
            addFieldError("country",getText("error.valueRequired"));
        }
        List<CountryData> countryList = Collectionz.newArrayList();
        List<String> countryCodes = getCountryAliasList(geographyData);
        countryCodes.forEach(country -> {
            DetachedCriteria detachedCriteria = DetachedCriteria.forClass(CountryData.class);
            detachedCriteria.add(Restrictions.eq("code",country));
            List<CountryData> countriesBasedOnAlias = CRUDOperationUtil.findAllByDetachedCriteria(detachedCriteria);
            if(Collectionz.isNullOrEmpty(countriesBasedOnAlias)) {
                addFieldError("country", "Country with code " + country + " does not exists in DB");
            }else{
                countryList.addAll(countriesBasedOnAlias);
            }
        });
        geographyData.setGeographyCountryRelDatas(countryList);
    }


    public String getCountryListAsJson(){
        Gson gson = GsonFactory.defaultInstance();
        GeographyData geographyData = (GeographyData) getModel();
        return gson.toJsonTree(geographyData.getGeographyCountryRelDatas(), new TypeToken<List<GeographyData>>() {}.getType()).getAsJsonArray().toString();
    }


    private List<String> getCountryAliasList(GeographyData geographyData) {
        return CommonConstants.COMMA_SPLITTER.split(geographyData.getCountry());
    }
}
