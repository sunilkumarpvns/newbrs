package com.elitecore.nvsmx.sm.controller.systemparameter;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.snmp.mib.mib2.autogen.System;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.sm.routing.network.OperatorData;
import com.elitecore.corenetvertex.sm.systemparameter.SystemParameter;
import com.elitecore.corenetvertex.sm.systemparameter.SystemParameterConfiguration;
import com.elitecore.corenetvertex.sm.systemparameter.SystemParameterData;
import com.elitecore.corenetvertex.sm.systemparameter.SystemParameterValuePool;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.sm.controller.CreateNotSupportedCTRL;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.constants.CommonConstants.ADMIN_USER_NAME;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * @author jaidiptrivedi
 */
@ParentPackage(value = "sm")
@Namespace("/sm/systemparameter")
@Results({
        @Result(name = SUCCESS, type = RestGenericCTRL.REDIRECT_ACTION, params = {"actionName", "system-parameter/show"}),
})
public class SystemParameterCTRL extends CreateNotSupportedCTRL<SystemParameterConfiguration> {

    private Map<String, List<SystemParameterValuePool>> systemParameterValuePoolMap = SystemParameterValuePool.getValueMap();
    private static final String  ALIAS = "alias";
    private List<CountryData> countryList = new ArrayList<>();
    private List<OperatorData> operatorList = new ArrayList<>();
    private CountryData countryData;
    private OperatorData operatorData;

