package com.elitecore.corenetvertex.sm.systemparameter;

/**
 * @author jaidiptrivedi
 */

import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonIgnore;

@Entity(name = "com.elitecore.corenetvertex.sm.systemparameter.SystemParameterData")
@Table(name = "TBLM_SYSTEM_PARAMETER")
public class SystemParameterData implements Serializable{

    private String name;
    private String description;
    private String alias;
    private String value;
    private transient String displayValue;

    public SystemParameterData() {
    }

    public SystemParameterData(String name, String description, String alias, String value) {
        this.name = name;
        this.description = description;
        this.alias = alias;
        this.value = value;
    }

    @Id
    @Column(name = "ALIAS")
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Column(name = "VALUE")
    public String getValue() {
        return value != null ? value : "";
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Transient
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Transient
    @XmlTransient
    @JsonIgnore
    public String getDisplayValue() {
        if(StringUtils.isBlank(displayValue)){
            getValue();
        }
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        SystemParameter systemParameter = SystemParameter.fromName(alias);
        if(systemParameter != null) {
                jsonObject.addProperty(systemParameter.getName(), value);
        }else{
            PackageParameter packageParameter = PackageParameter.fromName(alias);
            if(packageParameter != null) {
                jsonObject.addProperty(packageParameter.getName(), value);
            }
        }
        OfflineRnCFileSystemParameter fileSystemParameter = OfflineRnCFileSystemParameter.fromName(alias);
        if(fileSystemParameter != null){
        	jsonObject.addProperty(fileSystemParameter.getName(), value);
        }
        OfflineRnCRatingSystemParameter ratingSystemParameter = OfflineRnCRatingSystemParameter.fromName(alias);
        if(ratingSystemParameter != null){
        	jsonObject.addProperty(ratingSystemParameter.getName(), value);
        }
        return jsonObject;
    }

}
