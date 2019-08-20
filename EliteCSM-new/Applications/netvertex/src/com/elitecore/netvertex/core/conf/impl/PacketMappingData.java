package com.elitecore.netvertex.core.conf.impl;

import java.util.ArrayList;
import java.util.List;

public class PacketMappingData {

    private String id;
    private String pid;
    private String policykey;
    private String attribute;
    private String defaultvalue;
    private String valuemapping;

    private List<PacketMappingData> childMappings;

    public PacketMappingData() {
        this.childMappings = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return pid;
    }

    public void setParentId(String pid) {
        this.pid = pid;
    }

    public String getPolicyKey() {
        return policykey;
    }

    public void setPolicyKey(String policyKey) {
        this.policykey = policyKey;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getDefaultValue() {
        return defaultvalue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultvalue = defaultValue;
    }

    public String getValueMapping() {
        return valuemapping;
    }

    public void setValueMapping(String valueMapping) {
        this.valuemapping = valueMapping;
    }

    public void addChildMapping(List<PacketMappingData> packetMappingDatas) {
        this.childMappings.addAll(packetMappingDatas);
    }

    public void addChildMapping(PacketMappingData packetMappingData) {
        this.childMappings.add(packetMappingData);
    }

    public List<PacketMappingData> getChildMappings() {
        return childMappings;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PacketMappingData other = (PacketMappingData) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }


    @Override
    public String toString() {
        return "PacketMappingData{" +
                "id='" + id + '\'' +
                ", pid='" + pid + '\'' +
                ", policyKey='" + policykey + '\'' +
                ", attribute='" + attribute + '\'' +
                ", defaultValue='" + defaultvalue + '\'' +
                ", valueMapping='" + valuemapping + '\'' +
                '}';
    }

    public boolean childPolicyKeyContains(String key) {

        for (PacketMappingData childMapping : childMappings) {
            if (childMapping.getPolicyKey() == null) {
                if (childMapping.childPolicyKeyContains(key)) {
                    return true;
                }
            } else if (childMapping.getPolicyKey().equals(key)) {
                return true;
            }
        }

        return false;
    }
}
