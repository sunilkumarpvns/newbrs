package com.elitecore.corenetvertex.sm.gateway;

import com.elitecore.corenetvertex.util.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity(name="com.elitecore.corenetvertex.sm.gateway.VendorData")
@Table( name = "TBLM_VENDOR")
public class VendorData implements Serializable{
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String description;

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    public JsonObject toJson() {
        Gson gson = GsonFactory.defaultInstance();
        return gson.toJsonTree(this).getAsJsonObject();
    }

}
