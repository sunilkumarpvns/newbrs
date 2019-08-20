package com.elitecore.corenetvertex.sm.ddf;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.spr.SprData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Used to manage SPR-DDF Relation
 *@author dhyani.raval
 */

@Entity(name = "com.elitecore.corenetvertex.sm.ddf.DdfSprRelData")
@Table(name = "TBLM_DDF_SPR_REL")
public class DdfSprRelData implements Serializable {

    private String id;
    private String identityPattern;
    private SprData sprData;
    private Integer orderNo;

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "IDENTITY_PATTERN")
    public String getIdentityPattern() {
        return identityPattern;
    }

    public void setIdentityPattern(String identityPattern) {
        this.identityPattern = identityPattern;
    }

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "SPR_ID")
    public SprData getSprData() {
        return sprData;
    }

    public void setSprData(SprData sprData) {
        this.sprData = sprData;
    }

    @Column(name = "ORDER_NO")
    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    @Transient
    public String getSprDataId() {
        if(this.getSprData()!=null) {
            return getSprData().getId();
        }
        return null;
    }

    public void setSprDataId(String sprDataId) {
        if(Strings.isNullOrBlank(sprDataId) == false) {
            SprData sprData = new SprData();
            sprData.setId(sprDataId);
            this.sprData = sprData;
        }
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.IDENTITY_PATTERN,identityPattern);
        if(sprData != null) {
            jsonObject.addProperty(FieldValueConstants.SPR, sprData.getName());
        }
        jsonObject.addProperty(FieldValueConstants.ORDER_NO ,orderNo);
        return jsonObject;
    }

}
