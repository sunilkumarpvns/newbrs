package com.elitecore.nvsmx.sm.controller.alert.file;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.alerts.AlertListenerData;
import com.elitecore.corenetvertex.sm.alerts.AlertListenerRelData;
import com.elitecore.corenetvertex.sm.alerts.AlertTypes;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.sm.model.alert.AlertDisplayData;
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
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(value = "sm")
@Namespace("/sm/alert")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","file-alert"}),
})

public class FileAlertCTRL extends RestGenericCTRL<AlertListenerData>{



    private static final Predicate<? super AlertListenerRelData> EMPTY_ALERT_RELATION_PREDICATE = alertListenerRelData -> {
        if (alertListenerRelData == null) {
            return false;
        }
        return Strings.isNullOrBlank(alertListenerRelData.getType()) ? false : true;
    };


    @Override
    public ACLModules getModule() {
        return ACLModules.ALERT;
    }

    @Override
    public AlertListenerData createModel() {
        return new AlertListenerData();
    }

    @Override
    protected String getRedirectURL(String method) {
        StringBuilder sb = new StringBuilder();
        sb.append(getModule().getComponent().getUrl()).append(CommonConstants.FORWARD_SLASH)
                .append(getModule().getActionURL()[0]).append(CommonConstants.FORWARD_SLASH).append(method);
        return sb.toString();
    }


    @Override
    @SkipValidation
    public HttpHeaders index() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called index()");
        }
        List<AlertListenerData> alertDatas = CRUDOperationUtil.findAll(AlertListenerData.class);
        List<AlertListenerData> fileAlertDatas = Collectionz.newArrayList();
        alertDatas.stream().forEach(fileAlertData -> {
            if(AlertTypes.FILE.name().equals(fileAlertData.getType())) {
                fileAlertDatas.add(fileAlertData);
            }
        });
        setList(fileAlertDatas);
        setActionChainUrl("sm/alert/alert/index");
        getRequest().setAttribute(NVSMXCommonConstants.TYPE,AlertTypes.FILE.name());

        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
    }




    @Override
    public HttpHeaders create() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called create()");
        }
        AlertListenerData alertListenerData = (AlertListenerData) getModel();
        alertListenerData.setType(AlertTypes.FILE.name());
        filterEmptyAlertRelation(alertListenerData);
        return super.create();
    }


    @Override
    public HttpHeaders update() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called update()");
        }
        AlertListenerData alertListenerData = (AlertListenerData) getModel();
        alertListenerData.setType(AlertTypes.FILE.name());
        filterEmptyAlertRelation(alertListenerData);
        return super.update();
    }


    private void filterEmptyAlertRelation(AlertListenerData alertListenerData) {
        Collectionz.filter(alertListenerData.getAlertListenerRelDataList(), EMPTY_ALERT_RELATION_PREDICATE);
    }

    public List<AlertDisplayData> getAlertDisplayDataList() {
        AlertListenerData alertData = (AlertListenerData) getModel();
        return generateAlertDisplayDataList(alertData.getAlertListenerRelDataList());
    }



    private List<AlertDisplayData> generateAlertDisplayDataList(List<AlertListenerRelData> alertListenerRelDataList){
        List<AlertDisplayData> alertDisplayDatas = new ArrayList<>();
        if(Collectionz.isNullOrEmpty(alertListenerRelDataList)){
            for (Alerts alertConstants : Alerts.values()) {
                alertDisplayDatas.add(new AlertDisplayData(alertConstants, false, alertConstants.isFloodControl()));
            }
            return alertDisplayDatas;
        }
        for(Alerts alertConstants : Alerts.values()){
            boolean contains = false;
            for(AlertListenerRelData alertListenerRelData: alertListenerRelDataList){
                if(alertListenerRelData.from().equals(alertConstants)){
                    alertDisplayDatas.add(new AlertDisplayData(alertConstants,true,alertListenerRelData.isFloodControl()));
                    contains = true;
                    break;
                }
            }
            if(contains == false){
                alertDisplayDatas.add(new AlertDisplayData(alertConstants, false, alertConstants.isFloodControl()));
            }
        }
        return alertDisplayDatas;
    }

    @Override
    public void validate(){
        super.validate();
        AlertListenerData fileAlertListenerData = (AlertListenerData) getModel();
        if(Collectionz.isNullOrEmpty(fileAlertListenerData.getAlertListenerRelDataList())){
            addFieldError("alertListenerRelDataList", getText("no.alert.configured"));
        }
    }
    public String getFileAlertListAsJson(){
        Gson gson = GsonFactory.defaultInstance();
        JsonArray modelJson = gson.toJsonTree(getList(),new TypeToken<List<AlertListenerData>>() {}.getType()).getAsJsonArray();
        return modelJson.toString();
    }
    @Override
    protected SimpleExpression getAdditionalCriteria() {
        return Restrictions.eq("type", AlertTypes.FILE.name());
    }
}
