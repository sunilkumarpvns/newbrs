package com.elitecore.corenetvertex.sm.ddf;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.elitecore.corenetvertex.sm.spr.SprData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * Used to manage DDF operations
 * @author dhyani.raval
 */
@Entity(name = "com.elitecore.corenetvertex.sm.ddf.DdfData")
@Table(name = "TBLM_DDF_TABLE")
public class DdfData extends DefaultGroupResourceData implements Serializable {

    private SprData defaultSprData;
    private String stripPrefixes;
    private List<DdfSprRelData> ddfSprRelDatas;

    public DdfData(){
        this.ddfSprRelDatas = Collectionz.newArrayList();
    }

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "DEFAULT_SPR_ID")
    public SprData getDefaultSprData() {
        return defaultSprData;
    }

    public void setDefaultSprData(SprData defaultSprData) {
        this.defaultSprData = defaultSprData;
    }

    @Column(name = "STRIP_PREFIXES")
    public String getStripPrefixes() {
        return stripPrefixes;
    }

    public void setStripPrefixes(String stripPrefixes) {
        this.stripPrefixes = stripPrefixes;
    }

    @OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "DDF_TABLE_ID")
    public List<DdfSprRelData> getDdfSprRelDatas() {
        return ddfSprRelDatas;
    }

    public void setDdfSprRelDatas(List<DdfSprRelData> ddfSprRelDatas) {
        this.ddfSprRelDatas = ddfSprRelDatas;
    }

    @Transient
    @Override
    public String getResourceName() {
        return "DDF";
    }

    @Transient
    public String getDefaultSprDataId() {
        if(this.getDefaultSprData()!=null){
            return getDefaultSprData().getId();
        }
        return null;
    }

    public void setDefaultSprDataId(String defaultSprDataId) {
        if(Strings.isNullOrBlank(defaultSprDataId) == false){
            SprData defaultSprData = new SprData();
            defaultSprData.setId(defaultSprDataId);
            this.defaultSprData = defaultSprData;
        }


    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        if(defaultSprData != null) {
            jsonObject.addProperty(FieldValueConstants.DEFAULT_SPR, defaultSprData.getName());
        }
        jsonObject.addProperty(FieldValueConstants.STRIP_PREFIXES,stripPrefixes);
        if(Collectionz.isNullOrEmpty(ddfSprRelDatas) == false) {
            JsonObject jsonObjectOfChild = new JsonObject();
            for(DdfSprRelData ddfSprRelData : ddfSprRelDatas){
                if(ddfSprRelData != null) {
                    jsonObjectOfChild.add(ddfSprRelData.getIdentityPattern(), ddfSprRelData.toJson());
                }
            }

            jsonObject.add("DDF Entries", jsonObjectOfChild);
        }
        return jsonObject;
    }
}
