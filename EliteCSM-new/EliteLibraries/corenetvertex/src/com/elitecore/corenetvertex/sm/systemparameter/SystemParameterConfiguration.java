package com.elitecore.corenetvertex.sm.systemparameter;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Transient;
import java.util.List;

/**
 * @author jaidiptrivedi
 */
public class SystemParameterConfiguration extends DefaultGroupResourceData {

    private List<SystemParameterData> systemParameters = Collectionz.newArrayList();
    private List<SystemParameterData> packageParameters = Collectionz.newArrayList();

    public List<SystemParameterData> getSystemParameters() {
        return systemParameters;
    }

    public void setSystemParameters(List<SystemParameterData> systemParameters) {
        this.systemParameters = systemParameters;
    }

    public List<SystemParameterData> getPackageParameters() {
        return packageParameters;
    }

    public void setPackageParameters(List<SystemParameterData> packageParameters) {
        this.packageParameters = packageParameters;
    }

    @Override
    @JsonIgnore
    //We don't have any id in SystemParameter. It works on key value formation. Where key is alias.
    public String getId() {
        return CommonConstants.EMPTY_STRING;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        if (Collectionz.isNullOrEmpty(systemParameters) == false) {
            JsonArray jsonArray = new JsonArray();
            for (SystemParameterData systemParameterData : systemParameters) {
                jsonArray.add(systemParameterData.toJson());
            }
            jsonObject.add("System Parameters ", jsonArray);
        }

        if (Collectionz.isNullOrEmpty(packageParameters) == false) {
            JsonArray jsonArray = new JsonArray();
            for (SystemParameterData systemParameterData : packageParameters) {
                jsonArray.add(systemParameterData.toJson());
            }
            jsonObject.add("Package Parameters ", jsonArray);
        }

        return jsonObject;
    }

    @Override
    @JsonIgnore
    public String getGroupNames() {
        return super.getGroupNames();
    }

    @Override
    @JsonIgnore
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    @Transient
    public String getResourceName() {
        return "Global-System-Parameter-Configuration";
    }
}
