package com.elitecore.netvertexsm.datamanager.servermgr.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author dhavalraval
 *
 */
public class NetConfigurationParameterData implements INetConfigurationParameterData{


    private String parameterId;
    private String configId;
    private int serialNo;
    private String name;
    private String displayName;
    private String alias;
    private String description;
    private String type;
    private String regExp;
    private int maxInstances;
    private String multipleInstances;
    private String parentParameterId;
    private String defaultValue;
    private int maxLength;
    private String editable;
    private String startUpMode;	
    private String status;
    private String isNotNull;


    private NetConfigurationParameterDataKey parameterKey;

    private INetConfigurationData  netConfiguration;

    @Deprecated
    private INetConfigurationParameterData netConfigurationParameter;


    private Set<INetConfigurationParameterData> netConfigChildParameters = new HashSet<INetConfigurationParameterData>();
    private Set<INetConfigurationValuesData> netConfigParamValues = new HashSet<INetConfigurationValuesData>();
    private Set<INetConfigParamValuePoolData> netConfigParamValuePool = new HashSet<INetConfigParamValuePoolData>();


    public String getEditable() {
        return editable;
    }
    public void setEditable(String editable) {
        this.editable = editable;
    }
    public Set<INetConfigParamValuePoolData> getNetConfigParamValuePool() {
        return netConfigParamValuePool;
    }
    public void setNetConfigParamValuePool(Set<INetConfigParamValuePoolData> netConfigParamValuePool) {
        this.netConfigParamValuePool = netConfigParamValuePool;
    }
    public int getMaxLength() {
        return maxLength;
    }
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
    public String getParameterId() {
        return parameterId;
    }
    public void setParameterId(String parameterId) {
        this.parameterId = parameterId;
    }
    public String getConfigId() {
        return configId;
    }
    public void setConfigId(String configId) {
        this.configId = configId;
    }
    public int getSerialNo() {
        return serialNo;
    }
    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getRegExp() {
        return regExp;
    }
    public void setRegExp(String regExp) {
        this.regExp = regExp;
    }
    public int getMaxInstances() {
        return maxInstances;
    }
    public void setMaxInstances(int maxInstances) {
        this.maxInstances = maxInstances;
    }
    public String getMultipleInstances() {
        return multipleInstances;
    }
    public void setMultipleInstances(String multipleInstances) {
        this.multipleInstances = multipleInstances;
    }
    public String getParentParameterId() {
        return parentParameterId;
    }
    public void setParentParameterId(String parentParameterId) {
        this.parentParameterId = parentParameterId;
    }
    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    public INetConfigurationData getNetConfiguration() {
        return netConfiguration;
    }
    public void setNetConfiguration(INetConfigurationData netConfiguration) {
        this.netConfiguration = netConfiguration;
    }
    public Set<INetConfigurationParameterData> getNetConfigChildParameters() {
        return netConfigChildParameters;
    }
    public void setNetConfigChildParameters(Set<INetConfigurationParameterData> netConfigChildParameters) {
        this.netConfigChildParameters = netConfigChildParameters;
    }
    public Set<INetConfigurationValuesData> getNetConfigParamValues() {
        return netConfigParamValues;
    }
    public void setNetConfigParamValues(Set<INetConfigurationValuesData> netConfigParamValues) {
        this.netConfigParamValues = netConfigParamValues;
    }

    @Deprecated
    public INetConfigurationParameterData getNetConfigurationParameter() {
        return netConfigurationParameter;
    }

    @Deprecated
    public void setNetConfigurationParameter(INetConfigurationParameterData netConfigurationParameter) {
        this.netConfigurationParameter = netConfigurationParameter;
    }

    public String getStartUpMode() {
        return startUpMode;
    }
    public void setStartUpMode(String startUpMode) {
        this.startUpMode = startUpMode;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public NetConfigurationParameterDataKey getParameterKey( ) {
        return parameterKey;
    }

    public void setParameterKey( NetConfigurationParameterDataKey parameterKey ) {
        this.parameterKey = parameterKey;
    }

    public String getIsNotNull() {
        return isNotNull;
    }

    public void setIsNotNull(String isNotNull) {

        this.isNotNull = isNotNull;

    }

    @Override
    public String toString() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println();
        writer.println("------------NetConfigurationParameterData-----------------");
        writer.println("parameterId=" +parameterId); 
        writer.println("configId=" +configId); 
        writer.println("serialNo=" +serialNo); 
        writer.println("name=" +name); 
        writer.println("displayName=" +displayName); 
        writer.println("alias=" +alias); 
        writer.println("description=" +description); 
        writer.println("type=" +type); 
        writer.println("regExp=" +regExp); 
        writer.println("maxInstances=" +maxInstances); 
        writer.println("multipleInstances=" +multipleInstances); 
        writer.println("parentParameterId=" +parentParameterId); 
        writer.println("defaultValue=" +defaultValue); 
        writer.println("maxLength=" +maxLength); 
        writer.println("editable=" +editable); 
        writer.println("startUpMode=" +startUpMode); 
        writer.println("status=" +status); 
        writer.println("----------------------------------------------------");
        writer.close();
        return out.toString();
    }



}
