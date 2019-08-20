package com.elitecore.corenetvertex.sm.location.area;

import com.google.gson.JsonObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * LAC Information
 * @author dhyani.raval
 */

@Entity(name = "com.elitecore.corenetvertex.sm.location.area.LacInformationData")
@Table(name = "TBLM_LAC_INFO")
public class LacInformationData implements Serializable {

    private String id;
    private Integer lac;
    private String ciList;
    private String racList;
    private String sacList;

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "LAC")
    public Integer getLac() {
        return lac;
    }

    public void setLac(Integer lac) {
        this.lac = lac;
    }

    @Column(name = "CI_LIST")
    public String getCiList() {
        return ciList;
    }

    public void setCiList(String ciList) {
        this.ciList = ciList;
    }

    @Column(name = "RAC_LIST")
    public String getRacList() {
        return racList;
    }

    public void setRacList(String racList) {
        this.racList = racList;
    }

    @Column(name = "SAC_LIST")
    public String getSacList() {
        return sacList;
    }

    public void setSacList(String sacList) {
        this.sacList = sacList;
    }

    public JsonObject toJson() {
        JsonObject jsonObject =  new JsonObject();
        jsonObject.addProperty("LAC",lac);
        jsonObject.addProperty("CIs",ciList);
        jsonObject.addProperty("SACs",sacList);
        jsonObject.addProperty("RACs",racList);
        return jsonObject;
    }
}
