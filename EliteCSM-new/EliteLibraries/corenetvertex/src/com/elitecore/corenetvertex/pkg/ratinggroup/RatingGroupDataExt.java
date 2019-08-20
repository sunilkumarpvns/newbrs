package com.elitecore.corenetvertex.pkg.ratinggroup;

import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.core.validator.RatingGroupValidatorExt;
import com.elitecore.corenetvertex.pkg.importpkg.RatingGroupImportOperation;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.LongToStringAdapter;
import com.google.gson.JsonObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;

/**
 * A class that will be exposed in Rating Group import web-service
 * Created by Ishani on 21/9/16.
 */
@Entity
@Table(name = "TBLM_RATING_GROUP")
@Import(required = true, validatorClass = RatingGroupValidatorExt.class, importClass = RatingGroupImportOperation.class)
@XmlRootElement(name="ratingGroup")
public class RatingGroupDataExt extends ResourceData implements Serializable {


        private static final long serialVersionUID = 1L;
        private String name;
        private String description;
        private Long identifier;

        @Column(name = "NAME")
        public String getName() {
            return name;
        }

    @Override
    @Transient
    public String getHierarchy() {
        return getId() + "<br>" + getName();
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

        @Column(name = "DESCRIPTION")
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Column(name = "IDENTIFIER", updatable = false)
        @XmlJavaTypeAdapter(LongToStringAdapter.class)
        public Long getIdentifier() {
            return identifier;
        }

        public void setIdentifier(Long identifier) {
            this.identifier = identifier;
        }

        @Transient
        public String getNameAndIdentifier(){
            return name + "(" + identifier+")";

        }


        @Override
        @Column(name="GROUPS")
        public String getGroups() {
            return super.getGroups();
        }


        @Override
        public JsonObject toJson() {
            return null;
        }

        @Override
        public String toString(){
            ToStringBuilder toStringBuilder = new ToStringBuilder(this,
                    ToStringStyle.CUSTOM_TO_STRING_STYLE).append("Name", name)
                    .append("Descritption", description)
                    .append("Status", getStatus())
                    .append("Identifier",identifier);
            return toStringBuilder.toString();
        }

    @Override
    @Transient
    public String getResourceName() {
        return getName();
    }

    }