    @Override
    public HttpHeaders show() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called show()");

        }
        try {
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
        } catch (Exception e) {
            addActionError("Error while viewing " + getModule().getDisplayLabel() + " data for id ");
            getLogger().error(getLogModule(), "Error while viewing " + getModule().getDisplayLabel() + " data. Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
        }

        addActionError(getModule().getDisplayLabel() + " Not Found");
        return new DefaultHttpHeaders("error").withStatus(400);

    }

    @Override
    public String edit() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called edit()");
        }
        try {

            if (isAdminUser() == false) {
                addActionError(getText("superadmin.allowed.access"));
                setActionChainUrl(getRedirectURL(METHOD_SHOW));
                return NVSMXCommonConstants.REDIRECT_URL;
            }

            setActionChainUrl(getRedirectURL(METHOD_EDIT));
            return NVSMXCommonConstants.REDIRECT_URL;
        } catch (Exception e) {
            getLogger().error(getLogModule(), "Error while updating " + getModule().getDisplayLabel() + " information.Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
            addActionError("Error while performing Update Operation");
            return ERROR;
        }

    }

    @Override
    public HttpHeaders update() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called update()");
        }

        if (isAdminUser() == false) {
            addActionError(getText("superadmin.allowed.access"));
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        try {
            SystemParameterConfiguration systemParameterConfiguration = (SystemParameterConfiguration) getModel();
            JsonArray difference = new JsonArray();
            for (SystemParameterData systemParameter : systemParameterConfiguration.getSystemParameters()) {
                SystemParameterData systemParameterDataFromDB = getSystemParameterData(systemParameter);
                // Set proper values from Currency util to handle lower case scenario from Rest Input

                if (SystemParameter.CURRENCY.name().equals(systemParameter.getAlias())) {
                    systemParameter.setValue(Currency.getInstance(systemParameter.getValue().trim().toUpperCase()).getCurrencyCode());
                }
             /*   if(SystemParameter.COUNTRY.name().equals(systemParameter.getAlias())){
                    systemParameter.setDisplayValue(SystemParameterDAO.getCountryName(systemParameter.getValue()));
                }
                if(SystemParameter.OPERATOR.name().equals(systemParameter.getAlias())){
                    systemParameter.setDisplayValue(SystemParameterDAO.getOperatorName(systemParameter.getValue()));
                }
                if(Objects.nonNull(systemParameterDataFromDB)){
                    systemParameterDataFromDB.setDisplayValue(SystemParameterDAO.getOperatorName(systemParameterDataFromDB.getValue()));
                    systemParameterDataFromDB.setDisplayValue(SystemParameterDAO.getCountryName(systemParameterDataFromDB.getValue()));
                }*/

                if(systemParameterDataFromDB == null) {
                    systemParameterDataFromDB = new SystemParameterData();
                    String existingValue = SystemParameterDAO.get(systemParameter.getAlias());
                    if(StringUtils.isNotBlank(existingValue)){
                        systemParameterDataFromDB.setValue(existingValue);
                    }
                    systemParameterDataFromDB.setAlias(systemParameter.getAlias());
                    systemParameterDataFromDB.setName(systemParameter.getName());
                    setSystemParameterDiff(difference, systemParameterDataFromDB.toJson(),systemParameter.toJson());
                }else{
                    setSystemParameterDiff(difference, systemParameterDataFromDB.toJson(),systemParameter.toJson());
                }
                CRUDOperationUtil.merge(systemParameter);
            }

            for (SystemParameterData packageParameter : systemParameterConfiguration.getPackageParameters()) {
                SystemParameterData packageParameterFromDB = getSystemParameterData(packageParameter);
                if(packageParameterFromDB != null) {
                    setSystemParameterDiff(difference, packageParameterFromDB.toJson(),packageParameter.toJson());
                }
                CRUDOperationUtil.merge(packageParameter);
            }

            String message = getModule().getDisplayLabel() + " <b><i>" + systemParameterConfiguration.getResourceName() + "</i></b> " + "Updated";

            if(difference.size() > 0) {
                CRUDOperationUtil.audit(systemParameterConfiguration, systemParameterConfiguration.getResourceName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference, systemParameterConfiguration.getHierarchy(), message);
            }

            refresh();
            getLogger().debug(getLogModule(), "Deployment mode in system parameter cache: " + SystemParameterDAO.getDeploymentMode().name());
            addActionMessage(getModule().getDisplayLabel() + " updated successfully");
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code).disableCaching();

        } catch (Exception e) {
            getLogger().error(getLogModule(), "Error while updating " + getModule().getDisplayLabel() + " information.Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
            addActionError("Error while performing Update Operation");
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
    }

    private boolean isAdminUser() {
        return ADMIN_USER_NAME.equalsIgnoreCase(getStaffData().getUserName());
    }


    private SystemParameterData getSystemParameterData(SystemParameterData systemParameter) {
        Criterion criterion = Restrictions.eq(ALIAS,systemParameter.getAlias());
        List<SystemParameterData> systemParams = CRUDOperationUtil.findAll(SystemParameterData.class,criterion);
        SystemParameterData systemParameterDataFromDB = null;
        if(Collectionz.isNullOrEmpty(systemParams) == false){
            systemParameterDataFromDB = systemParams.get(0);
        }
        return systemParameterDataFromDB;
    }




    private void setSystemParameterDiff(JsonArray difference, JsonObject oldJsonObject, JsonObject newJsonObject) {
            JsonArray diff = ObjectDiffer.diff(oldJsonObject, newJsonObject);
            if(diff.size() > 0) {
                difference.add(diff.get(0));
            }
    }

    @Override
    public void validate() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called validate()");
        }
        SystemParameterConfiguration systemParameterConfiguration = (SystemParameterConfiguration) getModel();
        validateSystemParameters(systemParameterConfiguration.getSystemParameters());
    }

    @SkipValidation
    public HttpHeaders refresh() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called refresh()");
        }
        try {
            SystemParameterDAO.refresh();
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(getLogModule(), "System Parameter Refresh Successful");
            }
        } catch (InitializationFailedException e) {
            getLogger().error(getLogModule(), "Error while refreshing " + getModule().getDisplayLabel() + " information.Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
            addActionError("Error while performing Refresh Operation");
        }
        setActionChainUrl(getRedirectURL(METHOD_SHOW));
        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
    }

    @Override
    public void prepareValuesForSubClass() throws Exception {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called prepareValuesForSubClass()");
        }
        if (Collectionz.isNullOrEmpty(SystemParameterDAO.getOrderedSystemParameterData()) || Collectionz.isNullOrEmpty(SystemParameterDAO.getOrderedPackageParameterData())) {
            SystemParameterDAO.initializeSystemParameterList();
        }

        SystemParameterConfiguration systemParameterConfiguration = (SystemParameterConfiguration) getModel();
        systemParameterConfiguration.setSystemParameters(SystemParameterDAO.getOrderedSystemParameterData());
        systemParameterConfiguration.setPackageParameters(SystemParameterDAO.getOrderedPackageParameterData());

        setCountryList(CRUDOperationUtil.findAll(CountryData.class));
        setOperatorList(CRUDOperationUtil.findAll(OperatorData.class));

        for (SystemParameterData data : SystemParameterDAO.getOrderedSystemParameterData()) {
            if (SystemParameter.COUNTRY.name().equals(data.getAlias())) {
                setCountryData(CRUDOperationUtil.get(CountryData.class, data.getValue()));
            } else if (SystemParameter.OPERATOR.name().equals(data.getAlias())) {
                setOperatorData(CRUDOperationUtil.get(OperatorData.class, data.getValue()));
            }
        }
    }


    public List<CountryData> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<CountryData> countryList) {
        this.countryList = countryList;
    }

    public List<OperatorData> getOperatorList() {
        return operatorList;
    }

    public void setOperatorList(List<OperatorData> operatorList) {
        this.operatorList = operatorList;
    }

    public CountryData getCountryData() {
        return countryData;
    }

    public void setCountryData(CountryData countryData) {
        this.countryData = countryData;
    }

    public OperatorData getOperatorData() {
        return operatorData;
    }

    public void setOperatorData(OperatorData operatorData) {
        this.operatorData = operatorData;
    }

    public void prepareShow() throws Exception{
    	getRequest().setAttribute(NVSMXCommonConstants.TYPE, NVSMXCommonConstants.GLOBAL_SYSTEM_PARAMETR);
        prepareValuesForSubClass();
    }

    public void prepareRefresh() throws Exception{
    	getRequest().setAttribute(NVSMXCommonConstants.TYPE, NVSMXCommonConstants.GLOBAL_SYSTEM_PARAMETR);
        prepareValuesForSubClass();
    }

    private void validateSystemParameters(List<SystemParameterData> systemParameterDatas) {
        for (SystemParameterData systemParameterData : systemParameterDatas) {
            SystemParameter systemParameter = SystemParameter.fromName(systemParameterData.getAlias());
            if (systemParameter == null || systemParameter.validate(systemParameterData.getValue()) == false) {
                addFieldError(systemParameterData.getName(), "Invalid value: " + systemParameterData.getValue());
            }
        }
    }

    @Override
    public ACLModules getModule() {
        return ACLModules.SYSTEM_PARAMETER;
    }

    @Override
    public SystemParameterConfiguration createModel() {
        return new SystemParameterConfiguration();
    }

    public Map<String, List<SystemParameterValuePool>> getSystemParameterValuePoolMap() {
        return systemParameterValuePoolMap;
    }
}
