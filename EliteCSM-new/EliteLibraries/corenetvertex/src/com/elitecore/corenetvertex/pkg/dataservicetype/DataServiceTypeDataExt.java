package com.elitecore.corenetvertex.pkg.dataservicetype;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.core.validator.DataServiceTypeValidatorExt;
import com.elitecore.corenetvertex.core.validator.DefaultServiceDataFlowValidator;
import com.elitecore.corenetvertex.core.validator.RatingGroupValidator;
import com.elitecore.corenetvertex.pkg.importpkg.DataServiceTypeImportOperation;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.LongToStringAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * A specific class that is been used for import-export operation of DataServiceTypeData
 * Created by Ishani on 14/9/16.
 */

@Entity
@Table(name = "TBLM_DATA_SERVICE_TYPE")
@Import(required = true, validatorClass = DataServiceTypeValidatorExt.class, importClass = DataServiceTypeImportOperation.class)
@XmlRootElement(name="dataServiceType")
public class DataServiceTypeDataExt extends ResourceData implements Serializable,Cloneable {

    private static final String MODULE = DataServiceTypeDataExt.class.getSimpleName();
    private static final long serialVersionUID = 1L;
    private String name;
    private transient String description;
    private Long serviceIdentifier;
    private List<DefaultServiceDataFlowExt> defaultServiceDataFlows;
    private List<RatingGroupData> ratingGroupDatas;
    private String [] ratingGroupIds;


    public DataServiceTypeDataExt(){
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
    public void setStatus(String status) {
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
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlJavaTypeAdapter(LongToStringAdapter.class)
    @Column(name = "SERVICE_IDENTIFIER")
    public Long getServiceIdentifier() {
        return serviceIdentifier;
    }

    public void setServiceIdentifier(Long serviceIdentifier) {
        this.serviceIdentifier = serviceIdentifier;
    }

    @OneToMany(cascade = { CascadeType.ALL },fetch = FetchType.LAZY, mappedBy = "dataServiceTypeExt" ,orphanRemoval = false)
    @Fetch(FetchMode.SUBSELECT)
    @Import(required = true,validatorClass = DefaultServiceDataFlowValidator.class)
    public List<DefaultServiceDataFlowExt> getDefaultServiceDataFlows() {
        return defaultServiceDataFlows;
    }

    public void setDefaultServiceDataFlows(
            List<DefaultServiceDataFlowExt> serviceDataFlows) {
        this.defaultServiceDataFlows = serviceDataFlows;
    }

    public String toString(ToStringStyle toStringStyle) {
        return new ToStringBuilder(this, toStringStyle)
                .append("Name", name)
                .append("Service Identifier", serviceIdentifier).toString();
    }

    @Transient
    @XmlTransient
    public String[] getRatingGroupIds() {
        try{
            ratingGroupIds = new String[this.getRatingGroupDatas().size()];
            int i = 0;
            for(RatingGroupData ratingGroupData : this.getRatingGroupDatas()){
                if(ratingGroupIds != null && ratingGroupIds.length>0){
                    ratingGroupIds[i] = ",";
                }
                ratingGroupIds[i] = ratingGroupData.getId();
                i++;
            }
        }catch(Exception e){
            getLogger().error(MODULE, "Failed to get rating Groups for data service type. Reason: "+e.getMessage());
            getLogger().trace(e);
        }
        return ratingGroupIds;
    }

    public void setRatingGroupIds(String[] ratingGroupIds) {
        this.ratingGroupIds = ratingGroupIds;
    }

    @Transient
    @XmlTransient
    @Override
    public String getHierarchy() {
        return getId() + "<br>" + name;
    }

    @Override
    @Column(name="GROUPS")
    public String getGroups() {
        return super.getGroups();
    }

    @Override
    public void setGroups(String groups) {
        super.setGroups(groups);
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "TBLM_DATA_SRV_RG_REL", joinColumns = { @JoinColumn(name = "DATA_SERVICE_TYPE_ID", nullable = false) },
            inverseJoinColumns = { @JoinColumn(name = "RATING_GROUP_ID", nullable = false) })
    @Fetch(FetchMode.SUBSELECT)
    @Where(clause="STATUS != 'DELETED'")
    @XmlElementWrapper(name="ratingGroups")
    @XmlElement(name="ratingGroup")
    @Import(required = true, validatorClass = RatingGroupValidator.class)
    public List<RatingGroupData> getRatingGroupDatas() {
        return ratingGroupDatas;
    }

    public void setRatingGroupDatas(List<RatingGroupData> ratingGroupDatas) {
        this.ratingGroupDatas = ratingGroupDatas;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME,name);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
        jsonObject.addProperty(FieldValueConstants.SERVICE_IDENTIFIER, serviceIdentifier);

        if(ratingGroupDatas != null){
            JsonArray ratingGroupJsonArray = new JsonArray();

            for(RatingGroupData ratingGroupData : ratingGroupDatas){
                JsonObject object = new JsonObject();
                object.addProperty(FieldValueConstants.NAME, ratingGroupData.getName());
                ratingGroupJsonArray.add(object);
            }
            jsonObject.add(FieldValueConstants.RATING_GROUPS, ratingGroupJsonArray);
        }

        if(defaultServiceDataFlows != null){
            JsonArray defServiceDataFlowsJsonArray = new JsonArray();
            for(DefaultServiceDataFlowExt defaultServiceDataFlowData : defaultServiceDataFlows){
                defServiceDataFlowsJsonArray.add(defaultServiceDataFlowData.toJson());
            }
            jsonObject.add(FieldValueConstants.DEFAULT_SERVICE_DATA_FLOW, defServiceDataFlowsJsonArray);
        }

        return jsonObject;
    }
    @Override
    @Transient
    public String getResourceName() {
        return getName();
    }
}

