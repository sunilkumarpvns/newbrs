package com.elitecore.corenetvertex.sm.routing.mccmncgroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.elitecore.corenetvertex.sm.routing.network.BrandData;
import com.elitecore.corenetvertex.sm.routing.network.NetworkData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

@Entity(name = "com.elitecore.corenetvertex.sm.routing.mccmncgroup.MccMncGroupData")
@Table(name = "TBLM_MCC_MNC_GROUP")
public class MccMncGroupData extends DefaultGroupResourceData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private String description;
    private BrandData brandData;
    private List<NetworkData> networkDatas = new ArrayList<NetworkData>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name = "TBLM_MCC_MNC_CODE_GROUP_REL", joinColumns = {@JoinColumn(name = "MCC_MNC_GROUP_ID")}, inverseJoinColumns = {@JoinColumn(name = "NETWORK_ID")})
    @JsonIgnore
    public List<NetworkData> getNetworkDatas() {
        return networkDatas;
    }

    public void setNetworkDatas(List<NetworkData> networkDatas) {
        this.networkDatas = networkDatas;
    }

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToOne
    @JoinColumn(name = "BRAND_ID", nullable = false)
    @JsonIgnore
    public BrandData getBrandData() {
        return brandData;
    }

    public void setBrandData(BrandData brandData) {
        this.brandData = brandData;
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Transient
    public String getResourceName() {
        return name;
    }

    @Override
    @Column(name = "STATUS")
    @JsonIgnore
    public String getStatus() {
        return super.getStatus();
    }

    @Transient
    public String getBrandId() {
        if (this.getBrandData() != null) {
            return getBrandData().getId();
        }
        return null;
    }

    public void setBrandId(String brandId) {
        if (Strings.isNullOrBlank(brandId) == false) {
            BrandData brandData = new BrandData();
            brandData.setId(brandId);
            this.brandData = brandData;
        }
    }

    @Transient
    public List<String> getNetworkIds() {
        if (Collectionz.isNullOrEmpty(networkDatas) == false) {
            return this.networkDatas.stream().map(NetworkData::getId).collect(Collectors.toList());
        }
        return null;
    }

    public void setNetworkIds(List<String> networkIds) {
        if (Collectionz.isNullOrEmpty(networkIds) == false) {
            networkIds.forEach(networkId -> {
                NetworkData networkData = new NetworkData();
                networkData.setId(networkId);
                this.networkDatas.add(networkData);
            });
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME, name);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
        jsonObject.addProperty(FieldValueConstants.BRAND_DATA, brandData.getName());
        if(Collectionz.isNullOrEmpty(networkDatas) == false){
            for(NetworkData networkData : networkDatas){
                jsonObject.add(networkData.getName(), networkData.toJson());
            }
        }
        return jsonObject;
    }
}