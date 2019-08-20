package com.elitecore.corenetvertex.pd.ratinggroup;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pd.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang.SystemUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
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
@Entity(name = "com.elitecore.corenetvertex.pd.ratinggroup.RatingGroupData")
@Table(name = "TBLM_RATING_GROUP")
public class RatingGroupData extends ResourceData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private String description;
    private Long identifier;
    private transient List<DataServiceTypeData> dataServiceTypeData;
    private static final ToStringStyle RATING_GROUP_TO_STRING_STYLE = new RatingGroupDataToString();
    private static final String MODULE = RatingGroupData.class.getSimpleName();

    public RatingGroupData() {
        dataServiceTypeData = Collectionz.newArrayList();
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Column(name = "STATUS")
    @XmlTransient
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    public void setStatus(String status) {//NOSONAR-- REQUIRED FOR IMPORT/EXPORT
        super.setStatus(status);
    }

    @Column(name = "DESCRIPTION")
    @XmlTransient
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "IDENTIFIER")
    @XmlTransient
    public Long getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Long identifier) {
        this.identifier = identifier;
    }

    @Transient
    @XmlTransient
    public String getNameAndIdentifier() {
        return name + "(" + identifier + ")";
    }

    @Override
    @Column(name = "GROUPS")
    public String getGroups() {
        return super.getGroups();
    }

    @Override
    public void setGroups(String groups) {//NOSONAR -- REQUIRED FOR IMPORT/EXPORT
        super.setGroups(groups);
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @OrderBy("name ASC")
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "TBLM_DATA_SRV_RG_REL", joinColumns = {@JoinColumn(name = "RATING_GROUP_ID", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "DATA_SERVICE_TYPE_ID", nullable = false)})
    @XmlTransient
    public List<DataServiceTypeData> getDataServiceTypeData() {
        return dataServiceTypeData;
    }

    public void setDataServiceTypeData(List<DataServiceTypeData> dataServiceTypeData) {
        this.dataServiceTypeData = dataServiceTypeData;
    }

    @Transient
    public List<String> getDataServiceTypeIds() {
        if (Collectionz.isNullOrEmpty(dataServiceTypeData) == false) {
            return this.dataServiceTypeData.stream().map(DataServiceTypeData::getId).collect(Collectors.toList());
        }
        return null;
    }

    public void setDataServiceTypeIds(List<String> dataServiceTypeIds) {
        if (Collectionz.isNullOrEmpty(dataServiceTypeIds) == false) {
            dataServiceTypeIds.forEach(dataServiceTypeId -> {
                DataServiceTypeData dataServiceType = new DataServiceTypeData();
                dataServiceType.setId(dataServiceTypeId);
                this.dataServiceTypeData.add(dataServiceType);
            });
        }
    }

    public RatingGroupData deepClone() throws CloneNotSupportedException {
        RatingGroupData newData = (RatingGroupData) this.clone();
        newData.dataServiceTypeData = Collectionz.newArrayList();
        if (dataServiceTypeData.isEmpty() == false) {
            for (DataServiceTypeData serviceType : dataServiceTypeData) {
                DataServiceTypeData clonedDataServiceTypeData = serviceType.deepClone();
                List<RatingGroupData> ratingGroupDataList = Collectionz.newArrayList();
                ratingGroupDataList.add(newData);
                clonedDataServiceTypeData.setRatingGroupDatas(ratingGroupDataList);
                newData.dataServiceTypeData.add(clonedDataServiceTypeData);
            }
        }
        return newData;
    }

    @Transient
    @XmlTransient
    @Override
    public String getHierarchy() {
        return getId() + "<br>" + name;
    }


    public String toString(ToStringStyle toStringStyle) {
        return new ToStringBuilder(this, toStringStyle)
                .append("Name", name)
                .append("Identifier", identifier).toString();
    }

    @Transient
    @XmlTransient
    public String getServiceTypeDataInJsonString() {
        try {
            Gson gson = GsonFactory.defaultInstance();
            return gson.toJsonTree(getDataServiceTypeData()).getAsJsonArray().toString();
        } catch (Exception e) {
            getLogger().error(MODULE, "Failed to get DataServiceType Rating Gorup Relaton for rating group. Reason: " + e.getMessage());
            getLogger().trace(e);
            return "";
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME, name);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
        jsonObject.addProperty(FieldValueConstants.GROUPS, getGroups());
        jsonObject.addProperty(FieldValueConstants.IDENTIFIER, identifier);
        return jsonObject;
    }

    @Override
    public String toString() {
        return toString(RATING_GROUP_TO_STRING_STYLE);
    }


    private static final class RatingGroupDataToString extends ToStringStyle.CustomToStringStyle {
        private static final long serialVersionUID = 1L;

        RatingGroupDataToString() {
            super();
            this.setContentStart(SystemUtils.LINE_SEPARATOR);
            this.setContentEnd("");
            this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(10)
                    + getTabs(4));
        }
    }

    @Override
    @Transient
    @XmlTransient
    public String getResourceName() {
        return getName();
    }
}
