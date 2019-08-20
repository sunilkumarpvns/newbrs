package com.elitecore.corenetvertex.pd.dataservicetype;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pd.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang.SystemUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * @author jaidiptrivedi
 */
@Entity(name = "com.elitecore.corenetvertex.pd.dataservicetype.DataServiceTypeData")
@Table(name = "TBLM_DATA_SERVICE_TYPE")
public class DataServiceTypeData extends DefaultGroupResourceData implements Serializable {

    private static final String MODULE = DataServiceTypeData.class.getSimpleName();
    private static final long serialVersionUID = 1L;
    private static final ToStringStyle DATA_SERVICE_TYPE_TO_STRING_STYLE = new DataServiceTypeDataToString();
    private String name;
    private String description;
    private Long serviceIdentifier;
    private List<DefaultServiceDataFlowData> defaultServiceDataFlows;
    private List<RatingGroupData> ratingGroupDatas;

    public DataServiceTypeData() {
        ratingGroupDatas = Collectionz.newArrayList();
        defaultServiceDataFlows = Collectionz.newArrayList();
    }

    @Column(name = "STATUS")
    @XmlTransient
    @Override
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    public void setStatus(String status) { //NOSONAR-- REQUIRED FOR IMPORT/EXPORT
        super.setStatus(status);
    }


    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION")
    @XmlTransient
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "SERVICE_IDENTIFIER")
    @XmlTransient
    public Long getServiceIdentifier() {
        return serviceIdentifier;
    }

    public void setServiceIdentifier(Long serviceIdentifier) {
        this.serviceIdentifier = serviceIdentifier;
    }

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "dataServiceTypeData", orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @XmlTransient
    public List<DefaultServiceDataFlowData> getDefaultServiceDataFlows() {
        return defaultServiceDataFlows;
    }

    public void setDefaultServiceDataFlows(
            List<DefaultServiceDataFlowData> serviceDataFlows) {
        this.defaultServiceDataFlows = serviceDataFlows;
    }

    public String toString(ToStringStyle toStringStyle) {
        return new ToStringBuilder(this, toStringStyle)
                .append("Name", name)
                .append("Service Identifier", serviceIdentifier).toString();
    }

    @Override
    public String toString() {
        return toString(DATA_SERVICE_TYPE_TO_STRING_STYLE);
    }


    private static final class DataServiceTypeDataToString extends ToStringStyle.CustomToStringStyle {
        private static final long serialVersionUID = 1L;

        DataServiceTypeDataToString() {
            super();
            this.setContentStart(SystemUtils.LINE_SEPARATOR);
            this.setContentEnd("");
            this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(10)
                    + getTabs(4));
        }
    }


    @Transient
    public List<String> getRatingGroupIds() {
        if (Collectionz.isNullOrEmpty(ratingGroupDatas) == false) {
            return this.ratingGroupDatas.stream().map(RatingGroupData::getId).collect(Collectors.toList());
        }
        return null;
    }

    public void setRatingGroupIds(List<String> ratingGroupIds) {
        if (Collectionz.isNullOrEmpty(ratingGroupIds) == false) {
            ratingGroupIds.forEach(ratingGroupId -> {
                RatingGroupData ratingGroupData = new RatingGroupData();
                ratingGroupData.setId(ratingGroupId);
                this.ratingGroupDatas.add(ratingGroupData);
            });
        }
    }


    @Transient
    @XmlTransient
    @JsonIgnore
    public String getDefaultServiceDataFlowsInJsonString() {
        try {
            Gson gson = GsonFactory.defaultInstance();
            return gson.toJsonTree(this.getDefaultServiceDataFlows()).getAsJsonArray().toString();
        } catch (Exception e) {
            getLogger().error(MODULE, "Failed to get DefaultServiceDataFlows for data service type. Reason: " + e.getMessage());
            getLogger().trace(e);
            return "";
        }
    }

    @Transient
    @JsonIgnore
    public String getRatingDataInJsonString() {
        try {
            Gson gson = GsonFactory.defaultInstance();
            return gson.toJsonTree(getRatingGroupDatas()).getAsJsonArray().toString();
        } catch (Exception e) {
            getLogger().error(MODULE, "Failed to get DataServiceType Rating Gorup Relaton for data service type. Reason: " + e.getMessage());
            getLogger().trace(e);
            return "";
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME, name);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
        jsonObject.addProperty(FieldValueConstants.SERVICE_IDENTIFIER, serviceIdentifier);


        if (ratingGroupDatas != null) {
            JsonObject ratingGroupJsonArray = new JsonObject();

            for (RatingGroupData ratingGroupData : ratingGroupDatas) {
                ratingGroupJsonArray.addProperty(ratingGroupData.getName(), CommonConstants.ENABLED);
            }
            jsonObject.add(FieldValueConstants.RATING_GROUPS, ratingGroupJsonArray);
        }


        if (defaultServiceDataFlows != null) {
            JsonArray defServiceDataFlowsJsonArray = new JsonArray();
            for (DefaultServiceDataFlowData defaultServiceDataFlowData : defaultServiceDataFlows) {
                defServiceDataFlowsJsonArray.add(defaultServiceDataFlowData.toJson());
            }
            jsonObject.add(FieldValueConstants.DEFAULT_SERVICE_DATA_FLOW, defServiceDataFlowsJsonArray);
        }

        return jsonObject;
    }

    @Transient
    @XmlTransient
    @JsonIgnore
    @Override
    public String getHierarchy() {
        return getId() + "<br>" + name;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "TBLM_DATA_SRV_RG_REL", joinColumns = {@JoinColumn(name = "DATA_SERVICE_TYPE_ID", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "RATING_GROUP_ID", nullable = false)})
    @Fetch(FetchMode.SUBSELECT)
    @XmlTransient
    @JsonIgnore
    public List<RatingGroupData> getRatingGroupDatas() {
        return ratingGroupDatas;
    }

    public void setRatingGroupDatas(List<RatingGroupData> ratingGroupDatas) {
        this.ratingGroupDatas = ratingGroupDatas;
    }

    public DataServiceTypeData deepClone() throws CloneNotSupportedException {
        DataServiceTypeData newData = (DataServiceTypeData) super.clone();
        newData.defaultServiceDataFlows = Collectionz.newArrayList();
        if (defaultServiceDataFlows.isEmpty() == false) {
            for (DefaultServiceDataFlowData serviceDataFlow : defaultServiceDataFlows) {
                DefaultServiceDataFlowData clonedServiceDataFlow = serviceDataFlow.deepClone();
                clonedServiceDataFlow.setDataServiceTypeData(newData);
                newData.defaultServiceDataFlows.add(clonedServiceDataFlow);
            }
        }

        newData.ratingGroupDatas = Collectionz.newArrayList();
        if (ratingGroupDatas.isEmpty() == false) {
            for (RatingGroupData ratingGroupData : ratingGroupDatas) {
                RatingGroupData clonedRatingGroupData = ratingGroupData.deepClone();
                List<DataServiceTypeData> dataServiceTypeList = Collectionz.newArrayList();
                dataServiceTypeList.add(newData);
                clonedRatingGroupData.setDataServiceTypeData(dataServiceTypeList);
                newData.ratingGroupDatas.add(clonedRatingGroupData);
            }
        }

        newData.description = description;
        return newData;
    }

    @Override
    @Transient
    public String getResourceName() {
        return getName();
    }

}
