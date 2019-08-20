/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   BaseUpdateConfigurationForm.java                            
 * ModualName com.elitecore.elitesm.web.servermgr                                      
 * Created on May 1, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.web.servermgr;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;
import com.elitecore.elitesm.web.servermgr.server.forms.NetConfParameterValueBean;

public abstract class BaseUpdateConfigurationForm extends BaseWebForm {

    protected List<NetConfParameterValueBean> lstParameterValue = new ArrayList<NetConfParameterValueBean>();

    public List<NetConfParameterValueBean> getLstParameterValue() {
        return lstParameterValue;
    }

    public void setLstParameterValue(List<NetConfParameterValueBean> lstParameterValue) {
        this.lstParameterValue = lstParameterValue;
    }

    public String getAlias(int index) {
        return lstParameterValue.get(index).getAlias();
    }

    public void setAlias(int index, String alias) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());

        lstParameterValue.get(index).setAlias(alias);
    }

    public String getConfigInstanceId(int index) {
        return lstParameterValue.get(index).getConfigInstanceId();
    }

    public void setConfigInstanceId(int index, String configInstanceId) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());

        lstParameterValue.get(index).setConfigInstanceId(configInstanceId);
    }

    public String getDisplayName(int index) {
        return lstParameterValue.get(index).getDisplayName();
    }

    public void setDisplayName(int index, String displayName) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());

        lstParameterValue.get(index).setDisplayName(displayName);
    }

    public String getInstanceId(int index) {
        return lstParameterValue.get(index).getInstanceId();
    }

    public void setInstanceId(int index, String instanceId) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());

        lstParameterValue.get(index).setInstanceId(instanceId);
    }

    public int getMaxInstances(int index) {
        return lstParameterValue.get(index).getMaxInstances();
    }

    public void setMaxInstances(int index, int maxInstances) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());

        lstParameterValue.get(index).setMaxInstances(maxInstances);
    }

    public String getMultipleInstances(int index) {
        return lstParameterValue.get(index).getMultipleInstances();
    }

    public void setMultipleInstances(int index, String multipleInstances) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());

        lstParameterValue.get(index).setMultipleInstances(multipleInstances);
    }

    public String getName(int index) {
        return lstParameterValue.get(index).getName();
    }

    public void setName(int index, String name) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());

        lstParameterValue.get(index).setName(name);
    }

    public String getParameterId(int index) {
        return lstParameterValue.get(index).getParameterId();
    }

    public void setParameterId(int index, String parameterId) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());

        lstParameterValue.get(index).setParameterId(parameterId);
    }

    public String getParentParameterId(int index) {
        return lstParameterValue.get(index).getParentParameterId();
    }

    public void setParentParameterId(int index, String parentParameterId) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());

        lstParameterValue.get(index).setParentParameterId(parentParameterId);
    }

    public int getSerialNo(int index) {
        return lstParameterValue.get(index).getSerialNo();
    }

    public void setSerialNo(int index, int serialNo) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());

        lstParameterValue.get(index).setSerialNo(serialNo);
    }

    public String getValue(int index) {
        return lstParameterValue.get(index).getValue();
    }

    public void setValue(int index, String value) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());

        System.out.println("Value : "+value);
        lstParameterValue.get(index).setValue(value);
    }

    public String getType(int index) {
        return lstParameterValue.get(index).getType();
    }

    public void setType(int index, String type) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());

        lstParameterValue.get(index).setType(type);
    }

    public int getTotalInstance(int index) {
        return lstParameterValue.get(index).getTotalInstance();

    }

    public void setTotalInstance(int index, int totalInstance) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());
        lstParameterValue.get(index).setTotalInstance(totalInstance);
    }

    public int getCurrentInstanceNo(int index) {
        return lstParameterValue.get(index).getCurrentInstanceNo();
    }

    public void setCurrentInstanceNo(int index, int currentInstanceNo) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());
        lstParameterValue.get(index).setCurrentInstanceNo(currentInstanceNo);
    }

    public String getDescription(int index) {
        return lstParameterValue.get(index).getDescription();
    }

    public void setDescription(int index, String description) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());
        lstParameterValue.get(index).setDescription(description);
    }

    public int getMaxLength(int index) {
        return lstParameterValue.get(index).getMaxLength();
    }

    public void setMaxLength(int index, int maxLength) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());
        lstParameterValue.get(index).setMaxLength(maxLength);
    }

    public Set getNetConfigParamValuePool(int index) {
        return lstParameterValue.get(index).getNetConfigParamValuePool();
    }

    public void setNetConfigParamValuePool(int index, Set netConfigParamValuePool) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());
        lstParameterValue.get(index).setNetConfigParamValuePool(netConfigParamValuePool);
    }

    public boolean getEditable(int index) {
        return lstParameterValue.get(index).getEditable();
    }

    public void setEditable(int index, boolean editable) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());
        lstParameterValue.get(index).setEditable(editable);
    }

    public String getStartUpMode(int index) {
        return lstParameterValue.get(index).getStartUpMode();
    }

    public void setStartUpMode(int index, String startUpMode) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());
        lstParameterValue.get(index).setStartUpMode(startUpMode);
    }

    public String getStatus(int index) {
        return lstParameterValue.get(index).getStatus();
    }

    public void setStatus(int index, String status) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());
        lstParameterValue.get(index).setStatus(status);
    }

    public String getRegexp(int index) {
        return lstParameterValue.get(index).getRegexp();
    }

    public void setRegexp(int index, String regexp) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());
        lstParameterValue.get(index).setRegexp(regexp);
    }

    public String getConfigId(int index) {
        return lstParameterValue.get(index).getConfigId();
    }

    public void setConfigId(int index, String configId) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());
        lstParameterValue.get(index).setConfigId(configId);
    }

    public boolean getIsNotNull(int index) {
        return lstParameterValue.get(index).isNotNull();
    }

    public void setIsNotNull(int index, boolean isNotNull) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());
        lstParameterValue.get(index).setNotNull(isNotNull);
    }

    public String getPoolExists(int index) {
        return lstParameterValue.get(index).getPoolExists();
    }

    public void setPoolExists(int index, String poolExists) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());
        lstParameterValue.get(index).setPoolExists(poolExists);
    }

    public boolean getIsRemoved(int index) {
        return lstParameterValue.get(index).isRemoved();
    }

    public void setIsRemoved(int index, boolean isRemoved) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());
        lstParameterValue.get(index).setRemoved(isRemoved);
    }


    public boolean getIsNewAdded(int index) {
        return lstParameterValue.get(index).isNewAdded();
    }

    public void setIsNewAdded(int index, boolean isNewAdded) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());
        lstParameterValue.get(index).setNewAdded(isNewAdded);
    }

    public String getParameterValueId(int index) {
        return lstParameterValue.get(index).getParameterValueId();
    }

    public void setParameterValueId(int index, String parameterValueId) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());
        lstParameterValue.get(index).setParameterValueId(parameterValueId);
    }
    
    public String getStartDivStatus(int index) {
        return lstParameterValue.get(index).getStartDivStatus();
    }

    public void setStartDivStatus(int index, String startDivStatus) {
        while(lstParameterValue.size() - 1 < index)
            lstParameterValue.add(new NetConfParameterValueBean());
        lstParameterValue.get(index).setStartDivStatus(startDivStatus);
    }
 
    
    
}
