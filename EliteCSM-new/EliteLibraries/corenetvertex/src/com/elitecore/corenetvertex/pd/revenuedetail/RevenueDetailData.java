package com.elitecore.corenetvertex.pd.revenuedetail;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;


@Entity(name = "com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData")
@Table(name = "TBLM_REVENUE_DETAIL")
public class RevenueDetailData extends DefaultGroupResourceData implements Serializable {
    private String name;
    private String description;

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    @Transient
    public String getResourceName() {
        return name;
    }

    @Transient
    @JsonIgnore
    public String getRevenueDetailId() {
        return super.getId();
    }

    public void setRevenueDetailId(String revenueDetailId) {
        super.setId(revenueDetailId);
    }

    @Override
    public JsonObject toJson() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME, name);
        jsonObject.addProperty(FieldValueConstants.ALIAS, getId());
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
        jsonObject.addProperty(FieldValueConstants.GROUPS, getGroups());
        return jsonObject;
    }
}
