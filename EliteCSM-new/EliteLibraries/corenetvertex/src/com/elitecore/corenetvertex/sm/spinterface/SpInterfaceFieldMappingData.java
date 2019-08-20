package com.elitecore.corenetvertex.sm.spinterface;

import com.elitecore.corenetvertex.spr.data.SPRFields;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Common field mapping class of DB and LDAP Sp Interface
 * @author dhyani.raval
 */
@Entity (name = "com.elitecore.corenetvertex.sm.spinterface.SpInterfaceFieldMappingData")
@Table (name = "TBLM_SP_INTERFACE_FIELD_MAP")
public class SpInterfaceFieldMappingData implements Serializable{

    private String id;
    private String logicalName;
    private String fieldName;
    private String logicalNameDisplayValue;

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "LOGICAL_NAME")
    public String getLogicalName() {
        return logicalName;
    }

    public void setLogicalName(String logicalName) {
        setLogicalNameDisplayValue(logicalName);
        this.logicalName = logicalName;
    }

    @Column(name = "FIELD_NAME")
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Transient
    @JsonIgnore
    public String getLogicalNameDisplayValue() {
        return logicalNameDisplayValue;
    }

    public void setLogicalNameDisplayValue(String logicalNameDisplayValue) {
        this.logicalNameDisplayValue = SPRFields.fromSPRFields(logicalNameDisplayValue).displayName;
    }
}
